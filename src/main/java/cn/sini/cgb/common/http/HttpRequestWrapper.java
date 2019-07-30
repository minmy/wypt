package cn.sini.cgb.common.http;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Enumeration;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.sini.cgb.admin.identity.entity.User;
import cn.sini.cgb.common.exception.ParamException;
import cn.sini.cgb.common.json.JsonUtils;
import cn.sini.cgb.common.util.CommonUtils;
import cn.sini.cgb.common.util.DateTimeUtils;

/**
 * HttpServletRequest包装类
 * 
 * @author 杨海彬
 */
public class HttpRequestWrapper extends HttpServletRequestWrapper {

	/**
	 * 构造方法
	 * 
	 * @param request HttpServletRequest
	 */
	public HttpRequestWrapper(HttpServletRequest request) {
		super(request);
	}

	/** 获取参数值 */
	private String getValue(String name, boolean trim, boolean must) {
		String value = trim ? StringUtils.trim(super.getParameter(name)) : super.getParameter(name);
		if (must && StringUtils.isEmpty(value)) {
			throw new ParamException("请求参数【" + name + "】不能为空");
		}
		return value;
	}

	/** 获取参数数组值 */
	private String[] getArray(String name, boolean trim, boolean must) {
		String[] values = super.getParameterValues(name);
		if (values != null && trim) {
			for (int i = 0; i < values.length; i++) {
				values[i] = StringUtils.trim(values[i]);
			}
		}
		values = CommonUtils.removeEmptyElement(values);
		if (must && ArrayUtils.isEmpty(values)) {
			throw new ParamException("请求参数【" + name + "】不能为空");
		}
		return values;
	}

	/** 获取Boolean值，允许返回空值 */
	public Boolean getBoolean(String name) {
		return getBoolean(name, false);
	}

	/** 获取Boolean值，不允许返回空值 */
	public Boolean getBooleanMust(String name) {
		return getBoolean(name, true);
	}

	/** 获取Boolean值 */
	private Boolean getBoolean(String name, boolean must) {
		String value = getValue(name, true, must);
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		try {
			return Boolean.valueOf(value);
		} catch (Exception e) {
			throw new ParamException("请求参数【" + name + "】格式不正确");
		}
	}

	/** 获取Boolean数组值，允许返回空值 */
	public Boolean[] getBooleans(String name) {
		return getBooleans(name, false);
	}

	/** 获取Boolean数组值，不允许返回空值 */
	public Boolean[] getBooleansMust(String name) {
		return getBooleans(name, true);
	}

	/** 获取Boolean数组值 */
	private Boolean[] getBooleans(String name, boolean must) {
		String[] values = getArray(name, true, must);
		if (values == null) {
			return null;
		}
		try {
			Boolean[] result = new Boolean[values.length];
			for (int i = 0; i < result.length; i++) {
				result[i] = Boolean.valueOf(values[i]);
			}
			return result;
		} catch (Exception e) {
			throw new ParamException("请求参数【" + name + "】格式不正确");
		}
	}

	/** 获取Integer值，允许返回空值 */
	public Integer getInteger(String name) {
		return getInteger(name, false, null, null);
	}

	/** 获取Integer值，并限制最小值和最大值，允许返回空值 */
	public Integer getInteger(String name, Integer min, Integer max) {
		return getInteger(name, false, min, max);
	}

	/** 获取Integer值，不允许返回空值 */
	public Integer getIntegerMust(String name) {
		return getInteger(name, true, null, null);
	}

	/** 获取Integer值，并限制最小值和最大值，不允许返回空值 */
	public Integer getIntegerMust(String name, Integer min, Integer max) {
		return getInteger(name, true, min, max);
	}

	/** 获取Integer值 */
	private Integer getInteger(String name, boolean must, Integer min, Integer max) {
		String value = getValue(name, true, must);
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		try {
			Integer result = Integer.valueOf(value);
			if (min != null && result < min || max != null && result > max) {
				throw new Exception();
			}
			return result;
		} catch (Exception e) {
			throw new ParamException("请求参数【" + name + "】格式不正确");
		}
	}

