package cn.sini.cgb.api.identity.action;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.LockMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.sini.cgb.admin.annex.entity.Annex;
import cn.sini.cgb.admin.annex.entity.Annex.AnnexType;
import cn.sini.cgb.admin.annex.query.AnnexQuery;
import cn.sini.cgb.admin.identity.entity.User;
import cn.sini.cgb.api.cgb.action.group.ShareTicketAction;
import cn.sini.cgb.api.cgb.entity.group.Community;
import cn.sini.cgb.api.cgb.entity.group.Follow;
import cn.sini.cgb.api.cgb.entity.group.GroupOrder;
import cn.sini.cgb.api.cgb.entity.group.Order;
import cn.sini.cgb.api.cgb.entity.group.Share;
import cn.sini.cgb.api.cgb.entity.group.ShareWeChatUser;
import cn.sini.cgb.api.cgb.entity.group.Township;
import cn.sini.cgb.api.cgb.entity.group.WeChatUser;
import cn.sini.cgb.api.cgb.query.group.CommunityQuery;
import cn.sini.cgb.api.cgb.query.group.FollowQuery;
import cn.sini.cgb.api.cgb.query.group.GroupOrderQuery;
import cn.sini.cgb.api.cgb.query.group.OrderQuery;
import cn.sini.cgb.api.cgb.query.group.ShareQuery;
import cn.sini.cgb.api.cgb.query.group.ShareWeChatUserQuery;
import cn.sini.cgb.api.cgb.query.group.TownshipQuery;
import cn.sini.cgb.api.cgb.query.group.WeChatUserQuery;
import cn.sini.cgb.api.identity.entity.ApiAccessToken;
import cn.sini.cgb.api.identity.entity.ApiRole;
import cn.sini.cgb.api.identity.query.ApiAccessTokenQuery;
import cn.sini.cgb.api.identity.query.ApiRoleQuery;
import cn.sini.cgb.common.http.HttpRequestWrapper;
import cn.sini.cgb.common.http.HttpResponseWrapper;
import cn.sini.cgb.common.json.JsonUtils;
import cn.sini.cgb.common.json.JsonWrapper;
import cn.sini.cgb.common.query.Page;
import cn.sini.cgb.common.util.DateTimeUtils;
import cn.sini.cgb.common.util.Environment;
import cn.sini.cgb.common.util.NetUtils;
import cn.sini.cgb.common.util.PayUtil;
import cn.sini.cgb.common.util.PinYinUtils;
import cn.sini.cgb.common.wxpay.AESUtil;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 接口用户Action
 * 
 * @author 杨海彬
 */
@Controller
@RequestMapping("/api/user")
public class ApiUserAction {

	private static Logger logger = Logger.getLogger(ApiUserAction.class);

	@Value("#{systemProperties['api.accessTokenPeriod']}")
	private Integer accessTokenPeriod;

