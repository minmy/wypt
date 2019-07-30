package cn.sini.cgb.api.cgb.action.group;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.sini.cgb.admin.annex.entity.Annex;
import cn.sini.cgb.api.cgb.entity.group.Community;
import cn.sini.cgb.api.cgb.entity.group.Follow;
import cn.sini.cgb.api.cgb.entity.group.GroupCommodity;
import cn.sini.cgb.api.cgb.entity.group.GroupCommodityBasic;
import cn.sini.cgb.api.cgb.entity.group.GroupOrder;
import cn.sini.cgb.api.cgb.entity.group.GroupOrder.GroupType;
import cn.sini.cgb.api.cgb.entity.group.GroupOrderState;
import cn.sini.cgb.api.cgb.entity.group.GroupOrderState.States;
import cn.sini.cgb.api.cgb.entity.group.Label;
import cn.sini.cgb.api.cgb.entity.group.Order;
import cn.sini.cgb.api.cgb.entity.group.Order.PayState;
import cn.sini.cgb.api.cgb.entity.group.OrderGoods;
import cn.sini.cgb.api.cgb.entity.group.OrderState.OrderStates;
import cn.sini.cgb.api.cgb.entity.group.WeChatUser;
import cn.sini.cgb.api.cgb.entity.statistics.StaGroupOrder;
import cn.sini.cgb.api.cgb.query.group.FollowQuery;
import cn.sini.cgb.api.cgb.query.group.GroupOrderQuery;
import cn.sini.cgb.api.cgb.query.group.GroupOrderStateQuery;
import cn.sini.cgb.api.cgb.query.group.WeChatUserQuery;
import cn.sini.cgb.common.http.HttpRequestWrapper;
import cn.sini.cgb.common.http.HttpResponseWrapper;
import cn.sini.cgb.common.json.JsonUtils;
import cn.sini.cgb.common.query.Page;
import cn.sini.cgb.common.util.Environment;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 拼团首页action
 * 
 * @author gaowei
 */
@Controller
@RequestMapping("/api/groupHome")
public class GroupHomeAction {

	private static final String UP = "up";
	private static final String DROP = "drop";

