package cn.sini.cgb.common.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.LockMode;

import cn.sini.cgb.api.cgb.entity.group.GroupOrder;
import cn.sini.cgb.api.cgb.entity.group.GroupOrderState;
import cn.sini.cgb.api.cgb.entity.group.Order;
import cn.sini.cgb.api.cgb.entity.group.WeChatUser;
import cn.sini.cgb.api.cgb.entity.pay.AllBill;
import cn.sini.cgb.api.cgb.entity.pay.AllBill.BillTypeEnum;
import cn.sini.cgb.api.cgb.entity.pay.AllBill.CashTypeEnum;
import cn.sini.cgb.api.cgb.entity.pay.ApplyWithdrawMoney;
import cn.sini.cgb.api.cgb.entity.pay.VirtualAccount;
import cn.sini.cgb.api.cgb.entity.pay.VirtualAccountBill;
import cn.sini.cgb.api.cgb.entity.pay.VirtualAccountBill.BillType;
import cn.sini.cgb.api.cgb.query.group.GroupOrderQuery;
import cn.sini.cgb.api.cgb.query.group.OrderQuery;
import cn.sini.cgb.api.cgb.query.group.WeChatUserQuery;
import cn.sini.cgb.api.cgb.query.pay.AllBillQuery;
import cn.sini.cgb.api.cgb.query.pay.ApplyWithdrawMoneyQuery;
import cn.sini.cgb.api.cgb.query.pay.VirtualAccountQuery;

@SuppressWarnings("deprecation")
public class PayUtil {

	public Order getOrder(String openId, String orderNumber, Boolean isLock) {
		WeChatUser weChatUser = new WeChatUserQuery().openId(openId).uniqueResult();
		Order order = new Order();
		if (isLock) {
			order = new OrderQuery().weChatUser(weChatUser).orderNumber(orderNumber).lockMode(LockMode.UPGRADE)
					.uniqueResult();
		} else {
			order = new OrderQuery().weChatUser(weChatUser).orderNumber(orderNumber).uniqueResult();
		}
		return order;
	}

	public GroupOrder getGroup(Long groupId, Boolean isLock) {
		GroupOrder groupOrder = new GroupOrder();
		if (isLock) {
			groupOrder = new GroupOrderQuery().id(groupId).lockMode(LockMode.UPGRADE).uniqueResult();
		} else {
			groupOrder = new GroupOrderQuery().id(groupId).uniqueResult();
		}
		return groupOrder;
	}

	public ApplyWithdrawMoney getApplyWithdrawMoney(String applyNumber) {
		ApplyWithdrawMoney applyWithdrawMoney = new ApplyWithdrawMoneyQuery().applyNumber(applyNumber).lockMode(LockMode.UPGRADE).uniqueResult();
		return applyWithdrawMoney;
	}
	public void virtualAccountAction(WeChatUser weChatUser, BillTypeEnum billTypeEnum, Order order,
			BigDecimal total_fee) {
		AllBill allBill = new AllBillQuery().orderNumber(order.getOrderNumber()).billType(billTypeEnum)
				.lockMode(LockMode.UPGRADE).uniqueResult();
		if (allBill == null) {
			allBill = new AllBill();
		}
		allBill.setBillType(billTypeEnum);
		allBill.setOrderNumber(order.getOrderNumber());
		//判断当前团单的状态
		GroupOrder groupOrder = getGroup(order.getGroupOrder().getId(),true);
		if(groupOrder.getGroupOrderState().getStates() == GroupOrderState.States.WCT) {
			allBill.setFlag(CashTypeEnum.QX);
		}else {
			allBill.setFlag(CashTypeEnum.DTX);
		}
		allBill.setWeChatUser(weChatUser);
		allBill.setTotal_fee(total_fee);
		allBill.setGroupId(order.getGroupOrder().getId().toString());
		allBill.saveOrUpdate();
		VirtualAccount virtualAccount = createVirtualAccount(weChatUser.getOpenId());
		VirtualAccountBill vab = new VirtualAccountBill();
		BigDecimal grossIncome = virtualAccount.getGrossIncome();
		BigDecimal noWithdrawn = virtualAccount.getNoWithdrawn();
		if (billTypeEnum == BillTypeEnum.SR) {
			virtualAccount.setGrossIncome(grossIncome.add(total_fee));
			virtualAccount.setNoWithdrawn(noWithdrawn.add(total_fee));
			vab.setBillType(BillType.SR);
		} else {
			virtualAccount.setGrossIncome(grossIncome.subtract(total_fee));
			virtualAccount.setNoWithdrawn(noWithdrawn.subtract(total_fee));
			vab.setBillType(BillType.TK);
		}
		// 记录虚拟账户流水
		vab.setOpenId(weChatUser.getOpenId());
		vab.setAmountMoney(total_fee);
		vab.setBeforeAlreadyAvailable(virtualAccount.getAlreadyAvailable());
		vab.setBeforeNoWithdrawn(virtualAccount.getNoWithdrawn());
		vab.setBeforeWithdrawableCash(virtualAccount.getWithdrawableCash());
		vab.setBeforeGrossIncome(virtualAccount.getGrossIncome());
		vab.setOrderNumber(order.getOrderNumber());
		vab.saveOrUpdate();
		virtualAccount.saveOrUpdate();
	}