	/** 登录获取AccessToken */
	@Transactional
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public void login(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
		String app_secret = request.getString("app_secret");
		String code = request.getString("code");
		if (StringUtils.isEmpty(app_secret)) {
			response.outputJson(40003, "缺少app_secret参数");
			return;
		}
		if (StringUtils.isEmpty(code)) {
			response.outputJson(40014, "缺少code参数");
			return;
		}
		User user = request.getApiLoginUser();
		if (!user.isRealPassword(app_secret)) {
			response.outputJson(40004, "无效的app_secret参数");
			return;
		}
		// 获取微信用户唯一标识openId和session_key
		String openId = "";
		String appId = Environment.getProperty("appId");
		String appSecret = Environment.getProperty("appSecret");
		String sourceData = NetUtils.getSourceData("https://api.weixin.qq.com/sns/jscode2session?appid=" + appId + "&secret=" + appSecret + "&js_code=" + code + "&grant_type=authorization_code");
		logger.info("【微信返回数据】：" + sourceData);
		JsonWrapper jw = new JsonWrapper(JsonUtils.toObjectNode(sourceData));
		Integer errcode = jw.getInteger("errcode");
		WeChatUser weChatUser = null;
		if (errcode == null || errcode == 0) {
			String sessionKey = jw.getStringMust("session_key");
			openId = jw.getStringMust("openid");
			weChatUser = new WeChatUserQuery().openId(openId).uniqueResult();
			if (weChatUser == null) {
				weChatUser = new WeChatUser();
				weChatUser.setOpenId(openId);
				weChatUser.setUnionId("");
			}
			weChatUser.setContacts(request.getString("name"));
			weChatUser.setSessionKey(sessionKey);
			weChatUser.setName(request.getString("name"));
			weChatUser.setHeadImgUrl(request.getString("headImgUrl"));
			// 授予接口普通角色
			ApiRole apiRole = new ApiRoleQuery().name("普通角色").readOnly().uniqueResult();
			if (!weChatUser.getApiRoles().contains(apiRole)) {
				weChatUser.getApiRoles().add(apiRole);
			}
			weChatUser.saveOrUpdate();
			PayUtil payUtil = new PayUtil();
			payUtil.createVirtualAccount(openId);
		} else {
			response.outputJson(errcode, "获取微信用户标识失败：" + jw.getString("errmsg"));
			return;
		}
		ApiAccessToken apiAccessToken = new ApiAccessToken();
		apiAccessToken.setExpireTime(DateTimeUtils.addSecond(apiAccessToken.getCreateTime(), this.accessTokenPeriod));
		apiAccessToken.setUser(user);
		apiAccessToken.saveOrUpdate();
		ObjectNode objectNode = JsonUtils.createObjectNode();
		objectNode.put("access_token", apiAccessToken.getAccessToken());
		objectNode.put("expires_in", this.accessTokenPeriod);
		objectNode.put("openId", openId);
		objectNode.put("isBusiness", weChatUser.getIsBusiness() == null ? false : weChatUser.getIsBusiness());
		objectNode.put("communityId", weChatUser.getCommunity() == null ? 0 : weChatUser.getCommunity().getId());
		response.outputJson(0, objectNode);
	}

	/** 微信用户信息(微信用户信息密文处理) */
	@Transactional
	@RequestMapping(value = "/weChatCiphertext", method = RequestMethod.POST)
	public void weChatCiphertext(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
		String openId = request.getString("openId");
		String encryptedData = request.getString("encryptedData");
		String iv = request.getString("iv");
		if (StringUtils.isEmpty(openId)) {
			response.outputJson(-1, "缺少openId参数");
			return;
		}
		if (StringUtils.isEmpty(encryptedData)) {
			response.outputJson(-1, "缺少encryptedData参数");
			return;
		}
		if (StringUtils.isEmpty(iv)) {
			response.outputJson(-1, "缺少iv参数");
			return;
		}
		WeChatUser weChatUser = new WeChatUserQuery().openId(openId).uniqueResult();
		if (weChatUser == null) {
			response.outputJson(-1, "缺少用户");
			return;
		}
		String sessionKey = weChatUser.getSessionKey();
		ObjectNode decryptData = AESUtil.decryptData(encryptedData, sessionKey, iv);
		logger.info("【微信数据解密参数及结果】：encryptedData:" + encryptedData + ",sessionKey:" + sessionKey + ",iv:" + iv + ",decryptData:" + decryptData);
		if (decryptData != null) {
			JsonWrapper wrapper = new JsonWrapper(decryptData);
			String unionId = wrapper.getString("unionId");
			weChatUser.setUnionId(unionId);
			weChatUser.setName(wrapper.getString("nickName"));
			weChatUser.setHeadImgUrl(wrapper.getString("avatarUrl"));
			weChatUser.saveOrUpdate();
		}
		response.outputJson(0);
	}

	/** 注销AccessToekn */
	@Transactional
	@RequestMapping("/logout")
	public void logout(HttpRequestWrapper request, HttpResponseWrapper response) {
		ApiAccessToken apiAccessToken = new ApiAccessTokenQuery().user(request.getApiLoginUser()).accessToken(request.getTrim("access_token")).uniqueResult();
		if (apiAccessToken != null) {
			apiAccessToken.remove();
		}
		response.outputJson(0);
	}

