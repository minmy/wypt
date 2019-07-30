package cn.sini.cgb.api.cgb.task.pay;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.LockMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.sini.cgb.api.cgb.entity.group.GroupOrder;
import cn.sini.cgb.api.cgb.entity.group.GroupOrderState.States;
import cn.sini.cgb.api.cgb.entity.group.Order;
import cn.sini.cgb.api.cgb.entity.group.Order.PayMethod;
import cn.sini.cgb.api.cgb.entity.group.Order.PayState;
import cn.sini.cgb.api.cgb.entity.group.OrderState;
import cn.sini.cgb.api.cgb.entity.group.OrderState.OrderStates;
import cn.sini.cgb.api.cgb.entity.group.WeChatUser;
import cn.sini.cgb.api.cgb.entity.pay.AllBill.BillTypeEnum;
import cn.sini.cgb.api.cgb.entity.pay.PayBill;
import cn.sini.cgb.api.cgb.entity.pay.PayBill.PayStatusEnum;
import cn.sini.cgb.api.cgb.query.group.OrderQuery;
import cn.sini.cgb.api.cgb.query.group.OrderStateQuery;
import cn.sini.cgb.api.cgb.query.pay.PayBillQuery;
import cn.sini.cgb.common.task.AbstractTask;
import cn.sini.cgb.common.util.DateTimeUtils;
import cn.sini.cgb.common.util.Environment;
import cn.sini.cgb.common.util.PayUtil;
import cn.sini.cgb.common.wxpay.PayConfig;
import cn.sini.cgb.common.wxpay.WXPay;
import cn.sini.cgb.common.wxpay.WXPayUtil;

/**
 * 订单支付中状态确认付款定时任务
 * 
 * @author gaowei
 */