	/** 获取Integer数组值，允许返回空值 */
	public Integer[] getIntegers(String name) {
		return getIntegers(name, false, null, null);
	}

	/** 获取Integer数组值，并限制每个元素的最小值和最大值，允许返回空值 */
	public Integer[] getIntegers(String name, Integer min, Integer max) {
		return getIntegers(name, false, min, max);
	}

	/** 获取Integer数组值，不允许返回空值 */
	public Integer[] getIntegersMust(String name) {
		return getIntegers(name, true, null, null);
	}

	/** 获取Integer数组值，并限制每个元素的最小值和最大值，不允许返回空值 */
	public Integer[] getIntegersMust(String name, Integer min, Integer max) {
		return getIntegers(name, true, min, max);
	}

	/** 获取Integer数组值 */
	private Integer[] getIntegers(String name, boolean must, Integer min, Integer max) {
		String[] values = getArray(name, true, must);
		if (values == null) {
			return null;
		}
		try {
			Integer[] result = new Integer[values.length];
			for (int i = 0; i < result.length; i++) {
				result[i] = Integer.valueOf(values[i]);
				if (min != null && result[i] < min || max != null && result[i] > max) {
					throw new Exception();
				}
			}
			return result;
		} catch (Exception e) {
			throw new ParamException("请求参数【" + name + "】格式不正确");
		}
	}

	/** 获取Long值，允许返回空值 */
	public Long getLong(String name) {
		return getLong(name, false, null, null);
	}

	/** 获取Long值，并限制最小值和最大值，允许返回空值 */
	public Long getLong(String name, Long min, Long max) {
		return getLong(name, false, min, max);
	}

	/** 获取Long值，不允许返回空值 */
	public Long getLongMust(String name) {
		return getLong(name, true, null, null);
	}

	/** 获取Long值，并限制最小值和最大值，不允许返回空值 */
	public Long getLongMust(String name, Long min, Long max) {
		return getLong(name, true, min, max);
	}

	/** 获取Long值 */
	private Long getLong(String name, boolean must, Long min, Long max) {
		String value = getValue(name, true, must);
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		try {
			Long result = Long.valueOf(value);
			if (min != null && result < min || max != null && result > max) {
				throw new Exception();
			}
			return result;
		} catch (Exception e) {
			throw new ParamException("请求参数【" + name + "】格式不正确");
		}
	}

	/** 获取Long数组值，允许返回空值 */
	public Long[] getLongs(String name) {
		return getLongs(name, false, null, null);
	}

	/** 获取Long数组值，并限制每个元素的最小值和最大值，允许返回空值 */
	public Long[] getLongs(String name, Long min, Long max) {
		return getLongs(name, false, min, max);
	}

	/** 获取Long数组值，不允许返回空值 */
	public Long[] getLongsMust(String name) {
		return getLongs(name, true, null, null);
	}

	/** 获取Long数组值，并限制每个元素的最小值和最大值，不允许返回空值 */
	public Long[] getLongsMust(String name, Long min, Long max) {
		return getLongs(name, true, min, max);
	}

	/** 获取Long数组值 */
	private Long[] getLongs(String name, boolean must, Long min, Long max) {
		String[] values = getArray(name, true, must);
		if (values == null) {
			return null;
		}
		try {
			Long[] result = new Long[values.length];
			for (int i = 0; i < result.length; i++) {
				result[i] = Long.valueOf(values[i]);
				if (min != null && result[i] < min || max != null && result[i] > max) {
					throw new Exception();
				}
			}
			return result;
		} catch (Exception e) {
			throw new ParamException("请求参数【" + name + "】格式不正确");
		}
	}

	/** 获取BigDecimal值，允许返回空值 */
	public BigDecimal getBigDecimal(String name) {
		return getBigDecimal(name, false, null, null, null, null);
	}

	/** 获取BigDecimal值，并限制最小值和最大值，允许返回空值 */
	public BigDecimal getBigDecimal(String name, Number min, Number max) {
		return getBigDecimal(name, false, min, max, null, null);
	}

