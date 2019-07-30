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

import cn.sini.cgb.api.cgb.entity.group.Order;
import cn.sini.cgb.api.cgb.entity.group.Order.PayState;
import cn.sini.cgb.api.cgb.entity.pay.RefundBill;
import cn.sini.cgb.api.cgb.entity.pay.RefundBill.RefundStatusEnum;
import cn.sini.cgb.api.cgb.query.group.OrderQuery;
import cn.sini.cgb.api.cgb.query.pay.RefundBillQuery;
import cn.sini.cgb.common.task.AbstractTask;
import cn.sini.cgb.common.util.DateTimeUtils;
import cn.sini.cgb.common.util.Environment;
import cn.sini.cgb.common.wxpay.PayConfig;
import cn.sini.cgb.common.wxpay.WXPay;
import cn.sini.cgb.common.wxpay.WXPayUtil;

/**
 * 订单退款中状态确认退款定时任务
 * 
 * @author gaowei
 */
@Component
public class ConfirmRefundTask extends AbstractTask {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmRefundTask.class);

	@Override
	protected String taskName() {
		return "订单退款中状态确认退款定时任务";
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
		String nonceStr = WXPayUtil.generateNonceStr();// 随机字符串
		PayConfig config = new PayConfig();
		WXPay wxPay = new WXPay(config);
		Date time = DateTimeUtils.addMinute(new Date(), -1);
		String sql = "select o.ID, o.ORDER_NUMBER, o.PAY_NUMBER, o.FK_GROUP_ORDER, o.FK_ORDER_STATE, o.FK_WECHAT_USER, o.PAY_STATE, o.PAY_TIME, o.IS_RECOVERY, o.TOTAL, o.FINAL_PAYMENT, r.OPEN_ID, r.CREATE_TIME"
				+ " from T_ORDER o " + " left join T_REFUNDBILL r on o.ORDER_NUMBER = r.ORDER_NUMBER "
				+ " where o.PAY_STATE = ? AND r.REFUND_STATUS = ? AND r.CREATE_TIME <= ?";
		List<Map<String, Object>> mapList = new OrderQuery().queryMapListBySql(sql,
				new Object[] { PayState.TKZ.toString(), RefundStatusEnum.TKZ.toString(), time });
		for (Map<String, Object> map : mapList) {
			String orderNumber = (String) map.get("ORDER_NUMBER");// 商户订单号
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("appid", config.getAppID());
			data.put("mch_id", config.getMchID());
			data.put("nonce_str", nonceStr);
			data.put("out_trade_no", orderNumber);
			data.put("sign", WXPayUtil.generateSignature(data, config.getKey()));
			LOGGER.info("【退款中】订单：" + orderNumber +"，主动获取退款状态");
			Map<String, String> resultMap = wxPay.refundQuery(data);
			// {transaction_id=4200000317201905277379408244,
			// nonce_str=pncHlVmbKYlIlLHZ,
			// out_refund_no_0=TK2019052722030206007,
			// refund_status_0=SUCCESS, sign=90E6D98657FA5453F1FB9451743997B6,
			// refund_fee_0=688, refund_recv_accout_0=建设银行借记卡0578,
			// return_msg=OK, mch_id=1491490122,
			// refund_success_time_0=2019-05-27 22:03:59, cash_fee=688,
			// refund_id_0=50000400562019052709756145287,
			// out_trade_no=2019052717340571502, appid=wx53b0d174ab62b643,
			// refund_fee=688,
			// total_fee=688, result_code=SUCCESS,
			// refund_account_0=REFUND_SOURCE_UNSETTLED_FUNDS, refund_count=1,
			// return_code=SUCCESS,
			// refund_channel_0=ORIGINAL}
			String return_code = resultMap.get("return_code");
			String result_code = resultMap.get("result_code");
			if ("SUCCESS".equals(return_code)) {
				if (return_code.equals(result_code)) {
					LOGGER.info("【退款中】订单：" + orderNumber +"，查询到退款结果");
					String refund_status = resultMap.get("refund_status_0");// 交易状态
					String success_time = resultMap.get("refund_success_time_0");
					String outTradeNo = resultMap.get("out_trade_no");
					String transaction_id = resultMap.get("transaction_id");
					String out_refund_no = resultMap.get("out_refund_no_0");
					String refund_id = resultMap.get("refund_id_0");

					Order order = new OrderQuery().orderNumber(outTradeNo).lockMode(LockMode.UPGRADE).uniqueResult();
					RefundBill refundBill = new RefundBillQuery().openId(order.getWeChatUser().getOpenId())
							.orderNumber(outTradeNo).refundStatus(RefundStatusEnum.YTK).lockMode(LockMode.UPGRADE)
							.uniqueResult();
					if (refundBill == null) {
						refundBill = new RefundBill();
					}

					refundBill.setRealRefundTime(DateTimeUtils.parse(success_time, "yyyy-MM-dd HH:mm:ss"));
					refundBill.setOrderNumber(outTradeNo);
					refundBill.setTransactionId(transaction_id);
					refundBill.setOutRefundNo(out_refund_no);
					refundBill.setRefundNo(refund_id);
					refundBill.setRefundFee(
							new BigDecimal(String.valueOf(Integer.valueOf(resultMap.get("refund_fee")) / 100.0)));
					refundBill.setSettlementRefundFee(
							new BigDecimal(String.valueOf(Integer.valueOf(resultMap.get("refund_fee")) / 100.0)));
					if ("SUCCESS".equals(refund_status)) {// 退款成功
						refundBill.setRefundStatus(RefundStatusEnum.YTK);
						order.setPayState(PayState.YTK);
					} else {
						refundBill.setRefundStatus(RefundStatusEnum.TKSB);
						order.setPayState(PayState.TKSB);
						LOGGER.info("【退款中】失败，" + resultMap.get("refund_status"));
					}
					refundBill.setRefundXml(resultMap.toString());
					refundBill.saveOrUpdate();
					order.saveOrUpdate();
					LOGGER.info("【退款中】订单：" + orderNumber +"，更新完成");
				} else {
					LOGGER.info("【退款中】订单号：" + orderNumber +"，"+ resultMap.get("return_msg"));
				}
			}
		}
	}
}
