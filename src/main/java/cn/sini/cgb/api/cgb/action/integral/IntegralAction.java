package cn.sini.cgb.api.cgb.action.integral;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.LockMode;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.sini.cgb.admin.annex.entity.Annex;
import cn.sini.cgb.api.cgb.entity.group.GroupCommodity;
import cn.sini.cgb.api.cgb.entity.group.GroupOrder;
import cn.sini.cgb.api.cgb.entity.group.GroupOrder.GroupType;
import cn.sini.cgb.api.cgb.entity.group.GroupOrderState;
import cn.sini.cgb.api.cgb.entity.group.Order;
import cn.sini.cgb.api.cgb.entity.group.Order.PayState;
import cn.sini.cgb.api.cgb.entity.group.OrderState.OrderStates;
import cn.sini.cgb.api.cgb.entity.group.OrderGoods;
import cn.sini.cgb.api.cgb.entity.group.OrderState;
import cn.sini.cgb.api.cgb.entity.group.WeChatUser;
import cn.sini.cgb.api.cgb.entity.group.GroupOrderState.States;
import cn.sini.cgb.api.cgb.entity.integral.IntegralAccount;
import cn.sini.cgb.api.cgb.entity.integral.IntegralAccountBill;
import cn.sini.cgb.api.cgb.entity.integral.IntegralAccountBill.IntegralSourceTypeEnum;
import cn.sini.cgb.api.cgb.entity.integral.IntegralAccountBill.IntegralTypeEnum;
import cn.sini.cgb.api.cgb.entity.integral.UpgradeCommodities;
import cn.sini.cgb.api.cgb.entity.verification.VerificationSheet;
import cn.sini.cgb.api.cgb.entity.verification.VerificationSheet.VerificationStatus;
import cn.sini.cgb.api.cgb.query.group.GroupCommodityQuery;
import cn.sini.cgb.api.cgb.query.group.GroupOrderQuery;
import cn.sini.cgb.api.cgb.query.group.GroupOrderStateQuery;
import cn.sini.cgb.api.cgb.query.group.OrderGoodsQuery;
import cn.sini.cgb.api.cgb.query.group.OrderQuery;
import cn.sini.cgb.api.cgb.query.group.OrderStateQuery;
import cn.sini.cgb.api.cgb.query.group.WeChatUserQuery;
import cn.sini.cgb.api.cgb.query.integral.IntegralAccountBillQuery;
import cn.sini.cgb.api.cgb.query.verification.VerificationSheetQuery;
import cn.sini.cgb.common.http.HttpRequestWrapper;
import cn.sini.cgb.common.http.HttpResponseWrapper;
import cn.sini.cgb.common.json.JsonUtils;
import cn.sini.cgb.common.util.DateTimeUtils;
import cn.sini.cgb.common.util.Environment;
import cn.sini.cgb.common.util.IntegralUtil;
import cn.sini.cgb.common.util.PayUtil;

/**
 * 积分升级Action
 * 
 * @author 黎嘉权
 */
