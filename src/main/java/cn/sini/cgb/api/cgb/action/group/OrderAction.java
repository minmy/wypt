package cn.sini.cgb.api.cgb.action.group;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.coobird.thumbnailator.Thumbnails;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.LockMode;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.sini.cgb.admin.annex.entity.Annex;
import cn.sini.cgb.admin.annex.entity.Annex.AnnexType;
import cn.sini.cgb.admin.annex.query.AnnexQuery;
import cn.sini.cgb.admin.annex.util.AnnexUtils;
import cn.sini.cgb.api.cgb.entity.group.Community;
import cn.sini.cgb.api.cgb.entity.group.GroupCommodity;
import cn.sini.cgb.api.cgb.entity.group.GroupCommodityBasic;
import cn.sini.cgb.api.cgb.entity.group.GroupOrder;
import cn.sini.cgb.api.cgb.entity.group.GroupOrder.GroupType;
import cn.sini.cgb.api.cgb.entity.group.GroupOrderState.States;
import cn.sini.cgb.api.cgb.entity.group.Order;
import cn.sini.cgb.api.cgb.entity.group.Order.PayState;
import cn.sini.cgb.api.cgb.entity.group.OrderGoods;
import cn.sini.cgb.api.cgb.entity.group.OrderState;
import cn.sini.cgb.api.cgb.entity.group.OrderState.OrderStates;
import cn.sini.cgb.api.cgb.entity.group.WeChatUser;
import cn.sini.cgb.api.cgb.query.group.CommunityQuery;
import cn.sini.cgb.api.cgb.query.group.GroupCommodityQuery;
import cn.sini.cgb.api.cgb.query.group.GroupOrderQuery;
import cn.sini.cgb.api.cgb.query.group.OrderQuery;
import cn.sini.cgb.api.cgb.query.group.OrderStateQuery;
import cn.sini.cgb.api.cgb.query.group.WeChatUserQuery;
import cn.sini.cgb.common.http.HttpRequestWrapper;
import cn.sini.cgb.common.http.HttpResponseWrapper;
import cn.sini.cgb.common.json.JsonUtils;
import cn.sini.cgb.common.json.JsonWrapper;
import cn.sini.cgb.common.query.Page;
import cn.sini.cgb.common.util.DateTimeUtils;
import cn.sini.cgb.common.util.Environment;
import cn.sini.cgb.common.util.GenerateImageUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 我参加的——拼团订单action
 * 
 * @author gaowei
 */
@Controller
@RequestMapping("/api/order")
public class OrderAction {
	private static final String UP = "up";
	private static final String DROP = "drop";

	/** 订单列表 */
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public void list(HttpRequestWrapper request, HttpResponseWrapper response) {
		String openId = request.getString("openId");
		OrderStates orderState = request.getEnum("orderState", OrderState.OrderStates.class);

		String createTimeOrder = request.getString("createTimeOrder");// 订单下单时间排序
		String totalOrder = request.getString("totalOrder");// 订单总金额排序
		String community = request.getString("community"); // 按所属小区筛选
		Integer beforeDate = request.getInteger("beforeDate"); // 按1,3,5,7,30天内的发布日期拼团筛选

		if (StringUtils.isEmpty(openId)) {
			response.outputJson(-1, "缺少openId参数");
			return;
		}
		WeChatUser weChatUser = new WeChatUserQuery().openId(openId).readOnly().uniqueResult();
		if (weChatUser == null) {
			response.outputJson(-1, "查询失败，未找到该用户");
			return;
		}

		// 筛选小区
		GroupOrderQuery groupOrderQuery = null;
		String[] arrCommunity = null;
		if (community != null && StringUtils.isNotEmpty(community)) {
			arrCommunity = community.split(",");
			Serializable[] communitys = new Long[arrCommunity.length];
			for (int i = 0; i < arrCommunity.length; i++) {
				communitys[i] = Long.valueOf(arrCommunity[i]);
			}
			groupOrderQuery = new GroupOrderQuery().communityQuery((CommunityQuery) new CommunityQuery().id(communitys));
		}
		OrderQuery orderQuery = new OrderQuery().weChatUser(weChatUser).orderStateQuery(new OrderStateQuery().orderStates(orderState));
		// 下单时间筛选
		if (beforeDate != null) {
			orderQuery.createTimeGe(DateTimeUtils.addDay(new Date(), beforeDate * -1));
		}
		// 订单总金额排序
		if (UP.equals(totalOrder)) {
			orderQuery.orderBy("total", true);
		} else if (DROP.equals(totalOrder)) {
			orderQuery.orderBy("total", false);
		}
		// 下单时间排序，默认倒序
		if (UP.equals(createTimeOrder)) {
			orderQuery.orderBy("createTime", true);
		} else if (DROP.equals(createTimeOrder)) {
			orderQuery.orderBy("createTime", false);
		} else {
			orderQuery.orderBy("createTime", false);
		}
		Page<Order> page = orderQuery.groupOrderQuery(groupOrderQuery).pageHasCount(request.getPageNum(), request.getPageSize());
		ObjectNode objectNode = JsonUtils.createObjectNode();
		List<Order> recordList = page.getRecordList();
		ArrayNode arrayNode = JsonUtils.createArrayNode();
		String path = Environment.getProperty("host") + Environment.getProperty("skin");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm");
		for (Order order : recordList) {
			GroupOrder groupOrder = order.getGroupOrder();
			ObjectNode node = JsonUtils.createObjectNode();
			WeChatUser user = groupOrder.getWeChatUser();
			node.put("orderId", order.getId());
			node.put("createTime", simpleDateFormat.format(order.getCreateTime()));
			node.put("orderNumber", order.getOrderNumber());
			node.put("groupOrderId", groupOrder.getId());
			node.put("waitingTime", groupOrder.getWaitingTime());
			Community comm = groupOrder.getCommunity();
			node.put("communityName", comm == null ? "" : comm.getName());
			node.put("townships", comm == null ? "" : comm.getTownship().getName());
			node.put("communityName", groupOrder.getCommunity().getName());
			// 自提地址
			node.put("selfExtractingAddress", order.getAddress() == null ? groupOrder.getSelfExtractingAddress() == null ? "" : groupOrder.getSelfExtractingAddress() : order.getAddress());
			node.put("selfExtractingTime", groupOrder.getSelfExtractingTime() == null ? "" : simpleDateFormat.format(groupOrder.getSelfExtractingTime()));// 自提时间
			node.put("theme", groupOrder.getTheme());
			node.put("name", user.getName());
			node.put("headImgUrl", user.getHeadImgUrl());
			node.put("endTime", simpleDateFormat.format(groupOrder.getEndTime()));
			node.put("cancelTime", order.getCancelTime() == null ? "" : simpleDateFormat.format(order.getCancelTime()));
			node.put("cancelReason", order.getCancelReason() == null ? "" : order.getCancelReason());// 取消原因
			node.put("groupOrderState", groupOrder.getGroupOrderState().getDesc());// 团单状态
			node.put("groupOrderPhone", groupOrder.getPhone());// 团单电话
			node.put("groupType", groupOrder.getGroupType() == null ? "PTT" : groupOrder.getGroupType().toString());// 团单类型

			if (order.getOrderState().getOrderStates() == OrderStates.DFK && order.getPayState() == PayState.DZF) {
				long thisTime = System.currentTimeMillis();
				long createTime = order.getCreateTime().getTime();
				// 60 * 60 * 1000 = 600000 10分钟毫秒数
				long remainingTime = (createTime + 600000) - thisTime;
				long remainingMin = remainingTime / 1000 / 60 % 60;// 计算分钟
				long remainingSecond = remainingTime / 1000 % 60;// 计算秒
				if (remainingSecond < 10) {
					node.put("remainingTime", remainingTime < 0 ? "0" : remainingMin + ":" + "0" + remainingSecond);
				} else {
					node.put("remainingTime", remainingTime < 0 ? "0" : remainingMin + ":" + remainingSecond);
				}
			}
			ArrayNode groupOrderPicsNodes = JsonUtils.createArrayNode();
			for (Annex annex : groupOrder.getAnnexs()) {
				ObjectNode on = JsonUtils.createObjectNode();
				on.put("annexId", annex.getId());
				on.put("annexFilePath", path + annex.getFilePath());
				groupOrderPicsNodes.add(on);
			}
			if (groupOrderPicsNodes.size() <= 0) {
				String defaultPicPath = "/images/group_order_pic.png";
				groupOrderPicsNodes.add(JsonUtils.createObjectNode().put("annexFilePath", path + defaultPicPath));
			}
			node.set("groupOrderPics", groupOrderPicsNodes);
			node.put("orderStates", order.getOrderState().getOrderStates().getDesc());
			node.put("payState", order.getPayState().getDesc());
			node.put("validOrder", order.getGroupOrder().getValidOrders());
			node.put("commodities", order.getOrderGoods().size());
			node.put("total", order.getTotal());
			Integer orderAnnexNumber = new AnnexQuery().order(order).annexType(AnnexType.ORDER_PICK_PIC).list().size();
			node.put("orderAnnexNumber", orderAnnexNumber);
			arrayNode.add(node);
		}
		// 查询所分布的订单的所有小区
		List<GroupOrder> groupOrderList = new OrderQuery().weChatUser(weChatUser).orderStateQuery(new OrderStateQuery().orderStates(orderState)).groupBy("groupOrder").list();
		List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
		for (GroupOrder groupOrder : groupOrderList) {
			Community comm = groupOrder.getCommunity();
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("communityId", comm.getId().toString());
			map.put("communityName", comm.getName());
			if (!mapList.contains(map)) {
				mapList.add(map);
			}
		}
		objectNode.set("communityArray", JsonUtils.toArrayNode(mapList));
		objectNode.set("recordList", arrayNode);
		objectNode.put("totalPage", page.getTotalPage());
		objectNode.put("totalRecord", page.getTotalRecord());
		objectNode.put("pageNum", page.getPageNum());
		objectNode.put("pageSize", page.getPageSize());
		response.outputJson(0, objectNode);
	}

