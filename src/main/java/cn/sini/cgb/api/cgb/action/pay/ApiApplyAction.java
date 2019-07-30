package cn.sini.cgb.api.cgb.action.pay;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.LockMode;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.sini.cgb.api.cgb.entity.group.WeChatUser;
import cn.sini.cgb.api.cgb.entity.pay.AllBill;
import cn.sini.cgb.api.cgb.entity.pay.AllBill.BillTypeEnum;
import cn.sini.cgb.api.cgb.entity.pay.AllBill.CashTypeEnum;
import cn.sini.cgb.api.cgb.entity.pay.ApplyWithdrawMoney;
import cn.sini.cgb.api.cgb.entity.pay.ApplyWithdrawMoney.ExamineState;
import cn.sini.cgb.api.cgb.entity.pay.ApplyWithdrawMoney.HandleState;
import cn.sini.cgb.api.cgb.entity.pay.VirtualAccount;
import cn.sini.cgb.api.cgb.entity.pay.VirtualAccountBill;
import cn.sini.cgb.api.cgb.entity.pay.VirtualAccountBill.BillType;
import cn.sini.cgb.api.cgb.query.group.WeChatUserQuery;
import cn.sini.cgb.api.cgb.query.pay.AllBillQuery;
import cn.sini.cgb.api.cgb.query.pay.ApplyWithdrawMoneyQuery;
import cn.sini.cgb.api.cgb.query.pay.VirtualAccountQuery;
import cn.sini.cgb.common.http.HttpRequestWrapper;
import cn.sini.cgb.common.http.HttpResponseWrapper;
import cn.sini.cgb.common.json.JsonUtils;
import cn.sini.cgb.common.util.DateTimeUtils;
import cn.sini.cgb.common.util.Environment;
import cn.sini.cgb.common.util.PayUtil;

/**
 * 申请提现Action
 * 
 * @author gaowei
 */
@Controller
@RequestMapping("/api/apply")
public class ApiApplyAction {