@Controller
@RequestMapping("/api/integral")
public class IntegralAction {
	@Transactional
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public void list(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
		Annex annex = null;
		String path = Environment.getProperty("host") + Environment.getProperty("skin");
		IntegralUtil integralUtil = new IntegralUtil();
		ObjectNode objectNode = JsonUtils.createObjectNode();
		ArrayNode arrayNode = JsonUtils.createArrayNode();
		String openId = request.getString("openId");
		String orderNumber = request.getString("orderNumber");
		Long orderGoodsId = request.getLong("orderGoodsId");
		if (StringUtils.isBlank(openId)) {
			response.outputJson(-1, "缺少openId参数");
			return;
		}
		if (StringUtils.isEmpty(orderNumber)) {
			response.outputJson(-1, "缺少orderNumber参数");
			return;
		}
		//获取用户的积分
		IntegralAccount integralAccount = integralUtil.getIntegralAccount(openId);
		Long currentIntegral = integralAccount.getCurrentIntegral();
		objectNode.put("currentIntegral", currentIntegral);
		//获取该订单对应的商品
		OrderGoods orderGoods = new OrderGoodsQuery().id(orderGoodsId).readOnly().uniqueResult();
		if(orderGoods == null){	
			response.outputJson(-1, "orderGoodsId参数错误，查询商品失败");
			return;
		}
		orderGoodsId = orderGoods.getId();
		String groupCommodityName = orderGoods.getGroupCommodity().getName();
		String groupCommodityDescription = orderGoods.getGroupCommodity().getDescription();
		String orderGoodsTotal = orderGoods.getTotal().toString();
		if (orderGoods.getGroupCommodity().getAnnexs().size() <= 0) {// 为空则使用默认图
			String defaultPicPath = "/images/group_commoditys_pic.png";
			objectNode.put("annexFilePath", path + defaultPicPath);
		}else {
			annex = orderGoods.getGroupCommodity().getAnnexs().iterator().next();
			objectNode.put("annexFilePath", path + annex.getFilePath());
		}
		objectNode.put("orderGoodsId", orderGoodsId);
		objectNode.put("groupCommodityName", groupCommodityName);
		objectNode.put("orderGoodsTotal", orderGoodsTotal);
		objectNode.put("groupCommodityDescription", groupCommodityDescription);
		
		//获取该订单下，能升级的商品列表
		WeChatUser weChatUser = new WeChatUserQuery().openId(openId).readOnly().uniqueResult();
		Order order = new OrderQuery().orderNumber(orderNumber).weChatUser(weChatUser).uniqueResult();
		Set<GroupCommodity> groupCommoditys = order.getGroupOrder().getGroupCommoditys();
		for (GroupCommodity groupCommodity : groupCommoditys) {
			if(groupCommodity.getIsUpgrade() != null && groupCommodity.getIsUpgrade()) {
				//用户订单对应可升级的商品
				ObjectNode commodityObjectNode = JsonUtils.createObjectNode();
				Long groupCommodityId = groupCommodity.getId();
				String commodityOriginalPrice = groupCommodity.getOriginalPrice().toString();
				String commodityPrice = groupCommodity.getPrice().toString();
				groupCommodityName = groupCommodity.getName();
				groupCommodityDescription = groupCommodity.getDescription();
				
				if (groupCommodity.getAnnexs().size() <= 0) {// 为空则使用默认图
					String defaultPicPath = "/images/group_commoditys_pic.png";
					commodityObjectNode.put("annexFilePath", path + defaultPicPath);
				}else {
					annex = groupCommodity.getAnnexs().iterator().next();
					commodityObjectNode.put("annexFilePath", path + annex.getFilePath());
				}
				long integral = groupCommodity.getIntegral();
				//判断是否组合商品
				Boolean commodityIsPack = groupCommodity.getGroupCommodityBasic().getIsPack();
				if(commodityIsPack != null && commodityIsPack) {
					continue;
				}

				//判断商品是否已核销过
				VerificationSheet verificationSheet = new VerificationSheetQuery().status(VerificationStatus.CONSUMED).orderNumber(orderNumber).orderGoodsNumber(orderGoodsId.toString()).openId(openId).readOnly().firstResult();
				if(verificationSheet != null) {
					response.outputJson(-1, "抱歉，商品已核销过，不能进行升级");
					return;
				}
				commodityObjectNode.put("groupCommodityId", groupCommodityId);
				commodityObjectNode.put("commodityOriginalPrice", commodityOriginalPrice);
				commodityObjectNode.put("commodityPrice", commodityPrice);
				commodityObjectNode.put("groupCommodityName", groupCommodityName);
				commodityObjectNode.put("groupCommodityDescription", groupCommodityDescription);
				commodityObjectNode.put("integral", integral);
				arrayNode.add(commodityObjectNode);
			}
		}
		objectNode.set("upgradeCommodityList", arrayNode);
		response.outputJson(0, objectNode);
	}
	
