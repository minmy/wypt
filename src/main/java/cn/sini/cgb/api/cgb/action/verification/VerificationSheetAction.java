package cn.sini.cgb.api.cgb.action.verification;

import cn.sini.cgb.admin.annex.entity.Annex;
import cn.sini.cgb.admin.annex.util.AnnexUtils;
import cn.sini.cgb.api.cgb.entity.group.*;
import cn.sini.cgb.api.cgb.entity.verification.BusinessTerminal;
import cn.sini.cgb.api.cgb.entity.verification.VerificationSheet;
import cn.sini.cgb.api.cgb.query.group.OrderGoodsQuery;
import cn.sini.cgb.api.cgb.query.group.OrderQuery;
import cn.sini.cgb.api.cgb.query.group.WeChatUserQuery;
import cn.sini.cgb.api.cgb.query.verification.BusinessTerminalQuery;
import cn.sini.cgb.api.cgb.query.verification.VerificationSheetQuery;
import cn.sini.cgb.common.exception.SystemException;
import cn.sini.cgb.common.http.HttpRequestWrapper;
import cn.sini.cgb.common.http.HttpResponseWrapper;
import cn.sini.cgb.common.json.JsonUtils;
import cn.sini.cgb.common.query.Page;
import cn.sini.cgb.common.util.DateTimeUtils;
import cn.sini.cgb.common.util.DesUtil;
import cn.sini.cgb.common.util.Environment;
import cn.sini.cgb.common.util.QRCodeUtil;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 核销单action
 *
 * @author lijianxin
 */
@Controller
@RequestMapping("/api/verification")
public class VerificationSheetAction {
    private static Logger logger = LoggerFactory.getLogger(VerificationSheetAction.class);

