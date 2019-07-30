package cn.sini.cgb.common.entity;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;

import cn.sini.cgb.common.json.JsonUtils;

/**
 * 实体工具类<br/>
 * 兼容字段：当字段的类型为八大基本类型、String、Date、BigDecimal和Enum类型，并且该字段只有public/private/protected修饰符时，该字段为兼容字段。如：<br/>
 * 【兼容】private String id;<br/>
 * 【兼容】public Date createTime;<br/>
 * 【不兼容】private User user;<br/>
 * 【不兼容】private static String id;<br/>
 * 【不兼容】public static final String id;
 * 
 * @author 杨海彬
 */
public class EntityUtils {

	private static final int SUPPORT_FIELD_MODIFIERS = Modifier.PUBLIC | Modifier.PRIVATE | Modifier.PROTECTED;
	private static final Set<Class<?>> SUPPORT_FIELD_CLASS = new HashSet<Class<?>>();
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");

	static {
		SUPPORT_FIELD_CLASS.add(Boolean.TYPE);
		SUPPORT_FIELD_CLASS.add(Boolean.class);
		SUPPORT_FIELD_CLASS.add(Character.TYPE);
		SUPPORT_FIELD_CLASS.add(Character.class);
		SUPPORT_FIELD_CLASS.add(Byte.TYPE);
		SUPPORT_FIELD_CLASS.add(Byte.class);
		SUPPORT_FIELD_CLASS.add(Short.TYPE);
		SUPPORT_FIELD_CLASS.add(Short.class);
		SUPPORT_FIELD_CLASS.add(Integer.TYPE);
		SUPPORT_FIELD_CLASS.add(Integer.class);
		SUPPORT_FIELD_CLASS.add(Long.TYPE);
		SUPPORT_FIELD_CLASS.add(Long.class);
		SUPPORT_FIELD_CLASS.add(Float.TYPE);
		SUPPORT_FIELD_CLASS.add(Float.class);
		SUPPORT_FIELD_CLASS.add(Double.TYPE);
		SUPPORT_FIELD_CLASS.add(Double.class);
		SUPPORT_FIELD_CLASS.add(String.class);
		SUPPORT_FIELD_CLASS.add(Date.class);
		SUPPORT_FIELD_CLASS.add(BigDecimal.class);
	}

	/** 将实体里面所有兼容的字段转为json格式的字符串 */
	public static String toString(Object entity) {
		return toString(entity, null, null);
	}

	/** 将实体里面所有兼容的字段转为json格式的字符串,并可增加不兼容的字段 */
	public static String toStringInclude(Object entity, String... fields) {
		return toString(entity, fields, null);
	}

	/** 将实体里面所有兼容的字段转为json格式的字符串,并可排除指定字段 */
	public static String toStringExclude(Object entity, String... fields) {
		return toString(entity, null, fields);
	}

	/** 将实体里面所有兼容的字段转为json格式的字符串,并可增加不兼容的字段和排除指定字段 */
	public static String toString(Object entity, String[] includeFields, String[] excludeFields) {
		try {
			Class<?> clazz = entity.getClass();
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			while (clazz != null && !Object.class.equals(clazz)) {
				Field[] fields = clazz.getDeclaredFields();
				for (Field field : fields) {
					String fieldName = field.getName();
					if (ArrayUtils.contains(excludeFields, fieldName)) {
						continue;
					}
					Class<?> type = field.getType();
					if (field.getModifiers() <= SUPPORT_FIELD_MODIFIERS && (type.isEnum() || SUPPORT_FIELD_CLASS.contains(type)) || ArrayUtils.contains(includeFields, fieldName)) {
						field.setAccessible(true);
						Object value = field.get(entity);
						if (value != null && Date.class.equals(type)) {
							value = DATE_FORMAT.format(Date.class.cast(value));
						}
						map.put(fieldName, value);
					}
				}
				clazz = clazz.getSuperclass();
			}
			return JsonUtils.toObjectNode(map).toString();
		} catch (Exception e) {
			return entity.getClass().getName();
		}
	}
}