	/** 下单(我要参团) */
	@SuppressWarnings("deprecation")
	@Transactional
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public void add(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
		String openId = request.getString("openId");
		Long groupOrderId = request.getLong("groupOrderId");// 团单ID
		String groupProducts = request.getString("groupProducts");// 拼团商品信息
		String originOpenId = request.getString("originOpenId");// 分享人OPENID
		String originOrderNumber = request.getString("originOrderNumber");// 分享人订单号
		String shareRandomNumber = request.getString("shareRandomNumber");// 分享随机数

		if (StringUtils.isEmpty(openId)) {
			response.outputJson(-1, "对不起，缺少openId参数");
			return;
		}
		WeChatUser weChatUser = new WeChatUserQuery().openId(openId).readOnly().uniqueResult();
		if (weChatUser == null) {
			response.outputJson(-1, "对不起，未找到该用户");
			return;
		}
		if (groupOrderId == null || groupOrderId == 0L) {
			response.outputJson(-1, "对不起，缺少groupOrderId参数");
			return;
		}
		if (groupProducts == null || groupProducts == "") {
			response.outputJson(-1, "对不起，缺少商品信息");
			return;
		}
		if (originOpenId == null) {
			originOpenId = "";
		}
		if (originOrderNumber == null) {
			originOrderNumber = "";
		}
		GroupOrder groupOrder = new GroupOrderQuery().id(groupOrderId).uniqueResult();
		if (groupOrder == null) {
			response.outputJson(-1, "对不起，该拼团不存在或已下架");
			return;
		}
		if (groupOrder.getGroupOrderState().getStates() != States.JXZ) {
			response.outputJson(-1, "对不起，该拼团已结束");
			return;
		}
		long endTime = groupOrder.getEndTime().getTime();
		long thisTime = System.currentTimeMillis();
		if (thisTime > endTime) {
			response.outputJson(-1, "对不起，拼团时间已结束");
			return;
		}
		if (groupOrder.getUpperlimit() <= groupOrder.getValidOrders()) {
			response.outputJson(-1, "对不起，该拼团人数已达上限");
			return;
		}
		// 是普通团时需验证小区
		if ((groupOrder.getGroupType() == null || groupOrder.getGroupType() == GroupType.PTT) && groupOrder.isSameCommunity(weChatUser) == 0) {
			response.outputJson(-1, "对不起，您所在小区不在当前拼团小区内");
			return;
		}
		int i = 0;
		ArrayNode arrayNode = JsonUtils.toArrayNode(groupProducts);
		if (groupOrder.getGroupType() == null || groupOrder.getGroupType() == GroupType.PTT) {
			for (JsonNode jsonNode : arrayNode) {
				JsonWrapper jw = new JsonWrapper(jsonNode);
				Long commodityId = jw.getLong("commodityId");// 商品ID
				Integer amount = jw.getInteger("amount");// 商品数量
				if (commodityId == null || commodityId == 0L) {
					response.outputJson(-1, "对不起，缺少commodityId参数");
					return;
				}
				GroupCommodity groupCommodity = new GroupCommodityQuery().groupOrder(groupOrder).id(commodityId).uniqueResult();
				if (groupCommodity == null) {
					response.outputJson(-1, "对不起，未找到该商品");
					return;
				}
				if (amount == null) {
					response.outputJson(-1, "对不起，缺少amount参数");
					return;
				} else if (amount > 0 && amount > groupCommodity.getRemnantInventory()) {
					response.outputJson(-1, "对不起，" + groupCommodity.getName() + "商品库存不足");
					return;
				} else if (amount <= 0) {
					++i;
					if (i == arrayNode.size()) {
						response.outputJson(-1, "对不起，请输入商品数量");
						return;
					}
				}
				// 如果不等于0，说明该商品有购买上限限制，否则，没有购买上限限制
				if (groupCommodity.getUpperlimit() != 0) {
					// 查询当前用户购买当前团单的所有订单, 过滤已取消的订单
					List<Order> orderList = new OrderQuery().weChatUser(weChatUser).groupOrder(groupOrder).orderStateQuery(new OrderStateQuery().orderStatesNe(OrderStates.YQX)).list();
					Integer purchaseAmount = 0;// 购买量
					for (Order order : orderList) {
						for (OrderGoods orderGoods : order.getOrderGoods()) {
							GroupCommodity commodity = orderGoods.getGroupCommodity();
							// 如果存在订单已购买了当前下单的相同的商品，则计算总的该商品购买的数量
							if (commodity.getId().equals(commodityId)) {
								purchaseAmount += orderGoods.getAmount();
							}
						}
					}
					if ((amount + purchaseAmount) > groupCommodity.getUpperlimit()) {
						response.outputJson(-1, "对不起，您当前购买的商品已达上限");
						return;
					}
				}
			}
		} else if (groupOrder.getGroupType() == GroupType.QYT) {
			// 是否是组合商品。是，则每个品目下都必须至少买一个商品。不是，则自由购买。
			if (groupOrder.getIsCombination()) {
				for (GroupCommodityBasic groupCommodityBasic : groupOrder.getGroupCommodityBasic()) {
					Set<GroupCommodity> groupCommoditys = groupCommodityBasic.getGroupCommoditys();
					Boolean isContains = false;
					for (JsonNode jsonNode : arrayNode) {
						JsonWrapper jw = new JsonWrapper(jsonNode);
						Long commodityId = jw.getLongMust("commodityId");// 商品ID
						Integer amount = jw.getIntegerMust("amount");// 商品数量
						GroupCommodity groupCommodity = new GroupCommodityQuery().id(commodityId).uniqueResult();
						if (groupCommodity == null) {
							response.outputJson(-1, "对不起，未找到该商品");
							return;
						}
						// 判断商品是否存在当前品目下的商品中
						if (groupCommoditys.contains(groupCommodity)) {
							isContains = true;
						}
						if (isContains) {
							// 判断库存数量
							if (amount == null) {
								response.outputJson(-1, "对不起，缺少amount参数");
								return;
							} else if (amount > 0 && amount > groupCommodity.getRemnantInventory()) {
								response.outputJson(-1, "对不起，商品库存不足");
								return;
							}
							// 判断购买上限
							// 如果不等于0，说明该商品有购买上限限制，否则，没有购买上限限制
							if (groupCommodity.getUpperlimit() != 0) {
								// 查询当前用户购买当前团单的所有订单, 过滤已取消的订单
								List<Order> orderList = new OrderQuery().weChatUser(weChatUser).groupOrder(groupOrder).orderStateQuery(new OrderStateQuery().orderStatesNe(OrderStates.YQX)).list();
								Integer purchaseAmount = 0;// 购买量
								for (Order order : orderList) {
									for (OrderGoods orderGoods : order.getOrderGoods()) {
										GroupCommodity commodity = orderGoods.getGroupCommodity();
										// 如果存在订单已购买了当前下单的相同的商品，则计算总的该商品购买的数量
										if (commodity.getId().equals(commodityId)) {
											purchaseAmount += orderGoods.getAmount();
										}
									}
								}
								if ((amount + purchaseAmount) > groupCommodity.getUpperlimit()) {
									response.outputJson(-1, "对不起，您当前购买的商品已达上限");
									return;
								}
							}
						}
					}
					if (!isContains) {
						response.outputJson(-1, "对不起，该拼团的商品不可单独购买");
						return;
					}
				}
			} else {
				// 不是组合商品
				for (JsonNode jsonNode : arrayNode) {
					JsonWrapper jw = new JsonWrapper(jsonNode);
					Long commodityId = jw.getLongMust("commodityId");// 商品ID
					Integer amount = jw.getIntegerMust("amount");// 商品数量
					GroupCommodity groupCommodity = new GroupCommodityQuery().id(commodityId).uniqueResult();
					if (groupCommodity == null) {
						response.outputJson(-1, "对不起，未找到该商品");
						return;
					}
					// 判断库存数量
					if (amount == null) {
						response.outputJson(-1, "对不起，缺少amount参数");
						return;
					} else if (amount > 0 && amount > groupCommodity.getRemnantInventory()) {
						response.outputJson(-1, "对不起，商品库存不足");
						return;
					}
					// 判断购买上限
					// 如果不等于0，说明该商品有购买上限限制，否则，没有购买上限限制
					if (groupCommodity.getUpperlimit() != 0) {
						// 查询当前用户购买当前团单的所有订单, 过滤已取消的订单
						List<Order> orderList = new OrderQuery().weChatUser(weChatUser).groupOrder(groupOrder).orderStateQuery(new OrderStateQuery().orderStatesNe(OrderStates.YQX)).list();
						Integer purchaseAmount = 0;// 购买量
						for (Order order : orderList) {
							for (OrderGoods orderGoods : order.getOrderGoods()) {
								GroupCommodity commodity = orderGoods.getGroupCommodity();
								// 如果存在订单已购买了当前下单的相同的商品，则计算总的该商品购买的数量
								if (commodity.getId().equals(commodityId)) {
									purchaseAmount += orderGoods.getAmount();
								}
							}
						}
						if ((amount + purchaseAmount) > groupCommodity.getUpperlimit()) {
							response.outputJson(-1, "对不起，您当前购买的商品已达上限");
							return;
						}
					}
				}
			}
		}
		String orderNumber = DateTimeUtils.format(new Date(), "yyyyMMddHHmmsss") + (int) ((Math.random() * 9 + 1) * 1000);
		OrderState orderState = new OrderStateQuery().orderStates(OrderStates.DFK).uniqueResult();
		Order order = new Order();
		order.setWeChatUser(weChatUser);
		order.setOrderNumber(orderNumber);
		order.setOrderState(orderState);
		order.setGroupOrder(groupOrder);
		order.setPayState(PayState.DZF);
		order.setOriginOpenId(originOpenId);
		order.setOriginOrderNumber(originOrderNumber);
		order.setRandomNumber(generateRandomNumber(6));
		order.setShareRandomNumber(shareRandomNumber);
		order.saveOrUpdate();
		BigDecimal total = new BigDecimal("0");
		for (JsonNode jsonNode : arrayNode) {
			JsonWrapper jw = new JsonWrapper(jsonNode);
			Long commodityId = jw.getLong("commodityId");// 商品ID
			Integer amount = jw.getInteger("amount");// 商品数量
			if (amount != null && amount != 0) {
				if (groupOrder.getGroupType() == null || groupOrder.getGroupType() == GroupType.PTT) {
					// 团单商品减去购买数量
					// 锁行
					GroupCommodity groupCommodity = new GroupCommodityQuery().groupOrder(groupOrder).id(commodityId).lockMode(LockMode.UPGRADE).uniqueResult();
					Integer remnantInventory = groupCommodity.getRemnantInventory() - amount;// 计算库存
					if (remnantInventory < 0) {
						response.outputJson(-1, "对不起，商品库存不足");
						throw new RuntimeException();
					}
					groupCommodity.setRemnantInventory(remnantInventory);
					groupCommodity.saveOrUpdate();

					OrderGoods orderGoods = new OrderGoods();
					orderGoods.setGroupCommodity(groupCommodity);
					orderGoods.setAmount(amount);
					// 计算
					Double price = groupCommodity.getPrice().doubleValue();
					String priceStr = price.toString();
					String amountStr = amount.toString();
					BigDecimal orderGoodsTotal = new BigDecimal(priceStr).multiply(new BigDecimal(amountStr));
					orderGoods.setTotal(orderGoodsTotal);
					orderGoods.setOrder(order);
					orderGoods.saveOrUpdate();
					Double orderGoodsTotalDouble = orderGoodsTotal.doubleValue();
					String orderGoodsTotalStr = orderGoodsTotalDouble.toString();
					total = total.add(new BigDecimal(orderGoodsTotalStr));
				} else if (groupOrder.getGroupType() == GroupType.QYT) {
					// 团单商品减去购买数量
					// 锁行
					GroupCommodity groupCommodity = new GroupCommodityQuery().groupOrder(groupOrder).id(commodityId).lockMode(LockMode.UPGRADE).uniqueResult();
					OrderGoods orderGoods = null;
					// 是否打包。是，则代表当前品目的所有商品实行一口价，即按照当前品目的价格。否，则代表是按照当前品目下各个商品的价格计算
					if (groupOrder.getGroupType() == GroupType.QYT && groupCommodity.getGroupCommodityBasic().getIsPack()) {
						GroupCommodityBasic groupCommodityBasic = groupCommodity.getGroupCommodityBasic();
						BigDecimal basicprice = groupCommodityBasic.getPrice();
						// 是打包，则除了设置一个没有隐藏的商品的价格外，其他都为0。并把所有商品保存至订单商品表
						for (GroupCommodity commodity : groupCommodityBasic.getGroupCommoditys()) {
							Integer remnantInventory = commodity.getRemnantInventory() - amount;// 计算库存
							if (remnantInventory < 0) {
								response.outputJson(-1, "对不起，商品库存不足");
								throw new RuntimeException();
							}
							commodity.setRemnantInventory(remnantInventory);
							commodity.saveOrUpdate();
							orderGoods = new OrderGoods();
							orderGoods.setGroupCommodity(commodity);
							orderGoods.setAmount(amount);
							// 计算
							Double price = 0.0;
							if (!commodity.getIsHidden()) {
								price = basicprice.doubleValue();
							}
							String priceStr = price.toString();
							String amountStr = amount.toString();
							BigDecimal orderGoodsTotal = new BigDecimal(priceStr).multiply(new BigDecimal(amountStr));
							orderGoods.setTotal(orderGoodsTotal);
							orderGoods.setOrder(order);
							orderGoods.saveOrUpdate();
							Double orderGoodsTotalDouble = orderGoodsTotal.doubleValue();
							String orderGoodsTotalStr = orderGoodsTotalDouble.toString();
							total = total.add(new BigDecimal(orderGoodsTotalStr));
						}
					} else {
						Integer remnantInventory = groupCommodity.getRemnantInventory() - amount;// 计算库存
						if (remnantInventory < 0) {
							response.outputJson(-1, "对不起，商品库存不足");
							throw new RuntimeException();
						}
						groupCommodity.setRemnantInventory(remnantInventory);
						groupCommodity.saveOrUpdate();

						orderGoods = new OrderGoods();
						orderGoods.setGroupCommodity(groupCommodity);
						orderGoods.setAmount(amount);
						// 计算
						Double price = groupCommodity.getPrice().doubleValue();
						String priceStr = price.toString();
						String amountStr = amount.toString();
						BigDecimal orderGoodsTotal = new BigDecimal(priceStr).multiply(new BigDecimal(amountStr));
						orderGoods.setTotal(orderGoodsTotal);
						orderGoods.setOrder(order);
						orderGoods.saveOrUpdate();
						Double orderGoodsTotalDouble = orderGoodsTotal.doubleValue();
						String orderGoodsTotalStr = orderGoodsTotalDouble.toString();
						total = total.add(new BigDecimal(orderGoodsTotalStr));
					}
				}
			}
		}
		order.setTotal(total);
		order.saveOrUpdate();
		groupOrder.setHeatDegree(groupOrder.getHeatDegree() + 1);
		groupOrder.saveOrUpdate();
		ObjectNode objectNode = JsonUtils.createObjectNode();
		objectNode.put("orderNumber", order.getOrderNumber());
		response.outputJson(0, objectNode);
	}

