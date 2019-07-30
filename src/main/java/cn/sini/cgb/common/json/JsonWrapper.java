package cn.sini.cgb.common.json;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;

import cn.sini.cgb.common.exception.ParamException;
import cn.sini.cgb.common.util.DateTimeUtils;

/**
 * Json包装类
 * 
 * @author 杨海彬
 */
public class JsonWrapper {

	/** JsonNode对象 */
	private JsonNode jsonNode;

	/** 构造方法 */
	public JsonWrapper(JsonNode jsonNode) {
		this.jsonNode = jsonNode;
	}

	/** 获取参数值 */
	private String getValue(String name, boolean trim, boolean must) {
		JsonNode node = this.jsonNode.get(name);
		String value = null;
		if (node != null && !(node instanceof NullNode)) {
			value = node.asText();
		}
		value = trim ? StringUtils.trim(value) : value;
		if (must && StringUtils.isEmpty(value)) {
			throw new ParamException("请求参数【" + name + "】不能为空");
		}
		return value;
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
}