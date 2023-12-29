package com.example.mq.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Local;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Slf4j
public class DateUtil {

    public static final String TIME_FORMAT ="yyyy-MM-dd HH:mm:ss";

    /**
     * 格式化时间
     *
     * @param date
     * @return
     */
    public static String formatDateTime(Date date, String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static final Date parseDateTime(String strDateTime, String format){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.parse(strDateTime);
        } catch (ParseException e) {
            log.error(" parseDateTime err, strDateTime:{}, format:{}", strDateTime, format, e);
        }

        return null;
    }

    public static final LocalDateTime parseLocalDateTime(String strDateTime, String format){
        DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.parse(strDateTime, df);
    }

}