	/** 获取订单个人信息 */
	@RequestMapping(value = "/getPersonalInfo", method = RequestMethod.POST)
	public void getPersonalInfo(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
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
		WeChatUser weChatUser = new WeChatUserQuery().openId(openId).readOnly().uniqueResult();
		if (weChatUser == null) {
			response.outputJson(-1, "保存失败，未找到该用户");
			return;
		}
		Order order = new OrderQuery().weChatUser(weChatUser).orderNumber(orderNumber).readOnly().uniqueResult();
		if (order == null) {
			response.outputJson(-1, "保存失败，未找到该订单");
			return;
		}
		GroupOrder groupOrder = order.getGroupOrder();
		ObjectNode objectNode = JsonUtils.createObjectNode();
		objectNode.put("phone", weChatUser.getPhone() == null ? "" : weChatUser.getPhone());
		objectNode.put("contacts", weChatUser.getContacts() == null ? "" : weChatUser.getContacts());
		objectNode.put("address", weChatUser.getAddress() == null ? "" : weChatUser.getAddress());
		objectNode.put("remarks", order.getRemarks() == null ? "" : order.getRemarks());
		Community community = groupOrder.getCommunity();
		objectNode.put("townships", community == null ? "" : community.getTownship().getName());
		objectNode.put("communityName", community == null ? "" : community.getName());
		objectNode.put("selfExtractingAddress", groupOrder.getSelfExtractingAddress() == null ? "" : groupOrder.getSelfExtractingAddress());// 自提地址
		response.outputJson(0, objectNode);
	}