	/** 查询用户个人信息 */
	@RequestMapping(value = "/getWechatUserInfo", method = RequestMethod.POST)
	public void getWechatUserInfo(HttpRequestWrapper request, HttpResponseWrapper response) {
		String openId = request.getString("openId");
		if (StringUtils.isEmpty(openId)) {
			response.outputJson(-1, "缺少openId参数");
			return;
		}
		WeChatUser weChatUser = new WeChatUserQuery().openId(openId).readOnly().uniqueResult();
		if (weChatUser == null) {
			response.outputJson(-1, "查询失败，未找到该用户");
			return;
		}
		long createTime = weChatUser.getCreateTime().getTime();
		long thisTime = System.currentTimeMillis();
		long remainingDays = 100 - ((thisTime - createTime) / 1000 / 60 / 60 / 24);// 计算剩余天数
		ObjectNode objectNode = JsonUtils.createObjectNode();
		objectNode.put("id", weChatUser.getId());
		objectNode.put("createTime", DateTimeUtils.format(weChatUser.getCreateTime(), "yyyy-MM-dd"));
		objectNode.put("openId", weChatUser.getOpenId());
		objectNode.put("phone", weChatUser.getPhone());
		objectNode.put("contacts", weChatUser.getContacts());
		objectNode.put("province", weChatUser.getProvince());
		objectNode.put("city", weChatUser.getCity());
		objectNode.put("area", weChatUser.getArea());
		objectNode.put("communityId", weChatUser.getCommunity() == null ? 0 : weChatUser.getCommunity().getId());
		objectNode.put("communityName", weChatUser.getCommunity() == null ? "" : weChatUser.getCommunity().getName());
		objectNode.put("address", weChatUser.getAddress());
		objectNode.put("remainingDays", remainingDays < 0 ? 0 : remainingDays);
		objectNode.put("applyPhone", weChatUser.getApplyPhone());
		objectNode.put("applyRealName", weChatUser.getApplyRealName());
		objectNode.put("applyWeChat", weChatUser.getApplyWeChat());
		ArrayNode townshipArrayNode = JsonUtils.createArrayNode();

		// List<TaAdministrativeDivision> tadList = new CommunityQuery().groupBy("town").list();
		// for (TaAdministrativeDivision tad : tadList) {
		// ObjectNode node = JsonUtils.createObjectNode();
		// node.put("townshipId", tad.getId());
		// node.put("townshipCode", tad.getCode());
		// node.put("townshipName", tad.getName());
		// ArrayNode communityArrayNode = JsonUtils.createArrayNode();
		// for (Community community : tad.getCommunities()) {
		// ObjectNode no = JsonUtils.createObjectNode();
		// no.put("communityId", community.getId());
		// no.put("communityName", community.getName());
		// communityArrayNode.add(no);
		// }
		// node.set("communitys", communityArrayNode);
		// townshipArrayNode.add(node);
		// }
		List<Township> townshipList = new TownshipQuery().list();
		Collections.sort(townshipList, new Comparator<Township>() {
			public int compare(Township township1, Township township2) {
				char[] charArray1 = township1.getName().toCharArray();
				char[] charArray2 = township2.getName().toCharArray();
				String para1 = PinYinUtils.chineseToPinyin(charArray1);
				String para2 = PinYinUtils.chineseToPinyin(charArray2);
				return para1.compareTo(para2);
			}
		});
		for (Township township : townshipList) {
			ObjectNode node = JsonUtils.createObjectNode();
			node.put("townshipId", township.getId());
			node.put("townshipCode", township.getCode());
			node.put("townshipName", township.getName());
			ArrayNode communityArrayNode = JsonUtils.createArrayNode();
			for (Community community : township.getCommunities()) {
				ObjectNode no = JsonUtils.createObjectNode();
				no.put("communityId", community.getId());
				no.put("communityName", community.getName());
				communityArrayNode.add(no);
			}
			node.set("communitys", communityArrayNode);
			townshipArrayNode.add(node);
		}
		objectNode.set("townships", townshipArrayNode);
		response.outputJson(0, objectNode);
	}

