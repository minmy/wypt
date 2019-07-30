package cn.sini.cgb.api.cgb.action.group;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.sini.cgb.api.cgb.entity.verification.Business;
import cn.sini.cgb.api.cgb.entity.verification.BusinessTerminal;
import cn.sini.cgb.api.cgb.query.verification.BusinessTerminalQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.sini.cgb.admin.annex.entity.Annex;
import cn.sini.cgb.api.cgb.entity.group.Community;
import cn.sini.cgb.api.cgb.entity.group.GroupCommodity;
import cn.sini.cgb.api.cgb.entity.group.GroupOrder;
import cn.sini.cgb.api.cgb.entity.group.GroupOrder.ReviewStates;
import cn.sini.cgb.api.cgb.entity.group.GroupOrderState.States;
import cn.sini.cgb.api.cgb.entity.group.Label;
import cn.sini.cgb.api.cgb.entity.group.WeChatUser;
import cn.sini.cgb.api.cgb.query.group.CommunityQuery;
import cn.sini.cgb.api.cgb.query.group.GroupOrderQuery;
import cn.sini.cgb.api.cgb.query.group.GroupOrderStateQuery;
import cn.sini.cgb.api.cgb.query.group.WeChatUserQuery;
import cn.sini.cgb.common.http.HttpRequestWrapper;
import cn.sini.cgb.common.http.HttpResponseWrapper;
import cn.sini.cgb.common.json.JsonUtils;
import cn.sini.cgb.common.query.Page;
import cn.sini.cgb.common.util.DateTimeUtils;
import cn.sini.cgb.common.util.Environment;
import cn.sini.cgb.common.util.PayUtil;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 我想拼团——里长action
 *
 * @author qi
 */
@Controller
@RequestMapping("/api/precinctHead")
public class PrecinctHeadAction {

	/** 判断是否为里长和是否有核销权限*/
	@RequestMapping(value = "/isBrigadier", method = RequestMethod.POST)
	public void isBrigadier(HttpRequestWrapper request, HttpResponseWrapper response) {
		String openId = request.getStringMust("openId");
		WeChatUser weChatUser = new WeChatUserQuery().openId(openId).uniqueResult();
		if (weChatUser == null) {
			response.outputJson(-1, "对不起，当前所查询的用户不存在");
			return;
		}
		BusinessTerminal businessTerminal = new BusinessTerminalQuery().posId(openId).readOnly().uniqueResult();
		ObjectNode objectNode = JsonUtils.createObjectNode();
		objectNode.put("brigadier", weChatUser.getBrigadier());
		objectNode.put("canVerify", businessTerminal == null ? false : true);
		response.outputJson(0, objectNode);
	}

