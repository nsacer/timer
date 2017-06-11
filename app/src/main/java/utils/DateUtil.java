package utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by Administrator on 2016/8/20.
 *
 */

public class DateUtil {

    private static DateUtil date;

    /**
     * 实例化单例
     */
    public static DateUtil getInstance() {

        synchronized (DateUtil.class) {

            if (date == null)
                date = new DateUtil();
            return date;
        }
    }

    /**
     * 根据传入的"System.currentTimeMillis()"输出格式化之后的时间
     * @return 2016/01/03
     */
    public String getDateFormat(long time) {

//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
        Date date = new Date(time);
        return sdf.format(date);
    }

    /**
     * 根据传入的"System.currentTimeMillis()"输出格式化之后的时间
     * @return 2016/01/03
     */
    public String getDateFormatShort(long time) {

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd", Locale.CHINA);
        Date date = new Date(time);
        return sdf.format(date);
    }

    /**
     * 根据传入的"System.currentTimeMillis()"输出格式化之后的时间
     * @return 2016.01.03   12:25   -25.00元
     */
    public String getDateFormatDetail(long time) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd   hh:mm   ", Locale.CHINA);
        Date date = new Date(time);
        return sdf.format(date);
    }

    /**
     * 根据传入的"System.currentTimeMillis()"输出String[]{"年","月","日","时","分","秒","周"}数组
     */
    public String[] getCalendarFormat(long time) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);

        String[] t = new String[7];
        t[0] = String.valueOf(calendar.get(Calendar.YEAR));
        t[1] = String.valueOf(calendar.get(Calendar.MONTH));
        t[2] = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        t[3] = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        t[4] = String.valueOf(calendar.get(Calendar.MINUTE));
        t[5] = String.valueOf(calendar.get(Calendar.SECOND));
        t[6] = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH));

        return t;
    }

    /**
     * 根据传入的对应格式时间字符串（yyyy/MM/dd）得到对应的毫秒数
     * @param sTime yyyy/MM/dd
     */
    public long getTimeInMillisFromString(String sTime) {

        long time = 0;

        if(TextUtils.isEmpty(sTime))
            return time;

        //输入日期的格式
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
        Date date = null;
        try {
            date = simpleDateFormat.parse(sTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        time = gc.getTimeInMillis();

        return time;
    }

    /**
     * 根据传入的起止时间字符串计算出中间间隔的天数
     * @param startTime yyyy/MM/dd
     * @param endTime yyyy/MM/dd
     */
    public int getCurrentDays(String startTime, String endTime) {

        int currentDays = 0;
        if (TextUtils.isEmpty(startTime) || TextUtils.isEmpty(endTime))
            return currentDays;

        long lStart = getTimeInMillisFromString(startTime);
        long lEnd = getTimeInMillisFromString(endTime);

        //从间隔毫秒变成间隔天数
        currentDays = (int) ((lEnd - lStart) / (1000 * 3600 * 24)) + 1;
        return currentDays;
    }
}
