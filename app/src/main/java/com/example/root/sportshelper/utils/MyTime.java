package com.example.root.sportshelper.utils;




import android.util.Log;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by root on 17-8-10.
 */

public class MyTime {
    private static final String TAG="MyTime";     //"StepDetector";
    private static int WeekNumber=-1;
    private static int MonthNumber=-1;

    //获取当前日期的String类型,如：12月10日
    public static String getNowDateToString(){
        Calendar calendar=Calendar.getInstance();
        int month=calendar.get(Calendar.MONTH)+1;
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        return  month+"月"+day+"日";
    }
    //是不是昨天
    public static Boolean isYesterday(String date)throws ParseException{
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.DATE,-1);
        String yesterday=new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
        if(yesterday.equals(date)){
            return true;
        }else {
            return false;
        }
    }

    //装换格式，如将2017-8-14装换为8月14日
    public static String changeFormat(String date) throws ParseException {
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
        Date mydate =sdf.parse(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mydate);
        String monthAndDay=(new SimpleDateFormat("M月d日")).format(calendar.getTime());
        return monthAndDay;
    }
    //装换格式，如将2017-8-14装换为8/14
    public static String changeFormatOther(String date) throws ParseException {
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
        Date mydate =sdf.parse(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mydate);
        String monthAndDay=(new SimpleDateFormat("M/d")).format(calendar.getTime());
        return monthAndDay;
    }

    /**
     * 获得今天的日期
     */
    public static String getTodayDate(){
        Date date=new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    //根据某个日期获得前几天的日期
    public static String getFrontDate(String startDate,int day)  {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date myDate= null;
        try {
            myDate = sdf.parse(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(myDate);
        calendar.add(Calendar.DAY_OF_MONTH,-day);
        return sdf.format(calendar.getTime());
    }

    //根据Date获得String类型时间
    public static String getTimeFromDate(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(date);
    }

    /**
     * 获得现在的时间
     */
    public static String getTodayTime(){
        Date time=new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(time);
    }

    //时间的秒数去掉
    public static String getTimeOffsecond(String time) throws ParseException {
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");
        Date oldTime=sdf.parse(time);

        SimpleDateFormat otherSdf=new SimpleDateFormat("HH:mm");
        String result=otherSdf.format(oldTime);
        return result;
    }

    //获得时间差
    public static String getTimeDifference(String startTime,String endTime) throws ParseException {
        int flag=0;         //进位
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");
        Date start=sdf.parse(startTime);
        Date end=sdf.parse(endTime);
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(start);
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(end);
        long s=endCalendar.get(Calendar.SECOND)-startCalendar.get(Calendar.SECOND);           //秒数
        if(s<0){
            s=s+60;
            flag=1;
        }
        long min=endCalendar.get(Calendar.MINUTE)-startCalendar.get(Calendar.MINUTE);         //分数
        if(flag==1){
            min--;
            flag=0;
        }
        if(min<0){
            min=min+60;
            flag=1;
        }
        long hour=endCalendar.get(Calendar.HOUR_OF_DAY)-startCalendar.get(Calendar.HOUR_OF_DAY);      //小时
        if(flag==1){
            hour--;
            flag=0;
        }
        return hour+":"+min+":"+s;
    }

    //获得时间和
    public static String getTimeSum(String ontTime,String twoTime) throws ParseException {
        String myMin,myS;
        int flag=0;         //进位
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");
        Date start=sdf.parse(ontTime);
        Date end=sdf.parse(twoTime);
        Calendar oneCalendar = Calendar.getInstance();
        oneCalendar.setTime(start);
        Calendar twoCalendar = Calendar.getInstance();
        twoCalendar.setTime(end);
        long s=oneCalendar.get(Calendar.SECOND)+twoCalendar.get(Calendar.SECOND);           //秒数
        if(s>60){
            s=s-60;
            flag=1;
        }
        long min=oneCalendar.get(Calendar.MINUTE)+twoCalendar.get(Calendar.MINUTE);         //分数
        if(flag==1){
            min++;
            flag=0;
        }
        if(min>60){
            min=min-60;
            flag=1;
        }
        long hour=oneCalendar.get(Calendar.HOUR_OF_DAY)+twoCalendar.get(Calendar.HOUR_OF_DAY);      //小时
        if(flag==1){
            hour++;
            flag=0;
        }
        //获得min前面的0
        if(min<10){
            myMin="0"+min;
        }else {
            myMin=String.valueOf(min);
        }

        if(s<10){
            myS="0"+s;
        }else {
            myS=String.valueOf(s);
        }
        return hour+":"+myMin+":"+myS;
    }

    //判断是不是新周
    public static Boolean isNewWeek(String time) throws ParseException {
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
        Date mydate =sdf.parse(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mydate);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);

        //如果是周日,日期减一天
        int dayofweek = calendar.get(Calendar.DAY_OF_WEEK);
        if(dayofweek==1){
            calendar.add(Calendar.DAY_OF_MONTH,-1);
        }
        int week=calendar.get(Calendar.WEEK_OF_YEAR);
        if(week==WeekNumber){
            return false;
        }else {
            WeekNumber=week;
            return true;
        }
    }

    //获得该周的起始日期
    public static String getBeginDayOfWeek(String time) throws ParseException {
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
        Date mydate =sdf.parse(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mydate);
        int dayofweek = calendar.get(Calendar.DAY_OF_WEEK);
        if(dayofweek==1){
            dayofweek+=7;
        }
        calendar.add(Calendar.DATE, 2 - dayofweek);
        return sdf.format(calendar.getTime());
    }

    //获得该周的结束日期
    public static String getEndDayOfWeek(String time) throws ParseException {
        String startOfWeek=getBeginDayOfWeek(time);
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
        Date mydate =sdf.parse(startOfWeek);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mydate);
        calendar.add(Calendar.DAY_OF_WEEK, 6);
        return sdf.format(calendar.getTime());
    }

    //是不是本周
    public static Boolean isThisWeek(String time) throws ParseException {
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
        Date mydate =sdf.parse(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mydate);
        int timeOfWeek=calendar.get(Calendar.WEEK_OF_YEAR);

        Calendar thisCanlendar=Calendar.getInstance();
        int thisWeek=thisCanlendar.get(Calendar.WEEK_OF_YEAR);
        if(timeOfWeek==thisWeek){
            return true;
        }else {
            return false;
        }
    }

    //是不是上周
    public static Boolean isLastWeek(String time) throws ParseException {
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
        Date mydate =sdf.parse(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mydate);
        int timeOfWeek=calendar.get(Calendar.WEEK_OF_YEAR);

        Calendar thisCanlendar=Calendar.getInstance();
        thisCanlendar.add(Calendar.WEEK_OF_YEAR,-1);
        int lastWeek=thisCanlendar.get(Calendar.WEEK_OF_YEAR);
        if(timeOfWeek==lastWeek){
            return true;
        }else {
            return false;
        }
    }

    //如1:25:30转换为多少小时
    public static String getOccupyHour(String time,int count) throws ParseException {
        double averageHour=0;         //平均小时
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");
        Date start=sdf.parse(time);
        Calendar hourCalendar = Calendar.getInstance();
        hourCalendar.setTime(start);
        long allS=hourCalendar.get(Calendar.SECOND)+hourCalendar.get(Calendar.MINUTE)*60+hourCalendar.get(Calendar.HOUR_OF_DAY)*3600;

        if(count!=-1){
            allS=allS/count;
        }
        averageHour=(double) allS/3600;

        DecimalFormat df = new DecimalFormat("0.00");//格式化小数，不足的补0
        String finalaverageHour = df.format(averageHour);//返回的是String类型的
        return finalaverageHour;
    }

    public static void resetWeekNumberAndMonthNumber(){
        WeekNumber=-1;
        MonthNumber=-1;
    }

    //判断是不是新月
    public static Boolean isNewMonth(String time) throws ParseException {
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
        Date mydate =sdf.parse(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mydate);
        int month=calendar.get(Calendar.MONTH);
        if(month==MonthNumber){
            return false;
        }else {
            MonthNumber=month;
            return true;
        }
    }
    //获得月数
    public static String getMonthNumber(String time)throws ParseException{
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
        Date mydate =sdf.parse(time);

        SimpleDateFormat mysdf = new SimpleDateFormat("M月");
        return mysdf.format(mydate);

    }
    //获得该月的起始日期
    public static String getBeginDayOfMonth(String time) throws ParseException {
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
        Date mydate =sdf.parse(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mydate);
        int year=calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        calendar.set(year,month,1);
        return sdf.format(calendar.getTime());
    }

    //获得该月的结束日期
    public static String getEndDayOfMonth(String time) throws ParseException {
        String startOfWeek=getBeginDayOfWeek(time);
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
        Date mydate =sdf.parse(startOfWeek);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mydate);
        int day=calendar.getActualMaximum(Calendar.DATE);
        calendar.set(Calendar.YEAR,Calendar.MONTH,day);
        return sdf.format(calendar.getTime());
    }

    //是不是本月
    public static Boolean isThisMonth(String time) throws ParseException {
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
        Date mydate =sdf.parse(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mydate);
        int timeOfMonth=calendar.get(Calendar.MONTH);

        Calendar thisCanlendar=Calendar.getInstance();
        int thisMonth=thisCanlendar.get(Calendar.MONTH);
        if(timeOfMonth==thisMonth){
            return true;
        }else {
            return false;
        }
    }

    //是不是上月
    public static Boolean isLastMonth(String time) throws ParseException {
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
        Date mydate =sdf.parse(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mydate);
        int timeOfMonth=calendar.get(Calendar.MONTH);

        Calendar thisCanlendar=Calendar.getInstance();
        thisCanlendar.add(Calendar.MONTH,-1);
        int lastMonth=thisCanlendar.get(Calendar.MONTH);
        if(timeOfMonth==lastMonth){
            return true;
        }else {
            return false;
        }
    }

    //将00:30转换为30分钟，将1:30转换为1时30分
    public static String getTimeConvert(String time) throws ParseException {
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");
        Date oldTime=sdf.parse(time);
        SimpleDateFormat hourSdf=new SimpleDateFormat("HH");
        String flag=hourSdf.format(oldTime);
        if(Integer.valueOf(flag)==0){
            SimpleDateFormat MinuteSdf=new SimpleDateFormat("mm分钟");
            return MinuteSdf.format(oldTime);
        }else {
            SimpleDateFormat hourAndMinuteSdf=new SimpleDateFormat("H时mm分钟");
            return hourAndMinuteSdf.format(oldTime);
        }
    }

    //如1:25:30转换为多少分钟
    public static int getOccupyMinute(String time) throws ParseException {
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");
        Date start=sdf.parse(time);
        Calendar minuteCalendar = Calendar.getInstance();
        minuteCalendar.setTime(start);
        int allMinute=minuteCalendar.get(Calendar.MINUTE)+minuteCalendar.get(Calendar.HOUR_OF_DAY)*60;

        return allMinute;
    }

    //将8:00转换为Calendar
    public static Calendar changStringToCalendar(String time) throws ParseException {
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");
        Date newTime=sdf.parse(time);
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(newTime);
        return calendar;
    }

    //比较时间大小
    public static Boolean compareTime(String startTime,String endTime) throws ParseException {
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");
        Date StartTime=sdf.parse(startTime);
        Date EndTime=sdf.parse(endTime);
        int num=EndTime.compareTo(StartTime);
        if(num==1){
            //结束时间大于起始时间
            return true;
        }else {
            return false;
        }
    }


    //获得时间差是否大于1小时,大于为true
    public static Boolean getTimeDifferenceGreaterThanHour(String startTime,String endTime) {
        int flag=0;         //进位
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");
        Date start= null;
        try {
            start = sdf.parse(startTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date end= null;
        try {
            end = sdf.parse(endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(start);
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(end);
        long s=endCalendar.get(Calendar.SECOND)-startCalendar.get(Calendar.SECOND);           //秒数
        if(s<0){
            s=s+60;
            flag=1;
        }
        long min=endCalendar.get(Calendar.MINUTE)-startCalendar.get(Calendar.MINUTE);         //分数
        if(flag==1){
            min--;
            flag=0;
        }
        if(min<0){
            min=min+60;
            flag=1;
        }
        long hour=endCalendar.get(Calendar.HOUR_OF_DAY)-startCalendar.get(Calendar.HOUR_OF_DAY);      //小时
        if(flag==1){
            hour--;
            flag=0;
        }
        if(hour>1){
            return true;
        }else {
            return false;
        }
    }
}