	/** 审核团单列表 */
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public void list(HttpRequestWrapper request, HttpResponseWrapper response) {
		String communityIds = request.getString("community");
		String reviewStates = request.getString("reviewStates");
		String releaseSort = request.getString("releaseSort");// 发布时间排序
		Integer beforeDate = request.getInteger("beforeDate"); // 按1,3,5,7,30天内的发布日期拼团筛选
		CommunityQuery communityQuery = null;
		if (StringUtils.isNotEmpty(communityIds)) {
			String[] arrCommunity = communityIds.split(",");
			Serializable[] communitys = new Long[arrCommunity.length];
			for (int i = 0; i < arrCommunity.length; i++) {
				communitys[i] = Long.valueOf(arrCommunity[i]);
			}
			communityQuery = new CommunityQuery();
			communityQuery.id(communitys);
		}
		ReviewStates[] reviewStatess = null;
		if (StringUtils.isNotEmpty(reviewStates)) {
			String[] arrReviewStates = reviewStates.split(",");
			reviewStatess = new ReviewStates[arrReviewStates.length];
			for (int i = 0; i < arrReviewStates.length; i++) {
				reviewStatess[i] = ReviewStates.valueOf(arrReviewStates[i]);
			}
		}
		WeChatUser weChatUser = new WeChatUserQuery().openId(request.getStringMust("openId")).readOnly().uniqueResult();
		if (weChatUser == null) {
			response.outputJson(-1, "对不起，当前所查询的用户不存在");
			return;
		}
		GroupOrderQuery groupOrderQuery = (GroupOrderQuery) new GroupOrderQuery().community(weChatUser.getCommunities().toArray(new Community[weChatUser.getCommunities().size()])).reviewStates(reviewStatess).communityQuery(communityQuery);
		if (beforeDate != null) {
			groupOrderQuery.releaseTimeGe(beforeDate * -1);
		}
		if ("up".equals(releaseSort)) {// 发布时间顺序
			groupOrderQuery.orderBy("releaseTime", true);
		} else {
			groupOrderQuery.orderBy("releaseTime", false);
		}
		Page<GroupOrder> page = groupOrderQuery.readOnly().pageHasCount(request.getPageNum(), request.getPageSize());
		ObjectNode objectNode = JsonUtils.createObjectNode();
		ArrayNode arrayNode = JsonUtils.createArrayNode();
		String path = Environment.getProperty("host") + Environment.getProperty("skin");
		for (GroupOrder groupOrder : page.getRecordList()) {
			ObjectNode node = JsonUtils.createObjectNode();
			node.put("name", groupOrder.getWeChatUser().getName());
			node.put("headImgUrl", groupOrder.getWeChatUser().getHeadImgUrl());
			node.put("groupOrderId", groupOrder.getId());
			node.put("theme", groupOrder.getTheme());
			node.put("reviewStates", groupOrder.getReviewStates() == null ? "" : groupOrder.getReviewStates().getDesc());
			node.put("reviewReason", groupOrder.getReviewReason() == null ? "" : groupOrder.getReviewReason());
			node.put("reviewTime", groupOrder.getReviewTime() == null ? "" : DateTimeUtils.format(groupOrder.getReviewTime(), "yyyy-MM-dd HH:mm:ss"));
			node.put("themeIntroduce", groupOrder.getThemeIntroduce());
			node.put("beginTime", DateTimeUtils.format(groupOrder.getBeginTime(), "MM-dd HH:mm"));
			node.put("releaseTime", groupOrder.getReleaseTime() == null ? "" : DateTimeUtils.format(groupOrder.getReleaseTime(), "MM-dd HH:mm"));
			node.put("endTime", DateTimeUtils.format(groupOrder.getEndTime(), "MM-dd HH:mm"));
			node.put("endTime2", DateTimeUtils.format(groupOrder.getEndTime(), "yyyy-MM-dd HH:mm:ss"));
			node.put("validOrder", groupOrder.getValidOrders());
			node.put("selfExtractingTime", DateTimeUtils.format(groupOrder.getSelfExtractingTime(), "MM-dd HH:mm"));
			node.put("selfExtractingTime2", DateTimeUtils.format(groupOrder.getSelfExtractingTime(), "yyyy-MM-dd HH:mm:ss"));
			node.put("browseVolume", groupOrder.getBrowseVolume());
			node.put("isDeliveryTime", groupOrder.getIsDeliveryTime());
			Community community = groupOrder.getCommunity();
			node.put("communityName", community == null ? "" : community.getName());
			node.put("townships", community == null ? "" : community.getTownship().getName());
			node.put("selfExtractingAddress", groupOrder.getSelfExtractingAddress());
			States states = groupOrder.getGroupOrderState().getStates();
			if (states == States.WCT) {
				node.put("cancelTime", groupOrder.getCancelTime() == null ? "" : DateTimeUtils.format(groupOrder.getCancelTime(), "MM-dd HH:mm"));
				node.put("cancelReason", groupOrder.getCancelReason() == null ? "" : groupOrder.getCancelReason());
			}
			if (states == States.JXZ || states == States.YJS) {
				node.put("allAmount", new PayUtil().getGroupPrice(weChatUser, groupOrder.getId()));// 所有金额
				node.put("payOrders", groupOrder.getPayOrders());// 已支付订单量
				if (states == States.YJS) {
					node.put("pickUpPics", groupOrder.getPickPicOrders());// 已上长传提货照
				}
			} else {
				if (states == States.DFB) {
					Boolean isRelease = groupOrder.isRelease();
					node.put("isRelease", isRelease);// 是否可发布
				}
				node.put("allAmount", 0);// 所有金额
				node.put("payOrders", 0);// 已支付订单量
			}
			ArrayNode groupOrderPics = JsonUtils.createArrayNode();
			for (Annex annex : groupOrder.getAnnexs()) {
				ObjectNode on = JsonUtils.createObjectNode();
				on.put("annexFilePath", path + annex.getFilePath());
				groupOrderPics.add(on);
			}
			if (groupOrderPics.size() <= 0) {
				String defaultPicPath = "/images/group_order_pic.png";
				groupOrderPics.add(JsonUtils.createObjectNode().put("annexFilePath", path + defaultPicPath));
			}
			Date date = new Date();
			long selfExtractingTime = groupOrder.getSelfExtractingTime().getTime();// 自提时间
			if (date.getTime() < selfExtractingTime) {
				long remainingTime = selfExtractingTime - date.getTime();
				// 计算天数 86400000 = (1000 * 60 * 60 * 24)
				long remainingDays = remainingTime / 86400000;
				// 计算小时 3600000 = (1000 * 60 * 60)
				long remainingHours = remainingTime / 3600000 % 24;
				// 计算分钟 60000 = (1000 * 60)
				long remainingMin = remainingTime / 60000 % 60;
				// 计算秒
				long remainingSecond = remainingTime / 1000 % 60;
				node.put("remainingDeliveryTime", remainingDays + "天" + remainingHours + "时" + remainingMin + "分" + remainingSecond + "秒后可发货");
			} else {
				node.put("remainingDeliveryTime", 0);
			}
			node.set("groupOrderPic", groupOrderPics);
			arrayNode.add(node);
		}
		// 小区
		ArrayNode communityArrayNode = JsonUtils.createArrayNode();
		for (Community comm : weChatUser.getCommunities()) {
			ObjectNode communityNode = JsonUtils.createObjectNode();
			communityNode.put("communityId", comm.getId());
			communityNode.put("communityName", comm.getName());
			communityArrayNode.add(communityNode);
		}
		objectNode.set("communityArray", communityArrayNode);
		objectNode.set("recordList", arrayNode);
		objectNode.put("totalPage", page.getTotalPage());
		objectNode.put("totalRecord", page.getTotalRecord());
		objectNode.put("pageNum", page.getPageNum());
		objectNode.put("pageSize", page.getPageSize());
		response.outputJson(0, objectNode);
	}

