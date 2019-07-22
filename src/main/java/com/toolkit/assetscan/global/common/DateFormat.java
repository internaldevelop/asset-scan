package com.toolkit.assetscan.global.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateFormat {
    /** 只有年日期格式，yyyy */
    public static String YEAR_FORMAT = "yyyy";
    /** 数据库日期格式，yyyy-MM-dd */
    public static String SQL_FORMAT = "yyyy-MM-dd";
    /** util工具日期格式，yyyy-MM-dd HH:mm:ss */
    public static String UTIL_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /** util工具日期格式，yyyy-MM-dd HH:mm:ss.sss */
    public static String UTIL_DETAIL_FORMAT = "yyyy-MM-dd HH:mm:ss.sss";
    /** 病人检查时间格式 */
    public static String PAT_STUDY_TIME_FORMAT = "yyyy-MM-dd HH:mm";
    /** 病人本地编号时间格式 **/
    public static String PAT_LOCAL_PAT_ID_TIME_FORMAT = "yyyyMMddHHmmss";

    /**
     * 将Date按照指定格式转换成String类型
     *
     * @param date
     * @param pattern "yyyy-MM-dd HH:mm:ss"
     * @return String
     */
    public static String dateToString(Date date, String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }

    /**
     * String转换成Date
     *
     * @param str "2019-07-20 06:12:34"
     * @param pattern "yyyy-MM-dd HH:mm:ss" pattern范围要比str小
     * @return Date
     */
    public static Date stringToDate(String str, String pattern) {
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            date = sdf.parse(str.trim());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 将日期增加指定月数
     *
     * @param date
     * @param month
     * @return
     * @since access 2.0
     */
    public static Date addMonth(Date date, Integer month) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        month = null == month ? 0 : month;
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + month.intValue());
        return cal.getTime();
    }

    /**
     * 获得日期的相差天数
     * @param startTime
     * @param endTime
     * @return
     */
    public static int timeInterval(String startTime, String endTime) {
        int days = 0;
        try {
            // 放map里面 键拿日期来当 值就先初始成0
            // 时间转换类
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date endDate = sdf.parse(endTime);
            Date startDate = sdf.parse(startTime);
            // 将转换的两个时间对象转换成Calendard对象
            Calendar end = Calendar.getInstance();
            end.setTime(endDate);
            Calendar start = Calendar.getInstance();
            start.setTime(startDate);
            // 拿出两个年份
            int yearEnd = end.get(Calendar.YEAR);
            int yearStart = start.get(Calendar.YEAR);
            // 天数

            Calendar can = null;
            // 如果can1 < can2
            // 减去小的时间在这一年已经过了的天数
            // 加上大的时间已过的天数
            if (end.before(start)) {
                days -= end.get(Calendar.DAY_OF_YEAR);
                days += start.get(Calendar.DAY_OF_YEAR);
                can = end;
            } else {
                days -= start.get(Calendar.DAY_OF_YEAR);
                days += end.get(Calendar.DAY_OF_YEAR);
                can = start;
            }
            for (int i = 0; i < Math.abs(yearStart - yearEnd); i++) {
                // 获取小的时间当前年的总天数
                days += can.getActualMaximum(Calendar.DAY_OF_YEAR);
                // 再计算下一年。
                can.add(Calendar.YEAR, 1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return days;

    }

    /**
     * 获得指定日期的后一天
     * @param specifiedDay
     * @return
     */
    public static String getSpecifiedDayAfter(String specifiedDay) {
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat("yy-MM-dd").parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date != null) {
            c.setTime(date);
        }

        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day + 1);

        String dayAfter = new SimpleDateFormat("yyyy-MM-dd")
                .format(c.getTime());
        return dayAfter;
    }


    /**
     * 获取当天时间
     * @return
     */
    public static Date getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.SQL_FORMAT);
        Date time = null;
        try {
            time = sdf.parse(sdf.format(new Date()));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public static String getCurrentDateStr() {
        SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.SQL_FORMAT);
        return sdf.format(new Date());
    }

    //由出生日期获得年龄
    public static int getAge(Date birthDay) throws Exception {
        Calendar cal = Calendar.getInstance();

        if (cal.before(birthDay)) {
            throw new IllegalArgumentException( "The birthDay is before Now.It's unbelievable!");
        }
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(birthDay);

        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

        int age = yearNow - yearBirth;

        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) age--;
            }else{
                age--;
            }
        }
        return age;
    }

    // 从当前日期往前推几天
    public static String getDayBefore(Integer dy){
        SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.SQL_FORMAT);
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = sdf.parse(sdf.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day - dy);

        String dayBefore = sdf.format(c.getTime());
        return dayBefore;
    }

    public static void main(String[] args) {
        // Date date = new Date();
        // System.out.println(DateFormat.dateToString(date,
        // DateFormat.YEAR_FORMAT));
        // System.out.println(DateFormat.dateToString(date,
        // DateFormat.SQL_FORMAT));
        // System.out.println(DateFormat.dateToString(date,
        // DateFormat.UTIL_DETAIL_FORMAT));
        // System.out.println(DateFormat.dateToString(date,
        // DateFormat.UTIL_FORMAT));
        // System.out.println(DateFormat.dateToString(date, "yyyy-MM-dd HH"));
        //
        // System.out.println(DateFormat.stringToDate("2013-01-13",
        // DateFormat.SQL_FORMAT));

        String d = "2009-02-07 09:46:12";
        System.out.println(DateFormat.stringToDate(d, UTIL_FORMAT));

    }
}