	/** 保存用户个人信息 */
	@Transactional
	@RequestMapping(value = "/saveWechatUserInfo", method = RequestMethod.POST)
	public void saveWechatUserInfo(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
		String openId = request.getString("openId");
		String phone = request.getTrim("phone", "^1\\d{10}$");
		String contacts = request.getTrim("contacts");
		String province = request.getTrim("province");// 省
		String city = request.getTrim("city");// 市
		String area = request.getTrim("area");// 镇区code
		Long communityId = request.getLong("communityId");// 小区
		// String address = request.getTrim("address");// 详细地址（暂时隐藏）
		if (StringUtils.isEmpty(openId)) {
			response.outputJson(-1, "缺少openId参数");
			return;
		}
		WeChatUser weChatUser = new WeChatUserQuery().openId(openId).uniqueResult();
		if (weChatUser == null) {
			response.outputJson(-1, "保存失败，未找到该用户");
			return;
		}
		Community community = null;
		if (communityId != null) {
			community = new CommunityQuery().townshipQuery(new TownshipQuery().code(area)).id(communityId).uniqueResult();
			if (community == null) {
				response.outputJson(-1, "保存失败，未找到该小区");
				return;
			}
		}
		weChatUser.setPhone(phone);
		weChatUser.setContacts(contacts);
		weChatUser.setProvince(province);
		weChatUser.setCity(city);
		weChatUser.setArea(area);
		weChatUser.setCommunity(community);
		// weChatUser.setAddress("");
		weChatUser.saveOrUpdate();
		response.outputJson(0, "保存成功");
	}

	/** 保存分享信息 */
	@Transactional
	@RequestMapping(value = "/saveShare", method = RequestMethod.POST)
	public synchronized void saveShare(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
		String openId = request.getString("openId");
		Long groupOrderId = request.getLong("groupOrderId");
		String annexId = request.getString("annexId");
		String randomNumber = request.getString("randomNumber");
		if (StringUtils.isEmpty(openId)) {
			response.outputJson(-1, "缺少openId参数");
			return;
		}
		if (groupOrderId == null || groupOrderId == 0) {
			response.outputJson(-1, "缺少groupOrderId参数");
			return;
		}
		if (StringUtils.isEmpty(randomNumber)) {
			response.outputJson(-1, "缺少randomNumber参数");
			return;
		}
		WeChatUser weChatUser = new WeChatUserQuery().openId(openId).uniqueResult();
		if (weChatUser == null) {
			response.outputJson(-1, "保存失败，未找到该用户");
			return;
		}
		GroupOrder groupOrder = new GroupOrderQuery().id(groupOrderId).uniqueResult();
		if (groupOrder == null) {
			response.outputJson(-1, "保存失败，未找到该团单");
			return;
		}
		Share share = new ShareQuery().weChatUser(weChatUser).groupOrder(groupOrder).lockMode(LockMode.UPGRADE).orderBy("createTime", false).firstResult();
		if (share == null) {
			share = new Share();
			share.setWeChatUser(weChatUser);
			share.setGroupOrder(groupOrder);
			share.setRandomNumber(randomNumber);
			share.saveOrUpdate();
			if (StringUtils.isNotBlank(annexId)) {
				Annex annex = new AnnexQuery().annexType(AnnexType.SHARE_PIC).id(annexId).uniqueResult();
				annex.setShare(share);
				annex.saveOrUpdate();
			}
		} else {
			long createTime = share.getCreateTime().getTime();
			long thisTime = System.currentTimeMillis();
			// 如果是同一个用户，分享同一个团单，距上一次分享的时间需要超过3秒
			if (thisTime - createTime > 3000) {
				share = new Share();
				share.setWeChatUser(weChatUser);
				share.setGroupOrder(groupOrder);
				share.setRandomNumber(randomNumber);
				share.saveOrUpdate();
				if (StringUtils.isNotBlank(annexId)) {
					Annex annex = new AnnexQuery().annexType(AnnexType.SHARE_PIC).id(annexId).uniqueResult();
					annex.setShare(share);
					annex.saveOrUpdate();
				}
			}
		}
		response.outputJson(0, "分享成功");
	}

	/** 获取分享的随机数 */
	@RequestMapping(value = "/getShareRandomNumber", method = RequestMethod.POST)
	public void getShareRandomNumber(HttpRequestWrapper request, HttpResponseWrapper response) {
		String randomNumber = generateRandomNumber(4);
		response.outputJson(0, JsonUtils.createObjectNode().put("randomNumber", randomNumber));
	}