	/**
	 * 首页排序规则： 1.未结束（距离结束时间近的排在上面，升序） 2.按热度降序
	 *
	 * 拼团首页列表
	 */
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public void list(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
		ObjectNode objectNode = JsonUtils.createObjectNode();
		String theme = request.getTrim("theme");
		String openId = request.getTrim("openId");
		String followOpenId = request.getTrim("followOpenId");
		String releaseSort = request.getString("releaseSort");// 发布时间排序
		String salesColumeSort = request.getString("salesColumeSort");// 销量排序
		String browseVolumeSort = request.getString("browseVolumeSort");// 浏览量排序
		GroupOrderQuery groupOrderQuery = new GroupOrderQuery();
		if (StringUtils.isNotBlank(followOpenId)) {
			WeChatUser weChatUser = new WeChatUserQuery().openId(followOpenId).uniqueResult();
			if (weChatUser == null) {
				response.outputJson(-1, "对不起，保存失败，未找到该用户");
				return;
			}
			groupOrderQuery.weChatUser(weChatUser);
		}
		if (StringUtils.isBlank(openId)) {
			response.outputJson(-1, "对不起，查询失败，未找当前用户");
			return;
		}
		WeChatUser weChatUser = new WeChatUserQuery().openId(openId).uniqueResult();
		Community community = weChatUser.getCommunity();
		ArrayNode arrayNode = JsonUtils.createArrayNode();
		Page<GroupOrder> page = null;
		GroupOrderState groupOrderState = new GroupOrderStateQuery().states(States.JXZ).readOnly().uniqueResult();
		String approval = Environment.getProperty("approval");
		if ("true".equals(approval)) {
			Serializable[] ids = { 715L, 716L, 717L, 718L };
			page = groupOrderQuery.isTop(false).id(ids).orderBy("endTime", true).pageHasCount(request.getPageNum(), request.getPageSize());
		} else {
			if (community != null) {
				// 根据用户所关联的小区查询对应的团单
				groupOrderQuery.community(community);
				groupOrderQuery.theme(theme).groupOrderState(groupOrderState).endTimeGe(new Date()).readOnly();
				if (UP.equals(salesColumeSort)) {
					groupOrderQuery.orderBy("heatDegree", true);
				} else if (DROP.equals(salesColumeSort)) {
					groupOrderQuery.orderBy("heatDegree", false);
				}
				if (UP.equals(browseVolumeSort)) {
					groupOrderQuery.orderBy("browseVolume", true);
				} else if (DROP.equals(browseVolumeSort)) {
					groupOrderQuery.orderBy("browseVolume", false);
				}
				if (UP.equals(releaseSort)) {
					groupOrderQuery.orderBy("releaseTime", true);
				} else if (DROP.equals(releaseSort)) {
					groupOrderQuery.orderBy("releaseTime", false);
				}
				page = groupOrderQuery.isTop(false).orderBy("endTime", true).pageHasCount(request.getPageNum(), request.getPageSize());
			} else {
				// 如果当前用户没有选择小区，则不显示数据
				objectNode.set("recordList", arrayNode);
				objectNode.put("totalPage", 0);
				objectNode.put("totalRecord", 0);
				objectNode.put("pageNum", 0);
				objectNode.put("pageSize", 10);
				objectNode.put("followUsers", weChatUser.getFollows().size());
				objectNode.put("communityName", "");
				response.outputJson(0, objectNode);
				return;
			}
		}
		// 查询置顶拼团，并合并分页集合。只有查询第一页的时候才查置顶，否则出现置顶团重复问题
		int pageNum = request.getPageNum();
		List<GroupOrder> groupOrderList = null;
		if (pageNum == 1) {
			groupOrderList = new GroupOrderQuery().groupOrderState(groupOrderState).endTimeGe(new Date()).isTop(true).list();
			groupOrderList.addAll(page.getRecordList());
		} else {
			groupOrderList = page.getRecordList();
		}
		String path = Environment.getProperty("host") + Environment.getProperty("skin");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm");
		for (GroupOrder groupOrder : groupOrderList) {
			ObjectNode node = JsonUtils.createObjectNode();
			node.put("groupOrderId", groupOrder.getId());
			node.put("theme", groupOrder.getTheme());
			node.put("releaseTime", groupOrder.getReleaseTime() == null ? "" : simpleDateFormat.format(groupOrder.getReleaseTime()));
			node.put("headImgUrl", groupOrder.getWeChatUser().getHeadImgUrl());
			// 标签
			String labels = " ";// 此处空格不可去掉
			for (Label label : groupOrder.getLabels()) {
				labels = labels + label.getTag() + ",";
			}
			node.put("labels", labels.substring(0, labels.length() - 1).trim());// 空格这里有用
			node.put("name", groupOrder.getWeChatUser().getName());
			node.put("endTime", simpleDateFormat.format(groupOrder.getEndTime()));
			// node.put("popularity", "人气团长"); // 已删除
			// 已下单用户的头像
			String orderHeadImgUrls = "";
			for (Order order : groupOrder.getOrders()) {
				OrderStates orderStates = order.getOrderState().getOrderStates();
				PayState payState = order.getPayState();
				if ((orderStates == OrderStates.DSH && payState == PayState.YZF) || (orderStates == OrderStates.YWC && payState == PayState.YZF)) {
					orderHeadImgUrls = orderHeadImgUrls + order.getWeChatUser().getHeadImgUrl() + ",";
				}
			}
			node.put("orderHeadImgUrls", orderHeadImgUrls);
			ArrayNode groupOrderPics = JsonUtils.createArrayNode();
			for (Annex annex : groupOrder.getAnnexs()) {
				ObjectNode on = JsonUtils.createObjectNode();
				on.put("annexId", annex.getId());
				on.put("annexFilePath", path + annex.getFilePath());
				groupOrderPics.add(on);
			}
			// 若团单没有图片则使用默认图
			if (groupOrderPics.size() <= 0) {
				String defaultPicPath = "/images/group_order_pic.png";
				groupOrderPics.add(JsonUtils.createObjectNode().put("annexFilePath", path + defaultPicPath));
			}
			node.set("groupOrderPics", groupOrderPics);
			node.put("validOrder", groupOrder.getValidOrders());
			node.put("payOrder", groupOrder.getPayOrders());
			node.put("groupType", groupOrder.getGroupType() == null ? "PTT" : groupOrder.getGroupType().toString());
			node.put("shareCoupon", groupOrder.getShareCoupon() == null ? false : groupOrder.getShareCoupon());
			arrayNode.add(node);
		}
		objectNode.set("recordList", arrayNode);
		objectNode.put("totalPage", page.getTotalPage());
		objectNode.put("totalRecord", page.getTotalRecord());
		objectNode.put("pageNum", page.getPageNum());
		objectNode.put("pageSize", page.getPageSize());
		objectNode.put("followUsers", weChatUser.getFollows().size());
		objectNode.put("communityName", community == null ? "" : community.getName());
		response.outputJson(0, objectNode);
	}