	/** 获取BigDecimal值，并限制最小值、最大值、最大整数位长度和最大小数位长度，允许返回空值 */
	public BigDecimal getBigDecimal(String name, Number min, Number max, Integer maxIntegerLength, Integer maxDecimalLength) {
		return getBigDecimal(name, false, min, max, maxIntegerLength, maxDecimalLength);
	}

	/** 获取BigDecimal值，不允许返回空值 */
	public BigDecimal getBigDecimalMust(String name) {
		return getBigDecimal(name, true, null, null, null, null);
	}

	/** 获取BigDecimal值，并限制最小值和最大值，不允许返回空值 */
	public BigDecimal getBigDecimalMust(String name, Number min, Number max) {
		return getBigDecimal(name, true, min, max, null, null);
	}

	/** 获取BigDecimal值，并限制最小值、最大值、最大整数位长度和最大小数位长度，不允许返回空值 */
	public BigDecimal getBigDecimalMust(String name, Number min, Number max, Integer maxIntegerLength, Integer maxDecimalLength) {
		return getBigDecimal(name, true, min, max, maxIntegerLength, maxDecimalLength);
	}

	/** 获取BigDecimal值 */
	private BigDecimal getBigDecimal(String name, boolean must, Number min, Number max, Integer maxIntegerLength, Integer maxDecimalLength) {
		String value = getValue(name, true, must);
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		try {
			BigDecimal result = new BigDecimal(value);
			if (min != null && result.doubleValue() < min.doubleValue() || max != null && result.doubleValue() > max.doubleValue() || maxIntegerLength != null && (result.precision() - result.scale()) > maxIntegerLength
					|| maxDecimalLength != null && result.scale() > maxDecimalLength) {
				throw new Exception();
			}
			return result;
		} catch (Exception e) {
			throw new ParamException("请求参数【" + name + "】格式不正确");
		}
	}

	/** 获取BigDecimal数组值，允许返回空值 */
	public BigDecimal[] getBigDecimals(String name) {
		return getBigDecimals(name, false, null, null, null, null);
	}

	/** 获取BigDecimal数组值，并限制每个元素的最小值和最大值，允许返回空值 */
	public BigDecimal[] getBigDecimals(String name, Number min, Number max) {
		return getBigDecimals(name, false, min, max, null, null);
	}

	/** 获取BigDecimal数组值，并限制每个元素的最小值、最大值、最大整数位长度和最大小数位长度，允许返回空值 */
	public BigDecimal[] getBigDecimals(String name, Number min, Number max, Integer maxIntegerLength, Integer maxDecimalLength) {
		return getBigDecimals(name, false, min, max, maxIntegerLength, maxDecimalLength);
	}

	/** 获取BigDecimal数组值，不允许返回空值 */
	public BigDecimal[] getBigDecimalsMust(String name) {
		return getBigDecimals(name, true, null, null, null, null);
	}

	/** 获取BigDecimal数组值，并限制每个元素的最小值和最大值，不允许返回空值 */
	public BigDecimal[] getBigDecimalsMust(String name, Number min, Number max) {
		return getBigDecimals(name, true, min, max, null, null);
	}

	/** 获取BigDecimal数组值，并限制每个元素的最小值、最大值、最大整数位长度和最大小数位长度，不允许返回空值 */
	public BigDecimal[] getBigDecimalsMust(String name, Number min, Number max, Integer maxIntegerLength, Integer maxDecimalLength) {
		return getBigDecimals(name, true, min, max, maxIntegerLength, maxDecimalLength);
	}

	/** 获取BigDecimal数组值 */
	private BigDecimal[] getBigDecimals(String name, boolean must, Number min, Number max, Integer maxIntegerLength, Integer maxDecimalLength) {
		String[] values = getArray(name, true, must);
		if (values == null) {
			return null;
		}
		try {
			BigDecimal[] result = new BigDecimal[values.length];
			for (int i = 0; i < result.length; i++) {
				result[i] = new BigDecimal(values[i]);
				if (min != null && result[i].doubleValue() < min.doubleValue() || max != null && result[i].doubleValue() > max.doubleValue() || maxIntegerLength != null && (result[i].precision() - result[i].scale()) > maxIntegerLength
						|| maxDecimalLength != null && result[i].scale() > maxDecimalLength) {
					throw new Exception();
				}
			}
			return result;
		} catch (Exception e) {
			throw new ParamException("请求参数【" + name + "】格式不正确");
		}
	}

