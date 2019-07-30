package cn.sini.cgb.common.util;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.LockMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.sini.cgb.api.cgb.entity.group.GroupOrder;
import cn.sini.cgb.api.cgb.entity.group.Order;
import cn.sini.cgb.api.cgb.entity.group.WeChatUser;
import cn.sini.cgb.api.cgb.entity.group.GroupOrder.GroupType;
import cn.sini.cgb.api.cgb.entity.group.Order.PayState;
import cn.sini.cgb.api.cgb.entity.integral.IntegralAccount;
import cn.sini.cgb.api.cgb.entity.integral.IntegralAccountBill;
import cn.sini.cgb.api.cgb.entity.integral.IntegralAccountBill.IntegralSourceTypeEnum;
import cn.sini.cgb.api.cgb.entity.integral.IntegralAccountBill.IntegralTypeEnum;
import cn.sini.cgb.api.cgb.query.group.OrderQuery;
import cn.sini.cgb.api.cgb.query.group.WeChatUserQuery;
import cn.sini.cgb.api.cgb.query.integral.IntegralAccountBillQuery;
import cn.sini.cgb.api.cgb.query.integral.IntegralAccountQuery;
import cn.sini.cgb.common.wxpay.Notify;

/*
 * 积分工具类
 */
public class IntegralUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(IntegralUtil.class);
	/** 用户的积分账户,不存在则创建 */
	public IntegralAccount getIntegralAccount(String openId) {
		IntegralAccount integralAccount = new IntegralAccountQuery().openId(openId).lockMode(LockMode.UPGRADE)
				.uniqueResult();
		if (integralAccount == null) {
			integralAccount = new IntegralAccount();
			integralAccount.setCurrentIntegral(0L);
			integralAccount.setOpenId(openId);
			integralAccount.saveOrUpdate();
		}
		return integralAccount;
	}

	/*
	 * 操作指定用户的积分
	 */
	public String operIntegral(GroupOrder groupOrder , String openId,  Order order ,IntegralSourceTypeEnum iste , IntegralTypeEnum ite ,Long groupCommodityId ) {
		 
        Long integralAmount =  groupOrder.getAddIntegral() == null ? 0L : groupOrder.getAddIntegral();
        
        String originOpenId = order.getOriginOpenId();
        String originOrderNumber = order.getOriginOrderNumber();
        Long groupId = groupOrder.getId();

        //如果分享人没有订单号则跳过积分加分逻辑
        if(StringUtils.isEmpty(originOrderNumber) || StringUtils.isEmpty(originOpenId) ) {
        	return "";
        }
        
		//判断是企业团
		if(order.getGroupOrder().getGroupType() != GroupType.QYT) {
			return "";
		}

		String orderNumber = order.getOrderNumber();
		//退款操作
		if(iste == IntegralSourceTypeEnum.TK && ite == IntegralTypeEnum.JS) {
			String integralNumber = order.getIntegralNumber();
			//退款沒有找到增加積分的流水
			if(StringUtils.isEmpty(integralNumber)) {
				return "";
			}
			IntegralAccountBill integralAccountBill = new IntegralAccountBillQuery().openId(originOpenId).integralNumber(integralNumber).uniqueResult();
			integralAmount = integralAccountBill.getConsumptionIntegral();
		}
		//分享增加积分
		else {
			
			WeChatUser originWeChatUser = new WeChatUserQuery().openId(originOpenId).uniqueResult();
	        Order originOrder = new OrderQuery().orderNumber(originOrderNumber).weChatUser(originWeChatUser).uniqueResult();
	        //判断分享人是否已支付
	        if(originOrder == null || originOrder.getPayState() != PayState.YZF) {
	        	return "";
	        }
	        //判断是否自己
	    	if(originOpenId.equals(openId)) {
	    		return "";
	    	}
	    	
		}
		
		//判断是否已经 操作过积分
    	IntegralAccountBill integralAccountBill = new IntegralAccountBillQuery().orderNumber(orderNumber).integralSourceType(iste).integralType(ite).lockMode(LockMode.UPGRADE).firstResult();
		if(integralAccountBill!=null) {
			return "";
		}
		
		Long integralAmountBill = 0L;
		Long beforeIntegralAmountBill = 0L;
		IntegralAccount integralAccount = this.getIntegralAccount(originOpenId);
		if(ite == IntegralTypeEnum.JS || ite == IntegralTypeEnum.XF ) {
			integralAmount = integralAmount * -1;
		}
		beforeIntegralAmountBill = integralAccount.getCurrentIntegral();
		integralAmountBill = integralAccount.getCurrentIntegral() + integralAmount;
		integralAccount.setOpenId(originOpenId);
		integralAccount.setCurrentIntegral(integralAmountBill);
		integralAccount.saveOrUpdate();
		return this.operIntegralBill(beforeIntegralAmountBill , originOpenId , openId , orderNumber , groupId ,Math.abs(integralAmount) , iste , ite ,groupCommodityId);
	}
	
	public String operIntegral( Long upgradeIntegral, String originOpenId, String openId, String orderNumber ,Long groupId, IntegralSourceTypeEnum iste , IntegralTypeEnum ite ,Long groupCommodityId ) {
		
		Long integralAmount = upgradeIntegral;
		Long integralAmountBill = 0L;
		Long beforeIntegralAmountBill = 0L;
		IntegralAccount integralAccount = this.getIntegralAccount(originOpenId);
		if(ite == IntegralTypeEnum.JS || ite == IntegralTypeEnum.XF ) {
			integralAmount = integralAmount * -1;
		}
		beforeIntegralAmountBill = integralAccount.getCurrentIntegral();
		integralAmountBill = integralAccount.getCurrentIntegral() + integralAmount;
		integralAccount.setOpenId(originOpenId);
		integralAccount.setCurrentIntegral(integralAmountBill);
		integralAccount.saveOrUpdate();
		return this.operIntegralBill(beforeIntegralAmountBill , originOpenId , openId , orderNumber , groupId ,Math.abs(integralAmount) , iste , ite ,groupCommodityId);
	}
	/*
	 * 积分增加日志
	 */
	private String operIntegralBill(Long currentIntegral , String openId, String afterOpenId, String orderNumber, Long groupId , Long integralAmount ,IntegralSourceTypeEnum iste , IntegralTypeEnum ite ,Long groupCommodityId) {
		IntegralAccountBill integralAccountBill = new IntegralAccountBill(); 
		integralAccountBill.setConsumptionIntegral(integralAmount);
		integralAccountBill.setGroupId(groupId);
		integralAccountBill.setOpenId(openId);
		integralAccountBill.setAfterOpenId(afterOpenId);
		integralAccountBill.setIntegralSourceTypeEnum(iste);
		integralAccountBill.setIntegralType(ite);
		String integralNumber = "JF" + DateTimeUtils.format(new Date(), "yyyyMMddHHmmsss") + (int) ((Math.random() * 9 + 1) * 1000);
		integralAccountBill.setIntegralNumber(integralNumber);
		//如果是新增积分，要记录对应的流水到订单表，用于退款时减掉
		if(ite == IntegralTypeEnum.ZJ) {
			Order order = new OrderQuery().orderNumber(orderNumber).originOpenId(openId).uniqueResult();
			order.setIntegralNumber(integralNumber);
			order.saveOrUpdate();
		}
		if(ite == IntegralTypeEnum.XF) {
			integralAccountBill.setGroupCommodityId(groupCommodityId); 
		}
		integralAccountBill.setOrderNumber(orderNumber);
		//设置积分有效期
		String stringvalidDate = Environment.getProperty("integral.validDate");
		Date date = new Date();
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);
	    calendar.add(calendar.DATE, Integer.parseInt(stringvalidDate));
	    date=calendar.getTime();
		integralAccountBill.setValidDate(date);
		integralAccountBill.setBeforeIntegral(currentIntegral);
		integralAccountBill.saveOrUpdate();
		return integralNumber;
	}
	
	//public OrderGoods get
}
