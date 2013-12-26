package com.duduto.util;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * <p>TimeUtil description</p>
 *
 * @since Mar 30, 2012 4:30:24 PM
 * @author phongtn
 * @version 1.1
 */
public class TimeUtil {
    
    public final static long ONE_SECOND = 1000;
    public final static long SECONDS = 60;
    public final static long ONE_MINUTE = ONE_SECOND * 60;
    public final static long MINUTES = 60;
    public final static long ONE_HOUR = ONE_MINUTE * 60;
    public final static long HOURS = 24;
    public final static long ONE_DAY = ONE_HOUR * 24;
    
    private TimeUtil() {
    }

    /**
     * converts time (in milliseconds) to human-readable format "<w> days, <x>
     * hours, <y> minutes and (z) seconds"
     */
    private static String millisToLongDHMS(long duration, String date) {
        StringBuilder res = new StringBuilder();
        long temp = 0;
        long cal = 0;
        if (duration >= ONE_SECOND) {
            temp = duration / ONE_SECOND;
            if (temp > 0 && temp < 60) {
                res.append(temp).append(" giây ");
            } else {
                temp = duration / ONE_MINUTE;
                if (temp > 0 && temp < 60) {
                    res.append(temp).append(" phút ");
                } else {
                    temp = duration / ONE_HOUR;
                    cal = duration / ONE_MINUTE;
                    if (temp > 0 && temp < 24) {
                        if (cal % 60 != 0) {
                            res.append("Khoảng ");
                        }
                        res.append(temp).append(" giờ ");
                    } else {
                        temp = duration / ONE_DAY;
                        cal = duration / ONE_HOUR;
                        if (temp > 0 && temp < 2) {
                            if (cal % 24 != 0) {
                                res.append("Khoảng ");
                            }
                            res.append(temp).append(" ngày ");
                        } else {
                            return res.append(getTimeDetails(date)).toString();
                        }
                    }
                }
            }
            
            return res.append("trước").toString();
        } else {
            return "Vừa tải lên";
        }
    }

    /**
     * Given a time in ms, this HowTo will output the long form "<w> days, <x>
     * hours, <y> minutes and (z) seconds" and the short form "<dd:>hh:mm:ss".
     *
     * @param duration
     * @return
     */
    public static String millisToLongDHMS(long duration) {
        StringBuilder res = new StringBuilder();
        long temp = 0;
        if (duration >= ONE_SECOND) {
            temp = duration / ONE_DAY;
            if (temp > 0) {
                duration -= temp * ONE_DAY;
                res.append(temp).append(" day").append(temp > 1 ? "s" : "").append(duration >= ONE_MINUTE ? ", " : "");
            }
            
            temp = duration / ONE_HOUR;
            if (temp > 0) {
                duration -= temp * ONE_HOUR;
                res.append(temp).append(" hour").append(temp > 1 ? "s" : "").append(duration >= ONE_MINUTE ? ", " : "");
            }
            
            temp = duration / ONE_MINUTE;
            if (temp > 0) {
                duration -= temp * ONE_MINUTE;
                res.append(temp).append(" minute").append(temp > 1 ? "s" : "");
            }
            
            if (!res.toString().equals("") && duration >= ONE_SECOND) {
                res.append(" and ");
            }
            
            temp = duration / ONE_SECOND;
            if (temp > 0) {
                res.append(temp).append(" second").append(temp > 1 ? "s" : "");
            }
            return res.toString();
        } else {
            return "0 second";
        }
    }

    /**
     * converts time (in milliseconds) to human-readable format "<dd:>hh:mm:ss"
     */
    public static String millisToShortDHMS(long duration) {
        String res = "";
        duration /= ONE_SECOND;
        int seconds = (int) (duration % SECONDS);
        duration /= SECONDS;
        int minutes = (int) (duration % MINUTES);
        duration /= MINUTES;
        int hours = (int) (duration % HOURS);
        int days = (int) (duration / HOURS);
        if (days == 0) {
            res = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            res = String.format("%dd%02d:%02d:%02d", days, hours, minutes, seconds);
        }
        return res;
    }
    
    public static String getTimeBefore(String date) {
        long crrTime = new Date().getTime();
        Date d = DateUtil.stringToDate(date, "dd-MM-yyyy hh:mm:ss");
        return TimeUtil.millisToLongDHMS(crrTime - d.getTime(), date);
    }
    
    public static String getTimeDetails(String date) {
        StringBuilder res = new StringBuilder();
        Date d = DateUtil.stringToDate(date, "dd-MM-yyyy hh:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        int month = calendar.get(Calendar.MONTH) + 1;
        res.append("ngày ").append(calendar.get(Calendar.DATE));
        res.append(" tháng ").append(month);
        res.append(" năm ").append(calendar.get(Calendar.YEAR));
        res.append(" vào lúc ").append(calendar.get(Calendar.HOUR)).append(":").append(calendar.get(Calendar.MINUTE));
        if (calendar.get(Calendar.AM_PM) == 0) {
            res.append(" sáng");
        } else {
            res.append(" chiều");
        }
        return res.toString();
    }

    /**
     * make a random integer with number 999999999
     *
     * @return
     */
    public static String generateRandomInteger() {
        SecureRandom random = new SecureRandom();
        int aStart = 1;
        long aEnd = 999999999;
        long range = aEnd - (long) aStart + 1;
        long fraction = (long) (range * random.nextDouble());
        long randomNumber = fraction + (long) aStart;
        return String.valueOf(randomNumber);
    }
//    public static void main(String args[]) {
//        String date = "21-11-2012 23:42:05";
//        Date d = DateUtil.stringToDate("21-11-2012 23:42:05", "dd-MM-yyyy hh:mm:ss");
//        System.out.println(TimeUtil.millisToShortDHMS(1444657L * 4));
//    }
}