	/** 获取String值，允许返回空值 */
	public String getString(String name) {
		return getString(name, false, false, null, null, null);
	}

	/** 获取String值，并匹配正则表达式，允许返回空值 */
	public String getString(String name, String regex) {
		return getString(name, false, false, regex, null, null);
	}

	/** 获取String值，并限制最大长度，允许返回空值 */
	public String getString(String name, Integer maxLength) {
		return getString(name, false, false, null, null, maxLength);
	}

	/** 获取String值，并限制最小长度和最大长度，允许返回空值 */
	public String getString(String name, Integer minLength, Integer maxLength) {
		return getString(name, false, false, null, minLength, maxLength);
	}

	/** 获取String值，并匹配正则表达式、限制最小值和最大值，允许返回空值 */
	public String getString(String name, String regex, Integer minLength, Integer maxLength) {
		return getString(name, false, false, regex, minLength, maxLength);
	}

	/** 获取String值，不允许返回空值 */
	public String getStringMust(String name) {
		return getString(name, false, true, null, null, null);
	}

	/** 获取String值，并匹配正则表达式，不允许返回空值 */
	public String getStringMust(String name, String regex) {
		return getString(name, false, true, regex, null, null);
	}

	/** 获取String值，并限制最大长度，不允许返回空值 */
	public String getStringMust(String name, Integer maxLength) {
		return getString(name, false, true, null, null, maxLength);
	}

	/** 获取String值，并限制最小长度和最大长度，不允许返回空值 */
	public String getStringMust(String name, Integer minLength, Integer maxLength) {
		return getString(name, false, true, null, minLength, maxLength);
	}

	/** 获取String值，并匹配正则表达式、限制最小值和最大值，不允许返回空值 */
	public String getStringMust(String name, String regex, Integer minLength, Integer maxLength) {
		return getString(name, false, true, regex, minLength, maxLength);
	}

	/** 获取去空格String值，允许返回空值 */
	public String getTrim(String name) {
		return getString(name, true, false, null, null, null);
	}

	/** 获取去空格String值，并匹配正则表达式，允许返回空值 */
	public String getTrim(String name, String regex) {
		return getString(name, true, false, regex, null, null);
	}

	/** 获取去空格String值，并限制最大长度，允许返回空值 */
	public String getTrim(String name, Integer maxLength) {
		return getString(name, true, false, null, null, maxLength);
	}

	/** 获取去空格String值，并限制最小长度和最大长度，允许返回空值 */
	public String getTrim(String name, Integer minLength, Integer maxLength) {
		return getString(name, true, false, null, minLength, maxLength);
	}

	/** 获取去空格String值，并匹配正则表达式、限制最小值和最大值，允许返回空值 */
	public String getTrim(String name, String regex, Integer minLength, Integer maxLength) {
		return getString(name, true, false, regex, minLength, maxLength);
	}

	/** 获取去空格String值，不允许返回空值 */
	public String getTrimMust(String name) {
		return getString(name, true, true, null, null, null);
	}

	/** 获取去空格String值，并匹配正则表达式，不允许返回空值 */
	public String getTrimMust(String name, String regex) {
		return getString(name, true, true, regex, null, null);
	}

	/** 获取去空格String值，并限制最大长度，不允许返回空值 */
	public String getTrimMust(String name, Integer maxLength) {
		return getString(name, true, true, null, null, maxLength);
	}

	/** 获取去空格String值，并限制最小长度和最大长度，不允许返回空值 */
	public String getTrimMust(String name, Integer minLength, Integer maxLength) {
		return getString(name, true, true, null, minLength, maxLength);
	}

	/** 获取去空格String值，并匹配正则表达式、限制最小值和最大值，不允许返回空值 */
	public String getTrimMust(String name, String regex, Integer minLength, Integer maxLength) {
		return getString(name, true, true, regex, minLength, maxLength);
	}

