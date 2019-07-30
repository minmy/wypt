package cn.sini.cgb.common.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.util.StringUtils;

import cn.sini.cgb.common.exception.SystemException;

/**
 * 日期时间工具类
 * 
 * @author 杨海彬
 */
public class DateTimeUtils {

	/** 获取指定日期时间的字段值 */
	private static int getFieldValue(Date date, int field) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(field);
	}

	/** 获取最小时间 */
	private static Date getMinTime(Calendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	/** 获取最大时间 */
	private static Date getMaxTime(Calendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTime();
	}

	/** 获取指定日期时间的年份 */
	public static Integer getYear(Date date) {
		if (date == null) {
			return null;
		}
		return getFieldValue(date, Calendar.YEAR);
	}

	/** 获取指定日期时间的月份 */
	public static Integer getMonth(Date date) {
		if (date == null) {
			return null;
		}
		return getFieldValue(date, Calendar.MONTH) + 1;
	}

	/** 获取指定日期时间的天数 */
	public static Integer getDay(Date date) {
		if (date == null) {
			return null;
		}
		return getFieldValue(date, Calendar.DAY_OF_MONTH);
	}

	/** 获取指定日期时间的小时 */
	public static Integer getHour(Date date) {
		if (date == null) {
			return null;
		}
		return getFieldValue(date, Calendar.HOUR_OF_DAY);
	}

	/** 获取指定日期时间的分钟 */
	public static Integer getMinute(Date date) {
		if (date == null) {
			return null;
		}
		return getFieldValue(date, Calendar.MINUTE);
	}

	/** 获取指定日期时间的秒钟 */
	public static Integer getSecond(Date date) {
		if (date == null) {
			return null;
		}
		return getFieldValue(date, Calendar.SECOND);
	}

	/** 获取指定日期时间的毫秒 */
	public static Integer getMilliSecond(Date date) {
		if (date == null) {
			return null;
		}
		return getFieldValue(date, Calendar.MILLISECOND);
	}

	/** 获取指定日期的最小时间 */
	public static Date getDayMinTime(Date date) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return getMinTime(calendar);
	}

	/** 获取指定日期的最大时间 */
	public static Date getDayMaxTime(Date date) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return getMaxTime(calendar);
	}

	/** 获取指定日期下一天的最小时间 */
	public static Date getNextDayMinTime(Date date) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		return getMinTime(calendar);
	}

	/** 获取指定日期下一天的最大时间 */
	public static Date getNextDayMaxTime(Date date) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		return getMaxTime(calendar);
	}

	/** 获取指定日期上一天的最小时间 */
	public static Date getPreviousDayMinTime(Date date) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		return getMinTime(calendar);
	}

	/** 获取指定日期上一天的最大时间 */
	public static Date getPreviousDayMaxTime(Date date) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		return getMaxTime(calendar);
	}

	/** 获取指定月份第一天的最小时间 */
	public static Date getMonthMinTime(Integer year, Integer month) {
		if (year == null || month == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		return getMinTime(calendar);
	}

	/** 获取指定月份最后一天的最大时间 */
	public static Date getMonthMaxTime(Integer year, Integer month) {
		if (year == null || month == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return getMaxTime(calendar);
	}

	/** 获取指定月份第一天的最小时间 */
	public static Date getMonthMinTime(Date date) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		return getMinTime(calendar);
	}

	/** 获取指定月份最后一天的最大时间 */
	public static Date getMonthMaxTime(Date date) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return getMaxTime(calendar);
	}

	/** 获取指定月份下个月第一天的最小时间 */
	public static Date getNextMonthMinTime(Date date) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, 1);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		return getMinTime(calendar);
	}

	/** 获取指定月份下个月最后一天的最大时间 */
	public static Date getNextMonthMaxTime(Date date) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, 1);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return getMaxTime(calendar);
	}

	/** 获取指定月份上个月第一天的最小时间 */
	public static Date getPreviousMonthMinTime(Date date) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, -1);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		return getMinTime(calendar);
	}

	/** 获取指定月份上个月最后一天的最大时间 */
	public static Date getPreviousMonthMaxTime(Date date) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, -1);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return getMaxTime(calendar);
	}

	/** 获取指定年份第一天的最小时间 */
	public static Date getYearMinTime(Date date) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.MONTH, calendar.getActualMinimum(Calendar.MONTH));
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		return getMinTime(calendar);
	}

	/** 获取指定年份最后一天的最大时间 */
	public static Date getYearMaxTime(Date date) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.MONTH, calendar.getActualMaximum(Calendar.MONTH));
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return getMaxTime(calendar);
	}

	/** 获取指定年份下一年第一天的最小时间 */
	public static Date getNextYearMinTime(Date date) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.YEAR, 1);
		calendar.set(Calendar.MONTH, calendar.getActualMinimum(Calendar.MONTH));
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		return getMinTime(calendar);
	}

	/** 获取指定年份下一年最后一天的最大时间 */
	public static Date getNextYearMaxTime(Date date) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.YEAR, 1);
		calendar.set(Calendar.MONTH, calendar.getActualMaximum(Calendar.MONTH));
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return getMaxTime(calendar);
	}

	/** 获取指定年份上一年第一天的最小时间 */
	public static Date getPreviousYearMinTime(Date date) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.YEAR, -1);
		calendar.set(Calendar.MONTH, calendar.getActualMinimum(Calendar.MONTH));
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		return getMinTime(calendar);
	}

	/** 获取指定年份上一年最后一天的最大时间 */
	public static Date getPreviousYearMaxTime(Date date) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.YEAR, -1);
		calendar.set(Calendar.MONTH, calendar.getActualMaximum(Calendar.MONTH));
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return getMaxTime(calendar);
	}

	/** 增加或减少x年 */
	public static Date addYear(Date date, Integer amount) {
		if (date == null) {
			return null;
		}
		if (amount == null) {
			return date;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.YEAR, amount);
		return calendar.getTime();
	}

	/** 增加或减少x月 */
	public static Date addMonth(Date date, Integer amount) {
		if (date == null) {
			return null;
		}
		if (amount == null) {
			return date;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, amount);
		return calendar.getTime();
	}

	/** 增加或减少x天 */
	public static Date addDay(Date date, Integer amount) {
		if (date == null) {
			return null;
		}
		if (amount == null) {
			return date;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, amount);
		return calendar.getTime();
	}

	/** 增加或减少x时 */
	public static Date addHour(Date date, Integer amount) {
		if (date == null) {
			return null;
		}
		if (amount == null) {
			return date;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.HOUR_OF_DAY, amount);
		return calendar.getTime();
	}

	/** 增加或减少x分 */
	public static Date addMinute(Date date, Integer amount) {
		if (date == null) {
			return null;
		}
		if (amount == null) {
			return date;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MINUTE, amount);
		return calendar.getTime();
	}

	/** 增加或减少x秒 */
	public static Date addSecond(Date date, Integer amount) {
		if (date == null) {
			return null;
		}
		if (amount == null) {
			return date;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.SECOND, amount);
		return calendar.getTime();
	}

	/** 根据表达式把时间字符串转换成时间对象，日期表达式为yyyy-MM-dd HH:mm:ss.S */
	public static Date parse(String value, String pattern) {
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		try {
			return sdf.parse(value);
		} catch (Exception e) {
			throw new SystemException("将字符串转成日期对象出现异常", e);
		}
	}

	/** 根据表达式把时间对象转换成时间字符串，日期表达式为yyyy-MM-dd HH:mm:ss.S */
	public static String format(Date date, String pattern) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}

	/** 判断两个时间是否为同一天 */
	public static boolean isSameDay(Date date1, Date date2) {
		if (date1 == null || date2 == null) {
			return false;
		}
		return DateUtils.isSameDay(date1, date2);
	}

	/** 将date1的时间部分复制到date2 */
	public static Date copyTime(Date date1, Date date2) {
		if (date1 == null || date2 == null) {
			return null;
		}
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(date1);
		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime(date2);
		calendar2.set(Calendar.HOUR_OF_DAY, calendar1.get(Calendar.HOUR_OF_DAY));
		calendar2.set(Calendar.MINUTE, calendar1.get(Calendar.MINUTE));
		calendar2.set(Calendar.SECOND, calendar1.get(Calendar.SECOND));
		calendar2.set(Calendar.MILLISECOND, calendar1.get(Calendar.MILLISECOND));
		return calendar2.getTime();
	}
}