	/** 生成随机数 */
	public static String generateRandomNumber(Integer digits) {
		long time = new Date().getTime();
		String range = "0123456789";
		String randomNumber = "";
		for (int i = 0; i < digits; i++) {
			int rand = (int) (Math.random() * range.length());
			randomNumber += range.charAt(rand);
		}
		randomNumber = time + randomNumber;
		Share share = new ShareQuery().randomNumber(randomNumber).uniqueResult();
		if (share == null) {
			return randomNumber;
		} else {
			return generateRandomNumber(4);
		}
	}

	/** 保存被分享用户的记录（被邀请用户的访问记录） */
	@Transactional
	@RequestMapping(value = "/saveInvited", method = RequestMethod.POST)
	public void saveInvited(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
		String openId = request.getStringMust("openId");
		String randomNumber = request.getString("randomNumber");
		WeChatUser weChatUser = new WeChatUserQuery().openId(openId).uniqueResult();
		if (weChatUser == null) {
			response.outputJson(-1, "保存失败，未找到该用户");
			return;
		}

		if (StringUtils.isNotBlank(randomNumber)) {
			Share share = new ShareQuery().randomNumber(randomNumber).uniqueResult();
			if (share == null) {
				response.outputJson(-1, "保存失败，未找到该分享记录");
				return;
			}
//			new ShareTicketAction().getShareTicketByShare(openId, randomNumber);
			// 如果分享人和被分享人是同一个用户，则不添加记录
			if (!share.getWeChatUser().getOpenId().equals(openId)) {
				ShareWeChatUser shareWeChatUser = new ShareWeChatUserQuery().weChatUser(weChatUser).share(share).uniqueResult();
				if (shareWeChatUser == null) {
					shareWeChatUser = new ShareWeChatUser();
					shareWeChatUser.setWeChatUser(weChatUser);
					shareWeChatUser.setShare(share);
					shareWeChatUser.setUpdateTime(new Date());
				}
				shareWeChatUser.saveOrUpdate();
			}
		}
		response.outputJson(0, "保存成功");
	}