	@Transactional
	@RequestMapping(value = "/upgrade", method = RequestMethod.POST)
	public void upgrade(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
		PayUtil payUtil = new PayUtil();
		IntegralUtil integralUtil = new IntegralUtil();
		String openId = request.getString("openId");
		String orderNumber = request.getString("orderNumber");
		Long orderGoodsId = request.getLong("orderGoodsId");
		Long groupCommodityId = request.getLong("groupCommodityId");
		//获取该订
		Order order = payUtil.getOrder(openId, orderNumber, true);
		// 设置X锁，不允许发货和取消操作
		GroupOrder groupOrder = payUtil.getGroup(order.getGroupOrder().getId(), true);
		long currentTime = System.currentTimeMillis() + 5 * 60 * 1000;
		Date date = new Date(currentTime);
		// 判断团单状态是否为：进行中，且结束时间5分钟前不能升级
		if (groupOrder.getGroupOrderState().getStates() != States.JXZ && date.getTime() > groupOrder.getEndTime().getTime()) {
			response.outputJson(-1, "抱歉，拼团状态不正确或即将结束，请刷新再试");
			return;
		}
		if(order.getPayState() != PayState.YZF) {
			response.outputJson(-1, "抱歉，系统未正确获取你的支付结果，请稍后再试");
			return;
		}
		//判断积分是否足够
		GroupCommodity groupCommodity = new GroupCommodityQuery().id(groupCommodityId).lockMode(LockMode.UPGRADE).uniqueResult();
		IntegralAccount integralAccount = integralUtil.getIntegralAccount(openId);
		Long currentIntegral = integralAccount.getCurrentIntegral();
		Long integral = Long.valueOf(groupCommodity.getIntegral());
		if(currentIntegral - groupCommodity.getIntegral() < 0) {
			response.outputJson(-1, "抱歉，您的积分不足够兑换【"+groupCommodity.getName()+"】，继续努力哦！");
			return;
		}
		//判断商品是否已核销过
		VerificationSheet verificationSheet = new VerificationSheetQuery().status(VerificationStatus.CONSUMED).orderNumber(orderNumber).orderGoodsNumber(orderGoodsId.toString()).openId(openId).readOnly().firstResult();
		if(verificationSheet != null) {
			response.outputJson(-1, "抱歉，商品已核销过，不能进行升级");
			return;
		}
		//判断库存是否足够
		//获取该订单对应的商品的购入数量
		OrderGoods orderGoods = new OrderGoodsQuery().id(orderGoodsId).uniqueResult();
		if(orderGoods == null){	
			response.outputJson(-1, "抱歉，orderGoodsId参数错误，查询商品失败");
			return;
		}
		if(orderGoods.isIntegralUpgrade() != null && orderGoods.isIntegralUpgrade()) {
			response.outputJson(-1, "抱歉，该商品已升级");
			return;
		}
		int orderGoodsAmount = orderGoods.getAmount();
		int remnantInventory = groupCommodity.getRemnantInventory();
		if(remnantInventory - orderGoodsAmount < 0) {
			response.outputJson(-1, "抱歉，你选择兑换的【"+groupCommodity.getName()+"】库存不足！");
			return;
		}
		//开始升级
		groupCommodity.setRemnantInventory(remnantInventory - orderGoodsAmount);		//减去库存
		GroupCommodity orderGroupCommodity = orderGoods.getGroupCommodity();		
		orderGroupCommodity.setRemnantInventory(orderGroupCommodity.getRemnantInventory() + orderGoodsAmount);	//恢复库存
		orderGroupCommodity.saveOrUpdate();
		groupCommodity.saveOrUpdate();
		orderGoods.setGroupCommodity(groupCommodity);	//升级商品
		orderGoods.setIntegralUpgrade(true);
		orderGoods.saveOrUpdate();
		String integralNumber = integralUtil.operIntegral(integral, openId, "", orderNumber, groupOrder.getId(), IntegralSourceTypeEnum.DH , IntegralTypeEnum.XF,  groupCommodityId);
		//插入升级关系表
		UpgradeCommodities upgradeCommodities = new UpgradeCommodities();
		upgradeCommodities.setGroupId(groupOrder.getId());
		upgradeCommodities.setIntegralNumber(integralNumber);
		upgradeCommodities.setNewCommodityId(groupCommodityId);
		upgradeCommodities.setOldCommodityId(orderGroupCommodity.getId());
		upgradeCommodities.setOpenId(openId);
		upgradeCommodities.setOrderNumber(orderNumber);
		upgradeCommodities.saveOrUpdate();
		response.outputJson(0, "恭喜你，成功升级"+groupCommodity.getName()+"】");
	}
	
