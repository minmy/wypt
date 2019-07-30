package cn.sini.cgb.api.test;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;

import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.sini.cgb.common.json.JsonUtils;
import cn.sini.cgb.common.util.NetUtils;

public class TestMain {

	public static final String API_ROOT = "http://localhost:9061/cgb";
	public static final String APP_ID = "admin";
	public static final String APP_SECRET = "111111";
	public static final String SIGN_KEY = "072f09e3-700f-4348-9644-20cd8b2941a7";

	public static void main(String[] args) throws Exception {
		// 第1步 登录以获取accessToken
		String accessToken = getAccessToken();
		// 第2步 调用接口
		String url = urlAppendCommonParam(API_ROOT + "/api/user/list.action", accessToken);
		System.out.println(url);
		String sourceData = NetUtils.getSourceData(url);
		System.out.println("调用接口返回的内容为：" + sourceData);
		// 第3步 注销accessToken
		logout(accessToken);
	}

	/** 获取访问令牌 */
	public static String getAccessToken() throws Exception {
		String url = urlAppendCommonParam(API_ROOT + "/api/user/login.action?app_id=" + APP_ID + "&app_secret=" + APP_SECRET, "");
		System.out.println(url);
		ObjectNode objectNode = JsonUtils.toObjectNode(NetUtils.getSourceData(url));
		System.out.println(objectNode);
		String accessToken = objectNode.get("data").get("access_token").asText();
		System.out.println("登录获取到的accessToken为：" + accessToken);
		return accessToken;
	}

	/** 注销accessToken */
	public static void logout(String accessToken) throws Exception {
		String url = urlAppendCommonParam(API_ROOT + "/api/user/logout.action", accessToken);
		System.out.println(url);
		String sourceData = NetUtils.getSourceData(url);
		System.out.println("注销accessToken的返回内容为：" + sourceData);
	}

	/** 链接地址增加公共参数 */
	public static String urlAppendCommonParam(String url, String accessToken) {
		String timestamp = new Date().getTime() + "";
		String requestId = UUID.randomUUID().toString();
		String signature = DigestUtils.md5Hex(accessToken + timestamp + requestId + SIGN_KEY);
		url += ((url.contains("?") ? "&" : "?") + "access_token=" + accessToken + "&timestamp=" + timestamp + "&request_id=" + requestId + "&signature=" + signature);
		return url;
	}
}