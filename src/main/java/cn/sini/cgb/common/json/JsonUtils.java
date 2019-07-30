package cn.sini.cgb.common.json;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.sini.cgb.common.exception.SystemException;

/**
 * Json工具类
 * 
 * @author 杨海彬
 */
public class JsonUtils {

	/** 创建ObjectMapper对象 */
	public static ObjectMapper createObjectMapper() {
		return new ObjectMapper();
	}

	/** 创建ObjectNode对象 */
	public static ObjectNode createObjectNode() {
		return createObjectMapper().createObjectNode();
	}

	/** 创建ArrayNode对象 */
	public static ArrayNode createArrayNode() {
		return createObjectMapper().createArrayNode();
	}

	/** 将指定值转换为ObjectNode对象 */
	public static ObjectNode toObjectNode(Object value) {
		return toObjectNode(createObjectMapper(), value);
	}

	/** 根据ObjectMapper将指定值转换为ObjectNode对象 */
	public static ObjectNode toObjectNode(ObjectMapper objectMapper, Object value) {
		try {
			if (value instanceof String) {
				return StringUtils.isEmpty(value.toString()) ? objectMapper.createObjectNode() : objectMapper.readValue(value.toString(), ObjectNode.class);
			} else {
				return objectMapper.convertValue(value, ObjectNode.class);
			}
		} catch (Exception e) {
			throw new SystemException("将【" + value + "】转换为ObjectNode对象出现异常", e);
		}
	}

	/** 将指定值转换为ArrayNode对象 */
	public static ArrayNode toArrayNode(Object value) {
		return toArrayNode(createObjectMapper(), value);
	}

	/** 根据ObjectMapper将指定值转换为ArrayNode对象 */
	public static ArrayNode toArrayNode(ObjectMapper objectMapper, Object value) {
		try {
			if (value instanceof String) {
				return StringUtils.isEmpty(value.toString()) ? objectMapper.createArrayNode() : objectMapper.readValue(value.toString(), ArrayNode.class);
			} else {
				return objectMapper.convertValue(value, ArrayNode.class);
			}
		} catch (Exception e) {
			throw new SystemException("将【" + value + "】转换为ArrayNode对象出现异常", e);
		}
	}
}