@Component
public class ConfirmPaymentTask extends AbstractTask {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmPaymentTask.class);

	@Override
	protected String taskName() {
		return "订单支付中状态确认付款定时任务";
	}

	@Override
	protected boolean showStartEndLog() {
		return true;
	}

	@Override
	@SuppressWarnings("static-access")
	protected void execute() throws Exception {
		if (!"true".equals(Environment.getProperty("job.enable"))) {
			return;
		}
		PayUtil payUtil = new PayUtil();
		String nonceStr = WXPayUtil.generateNonceStr();// 随机字符串
		PayConfig config = new PayConfig();
		WXPay wxPay = new WXPay(config);
		Date time = DateTimeUtils.addMinute(new Date(), -1);
		String sql = "select o.ID, o.ORDER_NUMBER, o.PAY_NUMBER, o.FK_GROUP_ORDER, o.FK_ORDER_STATE, o.FK_WECHAT_USER, o.PAY_STATE, o.PAY_TIME, o.IS_RECOVERY, o.TOTAL, o.FINAL_PAYMENT, p.OPEN_ID, p.CREATE_TIME "
				+ " from T_ORDER o " + " left join T_PAYBILL p on o.ORDER_NUMBER = p.ORDER_NUMBER "
				+ " where o.PAY_STATE = ? AND p.PAY_STATUS = ? AND p.CREATE_TIME <= ?";
		List<Map<String, Object>> mapList = new OrderQuery().queryMapListBySql(sql,
				new Object[] { PayState.ZFZ.toString(), PayStatusEnum.ZFZ.toString(), time });
		for (Map<String, Object> map : mapList) {
			String orderNumber = (String) map.get("ORDER_NUMBER");// 商户订单号
			LOGGER.info("【支付中】订单：" + orderNumber + "，主动获取支付状态");
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("appid", config.getAppID());
			data.put("mch_id", config.getMchID());
			data.put("nonce_str", nonceStr);
			data.put("out_trade_no", orderNumber);
			data.put("sign", WXPayUtil.generateSignature(data, config.getKey()));
			Map<String, String> resultMap = wxPay.orderQuery(data);
			String return_code = resultMap.get("return_code");
			String result_code = resultMap.get("result_code");
			if ("SUCCESS".equals(return_code) && return_code.equals(result_code)) {
				LOGGER.info("【支付中】订单：" + orderNumber + "，查询到支付结果");
				String trade_state = resultMap.get("trade_state");// 交易状态
				String out_trade_no = resultMap.get("out_trade_no");// 订单编号
				String openId = resultMap.get("openid");
				String transaction_id = resultMap.get("transaction_id");// 微信支付订单号
				String time_end = resultMap.get("time_end");// 支付完成时间
				String total_fee = resultMap.get("total_fee");// 订单总金额，单位为分
				Order order = payUtil.getOrder(openId, out_trade_no, true);
				Long groupId = order.getGroupOrder().getId();
				// 判断团单状态
				OrderState orderState = new OrderStateQuery().orderStates(OrderStates.DSH).uniqueResult();
				GroupOrder groupOrder = payUtil.getGroup(groupId, true);
				// Order order = new
				// OrderQuery().orderNumber(orderNumber).uniqueResult();
				if ("SUCCESS".equals(trade_state)) {// 订单已支付
					// 判断团单状态是否为：进行中，最正常的状态
					if (groupOrder.getGroupOrderState().getStates() == States.JXZ) {
						// 进行中，最正常的状态
						if (order.getOrderState().getOrderStates() == OrderState.OrderStates.DFK) {
							order.setOrderState(orderState);
							order.setPayState(PayState.YZF);
						}
						// 被定时器取消，这个支付需要退款
						if (order.getOrderState().getOrderStates() == OrderState.OrderStates.YQX) {
							order.setPayState(PayState.DTK);
						}
					}
					// 判断团单状态是否为：未成团 或者 判断团单状态是否为：已结束
					if (groupOrder.getGroupOrderState().getStates() == States.WCT
							|| groupOrder.getGroupOrderState().getStates() == States.YJS) {
						order.setPayState(PayState.DTK);
					}
					order.setPayNumber(transaction_id);
					order.setPayTime(DateTimeUtils.parse(time_end, "yyyyMMddHHmmss"));// 支付完成时间
					BigDecimal divide = new BigDecimal(total_fee).divide(new BigDecimal("100"));
					order.setFinalPayment(divide);
					order.setPayMethod(PayMethod.微信);
					order.saveOrUpdate();
					
					// 写入支付流水表
					PayBill payBill = new PayBillQuery().orderNumber(out_trade_no).openId(openId)
							.payStatus(PayStatusEnum.YZF).lockMode(LockMode.UPGRADE).uniqueResult();
					if (payBill == null) {
						payBill = new PayBill();
					}
					payBill.setOpenId(openId);
					payBill.setPayStatus(PayStatusEnum.YZF);
					payBill.setOrderNumber(out_trade_no);
					payBill.setTransactionId(transaction_id);
					payBill.setTotal_fee(divide);
					payBill.setTime_end(DateTimeUtils.parse(time_end, "yyyyMMddHHmmss"));
					payBill.setPayXml(resultMap.toString());
					payBill.saveOrUpdate();
					// 团长虚拟账号操作
					WeChatUser weChatUser = order.getGroupOrder().getWeChatUser();
					payUtil.virtualAccountAction(weChatUser, BillTypeEnum.SR, order, divide);
					
					LOGGER.info("【支付中】订单：" + orderNumber + "，更新完成");

				} else if ("NOTPAY".equals(trade_state)) {// 订单未支付
					LOGGER.info("【支付中】订单号：" + orderNumber + "，未支付，NOTPAY：" + resultMap.get("return_msg"));
				} else if ("PAYERROR".equals(trade_state)) {// 支付失败
					// 支付失败
					order.setPayState(PayState.YZSB);
					order.setOrderState(new OrderStateQuery().orderStates(OrderStates.YQX).uniqueResult());
					order.saveOrUpdate();
					// 写入支付流水表
					PayBill payBill = new PayBillQuery().orderNumber(out_trade_no).openId(openId)
							.payStatus(PayStatusEnum.ZFSB).lockMode(LockMode.UPGRADE).uniqueResult();
					if (payBill == null) {
						payBill = new PayBill();
					}
					BigDecimal divide = new BigDecimal(total_fee).divide(new BigDecimal("100"));
					payBill.setOpenId(openId);
					payBill.setPayStatus(PayStatusEnum.ZFSB);
					payBill.setOrderNumber(out_trade_no);
					payBill.setTransactionId(transaction_id);
					payBill.setTotal_fee(divide);
					payBill.setTime_end(DateTimeUtils.parse(time_end, "yyyyMMddHHmmss"));
					payBill.setPayXml(resultMap.toString());
					payBill.saveOrUpdate();
				}
			} else {
				LOGGER.info("【支付中】订单号：" + orderNumber + "，" + resultMap.get("return_msg"));
			}
		}
	}

	/** 保存支付流水表 */
	// public void savePayBill(String out_trade_no, payStatusEnum payStatus,
	// String openId, String payNo, BigDecimal finalPayment, Date payTime) {
	// PayBill payBill = new
	// PayBillQuery().orderNumber(out_trade_no).openId(openId).payStatus(payStatusEnum.YZF).lockMode(LockMode.UPGRADE).uniqueResult();
	// if (payBill == null) {
	// payBill = new PayBill();
	// }
	// payBill.setOpenId(openId);
	// payBill.setPayStatus(payStatus);
	// payBill.setOrderNumber(out_trade_no);
	// payBill.setTransactionId(payNo);
	// payBill.setTotal_fee(finalPayment);
	// payBill.setTime_end(payTime);
	// payBill.saveOrUpdate();
	// }
}
