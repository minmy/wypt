package cn.sini.cgb.common.util;

import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import cn.sini.cgb.common.exception.SystemException;

/**
 * 公共工具类
 * 
 * @author 杨海彬
 */
public class CommonUtils {

	/** 移除数组中的null或空白字符串元素 */
	@SuppressWarnings("unchecked")
	public static <T> T[] removeEmptyElement(T[] values) {
		if (values == null) {
			return null;
		}
		List<T> list = new ArrayList<T>();
		for (T value : values) {
			if (value == null || (value instanceof String && StringUtils.isEmpty((String) value))) {
				continue;
			}
			list.add(value);
		}
		T[] result = (T[]) Array.newInstance(values.getClass().getComponentType(), list.size());
		return list.toArray(result);
	}

	/** 格式化时间 */
	public static String formatDate(Date date, String pattern) {
		return date == null ? "" : DateTimeUtils.format(date, pattern);
	}

	/** 将字符串进行UTF-8编码 */
	public static String encode(String value) {
		try {
			return URLEncoder.encode(value, "UTF-8");
		} catch (Exception e) {
			return value;
		}
	}

	/** 截断字符串并增加后缀 */
	public static String substring(String value, Integer length, String suffix) {
		String result = value;
		if (value.length() > length) {
			result = value.substring(0, length) + suffix;
		}
		return result;
	}

	/** 分隔字符串 */
	public static String[] split(String value, String delimiters) {
		return StringUtils.split(value, delimiters);
	}

	/** 获取对象的大小 */
	public static int length(Object value) {
		if (value == null) {
			return 0;
		}
		if ((value instanceof String))
			return ((String) value).length();
		if ((value instanceof Collection))
			return ((Collection<?>) value).size();
		if ((value instanceof Map)) {
			return ((Map<?, ?>) value).size();
		}
		int count = 0;
		if ((value instanceof Iterator)) {
			Iterator<?> iter = (Iterator<?>) value;
			count = 0;
			while (iter.hasNext()) {
				count++;
				iter.next();
			}
			return count;
		}
		if ((value instanceof Enumeration)) {
			Enumeration<?> enum_ = (Enumeration<?>) value;
			count = 0;
			while (enum_.hasMoreElements()) {
				count++;
				enum_.nextElement();
			}
			return count;
		}
		try {
			return Array.getLength(value);
		} catch (Exception e) {
			throw new SystemException("获取对象大小异常", e);
		}
	}

	/** 将字符串转为类对象 */
	@SuppressWarnings("unchecked")
	public static <T> Class<? extends T> convertClass(String className) {
		try {
			return (Class<? extends T>) Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new SystemException("将字符串转为类对象时出现异常", e);
		}
	}
}