	/** 获取String值 */
	private String getString(String name, boolean trim, boolean must, String regex, Integer minLength, Integer maxLength) {
		String value = getValue(name, trim, must);
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		try {
			if (minLength != null && value.length() < minLength || maxLength != null && value.length() > maxLength || StringUtils.isNotEmpty(regex) && !value.matches(regex)) {
				throw new Exception();
			}
			return value;
		} catch (Exception e) {
			throw new ParamException("请求参数【" + name + "】格式不正确");
		}
	}

	/** 获取String数组值，允许返回空值 */
	public String[] getStrings(String name) {
		return getStrings(name, false, false, null, null, null);
	}

	/** 获取String数组值，并匹配正则表达式，允许返回空值 */
	public String[] getStrings(String name, String regex) {
		return getStrings(name, false, false, regex, null, null);
	}

	/** 获取String数组值，并限制最大长度，允许返回空值 */
	public String[] getStrings(String name, Integer maxLength) {
		return getStrings(name, false, false, null, null, maxLength);
	}

	/** 获取String数组值，并限制最小长度和最大长度，允许返回空值 */
	public String[] getStrings(String name, Integer minLength, Integer maxLength) {
		return getStrings(name, false, false, null, minLength, maxLength);
	}

	/** 获取String数组值，并匹配正则表达式、限制最小值和最大值，允许返回空值 */
	public String[] getStrings(String name, String regex, Integer minLength, Integer maxLength) {
		return getStrings(name, false, false, regex, minLength, maxLength);
	}

	/** 获取String数组值，不允许返回空值 */
	public String[] getStringsMust(String name) {
		return getStrings(name, false, true, null, null, null);
	}

	/** 获取String数组值，并匹配正则表达式，不允许返回空值 */
	public String[] getStringsMust(String name, String regex) {
		return getStrings(name, false, true, regex, null, null);
	}

	/** 获取String数组值，并限制最大长度，不允许返回空值 */
	public String[] getStringsMust(String name, Integer maxLength) {
		return getStrings(name, false, true, null, null, maxLength);
	}

	/** 获取String数组值，并限制最小长度和最大长度，不允许返回空值 */
	public String[] getStringsMust(String name, Integer minLength, Integer maxLength) {
		return getStrings(name, false, true, null, minLength, maxLength);
	}

	/** 获取String数组值，并匹配正则表达式、限制最小值和最大值，不允许返回空值 */
	public String[] getStringsMust(String name, String regex, Integer minLength, Integer maxLength) {
		return getStrings(name, false, true, regex, minLength, maxLength);
	}

	/** 获取去空格String数组值，允许返回空值 */
	public String[] getTrims(String name) {
		return getStrings(name, true, false, null, null, null);
	}

	/** 获取去空格String数组值，并匹配正则表达式，允许返回空值 */
	public String[] getTrims(String name, String regex) {
		return getStrings(name, true, false, regex, null, null);
	}

	/** 获取去空格String数组值，并限制最大长度，允许返回空值 */
	public String[] getTrims(String name, Integer maxLength) {
		return getStrings(name, true, false, null, null, maxLength);
	}

	/** 获取去空格String数组值，并限制最小长度和最大长度，允许返回空值 */
	public String[] getTrims(String name, Integer minLength, Integer maxLength) {
		return getStrings(name, true, false, null, minLength, maxLength);
	}

	/** 获取去空格String数组值，并匹配正则表达式、限制最小值和最大值，允许返回空值 */
	public String[] getTrims(String name, String regex, Integer minLength, Integer maxLength) {
		return getStrings(name, true, false, regex, minLength, maxLength);
	}

	/** 获取去空格String数组值，不允许返回空值 */
	public String[] getTrimsMust(String name) {
		return getStrings(name, true, true, null, null, null);
	}

	/** 获取去空格String数组值，并匹配正则表达式，不允许返回空值 */
	public String[] getTrimsMust(String name, String regex) {
		return getStrings(name, true, true, regex, null, null);
	}

	/** 获取去空格String数组值，并限制最大长度，不允许返回空值 */
	public String[] getTrimsMust(String name, Integer maxLength) {
		return getStrings(name, true, true, null, null, maxLength);
	}

	/** 获取去空格String数组值，并限制最小长度和最大长度，不允许返回空值 */
	public String[] getTrimsMust(String name, Integer minLength, Integer maxLength) {
		return getStrings(name, true, true, null, minLength, maxLength);
	}