	@RequestMapping(value = "/home", method = RequestMethod.POST)
	public void home(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
		String openId = request.getString("openId");
		//openId="oxGbN4mFD0DuubJ_yOGkNkcmuRNo";
		ObjectNode objectNode = JsonUtils.createObjectNode();
		ArrayNode arrayNode = JsonUtils.createArrayNode();
		IntegralUtil integralUtil = new IntegralUtil();
		Long currentIntegral = integralUtil.getIntegralAccount(openId).getCurrentIntegral();
		objectNode.put("currentIntegral", currentIntegral);
		WeChatUser weChatUser = new WeChatUserQuery().openId(openId).uniqueResult();
		OrderState orderState = new OrderStateQuery().orderStates(OrderStates.DSH).uniqueResult();
		List<Order> orders = new OrderQuery().weChatUser(weChatUser).orderState(orderState).list();
		for (Order order : orders) {
			Boolean isUpgrade = order.getGroupOrder().getIsUpgrade();
			if(isUpgrade != null && isUpgrade) {
				Set<OrderGoods> orderGoods = order.getOrderGoods();
				for (OrderGoods OrderGood : orderGoods) {  
					//判断是否组合商品
					Boolean commodityIsPack = OrderGood.getGroupCommodity().getGroupCommodityBasic().getIsPack();
					if(commodityIsPack != null && commodityIsPack) {
						continue;
					}
					//判断商品是否隱藏
					Boolean commodityIsHidden = OrderGood.getGroupCommodity().getIsHidden();
					if(commodityIsHidden != null && commodityIsHidden) {
						continue;
					}
					//判断是否已升级过
					if(OrderGood.isIntegralUpgrade() !=null && OrderGood.isIntegralUpgrade()) {
						continue;
					}
					//判断商品是否已核销过
					VerificationSheet verificationSheet = new VerificationSheetQuery().status(VerificationStatus.CONSUMED).orderNumber(order.getOrderNumber()).orderGoodsNumber(OrderGood.getId().toString()).openId(openId).readOnly().firstResult();
					if(verificationSheet != null) {
						continue;
					}
					ObjectNode orderObjectNode = JsonUtils.createObjectNode();
					String theme = order.getGroupOrder().getTheme();
					String groupOrderId = order.getGroupOrder().getId().toString();
					String createTime = DateTimeUtils.format(order.getCreateTime(), "yyyy-MM-dd HH:mm:ss");
					String orderNumber = order.getOrderNumber();
					orderObjectNode.put("theme", theme);
					orderObjectNode.put("groupOrderId", groupOrderId);
					orderObjectNode.put("createTime", createTime);
					orderObjectNode.put("orderNumber", orderNumber);
					String groupCommodityName = OrderGood.getGroupCommodity().getName();
					Long groupCommodityId = OrderGood.getGroupCommodity().getId();
					Long orderGoodsId = OrderGood.getId();
					orderObjectNode.put("orderGoodsId", orderGoodsId);
					orderObjectNode.put("groupCommodityId", groupCommodityId);
					orderObjectNode.put("groupCommodityName", groupCommodityName);
					arrayNode.add(orderObjectNode);
				}  
			}
		}
		objectNode.set("upgradeCommodityList", arrayNode);
		response.outputJson(0, objectNode);
	}
	