	/** 用户分享列表 */
	@RequestMapping(value = "/shareList", method = RequestMethod.POST)
	public void shareList(HttpRequestWrapper request, HttpResponseWrapper response) {
		String openId = request.getString("openId");
		if (StringUtils.isEmpty(openId)) {
			response.outputJson(-1, "缺少openId参数");
			return;
		}
		WeChatUser weChatUser = new WeChatUserQuery().openId(openId).uniqueResult();
		if (weChatUser == null) {
			response.outputJson(-1, "保存失败，未找到该用户");
			return;
		}
		Community community = weChatUser.getCommunity();
		Page<Share> page = new ShareQuery().weChatUser(weChatUser).readOnly().orderBy("createTime", false).pageHasCount(request.getPageNum(), request.getPageSize());
		ObjectNode objectNode = JsonUtils.createObjectNode();
		ArrayNode arrayNode = JsonUtils.createArrayNode();
		String path = Environment.getProperty("host") + Environment.getProperty("skin");
		for (Share share : page.getRecordList()) {
			GroupOrder groupOrder = share.getGroupOrder();
			ObjectNode node = JsonUtils.createObjectNode();
			node.put("groupOrderId", groupOrder.getId());
			node.put("theme", groupOrder.getTheme());
			node.put("createTime", DateTimeUtils.format(share.getCreateTime(), "MM-dd HH:mm"));
			node.put("headImgUrl", groupOrder.getWeChatUser().getHeadImgUrl());
			node.put("name", groupOrder.getWeChatUser().getName());
			// 团单图片
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
			node.put("groupType", groupOrder.getGroupType() == null ? "PTT" : groupOrder.getGroupType().toString());
			// 被分享者信息(被邀请人)
			ArrayNode inviteeArrayNode = JsonUtils.createArrayNode();
			for (ShareWeChatUser shareWeChatUser : share.getShareWeChatUsers()) {
				ObjectNode shareWeChatUserNode = JsonUtils.createObjectNode();
				WeChatUser user = shareWeChatUser.getWeChatUser();
				shareWeChatUserNode.put("shareHeadImgUrl", user.getHeadImgUrl());
				shareWeChatUserNode.put("shareName", user.getContacts().substring(0, 1) + "**");
				shareWeChatUserNode.put("updateTime", DateTimeUtils.format(shareWeChatUser.getUpdateTime(), "yyyy-MM-dd HH:mm"));
				inviteeArrayNode.add(shareWeChatUserNode);
			}
			node.set("inviteeArray", inviteeArrayNode);
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

	/** 保存关注 */
	@Transactional
	@RequestMapping(value = "/saveFollow", method = RequestMethod.POST)
	public void saveFollow(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
		Long followId = request.getLong("followId");
		String openId = request.getString("openId");
		String followOpenId = request.getString("followOpenId");
		if (StringUtils.isEmpty(openId)) {
			response.outputJson(-1, "缺少openId参数");
			return;
		}
		if (StringUtils.isEmpty(followOpenId)) {
			response.outputJson(-1, "缺少groupOrderId参数");
			return;
		}
		WeChatUser weChatUser = new WeChatUserQuery().openId(openId).uniqueResult();
		if (weChatUser == null) {
			response.outputJson(-1, "保存失败，未找到当前用户");
			return;
		}
		WeChatUser followWeChatUser = new WeChatUserQuery().openId(followOpenId).uniqueResult();
		if (followWeChatUser == null) {
			response.outputJson(-1, "保存失败，未找到关注用户");
			return;
		}
		Follow follow = null;
		String msg = "";
		// id为空，则表示用户关注。不为空，则表示用户取消关注
		if (followId == null) {
			// 用户关注时，判断数据库是否已有记录
			follow = new FollowQuery().weChatUser(weChatUser).followUser(followWeChatUser).includeRemove().uniqueResult();
			if (follow != null) {
				follow.setRemove(false);
				follow.setRemoveTime(null);
			} else {
				follow = new Follow();
				follow.setWeChatUser(weChatUser);
				follow.setFollowUser(followWeChatUser);
			}
			follow.saveOrUpdate();
			msg = "关注成功";
		} else {
			follow = new FollowQuery().weChatUser(weChatUser).followUser(followWeChatUser).id(followId).uniqueResult();
			if (follow == null) {
				response.outputJson(-1, "取消失败,未找到关注信息");
				return;
			}
			follow.remove();
			msg = "取消成功";
		}
		response.outputJson(0, msg);
	}

	/** 用户关注列表 */
	@RequestMapping(value = "/followList", method = RequestMethod.POST)
	public void followList(HttpRequestWrapper request, HttpResponseWrapper response) {
		String openId = request.getString("openId");
		Integer pageNum = request.getInteger("pageNum");
		Integer pageSize = request.getInteger("pageSize");
		pageNum = pageNum == null ? request.getPageNum() : pageNum;
		pageSize = pageSize == null ? request.getPageSize() : pageSize;
		if (StringUtils.isEmpty(openId)) {
			response.outputJson(-1, "缺少openId参数");
			return;
		}
		WeChatUser weChatUser = new WeChatUserQuery().openId(openId).uniqueResult();
		if (weChatUser == null) {
			response.outputJson(-1, "保存失败，未找到该用户");
			return;
		}
		Page<Follow> page = new FollowQuery().weChatUser(weChatUser).readOnly().orderBy("createTime", false).pageHasCount(pageNum, pageSize);
		ObjectNode objectNode = JsonUtils.createObjectNode();
		ArrayNode arrayNode = JsonUtils.createArrayNode();
		for (Follow follow : page.getRecordList()) {
			WeChatUser followUser = follow.getFollowUser();
			ObjectNode node = JsonUtils.createObjectNode();
			node.put("followId", follow.getId());
			node.put("followOpenId", followUser.getOpenId());
			node.put("headImgUrl", followUser.getHeadImgUrl());
			node.put("name", followUser.getName());
			node.put("phone", followUser.getPhone() == null ? "" : followUser.getPhone());
			arrayNode.add(node);
		}
		objectNode.set("recordList", arrayNode);
		objectNode.put("totalPage", page.getTotalPage());
		objectNode.put("totalRecord", page.getTotalRecord());
		objectNode.put("pageNum", page.getPageNum());
		objectNode.put("pageSize", page.getPageSize());
		response.outputJson(0, objectNode);
	}
}