	/** 获取去空格String数组值，并匹配正则表达式、限制最小值和最大值，不允许返回空值 */
	public String[] getTrimsMust(String name, String regex, Integer minLength, Integer maxLength) {
		return getStrings(name, true, true, regex, minLength, maxLength);
	}

	/** 获取String数组值 */
	private String[] getStrings(String name, boolean trim, boolean must, String regex, Integer minLength, Integer maxLength) {
		String[] values = getArray(name, trim, must);
		if (values == null) {
			return null;
		}
		try {
			for (int i = 0; i < values.length; i++) {
				if (minLength != null && values[i].length() < minLength || maxLength != null && values[i].length() > maxLength || StringUtils.isNotEmpty(regex) && !values[i].matches(regex)) {
					throw new Exception();
				}
			}
			return values;
		} catch (Exception e) {
			throw new ParamException("请求参数【" + name + "】格式不正确");
		}
	}

	/** 获取Date值，允许返回空值 */
	public Date getDate(String name, String pattern) {
		return getDate(name, pattern, false, false);
	}

	/** 获取Date值，并将Time值设为最大，允许返回空值 */
	public Date getDate(String name, String pattern, boolean toMaxTime) {
		return getDate(name, pattern, false, toMaxTime);
	}

	/** 获取Date值，不允许返回空值 */
	public Date getDateMust(String name, String pattern) {
		return getDate(name, pattern, true, false);
	}

	/** 获取Date值，并将Time值设为最大，不允许返回空值 */
	public Date getDateMust(String name, String pattern, boolean toMaxTime) {
		return getDate(name, pattern, true, toMaxTime);
	}

	/** 获取Date值 */
	private Date getDate(String name, String pattern, boolean must, boolean toMaxTime) {
		String value = getValue(name, true, must);
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		try {
			Date result = DateTimeUtils.parse(value, pattern);
			return toMaxTime ? DateTimeUtils.getDayMaxTime(result) : result;
		} catch (Exception e) {
			throw new ParamException("请求参数【" + name + "】格式不正确");
		}
	}

	/** 获取Date数组值，允许返回空值 */
	public Date[] getDates(String name, String pattern) {
		return getDates(name, pattern, false, false);
	}

	/** 获取Date数组值，并将Time值设为最大，允许返回空值 */
	public Date[] getDates(String name, String pattern, boolean toMaxTime) {
		return getDates(name, pattern, false, toMaxTime);
	}

	/** 获取Date数组值，不允许返回空值 */
	public Date[] getDatesMust(String name, String pattern) {
		return getDates(name, pattern, true, false);
	}

	/** 获取Date数组值，并将Time值设为最大，不允许返回空值 */
	public Date[] getDatesMust(String name, String pattern, boolean toMaxTime) {
		return getDates(name, pattern, true, toMaxTime);
	}

	/** 获取Date数组值 */
	private Date[] getDates(String name, String pattern, boolean must, boolean toMaxTime) {
		String[] values = getArray(name, true, must);
		if (values == null) {
			return null;
		}
		try {
			Date[] result = new Date[values.length];
			for (int i = 0; i < result.length; i++) {
				Date date = DateTimeUtils.parse(values[i], pattern);
				result[i] = toMaxTime ? DateTimeUtils.getDayMaxTime(date) : date;
			}
			return result;
		} catch (Exception e) {
			throw new ParamException("请求参数【" + name + "】格式不正确");
		}
	}

	/** 获取Enum值，允许返回空值 */
	public <T extends Enum<T>> T getEnum(String name, Class<T> clazz) {
		return getEnum(name, clazz, false);
	}

	/** 获取Enum值，不允许返回空值 */
	public <T extends Enum<T>> T getEnumMust(String name, Class<T> clazz) {
		return getEnum(name, clazz, true);
	}

	/** 获取Enum值 */
	private <T extends Enum<T>> T getEnum(String name, Class<T> clazz, boolean must) {
		String value = getValue(name, true, must);
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		try {
			return Enum.valueOf(clazz, value);
		} catch (Exception e) {
			throw new ParamException("请求参数【" + name + "】格式不正确");
		}
	}