	/** 团单审核详情 */
	@Transactional
	@RequestMapping(value = "/details", method = RequestMethod.POST)
	public void details(HttpRequestWrapper request, HttpResponseWrapper response) {
		String type = request.getString("type");
		String openId = request.getStringMust("openId");
		Long groupOrderId = request.getLongMust("groupOrderId");
		WeChatUser weChatUser = new WeChatUserQuery().openId(openId).uniqueResult();
		if (weChatUser == null) {
			response.outputJson(-1, "对不起，当前所查询的用户不存在");
			return;
		}
		GroupOrder groupOrder = new GroupOrderQuery().community(weChatUser.getCommunities().toArray(new Community[weChatUser.getCommunities().size()])).id(groupOrderId).uniqueResult();
		if (groupOrder == null) {
			response.outputJson(-1, "对不起，您操作的拼团不存在");
			return;
		}
		if ("review".equals(type) && groupOrder.getReviewStates() == ReviewStates.DSH) {
			groupOrder.setReviewStates(ReviewStates.SHZ);
			groupOrder.setReviewTime(new Date());
			groupOrder.saveOrUpdate();
		}
		// 团单的商品信息
		String path = Environment.getProperty("host") + Environment.getProperty("skin");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		ObjectNode objectNode = JsonUtils.createObjectNode();
		ArrayNode annexArrayNode = JsonUtils.createArrayNode();
		objectNode.put("groupOrderId", groupOrder.getId().toString());
		objectNode.put("name", groupOrder.getWeChatUser().getName());
		objectNode.put("headImg", groupOrder.getWeChatUser().getHeadImgUrl());
		objectNode.put("theme", groupOrder.getTheme());
		objectNode.put("reviewStatus", groupOrder.getReviewStates() == null ? "" : groupOrder.getReviewStates().getDesc());
		objectNode.put("reviewReason", groupOrder.getReviewReason() == null ? "" : groupOrder.getReviewReason());
		objectNode.put("reviewTime", groupOrder.getReviewTime() == null ? "" : DateTimeUtils.format(groupOrder.getReviewTime(), "yyyy-MM-dd HH:mm:ss"));
		objectNode.put("themeIntroduce", groupOrder.getThemeIntroduce() == null ? "" : groupOrder.getThemeIntroduce());
		for (Annex annex : groupOrder.getAnnexs()) {
			ObjectNode on = JsonUtils.createObjectNode();
			on.put("annexId", annex.getId());
			on.put("annexFilePath", path + annex.getFilePath());
			annexArrayNode.add(on);
		}
		objectNode.set("groupOrderPics", annexArrayNode);
		objectNode.put("notice", groupOrder.getNotice() == null ? "" : groupOrder.getNotice());
		objectNode.put("beginTime", simpleDateFormat.format(groupOrder.getBeginTime()));
		objectNode.put("endTime", groupOrder.getEndTime() == null ? "" : simpleDateFormat.format(groupOrder.getEndTime()));
		objectNode.put("upperlimit", groupOrder.getUpperlimit());
		objectNode.put("selfExtractingTime", groupOrder.getSelfExtractingTime() == null ? "" : simpleDateFormat.format(groupOrder.getSelfExtractingTime()));
		objectNode.put("selfExtractingAddress", groupOrder.getSelfExtractingAddress());// 详细地址
		objectNode.put("phone", groupOrder.getPhone() == null ? "" : groupOrder.getPhone());
		objectNode.put("waitingTime", groupOrder.getWaitingTime());
		// 地址信息
		objectNode.put("province", groupOrder.getProvince() == null ? "" : groupOrder.getProvince());
		objectNode.put("city", groupOrder.getCity() == null ? "" : groupOrder.getCity());
		objectNode.put("community", groupOrder.getCommunity() == null ? "" : groupOrder.getCommunity().getName());
		objectNode.put("area", groupOrder.getTownships() == null ? "" : groupOrder.getCommunity().getTownship().getName());
		Community community = groupOrder.getCommunity();
		if (community == null) {
			objectNode.put("communityId", 0);
		} else {
			objectNode.put("communityId", community.getId());
		}
		ArrayNode labelArrayNode = JsonUtils.createArrayNode();
		for (Label label : groupOrder.getLabels()) {
			ObjectNode on = JsonUtils.createObjectNode();
			on.put("labelId", label.getId());
			on.put("labelTag", label.getTag());
			labelArrayNode.add(on);
		}
		objectNode.set("labels", labelArrayNode);
		ArrayNode commodityArrayNode = JsonUtils.createArrayNode();
		for (GroupCommodity groupCommodity : groupOrder.getGroupCommoditys()) {
			ObjectNode node = JsonUtils.createObjectNode();
			node.put("commodityId", groupCommodity.getId().toString());
			node.put("name", groupCommodity.getName());
			node.put("price", groupCommodity.getPrice());
			node.put("description", groupCommodity.getDescription() == null ? "" : groupCommodity.getDescription());
			node.put("totalInventory", groupCommodity.getTotalInventory());
			node.put("remnantInventory", groupCommodity.getRemnantInventory());
			node.put("commodityUpperlimit", groupCommodity.getUpperlimit());
			node.put("details", groupCommodity.getDetails() == null ? "" : groupCommodity.getDetails());
			ArrayNode commodityAnnexArrayNode = JsonUtils.createArrayNode();
			for (Annex annex : groupCommodity.getAnnexs()) {
				ObjectNode on = JsonUtils.createObjectNode();
				on.put("annexId", annex.getId());
				on.put("annexFilePath", path + annex.getFilePath());
				commodityAnnexArrayNode.add(on);
			}
			node.set("commodityPics", commodityAnnexArrayNode);
			commodityArrayNode.add(node);
		}
		objectNode.set("commodities", commodityArrayNode);// 多个商品
		response.outputJson(0, objectNode);
	}