	/** 判断当前用户所属小区和团单所属小区是否是同一小区 */
	@Transactional
	@RequestMapping(value = "/isSameCommunity", method = RequestMethod.POST)
	public void isSameCommunity(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
		Long groupOrderId = request.getLong("groupOrderId");
		String openId = request.getString("openId");// 当前访问团单用户的openId
		Boolean isSwitch = request.getBoolean("isSwitch");// 是否切换小区
		// String randomNumber = request.getString("randomNumber");
		if (groupOrderId == null || groupOrderId == 0) {
			response.outputJson(-1, "对不起，缺少groupOrderId参数");
			return;
		}
		if (StringUtils.isEmpty(openId)) {
			response.outputJson(-1, "对不起，缺少openId参数");
			return;
		}
		WeChatUser weChatUser = new WeChatUserQuery().openId(openId).uniqueResult();
		if (weChatUser == null) {
			response.outputJson(-1, "对不起，查询失败，未找到该用户");
			return;
		}
		GroupOrder groupOrder = new GroupOrderQuery().id(groupOrderId).uniqueResult();
		if (groupOrder == null) {
			response.outputJson(-1, "对不起，您查看的拼团不存在或已下架");
			return;
		}
		// 用户小区
		Community community = weChatUser.getCommunity();
		// 如果用户小区为null，或者isSwitch为true，则切换与当前团单相同的小区地址
		if (community == null || isSwitch) {
			weChatUser.setProvince(groupOrder.getProvince());
			weChatUser.setCity(groupOrder.getCity());
			weChatUser.setArea(groupOrder.getTownships());
			weChatUser.setCommunity(groupOrder.getCommunity());
			weChatUser.saveOrUpdate();
			// new ShareTicketAction().getShareTicketByShare(openId, randomNumber);
		}
		Integer sameCommunity = groupOrder.isSameCommunity(weChatUser);
		ObjectNode objectNode = JsonUtils.createObjectNode();
		objectNode.put("sameCommunity", sameCommunity);
		response.outputJson(0, objectNode);
	}