	/** 获取Enum数组值，允许返回空值 */
	public <T extends Enum<T>> T[] getEnums(String name, Class<T> clazz) {
		return getEnums(name, clazz, false);
	}

	/** 获取Enum数组值，不允许返回空值 */
	public <T extends Enum<T>> T[] getEnumsMust(String name, Class<T> clazz) {
		return getEnums(name, clazz, true);
	}

	/** 获取Enum数组值 */
	@SuppressWarnings("unchecked")
	private <T extends Enum<T>> T[] getEnums(String name, Class<T> clazz, boolean must) {
		String[] values = getArray(name, true, must);
		if (values == null) {
			return null;
		}
		try {
			T[] result = (T[]) Array.newInstance(clazz, values.length);
			for (int i = 0; i < result.length; i++) {
				result[i] = Enum.valueOf(clazz, values[i]);
			}
			return result;
		} catch (Exception e) {
			throw new ParamException("请求参数【" + name + "】格式不正确");
		}
	}

	/** 获取分页PageNum值 */
	public int getPageNum() {
		Integer pageNum = getInteger("pageNum");
		if (pageNum == null || pageNum < 1) {
			pageNum = 1;
		}
		return pageNum;
	}

	/** 获取分页PageSize值，默认10条 */
	public int getPageSize() {
		return getPageSize(10);
	}

	/** 获取分页PageSize值 */
	public int getPageSize(int defaultValue) {
		Integer pageSize = getInteger("pageSize", null, 1000);
		if (pageSize == null || pageSize < 1) {
			pageSize = defaultValue;
		}
		return pageSize;
	}

	/** 获取本次请求的IP地址 */
	public String getIp() {
		String[] ipHeaders = new String[] { "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR" };
		for (String ipHeader : ipHeaders) {
			String ip = super.getHeader(ipHeader);
			if (StringUtils.isNotEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
				return ip;
			}
		}
		return super.getRemoteAddr();
	}

	/** 是否为AJAX请求 */
	public boolean isAjaxRequest() {
		return "XMLHttpRequest".equalsIgnoreCase(super.getHeader("X-Requested-With"));
	}

	/** 是否为WAP版请求 */
	public boolean isWapRequest() {
		String userAgent = this.getHeader("user-agent");
		Pattern reg1 = Pattern.compile("AppleWebKit.*Mobile", Pattern.CASE_INSENSITIVE);
		Pattern reg2 = Pattern.compile("MIDP|SymbianOS|NOKIA|SAMSUNG|LG|NEC|TCL|Alcatel|BIRD|DBTEL|Dopod|PHILIPS|HAIER|LENOVO|MOT-|Nokia|SonyEricsson|SIE-|Amoi|ZTE");
		return userAgent != null && (reg1.matcher(userAgent).find() || reg2.matcher(userAgent).find());
	}

	/** 获取请求的URI并带上参数 */
	public String getRequestURIWithQueryString() {
		String uri = getRequestURI();
		String queryString = getQueryString();
		if (StringUtils.isNotEmpty(queryString)) {
			uri += "?" + queryString;
		}
		return uri;
	}

	/** 获取当前登录认证的Token */
	public AbstractAuthenticationToken getToken() {
		return (AbstractAuthenticationToken) super.getUserPrincipal();
	}

	/** 获取当前登录的用户 */
	public User getLoginUser() {
		AbstractAuthenticationToken token = getToken();
		return token == null ? null : (User) token.getPrincipal();
	}

	/** 获取API当前登录的用户 */
	public User getApiLoginUser() {
		return (User) getAttribute("API_LOGIN_USER");
	}

	@Override
	public String toString() {
		ObjectNode info = JsonUtils.createObjectNode();
		info.put("url", super.getRequestURL().toString());
		info.put("ip", getIp());
		ObjectNode header = info.putObject("header");
		Enumeration<String> headerNames = super.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String name = headerNames.nextElement();
			header.put(name, super.getHeader(name));
		}
		info.set("parameter", JsonUtils.toObjectNode(super.getParameterMap()));
		return info.toString();
	}
}