    /**
     * 查看提货二维码
     */
    @Transactional
    @RequestMapping(value = "/qrcode", method = RequestMethod.POST)
    public void list(HttpRequestWrapper request, HttpResponseWrapper response) {
        String openId = request.getString("openId");
        if (StringUtils.isEmpty(openId)) {
            response.outputJson(-1, "缺少openId参数");
            return;
        }
        WeChatUser weChatUser = new WeChatUserQuery().openId(openId).readOnly().uniqueResult();
        if (weChatUser == null) {
            response.outputJson(-1, "查询失败，未找到该用户");
            return;
        }
        String orderNumber = request.getString("orderNumber");
        if (StringUtils.isEmpty(orderNumber)) {
            response.outputJson(-1, "缺少orderNumber参数");
            return;
        }
        Order order = new OrderQuery().orderNumber(orderNumber).readOnly().uniqueResult();
        if (null == order) {
            response.outputJson(-1, "查询失败，未找到该订单");
            return;
        }
        //判断是否为企业团
        if (order.getGroupOrder().getGroupType() != GroupOrder.GroupType.QYT) {
            // 订单必须为企业团
            response.outputJson(-1, "该订单非企业团");
            return;
        }
        if (order.getGroupOrder().getWayOfDeliverys().stream().noneMatch(wayOfDelivery -> wayOfDelivery.getDeliveryType() == WayOfDelivery.DeliveryType.HX)) {
            // 订单没有自提的收货方式
            response.outputJson(-1, "该订单没有自提的收货方式");
            return;
        }
        //标识，区分大信和普通商家
        Integer sign = request.getInteger("sign");
        //提货二维码
        List<Annex> annexs = order.getAnnexs().stream().filter(a -> a.getAnnexType() == Annex.AnnexType.VERIFICATION_QR_CODE).collect(Collectors.toList());
        Annex annex = CollectionUtils.isEmpty(annexs) ? saveVerificationQrcode(order, sign) : annexs.get(0);
        //订单核销记录
        ArrayNode commoditys = JsonUtils.createArrayNode();
        //排序和显示能核销的
        order.getOrderGoods().stream().sorted(Comparator.comparing(orderGood -> orderGood.getGroupCommodity().getBusiness().getId()))
                .filter(orderGood -> !orderGood.getGroupCommodity().getVerify())
                .forEach(orderGood -> {
                    ObjectNode commodity = JsonUtils.createObjectNode();
                    List<VerificationSheet> verificationSheet = new VerificationSheetQuery().orderGoodsNumber(String.valueOf(orderGood.getId()))
                            .status(VerificationSheet.VerificationStatus.CONSUMED).orderBy("createTime", false).list();
                    commodity.put("businessName", orderGood.getGroupCommodity().getBusiness() == null ? "" : orderGood.getGroupCommodity().getBusiness().getName());
                    commodity.put("commodityName", orderGood.getGroupCommodity().getName());
                    commodity.put("verificationCount", verificationSheet == null ? 0 : verificationSheet.size());
                    commodity.put("writeOffsNumber", orderGood.getGroupCommodity().getWriteOffsNumber());
                    commodity.put("verificationDate",
                            verificationSheet == null || verificationSheet.size() == 0 ? "" : DateTimeUtils.format(verificationSheet.get(0).getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
                    ArrayNode verificationDates = JsonUtils.createArrayNode();
                    if (null != verificationSheet) {
                        verificationSheet.forEach(v ->
                                verificationDates.add(DateTimeUtils.format(v.getCreateTime(), "yyyy-MM-dd HH:mm:ss")));
                    }
                    commodity.set("verificationDates", verificationDates);
                    commoditys.add(commodity);
                });
        ObjectNode data = JsonUtils.createObjectNode();
        // 文件url地址
        String host = Environment.getProperty("host");
        String path = host + Environment.getProperty("skin");
        data.put("orderNumber", orderNumber);
        data.put("groupOrderName", order.getGroupOrder().getTheme());
        data.put("qrcode", path + annex.getFilePath());
        data.put("randomNumber", order.getRandomNumber());
        data.put("selfExtractingTime", DateTimeUtils.format(order.getGroupOrder().getSelfExtractingTime(), "yyyy-MM-dd HH:mm:ss"));
        data.put("selfExtractingEndTime", DateTimeUtils.format(order.getGroupOrder().getSelfExtractingEndTime(), "yyyy-MM-dd HH:mm:ss"));
        data.set("commoditys", commoditys);
        response.outputJson(0, data);
    }

    /**
     * 查看我的核销记录(商家的所有核销记录)
     */
    @Transactional
    @RequestMapping(value = "/mine", method = RequestMethod.POST)
    public void mine(HttpRequestWrapper request, HttpResponseWrapper response) {
        String openId = request.getString("openId");
        if (StringUtils.isEmpty(openId)) {
            response.outputJson(-1, "缺少openId参数");
            return;
        }
        BusinessTerminal businessTerminal = new BusinessTerminalQuery().posId(openId).readOnly().uniqueResult();
        if (null == businessTerminal) {
            response.outputJson(-1, "没有核销权限，无法查看");
            return;
        }
        Page<VerificationSheet> page = new VerificationSheetQuery().mchId(businessTerminal.getBusiness().getMchId()).status(VerificationSheet.VerificationStatus.CONSUMED)
                .readOnly().orderBy("createTime", false).pageHasCount(request.getPageNum(), request.getPageSize(10));
        ArrayNode verificationSheets = JsonUtils.createArrayNode();
        page.getRecordList().forEach(v -> {
            if (StringUtils.isNotEmpty(v.getOrderGoodsNumber())) {
                OrderGoods orderGoods = new OrderGoodsQuery().id(Long.valueOf(v.getOrderGoodsNumber())).readOnly().uniqueResult();
                WeChatUser consumer = new WeChatUserQuery().openId(v.getOpenId()).readOnly().uniqueResult();
                if (null != orderGoods && null != consumer) {
                    ObjectNode objectNode = JsonUtils.createObjectNode();
                    objectNode.put("contacts", consumer.getContacts());
                    objectNode.put("headImgUrl", consumer.getHeadImgUrl());
                    objectNode.put("orderNumber", orderGoods.getOrder().getOrderNumber());
                    objectNode.put("commodityName", orderGoods.getGroupCommodity().getName());
                    objectNode.put("verificationDate", DateTimeUtils.format(orderGoods.getCreateTime(), "yyyy-MM-dd HH:mm"));
                    objectNode.put("verificationer", v.getVerificationer());
                    verificationSheets.add(objectNode);
                }
            }
        });
        ObjectNode data = JsonUtils.createObjectNode();
        data.put("pageNum", page.getPageNum());
        data.put("pageSize", page.getPageSize());
        data.put("totalPage", page.getTotalPage());
        data.put("totalRecord", page.getTotalRecord());
        data.set("verificationSheets", verificationSheets);
        response.outputJson(0, data);
    }

    @RequestMapping(value = "/qrcode", method = RequestMethod.GET)
    public void qrcode(HttpRequestWrapper request, HttpResponseWrapper response) {
        StringBuilder code = new StringBuilder();
        code.append("SiniCgb_");
        String orderNumber = request.getStringMust("orderNumber");
        Order order = new OrderQuery().orderNumber(orderNumber).readOnly().uniqueResult();
        if (null == order) {
            response.outputJson(-1, "订单不存在");
            return;
        }
        code.append(DesUtil.encryptDES(orderNumber, Environment.getProperty("encryptKey")));
        try {
            BitMatrix matrix = new MultiFormatWriter().encode(code.toString(), BarcodeFormat.QR_CODE, 200, 200);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            QRCodeUtil.writeToStream(matrix, "jpg", os);
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(os.toByteArray()));
            ImageIO.write(bufferedImage, "JPEG", response.getOutputStream());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new SystemException(e.getMessage());

        }
    }


    /**
     * 保存提货二维码
     *
     * @param order 订单
     * @param sign  是否加密
     */
    private Annex saveVerificationQrcode(Order order, Integer sign) {
        StringBuilder code = new StringBuilder();
        code.append("SiniCgb_");
        //区分大信和其他商家
        if (null != sign && sign == 1) {
//            code.append(order.getOrderNumber());
            //需求改了，都加密
            code.append(DesUtil.encryptDES(order.getOrderNumber(), Environment.getProperty("encryptKey")));
        } else {
            //加密
            code.append(DesUtil.encryptDES(order.getOrderNumber(), Environment.getProperty("encryptKey")));
        }
        // 生成二维码
        BitMatrix matrix;
        Annex annex = null;
        try {
            matrix = new MultiFormatWriter().encode(code.toString(), BarcodeFormat.QR_CODE, 200, 200);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            QRCodeUtil.writeToStream(matrix, "jpg", os);
            String fileName = order.getOrderNumber() + "_verificationQrCode.jpg";
            annex = AnnexUtils.saveAnnex(Annex.AnnexType.VERIFICATION_QR_CODE, fileName, null, os.toByteArray());
            annex.setOrder(order);
            annex.saveOrUpdate();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return annex;
    }

}
