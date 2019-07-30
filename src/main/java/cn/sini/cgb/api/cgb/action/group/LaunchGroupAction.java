package cn.sini.cgb.api.cgb.action.group;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.VerticalAlignment;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.LockMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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
import cn.sini.cgb.api.cgb.entity.group.GroupOrder.ReviewStates;
import cn.sini.cgb.api.cgb.entity.group.GroupOrderState;
import cn.sini.cgb.api.cgb.entity.group.GroupOrderState.States;
import cn.sini.cgb.api.cgb.entity.group.Label;
import cn.sini.cgb.api.cgb.entity.group.Order;
import cn.sini.cgb.api.cgb.entity.group.Order.PayState;
import cn.sini.cgb.api.cgb.entity.group.OrderGoods;
import cn.sini.cgb.api.cgb.entity.group.OrderState.OrderStates;
import cn.sini.cgb.api.cgb.entity.group.Township;
import cn.sini.cgb.api.cgb.entity.group.WeChatUser;
import cn.sini.cgb.api.cgb.entity.group.WechatNotice;
import cn.sini.cgb.api.cgb.entity.group.WechatNotice.NoticeType;
import cn.sini.cgb.api.cgb.entity.pay.AllBill;
import cn.sini.cgb.api.cgb.entity.pay.AllBill.CashTypeEnum;
import cn.sini.cgb.api.cgb.query.group.CommunityQuery;
import cn.sini.cgb.api.cgb.query.group.GroupCommodityQuery;
import cn.sini.cgb.api.cgb.query.group.GroupOrderQuery;
import cn.sini.cgb.api.cgb.query.group.GroupOrderStateQuery;
import cn.sini.cgb.api.cgb.query.group.LabelQuery;
import cn.sini.cgb.api.cgb.query.group.OrderQuery;
import cn.sini.cgb.api.cgb.query.group.OrderStateQuery;
import cn.sini.cgb.api.cgb.query.group.ShareQuery;
import cn.sini.cgb.api.cgb.query.group.TownshipQuery;
import cn.sini.cgb.api.cgb.query.group.WeChatUserQuery;
import cn.sini.cgb.api.cgb.query.group.WechatNoticeQuery;
import cn.sini.cgb.api.cgb.query.pay.AllBillQuery;
import cn.sini.cgb.common.http.HttpRequestWrapper;
import cn.sini.cgb.common.http.HttpResponseWrapper;
import cn.sini.cgb.common.json.JsonUtils;
import cn.sini.cgb.common.json.JsonWrapper;
import cn.sini.cgb.common.query.Page;
import cn.sini.cgb.common.util.DateTimeUtils;
import cn.sini.cgb.common.util.Environment;
import cn.sini.cgb.common.util.PayUtil;
import cn.sini.cgb.common.util.QRCodeUtil;
import cn.sini.cgb.common.util.WechatUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

/**
 * 发起拼团action
 * 
 * @author gaowei
 */