	@RequestMapping(value = "/integrallist", method = RequestMethod.POST)
	public void integralList(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
		String openId = request.getString("openId");
		ObjectNode objectNode = JsonUtils.createObjectNode();
		ArrayNode arrayNode = JsonUtils.createArrayNode();
		List<IntegralAccountBill> integralAccountBills = new IntegralAccountBillQuery().openId(openId).orderBy("createTime", false).readOnly().list();
		for (IntegralAccountBill integralAccountBill : integralAccountBills) {
			ObjectNode integralobjectNode = JsonUtils.createObjectNode();
			String afterOpenId = integralAccountBill.getAfterOpenId();
			if(StringUtils.isNotEmpty(afterOpenId)) {
				WeChatUser weChatUser = new WeChatUserQuery().openId(afterOpenId).uniqueResult();
				integralobjectNode.put("name", weChatUser.getName());
				integralobjectNode.put("headImgUrl", weChatUser.getHeadImgUrl());
			}else {
				integralobjectNode.put("name", "");
				integralobjectNode.put("headImgUrl", "");
			}
			Long consumptionIntegral = integralAccountBill.getConsumptionIntegral();
			Long groupId = integralAccountBill.getGroupId();
			String orderNumber = integralAccountBill.getOrderNumber();
			GroupOrder groupOrder = new GroupOrderQuery().id(groupId).readOnly().uniqueResult();
			String theme = groupOrder.getTheme();
			Order order = new OrderQuery().orderNumber(orderNumber).readOnly().uniqueResult();
			String orderCreateTime = DateTimeUtils.format(order.getCreateTime(), "yyyy-MM-dd HH:mm:ss");
			String createTime = DateTimeUtils.format(integralAccountBill.getCreateTime(), "yyyy-MM-dd HH:mm:ss");
			String integralSourceType =integralAccountBill.getIntegralSourceTypeEnum().desc;
			String integralType =integralAccountBill.getIntegralType().desc;
			integralobjectNode.put("theme", theme);
			integralobjectNode.put("orderCreateTime", orderCreateTime);
			integralobjectNode.put("consumptionIntegral", consumptionIntegral);
			integralobjectNode.put("createTime", createTime);
			integralobjectNode.put("integralSourceType", integralSourceType);
			integralobjectNode.put("integralType", integralType);
			arrayNode.add(integralobjectNode);
		}
		objectNode.set("integrallist", arrayNode);
		response.outputJson(0, objectNode);
	}
	
	@RequestMapping(value = "/check", method = RequestMethod.POST)
	public void check(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
		IntegralUtil integralUtil = new IntegralUtil();
		ObjectNode objectNode = JsonUtils.createObjectNode();
		String openId = request.getString("openId");
		String orderNumber = request.getString("orderNumber");
		//获取用户的积分
		IntegralAccount integralAccount = integralUtil.getIntegralAccount(openId);
		Long currentIntegral = integralAccount.getCurrentIntegral();
		//查询所有能升级的团
		WeChatUser weChatUser = new WeChatUserQuery().openId(openId).readOnly().uniqueResult();
		OrderState orderState = new OrderStateQuery().orderStates(OrderStates.DSH).readOnly().uniqueResult();
		List<Order> orders = new OrderQuery().weChatUser(weChatUser).orderNumber(orderNumber).orderState(orderState).readOnly().list();
		for (Order order : orders) {
			Boolean isUpgrade = order.getGroupOrder().getIsUpgrade();
			//判断团是否支持升级
			if(isUpgrade != null && isUpgrade) {
				Set<OrderGoods> OrderGoods = order.getOrderGoods();
				for (OrderGoods OrderGood : OrderGoods) {  
					//判断商品是否已经升级过
					Boolean isIntegralUpgrade = OrderGood.isIntegralUpgrade(); 
					if(isIntegralUpgrade == null || !isIntegralUpgrade) {
						//获取该订单对应团单下可以升级的商品积分
						Set<GroupCommodity> groupCommoditys = order.getGroupOrder().getGroupCommoditys();
						for (GroupCommodity groupCommodity : groupCommoditys) {  
							if(groupCommodity.getIntegral() !=null && currentIntegral >= groupCommodity.getIntegral()) {
								//判断是否组合商品
								Boolean commodityIsPack = groupCommodity.getGroupCommodityBasic().getIsPack();
								if(commodityIsPack != null && commodityIsPack) {
									continue;
								}
								
								//判断商品是否已核销过
								VerificationSheet verificationSheet = new VerificationSheetQuery().status(VerificationStatus.CONSUMED).orderNumber(order.getOrderNumber()).orderGoodsNumber(OrderGood.getId().toString()).openId(openId).readOnly().firstResult();
								if(verificationSheet == null) {
									objectNode.put("check", "1");
									response.outputJson(0, objectNode);
									return;
								}
							}
						}
					}
				}  
			}
		}
		objectNode.put("check", "0");
		response.outputJson(0, objectNode);
	}
}
