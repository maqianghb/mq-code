package com.example.mq.common.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.StringUtils;

/**
 * @program: mq-code
 * @description: 时间操作类
 * @author: maqiang
 * @create: 2018/10/29
 *
 */

public class DateUtil {
	private static final Logger LOG = LoggerFactory.getLogger(DateUtil.class);

	public static final long ONE_DAY_MILLIS = 24 * 60 * 60 * 1000L;
	public static final long ONE_WEEK_MILLIS = 7 * ONE_DAY_MILLIS;

	/**
	 * 获取date当天的凌晨时间，yyyy-MM-dd 00:00:00
	 * @param date
	 * @return
	 */
	public static Date getDateInMidnigth(Date date){
		if(Objects.isNull(date)){
			return null;
		}
		String day = DateFormatUtils.format(date, "yyyy-MM-dd");
		String midnight = day + " 00:00:00";
		try {
			return dateTimeThreadLocal.get().parse(midnight);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 获取当天凌晨的时间
	 * ps：不建议使用，凌晨时刻调用可能会出现前后天的问题
	 * @return
	 */
	public static Date getCurrentDateInMidnight() {
		return getDateInMidnigth(new Date());
	}


	/**
	 * yyyyMMdd
	 */
	public static final String LDATE_FORMAT = "yyyyMMdd";

	private static final ThreadLocal<SimpleDateFormat> lDateThreadLocal = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat( LDATE_FORMAT);
		}
	};

	/**
	 * yyyyMMdd
	 * @param date
	 * @return
	 */
	public static Date parseLDate(String date) {
		return paser(lDateThreadLocal.get(), date);
	}

	/**
	 * @param date
	 * @return yyyyMMdd
	 */
	public static String formatLDate(Date date) {
		return format(lDateThreadLocal.get(), date);
	}

	/**
	 * yyyy-MM-dd HH:mm:ss
	 */
	public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	private static final ThreadLocal<SimpleDateFormat> dateTimeThreadLocal = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat( DATETIME_FORMAT);
		}
	};

	/**
	 * yyyy-MM-dd HH:mm:ss
	 * @param date
	 * @return
	 */
	public static Date parseDateTime(String date) {
		return paser(dateTimeThreadLocal.get(), date);
	}

	/**
	 * @param date
	 * @return yyyy-MM-dd HH:mm:ss
	 */
	public static String formatDateTime(Date date) {
		return format(dateTimeThreadLocal.get(), date);
	}


	/**
	 * yyyyMMddHHmmss
	 */
	public static final String LDATETIME_FORMAT = "yyyyMMddHHmmss";

	private static final ThreadLocal<SimpleDateFormat> lDateTimeThreadLocal = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat( LDATETIME_FORMAT);
		}
	};

	/**
	 * yyyyMMddHHmmss
	 * @param date
	 * @return
	 */
	public static Date parseLDateTime(String date) {
		return paser(lDateTimeThreadLocal.get(), date);
	}

	/**
	 * @param date
	 * @return yyyyMMddHHmmss
	 */
	public static String formatLDateTime(Date date) {
		return format(lDateTimeThreadLocal.get(), date);
	}

	private static Date paser(DateFormat dateFormat, String dateString) {
		if(Objects.isNull(dateFormat) || StringUtils.isEmpty(dateString)){
			return null;
		}
		Date result =null;
		try {
			result = dateFormat.parse(dateString);
		} catch (ParseException e) {
			throw new IllegalArgumentException("日期格式错误:" + dateString, e);
		}
		return result;
	}

	private static String format(DateFormat dateFormat, Date date) {
		if (Objects.isNull(date) || Objects.isNull(dateFormat)){
			return "";
		}
		String result = null;
		try {
			result = dateFormat.format(date);
		} catch (Exception e) {
			throw new IllegalArgumentException("日期转换失败", e);
		}
		return result;
	}
}
