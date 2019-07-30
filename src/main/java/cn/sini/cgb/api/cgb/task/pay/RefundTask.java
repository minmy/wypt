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
import cn.sini.cgb.api.cgb.entity.group.WeChatUser;
import cn.sini.cgb.api.cgb.entity.pay.AllBill.BillTypeEnum;
import cn.sini.cgb.api.cgb.entity.pay.RefundBill;
import cn.sini.cgb.api.cgb.entity.pay.RefundBill.RefundStatusEnum;
import cn.sini.cgb.api.cgb.query.group.OrderQuery;
import cn.sini.cgb.api.cgb.query.pay.RefundBillQuery;
import cn.sini.cgb.common.task.AbstractTask;
import cn.sini.cgb.common.util.DateTimeUtils;
import cn.sini.cgb.common.util.Environment;
import cn.sini.cgb.common.util.PayUtil;
import cn.sini.cgb.common.wxpay.PayConfig;
import cn.sini.cgb.common.wxpay.WXPay;
import cn.sini.cgb.common.wxpay.WXPayUtil;

@Component
public class RefundTask extends AbstractTask {
	private static final Logger LOGGER = LoggerFactory.getLogger(RefundTask.class);

	@Override
	protected String taskName() {
		return "统一退款定时任务";
	}

	@Override
	protected boolean showStartEndLog() {
		return true;
	}

	@Override
	public void execute() throws Exception {
		if (!"true".equals(Environment.getProperty("job.enable"))) {
			return;
		}
		// 获取需要退款的order
		PayUtil payUtil = new PayUtil();
		PayConfig config = null;
		WXPay wxpay = null;
		try {
			config = new PayConfig();
			wxpay = new WXPay(config);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String openid = "";
		String nonce_str = "";
		String out_trade_no = "";
		String transaction_id = "";
		String out_refund_no = "";
		BigDecimal refund_fee = null;
		BigDecimal total_fee = null;
		List<Order> orders = new OrderQuery().payState(PayState.DTK).lockMode(LockMode.UPGRADE).list();
		for (Order order : orders) {
			openid = order.getWeChatUser().getOpenId();
			out_trade_no = order.getOrderNumber();
			transaction_id = order.getPayNumber();
			refund_fee = order.getFinalPayment();
			total_fee = order.getTotal();
			// 退款参数拼接
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("appid", config.getAppID());
			data.put("mch_id", config.getMchID());
			nonce_str = WXPayUtil.generateNonceStr();
			data.put("nonce_str", nonce_str);
			out_refund_no = "TK" + DateTimeUtils.format(new Date(), "yyyyMMddHHmmsss")
					+ (int) ((Math.random() * 9 + 1) * 1000);
			data.put("out_refund_no", out_refund_no);
			data.put("out_trade_no", out_trade_no);
			data.put("refund_fee",
					refund_fee.multiply(new BigDecimal("100")).setScale(0, BigDecimal.ROUND_DOWN).toString());
			data.put("total_fee",
					total_fee.multiply(new BigDecimal("100")).setScale(0, BigDecimal.ROUND_DOWN).toString());
			data.put("transaction_id", transaction_id);
			data.put("notify_url", Environment.getProperty("host") + Environment.getProperty("path")
					+ "/payaction/refundnotify.action");
			data.put("sign", WXPayUtil.generateSignature(data, config.getKey()));
			Map<String, String> rMap = wxpay.refund(data);
			Thread.sleep(100);
			String return_code = (String) rMap.get("return_code");
			String result_code = (String) rMap.get("result_code");
			String err_code_des = (String) rMap.get("err_code_des");
			String return_msg = (String) rMap.get("return_msg");
			LOGGER.info("【发起退款】订单号：" + out_trade_no + "，"+ return_msg);
			if ("SUCCESS".equals(return_code) && return_code.equals(result_code)) {
				LOGGER.info("【发起退款】订单号：" + out_trade_no + "，退款中");
				order.setPayState(PayState.TKZ);
				order.saveOrUpdate();
				// 插入退款申请流水
				RefundBill refundBill = new RefundBillQuery().orderNumber(out_trade_no).openId(openid)
						.refundStatus(RefundStatusEnum.TKZ).lockMode(LockMode.UPGRADE).uniqueResult();
				if (refundBill == null) {
					refundBill = new RefundBill();
				}
				refundBill.setOrderNumber(out_trade_no);
				refundBill.setTransactionId(transaction_id);
				refundBill.setOutRefundNo(out_refund_no);
				refundBill.setRefundFee(order.getFinalPayment());
				refundBill.setRefundStatus(RefundStatusEnum.TKZ);
				refundBill.saveOrUpdate();
				WeChatUser weChatUser = order.getGroupOrder().getWeChatUser();
				payUtil.virtualAccountAction(weChatUser, BillTypeEnum.TZ, order, order.getFinalPayment());
				LOGGER.info("【发起退款】订单号：" + out_trade_no + "，发起退款完成，待退款通知确认");
			} else {
				LOGGER.info("【发起退款】订单号：" + out_trade_no + "，退款失败" + err_code_des);
				order.setPayState(PayState.TKSB);
				order.saveOrUpdate();
			}
		}
	}

}