	/** 订单商品详情信息(订单公用) */
	@RequestMapping(value = "/getCommodityDetails", method = RequestMethod.POST)
	public void getCommodityDetails(HttpRequestWrapper request, HttpResponseWrapper response) {
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
		WeChatUser weChatUser = new WeChatUserQuery().openId(openId).readOnly().uniqueResult();
		if (weChatUser == null) {
			response.outputJson(-1, "保存失败，未找到该用户");
			return;
		}
		Order order = new OrderQuery().weChatUser(weChatUser).orderNumber(orderNumber).readOnly().uniqueResult();
		if (order == null) {
			response.outputJson(-1, "保存失败，未找到该订单");
			return;
		}
		String path = Environment.getProperty("host") + Environment.getProperty("skin");
		ObjectNode objectNode = JsonUtils.createObjectNode();
		ArrayNode commodities = JsonUtils.createArrayNode();
		Set<OrderGoods> orderGoods = order.getOrderGoods();
		BigDecimal groupOrderTotal = new BigDecimal("0");
		for (OrderGoods og : orderGoods) {
			ObjectNode node = JsonUtils.createObjectNode();
			BigDecimal total = og.getTotal();
			node.put("amount", og.getAmount());
			node.put("total", total);
			GroupCommodity groupCommodity = og.getGroupCommodity();
			if (groupCommodity.getIsHidden() == null || !groupCommodity.getIsHidden()) {
				node.put("name", groupCommodity.getName());
				node.put("price", groupCommodity.getPrice());
				ArrayNode groupCommodityPic = JsonUtils.createArrayNode();
				for (Annex annex : groupCommodity.getAnnexs()) {
					ObjectNode on = JsonUtils.createObjectNode();
					on.put("annexFilePath", path + annex.getFilePath());
					groupCommodityPic.add(on);
				}
				if (groupCommodityPic.size() <= 0) {
					String defaultPicPath = "/images/group_commoditys_pic.png";
					groupCommodityPic.add(JsonUtils.createObjectNode().put("annexFilePath", path + defaultPicPath));
				}
				node.set("groupCommodityPic", groupCommodityPic);
				commodities.add(node);
				groupOrderTotal = groupOrderTotal.add(total);
			} else {
				// 显示隐藏的商品且是升级过的商品
				if (og.isIntegralUpgrade() != null && og.isIntegralUpgrade()) {
					node.put("name", groupCommodity.getName());
					node.put("price", groupCommodity.getPrice());
					ArrayNode groupCommodityPic = JsonUtils.createArrayNode();
					for (Annex annex : groupCommodity.getAnnexs()) {
						ObjectNode on = JsonUtils.createObjectNode();
						on.put("annexFilePath", path + annex.getFilePath());
						groupCommodityPic.add(on);
					}
					if (groupCommodityPic.size() <= 0) {
						String defaultPicPath = "/images/group_commoditys_pic.png";
						groupCommodityPic.add(JsonUtils.createObjectNode().put("annexFilePath", path + defaultPicPath));
					}
					node.set("groupCommodityPic", groupCommodityPic);
					commodities.add(node);
					groupOrderTotal = groupOrderTotal.add(total);
				}
			}
		}
		objectNode.set("commodities", commodities);
		objectNode.put("orderTotal", groupOrderTotal);
		response.outputJson(0, objectNode);
	}

