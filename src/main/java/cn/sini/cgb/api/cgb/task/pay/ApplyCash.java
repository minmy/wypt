package cn.sini.cgb.api.cgb.task.pay;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.LockMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.sini.cgb.api.cgb.entity.pay.ApplyWithdrawMoney;
import cn.sini.cgb.api.cgb.entity.pay.ApplyWithdrawMoney.ExamineState;
import cn.sini.cgb.api.cgb.entity.pay.ApplyWithdrawMoney.HandleState;
import cn.sini.cgb.api.cgb.entity.pay.ApplyWithdrawMoneyBill;
import cn.sini.cgb.api.cgb.query.pay.ApplyWithdrawMoneyQuery;
import cn.sini.cgb.common.task.AbstractTask;
import cn.sini.cgb.common.util.DateTimeUtils;
import cn.sini.cgb.common.util.Environment;
import cn.sini.cgb.common.util.PayUtil;
import cn.sini.cgb.common.wxpay.PayConfig;
import cn.sini.cgb.common.wxpay.WXPay;
import cn.sini.cgb.common.wxpay.WXPayUtil;

/*
 * 公司微信账户直接提现到团长微信零钱
 * 具有以下限制
 * 1、同一用户当天不能提现超过1次
 * 2、同一收款账户当天不能超过2000元
 * 3、单笔提现金额为0.99~2000元 人民币
 * 4、商户当天不能超过1万
 */
@Component
public class ApplyCash extends AbstractTask {
	private static final Logger LOGGER = LoggerFactory.getLogger(ApplyCash.class);

	@Override
	protected String taskName() {
		return "付款到零钱任务";
	}

	@Override
	protected void execute() throws Exception {
		if (!"true".equals(Environment.getProperty("job.enable"))) {
			return;
		}
		PayUtil payUtil = new PayUtil();
		// 获取提现记录 ,为了安全起见，每次只处理1条提现，以后按业务需求变更
		ApplyWithdrawMoney applyWithdrawMoneys = new ApplyWithdrawMoneyQuery().handleState(HandleState.TXZ).examineState(ExamineState.YTG).lockMode(LockMode.UPGRADE).firstResult();
		if(applyWithdrawMoneys == null) {
			return;
		}
		BigDecimal checkFee = applyWithdrawMoneys.getApplyMoneyAmount();
		BigDecimal totalFee = applyWithdrawMoneys.getActualAmount();
		String applyNumber = applyWithdrawMoneys.getApplyNumber();
		String openId = applyWithdrawMoneys.getOpenId();
		String signString = applyWithdrawMoneys.getSign();
		String iv = applyWithdrawMoneys.getTimestamp();
		String sign = payUtil.newSign(openId, checkFee, applyNumber, iv, "");
		String applyRealName = applyWithdrawMoneys.getWeChatUser().getApplyRealName();
		ApplyWithdrawMoneyBill amb = new ApplyWithdrawMoneyBill();
		amb.setApplyNumber(applyNumber);
		amb.setOpenId(openId);
		
		if (StringUtils.isEmpty(signString) || !signString.equals(sign) ) {
			applyWithdrawMoneys.setHandleState(HandleState.TXSB);
			applyWithdrawMoneys.setTransResults("校验错误");
			applyWithdrawMoneys.saveOrUpdate();
			amb.setTransResult("校验错误："+ signString);
			amb.saveOrUpdate();
			return;
		}
		//发起提现
		PayConfig config = null;
		WXPay wxpay = null;
		try {
			config = new PayConfig();
			wxpay = new WXPay(config);
		} catch (Exception e) {
			e.printStackTrace();
		}
		InetAddress addr = null;
		try {
			addr = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			throw e;
		}
		String spbill_create_ip = addr.getHostAddress();
		HashMap<String, String> data = new HashMap<String, String>();
		String nonce_str = WXPayUtil.generateNonceStr();
		data.put("mch_appid", config.getAppID());
		data.put("mchid", config.getMchID());
		data.put("nonce_str", nonce_str);
		data.put("partner_trade_no", applyNumber);
		data.put("openid", openId);
		data.put("check_name", "FORCE_CHECK");
		data.put("re_user_name", applyRealName);
		data.put("amount", totalFee.multiply(new BigDecimal("100")).setScale(0, BigDecimal.ROUND_DOWN).toString());
		data.put("desc", "我想拼团提现");
		data.put("spbill_create_ip", spbill_create_ip);
		String signKeyString = WXPayUtil.generateSignature(data, config.getKey());
		data.put("sign", signKeyString);
		//请求接口
		Map<String, String> rMap = wxpay.transfers(data);
		String return_code = (String) rMap.get("return_code");
		String result_code = (String) rMap.get("result_code");
		String return_msg = (String) rMap.get("return_msg");
		amb.setReturnCode(return_code);
		amb.setResultCode(result_code);
		amb.setReturnMsg(return_msg);
		amb.setTransResultsXml(rMap.toString());
		amb.saveOrUpdate();
		if ("SUCCESS".equals(return_code)) {
			String err_code = (String) rMap.get("err_code");
			//当返回错误码为“SYSTEMERROR”时，请不要更换商户订单号，一定要使用原商户订单号重试，否则可能造成重复支付等资金风险
			if("SYSTEMERROR".equals(err_code)) {
				LOGGER.info("【发起提现】订单号：" + applyNumber + "，SYSTEMERROR");
				applyWithdrawMoneys.setHandleState(HandleState.TXSB);
				applyWithdrawMoneys.setTransResults("SYSTEMERROR");
			}
			if("FAIL".equals(result_code)) {
				LOGGER.info("【发起提现】订单号：" + applyNumber + "，FAIL:" + err_code);
				applyWithdrawMoneys.setHandleState(HandleState.TXSB);
				applyWithdrawMoneys.setTransResults("FAIL");
			}
			//提现成功
			if("SUCCESS".equals(result_code)) {
				LOGGER.info("【发起提现】订单号：" + applyNumber + "，SUCCESS");
				String paymentNo = rMap.get("payment_no");
				String paymentTime = rMap.get("payment_time");
				applyWithdrawMoneys.setHandleState(HandleState.YTX);
				applyWithdrawMoneys.setPaymentNo(paymentNo);
				applyWithdrawMoneys.setTransResults(result_code);
				applyWithdrawMoneys.setPaymentTime(DateTimeUtils.parse(paymentTime, "yyyy-MM-dd HH:mm:ss"));
				applyWithdrawMoneys.saveOrUpdate();
			}
			applyWithdrawMoneys.saveOrUpdate();
		} else {
			LOGGER.info("【发起提现】订单号：" + applyNumber + "，提现失败:" + return_msg);
			applyWithdrawMoneys.setHandleState(HandleState.TXSB);
			applyWithdrawMoneys.setTransResults(return_msg);
			applyWithdrawMoneys.saveOrUpdate();
		}
	}
}