	/** 团单审核 */
	@Transactional
	@RequestMapping(value = "/review", method = RequestMethod.POST)
	public void review(HttpRequestWrapper request, HttpResponseWrapper response) {
		WeChatUser weChatUser = new WeChatUserQuery().openId(request.getStringMust("openId")).uniqueResult();
		if (weChatUser == null) {
			response.outputJson(-1, "对不起，当前所查询的用户不存在");
			return;
		}
		GroupOrder groupOrder = new GroupOrderQuery().community(weChatUser.getCommunities().toArray(new Community[weChatUser.getCommunities().size()])).reviewStates(ReviewStates.DSH, ReviewStates.SHZ).id(request.getLongMust("groupOrderId")).uniqueResult();
		if (groupOrder == null) {
			response.outputJson(-1, "对不起，您操作的拼团不存在或已审核");
			return;
		}
		ReviewStates reviewStates = request.getEnumMust("reviewStates", ReviewStates.class);
		groupOrder.setReviewStates(reviewStates);
		if (reviewStates == ReviewStates.TG) {
			groupOrder.setGroupOrderState(new GroupOrderStateQuery().states(States.JXZ).readOnly().uniqueResult());
		}
		groupOrder.setReviewTime(new Date());
		groupOrder.setReviewReason(request.getString("reviewReason"));
		groupOrder.saveOrUpdate();
		ObjectNode objectNode = JsonUtils.createObjectNode();
		objectNode.put("id", groupOrder.getId());
		response.outputJson(0, objectNode);
	}
}