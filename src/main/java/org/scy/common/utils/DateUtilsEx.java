package org.scy.common.utils;

import org.apache.commons.lang3.time.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 日期工具类，是{@link org.apache.commons.lang3.time.DateUtils}的扩展
 * Created by hykj on 2017/8/11.
 */
public abstract class DateUtilsEx {

    /** yyyy-MM-dd HH:mm:ss.sss */
    public final static DateFormat dt_full = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    /** yyyy-MM-dd HH:mm:ss */
    public final static DateFormat dt_date_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /** yyyy-MM-dd */
    public final static DateFormat dt_date = new SimpleDateFormat("yyyy-MM-dd");
    /** HH:mm:ss */
    public final static DateFormat dt_time = new SimpleDateFormat("HH:mm:ss");
    /** HH:mm:ss.sss */
    public final static DateFormat dt_time_msec = new SimpleDateFormat("HH:mm:ss.SSS");
    /** yyyy年MM月dd */
    public final static DateFormat dt_ymd_cn = new SimpleDateFormat("yyyy年MM月dd日");
    /** yyyyMM */
    public final static DateFormat dt_ym = new SimpleDateFormat("yyyyMM");
    /** yyyy年MM月 */
    public final static DateFormat dt_ym_cn = new SimpleDateFormat("yyyy年MM月");
    /** 格林时间，如 Sun Oct 17 01:34:44 CST 2010 */
    public final static DateFormat dt_gmt = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);

    /**
     * 比较两个时间，当date1在date2之前返回-1，当date1等于date2时返回0，当date1在date2之后返回1。
     * date为null视为最小时间（小于任何时间）
     * add by shicy 2011-4-16
     * @param date1
     * @param date2
     * @return
     * @see java.util.Date#compareTo(Date)
     */
    public static int compareDate(Date date1, Date date2) {
        if (date1 == null && date2 == null)
            return 0;
        if (date1 == null)
            return -1;
        if (date2 == null)
            return 1;
        return date1.compareTo(date2);
    }

    /**
     * 试解析时间字符串，给定一个默认时间格式
     * @param value
     * @param format
     * @return
     */
    public static Date tryParseDate(String value, String format) {
        DateFormat df = new SimpleDateFormat(format);
        return tryParseDate(value, df);
    }

    /**
     * 试解析时间字符串，给定一个默认格式化对象
     * @param value
     * @param df
     * @return
     */
    public static Date tryParseDate(String value, DateFormat df) {
        try {
            return df.parse(value);
        }
        catch (Exception e) {
            return tryParseDate(value);
        }
    }

    /**
     * 试解析时间字符串
     * @param value 未知字符串格式
     * @return
     */
    public static Date tryParseDate(String value) {
        try {
            // Sun Oct 17 01:34:44 CST 2010
            return DateUtils.parseDate(value, new String[]{
                    "yyyy-MM-dd", "yyyy.MM.dd", "yyyyMMdd", "yyyy/MM/dd",
                    "yyyy-MM-dd HH:mm:ss.SSS", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm",
                    "yyyy年MM月dd日", "yyyy年MM月", "HH:mm:ss.SSS", "HH:mm:ss", "yyyy-M-d",
                    "yyyy-M-d H:m:s"
            });
        }
        catch (ParseException e) {
            try {
                return dt_gmt.parse(value);
            }
            catch (ParseException pe) {
                //
            }
        }
        return null;
    }

    /**
     * 获取两个时间的差异值，差异可以是年差异、月差异、日差异等等，由参数fragment控制。
     * @see #getFragmentBetween(Date, Date, int, boolean)
     */
    public static long getFragmentBetween(Date one, Date two, int fragment) {
        return getFragmentBetween(one, two, fragment, false);
    }

    /**
     * 获取两个时间的差异值，差异可以是年差异、月差异、日差异等等，由参数fragment控制。
     * 当one小于two时返回一个大于0的值，否则返回一个小于0的值
     * @param one 一个时间对象
     * @param two 另一个时间对象
     * @param fragment 比较的差异段，如月{@link Calendar#MONTH}，请参数考Calendar类
     * @param onlyFragment 用于忽略某些段，如要获取月份差异值，该参数为true时将忽略年份差异
     * @return
     */
    public static long getFragmentBetween(Date one, Date two, int fragment, boolean onlyFragment) {
        Calendar cal1 = dateToCalendar(one);
        Calendar cal2 = dateToCalendar(two);
        if (cal1.compareTo(cal2) == 0)
            return 0;

        if (onlyFragment)
            return cal2.get(fragment) - cal1.get(fragment);

        int ret = 0, flag = 1; // flag为1表示正数
        if (cal1.compareTo(cal2) > 0) { // 比较大小，互调
            cal1 = dateToCalendar(two);
            cal2 = dateToCalendar(one);
            flag = -1;
        }

        // 采用增量方法计算差异值，分为几个阶段分别计算（总不能差异是年的却要以毫秒递增）
        // 由于年计算天或月计算天都不是固定值，所以这里最小以天递增
        int[] asc = new int[]{Calendar.DAY_OF_WEEK_IN_MONTH, Calendar.HOUR_OF_DAY, Calendar.MINUTE,
                Calendar.SECOND, Calendar.MILLISECOND};
        int[] mul = new int[]{1, 24, 60, 60, 1000}; // 分别对应上面的转换
        for (int i = 0; i < asc.length; i++) {
            int tmpFragment = fragment <= asc[i] ? fragment : asc[i];
            ret *= mul[i]; // 上一个时间段到下一个时间段的差异转换
            while (true) {
                cal1.add(tmpFragment, 1); // fragment增量为1
                if (cal1.compareTo(cal2) > 0) {
                    cal1.add(tmpFragment, -1); // 还原1个fragment
                    break;
                }
                ret++; // 递增差异值
            }
            if (fragment <= asc[i])
                break;
        }

        if (cal1.compareTo(cal2) < 0) // 计算结果还是小一点，那就差异值加1
            ret++;
        return ret * flag;
    }

    /**
     * 获得格式化日期字符串
     * @param date the Date to be format
     * @param pattern 转化格式字符串，符合SimpleDateFormat格式定义
     * @return
     */
    public static String format(Date date, String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }

    /**
     * 获得格式化日期字符串
     * @param date the Date to be format
     * @param df 格式对象
     * @return
     */
    public static String format(Date date, DateFormat df) {
        return df.format(date);
    }

    /**
     * 将一个Date类型转化为Calendar类型（日历）
     * @param date 为空时取当前时间
     * @return
     */
    public static Calendar dateToCalendar(Date date) {
        Calendar calerdar = Calendar.getInstance();
        if (date == null)
            date = new Date();
        calerdar.setTime(date);
        return calerdar;
    }

    /**
     * 获取Date中的日期值，即滤去时间值
     * @param date 原Date对象，如果为null则取当前时间
     * @return
     */
    public static Date getDateOnly(Date date) {
        if (date == null)
            date = new Date();
        try {
            return dt_date.parse(dt_date.format(date));
        }
        catch (Exception e) {
            //
        }
        return date;
    }

    /**
     * 长整形转换成时间类型 add by shicy 2011-8-4
     * @param time
     * @return
     */
    public static Date longToDate(long time) {
        Calendar calerdar = Calendar.getInstance();
        calerdar.setTimeInMillis(time);
        return calerdar.getTime();
    }

    /**
     * 判断给定时间是否在当前
     * @param date
     * @return
     */
    public static boolean isDaySame(Date date) {
        return DateUtils.isSameDay(new Date(), date);
    }

    /**
     * 判断给定时间是否在当天之前
     * @param date
     * @return
     */
    public static boolean isDayBefore(Date date) {
        if (date == null)
            throw new RuntimeException("the date must not null");

        Calendar cal1 = dateToCalendar(date);
        Calendar cal2 = dateToCalendar(new Date());

        if (cal1.get(Calendar.YEAR) > cal2.get(Calendar.YEAR))
            return false;

        if (cal1.get(Calendar.YEAR) < cal2.get(Calendar.YEAR))
            return true;

        if (cal1.get(Calendar.DAY_OF_YEAR) < cal2.get(Calendar.DAY_OF_YEAR))
            return true;

        return false;
    }

    /**
     * 判断给定时间是否在当天之后
     * @param date
     * @return
     */
    public static boolean isDayAfter(Date date) {
        if (date == null)
            throw new RuntimeException("the date must not null");

        Calendar cal1 = dateToCalendar(date);
        Calendar cal2 = dateToCalendar(new Date());

        if (cal1.get(Calendar.YEAR) > cal2.get(Calendar.YEAR))
            return true;

        if (cal1.get(Calendar.YEAR) < cal2.get(Calendar.YEAR))
            return false;

        if (cal1.get(Calendar.DAY_OF_YEAR) > cal2.get(Calendar.DAY_OF_YEAR))
            return true;

        return false;
    }

    /**
     * 判断是否是同一个月
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isMonthSame(Date date1, Date date2) {
        if (date1 == date2)
            return true;

        if (date1 == null || date2 == null)
            return false;

        Calendar cal1 = dateToCalendar(date1);
        Calendar cal2 = dateToCalendar(date2);

        if (cal1.get(Calendar.YEAR) != cal2.get(Calendar.YEAR))
            return false;

        if (cal1.get(Calendar.MONTH) != cal2.get(Calendar.MONTH))
            return false;

        return true;
    }

    /**
     * 创建日期
     * @param year
     * @param month 1-12
     * @param date
     * @return
     */
    public static Date newDate(int year, int month, int date) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, date);
        return calendar.getTime();
    }

    /**
     * 获取系统记账月内的所有日期
     * @param sysMonth
     * @return
     */
    public static List<Date> daysOfMonth(String sysMonth){
        ArrayList<Date> days = new ArrayList<Date>();
        Calendar cld= Calendar.getInstance();
        Date firstDay = tryParseDate(sysMonth+"01","yyyyMMdd");
        if(firstDay!=null){
            cld.setTime(firstDay);
            int currMonth=cld.get(Calendar.MONTH);
            int newMonth=currMonth;
            while(currMonth==newMonth){
                days.add(cld.getTime());
                cld.add(Calendar.DAY_OF_MONTH, 1);
                newMonth=cld.get(Calendar.MONTH);
            }
        }
        return days;
    }

}
