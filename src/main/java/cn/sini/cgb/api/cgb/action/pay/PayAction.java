package cn.sini.cgb.api.cgb.action.pay;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.LockMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.sini.cgb.api.cgb.entity.group.GroupCommodity;
import cn.sini.cgb.api.cgb.entity.group.GroupOrder;
import cn.sini.cgb.api.cgb.entity.group.GroupOrderState.States;
import cn.sini.cgb.api.cgb.entity.group.Order;
import cn.sini.cgb.api.cgb.entity.group.Order.PayState;
import cn.sini.cgb.api.cgb.entity.group.OrderGoods;
import cn.sini.cgb.api.cgb.entity.group.OrderState;
import cn.sini.cgb.api.cgb.entity.group.OrderState.OrderStates;
import cn.sini.cgb.api.cgb.entity.group.WeChatUser;
import cn.sini.cgb.api.cgb.entity.group.WechatNotice.NoticeType;
import cn.sini.cgb.api.cgb.entity.pay.PayBill;
import cn.sini.cgb.api.cgb.entity.pay.PayBill.PayStatusEnum;
import cn.sini.cgb.api.cgb.entity.pay.RefundBill;
import cn.sini.cgb.api.cgb.entity.pay.RefundBill.RefundStatusEnum;
import cn.sini.cgb.api.cgb.entity.verification.VerificationSheet;
import cn.sini.cgb.api.cgb.query.group.GroupCommodityQuery;
import cn.sini.cgb.api.cgb.query.group.GroupOrderQuery;
import cn.sini.cgb.api.cgb.query.group.OrderQuery;
import cn.sini.cgb.api.cgb.query.group.OrderStateQuery;
import cn.sini.cgb.api.cgb.query.group.ShareQuery;
import cn.sini.cgb.api.cgb.query.group.WeChatUserQuery;
import cn.sini.cgb.api.cgb.query.pay.PayBillQuery;
import cn.sini.cgb.api.cgb.query.pay.RefundBillQuery;
import cn.sini.cgb.api.cgb.query.verification.VerificationSheetQuery;
import cn.sini.cgb.common.http.HttpRequestWrapper;
import cn.sini.cgb.common.http.HttpResponseWrapper;
import cn.sini.cgb.common.json.JsonUtils;
import cn.sini.cgb.common.util.DateTimeUtils;
import cn.sini.cgb.common.util.Environment;
import cn.sini.cgb.common.util.PayUtil;
import cn.sini.cgb.common.util.WechatUtils;
import cn.sini.cgb.common.wxpay.Notify;
import cn.sini.cgb.common.wxpay.PayConfig;
import cn.sini.cgb.common.wxpay.WXPay;
import cn.sini.cgb.common.wxpay.WXPayUtil;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 订单支付、退款action 当你看到这里的注释，那么首先恭喜你入坑了，不要吐槽这些代码，我也知道很烂，赶出来的，先上
 * 
 * @author 黎嘉权
 */
@Controller
@RequestMapping("/api/payaction")
public class PayAction {
	private static final Logger LOGGER = LoggerFactory.getLogger(Notify.class);