	public String newSign(String openId, BigDecimal totalFee, String applyNumber , String iv , String iv2) {
		return DigestUtils.md5Hex(openId + totalFee.toString() + applyNumber + iv + iv2);
	}
	/** 创建用户的虚拟账户 */
	public VirtualAccount createVirtualAccount(String openId) {
		VirtualAccount virtualAccount = new VirtualAccountQuery().openId(openId).lockMode(LockMode.UPGRADE)
				.uniqueResult();
		if (virtualAccount == null) {
			virtualAccount = new VirtualAccount();
			virtualAccount.setOpenId(openId);
			virtualAccount.setAlreadyAvailable(new BigDecimal("0.00"));
			virtualAccount.setGrossIncome(new BigDecimal("0.00"));
			virtualAccount.setNoWithdrawn(new BigDecimal("0.00"));
			virtualAccount.setWithdrawableCash(new BigDecimal("0.00"));
			virtualAccount.saveOrUpdate();
		}
		return virtualAccount;
	}

	/*
	 * 获取团单目前的收入
	 */
	public BigDecimal getGroupPrice(WeChatUser weChatUser, Long groupId) {
		BigDecimal bigDecimal = new BigDecimal("0");
		if (weChatUser != null) {
			GroupOrder groupOrder = new GroupOrderQuery().weChatUser(weChatUser).id(groupId).uniqueResult();
			if (groupOrder != null) {
				Set<Order> orders = groupOrder.getOrders();
				for (Order order : orders) {
					List<AllBill> allBills = new AllBillQuery().orderNumber(order.getOrderNumber()).list();
					for (AllBill allBill : allBills) {
						if (allBill != null && allBill.getBillType() == BillTypeEnum.SR) {
							bigDecimal = bigDecimal.add(order.getFinalPayment());
						} else if (allBill != null && allBill.getBillType() == BillTypeEnum.TZ) {
							bigDecimal = bigDecimal.subtract(order.getFinalPayment());
						}
					}
				}
			}
		}
		return bigDecimal;
	}
	/*
	 * 公司微信账户直接提现到团长微信零钱
	 * 具有以下限制
	 * 1、同一用户当天不能提现超过1次
	 * 2、同一收款账户当天不能超过2000元
	 * 3、单笔提现金额为0.99~2000元 人民币
	 * 4、商户当天不能超过1万
	 */
	public List<String> checkCashwithdrawal(String openId , BigDecimal totalFee) throws Exception {
		List<String> resMap = new ArrayList<String>();
		String totalFeesString = "2000";
		String mchDayString = "10000";
		String upperCash = "2000";
		String lowerCash = "0.99";
		int cashwithdrawalCount = 1;
		BigDecimal totalFeebBigDecimal = new BigDecimal(totalFeesString);
		BigDecimal mchDaybiBigDecimal = new BigDecimal(mchDayString);
		BigDecimal mchBigDecimal = new BigDecimal("0");
		BigDecimal upperCashDecimal = new BigDecimal(upperCash);
		BigDecimal lowerDecimal = new BigDecimal(lowerCash);
		//先判断团长今天提了多少次
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String s = sdf.format(new Date());
		Date date =  sdf.parse(s);
		List<ApplyWithdrawMoney> applyWithdrawMoney = new ApplyWithdrawMoneyQuery().createTimeGe(date).openId(openId).readOnly().list();
		if(!applyWithdrawMoney.isEmpty()) {
			if(applyWithdrawMoney.size() >= cashwithdrawalCount) {
				resMap.add("超出当天提现次数，请明天再试");
				return resMap;
			}
		}
		//当次提现大于2000
		if(totalFee.compareTo(totalFeebBigDecimal) > 0 ){
			resMap.add("提现额度不能大于"+totalFeesString);
			return resMap;
		}
		//超出范围
		if(totalFee.compareTo(lowerDecimal) >= 0 && totalFee.compareTo(upperCashDecimal) <= 0)	{
			
		}else {
			resMap.add("提现额度超出范围，必须大于"+lowerCash+"元，小于"+upperCash+"元");
			return resMap;
		}
		//公司账户是否超出
		applyWithdrawMoney = new ApplyWithdrawMoneyQuery().createTimeGe(date).readOnly().list();
		if(applyWithdrawMoney.isEmpty()) {
		}else {
			for (ApplyWithdrawMoney awm : applyWithdrawMoney) {
				mchBigDecimal = awm.getActualAmount().add(mchBigDecimal);
			}
			if(mchBigDecimal.add(totalFee).compareTo(mchDaybiBigDecimal) < 0) {
			
			}else {
				resMap.add("超出当天提现总额，请明天再试");
				return resMap;
			}
		}
		return resMap;
	}
}
