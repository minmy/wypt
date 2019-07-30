package cn.sini.cgb.api.cgb.action.verification;

import cn.sini.cgb.admin.annex.entity.Annex;
import cn.sini.cgb.api.cgb.entity.group.*;
import cn.sini.cgb.api.cgb.entity.verification.Business;
import cn.sini.cgb.api.cgb.entity.verification.BusinessTerminal;
import cn.sini.cgb.api.cgb.entity.verification.VerificationLog;
import cn.sini.cgb.api.cgb.entity.verification.VerificationSheet;
import cn.sini.cgb.api.cgb.query.group.OrderGoodsQuery;
import cn.sini.cgb.api.cgb.query.group.OrderQuery;
import cn.sini.cgb.api.cgb.query.group.OrderStateQuery;
import cn.sini.cgb.api.cgb.query.group.WeChatUserQuery;
import cn.sini.cgb.api.cgb.query.verification.BusinessQuery;
import cn.sini.cgb.api.cgb.query.verification.BusinessTerminalQuery;
import cn.sini.cgb.api.cgb.query.verification.VerificationSheetQuery;
import cn.sini.cgb.common.exception.ParamException;
import cn.sini.cgb.common.exception.SystemException;
import cn.sini.cgb.common.http.HttpResponseWrapper;
import cn.sini.cgb.common.json.JsonUtils;
import cn.sini.cgb.common.json.JsonWrapper;
import cn.sini.cgb.common.util.DateTimeUtils;
import cn.sini.cgb.common.util.DesUtil;
import cn.sini.cgb.common.util.Environment;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ContainerNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.LockMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 我想拼团订单核销Action
 *
 * @author lijianxin
 */
@Controller
@RequestMapping("verification")
public class OrderVerificationAction {

    private static Logger logger = LoggerFactory.getLogger(OrderVerificationAction.class);

    /**
     * 返回状态及说明枚举类
     */
    private enum ResponseStatus {
        _0(0, "成功"),
        _20001(20001, "签名错误"),
        _20002(20002, "mchId参数不存在"),
        _20003(20003, "不存在此商户"),
        _20004(20004, "posId参数不存在"),
        _20005(20005, "对不起，此终端没有核销权限"),
        _20006(20006, "orderName参数不存在"),
        _20007(20007, "不存在此订单"),
        _20008(20008, "serialNumber参数不存在"),
        _20009(20009, "不存在此核销单"),
        _20010(20010, "核销失败"),
        _20011(20011, "未核销"),
        _20012(20012, "订单已核销"),
        _20013(20013, "核销流水号已被使用并已核销"),
        _20014(20014, "订单已完成"),//订单不在待收货状态
        _20015(20015, "核销流水号未核销"),
        _20016(20016, "核销单上的订单编号与参数的订单编号不匹配"),
        _20017(20017, "参数key为空"),
        _20018(20018, "参数serialNumber为空"),
        _20019(20019, "对不起，订单里不存在该商户的商品"),
        _20020(20020, "该订单非企业团"),
        _20021(20021, "对不起，还没到提货时间"),
        _20022(20022, "对不起，提货时间已结束"),
        _20023(20023, "该订单核销次数已到上限"),
        _20024(20024, "openId不存在"),
        _20025(20025, "微信用户不存在"),
        _20026(20026, "该订单没有自提的收货方式"),
        _20027(20027, "商品编号不存在"),
        _20028(20028, "商品编号错误"),
        _20029(20029, "商品编号存在空"),
        _20030(20030, "商品已核销完"),
        _20031(20031, "核销号存在空"),
        _20032(20032, "商品号数量不等于核销号数量"),
        _20033(20033, "对不起，团单已结束"),//团单状态不是进行中
        _20034(20034, "订单支付状态不是已支付"),
        _20035(20035, "参数key未按要求加密"),
        _20036(20036, "该商品不能核销"),
        //        _20014(20014, "冲正失败"),
        _20500(20500, "服务器内部错误");

        public Integer code;
        public String desc;