	/** 申请提现 */
	@SuppressWarnings("deprecation")
	@Transactional
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public void save(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
		String openId = request.getString("openId");
		String applyRealName = request.getString("applyRealName");
		String applyPhone = request.getString("applyPhone", "^1\\d{10}$");
		String applyWeChat = request.getString("applyWeChat");
		String applyGroupOrderId = request.getString("applyGroupOrderId");
		String applyTotalFee = request.getString("applyTotalFee");
		BigDecimal totalFee = new BigDecimal("0");
		String timestamp = new Date().getTime() + "";
		PayUtil payUtil = new PayUtil();
		if (StringUtils.isBlank(openId)) {
			response.outputJson(-1, "缺少openId参数");
			return;
		}
		if (StringUtils.isBlank(applyRealName)) {
			response.outputJson(-1, "缺少applyRealName参数");
			return;
		}
		if (StringUtils.isBlank(applyPhone)) {
			response.outputJson(-1, "缺少applyPhone参数");
			return;
		}
		if (StringUtils.isBlank(applyWeChat)) {
			response.outputJson(-1, "缺少applyWeChat参数");
			return;
		}
		if (StringUtils.isBlank(applyGroupOrderId)) {
			response.outputJson(-1, "缺少applyGroupOrderId参数");
			return;
		}
		if (StringUtils.isBlank(applyTotalFee)) {
			response.outputJson(-1, "缺少applyTotalFee参数");
			return;
		}
		if(StringUtils.isEmpty(applyGroupOrderId)){
			response.outputJson(-1, "请选择需要提现的拼团");
			return;
		}
		if(StringUtils.isEmpty(applyTotalFee)){
			response.outputJson(-1, "提现金额错误");
			return;
		}
		
		String[] arrApplyGroupOrderId = applyGroupOrderId.split(",");
		WeChatUser weChatUser = new WeChatUserQuery().openId(openId).uniqueResult();
		if (weChatUser == null) {
			response.outputJson(-1, "申请失败，用户不存在");
			return;
		}
		//查询对应体现的团单号
		List<AllBill> allbillList = new AllBillQuery().weChatUser(weChatUser).flag(CashTypeEnum.KTX).groupId(arrApplyGroupOrderId).lockMode(LockMode.UPGRADE).list();
		if(allbillList == null) {
			response.outputJson(-1, "申请失败，找不到对应支付记录");
			return;
		}
		
		//对比前端提交的金额
		for (AllBill allBill : allbillList) {
			if(allBill.getBillType().equals(BillTypeEnum.SR)) {
				totalFee = totalFee.add(allBill.getTotal_fee());
			}else {
				totalFee = totalFee.subtract(allBill.getTotal_fee());
			}
		}
		BigDecimal applyTotalFeebDecimal  = new BigDecimal(applyTotalFee);
		if(totalFee.compareTo(applyTotalFeebDecimal) != 0) {
			response.outputJson(-1, "申请失败，申请金额错误");
			return;
		}
		
		if(totalFee.compareTo(BigDecimal.ONE) < 0) {
			response.outputJson(-1, "申请失败，申请金额不能少于1元");
			return;
		}
		weChatUser.setApplyPhone(applyPhone);
		weChatUser.setApplyRealName(applyRealName);
		weChatUser.setApplyWeChat(applyWeChat);
		weChatUser.saveOrUpdate();

		VirtualAccount virtualAccount = new VirtualAccountQuery().openId(openId).lockMode(LockMode.UPGRADE).uniqueResult();
		if (virtualAccount == null) {
			response.outputJson(-1, "申请失败，未找到团长账户");
			return;
		}
		BigDecimal withdrawableCash = virtualAccount.getWithdrawableCash();// 判断是否够钱
		if (withdrawableCash.subtract(totalFee).compareTo(BigDecimal.ZERO) < 0) {
			response.outputJson(-1, "申请失败，可提现金额不足");
			return;
		}
		//计算手续费和实际提现金额
		long thisDate = System.currentTimeMillis() - weChatUser.getCreateTime().getTime();
		long days = thisDate / (1000 * 3600 * 24);// 计算两个日期相差的天数
		BigDecimal actualAmount = new BigDecimal("0");// 实际金额
		if (days > 99) {
			String serviceChargeLater = Environment.getProperty("pay.serviceCharge.later");// 0.012
			actualAmount = totalFee.subtract(totalFee.multiply(new BigDecimal(serviceChargeLater)));
		} else {
			String serviceChargeEarly = Environment.getProperty("pay.serviceCharge.early");// 0.006
			actualAmount = totalFee.subtract(totalFee.multiply(new BigDecimal(serviceChargeEarly)));
		}
		actualAmount = actualAmount.setScale(2, BigDecimal.ROUND_DOWN);
		List<String> resMap = payUtil.checkCashwithdrawal(openId, actualAmount);
		if(resMap.size() != 0) {
			String errString = resMap.get(0);
			response.outputJson(-1, errString);
			return;
		}
		
		//满足所有条件后,团长账户减去响应的金额
		BigDecimal alreadyAvailable = virtualAccount.getAlreadyAvailable();
		BigDecimal newAlreadyAvailable = new BigDecimal(String.valueOf(alreadyAvailable)).add(totalFee);
		virtualAccount.setAlreadyAvailable(newAlreadyAvailable);
		virtualAccount.setWithdrawableCash(virtualAccount.getWithdrawableCash().subtract(totalFee));
		virtualAccount.saveOrUpdate();

		String applyNumber = "TX" + DateTimeUtils.format(new Date(), "yyyyMMddHHmmsss") + (int) ((Math.random() * 9 + 1) * 1000);

		
		// 提现申请
		ApplyWithdrawMoney awm = new ApplyWithdrawMoney();
		awm.setOpenId(openId);
		awm.setWeChatUser(weChatUser);
		awm.setHandleState(HandleState.DTX);
		awm.setExamineState(ExamineState.DSH);
		awm.setRetryNumber(1);
		awm.setApplyNumber(applyNumber);
		awm.setApplyMoneyAmount(totalFee);
		awm.setTimestamp(timestamp);
		awm.setActualAmount(actualAmount);
		awm.setSign(payUtil.newSign(openId, totalFee, applyNumber, timestamp , "" ));
		awm.saveOrUpdate();

		// 转移allbill的流水，将对应的可提现金额明细转移到，已提现
		for (AllBill allBill : allbillList) {
			allBill.setApplyNumber(applyNumber);
			allBill.setFlag(CashTypeEnum.YTX);
			allBill.saveOrUpdate();
		}
		// 虚拟账户记录流水表
		VirtualAccountBill vab = new VirtualAccountBill();
		vab.setOpenId(openId);
		vab.setApplyNumber(applyNumber);
		vab.setBillType(BillType.TX);
		vab.setAmountMoney(totalFee);
		vab.setBeforeAlreadyAvailable(alreadyAvailable);
		vab.setBeforeNoWithdrawn(virtualAccount.getNoWithdrawn());
		vab.setBeforeWithdrawableCash(withdrawableCash);
		vab.setBeforeGrossIncome(virtualAccount.getGrossIncome());
		vab.saveOrUpdate();
		response.outputJson(0, "申请成功");
	}
	
	/*
	 * 提現列表
	 */
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public void list(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
		String openId = request.getString("openId");
		if (StringUtils.isEmpty(openId)) {
			response.outputJson(-1, "缺少openId参数");
			return;
		}
		ArrayNode arrayNode = JsonUtils.createArrayNode();
		ObjectNode objectNode = JsonUtils.createObjectNode();
		List<ApplyWithdrawMoney> awmlists = new ApplyWithdrawMoneyQuery().openId(openId).orderBy("createTime", false).list();
		for (ApplyWithdrawMoney applyWithdrawMoney : awmlists) {
			ObjectNode node = JsonUtils.createObjectNode();
			String applyNumber = applyWithdrawMoney.getApplyNumber();
			String applyMoneyAmount = applyWithdrawMoney.getApplyMoneyAmount().toString();
			String actualAmount = applyWithdrawMoney.getActualAmount().toString();
			String createTime = DateTimeUtils.format(applyWithdrawMoney.getCreateTime(), "yyyy-MM-dd-HH:mm:ss");
			String handleState = applyWithdrawMoney.getHandleState().getDesc();
			String examineState = applyWithdrawMoney.getExamineState().getDesc();
			node.put("applyNumber", applyNumber);
			node.put("applyMoneyAmount", applyMoneyAmount);
			node.put("actualAmount", actualAmount);
			node.put("createTime", createTime);
			node.put("handleState", handleState);
			node.put("examineState", examineState);
			arrayNode.add(node);
		}
		
		response.outputJson(0, arrayNode);
	}
}