	/** 保存订单个人信息 */
	@Transactional
	@RequestMapping(value = "/savePersonalInfo", method = RequestMethod.POST)
	public void savePersonalInfo(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
		String openId = request.getString("openId");
		String orderNumber = request.getString("orderNumber");
		String phone = request.getString("phone", "^1\\d{10}$");
		String contacts = request.getString("contacts");
		// String address = request.getString("address");
		if (StringUtils.isEmpty(openId)) {
			response.outputJson(-1, "缺少openId参数");
			return;
		}
		if (StringUtils.isEmpty(orderNumber)) {
			response.outputJson(-1, "缺少orderNumber参数");
			return;
		}
		if (StringUtils.isEmpty(phone)) {
			response.outputJson(-1, "缺少phone参数");
			return;
		}
		if (StringUtils.isEmpty(contacts)) {
			response.outputJson(-1, "缺少contacts参数");
			return;
		}
		// if (StringUtils.isEmpty(address)) {
		// response.outputJson(-1, "缺少address参数");
		// return;
		// }
		WeChatUser weChatUser = new WeChatUserQuery().openId(openId).uniqueResult();
		if (weChatUser == null) {
			response.outputJson(-1, "保存失败，未找到该用户");
			return;
		}
		Order order = new OrderQuery().weChatUser(weChatUser).orderNumber(orderNumber).uniqueResult();
		if (order == null) {
			response.outputJson(-1, "保存失败，未找到该订单");
			return;
		}
		order.setPhone(phone);
		order.setContacts(contacts);
		// order.setAddress(address);
		order.setRemarks(request.getString("remarks", 255));
		order.saveOrUpdate();
		weChatUser.setPhone(phone);
		// weChatUser.setContacts(contacts);
		weChatUser.saveOrUpdate();
		response.outputJson(0, "保存成功");
	}