        ResponseStatus(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public Integer getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

    /**
     * 业务代码接口
     */
    public interface BusinessCode {

        /**
         * 执行方法，用不着的参数传null
         *
         * @param key              解析后的key，用于日志记录
         * @param mchId            商户编号
         * @param posId            pos机终端号
         * @param orderNumber      订单编号
         * @param orderGoodsNumber 商品编号
         * @param serialNumber     核销流水号
         */
        void execute(String key, String mchId, String posId, String orderNumber, String orderGoodsNumber, String serialNumber);
    }

    /**
     * 参数名称
     */
    private static final String KEY = "key";
    private static final String SIGNATURE = "signature";
    private static final String SIGN = "sign";

    /**
     * 签名所需要的key
     */
    private static final String MCHID = "mchId";
    private static final String POSID = "posId";
    private static final String ORDERNUMBER = "orderNumber";
    private static final String ORDERGOODSNUMBERS = "commodityNumber";
    private static final String MCHKEY = "mchKey";
    private static final String SERIALNUMBER = "serialNumber";

    /**
     * 参数标点符号
     */
    private static final String EQUAL = "=";
    private static final String AND = "&";
    private static final String SEPARATOR = "#@#";

    /**
     * 查询订单中需要核销的商品明细
     */
    @Transactional
    @RequestMapping(value = "/queryorder", method = RequestMethod.POST)
    public void queryorder(@RequestBody Map<String, Object> bodyData, HttpResponseWrapper response) {
        //无serialNumber参数，serialNumberMust=false
        executeBusinessCodeAfterVerify(bodyData, response, false, VerificationLog.ApiType.QUERYORDER, new BusinessCode() {
            @Override
            public void execute(String key, String mchId, String posId, String orderNumber, String orderGoodsNumber, String serialNumber) {
                //
                try {
                    Order order = new OrderQuery().orderNumber(orderNumber).readOnly().uniqueResult();
                    if (null == order) {
                        //订单编号错误
                        outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20007.getCode(), ResponseStatus._20007.getDesc(),
                                key, mchId, VerificationLog.ApiType.QUERYORDER);
                        return;
                    }
                    //商户
                    Business business = new BusinessQuery().mchId(mchId).readOnly().uniqueResult();
                    //当前核销人，只有小程序端有核销人
                    WeChatUser weChatUser = new WeChatUserQuery().openId(posId).readOnly().uniqueResult();
                    String verificationer = null == weChatUser ? null : weChatUser.getContacts();
                    if (StringUtils.isEmpty(verificationer)) {
                        verificationer = business.getName();
                    }
                    //在商店上已核销记录
                    List<VerificationSheet> verificationSheets = new VerificationSheetQuery().mchId(mchId).orderNumber(orderNumber)
                            .status(VerificationSheet.VerificationStatus.CONSUMED).orderBy("createTime", false).list();
                    //
                    boolean verificationOrderFinish = verificationOrderFinish(mchId, verificationSheets, order);
                    //返回
                    outputString(bodyData, response, verificationOrderFinish ? ResponseStatus._20012.getCode() : ResponseStatus._0.getCode(),
                            queryOrderPackData(order, verificationSheets, business, verificationOrderFinish, verificationer));
                    saveVerificationLog(key, mchId, VerificationLog.ApiType.QUERYORDER, ResponseStatus._0.getCode(), ResponseStatus._0.getDesc());
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20500.getCode(), ResponseStatus._20500.getDesc(),
                            key, mchId, VerificationLog.ApiType.WRITEOFF);
                }
            }
        });
    }