	/** 拼团详情 */
	@Transactional
	@RequestMapping(value = "/details", method = RequestMethod.POST)
	public void details(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
		Long groupOrderId = request.getLong("groupOrderId");
		String openId = request.getString("openId");// 当前访问团单用户的openId
		if (groupOrderId == null || groupOrderId == 0) {
			response.outputJson(-1, "对不起，缺少groupOrderId参数");
			return;
		}
		if (StringUtils.isEmpty(openId)) {
			response.outputJson(-1, "对不起，缺少openId参数");
			return;
		}
		WeChatUser user = new WeChatUserQuery().openId(openId).uniqueResult();
		if (user == null) {
			response.outputJson(-1, "对不起，查询失败，未找到该用户");
			return;
		}
		GroupOrder groupOrder = new GroupOrderQuery().id(groupOrderId).uniqueResult();
		if (groupOrder == null) {
			response.outputJson(-1, "对不起，您查看的拼团不存在或已下架");
			return;
		}
		groupOrder.setBrowseVolume(groupOrder.getBrowseVolume() + 1);
		groupOrder.saveOrUpdate();
		ObjectNode objectNode = JsonUtils.createObjectNode();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		objectNode.put("groupOrderId", groupOrder.getId());
		objectNode.put("theme", groupOrder.getTheme());
		objectNode.put("themeintroduce", groupOrder.getThemeIntroduce() == null ? "" : groupOrder.getThemeIntroduce());
		objectNode.put("notice", groupOrder.getNotice() == null ? "" : groupOrder.getNotice());
		objectNode.put("selfExtractingAddress", groupOrder.getSelfExtractingAddress());
		objectNode.put("phone", groupOrder.getPhone());
		objectNode.put("endTime", groupOrder.getEndTime() == null ? "" : simpleDateFormat.format(groupOrder.getEndTime()));
		objectNode.put("upperlimit", groupOrder.getUpperlimit());
		objectNode.put("payOrders", groupOrder.getPayOrders());
		objectNode.put("waitingTime", groupOrder.getWaitingTime());
		Community community = groupOrder.getCommunity();
		objectNode.put("community", community == null ? "" : community.getName());// 小区
		objectNode.put("groupOrderState", groupOrder.getGroupOrderState().getStates().getDesc());
		WeChatUser weChatUser = groupOrder.getWeChatUser();// 拼团团长
		objectNode.put("followOpenId", weChatUser.getOpenId());
		// 关注
		Follow follow = new FollowQuery().weChatUser(user).followUser(weChatUser).uniqueResult();
		objectNode.put("followId", follow == null ? "" : follow.getId().toString());
		// 标签
		String labels = " ";// 此处空格不可去掉
		for (Label label : groupOrder.getLabels()) {
			labels = labels + label.getTag() + ",";
		}
		objectNode.put("labels", labels.substring(0, labels.length() - 1).trim());// 空格这里有用
		// 拼团图片
		String path = Environment.getProperty("host") + Environment.getProperty("skin");
		ArrayNode groupOrderPicNodes = JsonUtils.createArrayNode();
		for (Annex annex : groupOrder.getAnnexs()) {
			ObjectNode on = JsonUtils.createObjectNode();
			on.put("annexId", annex.getId());
			on.put("annexFilePath", path + annex.getFilePath());
			groupOrderPicNodes.add(on);
		}
		// 默认图
		if (groupOrderPicNodes.size() <= 0) {
			String defaultPicPath = "/images/group_order_pic.png";
			groupOrderPicNodes.add(JsonUtils.createObjectNode().put("annexFilePath", path + defaultPicPath));
		}
		objectNode.set("groupOrderPics", groupOrderPicNodes);
		// 订单用户头像
		String orderHeadImgUrls = "";
		ArrayNode groupUserInfo = JsonUtils.createArrayNode();
		for (Order order : groupOrder.getOrders()) {
			OrderStates orderStates = order.getOrderState().getOrderStates();
			PayState payState = order.getPayState();
			if ((orderStates == OrderStates.DSH && payState == PayState.YZF) || (orderStates == OrderStates.YWC && payState == PayState.YZF)) {
				WeChatUser chatUser = order.getWeChatUser();
				String headImgUrl = chatUser.getHeadImgUrl();
				orderHeadImgUrls = orderHeadImgUrls + headImgUrl + ",";
				// 已拼团用户信息和信息信息
				ObjectNode node = JsonUtils.createObjectNode();
				node.put("orderUserHearImgUrls", headImgUrl);
				node.put("orderUserName", chatUser.getName().substring(0, 1) + "**");
				// 计算时间
				long thisTime = System.currentTimeMillis();
				long createTime = order.getCreateTime().getTime();
				long beforeTime = thisTime - createTime;
				// 计算分钟 60000 = (1000 * 60)
				long remainingMin = beforeTime / 60000;
				if (remainingMin >= 60) {
					// 计算小时 3600000 = (1000 * 60 * 60)
					long remainingHours = beforeTime / 3600000;
					if (remainingHours >= 24) {
						// 计算天数 86400000 = (1000 * 60 * 60 * 24)
						long remainingDays = beforeTime / 86400000;
						if (remainingDays >= 30) {
							node.put("beforeTime", "30天前");
						} else {
							node.put("beforeTime", remainingDays + "天前");
						}
					} else {
						node.put("beforeTime", remainingHours + "小时前");
					}
				} else {
					node.put("beforeTime", remainingMin + "分钟前");
				}
				// 商品信息
				ArrayNode goodsArrayNode = JsonUtils.createArrayNode();
				for (OrderGoods orderGoods : order.getOrderGoods()) {
					// 不显示隐藏的商品
					ObjectNode goodsNode = JsonUtils.createObjectNode();
					if (orderGoods.getGroupCommodity().getIsHidden() == null || !orderGoods.getGroupCommodity().getIsHidden()) {
						goodsNode.put("commodityName", orderGoods.getGroupCommodity().getName());
						goodsNode.put("commodityAmount", orderGoods.getAmount());
						goodsArrayNode.add(goodsNode);
					} else {
						// 升级商品
						if (orderGoods.isIntegralUpgrade() != null && orderGoods.isIntegralUpgrade()) {
							GroupCommodity groupCommodity = orderGoods.getGroupCommodity();
							goodsNode.put("commodityName", groupCommodity.getName());
							goodsNode.put("commodityAmount", orderGoods.getAmount());
							goodsArrayNode.add(goodsNode);
						}
					}
				}
				node.set("orderGoodsInfo", goodsArrayNode);
				groupUserInfo.add(node);
			}
		}
		objectNode.set("groupUserInfo", groupUserInfo);// 已拼团用户信息
		objectNode.put("orderHeadImgUrls", orderHeadImgUrls);
		// 计算当前时间距团单结束时间的剩余时间
		long thisTime = System.currentTimeMillis();
		long endTime = groupOrder.getEndTime().getTime();
		long remainingTime = endTime - thisTime;
		objectNode.put("remainingTime", remainingTime < 0 ? 0 : remainingTime);
		objectNode.put("userHeadImgUrls", weChatUser.getHeadImgUrl());
		objectNode.put("userName", weChatUser.getName());
		// 自提时间
		Date selfExtractingTime = groupOrder.getSelfExtractingTime();
		Date selfExtractingEndTime = groupOrder.getSelfExtractingEndTime();
		objectNode.put("selfExtractingTime", selfExtractingTime == null ? "" : simpleDateFormat.format(selfExtractingTime));
		objectNode.put("selfExtractingEndTime", selfExtractingEndTime == null ? "" : simpleDateFormat.format(selfExtractingEndTime));
		// 团类型
		objectNode.put("groupType", groupOrder.getGroupType() == null ? "PTT" : groupOrder.getGroupType().toString());

		// 普通拼团商品信息
		if (groupOrder.getGroupType() == null || groupOrder.getGroupType() == GroupType.PTT) {
			// 商品信息
			ArrayNode commodityNodes = JsonUtils.createArrayNode();
			for (GroupCommodity groupCommodity : groupOrder.getGroupCommoditys()) {
				ObjectNode node = JsonUtils.createObjectNode();
				node.put("commodityId", groupCommodity.getId());
				node.put("name", groupCommodity.getName());
				node.put("description", groupCommodity.getDescription() == null ? "" : groupCommodity.getDescription());
				node.put("details", groupCommodity.getDetails() == null ? "" : groupCommodity.getDetails());
				node.put("remnantInventory", groupCommodity.getRemnantInventory());
				node.put("buyNumber", groupCommodity.getTotalInventory() - groupCommodity.getRemnantInventory());
				node.put("price", groupCommodity.getPrice());
				if (groupCommodity.getUpperlimit() == 0 || groupCommodity.getUpperlimit() == null) {
					node.put("surplusUpperlimitNumber", "不限");
				} else {
					node.put("surplusUpperlimitNumber", groupCommodity.getSurplusUpperlimitNumber(user));
				}
				ArrayNode groupCommodityPic = JsonUtils.createArrayNode();
				for (Annex annex : groupCommodity.getAnnexs()) {// 商品图
					ObjectNode on = JsonUtils.createObjectNode();
					on.put("annexFilePath", path + annex.getFilePath());
					groupCommodityPic.add(on);
				}
				if (groupCommodityPic.size() <= 0) {// 为空则使用默认图
					String defaultPicPath = "/images/group_commoditys_pic.png";
					groupCommodityPic.add(JsonUtils.createObjectNode().put("annexFilePath", path + defaultPicPath));
				}
				node.set("groupCommodityPic", groupCommodityPic);
				commodityNodes.add(node);
			}
			objectNode.set("groupCommoditys", commodityNodes);
		} else if (groupOrder.getGroupType() == GroupType.QYT) {
			objectNode.put("isUpgrade", groupOrder.getIsUpgrade());// 是否升级
			objectNode.put("isCombination", groupOrder.getIsCombination());// 是否组合
			// 企业团商品信息
			// 商品品目
			ArrayNode commodityBasicArrayNode = JsonUtils.createArrayNode();
			for (GroupCommodityBasic groupCommodityBasic : groupOrder.getGroupCommodityBasic()) {
				ObjectNode commodityBasicNode = JsonUtils.createObjectNode();
				commodityBasicNode.put("itemId", groupCommodityBasic.getId());// 商品品目ID
				commodityBasicNode.put("itemName", groupCommodityBasic.getName());// 商品品目名称
				commodityBasicNode.put("basicPrice", groupCommodityBasic.getPrice());// 商品品目价格
				commodityBasicNode.put("isPack", groupCommodityBasic.getIsPack());// 是否打包
				// 商品信息
				ArrayNode commodityNodes = JsonUtils.createArrayNode();
				for (GroupCommodity groupCommodity : groupCommodityBasic.getGroupCommoditys()) {
					if (!groupCommodity.getIsHidden()) {
						ObjectNode node = JsonUtils.createObjectNode();
						node.put("commodityId", groupCommodity.getId());
						node.put("businessId", groupCommodity.getBusiness().getId());
						node.put("businessName", groupCommodity.getBusiness().getName());
						node.put("name", groupCommodity.getName());// 商品名称
						node.put("description", groupCommodity.getDescription() == null ? "" : groupCommodity.getDescription());// 规格说明
						node.put("details", groupCommodity.getDetails() == null ? "" : groupCommodity.getDetails());// 商品详情
						node.put("remnantInventory", groupCommodity.getRemnantInventory());// 剩余库存
						node.put("buyNumber", groupCommodity.getTotalInventory() - groupCommodity.getRemnantInventory());
						node.put("originalPrice", groupCommodity.getOriginalPrice() == null ? "" : groupCommodity.getOriginalPrice().toString());// 原价
						node.put("price", groupCommodity.getPrice());// 价格
						node.put("isUpgrade", groupCommodity.getIsUpgrade());// 是否能升级商品
						node.put("integral", groupCommodity.getIntegral());// 升级商品所需积分
						if (groupCommodity.getUpperlimit() == 0 || groupCommodity.getUpperlimit() == null) {
							node.put("surplusUpperlimitNumber", "不限");
						} else {
							node.put("surplusUpperlimitNumber", groupCommodity.getSurplusUpperlimitNumber(user));// 剩余购买数量
						}
						ArrayNode groupCommodityPic = JsonUtils.createArrayNode();
						for (Annex annex : groupCommodity.getAnnexs()) {// 商品图
							ObjectNode on = JsonUtils.createObjectNode();
							on.put("annexFilePath", path + annex.getFilePath());
							groupCommodityPic.add(on);
						}
						if (groupCommodityPic.size() <= 0) {// 为空则使用默认图
							String defaultPicPath = "/images/group_commoditys_pic.png";
							groupCommodityPic.add(JsonUtils.createObjectNode().put("annexFilePath", path + defaultPicPath));
						}
						node.set("groupCommodityPic", groupCommodityPic);
						commodityNodes.add(node);
					}
				}
				commodityBasicNode.set("groupCommoditys", commodityNodes);
				commodityBasicArrayNode.add(commodityBasicNode);
			}
			objectNode.set("groupBasics", commodityBasicArrayNode);
		}
		// 判断当前用户所属小区和团单所属小区是否是同一小区
		objectNode.put("isSameCommunity", groupOrder.isSameCommunity(user));
		// 判断用户下是否存在当前团单未付款的订单
		objectNode.put("isGroupOrderByWeChatUserOrder", groupOrder.isGroupOrderByWeChatUserOrder(user));
		// 访问记录
		StaGroupOrder staGroupOrder = new StaGroupOrder();
		staGroupOrder.setGroupOrder(groupOrder);
		staGroupOrder.setName(user.getName());
		staGroupOrder.setOpenId(openId);
		staGroupOrder.saveOrUpdate();
		response.outputJson(0, objectNode);
	}