	/** 订单详情 */
	@RequestMapping(value = "/details", method = RequestMethod.POST)
	public void details(HttpRequestWrapper request, HttpResponseWrapper response) {
		String openId = request.getString("openId");
		String orderNumber = request.getString("orderNumber");
		if (StringUtils.isEmpty(openId)) {
			response.outputJson(-1, "缺少openId参数");
			return;
		}
		WeChatUser weChatUser = new WeChatUserQuery().openId(openId).readOnly().uniqueResult();
		if (weChatUser == null) {
			response.outputJson(-1, "参团失败，未找到该用户");
			return;
		}
		if (StringUtils.isEmpty(orderNumber)) {
			response.outputJson(-1, "缺少orderNumber参数");
			return;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Order order = new OrderQuery().weChatUser(weChatUser).orderNumber(orderNumber).readOnly().uniqueResult();
		if (order == null) {
			response.outputJson(-1, "当前订单不存在");
			return;
		}
		GroupOrder groupOrder = order.getGroupOrder();
		ObjectNode objectNode = JsonUtils.createObjectNode();
		objectNode.put("orderId", order.getId());
		objectNode.put("groupOrderId", order.getGroupOrder().getId());
		objectNode.put("orderNumber", order.getOrderNumber());
		objectNode.put("createTime", sdf.format(order.getCreateTime()));// 下单时间
		objectNode.put("payMethod", order.getPayMethod() == null ? "" : order.getPayMethod().toString());// 支付方式
		objectNode.put("payTime", order.getPayTime() == null ? "" : sdf.format(order.getPayTime()));// 支付时间
		objectNode.put("payState", order.getPayState().getDesc());// 支付状态
		objectNode.put("contacts", order.getContacts());
		objectNode.put("phone", order.getPhone());
		objectNode.put("townships", groupOrder.getCommunity().getTownship().getName());
		objectNode.put("communityName", groupOrder.getCommunity().getName());
		// 自提地址
		objectNode.put("selfExtractingAddress", order.getAddress() == null ? groupOrder.getSelfExtractingAddress() == null ? "" : groupOrder.getSelfExtractingAddress() : order.getAddress());
		objectNode.put("selfExtractingTime", groupOrder.getSelfExtractingTime() == null ? "" : sdf.format(groupOrder.getSelfExtractingTime()));// 自提时间
		objectNode.put("remarks", order.getRemarks() == null ? "" : order.getRemarks());// 备注
		objectNode.put("waitingTime", groupOrder.getWaitingTime());
		objectNode.put("cancelTime", order.getCancelTime() == null ? "" : sdf.format(order.getCancelTime())); // 取消时间
		objectNode.put("orderState", order.getOrderState() == null ? "" : order.getOrderState().getDesc());// 订单状态
		// 判断结束时间
		objectNode.put("isEnd", groupOrder.getEndTime().getTime() < System.currentTimeMillis() ? true : false);
		if (order.getOrderState().getOrderStates() == OrderStates.DSH) {
			long thisTime = System.currentTimeMillis();
			long endTime = groupOrder.getEndTime().getTime();
			long remainingTime = endTime - thisTime;
			if (remainingTime <= 0) {
				objectNode.put("remainingDays", 0);// 剩余天
				objectNode.put("remainingHours", 0);// 剩余小时
			} else {
				long remainingDays = remainingTime / 1000 / 60 / 60 / 24;
				long remainingHours = remainingTime / 1000 / 60 / 60 % 24;
				objectNode.put("remainingDays", remainingDays);// 剩余天
				objectNode.put("remainingHours", remainingHours);// 剩余小时
			}
		}
		// 用户订单提货图片
		// String path = Environment.getProperty("host") +
		// Environment.getProperty("skin");
		// ArrayNode orderPickUpPics = JsonUtils.createArrayNode();
		// for (Annex annex : order.getAnnexs()) {
		// ObjectNode no = JsonUtils.createObjectNode();
		// no.put("annexFilePath", path + annex.getFilePath());
		// orderPickUpPics.add(objectNode);
		// }
		// objectNode.set("orderPickUpPics", orderPickUpPics);
		objectNode.put("receivingTime", order.getReceivingTime() == null ? "" : sdf.format(order.getReceivingTime()));// 完成时间
		objectNode.put("refundTime", order.getRefundTime() == null ? "" : sdf.format(order.getRefundTime()));// 退款时间
		objectNode.put("closingTime", order.getClosingTime() == null ? "" : sdf.format(order.getClosingTime()));// 关闭时间;
		objectNode.put("total", order.getTotal());// 订单总价
		objectNode.put("finalPayment", order.getFinalPayment());// 实际付款
		objectNode.put("discount", order.getDiscount());// 优惠金额
		objectNode.put("freight", order.getFreight());// 运费
		objectNode.put("groupOrderEndTime", groupOrder.getEndTime() == null ? "" : sdf.format(groupOrder.getEndTime()));// 团单结束时间
		objectNode.put("groupOrderState", groupOrder.getGroupOrderState().getStates().getDesc());// 团单状态
		objectNode.put("cancelReason", order.getCancelReason() == null ? "" : order.getCancelReason());// 取消原因
		objectNode.put("isRefund", order.getGroupOrder().getIsRefund() == null ? true : order.getGroupOrder().getIsRefund());// 团单是否允许退款
		response.outputJson(0, objectNode);
	}

	/** 绑定用户订单取货照片 */
	@Transactional
	@RequestMapping(value = "/uploadOrderPickUpPic", method = RequestMethod.POST)
	public void uploadOrderPickUpPic(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
		String openId = request.getString("openId");
		String orderNumber = request.getString("orderNumber");
		String orderPics = request.getString("orderPics");// 图片ID

		if (StringUtils.isEmpty(openId)) {
			response.outputJson(-1, "缺少openId参数");
			return;
		}
		if (StringUtils.isEmpty(orderNumber)) {
			response.outputJson(-1, "缺少orderNumber参数");
			return;
		}
		WeChatUser weChatUser = new WeChatUserQuery().openId(openId).readOnly().uniqueResult();
		if (weChatUser == null) {
			response.outputJson(-1, "参团失败，未找到该用户");
			return;
		}
		Order order = new OrderQuery().weChatUser(weChatUser).orderNumber(orderNumber).uniqueResult();
		if (order == null) {
			response.outputJson(-1, "保存失败，未找到该订单");
			return;
		}

		Serializable[] pics = orderPics.split(",");
		if (pics != null && pics.length > 0) {
			List<Annex> groupOrderAnnexList = new AnnexQuery().id(pics).list();
			for (Annex annex : groupOrderAnnexList) {
				annex.setOrder(order);
				annex.saveOrUpdate();
			}
		}
		response.outputJson(0, "操作成功");
	}

	/** 查询订单提货图片（注意区分核销二维码的图片和用户上传的提货照，两者目前都与订单绑定） */
	@RequestMapping(value = "/getOrderPickUpPic", method = RequestMethod.POST)
	public void getOrderPickUpPic(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
		String openId = request.getString("openId");
		String orderNumber = request.getString("orderNumber");
		if (StringUtils.isEmpty(openId)) {
			response.outputJson(-1, "对不起，缺少openId参数");
			return;
		}
		if (StringUtils.isEmpty(orderNumber)) {
			response.outputJson(-1, "对不起，缺少orderNumber参数");
			return;
		}
		WeChatUser weChatUser = new WeChatUserQuery().openId(openId).readOnly().uniqueResult();
		if (weChatUser == null) {
			response.outputJson(-1, "对不起，参团失败，未找到该用户");
			return;
		}

		Order order = new OrderQuery().weChatUser(weChatUser).orderNumber(orderNumber).uniqueResult();
		if (order == null) {
			response.outputJson(-1, "对不起，保存失败，未找到该订单");
			return;
		}
		ObjectNode objectNode = JsonUtils.createObjectNode();
		String path = Environment.getProperty("host") + Environment.getProperty("skin");
		ArrayNode orderPickUpPics = JsonUtils.createArrayNode();
		List<Annex> annexList = new AnnexQuery().order(order).annexType(AnnexType.ORDER_PICK_PIC).list();
		for (Annex annex : annexList) {
			ObjectNode on = JsonUtils.createObjectNode();
			on.put("annexFilePath", path + annex.getFilePath());
			orderPickUpPics.add(on);
		}
		objectNode.set("orderPickUpPics", orderPickUpPics);
		response.outputJson(0, objectNode);
	}

	/** 待付款状态的-用户发起取消参团 */
	@Transactional
	@RequestMapping(value = "/cancelorder", method = RequestMethod.POST)
	public void cancelorder(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
		// 把待付款且待支付的订单变更为已取消状态，锁行
		String openId = request.getString("openId");
		String orderNumber = request.getString("orderNumber");
		if (StringUtils.isEmpty(openId)) {
			response.outputJson(-1, "对不起，缺少openId参数");
			return;
		}
		if (StringUtils.isEmpty(orderNumber)) {
			response.outputJson(-1, "对不起，缺少orderNumber参数");
			return;
		}
		WeChatUser weChatUser = new WeChatUserQuery().openId(openId).readOnly().uniqueResult();
		OrderState orderState = new OrderStateQuery().orderStates(OrderStates.DFK).uniqueResult();
		Date date = new Date();
		Order order = new OrderQuery().weChatUser(weChatUser).orderNumber(orderNumber).orderState(orderState).payState(PayState.DZF).lockMode(LockMode.UPGRADE).uniqueResult();

		if (order == null) {
			response.outputJson(-1, "对不起，未找到该订单，取消参团失败！");
			return;
		}
		// 取消订单
		order.setOrderState(new OrderStateQuery().orderStates(OrderStates.YQX).uniqueResult());
		order.setCancelTime(date);
		order.setCancelReason("用户取消参团");
		// 恢复单数和库存
		if (!order.getIsRecovery()) {
			for (OrderGoods orderGoods : order.getOrderGoods()) {
				Integer amount = orderGoods.getAmount();
				GroupCommodity groupCommodity = new GroupCommodityQuery().id(orderGoods.getGroupCommodity().getId()).lockMode(LockMode.UPGRADE).uniqueResult();
				Integer remnantInventory = groupCommodity.getRemnantInventory() + amount;
				groupCommodity.setRemnantInventory(remnantInventory);
				groupCommodity.saveOrUpdate();
			}
			order.setIsRecovery(true);
		}
		order.saveOrUpdate();
		response.outputJson(0, "取消参团成功");
	}

	/** 生成分享图片 */
	@Transactional
	@RequestMapping(value = "/generateShareImage", method = RequestMethod.POST)
	public void generateShareImage(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
		String openId = request.getString("openId");
		String orderNumber = request.getString("orderNumber");
		Long groupOrderId = request.getLong("groupOrderId");
		if (StringUtils.isEmpty(openId)) {
			response.outputJson(-1, "对不起，缺少openId参数");
			return;
		}
		if (StringUtils.isEmpty(orderNumber) && groupOrderId == null) {
			response.outputJson(-1, "对不起，缺少orderNumber或groupOrderId参数");
			return;
		}
		if (StringUtils.isNotBlank(orderNumber) && groupOrderId != null) {
			response.outputJson(-1, "对不起，orderNumber和groupOrderId参数不可同时传输");
			return;
		}
		WeChatUser weChatUser = new WeChatUserQuery().openId(openId).readOnly().uniqueResult();
		if (weChatUser == null) {
			response.outputJson(-1, "对不起，分享失败，未找到用户");
			return;
		}
		// 查询是否已存在分享图片
		Annex annex = null;
		GroupOrder groupOrder = null;
		if (StringUtils.isNotBlank(orderNumber)) {
			Order order = new OrderQuery().weChatUser(weChatUser).orderNumber(orderNumber).uniqueResult();
			if (order == null) {
				response.outputJson(-1, "对不起，分享失败，未找到订单");
				return;
			}
			groupOrder = order.getGroupOrder();
			annex = new AnnexQuery().annexType(AnnexType.SHARE_PIC).fileName(orderNumber + "_compositeGraph.png").uniqueResult();
		}
		if (groupOrderId != null) {
			groupOrder = new GroupOrderQuery().id(groupOrderId).uniqueResult();
			if (groupOrder == null) {
				response.outputJson(-1, "对不起，分享失败，未找到拼团");
				return;
			}
			annex = new AnnexQuery().annexType(AnnexType.SHARE_PIC).fileName(groupOrderId + "_compositeGraph.png").uniqueResult();
		}

		String skin = Environment.getProperty("skin");
		String host = Environment.getProperty("host");
		List<File> fileList = new ArrayList<File>();
		if (annex == null) {
			String path = Environment.getProperty("annex.path");
			String generatePath = path + Environment.getProperty("generate.images");
			File backgroundFile = null;
			String publicPara = "";
			if (StringUtils.isNotBlank(orderNumber)) {
				publicPara = orderNumber;
				// 背景图
				String backgroundPath = generatePath + "/fx_bg.png";
				backgroundFile = new File(backgroundPath);
				FileUtils.forceMkdir(backgroundFile.getParentFile());
				if (!backgroundFile.exists()) {
					backgroundFile = GenerateImageUtils.createBackground(backgroundPath, 1000, 810);
				}
				// 头像图片处理
				String headImagesPath = generatePath + "/" + orderNumber + "_head.png";
				File headFile = GenerateImageUtils.createPortrait(headImagesPath, weChatUser.getHeadImgUrl());
				fileList.add(headFile);
				// 用户名图片处理
				String userNameImagesPath = generatePath + "/" + orderNumber + "_userName.png";
				String contacts = weChatUser.getContacts().substring(0, 1) + "**";
				File userNameFile = GenerateImageUtils.createTextImage(new Font("微软雅黑", Font.BOLD, 60), userNameImagesPath, contacts, Color.black, true, false);
				fileList.add(userNameFile);
				// ‘拥抱团长’ 图
				GroupOrderQuery groupOrderQuery = new GroupOrderQuery().weChatUser(groupOrder.getWeChatUser());
				int size = new OrderQuery().weChatUser(weChatUser).groupOrderQuery(groupOrderQuery).payState(PayState.YZF, PayState.ZFZ).list().size();
				String cantuanFilePath = generatePath + "/" + orderNumber + "_cantuan.png";
				File cantuanFile = GenerateImageUtils.createTextImage(new Font("微软雅黑", Font.BOLD, 60), cantuanFilePath, "第" + size + "次拥抱团长", Color.red, false, false);
				fileList.add(cantuanFile);
			}
			if (groupOrderId != null) {
				publicPara = groupOrderId.toString();
				// 背景图
				String backgroundPath = generatePath + "/fx_group_bg.png";
				backgroundFile = new File(backgroundPath);
				FileUtils.forceMkdir(backgroundFile.getParentFile());
				if (!backgroundFile.exists()) {
					backgroundFile = GenerateImageUtils.createBackground(backgroundPath, 1000, 650);
				}
			}
			// 商品图(合成图)
			Set<Annex> annexs = groupOrder.getAnnexs();
			File sourceFile = null;// 源文件
			String thumbnailImagesPath = generatePath + "/" + publicPara + "_groupOrder.png";
			for (Annex an : annexs) {
				sourceFile = new File(path + an.getFilePath());
				break;
			}
			// 如果为空，或者文件不存在，则使用默认图片
			if (sourceFile == null || !sourceFile.exists()) {
				sourceFile = ResourceUtils.getFile("classpath:images/group_order_pic.png");
			}
			Set<GroupCommodity> groupCommoditys = groupOrder.getGroupCommoditys();
			int groupCommoditysSize = groupCommoditys.size();
			int orderSize = new OrderQuery().groupOrder(groupOrder).orderStateQuery(new OrderStateQuery().orderStates(OrderStates.DSH)).payState(PayState.YZF).list().size();
			File graPh = null;
			String groupHeChengPath = generatePath + "/" + publicPara + "_groupHeCheng.png";
			if (groupCommoditysSize == 1) {
				for (GroupCommodity groupCommodity : groupCommoditys) {
					BigDecimal xj = groupCommodity.getPrice();
					BigDecimal yj = groupCommodity.getOriginalPrice();
					graPh = GenerateImageUtils.belowShareCompositeGraPh(publicPara, sourceFile, thumbnailImagesPath, xj, yj, orderSize, groupHeChengPath, true);
					break;
				}
			} else {
				graPh = GenerateImageUtils.belowShareCompositeGraPh(publicPara, sourceFile, thumbnailImagesPath, null, null, orderSize, groupHeChengPath, false);
			}
			fileList.add(graPh);
			// 我要拼团图片
			String wyptPath = generatePath + "/" + publicPara + "_wypt.png";
			File wyptFile = ResourceUtils.getFile("classpath:images/wypt.png");
			File wyptFile2 = GenerateImageUtils.thumbnail(wyptFile, 890, 100, wyptPath);
			fileList.add(wyptFile2);

			// 图片合成
			File compositeGraPhFile = null;
			String compositeGraPhPath = generatePath + "/" + publicPara + "_compositeGraph.png";
			if (StringUtils.isNotBlank(orderNumber)) {
				compositeGraPhFile = GenerateImageUtils.synthesisPicture(backgroundFile, fileList, compositeGraPhPath, true);
			}
			if (groupOrderId != null) {
				compositeGraPhFile = GenerateImageUtils.synthesisPicture(backgroundFile, fileList, compositeGraPhPath, false);
			}
			// 压缩图片
			Thumbnails.of(compositeGraPhFile).scale(0.25f).toFile(compositeGraPhFile);
			fileList.add(compositeGraPhFile);
			// 保存合成图
			annex = AnnexUtils.saveAnnex(AnnexType.SHARE_PIC, publicPara + "_compositeGraph.png", null, FileUtils.readFileToByteArray(compositeGraPhFile));
		}
		// 删除生成的图片
		for (File file : fileList) {
			file.delete();
		}
		ObjectNode objectNode = JsonUtils.createObjectNode();
		objectNode.put("filePath", host + skin + annex.getFilePath());
		objectNode.put("annexId", annex.getId());
		response.outputJson(0, objectNode);
	}

	/**
	 * 生成订单随机6位数
	 * 
	 * @param digits 位数
	 * @return String
	 */
	public static String generateRandomNumber(Integer digits) {
		String range = "0123456789abcdefghijklmnopqrstuvwxyz";
		String rands = "";
		for (int i = 0; i < digits; i++) {
			int rand = (int) (Math.random() * range.length());
			rands += range.charAt(rand);
		}
		Order order = new OrderQuery().randomNumber(rands).readOnly().uniqueResult();
		if (order == null) {
			return rands;
		} else {
			return generateRandomNumber(6);
		}
	}
}