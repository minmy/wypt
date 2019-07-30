package cn.sini.cgb.api.cgb.action.group;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.sini.cgb.admin.annex.entity.Annex;
import cn.sini.cgb.admin.annex.query.AnnexQuery;
import cn.sini.cgb.api.cgb.entity.group.Community;
import cn.sini.cgb.api.cgb.entity.group.GroupCommodity;
import cn.sini.cgb.api.cgb.entity.group.GroupCommodityBasic;
import cn.sini.cgb.api.cgb.entity.group.GroupOrder;
import cn.sini.cgb.api.cgb.entity.group.GroupOrder.GroupType;
import cn.sini.cgb.api.cgb.entity.group.GroupOrderState;
import cn.sini.cgb.api.cgb.entity.group.GroupOrderState.States;
import cn.sini.cgb.api.cgb.entity.group.Label;
import cn.sini.cgb.api.cgb.entity.group.WayOfDelivery.DeliveryType;
import cn.sini.cgb.api.cgb.entity.group.WeChatUser;
import cn.sini.cgb.api.cgb.entity.verification.Business;
import cn.sini.cgb.api.cgb.query.group.CommunityQuery;
import cn.sini.cgb.api.cgb.query.group.GroupCommodityBasicQuery;
import cn.sini.cgb.api.cgb.query.group.GroupCommodityQuery;
import cn.sini.cgb.api.cgb.query.group.GroupOrderQuery;
import cn.sini.cgb.api.cgb.query.group.GroupOrderStateQuery;
import cn.sini.cgb.api.cgb.query.group.LabelQuery;
import cn.sini.cgb.api.cgb.query.group.TownshipQuery;
import cn.sini.cgb.api.cgb.query.group.WayOfDeliveryQuery;
import cn.sini.cgb.api.cgb.query.group.WeChatUserQuery;
import cn.sini.cgb.api.cgb.query.verification.BusinessQuery;
import cn.sini.cgb.common.http.HttpRequestWrapper;
import cn.sini.cgb.common.http.HttpResponseWrapper;
import cn.sini.cgb.common.json.JsonUtils;
import cn.sini.cgb.common.json.JsonWrapper;
import cn.sini.cgb.common.util.DateTimeUtils;
import cn.sini.cgb.common.util.Environment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 企业团action
 * 
 * @author gaowei
 */
@Controller
@RequestMapping("/api/enterpriseGroup")
public class EnterpriseGroupAction {

	// private static final Logger LOGGER = LoggerFactory.getLogger(EnterpriseGroupAction.class);