	/** 预览 */
	@RequestMapping(value = "/preview", method = RequestMethod.POST)
	public void preview(HttpRequestWrapper request, HttpResponseWrapper response) {
		Long groupOrderId = request.getLong("groupOrderId");
		if (groupOrderId == null || groupOrderId == 0) {
			response.outputJson(-1, "对不起，缺少groupOrderId参数");
			return;
		}
		GroupOrder groupOrder = new GroupOrderQuery().id(groupOrderId).uniqueResult();
		if (groupOrder == null) {
			response.outputJson(-1, "对不起，您查看的拼团不存在或已下架");
			return;
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		ObjectNode objectNode = JsonUtils.createObjectNode();
		objectNode.put("groupOrderId", groupOrder.getId());
		objectNode.put("theme", groupOrder.getTheme());
		objectNode.put("themeintroduce", groupOrder.getThemeIntroduce() == null ? "" : groupOrder.getThemeIntroduce());
		objectNode.put("notice", groupOrder.getNotice() == null ? "" : groupOrder.getNotice());
		objectNode.put("selfExtractingAddress", groupOrder.getSelfExtractingAddress() == null ? "" : groupOrder.getSelfExtractingAddress());
		Date selfExtractingTime = groupOrder.getSelfExtractingTime();
		objectNode.put("selfExtractingTime", selfExtractingTime == null ? "" : simpleDateFormat.format(selfExtractingTime));
		objectNode.put("phone", groupOrder.getPhone() == null ? "" : groupOrder.getPhone());
		objectNode.put("endTime", groupOrder.getEndTime() == null ? "" : simpleDateFormat.format(groupOrder.getEndTime()));
		objectNode.put("upperlimit", groupOrder.getUpperlimit() == null ? 0 : groupOrder.getUpperlimit());
		objectNode.put("validOrder", groupOrder.getValidOrders());
		objectNode.put("waitingTime", groupOrder.getWaitingTime());
		Community community = groupOrder.getCommunity();
		objectNode.put("community", community == null ? "" : community.getName());// 小区
		objectNode.put("groupOrderState", groupOrder.getGroupOrderState().getStates().getDesc());
		WeChatUser weChatUser = groupOrder.getWeChatUser();
		objectNode.put("openId", weChatUser.getOpenId());
		// 标签
		String labels = " ";// 此处空格不可去掉
		for (Label label : groupOrder.getLabels()) {
			labels = labels + label.getTag() + ",";
		}
		objectNode.put("labels", labels.substring(0, labels.length() - 1).trim());// 空格这里有用
		String path = Environment.getProperty("host") + Environment.getProperty("skin");
		ArrayNode groupOrderPicNodes = JsonUtils.createArrayNode();
		for (Annex annex : groupOrder.getAnnexs()) {
			ObjectNode on = JsonUtils.createObjectNode();
			on.put("annexId", annex.getId());
			on.put("annexFilePath", path + annex.getFilePath());
			groupOrderPicNodes.add(on);
		}
		// 默认图
		if (groupOrderPicNodes.size() <= 0) {
			String defaultPicPath = "/images/group_order_pic.png";
			groupOrderPicNodes.add(JsonUtils.createObjectNode().put("annexFilePath", path + defaultPicPath));
		}
		objectNode.set("groupOrderPics", groupOrderPicNodes);
		ArrayNode groupUserInfo = JsonUtils.createArrayNode();
		objectNode.put("remainingTime", 0);
		objectNode.set("groupUserInfo", groupUserInfo);// 已拼团用户信息
		objectNode.put("orderHeadImgUrls", "");
		objectNode.put("userHeadImgUrls", weChatUser.getHeadImgUrl());
		objectNode.put("userName", weChatUser.getName());
		// 商品信息
		ArrayNode commodityNodes = JsonUtils.createArrayNode();
		for (GroupCommodity groupCommodity : groupOrder.getGroupCommoditys()) {
			ObjectNode node = JsonUtils.createObjectNode();
			node.put("commodityId", groupCommodity.getId());
			node.put("name", groupCommodity.getName() == null ? "" : groupCommodity.getName());
			node.put("description", groupCommodity.getDescription() == null ? "" : groupCommodity.getDescription());
			node.put("details", groupCommodity.getDetails() == null ? "" : groupCommodity.getDetails());
			node.put("remnantInventory", groupCommodity.getRemnantInventory() == null ? 0 : groupCommodity.getRemnantInventory());
			node.put("buyNumber", 0);
			node.put("price", groupCommodity.getPrice() == null ? new BigDecimal("0") : groupCommodity.getPrice());
			if (groupCommodity.getUpperlimit() == 0 || groupCommodity.getUpperlimit() == null) {
				node.put("surplusUpperlimitNumber", "不限");
			} else {
				node.put("surplusUpperlimitNumber", groupCommodity.getUpperlimit());
			}
			ArrayNode groupCommodityPic = JsonUtils.createArrayNode();
			for (Annex annex : groupCommodity.getAnnexs()) {// 商品图
				ObjectNode on = JsonUtils.createObjectNode();
				on.put("annexFilePath", path + annex.getFilePath());
				groupCommodityPic.add(on);
			}
			if (groupCommodityPic.size() <= 0) {// 为空则使用默认图
				String defaultPicPath = "/images/group_commoditys_pic.png";
				groupCommodityPic.add(JsonUtils.createObjectNode().put("annexFilePath", path + defaultPicPath));
			}
			node.set("groupCommodityPic", groupCommodityPic);
			commodityNodes.add(node);
		}
		objectNode.set("groupCommoditys", commodityNodes);
		response.outputJson(0, objectNode);
	}
}