package cn.sini.cgb.common.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.sini.cgb.api.cgb.entity.group.WechatNotice;
import cn.sini.cgb.api.cgb.entity.group.WechatNotice.NoticeStatus;
import cn.sini.cgb.api.cgb.entity.group.WechatNotice.NoticeType;
import cn.sini.cgb.common.json.JsonUtils;
import cn.sini.cgb.common.json.JsonWrapper;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author gaowei
 * 
 * @date 2019年3月20日
 */
public class WechatUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(WechatUtils.class);
	private static Map<String, Map<String, Object>> accessTokenMap = new HashMap<String, Map<String, Object>>();
	/** 小程序appid */
	private static String APP_ID = "";
	/** 小程序secret */
	private static String APP_SECRET = "";

	static {
		APP_ID = Environment.getProperty("appId");
		APP_SECRET = Environment.getProperty("appSecret");
	}

	/** 获取AccessToken */
	public synchronized static String getAccessToken(String appId, String appSecret) {
		Map<String, Object> map = accessTokenMap.get(appId);
		if (map != null) {
			Long updateTime = (Long) map.get("updateTime");
			Long expiresIn = (Long) map.get("expiresIn");
			if (updateTime != null && expiresIn != null && (new Date().getTime() - updateTime) < expiresIn) {
				return (String) map.get("accessToken");
			}
		}
		String wechatAccessTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appId + "&secret=" + appSecret;
		ObjectNode objectNode = JsonUtils.toObjectNode(NetUtils.getSourceData(wechatAccessTokenUrl));
		LOGGER.info("【微信AccessToken】:" + objectNode);
		JsonWrapper result = new JsonWrapper(objectNode);
		String accessToken = result.getStringMust("access_token");
		map = new HashMap<String, Object>();
		map.put("accessToken", accessToken);
		map.put("updateTime", new Date().getTime());
		map.put("expiresIn", result.getLongMust("expires_in") / 3 * 1000);
		accessTokenMap.put(appId, map);
		return accessToken;
	}

	/**
	 * 发送模版消息（统一服务消息通知）
	 * 
	 * @param Long groupId
	 * @param String sendOpenId 发送人的openid
	 * @param formId FormId
	 * @param openId OpenId
	 * @param templateJson json模版
	 * @param templateId 消息提示模版Id
	 * @param noticeType 通知类型
	 */
	public static void sendTemplateMessage(Long groupId, String sendOpenId , String formId, String openId, ObjectNode templateJson, String templateId, NoticeType noticeType) {
		String accessToken = getAccessToken(APP_ID, APP_SECRET);
		String url = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/uniform_send?access_token=" + accessToken;
		// 模版数据拼接
		ObjectNode weapp_template_msg = JsonUtils.createObjectNode();
		weapp_template_msg.put("template_id", templateId);
		weapp_template_msg.put("page", "");
		weapp_template_msg.put("form_id", formId);
		weapp_template_msg.set("data", templateJson);
		weapp_template_msg.put("emphasis_keyword", "");

		ObjectNode node = JsonUtils.createObjectNode();
		node.put("touser", openId);
		node.set("weapp_template_msg", weapp_template_msg);
		node.put("mp_template_msg", "");
		LOGGER.info("【微信发送模版消息地址以及参数】：url：" + url + ",param：" + node);
		ObjectNode objectNode = JsonUtils.toObjectNode(NetUtils.getSourceData(url, node, true));
		LOGGER.info("【微信发送模版消息微信返回内容】：" + objectNode);
		JsonWrapper jsonWrapper = new JsonWrapper(objectNode);
		Long errcode = jsonWrapper.getLongMust("errcode");
		WechatNotice wechatNotice = new WechatNotice();
		wechatNotice.setData(templateJson.toString());
		wechatNotice.setNoticeType(noticeType);
		wechatNotice.setReturnMessage(objectNode.toString());
		wechatNotice.setTemplateId(templateId);
		wechatNotice.setOpenId(sendOpenId);
		wechatNotice.setGroupId(groupId);
		wechatNotice.setTouser(openId);
		wechatNotice.setUrl(null);
		if (errcode == 0L) {
			wechatNotice.setNoticeStatus(NoticeStatus.SUCCESS);
		} else {
			wechatNotice.setNoticeStatus(NoticeStatus.ERROR);
		}
		wechatNotice.saveOrUpdate();
	}
}