	/**
	 * 发起统一支付
	 */
	@Transactional
	@RequestMapping(value = "/pay", method = RequestMethod.POST)
	public void Pay(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
		// 支付金额，需要转成字符串类型，否则后面的签名会失败
		BigDecimal total_fee = new BigDecimal(1);
		String openId = request.getString("openId");
		String out_trade_no = request.getString("outTradeNo");
		PayUtil payUtil = new PayUtil();
		long groupId = 0L;
		if (openId == null || out_trade_no == null) {
			response.outputJson(80003, "缺少订单或用户");
			LOGGER.info("【統一支付】订单号：空 ，缺少订单或用户");
			return;
		}
		LOGGER.info("【統一支付】订单号：" + out_trade_no + "，用户发起支付：" + openId);
		// 设置X锁，不允许发货和取消操作
		Order order = payUtil.getOrder(openId, out_trade_no, true);
		if (order == null) {
			response.outputJson(80002, "订单号：" + out_trade_no + "，不存在");
			LOGGER.info("【統一支付】订单号：" + out_trade_no + "，不存在");
			return;
		}
		groupId = order.getGroupOrder().getId();
		// 设置X锁，不允许发货和取消操作
		GroupOrder groupOrder = payUtil.getGroup(groupId, true);
		long currentTime = System.currentTimeMillis() + 5 * 60 * 1000;
		Date date = new Date(currentTime);
		// 判断团单状态是否为：进行中，且结束时间5分钟前不能提单
		if (groupOrder.getGroupOrderState().getStates() != States.JXZ && date.getTime() > groupOrder.getEndTime().getTime()) {
			response.outputJson(80005, "拼团状态不正确或即将结束，请刷新再试");
			LOGGER.info("【統一支付】订单号：" + out_trade_no + "，拼团状态不正确或即将结束，请刷新再试");
			return;
		}
		// 判断订单支付状态,可能出现用户一直放在订单的待付款页面
		OrderState orderState = new OrderStateQuery().orderStates(OrderStates.DFK).uniqueResult();
		if (order.getOrderState() != orderState) {
			response.outputJson(80004, "订单号" + out_trade_no + "，状态不正确，当前为：" + order.getOrderState().getOrderStates().getDesc());
			LOGGER.info("【統一支付】订单号" + out_trade_no + "，状态不正确，当前为：" + order.getOrderState().getDesc());
			return;
		}
		// 判断金额
		if (order.getTotal() == null) {
			response.outputJson(80001, "订单金额为空");
			LOGGER.info("【統一支付】订单号：" + out_trade_no + "，订单金额为空");
			return;
		}
		// 微信需要单位为：分
		total_fee = order.getTotal().multiply(new BigDecimal("100")).setScale(0, BigDecimal.ROUND_DOWN);
		if (total_fee.toString().equals("0")) {
			response.outputJson(80001, "订单金额为0");
			LOGGER.info("【統一支付】订单号：" + out_trade_no + "，订单金额为0");
			return;
		}
		Map resultMap = new HashMap();
		PayConfig config = null;
		WXPay wxpay = null;
		try {
			config = new PayConfig();
			wxpay = new WXPay(config);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 生成的随机字符串
		String nonce_str = WXPayUtil.generateNonceStr();
		// 获取客户端的ip地址
		// 获取本机的ip地址
		InetAddress addr = null;
		try {
			addr = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			throw e;
		}
		String spbill_create_ip = addr.getHostAddress();
		// 商品描述
		String body = "我想拼团支付";
		// 统一下单接口参数
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("appid", config.getAppID());
		data.put("mch_id", config.getMchID());
		data.put("nonce_str", nonce_str);
		data.put("body", body);
		data.put("out_trade_no", out_trade_no);
		data.put("total_fee", String.valueOf(total_fee));
		data.put("spbill_create_ip", spbill_create_ip);
		data.put("notify_url", Environment.getProperty("host") + Environment.getProperty("path") + "/payaction/notify.action");
		data.put("trade_type", "JSAPI");
		data.put("openid", openId);
		try {
			LOGGER.info("【統一支付】订单号：" + out_trade_no + "，发起统一支付");
			Map<String, String> rMap = wxpay.unifiedOrder(data);
			String return_code = (String) rMap.get("return_code");
			String result_code = (String) rMap.get("result_code");
			String nonceStr = WXPayUtil.generateNonceStr();
			resultMap.put("nonceStr", nonceStr);

			Long timeStamp = System.currentTimeMillis() / 1000;
			if ("SUCCESS".equals(return_code) && return_code.equals(result_code)) {
				// 写入支付流水表
				PayBill payBill = new PayBillQuery().orderNumber(out_trade_no).openId(openId).payStatus(PayStatusEnum.DZF).lockMode(LockMode.UPGRADE).uniqueResult();
				if (payBill == null) {
					payBill = new PayBill();
				}
				payBill.setOpenId(openId);
				payBill.setOrderNumber(out_trade_no);
				payBill.setPayStatus(PayStatusEnum.DZF);
				payBill.setTotal_fee(order.getTotal());
				payBill.setPayXml(rMap.toString());
				payBill.saveOrUpdate();

				LOGGER.info("【統一支付】订单号：" + out_trade_no + "，统一支付成功，");
				String prepayid = rMap.get("prepay_id");
				resultMap.put("package", "prepay_id=" + prepayid);
				resultMap.put("signType", "MD5");
				// 这边要将返回的时间戳转化成字符串，不然小程序端调用wx.requestPayment方法会报签名错误
				resultMap.put("timeStamp", timeStamp + "");
				// 再次签名，这个签名用于小程序端调用wx.requesetPayment方法
				resultMap.put("appId", config.getAppID());
				String sign = WXPayUtil.generateSignature(resultMap, config.getKey());
				resultMap.put("paySign", sign);

				ObjectNode objectNode = JsonUtils.createObjectNode();
				objectNode.put("nonceStr", nonceStr);
				objectNode.put("package", "prepay_id=" + prepayid);
				objectNode.put("signType", "MD5");
				objectNode.put("timeStamp", timeStamp + "");
				objectNode.put("appId", config.getAppID());
				objectNode.put("paySign", sign);

				// 保存prepayid
				order.setPrepayId(prepayid);
				order.saveOrUpdate();

				response.outputJson(0, objectNode);

			} else {
				LOGGER.info("【統一支付】订单号：" + out_trade_no + "，统一支付失败：" + (String) rMap.get("err_code_des"));
				response.outputJson(80001, "【支付】订单号：" + out_trade_no + "，统一支付失败：" + (String) rMap.get("err_code_des"));
				return;
			}
		} catch (Exception e) {
			LOGGER.error("【統一支付】订单号：" + out_trade_no + "，内部错误", e);
			throw e;
		}

	}

	/**
	 * 用户已支付，调用该方法记录流水，该订单状态支付状态变更为：支付中
	 */
	@Transactional
	@RequestMapping(value = "/payment", method = RequestMethod.POST)
	public void Payment(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
		String openId = request.getString("openId");
		String out_trade_no = request.getString("outTradeNo");
		LOGGER.info("【支付中】订单号：" + out_trade_no);
		// 只需要把订单支付状态改为：支付中，加X锁，尽量防止支付通知比这里快
		WeChatUser weChatUser = new WeChatUserQuery().openId(openId).uniqueResult();
		Order order = new OrderQuery().weChatUser(weChatUser).orderNumber(out_trade_no).lockMode(LockMode.UPGRADE).uniqueResult();
		if (order == null) {
			response.outputJson(80002, "订单号" + out_trade_no + "不存在");
			LOGGER.info("【支付中】订单号：" + out_trade_no + "，不存在");
			return;
		}
		// 写入支付流水表
		PayBill payBill = new PayBillQuery().orderNumber(out_trade_no).openId(openId).payStatus(PayStatusEnum.ZFZ).lockMode(LockMode.UPGRADE).uniqueResult();
		if (payBill == null) {
			payBill = new PayBill();
		}
		payBill.setOpenId(openId);
		payBill.setOrderNumber(out_trade_no);
		payBill.setTotal_fee(order.getTotal());
		payBill.setPayStatus(PayStatusEnum.ZFZ);
		payBill.saveOrUpdate();
		order.setUinionPaytime(new Date());
		if (order.getPayState() == PayState.DZF) {
			order.setPayState(PayState.ZFZ);
			order.saveOrUpdate();
		} else {
			LOGGER.info("【支付中】订单号：" + out_trade_no + "，支付中状态：" + order.getPayState());
		}
		response.outputJson(0, "OK");

	}

	/**
	 * 退款请求
	 */
	@Transactional
	@RequestMapping(value = "/refund", method = RequestMethod.POST)
	public void Refund(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
		PayUtil payUtil = new PayUtil();
		String openId = request.getString("openId");
		String outTradeNo = request.getString("outTradeNo");

		if (openId == null || outTradeNo == null) {
			response.outputJson(90003, "抱歉，缺少订单或用户");
			LOGGER.info("【发起退款】订单号：空 ，缺少订单或用户");
			return;
		}
		LOGGER.info("【发起退款】订单号：" + outTradeNo + "，用户发起退款：" + openId);
		Order order = payUtil.getOrder(openId, outTradeNo, true);
		GroupOrder groupOrder = payUtil.getGroup(order.getGroupOrder().getId(), true);
		//是否允许申请退款
		if( groupOrder.getIsRefund() != null && groupOrder.getIsRefund() == false) {
			response.outputJson(90001, "抱歉，该团不允许申请退货，如有疑问请联系客服");
			return;
		}
		//判断商品是否已核销过
		VerificationSheet verificationSheet = new VerificationSheetQuery().orderNumber(outTradeNo).openId(openId).readOnly().firstResult();
		if(verificationSheet != null) {
			response.outputJson(90001, "抱歉，您的订单已经核销过，不能申请退货，如有疑问请联系客服");
			return;
		}
		//判断是否已升级过
		Set<OrderGoods> orderGoodsCheck = order.getOrderGoods();
		for (OrderGoods OrderGood : orderGoodsCheck) {  
				if(OrderGood.isIntegralUpgrade() !=null && OrderGood.isIntegralUpgrade()) {
				String commodityName = OrderGood.getGroupCommodity().getName();
				response.outputJson(90001, "抱歉，您的订单中【"+commodityName+"】已经升级，不能申请退货，如有疑问请联系客服");
				return;
			}
		}

		if (order == null) {
			response.outputJson(90002, "订单号" + outTradeNo + "不存在");
			LOGGER.info("【发起退款】订单号：" + outTradeNo + "，不存在");
			return;
		}
		OrderState orderState = new OrderStateQuery().orderStates(OrderStates.YQX).uniqueResult();
		// 判断该订单是否符合退款，如果符合，交给定时器处理
		if (order.getOrderState().getOrderStates() == OrderStates.DSH && order.getPayState() == PayState.YZF) {
			order.setPayState(PayState.DTK);
			order.setOrderState(orderState);
			order.setCancelTime(new Date());
			order.setCancelReason("用户发起退款");
			// 恢复单数和库存
			if (!order.getIsRecovery()) {
				for (OrderGoods orderGoods : order.getOrderGoods()) {
					Integer amount = orderGoods.getAmount();
					// GroupCommodity groupCommodity = orderGoods.getGroupCommodity();
					GroupCommodity groupCommodity = new GroupCommodityQuery().id(orderGoods.getGroupCommodity().getId()).lockMode(LockMode.UPGRADE).uniqueResult();
					Integer remnantInventory = groupCommodity.getRemnantInventory() + amount;
					groupCommodity.setRemnantInventory(remnantInventory);
					groupCommodity.saveOrUpdate();

				}
				order.setIsRecovery(true);
			}
			// 插入退款申请流水
			RefundBill refundBill = new RefundBillQuery().orderNumber(outTradeNo).openId(openId).refundStatus(RefundStatusEnum.DTK).lockMode(LockMode.UPGRADE).uniqueResult();
			if (refundBill == null) {
				refundBill = new RefundBill();
			}
			refundBill.setOrderNumber(outTradeNo);
			refundBill.setTransactionId(order.getPayNumber());
			refundBill.setRefundFee(order.getFinalPayment());
			refundBill.setRefundStatus(RefundStatusEnum.DTK);
			refundBill.setOpenId(openId);
			refundBill.saveOrUpdate();
			order.saveOrUpdate();
			response.outputJson(0, "申请退款成功");
		} else {
			LOGGER.info("【发起退款】订单号：" + outTradeNo + "，申请退款失败，订单状态已发生变化：" + order.getOrderState().getOrderStates() + "," + order.getPayState());
			response.outputJson(90001, "申请退款失败，订单状态已发生变化");
		}

	}

	/**
	 * 发送用户支付完成通知
	 */
	@Transactional
	@RequestMapping(value = "/paynotice", method = RequestMethod.POST)
	public void paynotice(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
		PayUtil payUtil = new PayUtil();
		String openId = request.getString("openId");
		String outTradeNo = request.getString("outTradeNo");
		String formId = request.getString("formId");
		if (openId == null || outTradeNo == null) {
			response.outputJson(90003, "抱歉，缺少订单或用户");
			LOGGER.info("【支付通知】订单号：空 ，缺少订单或用户");
			return;
		}
		Order order = payUtil.getOrder(openId, outTradeNo, true);
		if (order == null) {
			response.outputJson(90002, "抱歉，订单号" + outTradeNo + "不存在");
			LOGGER.info("【支付通知】订单号：" + outTradeNo + "，不存在");
			return;
		}
		GroupOrder groupOrder = order.getGroupOrder();
		String commodityName = "";
		for (OrderGoods orderGoods : order.getOrderGoods()) {
			commodityName = orderGoods.getGroupCommodity().getName();
			commodityName = order.getOrderGoods().size() > 1 ? commodityName + "等" : commodityName;
			break;
		}
		// 模版ID
		String templateId = Environment.getProperty("wechat.notice.joingroup");
		ObjectNode templateJson = JsonUtils.createObjectNode();
		templateJson.set("keyword1", JsonUtils.createObjectNode().put("value", order.getOrderNumber()));// 订单号
		templateJson.set("keyword2", JsonUtils.createObjectNode().put("value", commodityName));// 拼团商品
		templateJson.set("keyword3", JsonUtils.createObjectNode().put("value", "￥"+order.getTotal().toString()));// 订单金额
		templateJson.set("keyword4", JsonUtils.createObjectNode().put("value", DateTimeUtils.format(order.getUinionPaytime(), "yyyy-MM-dd HH:mm:ss")));// 支付时间
		templateJson.set("keyword5", JsonUtils.createObjectNode().put("value", "您已参团成功，请留意提货服务通知，谢谢！"));// 温馨提示
		WechatUtils.sendTemplateMessage(groupOrder.getId() , openId , formId, openId, templateJson, templateId, NoticeType.JOINGROUP);
		response.outputJson(0, "OK");
	}

	/**
	 * 向商户发起提现到零钱请求
	 */
	@Transactional
	@RequestMapping(value = "/TransToUser", method = RequestMethod.POST)
	public void transtouser(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {

	}

	/** 支付完成页信息 */
	@RequestMapping(value = "/paySuccess", method = RequestMethod.POST)
	public void paySuccess(HttpRequestWrapper request, HttpResponseWrapper response) {
		String openId = request.getString("openId");
		String orderNumber = request.getString("orderNumber");
		if (StringUtils.isEmpty(openId)) {
			response.outputJson(-1, "缺少openId参数");
			return;
		}
		if (StringUtils.isEmpty(orderNumber)) {
			response.outputJson(-1, "缺少orderNumber参数");
			return;
		}
		WeChatUser weChatUser = new WeChatUserQuery().openId(openId).uniqueResult();
		if (weChatUser == null) {
			response.outputJson(-1, "抱歉，当前所查询的用户不存在");
			return;
		}
		Order order = new OrderQuery().orderNumber(orderNumber).weChatUser(weChatUser).uniqueResult();
		if (order == null) {
			response.outputJson(-1, "抱歉，当前用户的订单不存在");
			return;
		}
		GroupOrder groupOrder = order.getGroupOrder();
		// 团长
		WeChatUser user = groupOrder.getWeChatUser();
		if (user == null) {
			response.outputJson(-1, "抱歉，当前所查询的团长用户不存在");
			return;
		}
		GroupOrderQuery groupOrderQuery = new GroupOrderQuery().weChatUser(user);
		int size = new OrderQuery().weChatUser(weChatUser).groupOrderQuery(groupOrderQuery).payState(PayState.YZF).list().size();

		int shareNumber = new ShareQuery().weChatUser(weChatUser).groupOrderQuery(groupOrderQuery).list().size();
		ObjectNode objectNode = JsonUtils.createObjectNode();
		objectNode.put("weChatUserName", weChatUser.getName());
		objectNode.put("orderSize", size);
		objectNode.put("communityName", order.getGroupOrder().getCommunity().getName());
		objectNode.put("shareNumber", shareNumber);
		objectNode.put("groupUserName", user.getName());
		objectNode.put("groupOrderId", groupOrder.getId());
		objectNode.put("theme", groupOrder.getTheme());

		response.outputJson(0, objectNode);
	}
}
