package cn.sini.cgb.common.http;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.node.ContainerNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.sini.cgb.common.json.JsonUtils;

/**
 * HttpServletResponse包装类
 * 
 * @author 杨海彬
 */
public class HttpResponseWrapper extends HttpServletResponseWrapper {

	/**
	 * 构造方法
	 * 
	 * @param response HttpServletResponse
	 */
	public HttpResponseWrapper(HttpServletResponse response) {
		super(response);
	}

	/**
	 * 响应输出普通字符串
	 * 
	 * @param result 要输出的字符串
	 */
	public void outputString(String result) {
		super.setContentType("text/html;charset=UTF-8");
		try {
			super.getWriter().write(result);
		} catch (Exception e) {
		}
	}

	/**
	 * 响应输出JSON字符串
	 * 
	 * @param status 响应状态
	 */
	public void outputJson(boolean status) {
		outputJson(status, null, null);
	}

	/**
	 * 响应输出JSON字符串
	 * 
	 * @param status  响应状态
	 * @param message 消息内容
	 */
	public void outputJson(boolean status, String message) {
		outputJson(status, message, null);
	}

	/**
	 * 响应输出JSON字符串
	 * 
	 * @param status 响应状态
	 * @param data   响应数据
	 */
	public void outputJson(boolean status, ContainerNode<?> data) {
		outputJson(status, null, data);
	}

	/**
	 * 响应输出JSON字符串
	 * 
	 * @param status  响应状态
	 * @param message 消息内容
	 * @param data    响应数据。可以是Json对象或Json数组
	 */
	public void outputJson(boolean status, String message, ContainerNode<?> data) {
		ObjectNode objectNode = JsonUtils.createObjectNode();
		objectNode.put("status", status);
		if (StringUtils.isNotEmpty(message)) {
			objectNode.put("message", message);
		}
		if (data != null) {
			objectNode.set("data", data);
		}
		outputJson(objectNode);
	}

	/**
	 * 响应输出JSON字符串
	 * 
	 * @param status 状态码
	 */
	public void outputJson(int status) {
		outputJson(status, null, null);
	}

	/**
	 * 响应输出JSON字符串
	 * 
	 * @param status  状态码
	 * @param message 消息内容
	 */
	public void outputJson(int status, String message) {
		outputJson(status, message, null);
	}

	/**
	 * 响应输出JSON字符串
	 * 
	 * @param status 状态码
	 * @param data   Json数据
	 */
	public void outputJson(int status, ContainerNode<?> data) {
		outputJson(status, null, data);
	}

	/**
	 * 响应输出JSON字符串
	 * 
	 * @param status  状态码
	 * @param message 消息内容
	 * @param data    Json数据
	 */
	public void outputJson(int status, String message, ContainerNode<?> data) {
		ObjectNode objectNode = JsonUtils.createObjectNode();
		objectNode.put("status", status);
		if (StringUtils.isNotEmpty(message)) {
			objectNode.put("message", message);
		}
		if (data != null) {
			objectNode.set("data", data);
		}
		outputJson(objectNode);
	}

	/**
	 * 响应输出JSON字符串
	 * 
	 * @param result 要输出的Json对象或Json数组
	 */
	public void outputJson(ContainerNode<?> result) {
		outputString(result.toString());
	}
}