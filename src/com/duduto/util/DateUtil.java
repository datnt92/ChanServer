/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.duduto.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tungtt
 * @since Jan 17, 2012 3:48:36 PM
 * @version 1.0
 *
 */
public class DateUtil {

    static final Logger logger = Logger.getLogger(DateUtil.class.getName());
    static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * <p>default pattern is yyyy-MM-dd hh:mm:ss aaa</p>
     *
     * @return
     */
    public static String getCurrentDateTime() {
        return dateToString(new Date(), DEFAULT_DATE_PATTERN);
    }

    /**
     * convert date to string
     *
     * @param date
     * @param format
     * @return String
     */
    public static String dateToString(Date date, String format) {
        try {
            if (format == null) {
                format = DEFAULT_DATE_PATTERN;
            }
            SimpleDateFormat mySimpleDateFormat = new SimpleDateFormat(format);
            return mySimpleDateFormat.format(date);
        } catch (Exception e) {

            logger.log(Level.SEVERE, "dateToString", e);
        }
        return null;
    }

    /**
     * convert date to date
     *
     * @param date
     * @param format
     * @return String
     */
    public static Date dateToDate(Date date, String format) {
        try {
            SimpleDateFormat mySimpleDateFormat = new SimpleDateFormat(format);
            String s = mySimpleDateFormat.format(date);
            return stringToDate(s, format);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "dateToDate", e);
        }
        return null;
    }

    /**
     * convert string to date
     *
     * @param dateStr
     * @param format
     * @return Date
     */
    public static Date stringToDate(String dateStr, String format) {
        try {
            SimpleDateFormat mySimpleDateFormat = new SimpleDateFormat(format);
            return mySimpleDateFormat.parse(dateStr);
        } catch (ParseException e) {
            logger.log(Level.SEVERE, "dateToString", e);
            return null;
        }
    }

    /**
     * <p>Phep tru ngay cho mot so nhat dinh</p>
     *
     * @param numberDay so ngay bi tru
     * @return ngay thang sau khi tru co dinh dang dd/MM/yyyy
     */
    public static Date dateSubtraction(Date date, int numberDay) {
        try {
            if (date == null) {
                return null;
            }
            //86400000 is number mili second in an day
            long iDay = numberDay * 86400000;
            long lNewDate = date.getTime() - iDay;
            Date dAfterSub = new Date(lNewDate);
            return dAfterSub;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "dateSubtration", e);
            return null;
        }

    }

    public static String nextOneHour() {
        try {
            long lNewDate = new Date().getTime() + 1000L * 60L * 60L;
            Date d = new Date(lNewDate);
            return DateUtil.dateToString(d, null);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "nextOneHour", e);
            return null;
        }
    }
    
       
 

    public static String nextMinutes(int minute) {
        try {
            long lNewDate = new Date().getTime() + 1000L * 60L * minute;
            Date d = new Date(lNewDate);
            return DateUtil.dateToString(d, null);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "nextOneHour", e);
            return null;
        }
    }
}