    /**
     * 核销订单
     */
    @Transactional
    @RequestMapping(value = "/writeoff", method = RequestMethod.POST)
    public void writeoff(@RequestBody Map<String, Object> bodyData, HttpResponseWrapper response) {
        //有serialNumber参数，serialNumberMust=true
        executeBusinessCodeAfterVerify(bodyData, response, true, VerificationLog.ApiType.WRITEOFF, new BusinessCode() {
            @Override
            public void execute(String key, String mchId, String posId, String orderNumber, String orderGoodsNumbers, String serialNumbers) {
                try {
                    //订单验证
                    Order order = new OrderQuery().orderNumber(orderNumber).lockMode(LockMode.PESSIMISTIC_WRITE).uniqueResult();
                    if (null == order) {
                        //订单编号错误
                        outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20007.getCode(), ResponseStatus._20007.getDesc(),
                                key, mchId, VerificationLog.ApiType.WRITEOFF);
                        return;
                    }
                    if (order.getPayState() != Order.PayState.YZF) {
                        //订单支付状态不是已支付
                        outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20034.getCode(), ResponseStatus._20034.getDesc(),
                                key, mchId, VerificationLog.ApiType.WRITEOFF);
                        return;
                    }
                    //检查自提时间
                    Date currentDate = new Date();
                    if (order.getGroupOrder().getSelfExtractingTime() == null
                            || currentDate.before(order.getGroupOrder().getSelfExtractingTime())) {
                        outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20021.getCode(), ResponseStatus._20021.getDesc(),
                                key, mchId, VerificationLog.ApiType.WRITEOFF);
                        return;
                    }
                    if (order.getGroupOrder().getSelfExtractingEndTime() == null
                            || currentDate.after(order.getGroupOrder().getSelfExtractingEndTime())) {
                        outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20022.getCode(), ResponseStatus._20022.getDesc(),
                                key, mchId, VerificationLog.ApiType.WRITEOFF);
                        return;
                    }
                    if (order.getGroupOrder().getGroupOrderState().getStates() != GroupOrderState.States.JXZ) {
                        //团单状态不是进行中
                        outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20033.getCode(), ResponseStatus._20033.getDesc(),
                                key, mchId, VerificationLog.ApiType.WRITEOFF);
                        return;
                    }
                    if (order.getOrderState().getOrderStates() != OrderState.OrderStates.DSH) {
                        //订单不在待收货状态
                        outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20014.getCode(), ResponseStatus._20014.getDesc(),
                                key, mchId, VerificationLog.ApiType.WRITEOFF);
                        return;
                    }
                    //查询本店本订单全部的已核销记录
                    List<VerificationSheet> orderVerificationSheets = new VerificationSheetQuery().orderNumber(orderNumber).mchId(mchId)
                            .status(VerificationSheet.VerificationStatus.CONSUMED).orderBy("createTime", false).list();
                    //本店商品订单
                    if (verificationOrderFinish(mchId, orderVerificationSheets, order)) {
                        //该订单核销次数已到上限
                        outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20023.getCode(), ResponseStatus._20023.getDesc(),
                                key, mchId, VerificationLog.ApiType.WRITEOFF);
                        return;
                    }
                    String[] orderGoodsNumberArray = orderGoodsNumbers.split(SEPARATOR);
                    String[] serialNumberArray = serialNumbers.split(SEPARATOR);
                    for (String orderGoodsNumber : orderGoodsNumberArray) {
                        //orderGoodsNumbers[]里不能有空
                        if (StringUtils.isEmpty(orderGoodsNumber)) {
                            outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20029.getCode(), ResponseStatus._20029.getDesc(),
                                    key, mchId, VerificationLog.ApiType.WRITEOFF);
                            return;
                        }
                        OrderGoods orderGoods = new OrderGoodsQuery().id(Long.parseLong(orderGoodsNumber)).readOnly().uniqueResult();
                        if (null == orderGoods) {
                            outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20028.getCode(), ResponseStatus._20028.getDesc(),
                                    key, mchId, VerificationLog.ApiType.WRITEOFF);
                            return;
                        }
                        //劵为特殊商品，里面包含多个商品，不能核销
                        if (orderGoods.getGroupCommodity().getVerify()) {
                            outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20036.getCode(), ResponseStatus._20036.getDesc(),
                                    key, mchId, VerificationLog.ApiType.WRITEOFF);
                        }
                        if (verificationOrderGoodFinish(orderVerificationSheets, orderGoods)) {
                            outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20030.getCode(), ResponseStatus._20030.getDesc(),
                                    key, mchId, VerificationLog.ApiType.WRITEOFF);
                            return;
                        }
                    }
                    for (String serialNumber : serialNumberArray) {
                        // serialNumber[]里不能有空
                        if (StringUtils.isEmpty(serialNumber)) {
                            outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20031.getCode(), ResponseStatus._20031.getDesc(),
                                    key, mchId, VerificationLog.ApiType.WRITEOFF);
                            return;
                        }
                    }
                    //orderGoodsNumbers.size = serialNumber.size
                    if (serialNumberArray.length != orderGoodsNumberArray.length) {
                        outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20032.getCode(), ResponseStatus._20032.getDesc(),
                                key, mchId, VerificationLog.ApiType.WRITEOFF);
                        return;
                    }
                    //-------------------验证完毕-------------------------
                    Business business = new BusinessQuery().mchId(mchId).readOnly().uniqueResult();
                    //核销人
                    WeChatUser weChatUser = new WeChatUserQuery().openId(posId).readOnly().uniqueResult();
                    String verificationer = null == weChatUser ? null : weChatUser.getContacts();
                    if (StringUtils.isEmpty(verificationer)) {
                        if (null != business) {
                            verificationer = business.getName();
                        }
                    }
                    for (int i = 0; i < orderGoodsNumberArray.length; i++) {
                        String orderGoodsNumber = orderGoodsNumberArray[i];
                        String serialNumber = serialNumberArray[i];
                        //用于记录日志
                        String oldOrderStatus = order.getOrderState().getOrderStates().toString();
                        //最后一次循环判断是否要改变订单状态
                        if (i == orderGoodsNumberArray.length - 1) {
                            //订单已核销总数
                            Long countVerification = new VerificationSheetQuery().orderNumber(orderNumber)
                                    .status(VerificationSheet.VerificationStatus.CONSUMED).count();
                            Long allCount = order.getOrderGoods().stream().filter(o -> !o.getGroupCommodity().getVerify()).collect(Collectors.summarizingInt(o ->
                                    o.getGroupCommodity().getWriteOffsNumber() == null ? 0 : o.getGroupCommodity().getWriteOffsNumber())).getSum();
                            // 当订单内所有商品都核销完之后才修改订单状态
                            if (allCount - countVerification <= orderGoodsNumberArray.length) {
                                OrderState orderState = new OrderStateQuery().orderStates(OrderState.OrderStates.YWC).uniqueResult();
                                order.setOrderState(orderState);
                                order.saveOrUpdate();
                            }
                        }
                        //通过核销流水号找核销单
                        VerificationSheet verificationSheet = new VerificationSheetQuery().verificationCode(serialNumber).lockMode(LockMode.PESSIMISTIC_WRITE).uniqueResult();
                        if (null != verificationSheet && verificationSheet.getStatus() == VerificationSheet.VerificationStatus.CONSUMED) {
                            //此核销流水号已被使用并核销成功
                            throw new ParamException(serialNumber + ResponseStatus._20013.getDesc());
                        }
                        //用于记录日志
                        String oldVerificationStatus = null == verificationSheet ? "null" : verificationSheet.getStatus().toString();
                        //核销
                        if (null == verificationSheet)
                            verificationSheet = new VerificationSheet();
                        verificationSheet.setMchId(mchId);
                        verificationSheet.setOrderNumber(orderNumber);
                        verificationSheet.setPosId(posId);
                        verificationSheet.setOpenId(order.getWeChatUser().getOpenId());
                        verificationSheet.setStatus(VerificationSheet.VerificationStatus.CONSUMED);
                        verificationSheet.setVerificationCode(serialNumber);
                        verificationSheet.setOrderGoodsNumber(orderGoodsNumber);
                        verificationSheet.setVerificationer(verificationer);
                        verificationSheet.saveOrUpdate();
                        //订单与核销单状态转变日志
                        String logMessage = "orderStatus:" + oldOrderStatus + "->" + order.getOrderState().getOrderStates()
                                + ",verificationStatus:" + oldVerificationStatus + "->" + verificationSheet.getStatus();
                        saveVerificationLog(key, mchId, VerificationLog.ApiType.WRITEOFF, ResponseStatus._0.getCode(), logMessage);
                    }
                    outputString(bodyData, response, ResponseStatus._0.getCode(), ResponseStatus._0.getDesc());
                } catch (ParamException e) {
                    logger.error(e.getMessage(), e);
                    outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20013.getCode(), e.getMessage(),
                            key, mchId, VerificationLog.ApiType.WRITEOFF);
                    //回滚
                    throw e;
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20500.getCode(), ResponseStatus._20500.getDesc(),
                            key, mchId, VerificationLog.ApiType.WRITEOFF);
                    //回滚
                    throw new SystemException(e.getMessage(), e);
                }
            }
        });
    }

    /**
     * 验证订单是否核销成功
     */
    @Transactional
    @RequestMapping(value = "/checkwriteoff", method = RequestMethod.POST)
    public void checkwriteoff(@RequestBody Map<String, Object> bodyData, HttpResponseWrapper response) {
        //有serialNumber参数，serialNumberMust=true
        executeBusinessCodeAfterVerify(bodyData, response, true, VerificationLog.ApiType.CHECKWRITEOFF,
                (String key, String mchId, String posId, String orderNumber, String orderGoodsNumber, String serialNumber) -> {
                    try {
                        VerificationSheet verificationSheet = new VerificationSheetQuery().verificationCode(serialNumber).readOnly().uniqueResult();
                        if (null == verificationSheet) {
                            //核销流水号错误
                            outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20009.getCode(), ResponseStatus._20009.getDesc(),
                                    key, mchId, VerificationLog.ApiType.CORRECT);
                            return;
                        }
                        if (!StringUtils.equals(verificationSheet.getOrderNumber(), orderNumber)) {
                            //核销单上的订单编号与参数的订单编号不匹配
                            outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20016.getCode(), ResponseStatus._20016.getDesc(),
                                    key, mchId, VerificationLog.ApiType.CORRECT);
                            return;
                        }
                        if (VerificationSheet.VerificationStatus.CONSUMED != verificationSheet.getStatus()) {
                            //未核销
                            outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20011.getCode(), ResponseStatus._20011.getDesc(),
                                    key, mchId, VerificationLog.ApiType.CHECKWRITEOFF);
                            return;
                        }
                        //核销成功
                        outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._0.getCode(), "已核销",
                                key, mchId, VerificationLog.ApiType.CHECKWRITEOFF);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20500.getCode(), ResponseStatus._20500.getDesc(),
                                key, mchId, VerificationLog.ApiType.WRITEOFF);
                    }
                });
    }

    /**
     * 订单冲正
     */
    @Transactional
    @RequestMapping(value = "/correct", method = RequestMethod.POST)
    public void correct(@RequestBody Map<String, Object> bodyData, HttpResponseWrapper response) {
        executeBusinessCodeAfterVerify(bodyData, response, true, VerificationLog.ApiType.CORRECT,
                (String key, String mchId, String posId, String orderNumber, String orderGoodsNumber, String serialNumber) -> {
                    try {
                        //通过核销流水号找核销单
                        VerificationSheet verificationSheet = new VerificationSheetQuery().verificationCode(serialNumber).lockMode(LockMode.PESSIMISTIC_WRITE).uniqueResult();
                        if (null == verificationSheet) {
                            //核销流水号错误
                            outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20009.getCode(), ResponseStatus._20009.getDesc(),
                                    key, mchId, VerificationLog.ApiType.CORRECT);
                            return;
                        }
                        if (VerificationSheet.VerificationStatus.UNCONSUMED == verificationSheet.getStatus()
                                || null == verificationSheet.getStatus()) {
                            //核销流水号未核销
                            outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20015.getCode(), ResponseStatus._20015.getDesc(),
                                    key, mchId, VerificationLog.ApiType.CORRECT);
                            return;
                        }
                        if (!StringUtils.equals(verificationSheet.getOrderNumber(), orderNumber)) {
                            //核销单上的订单编号与参数的订单编号不匹配
                            outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20016.getCode(), ResponseStatus._20016.getDesc(),
                                    key, mchId, VerificationLog.ApiType.CORRECT);
                            return;
                        }
                        Order order = new OrderQuery().orderNumber(orderNumber).lockMode(LockMode.PESSIMISTIC_WRITE).uniqueResult();
                        if (null == order) {
                            //订单编号错误
                            outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20007.getCode(), ResponseStatus._20007.getDesc(),
                                    key, mchId, VerificationLog.ApiType.WRITEOFF);
                            return;
                        }
                        //已过提货时间
                        if (order.getGroupOrder().getSelfExtractingEndTime() != null
                                && new Date().after(order.getGroupOrder().getSelfExtractingEndTime())) {
                            outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20022.getCode(), ResponseStatus._20022.getDesc() + "，不能再冲正",
                                    key, mchId, VerificationLog.ApiType.WRITEOFF);
                            return;
                        }
                        //团单已结束
                        if (order.getGroupOrder().getGroupOrderState().getStates() != GroupOrderState.States.JXZ) {
                            outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20033.getCode(), ResponseStatus._20033.getDesc(),
                                    key, mchId, VerificationLog.ApiType.WRITEOFF);
                            return;
                        }
                        //用于记录日志
                        String oldOrderStatus = order.getOrderState().getOrderStates().toString();
                        // 如果订单已完成则修改订单状态为待收货,如果过了提货时间不修改
                        if (OrderState.OrderStates.YWC == order.getOrderState().getOrderStates()
                                && order.getGroupOrder().getSelfExtractingEndTime() != null
                                && new Date().before(order.getGroupOrder().getSelfExtractingEndTime())) {
                            OrderState orderState = new OrderStateQuery().orderStates(OrderState.OrderStates.DSH).uniqueResult();
                            order.setOrderState(orderState);
                            order.saveOrUpdate();
                        }
                        //用于记录日志
                        String oldVerificationStatus = verificationSheet.getStatus().toString();
                        //冲正
                        verificationSheet.setStatus(VerificationSheet.VerificationStatus.UNCONSUMED);
                        verificationSheet.saveOrUpdate();
                        //订单与核销单状态转变日志
                        String logMessage = "orderStatus:" + oldOrderStatus + "->" + order.getOrderState().getOrderStates()
                                + ",verificationStatus:" + oldVerificationStatus + "->" + verificationSheet.getStatus();
                        outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._0.getCode(), ResponseStatus._0.getDesc(),
                                key, mchId, VerificationLog.ApiType.CORRECT, logMessage);
                    } catch (Exception e) {
                        //冲正失败
                        logger.error(e.getMessage(), e);
                        outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20500.getCode(), ResponseStatus._20500.getDesc(),
                                key, mchId, VerificationLog.ApiType.CORRECT);
                        //回滚
                        throw new SystemException(e.getMessage(), e);
                    }
                });
    }

    /**
     * 解析请求参数并验证，最后执行对应的业务代码
     *
     * @param bodyData         请求body
     * @param serialNumberMust serialNumber参数是否必须
     * @param apiType          api类型，用于保存日志
     * @param businessCode     业务代码
     */
    private void executeBusinessCodeAfterVerify(Map<String, Object> bodyData, HttpResponseWrapper response, Boolean serialNumberMust, VerificationLog.ApiType apiType, BusinessCode businessCode) {
        //key参数
        String key = null;
        //签名参数
        String signature = null;
        //核销流水号
        String mchId;
        String posId;
        String orderNumber;
        String orderGoodsNumber = null;
        String serialNumber = null;
        try {
            //标志，不为空时为小程序传的未加密参数
            String sign = null == bodyData.get(SIGN) ? null : String.valueOf(bodyData.get(SIGN));
            //解析大信参数或组装小程序参数，sign不为null时为小程序端的参数
            if (StringUtils.isEmpty(sign)) {
                //大信端的参数
                key = null == bodyData.get(KEY) ? null : String.valueOf(bodyData.get(KEY));
                if (StringUtils.isEmpty(key)) {
                    outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20017.getCode(), ResponseStatus._20017.getDesc(),
                            null, null, apiType);
                    return;
                }
                //解析key
                key = DesUtil.decryptDES(key, Environment.getProperty("encryptKey"));
                signature = null == bodyData.get(SIGNATURE) ? null : String.valueOf(bodyData.get(SIGNATURE));
                if (StringUtils.isEmpty(signature)) {
                    outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20018.getCode(), ResponseStatus._20018.getDesc(),
                            key, null, apiType);
                    return;
                }
            } else {
                //小程序端的参数
                key = packageKey(bodyData, response, apiType, serialNumberMust);

            }
            JsonWrapper jw = new JsonWrapper(JsonUtils.toObjectNode(key));
            mchId = getParamFromKey(bodyData, jw, key, MCHID, apiType, response);
            posId = getParamFromKey(bodyData, jw, key, POSID, apiType, response);
            orderNumber = getParamFromKey(bodyData, jw, key, ORDERNUMBER, apiType, response);
            //有serialNumber参数
            if (serialNumberMust) {
                serialNumber = getParamFromKey(bodyData, jw, key, SERIALNUMBER, apiType, response);
                orderGoodsNumber = getParamFromKey(bodyData, jw, key, ORDERGOODSNUMBERS, apiType, response);
            }

            //---------------参数解析完,开始验证------------
            //获取商户对应的mchkey
            Business business = new BusinessQuery().mchId(mchId).readOnly().uniqueResult();
            if (null == business) {
                outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20003.getCode(), ResponseStatus._20003.getDesc(),
                        key, mchId, apiType);
                return;
            }
            String mchKey = business.getMchKey();
            //验证签名
            StringBuilder signatureString = new StringBuilder();
            signatureString.append(MCHID).append(EQUAL).append(mchId).append(AND)
                    .append(POSID).append(EQUAL).append(posId).append(AND)
                    .append(ORDERNUMBER).append(EQUAL).append(orderNumber).append(AND);
            //有serialNumber参数
            if (StringUtils.isEmpty(sign) && serialNumberMust) {
                signatureString.append(ORDERGOODSNUMBERS).append(EQUAL).append(orderGoodsNumber).append(AND);
                signatureString.append(SERIALNUMBER).append(EQUAL).append(serialNumber).append(AND);
            }
            signatureString.append(MCHKEY).append(EQUAL).append(mchKey);
            //验证签名是否正确
            if (StringUtils.isEmpty(sign) && !StringUtils.equals(DigestUtils.md5Hex(signatureString.toString()), signature)) {
                outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20001.getCode(), ResponseStatus._20001.getDesc(),
                        key, mchId, apiType);
                return;
            }
            //验证posId
            BusinessTerminal businessTerminal = new BusinessTerminalQuery().posId(posId).readOnly().uniqueResult();
            if (null == businessTerminal || !StringUtils.equals(businessTerminal.getBusiness().getMchId(), mchId)) {
                outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20005.getCode(), ResponseStatus._20005.getDesc(),
                        key, mchId, apiType);
                return;
            }
            Order order = new OrderQuery().orderNumber(orderNumber).uniqueResult();
            if (null == order) {
                //订单编号错误
                outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20007.getCode(), ResponseStatus._20007.getDesc(),
                        key, mchId, apiType);
                return;
            }
            //
            if (order.getOrderGoods().stream().noneMatch(orderGood -> {
                Business b = orderGood.getGroupCommodity().getBusiness();
                return b != null && StringUtils.equals(b.getMchId(), mchId);
            })) {
                //订单中是否有该商户的商品
                outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20019.getCode(), ResponseStatus._20019.getDesc(),
                        key, mchId, apiType);
                return;
            }
            //检验orderGoodsNumber是否在order里
            if (serialNumberMust) {
                for (String s : orderGoodsNumber.split(SEPARATOR)) {
                    if (order.getOrderGoods().stream().noneMatch(orderGoods -> StringUtils.equals(s, String.valueOf(orderGoods.getId())))) {
                        outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20028.getCode(), ResponseStatus._20028.getDesc(),
                                key, mchId, apiType);
                        return;
                    }
                }
            }
            if (order.getGroupOrder().getGroupType() != GroupOrder.GroupType.QYT) {
                // 订单必须为企业团
                outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20020.getCode(), ResponseStatus._20020.getDesc(),
                        key, mchId, apiType);
                return;
            }
            if (order.getGroupOrder().getWayOfDeliverys().stream().noneMatch(wayOfDelivery -> wayOfDelivery.getDeliveryType() == WayOfDelivery.DeliveryType.HX)) {
                // 订单没有自提的收货方式
                outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20026.getCode(), ResponseStatus._20026.getDesc(),
                        key, mchId, apiType);
                return;
            }
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
            outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20035.getCode(), ResponseStatus._20035.getDesc(),
                    key, null, VerificationLog.ApiType.CORRECT);
            return;
        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
            outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20500.getCode(), e.getMessage(),
                    key, null, VerificationLog.ApiType.CORRECT);
            return;
        } catch (ParamException e) {
            //getParamFromKey()已做返回处理，抛异常主要为了跳出此方法体
            logger.error(e.getMessage(), e);
            return;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20500.getCode(), ResponseStatus._20500.getDesc(),
                    key, null, VerificationLog.ApiType.CORRECT);
            return;
        }
        //最后执行业务代码
        businessCode.execute(key, mchId, posId, orderNumber, orderGoodsNumber, serialNumber);
    }

    /**
     * 检查请求参数key里参数，不存在抛出异常并保存日志
     *
     * @param bodyData  用于获取标志sign，判断最后输出是否加密
     * @param jw        已放入key的Json包装类
     * @param key       请求参数key
     * @param paramName key里的参数名称
     * @param apiType   api类型，保存日志用
     */
    private String getParamFromKey(Map<String, Object> bodyData, JsonWrapper jw, String key, String paramName, VerificationLog.ApiType apiType, HttpResponseWrapper response) {
        String param = jw.getString(paramName);
        if (StringUtils.isEmpty(param)) {
            switch (paramName) {
                case MCHID:
                    outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20002.getCode(), ResponseStatus._20002.getDesc(),
                            key, null, apiType);
                    break;
                case POSID:
                    outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20004.getCode(), ResponseStatus._20004.getDesc(),
                            key, jw.getString(MCHID), apiType);
                    break;
                case ORDERNUMBER:
                    outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20006.getCode(), ResponseStatus._20006.getDesc(),
                            key, jw.getString(MCHID), apiType);
                    break;
                case SERIALNUMBER:
                    outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20008.getCode(), ResponseStatus._20008.getDesc(),
                            key, jw.getString(MCHID), apiType);
                    break;
                case ORDERGOODSNUMBERS:
                    outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20027.getCode(), ResponseStatus._20027.getDesc(),
                            key, jw.getString(MCHID), apiType);
                    break;
            }
            throw new ParamException("请求参数key里属性" + paramName + "不存在");
        }
        return param;
    }

    /**
     * 小程序包装key，为了公用一套代码
     *
     * @param bodyData         请求参数
     * @param response         响应
     * @param apiType          用于保存日志
     * @param serialNumberMust 是否需要核销流水号
     */
    private String packageKey(Map<String, Object> bodyData, HttpResponseWrapper response, VerificationLog.ApiType apiType, Boolean serialNumberMust) throws UnsupportedEncodingException {
        String openId = null == bodyData.get("openId") ? null : String.valueOf(bodyData.get("openId"));
        if (StringUtils.isEmpty(openId)) {
            outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20024.getCode(), ResponseStatus._20024.getDesc(),
                    null, openId, apiType);
            throw new ParamException(ResponseStatus._20024.getDesc());
        }
        //验证商家
        BusinessTerminal businessTerminal = new BusinessTerminalQuery().posId(openId).readOnly().uniqueResult();
        if (null == businessTerminal) {
            outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20005.getCode(), ResponseStatus._20005.getDesc(),
                    null, openId, apiType);
            throw new ParamException(ResponseStatus._20005.getDesc());
        }
        String orderNumber = null == bodyData.get(ORDERNUMBER) ? null : String.valueOf(bodyData.get(ORDERNUMBER));
        if (StringUtils.isEmpty(orderNumber)) {
            outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20006.getCode(), ResponseStatus._20006.getDesc(),
                    null, openId, apiType);
            throw new ParamException(ResponseStatus._20006.getDesc());
        }
        //组合key，使小程序和大信可以公用一套代码
        String key;
        if (!serialNumberMust) {
            key = "{\"" + MCHID + "\":\"" + businessTerminal.getBusiness().getMchId() + "\",\"" + POSID + "\": \"" + openId + "\",\""
                    + ORDERNUMBER + "\": \"" + DesUtil.decryptDES(orderNumber, Environment.getProperty("encryptKey")) + "\"}";
        } else {
            String serialNumber = null == bodyData.get("serialNumber") ? null : String.valueOf(bodyData.get("serialNumber"));
            if (StringUtils.isEmpty(serialNumber)) {
                outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20008.getCode(), ResponseStatus._20008.getDesc(),
                        null, openId, apiType);
                throw new ParamException(ResponseStatus._20008.getDesc());
            }
            String orderGoodsNumber = null == bodyData.get(ORDERGOODSNUMBERS) ? null : String.valueOf(bodyData.get(ORDERGOODSNUMBERS));
            if (StringUtils.isEmpty(orderGoodsNumber)) {
                outputStringAndSaveVerificationLog(bodyData, response, ResponseStatus._20027.getCode(), ResponseStatus._20027.getDesc(),
                        null, openId, apiType);
                throw new ParamException(ResponseStatus._20027.getDesc());
            }
            key = "{\"" + MCHID + "\": \"" + businessTerminal.getBusiness().getMchId() + "\",\"" + POSID + "\": \"" + openId + "\",\""
                    + ORDERNUMBER + "\": \"" + DesUtil.decryptDES(orderNumber, Environment.getProperty("encryptKey")) + "\",\"" + ORDERGOODSNUMBERS + "\":\"" + orderGoodsNumber + "\",\"" + SERIALNUMBER + "\":\"" + serialNumber + "\"}";
        }
        return key;
    }

    /**
     * 订单在本商店是否已核销完
     *
     * @param mchId              商店mchId
     * @param verificationSheets 在mchId商店上已核销记录
     * @param order              订单
     */
    private Boolean verificationOrderFinish(String mchId, List<VerificationSheet> verificationSheets, Order order) {
        //是否核销完
        boolean verificationFinish = false;
        long writeOffsNumber = order.getOrderGoods().stream().filter(v -> StringUtils.equals(v.getGroupCommodity().getBusiness().getMchId(), mchId) && !v.getGroupCommodity().getVerify())
                .collect(Collectors.summarizingInt(v -> null == v.getGroupCommodity().getWriteOffsNumber() ? 0 : v.getGroupCommodity().getWriteOffsNumber())).getSum();
        if (verificationSheets.size() == writeOffsNumber && writeOffsNumber != 0) {
            verificationFinish = true;
        }
        return verificationFinish;
    }

    /**
     * 某商品是否核销完成
     *
     * @param verificationSheets 本商店上已核销记录
     * @param orderGoods         本商店订单订单
     */
    private Boolean verificationOrderGoodFinish(List<VerificationSheet> verificationSheets, OrderGoods orderGoods) {
        //是否核销完
        boolean verificationFinish = false;
        if (!CollectionUtils.isEmpty(verificationSheets)) {
            //核销次数
            long verificationCount = verificationSheets.stream().filter(v -> StringUtils.equals(v.getOrderGoodsNumber(), String.valueOf(orderGoods.getId()))).count();
            //最多核销次数
            long writeOffsNumber = orderGoods.getGroupCommodity().getWriteOffsNumber();
            if (verificationCount == writeOffsNumber && writeOffsNumber != 0) {
                verificationFinish = true;
            }
        }
        return verificationFinish;
    }

    /**
     * 返回结果加密并保存日志
     *
     * @param bodyData 用于获取标志sign，判断最后输出是否加密
     * @param status   请求参数
     * @param message  返回信息
     * @param key      请求参数（用于保存日志）
     * @param mchId    商户mchId（用于保存日志）
     * @param apiType  api类型（用于保存日志）
     */
    private void outputStringAndSaveVerificationLog(Map<String, Object> bodyData, HttpResponseWrapper response, int status, String message, String key, String mchId, VerificationLog.ApiType apiType) {
        outputStringAndSaveVerificationLog(bodyData, response, status, message, key, mchId, apiType, null);
    }

    /**
     * 返回结果加密并保存日志
     *
     * @param bodyData   用于获取标志sign，判断最后输出是否加密
     * @param status     请求参数
     * @param message    返回信息
     * @param key        请求参数（用于保存日志）
     * @param mchId      商户mchId（用于保存日志）
     * @param apiType    api类型（用于保存日志）
     * @param logMessage 日志信息（用于保存日志）
     */
    private void outputStringAndSaveVerificationLog(Map<String, Object> bodyData, HttpResponseWrapper response, int status, String message, String key, String mchId, VerificationLog.ApiType apiType, String logMessage) {
        outputString(bodyData, response, status, message);
        if (StringUtils.isEmpty(logMessage)) {
            saveVerificationLog(key, mchId, apiType, status, message);
        } else {
            saveVerificationLog(key, mchId, apiType, status, logMessage);
        }
    }

    /**
     * 返回结果加密
     *
     * @param bodyData 用于获取标志sign，判断最后输出是否加密
     * @param status   请求参数
     * @param message  返回信息
     */
    private void outputString(Map<String, Object> bodyData, HttpResponseWrapper response, int status, String message) {
        outputString(bodyData, response, status, message, null);

    }

    /**
     * 返回结果加密
     *
     * @param bodyData 用于获取标志sign，判断最后输出是否加密
     * @param status   请求参数
     * @param data     返回数据
     */
    private void outputString(Map<String, Object> bodyData, HttpResponseWrapper response, int status, ContainerNode<?> data) {
        outputString(bodyData, response, status, null, data);
    }

    /**
     * 返回结果加密
     *
     * @param bodyData 用于获取标志sign，判断最后输出是否加密
     * @param status   请求参数
     * @param message  返回信息
     * @param data     返回数据
     */
    private void outputString(Map<String, Object> bodyData, HttpResponseWrapper response, int status, String message, ContainerNode<?> data) {
        ObjectNode objectNode = JsonUtils.createObjectNode();
        objectNode.put("status", status);
        if (StringUtils.isNotEmpty(message)) {
            objectNode.put("message", message);
        }
        if (data != null) {
            objectNode.set("data", data);
        }
        //从小程序和后台发来的请求也不加密
        if (null == bodyData.get(SIGN) && null == bodyData.get("background")) {
            response.outputString(DesUtil.encryptDES(objectNode.toString(), Environment.getProperty("encryptKey")));
            return;
        }
        response.outputString(objectNode.toString());

    }

    /**
     * 记录日志
     *
     * @param key     请求参数
     * @param mchId   商户id
     * @param apiType api类型
     * @param status  响应状态码
     * @param message 响应信息
     */
    private void saveVerificationLog(String key, String mchId, VerificationLog.ApiType apiType, int status, String message) {
        VerificationLog log = new VerificationLog();
        log.setKey(key);
        log.setMchId(mchId);
        log.setApiType(apiType);
        log.setStatus(status);
        log.setMessage(message);
        log.saveOrUpdate();
    }

    /**
     * 包装queryorder接口返回的data，订单已核销时加上核销流水号
     *
     * @param order              订单
     * @param verificationSheets 核销记录
     * @param business           商户
     * @param verificationFinish 该订单是否核销完
     * @param verificationer     当前核销者
     */
    private ObjectNode queryOrderPackData(Order order, List<VerificationSheet> verificationSheets, Business business, Boolean verificationFinish, String verificationer) {
        //data
        ObjectNode orderNode = JsonUtils.createObjectNode();
        ArrayNode orderGoodsNodes = JsonUtils.createArrayNode();
        order.getOrderGoods().stream().filter(orderGoods -> !orderGoods.getGroupCommodity().getVerify()).forEach(orderGoods -> {
            if (null != orderGoods) {
                Business b = orderGoods.getGroupCommodity().getBusiness();
                //只显示商家自己的商品
                if (null != b && StringUtils.equals(b.getMchId(), business.getMchId())) {
                    ObjectNode orderGoodsNode = JsonUtils.createObjectNode();
                    orderGoodsNode.put("commodityNumber", orderGoods.getId());
                    orderGoodsNode.put("name", orderGoods.getGroupCommodity().getName());
                    orderGoodsNode.put("amount", orderGoods.getAmount());
                    String path = Environment.getProperty("host") + Environment.getProperty("skin");
                    orderGoodsNode.put("pic", CollectionUtils.isEmpty(orderGoods.getGroupCommodity().getAnnexs()) ?
                            path + "/images/group_order_pic.png" : path + ((Annex) orderGoods.getGroupCommodity().getAnnexs().toArray()[0]).getFilePath());
                    //已核销时显示全部
                    if (verificationFinish) {
                        //该商品的全部核销记录
                        List<VerificationSheet> verificationOrderGoodSheets = verificationSheets.stream()
                                .filter(v -> StringUtils.equals(v.getOrderGoodsNumber(), String.valueOf(orderGoods.getId()))).collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(verificationOrderGoodSheets)) {
                            //该商品最后一次核销记录
                            VerificationSheet verificationSheet = verificationOrderGoodSheets.get(0);
                            orderGoodsNode.put("serialNumber", verificationSheet.getVerificationCode());
                            orderGoodsNode.put("verificationer", verificationSheet.getVerificationer());
                            orderGoodsNode.put("verificationDate",
                                    DateTimeUtils.format(verificationSheet.getUpdateTime() == null
                                            ? verificationSheet.getCreateTime() : verificationSheet.getUpdateTime(), "yyyy-MM-dd HH:mm"));
                        }
                        orderGoodsNodes.add(orderGoodsNode);
                    } else {
                        //未核销时显示未核销完的
                        if (!verificationOrderGoodFinish(verificationSheets, orderGoods)) {
                            orderGoodsNodes.add(orderGoodsNode);
                        }
                    }

                }
            }
        });
        orderNode.put("orderNumber", order.getOrderNumber());
        orderNode.set("orderList", orderGoodsNodes);
        //以下是小程序端的额外添加的
        orderNode.put("groupOrderName", order.getGroupOrder().getTheme());
        orderNode.put("consumer", order.getWeChatUser().getContacts());
        orderNode.put("joinDate", DateTimeUtils.format(order.getCreateTime(), "yyyy-MM-dd HH:mm"));
        orderNode.put("businessName", business.getName());
        orderNode.put("currentVerificationer", verificationer);
        orderNode.put("currentDate", DateTimeUtils.format(new Date(), "yyyy-MM-dd HH:mm"));
        return orderNode;
    }
}