	/** 保存企业团单 */
	@Transactional
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public void save(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
		String openId = request.getStringMust("openId");// 用户openId
		// 团单信息
		Long groupOrderId = request.getLong("groupOrderId");// 团单ID
		String theme = request.getStringMust("theme", 255);// 主题
		String themeIntroduce = request.getString("themeIntroduce", 255);// 主题介绍
		Boolean isUpgrade = request.getBooleanMust("isUpgrade");// 是否升级
		Integer addIntegral = request.getInteger("addIntegral");// 新增积分
		States groupOrderState = request.getEnumMust("groupOrderState", GroupOrderState.States.class);// 团单状态
		String label = request.getString("label");// 标签字符串
		String groupOrderPics = request.getString("groupOrderPics");// 拼团图片
		Boolean isCombination = request.getBooleanMust("isCombination");// 是否组合商品
		Date endTime = request.getDate("endTime", "yyyy-MM-dd HH:mm:ss");// 拼团结束时间
		Date selfExtractingBeginTime = request.getDate("selfExtractingBeginTime", "yyyy-MM-dd HH:mm:ss");// 自提开始时间
		Date selfExtractingEndTime = request.getDate("selfExtractingEndTime", "yyyy-MM-dd HH:mm:ss");// 自提结束时间
		String province = request.getString("province");// 省
		String city = request.getString("city");// 市
		String area = request.getString("area");// 区
		String street = request.getString("street");// 街道
		Long communityId = request.getLong("communityId");// 小区Id
		String selfExtractingAddress = request.getString("selfExtractingAddress", 255);// 自提地址(详细地址)
		String phone = request.getTrim("phone", "^1\\d{10}$");// 联系电话
		String notice = request.getString("notice", 255);// 拼团须知
		Boolean isRefund = request.getBooleanMust("isRefund");// 是否允许退款
		// 团单基本商品信息以及团单商品信息
		String products = request.getString("products");

		// 进行中状态下部分字段不可为空
		if (groupOrderState != null && groupOrderState == States.JXZ) {
			if (StringUtils.isBlank(themeIntroduce)) {
				response.outputJson(-1, "缺少themeIntroduce参数");
				return;
			}
			if (isUpgrade && (addIntegral == null || addIntegral == 0L)) {
				response.outputJson(-1, "缺少addIntegral参数");
				return;
			}
			if (endTime == null) {
				response.outputJson(-1, "缺少endTime参数");
				return;
			}
			if (selfExtractingBeginTime == null) {
				response.outputJson(-1, "缺少selfExtractingBeginTime参数");
				return;
			}
			if (selfExtractingEndTime == null) {
				response.outputJson(-1, "缺少selfExtractingEndTime参数");
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
			if (StringUtils.isBlank(selfExtractingAddress)) {
				response.outputJson(-1, "缺少selfExtractingAddress参数");
				return;
			}
			if (StringUtils.isBlank(phone)) {
				response.outputJson(-1, "缺少phone参数");
				return;
			}
			if (StringUtils.isBlank(notice)) {
				response.outputJson(-1, "缺少notice参数");
				return;
			}
			if (StringUtils.isBlank(products)) {
				response.outputJson(-1, "缺少products参数");
				return;
			}
			// 判断商品品目字段
			int j = 0;
			ArrayNode productsArrayNode = JsonUtils.toArrayNode(products);
			for (JsonNode json : productsArrayNode) {
				JsonWrapper jw = new JsonWrapper(json);
				Long itemId = jw.getLong("itemId");// 商品品目id
				String itemName = jw.getString("itemName");// 商品品目
				BigDecimal basicPrice = jw.getBigDecimal("basicPrice");// 商品品目价格
				Boolean isPack = jw.getBooleanMust("isPack");// 是否打包
				String commodity = json.get("commodity").toString();// 商品信息
				if (itemId != null && itemId == 0) {
					response.outputJson(-1, "itemId参数格式错误");
					return;
				}
				if (StringUtils.isBlank(itemName)) {
					response.outputJson(-1, "缺少itemName参数");
					return;
				}
				if (isPack && (basicPrice == null || basicPrice.compareTo(BigDecimal.ZERO) == 0)) {
					response.outputJson(-1, "缺少basicPrice参数");
					return;
				}
				if (StringUtils.isBlank(commodity)) {
					response.outputJson(-1, "缺少commodity参数");
					return;
				}
				// 判断商品信息字段
				int i = 0;
				int k = 0;
				ArrayNode arrayNode = JsonUtils.toArrayNode(commodity);
				int size = arrayNode.size();// 品目下商品的数量
				for (JsonNode jsonNode : arrayNode) {
					JsonWrapper wrapper = new JsonWrapper(jsonNode);
					Long commodityId = wrapper.getLong("commodityId");// 商品ID
					Long businessId = wrapper.getLong("businessId");// 所属商家ID
					String name = wrapper.getString("name");// 商品名称
					BigDecimal price = wrapper.getBigDecimal("price", null, null, 6, 2);// 售价（现价）
					Integer inventory = wrapper.getInteger("inventory", 0, 99999999);// 库存数量
					Integer commodityUpperLimit = wrapper.getInteger("commodityUpperLimit", 0, 99999999);// 商品购买上限
					Boolean commodityIsUpgrade = wrapper.getBooleanMust("commodityIsUpgrade");// 商品是否可升級
					Integer integral = wrapper.getInteger("integral");// 所需积分
					Boolean isHidden = wrapper.getBooleanMust("isHidden");// 是否隐藏
					if (commodityId != null && commodityId == 0) {
						response.outputJson(-1, "commodityId参数格式错误");
						return;
					}
					if (businessId != null && businessId == 0) {
						response.outputJson(-1, "businessId参数格式错误");
						return;
					}
					if (StringUtils.isBlank(name)) {
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
					// 当团单isUpgrade为true（可升级），下面所添加的商品必须有一个为升级商品
					if (isUpgrade) {
						if (commodityIsUpgrade) {
							// commodityIsUpgrade 为true时，积分不能为空或0
							if (integral == null || integral <= 0) {
								response.outputJson(-1, "缺少integral参数");
								return;
							}
							++j;
						}
					}
					// 如果品目打包了，那只能控制只有一个商品可以显示出来，不能显示多个，也不能一个也不显示
					if (isPack && !isHidden) {
						++i;
					}
					// 如果品目没有打包，则商品不能全部隐藏，至少显示一个(因为可能会有可以升级的商品，所以这种情况下允许商品隐藏)
					if (!isPack && isHidden) {
						++k;
						if (k == size) {
							response.outputJson(-1, "当前品目没有一个可显示的商品信息，请重新选择");
							return;
						}
					}
				}
				if (isPack && i < 1) {
					response.outputJson(-1, "当前品目没有一个可显示的商品信息，请重新选择");
					return;
				} else if (isPack && i > 1) {
					response.outputJson(-1, "当前品目只能有一个可显示的商品信息，请重新选择");
					return;
				}
			}
			// 当团单isUpgrade为true（可升级），下面所添加的商品必须有一个为升级商品
			if (isUpgrade && j == 0) {
				response.outputJson(-1, "请至少添加一个升级商品");
				return;
			}
		}
		// 用户
		WeChatUser weChatUser = new WeChatUserQuery().openId(openId).uniqueResult();
		if (weChatUser == null) {
			response.outputJson(-1, "保存失败，未找到该用户");
			return;
		}

		// 保存团单
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
		groupOrder.setTheme(theme);
		groupOrder.setThemeIntroduce(themeIntroduce);
		groupOrder.setAddIntegral(addIntegral);
		groupOrder.setIsUpgrade(isUpgrade);
		groupOrder.setGroupOrderState(new GroupOrderStateQuery().states(groupOrderState).uniqueResult());
		if (groupOrderState == GroupOrderState.States.JXZ) {
			groupOrder.setReleaseTime(new Date());
		}
		// 保存标签
		groupOrder.getLabels().removeAll(new LabelQuery().list());
		if (StringUtils.isNotBlank(label)) {
			String[] split = label.split(",");
			Serializable[] labels = new Serializable[split.length];// 标签数组
			for (int i = 0; i < split.length; i++) {
				labels[i] = Long.parseLong(split[i]);
			}
			if (labels != null && labels.length > 0) {
				List<Label> labelList = new LabelQuery().id(labels).list();
				groupOrder.getLabels().addAll(labelList);
			}
		}
		groupOrder.setIsCombination(isCombination);// 是否组合商品
		groupOrder.setBeginTime(DateTimeUtils.parse(DateTimeUtils.format(new Date(), "yyyy-MM-dd HH:mm"), "yyyy-MM-dd HH:mm"));
		groupOrder.setEndTime(endTime);
		groupOrder.setSelfExtractingTime(selfExtractingBeginTime);// 提货开始时间
		groupOrder.setSelfExtractingEndTime(selfExtractingEndTime);// 提货结束时间
		groupOrder.setSelfExtractingAddress(selfExtractingAddress);// 自提地址(详细地址)
		groupOrder.setProvince(province);
		groupOrder.setCity(city);
		groupOrder.setTownships(area);
		groupOrder.setStreet(street);
		Community community = null;
		if (communityId != null) {
			community = new CommunityQuery().townshipQuery(new TownshipQuery().code(area)).id(communityId).uniqueResult();
			if (community == null) {
				response.outputJson(-1, "保存失败，未找到该小区");
				return;
			}
		}
		groupOrder.setCommunity(community);
		groupOrder.setPhone(phone);
		groupOrder.setNotice(notice);
		groupOrder.setWeChatUser(weChatUser);
		groupOrder.setGroupType(GroupType.QYT);
		groupOrder.getWayOfDeliverys().clear();
		groupOrder.getWayOfDeliverys().addAll(new WayOfDeliveryQuery().deliveryType(DeliveryType.HX).list());
		groupOrder.setIsRefund(isRefund);
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

		// 保存团单基本商品信息以及团单商品信息
		ArrayNode productsArrayNode = JsonUtils.toArrayNode(products);
		if (productsArrayNode != null) {
			for (JsonNode jsonNode : productsArrayNode) {
				JsonWrapper jw = new JsonWrapper(jsonNode);
				Long itemId = jw.getLong("itemId");// 商品品目id
				String itemName = jw.getString("itemName");// 商品品目
				BigDecimal basicPrice = jw.getBigDecimal("basicPrice");// 商品品目价格
				Boolean isPack = jw.getBooleanMust("isPack");// 是否打包
				Integer basicSort = jw.getInteger("basicSort");// 商品品目排序
				String commodity = jsonNode.get("commodity").toString();// 商品信息
				// 保存商品品目
				GroupCommodityBasic groupCommodityBasic = null;
				if (itemId == null) {
					groupCommodityBasic = new GroupCommodityBasic();
				} else {
					groupCommodityBasic = new GroupCommodityBasicQuery().id(itemId).uniqueResult();
				}
				groupCommodityBasic.setName(itemName);
				groupCommodityBasic.setPrice(basicPrice);
				groupCommodityBasic.setIsPack(isPack);
				groupCommodityBasic.setSort(basicSort);
				groupCommodityBasic.setGroupOrder(groupOrder);
				groupCommodityBasic.saveOrUpdate();

				ArrayNode commodityArrayNode = JsonUtils.toArrayNode(commodity);
				if (commodityArrayNode != null) {
					for (JsonNode node : commodityArrayNode) {
						JsonWrapper wrapper = new JsonWrapper(node);
						Long commodityId = wrapper.getLong("commodityId");// 商品ID
						Long businessId = wrapper.getLong("businessId");// 所属商家ID
						String name = wrapper.getString("name");// 商品名称
						BigDecimal originalPrice = wrapper.getBigDecimal("originalPrice", null, null, 6, 2);// 原价
						BigDecimal price = wrapper.getBigDecimal("price", null, null, 6, 2);// 售价（现价）
						String description = wrapper.getString("description", 255);// 规格说明
						Integer inventory = wrapper.getInteger("inventory", 0, 99999999);// 库存数量
						Integer commodityUpperLimit = wrapper.getInteger("commodityUpperLimit", 0, 99999999);// 商品购买上限
						Integer writeOffsNumber = wrapper.getInteger("writeOffsNumber");// 核销次数
						String details = wrapper.getString("details", 255);// 商品详情
						Boolean commodityIsUpgrade = wrapper.getBoolean("commodityIsUpgrade");// 商品是否可升級
						Integer integral = wrapper.getInteger("integral");// 所需积分
						Boolean isHidden = wrapper.getBooleanMust("isHidden");// 商品是否隐藏
						Integer sort = wrapper.getInteger("sort");// 商品排序
						Boolean verify = wrapper.getBooleanMust("verify");// 是否不可核销

						String commodityPics = wrapper.getString("commodityPics");// 商品图片ID

						// 保存商品信息
						GroupCommodity groupCommodity = null;
						if (commodityId == null) {
							groupCommodity = new GroupCommodity();
						} else {
							groupCommodity = new GroupCommodityQuery().id(commodityId).uniqueResult();
							// 商品图片
							List<Annex> annexList = new AnnexQuery().groupCommodity(groupCommodity).list();
							for (Annex annex : annexList) {
								annex.setGroupCommodity(null);
								annex.saveOrUpdate();
							}
						}
						// 关联商家
						if (businessId != null) {
							Business business = new BusinessQuery().id(businessId).uniqueResult();
							groupCommodity.setBusiness(business);
						}
						groupCommodity.setName(name);
						groupCommodity.setOriginalPrice(originalPrice);
						groupCommodity.setPrice(price);
						groupCommodity.setDescription(description);
						groupCommodity.setTotalInventory(inventory);
						groupCommodity.setRemnantInventory(inventory);
						groupCommodity.setUpperlimit(commodityUpperLimit);
						groupCommodity.setWriteOffsNumber(writeOffsNumber);
						groupCommodity.setDetails(details);
						groupCommodity.setGroupOrder(groupOrder);
						groupCommodity.setIsUpgrade(commodityIsUpgrade);
						groupCommodity.setIntegral(integral);
						groupCommodity.setIsHidden(isHidden);
						groupCommodity.setSort(sort);
						groupCommodity.setGroupCommodityBasic(groupCommodityBasic);
						groupCommodity.setVerify(verify);
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
					}
				}
			}
		}
		response.outputJson(0, JsonUtils.createObjectNode().put("groupOrderId", groupOrder.getId()));
	}

	/** 根据ID查询企业团 */
	@Transactional
	@RequestMapping(value = "/getById", method = RequestMethod.POST)
	public void getById(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
		String openId = request.getStringMust("openId");
		Long groupOrderId = request.getLongMust("groupOrderId");// 团单ID
		Boolean isAgain = request.getBooleanMust("isAgain");// 是否再来一单
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

		ObjectNode objectNode = JsonUtils.createObjectNode();
		objectNode.put("groupOrderId", isAgain == true ? "" : groupOrder.getId().toString());
		objectNode.put("groupType", groupOrder.getGroupType().toString());
		objectNode.put("addIntegral", groupOrder.getAddIntegral());
		objectNode.put("theme", groupOrder.getTheme());
		objectNode.put("themeIntroduce", groupOrder.getThemeIntroduce() == null ? "" : groupOrder.getThemeIntroduce());
		objectNode.put("isUpgrade", groupOrder.getIsUpgrade());
		// 标签
		ArrayNode labelArrayNode = JsonUtils.createArrayNode();
		for (Label label : groupOrder.getLabels()) {
			ObjectNode on = JsonUtils.createObjectNode();
			on.put("labelId", label.getId());
			on.put("labelTag", label.getTag());
			labelArrayNode.add(on);
		}
		objectNode.set("labels", labelArrayNode);
		// 图片
		String path = Environment.getProperty("host") + Environment.getProperty("skin");
		ArrayNode annexArrayNode = JsonUtils.createArrayNode();
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
		objectNode.put("isCombination", groupOrder.getIsCombination());
		objectNode.put("beginTime", groupOrder.getBeginTime() == null ? "" : DateTimeUtils.format(groupOrder.getBeginTime(), "yyyy-MM-dd HH:mm:ss"));
		objectNode.put("endTime", groupOrder.getEndTime() == null ? "" : DateTimeUtils.format(groupOrder.getEndTime(), "yyyy-MM-dd HH:mm:ss"));
		objectNode.put("selfExtractingBeginTime", groupOrder.getSelfExtractingTime() == null ? "" : DateTimeUtils.format(groupOrder.getSelfExtractingTime(), "yyyy-MM-dd HH:mm:ss"));
		objectNode.put("selfExtractingEndTime", groupOrder.getSelfExtractingEndTime() == null ? "" : DateTimeUtils.format(groupOrder.getSelfExtractingEndTime(), "yyyy-MM-dd HH:mm:ss"));
		objectNode.put("province", groupOrder.getProvince() == null ? "" : groupOrder.getProvince());
		objectNode.put("city", groupOrder.getCity() == null ? "" : groupOrder.getCity());
		objectNode.put("area", groupOrder.getTownships() == null ? "" : groupOrder.getTownships());
		objectNode.put("street", groupOrder.getStreet() == null ? "" : groupOrder.getStreet());
		Community community = groupOrder.getCommunity();
		if (community == null) {
			objectNode.put("communityId", 0);
		} else {
			objectNode.put("communityId", community.getId());
		}
		objectNode.put("selfExtractingAddress", groupOrder.getSelfExtractingAddress() == null ? "" : groupOrder.getSelfExtractingAddress());// 详细地址
		objectNode.put("phone", groupOrder.getPhone() == null ? "" : groupOrder.getPhone());
		objectNode.put("notice", groupOrder.getNotice() == null ? "" : groupOrder.getNotice());
		objectNode.put("isRefund", groupOrder.getIsRefund() == null ? false : groupOrder.getIsRefund());
		// 商品品目信息
		ArrayNode products = JsonUtils.createArrayNode();
		for (GroupCommodityBasic gcb : groupOrder.getGroupCommodityBasic()) {
			ObjectNode node = JsonUtils.createObjectNode();
			node.put("itemId", gcb.getId());
			node.put("itemName", gcb.getName() == null ? "" : gcb.getName());
			node.put("basicPrice", gcb.getPrice() == null ? "" : gcb.getPrice().toString());
			node.put("isPack", gcb.getIsPack() == null ? false : gcb.getIsPack());
			node.put("basicSort", gcb.getSort() == null ? "" : gcb.getSort().toString());
			// 商品信息
			ArrayNode commodityArrayNode = JsonUtils.createArrayNode();
			for (GroupCommodity groupCommodity : gcb.getGroupCommoditys()) {
				ObjectNode no = JsonUtils.createObjectNode();
				no.put("commodityId", isAgain == true ? "" : groupCommodity.getId().toString());
				no.put("businessId", groupCommodity.getBusiness() == null ? "" : groupCommodity.getBusiness().getId().toString());
				no.put("commodityName", groupCommodity.getName() == null ? "" : groupCommodity.getName());
				no.put("originalPrice", groupCommodity.getOriginalPrice() == null ? "" : groupCommodity.getOriginalPrice().toString());
				no.put("price", groupCommodity.getPrice() == null ? "" : groupCommodity.getPrice().toString());
				no.put("description", groupCommodity.getDescription() == null ? "" : groupCommodity.getDescription());
				no.put("inventory", groupCommodity.getTotalInventory() == null ? "" : groupCommodity.getTotalInventory().toString());
				no.put("commodityUpperLimit", groupCommodity.getUpperlimit() == null ? "" : groupCommodity.getUpperlimit().toString());
				no.put("writeOffsNumber", groupCommodity.getWriteOffsNumber() == null ? "" : groupCommodity.getWriteOffsNumber().toString());
				no.put("details", groupCommodity.getDetails() == null ? "" : groupCommodity.getDetails());
				no.put("commodityIsUpgrade", groupCommodity.getIsUpgrade() == null ? false : groupCommodity.getIsUpgrade());
				no.put("isHidden", groupCommodity.getIsHidden());
				no.put("verify", groupCommodity.getVerify());
				no.put("sort", groupCommodity.getSort());
				no.put("integral", groupCommodity.getIntegral() == null ? 0 : groupCommodity.getIntegral());
				// 商品图片
				i = 1L;
				ArrayNode annexArray = JsonUtils.createArrayNode();
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
					annexArray.add(on);
				}
				no.set("commodityPics", annexArray);
				commodityArrayNode.add(no);
			}
			node.set("commodity", commodityArrayNode);
			products.add(node);
		}
		objectNode.set("products", products);
		response.outputJson(0, objectNode);
	}

	/** 删除商品品目或商品 */
	@Transactional
	@RequestMapping("/delete")
	public void delete(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
		String openId = request.getStringMust("openId");
		Long groupOrderId = request.getLongMust("groupOrderId");// 团单ID
		Long basicId = request.getLongMust("basicId");// 商品品目ID
		Long commodityId = request.getLong("commodityId");// 商品ID
		String deleteType = request.getStringMust("deleteType");// 刪除类型

		WeChatUser weChatUser = new WeChatUserQuery().openId(openId).uniqueResult();
		if (weChatUser == null) {
			response.outputJson(-1, "对不起，删除失败，未找到该用户");
			return;
		}
		GroupOrder groupOrder = new GroupOrderQuery().weChatUser(weChatUser).id(groupOrderId).uniqueResult();
		GroupCommodityBasic groupCommodityBasic = new GroupCommodityBasicQuery().groupOrder(groupOrder).id(basicId).uniqueResult();
		if (groupCommodityBasic == null) {
			response.outputJson(-1, "对不起，删除失败，未找到该商品品目");
			return;
		}
		// 删除商品
		if (deleteType.equals("SP")) {
			GroupCommodity groupCommodity = new GroupCommodityQuery().groupCommodityBasic(groupCommodityBasic).id(commodityId).uniqueResult();
			if (groupCommodity == null) {
				response.outputJson(-1, "对不起，删除失败，未找到该商品");
				return;
			}
			groupCommodity.remove();
		} else if (deleteType.equals("SPPM")) {// 刪除商品品目
			// 删除商品品目以及商品
			for (GroupCommodity groupCommodity : groupCommodityBasic.getGroupCommoditys()) {
				groupCommodity.remove();
			}
			groupCommodityBasic.remove();
		}
		response.outputJson(0, "刪除成功");
	}
}