@Controller
@RequestMapping("/api/launchGroup")
public class LaunchGroupAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(LaunchGroupAction.class);

	private static final String UP = "up";
	private static final String DROP = "drop";

	/** 发起的拼团列表 */
	@SuppressWarnings("static-access")
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public void list(HttpRequestWrapper request, HttpResponseWrapper response) {
		String openId = request.getString("openId");
		States groupOrderState = request.getEnum("groupOrderState", GroupOrderState.States.class);
		String releaseSort = request.getString("releaseSort");// 发布时间排序
		String salesColumeSort = request.getString("salesColumeSort");// 销量排序
		String browseVolumeSort = request.getString("browseVolumeSort");// 浏览量排序
		String communityIds = request.getString("community"); // 按所属小区筛选
		Integer beforeDate = request.getInteger("beforeDate"); // 按1,3,5,7,30天内的发布日期拼团筛选
		if (StringUtils.isEmpty(openId)) {
			response.outputJson(-1, "缺少openId参数");
			return;
		}
		if (groupOrderState == null) {
			response.outputJson(-1, "缺少groupOrderState参数");
			return;
		}
		// 小区
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
		WeChatUser weChatUser = new WeChatUserQuery().openId(openId).readOnly().uniqueResult();
		GroupOrderState goState = new GroupOrderStateQuery().states(groupOrderState).readOnly().uniqueResult();
		GroupOrderQuery groupOrderQuery = (GroupOrderQuery) new GroupOrderQuery().weChatUser(weChatUser).groupOrderState(goState);
		groupOrderQuery.communityQuery(communityQuery);
		if (beforeDate != null) {
			groupOrderQuery.releaseTimeGe(beforeDate * -1);
		}
		if (UP.equals(salesColumeSort)) {// 销量排序
			groupOrderQuery.orderBy("heatDegree", true);
		} else if (DROP.equals(salesColumeSort)) {
			groupOrderQuery.orderBy("heatDegree", false);
		}
		if (UP.equals(browseVolumeSort)) {// 浏览量排序
			groupOrderQuery.orderBy("browseVolume", true);
		} else if (DROP.equals(browseVolumeSort)) {
			groupOrderQuery.orderBy("browseVolume", false);
		}
		if (UP.equals(releaseSort)) {// 开团时间排序（创建时间）
			groupOrderQuery.orderBy("createTime", true);
		} else if (DROP.equals(releaseSort)) {
			groupOrderQuery.orderBy("createTime", false);
		} else {
			groupOrderQuery.orderBy("createTime", false);
		}
		Page<GroupOrder> page = groupOrderQuery.pageHasCount(request.getPageNum(), request.getPageSize());
		ObjectNode objectNode = JsonUtils.createObjectNode();
		ArrayNode arrayNode = JsonUtils.createArrayNode();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm");
		String path = Environment.getProperty("host") + Environment.getProperty("skin");
		String format = "yyyy-MM-dd HH:mm:ss";
		for (GroupOrder groupOrder : page.getRecordList()) {
			ObjectNode node = JsonUtils.createObjectNode();
			node.put("groupOrderId", groupOrder.getId());
			node.put("reviewStates", groupOrder.getReviewStates() == null ? "" : groupOrder.getReviewStates().getDesc());
			node.put("reviewReason", groupOrder.getReviewReason() == null ? "" : groupOrder.getReviewReason());
			node.put("reviewTime", groupOrder.getReviewTime() == null ? "" : DateTimeUtils.format(groupOrder.getReviewTime(), "yyyy-MM-dd HH:mm:ss"));
			node.put("theme", groupOrder.getTheme());
			node.put("themeIntroduce", groupOrder.getThemeIntroduce());
			node.put("beginTime", simpleDateFormat.format(groupOrder.getBeginTime()));
			node.put("releaseTime", groupOrder.getReleaseTime() == null ? "" : simpleDateFormat.format(groupOrder.getReleaseTime()));
			node.put("endTime", simpleDateFormat.format(groupOrder.getEndTime()));
			node.put("endTime2", DateTimeUtils.format(groupOrder.getEndTime(), format));
			node.put("validOrder", groupOrder.getValidOrders());
			node.put("selfExtractingTime", simpleDateFormat.format(groupOrder.getSelfExtractingTime()));
			node.put("selfExtractingTime2", DateTimeUtils.format(groupOrder.getSelfExtractingTime(), format));
			node.put("browseVolume", groupOrder.getBrowseVolume());
			node.put("isDeliveryTime", groupOrder.getIsDeliveryTime());
			Community community = groupOrder.getCommunity();
			node.put("communityName", community == null ? "" : community.getName());
			node.put("townships", community == null ? "" : community.getTownship().getName());
			node.put("selfExtractingAddress", groupOrder.getSelfExtractingAddress());
			node.put("groupType", groupOrder.getGroupType() == null ? "PTT" : groupOrder.getGroupType().toString());
			node.put("shareCoupon", groupOrder.getShareCoupon() == null ? false : groupOrder.getShareCoupon());

			States states = groupOrder.getGroupOrderState().getStates();
			if (states == States.WCT) {
				node.put("cancelTime", groupOrder.getCancelTime() == null ? "" : simpleDateFormat.format(groupOrder.getCancelTime()));
				node.put("cancelReason", groupOrder.getCancelReason() == null ? "" : groupOrder.getCancelReason());
			}
			if (states == States.JXZ || states == States.YJS) {
				node.put("allAmount", new PayUtil().getGroupPrice(weChatUser, groupOrder.getId()));// 所有金额
				node.put("payOrders", groupOrder.getPayOrders());// 已支付订单量
				if (states == States.YJS) {
					node.put("pickUpPics", groupOrder.getPickPicOrders());// 已上长传提货照
				}
				// 分享记录
				String sql = "select s.FK_WECHAT_USER as weChatUser, count(s.FK_WECHAT_USER) as count, MAX(s.CREATE_TIME) createTime from T_SHARE s where s.FK_GROUP_ORDER = ? group by s.FK_WECHAT_USER";
				List<Map<String, Object>> shareMapList = new ShareQuery().queryMapListBySql(sql, new Object[] { groupOrder.getId() });
				Integer shareCountNumber = 0;// 分享总记录
				ArrayNode shareArrayNode = JsonUtils.createArrayNode();
				for (Map<String, Object> map : shareMapList) {
					Long weChatUserId = Long.parseLong(map.get("weChatUser").toString());
					WeChatUser user = new WeChatUserQuery().id(weChatUserId).uniqueResult();
					ObjectNode shareNode = JsonUtils.createObjectNode();
					shareNode.put("weChatUser", user.getName());
					shareNode.put("headImgUrl", user.getHeadImgUrl());
					String createTime = map.get("createTime").toString();
					shareNode.put("createTime", createTime.substring(0, createTime.length() - 2));// 去掉后多余的.0
					Integer shareNumber = Integer.parseInt(map.get("count").toString());
					shareNode.put("shareNumber", shareNumber);
					shareArrayNode.add(shareNode);
					shareCountNumber += shareNumber;
				}
				node.set("shareArray", shareArrayNode);
				node.put("shareCountNumber", shareCountNumber);
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
		List<Community> communityList = new GroupOrderQuery().weChatUser(weChatUser).groupOrderState(goState).groupBy("community").list();
		ArrayNode communityArrayNode = JsonUtils.createArrayNode();
		for (Community comm : communityList) {
			// 过滤没有填写小区的团单
			if (comm == null) {
				continue;
			}
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

	/** 查询所有标签集合 */
	@RequestMapping(value = "/getLabelList", method = RequestMethod.POST)
	public void getLabelList(HttpRequestWrapper request, HttpResponseWrapper response) {
		List<Label> labelList = new LabelQuery().superLabelIsNull().orderBy("sort", true).readOnly().list();
		ObjectNode objectNode = JsonUtils.createObjectNode();
		ArrayNode labelArrayNode = JsonUtils.createArrayNode();
		for (Label label : labelList) {
			ObjectNode node = JsonUtils.createObjectNode();
			node.put("id", label.getId());
			node.put("tag", label.getTag());
			node.put("desc", label.getDesc_() == null ? "" : label.getDesc_());
			ArrayNode subLabelArrayNode = JsonUtils.createArrayNode();
			for (Label subLabel : label.getSubLabels()) {
				ObjectNode no = JsonUtils.createObjectNode();
				no.put("id", subLabel.getId());
				no.put("tag", subLabel.getTag());
				no.put("desc", subLabel.getDesc_() == null ? "" : subLabel.getDesc_());
				subLabelArrayNode.add(no);
			}
			node.set("subLabels", subLabelArrayNode);
			labelArrayNode.add(node);
		}
		objectNode.set("labels", labelArrayNode);
		response.outputJson(0, objectNode);
	}

	/** 保存团单 */
	@Transactional
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public void save(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
		Long groupOrderId = request.getLong("groupOrderId");
		String openId = request.getString("openId");
		String theme = request.getString("theme", 255);// 主题
		States groupOrderState = request.getEnum("groupOrderState", GroupOrderState.States.class);// 团单状态
		String themeIntroduce = request.getString("themeIntroduce", 255);// 主题介绍
		String groupOrderPics = request.getString("groupOrderPics");// 拼团图片
		String groupOrderPics2 = request.getString("groupOrderPics2");// 另开一团的拼团图片
		String selfExtractingAddress = request.getString("selfExtractingAddress", 255);// 自提地址(详细地址)
		Date selfExtractingTime = request.getDate("selfExtractingTime", "yyyy-MM-dd HH:mm:ss");// 自提时间
		String phone = request.getTrim("phone", "^1\\d{10}$");
		String label = request.getString("label");// 标签字符串
		List<Label> labelList = null;
		if (label != null && label != "") {
			String[] split = label.split(",");
			Serializable[] labels = new Serializable[split.length];// 标签数组
			for (int i = 0; i < split.length; i++) {
				labels[i] = Long.parseLong(split[i]);
			}
			if (labels != null && labels.length > 0) {
				labelList = new LabelQuery().id(labels).list();
			}
		}
		String notice = request.getString("notice", 255);// 拼团须知
		Date beginTime = request.getDate("beginTime", "yyyy-MM-dd HH:mm:ss");// 开始时间
		Date endTime = request.getDate("endTime", "yyyy-MM-dd HH:mm:ss");// 结束时间
		// 商品券信息
		Boolean shareCoupon = request.getBoolean("shareCoupon");// 普通团分享券的开关。false是关，true是开
		Integer shareNumber = request.getInteger("shareNumber");// 用戶分享所获取的数量
		Integer invitedNumber = request.getInteger("invitedNumber");// 被分享用戶所获取的分享券数量（被分享用户点击分享所获得的分享券）
		Integer shareSuccessNumber = request.getInteger("shareSuccessNumber");// 成功邀请用户消费获取的分享券数量
		Integer successInvitedNumber = request.getInteger("successInvitedNumber");// 用户通过邀请完成消费获得分享券数
		Integer discountNumber = request.getInteger("discountNumber");// 分享优惠阀值
		String shareCouponInstructions = request.getString("shareCouponInstructions");// 分享券使用说明
		
		//先屏蔽
		invitedNumber=0;
		successInvitedNumber=0;
		
		// 拼团商品
		String commodities = request.getString("commodities");
		ArrayNode arrayNode = JsonUtils.toArrayNode(commodities);
		// 地址信息
		String province = request.getString("province");// 省
		String city = request.getString("city");// 市
		String area = request.getString("area");// 区
		Long communityId = request.getLong("communityId");// 社区名称
		// 等待时间
		Integer waitingTime = request.getInteger("waitingTime");

		if (groupOrderId != null && groupOrderId == 0) {
			response.outputJson(-1, "groupOrderId参数格式错误");
			return;
		}
		if (StringUtils.isEmpty(openId)) {
			response.outputJson(-1, "缺少openId参数");
			return;
		}
		if (StringUtils.isEmpty(theme)) {
			response.outputJson(-1, "缺少theme参数");
			return;
		}
		if (groupOrderState == null) {
			response.outputJson(-1, "缺少groupOrderState参数");
			return;
		}
		if (groupOrderState == GroupOrderState.States.JXZ) {
			if (StringUtils.isEmpty(themeIntroduce)) {
				response.outputJson(-1, "缺少themeIntroduce参数");
				return;
			}
			if (StringUtils.isEmpty(selfExtractingAddress)) {
				response.outputJson(-1, "缺少selfExtractingAddress参数");
				return;
			}
			if (selfExtractingTime == null) {
				response.outputJson(-1, "缺少selfExtractingTime参数");
				return;
			}
			if (StringUtils.isEmpty(notice)) {
				response.outputJson(-1, "缺少notice参数");
				return;
			}
			if (beginTime == null) {
				response.outputJson(-1, "缺少beginTime参数");
				return;
			}
			if (endTime == null) {
				response.outputJson(-1, "缺少endTime参数");
				return;
			}
			if (selfExtractingTime.getTime() <= endTime.getTime()) {
				response.outputJson(-1, "自提时间必须大于结束时间");
				return;
			}
			if (StringUtils.isEmpty(phone)) {
				response.outputJson(-1, "缺少phone参数");
				return;
			}
			if (arrayNode == null) {
				response.outputJson(-1, "请至少添加一个拼团商品");
				return;
			}
			if (StringUtils.isBlank(province)) {
				response.outputJson(-1, "缺少province参数");
				return;
			}
			if (StringUtils.isBlank(city)) {
				response.outputJson(-1, "缺少city参数");
				return;
			}
			if (StringUtils.isBlank(area)) {
				response.outputJson(-1, "缺少area参数");
				return;
			}
			if (communityId == null || communityId == 0L) {
				response.outputJson(-1, "缺少communityId参数");
				return;
			}
			if (shareCoupon == null) {
				response.outputJson(-1, "缺少shareCoupon参数");
				return;
			}
			if (shareCoupon) {
				if (shareNumber == null || invitedNumber == null || shareSuccessNumber == null || successInvitedNumber == null || discountNumber == null || StringUtils.isBlank(shareCouponInstructions)) {
					response.outputJson(-1, "缺少分享券的相关参数");
					return;
				}
			}
			for (JsonNode jsonNode : arrayNode) {
				JsonWrapper jw = new JsonWrapper(jsonNode);
				Long commodityId = jw.getLong("commodityId");// 商品Id
				String name = jw.getString("name", 255);// 商品名称
				BigDecimal price = jw.getBigDecimal("price", null, null, 6, 2);// 售价
				Integer inventory = jw.getInteger("inventory", 0, 99999999);// 库存数量
				Integer commodityUpperLimit = jw.getInteger("commodityUpperLimit", 0, 99999999);// 商品购买上限
				if (commodityId != null && commodityId == 0) {
					response.outputJson(-1, "commodityId参数格式错误");
					return;
				}
				if (StringUtils.isEmpty(name)) {
					response.outputJson(-1, "缺少name参数");
					return;
				}
				if (price == null || price.compareTo(BigDecimal.ZERO) == 0) {
					response.outputJson(-1, "缺少price参数");
					return;
				}
				if (inventory == null || inventory == 0) {
					response.outputJson(-1, "缺少inventory参数");
					return;
				}
				if (commodityUpperLimit == null) {
					response.outputJson(-1, "缺少commodityUpperLimit参数");
					return;
				}
				if (commodityUpperLimit > inventory) {
					response.outputJson(-1, "商品购买上限不能大于库存数量");
					return;
				}
			}
		}
		WeChatUser weChatUser = new WeChatUserQuery().openId(openId).uniqueResult();
		if (weChatUser == null) {
			response.outputJson(-1, "保存失败，未找到该用户");
			return;
		}
		Community community = null;
		if (communityId != null && communityId != 0L) {
			community = new CommunityQuery().townshipQuery(new TownshipQuery().code(area)).id(communityId).uniqueResult();
			if (community == null) {
				response.outputJson(-1, "保存失败，未找到该小区");
				return;
			}
		}
		// 团单信息保存
		GroupOrderState state = new GroupOrderStateQuery().states(States.DFB).uniqueResult();
		GroupOrder groupOrder = null;
		if (groupOrderId == null) {
			groupOrder = new GroupOrder();
		} else {
			groupOrder = new GroupOrderQuery().weChatUser(weChatUser).id(groupOrderId).uniqueResult();
			// 团单图片
			List<Annex> list = new AnnexQuery().groupOrder(groupOrder).list();
			for (Annex annex : list) {
				annex.setGroupOrder(null);// 修改时，防止用户删除图片却任然没有变
				annex.saveOrUpdate();
			}
		}
		// 标签
		groupOrder.getLabels().removeAll(new LabelQuery().list());
		if (labelList != null) {
			groupOrder.getLabels().addAll(labelList);
		}
		groupOrder.setTheme(theme);
		groupOrder.setThemeIntroduce(themeIntroduce);
		groupOrder.setGroupOrderState(state);
		groupOrder.setSelfExtractingAddress(selfExtractingAddress);
		groupOrder.setSelfExtractingTime(selfExtractingTime);
		groupOrder.setNotice(notice);
		groupOrder.setBeginTime(beginTime);
		groupOrder.setEndTime(endTime);
		groupOrder.setWeChatUser(weChatUser);
		groupOrder.setWaitingTime(waitingTime == null ? null : waitingTime);
		if (groupOrderState == GroupOrderState.States.JXZ) {
			groupOrder.setReleaseTime(new Date());
			groupOrder.setReviewStates(ReviewStates.DSH);
		}
		groupOrder.setProvince(province);
		groupOrder.setCity(city);
		groupOrder.setTownships(area);
		groupOrder.setCommunity(community);
		groupOrder.setPhone(phone);
		groupOrder.setGroupType(GroupType.PTT);
		groupOrder.setShareCoupon(shareCoupon);
		groupOrder.setShareNumber(shareNumber);
		groupOrder.setInvitedNumber(invitedNumber);
		groupOrder.setShareSuccessNumber(shareSuccessNumber);
		groupOrder.setSuccessInvitedNumber(successInvitedNumber);
		groupOrder.setDiscountNumber(discountNumber);
		groupOrder.setShareCouponInstructions(shareCouponInstructions);
		groupOrder.saveOrUpdate();
		// 团单图片
		if (StringUtils.isNotBlank(groupOrderPics)) {
			Serializable[] pics = groupOrderPics.split(",");
			List<Annex> groupOrderAnnexList = new AnnexQuery().id(pics).list();
			for (Annex annex : groupOrderAnnexList) {
				annex.setGroupOrder(groupOrder);
				annex.saveOrUpdate();
			}
		}
		// 另开一团的图片
		if (StringUtils.isNotBlank(groupOrderPics2)) {
			Serializable[] ids = groupOrderPics2.split(",");
			// 复制
			List<Annex> annexList = new AnnexQuery().id(ids).orderBy("sort", true).list();
			Long i = 1L;
			for (Annex annex : annexList) {
				Annex ann = new Annex();
				ann.setAnnexType(annex.getAnnexType());
				ann.setFileName(annex.getFileName());
				ann.setFilePath(annex.getFilePath());
				ann.setSort(i);
				ann.setGroupOrder(groupOrder);
				ann.saveOrUpdate();
				++i;
			}
		}
		if (arrayNode != null) {
			// 团单商品信息保存
			for (JsonNode jsonNode : arrayNode) {
				JsonWrapper jw = new JsonWrapper(jsonNode);
				Long commodityId = jw.getLong("commodityId");// 商品ID
				String name = jw.getString("name", 255);// 商品名称
				BigDecimal price = jw.getBigDecimal("price", null, null, 6, 2);// 售价
				String description = jw.getString("description", 255);// 规格说明
				Integer inventory = jw.getInteger("inventory");// 库存数量
				String details = jw.getString("details", 255);// 商品详情
				Integer commodityUpperLimit = jw.getInteger("commodityUpperLimit");// 商品购买上限
				String commodityPics = jw.getString("commodityPics");// 商品图片ID
				String commodityPics2 = jw.getString("commodityPics2");// 另开一团的商品图片
				Integer sort = jw.getInteger("sort");// 商品排序
				GroupCommodity groupCommodity = null;
				if (commodityId == null) {
					groupCommodity = new GroupCommodity();
				} else {
					groupCommodity = new GroupCommodityQuery().groupOrder(groupOrder).id(commodityId).uniqueResult();
					// 商品图片
					List<Annex> annexList = new AnnexQuery().groupCommodity(groupCommodity).list();
					for (Annex annex : annexList) {
						annex.setGroupCommodity(null);
						annex.saveOrUpdate();
					}
				}
				groupCommodity.setPrice(price);
				groupCommodity.setName(name);
				groupCommodity.setDescription(description);
				groupCommodity.setTotalInventory(inventory);
				groupCommodity.setRemnantInventory(inventory);
				groupCommodity.setDetails(details);
				groupCommodity.setGroupOrder(groupOrder);
				groupCommodity.setUpperlimit(commodityUpperLimit);
				groupCommodity.setSort(sort);
				groupCommodity.saveOrUpdate();
				// 商品图片
				if (StringUtils.isNotBlank(commodityPics)) {
					Serializable[] groupCommodityPics = commodityPics.split(",");
					List<Annex> groupCommodityAnnexList = new AnnexQuery().id(groupCommodityPics).list();
					for (Annex annex : groupCommodityAnnexList) {
						annex.setGroupCommodity(groupCommodity);
						annex.saveOrUpdate();
					}
				}
				// 另开一团的图片
				if (StringUtils.isNotBlank(commodityPics2)) {
					Serializable[] ids = commodityPics2.split(",");
					// 复制
					List<Annex> commodityAnnexList = new AnnexQuery().id(ids).orderBy("sort", true).list();
					Long j = 0L;
					for (Annex annex : commodityAnnexList) {
						Annex ann = new Annex();
						ann.setAnnexType(annex.getAnnexType());
						ann.setFileName(annex.getFileName());
						ann.setFilePath(annex.getFilePath());
						ann.setSort(j);
						ann.setGroupCommodity(groupCommodity);
						ann.saveOrUpdate();
						++j;
					}
				}
			}
		}
		response.outputJson(0, JsonUtils.createObjectNode().put("groupOrderId", groupOrder.getId()));
	}

	/** 根据ID查询团拼(与再组团复用) */
	@Transactional
	@RequestMapping(value = "/getById", method = RequestMethod.POST)
	public void getById(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
		String openId = request.getString("openId");
		Long groupOrderId = request.getLong("groupOrderId");// 团单ID
		Boolean isAgain = request.getBoolean("isAgain");// 是否再来一单
		if (StringUtils.isEmpty(openId)) {
			response.outputJson(-1, "缺少openId参数");
			return;
		}
		if (groupOrderId == null || groupOrderId == 0) {
			response.outputJson(-1, "缺少groupOrderId参数");
			return;
		}
		if (isAgain == null) {
			response.outputJson(-1, "缺少isAgain参数");
			return;
		}
		WeChatUser weChatUser = new WeChatUserQuery().openId(openId).uniqueResult();
		if (weChatUser == null) {
			response.outputJson(-1, "对不起，查询失败，未找到该用户");
			return;
		}
		GroupOrder groupOrder = new GroupOrderQuery().weChatUser(weChatUser).id(groupOrderId).uniqueResult();
		if (groupOrder == null) {
			response.outputJson(-1, "对不起，您查看的拼团不存在或已下架");
			return;
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		ObjectNode objectNode = JsonUtils.createObjectNode();
		ArrayNode annexArrayNode = JsonUtils.createArrayNode();
		objectNode.put("groupOrderId", isAgain == true ? "" : groupOrder.getId().toString());
		objectNode.put("theme", groupOrder.getTheme());
		objectNode.put("themeIntroduce", groupOrder.getThemeIntroduce() == null ? "" : groupOrder.getThemeIntroduce());
		String path = Environment.getProperty("host") + Environment.getProperty("skin");
		Long i = 1L;
		for (Annex annex : groupOrder.getAnnexs()) {
			ObjectNode on = JsonUtils.createObjectNode();
			if (isAgain) {
				Annex an = new Annex();
				an.setAnnexType(annex.getAnnexType());
				an.setFileName(annex.getFileName());
				an.setFilePath(annex.getFilePath());
				an.setSort(i);
				an.saveOrUpdate();
				on.put("annexId", an.getId());
				on.put("annexFilePath", path + an.getFilePath());
				++i;
			} else {
				on.put("annexId", annex.getId());
				on.put("annexFilePath", path + annex.getFilePath());
			}
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
		// 分享券信息
		objectNode.put("shareCoupon", groupOrder.getShareCoupon() == null ? false : groupOrder.getShareCoupon());
		objectNode.put("shareNumber", groupOrder.getShareNumber() == null ? 0 : groupOrder.getShareNumber());
		objectNode.put("invitedNumber", groupOrder.getInvitedNumber() == null ? 0 : groupOrder.getInvitedNumber());
		objectNode.put("shareSuccessNumber", groupOrder.getShareSuccessNumber() == null ? 0 : groupOrder.getShareSuccessNumber());
		objectNode.put("successInvitedNumber", groupOrder.getSuccessInvitedNumber() == null ? 0 : groupOrder.getSuccessInvitedNumber());
		objectNode.put("discountNumber", groupOrder.getDiscountNumber() == null ? 0 : groupOrder.getDiscountNumber());
		objectNode.put("shareCouponInstructions", groupOrder.getShareCouponInstructions() == null ? "" : groupOrder.getShareCouponInstructions());
		// 地址信息
		objectNode.put("province", groupOrder.getProvince() == null ? "" : groupOrder.getProvince());
		objectNode.put("city", groupOrder.getCity() == null ? "" : groupOrder.getCity());
		objectNode.put("area", groupOrder.getTownships() == null ? "" : groupOrder.getTownships());
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
			node.put("commodityId", isAgain == true ? "" : groupCommodity.getId().toString());
			node.put("name", groupCommodity.getName());
			node.put("price", groupCommodity.getPrice());
			node.put("description", groupCommodity.getDescription() == null ? "" : groupCommodity.getDescription());
			node.put("totalInventory", groupCommodity.getTotalInventory());
			node.put("remnantInventory", groupCommodity.getRemnantInventory());
			node.put("commodityUpperlimit", groupCommodity.getUpperlimit());
			node.put("details", groupCommodity.getDetails() == null ? "" : groupCommodity.getDetails());
			ArrayNode commodityAnnexArrayNode = JsonUtils.createArrayNode();
			i = 1L;
			for (Annex annex : groupCommodity.getAnnexs()) {
				ObjectNode on = JsonUtils.createObjectNode();
				if (isAgain) {
					Annex an = new Annex();
					an.setAnnexType(annex.getAnnexType());
					an.setFileName(annex.getFileName());
					an.setFilePath(annex.getFilePath());
					an.setSort(i);
					an.saveOrUpdate();
					on.put("annexId", an.getId());
					on.put("annexFilePath", path + an.getFilePath());
					++i;
				} else {
					on.put("annexId", annex.getId());
					on.put("annexFilePath", path + annex.getFilePath());
				}
				commodityAnnexArrayNode.add(on);
			}
			node.set("commodityPics", commodityAnnexArrayNode);
			commodityArrayNode.add(node);
		}
		objectNode.set("commodities", commodityArrayNode);// 多个商品
		response.outputJson(0, objectNode);
	}

	/** 删除商品 */
	@Transactional
	@RequestMapping(value = "/deleteCommodity", method = RequestMethod.POST)
	public void deleteCommodity(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
		String openId = request.getString("openId");
		Long groupOrderId = request.getLong("groupOrderId");// 团单ID
		Long commodityId = request.getLong("commodityId");// 商品ID
		if (StringUtils.isEmpty(openId)) {
			response.outputJson(-1, "缺少openId参数");
			return;
		}
		if (groupOrderId == null || groupOrderId == 0) {
			response.outputJson(-1, "缺少groupOrderId参数");
			return;
		}
		if (commodityId == null || commodityId == 0) {
			response.outputJson(-1, "缺少commodityId参数");
			return;
		}
		WeChatUser weChatUser = new WeChatUserQuery().openId(openId).uniqueResult();
		if (weChatUser == null) {
			response.outputJson(-1, "删除失败，未找到该用户");
			return;
		}
		GroupOrder groupOrder = new GroupOrderQuery().weChatUser(weChatUser).id(groupOrderId).uniqueResult();
		if (groupOrder == null) {
			response.outputJson(-1, "删除失败，未找到该拼团");
			return;
		}
		GroupCommodity groupCommodity = new GroupCommodityQuery().groupOrder(groupOrder).id(commodityId).uniqueResult();
		if (groupCommodity == null) {
			response.outputJson(-1, "删除失败，未找到该商品");
			return;
		}
		groupCommodity.remove();
		groupCommodity.saveOrUpdate();
		Set<Annex> annexs = groupCommodity.getAnnexs();
		for (Annex annex : annexs) {
			annex.remove();
		}
		response.outputJson(0, "删除成功");
	}

	/** 删除团单 */
	@Transactional
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public void delete(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
		String openId = request.getString("openId");
		Long groupOrderId = request.getLong("groupOrderId");// 团单ID
		if (StringUtils.isEmpty(openId)) {
			response.outputJson(-1, "缺少openId参数");
			return;
		}
		if (groupOrderId == null || groupOrderId == 0) {
			response.outputJson(-1, "缺少groupOrderId参数");
			return;
		}
		WeChatUser weChatUser = new WeChatUserQuery().openId(openId).uniqueResult();
		if (weChatUser == null) {
			response.outputJson(-1, "删除失败，未找到该用户");
			return;
		}
		GroupOrder groupOrder = new GroupOrderQuery().weChatUser(weChatUser).id(groupOrderId).uniqueResult();
		if (groupOrder == null) {
			response.outputJson(-1, "删除失败，未找到该拼团");
			return;
		}
		if (groupOrder.getGroupOrderState().getStates() == States.DFB) {
			Set<GroupCommodity> groupCommoditys = groupOrder.getGroupCommoditys();
			for (GroupCommodity groupCommodity : groupCommoditys) {
				for (Annex annex : groupCommodity.getAnnexs()) {
					annex.remove();
				}
				groupCommodity.remove();
			}
			for (Annex annex : groupOrder.getAnnexs()) {
				annex.remove();
			}
			groupOrder.remove();
		} else {
			response.outputJson(-1, "删除失败，该团单无法删除");
			return;
		}
		response.outputJson(0, "删除成功");
	}

	/** 团单发布按钮（普通团和企业团） */
	@Transactional
	@RequestMapping(value = "/release", method = RequestMethod.POST)
	public void release(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
		String openId = request.getString("openId");
		Long groupOrderId = request.getLong("groupOrderId");// 团单ID
		if (StringUtils.isEmpty(openId)) {
			response.outputJson(-1, "缺少openId参数");
			return;
		}
		if (groupOrderId == null || groupOrderId == 0) {
			response.outputJson(-1, "缺少groupOrderId参数");
			return;
		}
		WeChatUser weChatUser = new WeChatUserQuery().openId(openId).uniqueResult();
		if (weChatUser == null) {
			response.outputJson(-1, "发布失败，未找到该用户");
			return;
		}
		GroupOrderState groupOrderState = new GroupOrderStateQuery().states(States.DFB).uniqueResult();
		GroupOrder groupOrder = new GroupOrderQuery().weChatUser(weChatUser).groupOrderState(groupOrderState).id(groupOrderId).uniqueResult();
		if (groupOrder == null) {
			response.outputJson(-1, "对不起，该拼团不存在或已下架");
			return;
		}
		Boolean isRelease = null;
		if (groupOrder.getGroupType() == GroupType.PTT || groupOrder.getGroupType() == null) {
			isRelease = groupOrder.isRelease();
		} else {
			isRelease = groupOrder.isEnterpriseRelease();
		}
		if (!isRelease) {
			response.outputJson(-1, "对不起，请完整填写拼团信息");
			return;
		}
		groupOrder.setReleaseTime(new Date());
		groupOrder.setReviewStates(groupOrder.getGroupType() == GroupType.QYT ? null : ReviewStates.DSH);
		if (groupOrder.getGroupType() == GroupType.QYT) {
			GroupOrderState gos = new GroupOrderStateQuery().states(States.JXZ).uniqueResult();
			groupOrder.setGroupOrderState(gos);
		}
		groupOrder.setReviewReason(null);
		groupOrder.setReviewTime(null);
		groupOrder.saveOrUpdate();
		response.outputJson(0, "发布成功");
	}

	/** 团单取消按钮(取消拼团按钮) */
	@SuppressWarnings("deprecation")
	@Transactional
	@RequestMapping(value = "/endImmediately", method = RequestMethod.POST)
	public void endImmediately(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
		String openId = request.getString("openId");
		String formId = request.getString("formId");
		Long groupOrderId = request.getLong("groupOrderId");// 团单ID
		String cancelReason = request.getString("cancelReason", 255);// 取消原因
		if (StringUtils.isEmpty(openId)) {
			response.outputJson(-1, "缺少openId参数");
			return;
		}
		if (StringUtils.isEmpty(formId)) {
			response.outputJson(-1, "缺少formId参数");
			return;
		}
		if (groupOrderId == null || groupOrderId == 0) {
			response.outputJson(-1, "缺少groupOrderId参数");
			return;
		}
		if (StringUtils.isEmpty(cancelReason)) {
			response.outputJson(-1, "缺少cancelReason参数");
			return;
		}
		WeChatUser weChatUser = new WeChatUserQuery().openId(openId).uniqueResult();
		if (weChatUser == null) {
			response.outputJson(-1, "对不起，当前所查询的用户不存在");
			return;
		}
		// 锁行
		GroupOrder groupOrder = new GroupOrderQuery().weChatUser(weChatUser).groupOrderStateQuery(new GroupOrderStateQuery().states(States.JXZ)).id(groupOrderId).lockMode(LockMode.UPGRADE).uniqueResult();
		if (groupOrder == null) {
			response.outputJson(-1, "对不起，您查询的拼团不存在或已下架");
			return;
		}
		Date date = new Date();
		groupOrder.setCancelTime(date);
		groupOrder.setCancelReason(cancelReason);
		groupOrder.setGroupOrderState(new GroupOrderStateQuery().states(States.WCT).uniqueResult());
		groupOrder.saveOrUpdate();
		String cancel = Environment.getProperty("wechat.notice.cancel");// 模版ID
		// 取消所有订单，已付款订单则改为待退款状态，由定时器扫描发起退款，并发送取消通知
		for (Order order : groupOrder.getOrders()) {
			// 锁行
			order = new OrderQuery().id(order.getId()).lockMode(LockMode.UPGRADE).uniqueResult();
			OrderStates orderStates = order.getOrderState().getOrderStates();
			if ((orderStates == OrderStates.DFK && order.getPayState() == PayState.DZF) || (orderStates == OrderStates.DSH && order.getPayState() == PayState.YZF)) {
				if (orderStates == OrderStates.DSH && order.getPayState() == PayState.YZF) {
					order.setPayState(PayState.DTK);
					// 发送取消拼团通知
					ObjectNode templateJson = JsonUtils.createObjectNode();
					templateJson.set("keyword1", JsonUtils.createObjectNode().put("value", order.getOrderNumber()));// 订单编号
					templateJson.set("keyword2", JsonUtils.createObjectNode().put("value", groupOrder.getTheme()));// 活动名称
					String commodityName = "";
					for (OrderGoods orderGoods : order.getOrderGoods()) {
						commodityName = orderGoods.getGroupCommodity().getName();
						commodityName = order.getOrderGoods().size() > 1 ? commodityName + "等" : commodityName;
						break;
					}
					templateJson.set("keyword3", JsonUtils.createObjectNode().put("value", commodityName));// 商品名称
					templateJson.set("keyword4", JsonUtils.createObjectNode().put("value", "¥" + String.valueOf(order.getFinalPayment())));// 订单金额
					templateJson.set("keyword5", JsonUtils.createObjectNode().put("value", DateTimeUtils.format(order.getCreateTime(), "yyyy-MM-dd HH:mm:ss")));// 下单时间
					WechatUtils.sendTemplateMessage(groupOrder.getId(), openId, order.getPrepayId(), order.getWeChatUser().getOpenId(), templateJson, cancel, NoticeType.CANCEL_GROUP);
				}
				order.setCancelTime(date);
				order.setOrderState(new OrderStateQuery().orderStates(OrderStates.YQX).uniqueResult());
				order.setCancelReason(cancelReason);
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
			}
		}
		List<AllBill> allBills = new AllBillQuery().groupId(groupOrder.getId().toString()).lockMode(LockMode.UPGRADE).list();
		for (AllBill allBill : allBills) {
			allBill.setFlag(CashTypeEnum.QX);
			allBill.saveOrUpdate();
		}
		response.outputJson(0, "取消成功");
	}

	/** 团长发货 */
	@SuppressWarnings("deprecation")
	@Transactional
	@RequestMapping(value = "/sendGoods", method = RequestMethod.POST)
	public void sendGoods(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
		String openId = request.getString("openId");
		Long groupOrderId = request.getLong("groupOrderId");// 团单ID
		if (StringUtils.isEmpty(openId)) {
			response.outputJson(-1, "缺少openId参数");
			return;
		}
		if (groupOrderId == null || groupOrderId == 0) {
			response.outputJson(-1, "缺少groupOrderId参数");
			return;
		}
		WeChatUser weChatUser = new WeChatUserQuery().openId(openId).uniqueResult();
		if (weChatUser == null) {
			response.outputJson(-1, "对不起，当前所查询的用户不存在");
			return;
		}
		GroupOrderState groupOrderState = new GroupOrderStateQuery().states(States.JXZ).uniqueResult();
		// 锁行
		GroupOrder groupOrder = new GroupOrderQuery().weChatUser(weChatUser).groupOrderState(groupOrderState).id(groupOrderId).lockMode(LockMode.UPGRADE).uniqueResult();
		if (groupOrder == null) {
			response.outputJson(-1, "对不起，您操作的拼团不存在或已下架");
			return;
		}
		if (groupOrder.getPayOrders() <= 0) {
			response.outputJson(-1, "对不起，当前没有已付款订单，无法发货");
			return;
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
			response.outputJson(-1, "对不起，" + remainingDays + "天" + remainingHours + "时" + remainingMin + "分" + remainingSecond + "秒后可发货");
			return;
		}
		if (!groupOrder.isOrderPayStateByZfzOrTkz()) {
			response.outputJson(-1, "拼团当前存在交易行为，请稍后再试");
			return;
		}
		Integer days = Integer.parseInt(Environment.getProperty("cash.withdrawal.days").trim());
		Date cashWithdrawalTime = DateTimeUtils.addDay(date, days);
		groupOrder.setGroupOrderState(new GroupOrderStateQuery().states(States.YJS).uniqueResult());
		groupOrder.setDeliveryTime(date);
		groupOrder.setCashWithdrawalTime(cashWithdrawalTime);
		groupOrder.setIsFinish(false);
		groupOrder.saveOrUpdate();
		for (Order order : groupOrder.getOrders()) {
			// 锁行
			order = new OrderQuery().id(order.getId()).lockMode(LockMode.UPGRADE).uniqueResult();
			if (order.getOrderState().getOrderStates() == OrderStates.DSH && order.getPayState() == PayState.YZF) {
				order.setOrderState(new OrderStateQuery().orderStates(OrderStates.YWC).uniqueResult());
				order.setReceivingTime(date);
				order.saveOrUpdate();
			} else if (order.getOrderState().getOrderStates() == OrderStates.DFK && order.getPayState() == PayState.DZF) {
				order.setOrderState(new OrderStateQuery().orderStates(OrderStates.YQX).uniqueResult());
				order.setCancelTime(date);
				order.setCancelReason("该拼团已发货，拼团结束");
				order.saveOrUpdate();
			}
		}
		response.outputJson(0, "发货成功");
	}

	/** 提货通知 */
	@Transactional
	@RequestMapping(value = "/pickUpGoods", method = RequestMethod.POST)
	public void pickUpGoods(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
		String openId = request.getString("openId");
		Long groupOrderId = request.getLong("groupOrderId");// 团单ID
		String formId = request.getString("formId");
		String remarks = request.getString("remarks", 255);
		String check = request.getString("check");
		if (StringUtils.isEmpty(openId)) {
			response.outputJson(-1, "缺少openId参数");
			return;
		}
		if (groupOrderId == null || groupOrderId == 0) {
			response.outputJson(-1, "缺少groupOrderId参数");
			return;
		}
		if (StringUtils.isEmpty(formId)) {
			response.outputJson(-1, "缺少formId参数");
			return;
		}
		WeChatUser weChatUser = new WeChatUserQuery().openId(openId).uniqueResult();
		if (weChatUser == null) {
			response.outputJson(-1, "对不起，当前所查询的用户不存在");
			return;
		}
		GroupOrderState groupOrderState = new GroupOrderStateQuery().states(States.YJS).uniqueResult();
		GroupOrder groupOrder = new GroupOrderQuery().weChatUser(weChatUser).groupOrderState(groupOrderState).id(groupOrderId).uniqueResult();
		if (groupOrder == null) {
			response.outputJson(-1, "对不起，您操作的拼团不存在或已下架");
			return;
		}
		if (groupOrder.getDeliveryTime() == null) {
			response.outputJson(-1, "对不起，该拼团未发货");
			return;
		}
		String theme = groupOrder.getTheme();
		String selfExtractingAddress = groupOrder.getSelfExtractingAddress();
		String waitingTimesString = "";
		if (groupOrder.getWaitingTime() != null) {
			waitingTimesString = "（等取时长：" + groupOrder.getWaitingTime() + "分钟）";
		}
		Date selfExtractingTime = groupOrder.getSelfExtractingTime();
		Set<Order> orders = groupOrder.getOrders();
		// 判断能否再发，30分钟一次
		Date date = new Date();
		// 取第一条记录
		WechatNotice wechatNotices = new WechatNoticeQuery().openId(openId).noticeType(NoticeType.DELIVERY).groupId(groupOrder.getId()).orderBy("createTime", false).readOnly().firstResult();
		if (wechatNotices != null) {
			long nd = 1000 * 24 * 60 * 60;
			long nh = 1000 * 60 * 60;
			long nm = 1000 * 60;
			Date creatDate = DateTimeUtils.addMinute(wechatNotices.getCreateTime(), +30);
			if (creatDate.getTime() > date.getTime()) {
				long diff = date.getTime() - creatDate.getTime();
				long min = Math.abs(diff % nd % nh / nm);
				if (min > 0) {
					response.outputJson(-1, "对不起，请在" + min + "分钟后再尝试发送提货通知");
					return;
				}
			} else {
				if (StringUtils.isNotEmpty(check)) {
					ObjectNode objectNode = JsonUtils.createObjectNode();
					objectNode.put("canSubmit", "1");
					response.outputJson(0, objectNode);
					return;
				}
			}
		} else {
			if (StringUtils.isNotEmpty(check)) {
				ObjectNode objectNode = JsonUtils.createObjectNode();
				objectNode.put("canSubmit", "1");
				response.outputJson(0, objectNode);
				return;
			}
		}
		for (Order order : orders) {
			OrderStates orderStates = order.getOrderState().getOrderStates();
			String commodityName = "";
			for (OrderGoods orderGoods : order.getOrderGoods()) {
				commodityName = orderGoods.getGroupCommodity().getName();
				commodityName = order.getOrderGoods().size() > 1 ? commodityName + "等" : commodityName;
				break;
			}
			if (orderStates == OrderStates.YWC && order.getPayState() == PayState.YZF) {
				String id = order.getWeChatUser().getOpenId();
				// 发送微信通知
				String delivery = Environment.getProperty("wechat.notice.delivery");
				ObjectNode templateJson = JsonUtils.createObjectNode();
				// 订单编号
				templateJson.set("keyword1", JsonUtils.createObjectNode().put("value", order.getOrderNumber()));
				// 商品
				templateJson.set("keyword2", JsonUtils.createObjectNode().put("value", theme + "/" + commodityName));
				// 提货时间
				templateJson.set("keyword3", JsonUtils.createObjectNode().put("value", DateTimeUtils.format(selfExtractingTime, "yyyy-MM-dd HH:mm:ss")));
				// 提货地址
				templateJson.set("keyword4", JsonUtils.createObjectNode().put("value", selfExtractingAddress + waitingTimesString));
				// 备注
				templateJson.set("keyword5", JsonUtils.createObjectNode().put("value", remarks));

				WechatUtils.sendTemplateMessage(groupOrder.getId(), openId, order.getPrepayId(), id, templateJson, delivery, NoticeType.DELIVERY);
			}
		}
		response.outputJson(0, "操作成功");
	}

	/** 修改团单自提时间信息(给用户发通知) */
	@Transactional
	@RequestMapping(value = "/updateSelfExtractingTime", method = RequestMethod.POST)
	public void updateSelfExtractingTime(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
		String openId = request.getString("openId");
		Long groupOrderId = request.getLong("groupOrderId");
		Date selfExtractingTime = request.getDate("selfExtractingTime", "yyyy-MM-dd HH:mm:ss");// 自提时间
		if (StringUtils.isEmpty(openId)) {
			response.outputJson(-1, "对不起，缺少openId参数");
			return;
		}
		if (groupOrderId == null || groupOrderId == 0) {
			response.outputJson(-1, "对不起，缺少groupOrderId参数");
			return;
		}
		if (selfExtractingTime == null) {
			response.outputJson(-1, "对不起，缺少selfExtractingTime参数");
			return;
		}
		WeChatUser weChatUser = new WeChatUserQuery().openId(openId).uniqueResult();
		if (weChatUser == null) {
			response.outputJson(-1, "对不起，操作失败，未找到该用户");
			return;
		}
		GroupOrder groupOrder = new GroupOrderQuery().weChatUser(weChatUser).id(groupOrderId).uniqueResult();
		if (groupOrder == null) {
			response.outputJson(-1, "对不起，您操作的拼团不存在或已下架");
			return;
		}
		States states = groupOrder.getGroupOrderState().getStates();
		if (states != States.JXZ) {
			response.outputJson(-1, "对不起，操作失败，当前拼团已结束");
			return;
		}
		if (groupOrder.getIsDeliveryTime() != null && groupOrder.getIsDeliveryTime()) {
			response.outputJson(-1, "对不起，你已修改过发货时间");
			return;
		}
		long endTime = groupOrder.getEndTime().getTime();
		long selfTime = groupOrder.getSelfExtractingTime().getTime();
		// 修改的自提时间，不能超过前后共10天,且要大于结束时间
		long intervalTime = endTime - selfTime;
		long days = intervalTime / 86400000;// 计算天数 86400000 = (1000 * 60 * 60 * 24)

		long upperLimitTime = 0;// 上限时间（之前5天）
		long lowerLimitTime = selfTime + 86400000 * 5;// 下限时间（之后5天）
		if (days >= 5) {
			upperLimitTime = intervalTime;
		} else {
			upperLimitTime = endTime;
		}
		// 判断修改的时间是否在计算的范围
		if (lowerLimitTime >= selfExtractingTime.getTime() && selfExtractingTime.getTime() > upperLimitTime) {
			groupOrder.setSelfExtractingTime(selfExtractingTime);
			// 模版ID
			String templateId = Environment.getProperty("wechat.notice.selfExtractingTime");
			// 发通知
			for (Order order : groupOrder.getOrders()) {
				OrderStates orderStates = order.getOrderState().getOrderStates();
				if (orderStates == OrderStates.DSH && order.getPayState() == PayState.YZF) {
					ObjectNode templateJson = JsonUtils.createObjectNode();
					templateJson.set("keyword1", JsonUtils.createObjectNode().put("value", groupOrder.getId()));// 拼单号
					templateJson.set("keyword2", JsonUtils.createObjectNode().put("value", groupOrder.getTheme()));// 拼单标题
					templateJson.set("keyword3", JsonUtils.createObjectNode().put("value", "该订单提货时间有变，调整为" + DateTimeUtils.format(groupOrder.getSelfExtractingTime(), "yyyy-MM-dd HH:mm:ss") + "。若有疑问请致电联系团长，谢谢！"));// 温馨提示
					templateJson.set("keyword4", JsonUtils.createObjectNode().put("value", groupOrder.getPhone()));// 联系电话
					WechatUtils.sendTemplateMessage(groupOrder.getId(), openId, order.getPrepayId(), order.getWeChatUser().getOpenId(), templateJson, templateId, NoticeType.SELF_EXTRACTING_TIME);
				}
			}
		} else {
			response.outputJson(-1, "对不起，修改失败，可修改范围为前后5天，且要大于结束时间");
			return;
		}
		// 只能修改一次发货时间
		groupOrder.setIsDeliveryTime(true);
		groupOrder.saveOrUpdate();
		response.outputJson(0, "操作成功");
	}

	/** 团单详情 */
	@RequestMapping(value = "/details", method = RequestMethod.POST)
	public void details(HttpRequestWrapper request, HttpResponseWrapper response) {
		String openId = request.getString("openId");
		Long groupOrderId = request.getLong("groupOrderId");
		if (StringUtils.isEmpty(openId)) {
			response.outputJson(-1, "缺少openId参数");
			return;
		}
		if (groupOrderId == null || groupOrderId == 0) {
			response.outputJson(-1, "缺少groupOrderId参数");
			return;
		}
		WeChatUser weChatUser = new WeChatUserQuery().openId(openId).uniqueResult();
		if (weChatUser == null) {
			response.outputJson(-1, "对不起，当前所查询的用户不存在");
			return;
		}
		GroupOrderQuery groupOrderQuery = new GroupOrderQuery();
		if (weChatUser.getBrigadier() && "brigadier".equals(request.getString("type"))) {
			groupOrderQuery.community(weChatUser.getCommunities().toArray(new Community[weChatUser.getCommunities().size()]));
		} else {
			groupOrderQuery.weChatUser(weChatUser);
		}
		GroupOrder groupOrder = groupOrderQuery.id(groupOrderId).uniqueResult();
		if (groupOrder == null) {
			response.outputJson(-1, "对不起，您操作的拼团不存在或已下架");
			return;
		}
		// 团单的商品信息
		String path = Environment.getProperty("host") + Environment.getProperty("skin");
		ArrayNode orderGoodsArrayNode = JsonUtils.createArrayNode();
		if (groupOrder.getGroupType() == null || groupOrder.getGroupType() == GroupType.PTT) {
			for (GroupCommodity commodity : groupOrder.getGroupCommoditys()) {
				ObjectNode no = JsonUtils.createObjectNode();
				no.put("commodityName", commodity.getName());// 商品名称
				no.put("commodityPrice", commodity.getPrice());// 商品价格
				no.put("commodityDetails", commodity.getDetails() == null ? "" : commodity.getDetails());// 商品详情
				no.put("remnantInventory", commodity.getRemnantInventory());// 剩余库存
				no.put("buyNumber", commodity.getTotalInventory() - commodity.getRemnantInventory());// 拼团数量
				ArrayNode groupCommodityPic = JsonUtils.createArrayNode();
				for (Annex annex : commodity.getAnnexs()) {
					ObjectNode o = JsonUtils.createObjectNode();
					o.put("annexFilePath", path + annex.getFilePath());
					groupCommodityPic.add(o);
				}
				if (groupCommodityPic.size() <= 0) {
					String defaultPicPath = "/images/group_commoditys_pic.png";
					groupCommodityPic.add(JsonUtils.createObjectNode().put("annexFilePath", path + defaultPicPath));
				}
				no.set("groupCommodityPic", groupCommodityPic);
				orderGoodsArrayNode.add(no);
			}
		} else if (groupOrder.getGroupType() == GroupType.QYT) {
			// 企业团先循环品目，再循环品目下的商品
			for (GroupCommodityBasic groupCommodityBasic : groupOrder.getGroupCommodityBasic()) {
				for (GroupCommodity commodity : groupCommodityBasic.getGroupCommoditys()) {
					ObjectNode no = JsonUtils.createObjectNode();
					no.put("commodityName", commodity.getName());// 商品名称
					no.put("commodityPrice", commodity.getPrice());// 商品价格
					no.put("commodityDetails", commodity.getDetails() == null ? "" : commodity.getDetails());// 商品详情
					no.put("remnantInventory", commodity.getRemnantInventory());// 剩余库存
					no.put("buyNumber", commodity.getTotalInventory() - commodity.getRemnantInventory());// 拼团数量
					ArrayNode groupCommodityPic = JsonUtils.createArrayNode();
					for (Annex annex : commodity.getAnnexs()) {
						ObjectNode o = JsonUtils.createObjectNode();
						o.put("annexFilePath", path + annex.getFilePath());
						groupCommodityPic.add(o);
					}
					if (groupCommodityPic.size() <= 0) {
						String defaultPicPath = "/images/group_commoditys_pic.png";
						groupCommodityPic.add(JsonUtils.createObjectNode().put("annexFilePath", path + defaultPicPath));
					}
					no.set("groupCommodityPic", groupCommodityPic);
					orderGoodsArrayNode.add(no);
				}
			}
		}
		// 用户订单信息
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Set<Order> orders = groupOrder.getOrders();
		Integer yfkOrder = 0;
		Integer dfkOrder = 0;
		Integer yqxOrder = 0;
		ArrayNode orderArrayNode = JsonUtils.createArrayNode();
		for (Order order : orders) {
			OrderStates orderStates = order.getOrderState().getOrderStates();
			PayState payState = order.getPayState();
			if (payState == PayState.YZF && (orderStates == OrderStates.DSH || orderStates == OrderStates.YWC)) {
				++yfkOrder;
			}
			if (orderStates == OrderStates.DFK) {
				++dfkOrder;
			}
			if (orderStates == OrderStates.YQX) {
				++yqxOrder;
			}
			ObjectNode node = JsonUtils.createObjectNode();
			WeChatUser user = order.getWeChatUser();
			Date pickpictimeDate = order.getPickPicTime();
			node.put("headImgUrl", user.getHeadImgUrl());
			node.put("name", user.getName().substring(0, 1) + "**");
			node.put("orderState", order.getOrderState().getDesc());
			node.put("payState", order.getPayState().getDesc());
			node.put("phone", order.getPhone() == null ? "" : order.getPhone());
			node.put("createTime", sdf.format(order.getCreateTime()));
			node.put("finalPayment", order.getFinalPayment());
			node.put("orderRemarks", order.getRemarks() == null ? "" : order.getRemarks());
			node.put("orderContacts", order.getContacts() == null ? "" : order.getContacts());
			node.put("total", order.getTotal());
			node.put("orderPickUpPicsTime", pickpictimeDate == null ? "" : sdf.format(pickpictimeDate));
			ArrayNode userOrderGoods = JsonUtils.createArrayNode();
			for (OrderGoods orderGoods : order.getOrderGoods()) {
				ObjectNode objectNode = JsonUtils.createObjectNode();
				GroupCommodity groupCommodity = orderGoods.getGroupCommodity();
				objectNode.put("commodityName", groupCommodity.getName());// 商品名称
				objectNode.put("commodityPrice", groupCommodity.getPrice());// 商品价格
				objectNode.put("amount", orderGoods.getAmount());
				userOrderGoods.add(objectNode);
			}
			node.set("userOrderGoods", userOrderGoods);
			// 用户订单提货图片
			ArrayNode orderPickUpPics = JsonUtils.createArrayNode();
			List<Annex> annexList = new AnnexQuery().order(order).annexType(AnnexType.ORDER_PICK_PIC).orderBy("sort", true).list();
			for (Annex annex : annexList) {
				ObjectNode objectNode = JsonUtils.createObjectNode();
				objectNode.put("annexFilePath", path + annex.getFilePath());
				orderPickUpPics.add(objectNode);
			}
			node.set("orderPickUpPics", orderPickUpPics);
			orderArrayNode.add(node);
		}
		ObjectNode objectNode = JsonUtils.createObjectNode();
		objectNode.put("groupOrderId", groupOrder.getId());
		objectNode.put("orderTotal", orders.size());
		objectNode.put("reviewStates", groupOrder.getReviewStates() == null ? "" : groupOrder.getReviewStates().getDesc());
		objectNode.put("reviewReason", groupOrder.getReviewReason() == null ? "" : groupOrder.getReviewReason());
		objectNode.put("reviewTime", groupOrder.getReviewTime() == null ? "" : DateTimeUtils.format(groupOrder.getReviewTime(), "yyyy-MM-dd HH:mm:ss"));
		objectNode.put("yfkOrder", yfkOrder);
		objectNode.put("dfkOrder", dfkOrder);
		objectNode.put("yqxOrder", yqxOrder);
		objectNode.put("theme", groupOrder.getTheme());
		objectNode.put("themeIntroduce", groupOrder.getThemeIntroduce());
		objectNode.put("selfExtractingTime", sdf.format(groupOrder.getSelfExtractingTime()));
		objectNode.put("waitingTime", groupOrder.getWaitingTime());
		objectNode.put("endTime", sdf.format(groupOrder.getEndTime()));
		objectNode.put("selfExtractingAddress", groupOrder.getSelfExtractingAddress() == null ? "" : groupOrder.getSelfExtractingAddress());
		objectNode.put("upperlimit", groupOrder.getUpperlimit());
		objectNode.put("groupType", groupOrder.getGroupType() == null ? "PTT" : groupOrder.getGroupType().toString());
		objectNode.set("orderGoods", orderGoodsArrayNode);// 订单商品信息
		objectNode.set("ordersInfo", orderArrayNode);
		States states = groupOrder.getGroupOrderState().getStates();
		if (states == States.JXZ || states == States.YJS) {
			objectNode.put("payOrders", groupOrder.getPayOrders());// 已支付订单量
		} else {
			objectNode.put("payOrders", 0);
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
			objectNode.put("remainingDeliveryTime", remainingDays + "天" + remainingHours + "时" + remainingMin + "分" + remainingSecond + "秒后可发货");
		} else {
			objectNode.put("remainingDeliveryTime", 0);
		}
		response.outputJson(0, objectNode);
	}

	/** 配送单 */
	@RequestMapping(value = "/distributionSlip", method = RequestMethod.POST)
	public void distributionSlip(HttpRequestWrapper request, HttpResponseWrapper response) {
		String openId = request.getString("openId");
		Long groupOrderId = request.getLong("groupOrderId");
		if (StringUtils.isEmpty(openId)) {
			response.outputJson(-1, "缺少openId参数");
			return;
		}
		if (groupOrderId == null || groupOrderId == 0) {
			response.outputJson(-1, "缺少groupOrderId参数");
			return;
		}
		WeChatUser weChatUser = new WeChatUserQuery().openId(openId).uniqueResult();
		if (weChatUser == null) {
			response.outputJson(-1, "对不起，当前所查询的用户不存在");
			return;
		}
		GroupOrder groupOrder = new GroupOrderQuery().weChatUser(weChatUser).id(groupOrderId).uniqueResult();
		if (groupOrder == null) {
			response.outputJson(-1, "对不起，您操作的拼团不存在或已下架");
			return;
		}
		States states = groupOrder.getGroupOrderState().getStates();
		if (states != States.JXZ && states != States.YJS) {
			response.outputJson(-1, "对不起，您操作的拼团状态有误，请稍后再试");
			return;
		}
		ObjectNode objectNode = JsonUtils.createObjectNode();
		Community community = groupOrder.getCommunity();
		Township township = community.getTownship();
		objectNode.put("theme", groupOrder.getTheme());
		objectNode.put("townshipName", township == null ? "" : township.getName());
		objectNode.put("communityName", community == null ? "" : community.getName());
		objectNode.put("contacts", groupOrder.getWeChatUser().getContacts());
		objectNode.put("phone", groupOrder.getPhone());
		objectNode.put("createTime", DateTimeUtils.format(new Date(), "yyyy-MM-dd HH:mm"));
		ArrayNode orderArrayNode = JsonUtils.createArrayNode();
		for (Order order : groupOrder.getOrders()) {
			OrderStates orderStates = order.getOrderState().getOrderStates();
			if ((orderStates == OrderStates.DSH || orderStates == OrderStates.YWC) && order.getPayState() == PayState.YZF) {
				ObjectNode orderNode = JsonUtils.createObjectNode();
				orderNode.put("orderNumber", order.getOrderNumber());
				orderNode.put("contacts", order.getContacts());
				orderNode.put("phone", order.getPhone());
				// 订单商品信息
				ArrayNode commodityArrayNode = JsonUtils.createArrayNode();
				for (OrderGoods orderGoods : order.getOrderGoods()) {
					ObjectNode no = JsonUtils.createObjectNode();
					no.put("commodityName", orderGoods.getGroupCommodity().getName());
					no.put("amount", orderGoods.getAmount());
					no.put("total", orderGoods.getTotal());
					commodityArrayNode.add(no);
				}
				orderNode.set("commodityArrayNode", commodityArrayNode);
				// 地址，包括 省，市，镇区，小区，详细地址
				orderNode.put("address", groupOrder.getSelfExtractingAddress());
				orderNode.put("remarks", order.getRemarks());
				orderArrayNode.add(orderNode);
			}
			objectNode.set("orderArrayNode", orderArrayNode);
		}
		response.outputJson(0, objectNode);
	}

	/** 二维码(生成excel配送单和二维码图片) */
	@Transactional
	@RequestMapping(value = "/QRCode")
	public void QRCode(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
		String openId = request.getString("openId");
		Long groupOrderId = request.getLong("groupOrderId");
		if (StringUtils.isEmpty(openId)) {
			response.outputJson(-1, "缺少openId参数");
			return;
		}
		if (groupOrderId == null || groupOrderId == 0) {
			response.outputJson(-1, "缺少groupOrderId参数");
			return;
		}
		WeChatUser weChatUser = new WeChatUserQuery().openId(openId).uniqueResult();
		if (weChatUser == null) {
			response.outputJson(-1, "对不起，当前所查询的用户不存在");
			return;
		}
		GroupOrder groupOrder = new GroupOrderQuery().weChatUser(weChatUser).id(groupOrderId).uniqueResult();
		if (groupOrder == null) {
			response.outputJson(-1, "对不起，您操作的拼团不存在或已下架");
			return;
		}
		States states = groupOrder.getGroupOrderState().getStates();
		if (states != States.JXZ && states != States.YJS) {
			response.outputJson(-1, "对不起，您操作的拼团状态有误，请稍后再试");
			return;
		}
		// 生成excel
		String annexId = generateExcel(groupOrder);
		// 文件下载地址
		String host = Environment.getProperty("host");
		String path = host + Environment.getProperty("path");
		String url = path + "/annex/download.action?id=" + annexId;
		// 生成二维码
		BitMatrix matrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, 200, 200);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		QRCodeUtil.writeToStream(matrix, "jpg", os);
		// 保存二维码图片，同一天同一个团单保证只有一个二维码
		String fileName = groupOrder.getId() + ".jpg";
		Date createTime = DateTimeUtils.getDayMinTime(new Date());
		Date endTime = DateTimeUtils.getDayMaxTime(new Date());
		Annex annex = new AnnexQuery().annexType(AnnexType.QR_CODE).createTime(createTime, endTime).fileName(fileName).uniqueResult();
		if (annex != null) {
			annex.remove(Environment.getSession());
		}
		annex = AnnexUtils.saveAnnex(AnnexType.QR_CODE, fileName, null, os.toByteArray());
		String skin = host + Environment.getProperty("skin");
		response.outputJson(0, JsonUtils.createObjectNode().put("qcCodeUrl", skin + annex.getFilePath()));
	}

	/** 生成excel文件 */
	public String generateExcel(GroupOrder groupOrder) throws Exception {
		WritableWorkbook workbook = null;
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			workbook = Workbook.createWorkbook(os);
			WritableSheet sheet = workbook.createSheet("配送单", 0);
			sheet.setColumnView(0, 20);// 设置列宽
			sheet.setColumnView(1, 10);
			sheet.setColumnView(2, 15);
			sheet.setColumnView(3, 20);
			sheet.setColumnView(4, 5);
			sheet.setColumnView(5, 15);
			sheet.setColumnView(6, 20);
			// 设置标题样式，字体，字体大小，垂直居中，加粗
			WritableFont writableFont = new WritableFont(WritableFont.createFont("宋体"), 16, WritableFont.BOLD);
			WritableCellFormat titleFormate = new WritableCellFormat(writableFont);
			titleFormate.setVerticalAlignment(VerticalAlignment.CENTRE);
			titleFormate.setAlignment(Alignment.CENTRE);
			// 标题，excel第一行
			sheet.getSettings().setShowGridLines(false);// 隐藏网格线
			sheet.addCell(new jxl.write.Label(0, 0, "配送单", titleFormate));
			sheet.mergeCells(0, 0, 6, 0);// 合并单元格(列合并)
			// 设置头信息样式
			writableFont = new WritableFont(WritableFont.createFont("宋体"), 10);
			titleFormate = new WritableCellFormat(writableFont);
			titleFormate.setVerticalAlignment(VerticalAlignment.CENTRE);// 垂直居中
			titleFormate.setAlignment(Alignment.RIGHT);// 右偏对齐
			titleFormate.setWrap(true);// 自动换行
			// 地址信息
			String address = "广东省中山市";
			address += groupOrder.getCommunity().getTownship().getName();
			address += groupOrder.getCommunity().getName();
			// 头信息
			String contacts = groupOrder.getWeChatUser().getContacts();
			String phone = groupOrder.getPhone();
			// 从第二行开始
			sheet.addCell(new jxl.write.Label(0, 1, "拼团名称：", titleFormate));
			sheet.addCell(new jxl.write.Label(0, 2, "团长：", titleFormate));
			sheet.addCell(new jxl.write.Label(4, 1, "所属小区：", titleFormate));
			sheet.addCell(new jxl.write.Label(4, 2, "生成日期：", titleFormate));
			sheet.mergeCells(4, 1, 5, 1);// 合并
			sheet.mergeCells(4, 2, 5, 2);
			// 设置单元格样式
			writableFont = new WritableFont(WritableFont.createFont("宋体"), 10);
			titleFormate = new WritableCellFormat(writableFont);
			titleFormate.setBorder(jxl.format.Border.NONE, jxl.format.BorderLineStyle.NONE);
			titleFormate.setAlignment(Alignment.LEFT);
			titleFormate.setVerticalAlignment(VerticalAlignment.CENTRE);
			titleFormate.setWrap(true);// 自动换行
			sheet.addCell(new jxl.write.Label(1, 1, groupOrder.getTheme(), titleFormate));
			sheet.addCell(new jxl.write.Label(1, 2, contacts + "（" + phone + "）", titleFormate));
			sheet.addCell(new jxl.write.Label(6, 1, address, titleFormate));
			sheet.addCell(new jxl.write.Label(6, 2, DateTimeUtils.format(new Date(), "yyyy-MM-dd HH:mm"), titleFormate));
			sheet.mergeCells(1, 1, 3, 1);// 合并单元格(列合并)
			sheet.mergeCells(1, 2, 3, 2);// 合并单元格(列合并)
			sheet.setRowView(0, 600);
			sheet.setRowView(1, 600);
			sheet.setRowView(2, 600);
			sheet.setRowView(3, 600);
			// 数据标题样式
			writableFont = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.BOLD);
			titleFormate = new WritableCellFormat(writableFont);
			titleFormate.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
			titleFormate.setVerticalAlignment(VerticalAlignment.CENTRE);
			titleFormate.setWrap(true);// 自动换行
			sheet.addCell(new jxl.write.Label(0, 3, "序号", titleFormate));
			sheet.addCell(new jxl.write.Label(1, 3, "昵称", titleFormate));
			sheet.addCell(new jxl.write.Label(2, 3, "联系电话", titleFormate));
			sheet.addCell(new jxl.write.Label(3, 3, "商品名称", titleFormate));
			sheet.addCell(new jxl.write.Label(4, 3, "商品数量", titleFormate));
			sheet.addCell(new jxl.write.Label(5, 3, "送货地址", titleFormate));
			sheet.addCell(new jxl.write.Label(6, 3, "参团留言", titleFormate));

			// 设置数据单元格样式，字体，字体大小，全边框，垂直居中
			writableFont = new WritableFont(WritableFont.createFont("宋体"), 10);
			titleFormate = new WritableCellFormat(writableFont);
			titleFormate.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
			titleFormate.setVerticalAlignment(VerticalAlignment.CENTRE);
			titleFormate.setWrap(true);// 自动换行
			// 循环订单信息
			int i = 4;// 控制excel行数
			for (Order order : groupOrder.getOrders()) {
				OrderStates orderStates = order.getOrderState().getOrderStates();
				if ((orderStates == OrderStates.DSH || orderStates == OrderStates.YWC) && order.getPayState() == PayState.YZF) {
					// 订单的商品数量（一共有多少个商品）
					sheet.addCell(new jxl.write.Label(0, i, order.getOrderNumber(), titleFormate));
					sheet.addCell(new jxl.write.Label(1, i, order.getContacts(), titleFormate));
					sheet.addCell(new jxl.write.Label(2, i, order.getPhone(), titleFormate));
					int j = 0;
					Iterator<OrderGoods> iterator = order.getOrderGoods().iterator();
					while (iterator.hasNext()) {
						OrderGoods orderGoods = (OrderGoods) iterator.next();
						if (j == 0) {
							sheet.addCell(new jxl.write.Label(3, i, orderGoods.getGroupCommodity().getName(), titleFormate));
							sheet.addCell(new jxl.write.Label(4, i, orderGoods.getAmount().toString(), titleFormate));
							sheet.addCell(new jxl.write.Label(5, i, order.getAddress() == null ? groupOrder.getSelfExtractingAddress() : order.getAddress(), titleFormate));
							sheet.addCell(new jxl.write.Label(6, i, order.getRemarks(), titleFormate));
							if (iterator.hasNext()) {
								sheet.setRowView(i, 600);
							}
						} else if (j > 0) {
							++i;
							sheet.addCell(new jxl.write.Label(0, i, "", titleFormate));
							sheet.addCell(new jxl.write.Label(1, i, "", titleFormate));
							sheet.addCell(new jxl.write.Label(2, i, "", titleFormate));
							sheet.addCell(new jxl.write.Label(3, i, orderGoods.getGroupCommodity().getName(), titleFormate));
							sheet.addCell(new jxl.write.Label(4, i, orderGoods.getAmount().toString(), titleFormate));
							sheet.addCell(new jxl.write.Label(5, i, "", titleFormate));
							sheet.addCell(new jxl.write.Label(6, i, "", titleFormate));
							sheet.setRowView(i, 600);
						}
						if (j > 0 && !iterator.hasNext()) {
							// 合并单元格(行合并)
							sheet.mergeCells(0, i - j, 0, i);
							sheet.mergeCells(1, i - j, 1, i);
							sheet.mergeCells(2, i - j, 2, i);
							sheet.mergeCells(5, i - j, 5, i);
							sheet.mergeCells(6, i - j, 6, i);
						}
						++j;
					}
					++i;
				}
			}
			// 兼容手机
			writableFont = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.BOLD);
			titleFormate = new WritableCellFormat(writableFont);
			titleFormate.setBorder(jxl.format.Border.TOP, jxl.format.BorderLineStyle.THIN);
			sheet.addCell(new jxl.write.Label(0, i, " ", titleFormate));
			sheet.addCell(new jxl.write.Label(1, i, " ", titleFormate));
			sheet.addCell(new jxl.write.Label(2, i, " ", titleFormate));
			sheet.addCell(new jxl.write.Label(3, i, " ", titleFormate));
			sheet.addCell(new jxl.write.Label(4, i, " ", titleFormate));
			sheet.addCell(new jxl.write.Label(5, i, " ", titleFormate));
			sheet.addCell(new jxl.write.Label(6, i, " ", titleFormate));
			// 写
			workbook.write();
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("【生成EXCEL错误】", e);
		} finally {
			if (workbook != null) {
				workbook.close();
			}
		}
		// 保存excel，同一天同一个团单保证只有一个excel文件
		String fileName = groupOrder.getId() + ".xls";
		Date createTime = DateTimeUtils.getDayMinTime(new Date());
		Date endTime = DateTimeUtils.getDayMaxTime(new Date());
		Annex annex = new AnnexQuery().annexType(AnnexType.GROUP_ORDER_PSD_EXCEL).createTime(createTime, endTime).fileName(fileName).uniqueResult();
		if (annex != null) {
			annex.remove(Environment.getSession());
		}
		annex = AnnexUtils.saveAnnex(AnnexType.GROUP_ORDER_PSD_EXCEL, fileName, null, os.toByteArray());
		return annex.getId();
	}
}