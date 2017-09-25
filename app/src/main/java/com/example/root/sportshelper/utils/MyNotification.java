package com.example.root.sportshelper.utils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.root.sportshelper.R;
import com.example.root.sportshelper.service.StepDetector;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * 通知工具
 * Created by root on 17-9-21.
 */

public class MyNotification {
    private static String TAG="MyNotification";
    private static NotificationManager mNotificationManager;
    private static NotificationCompat.Builder mBuilder;

    private static long systemTime;
    private static Intent oneIntent;
    private static PendingIntent oneSender;
    private static long  oneFirstTime;
    private static Calendar oneCalendar;
    private static AlarmManager oneAlarmManager;

    private static Intent twoIntent;
    private static PendingIntent twoSender;
    private static long twoFirstTime;
    private static Calendar twoCalendar;
    private static AlarmManager twoAlarmManager;

    private static Context myContext;

    private static final long DAY = 1000L * 60 * 60 * 24;
    private static final String ALARM_ACTION="com.example.root.sportshelper.alarm.clock";
    private static final String ALARM_ACTION_TEN="com.example.root.sportshelper.alarm.clockTen";

    //单例模式，参考：http://blog.csdn.net/beyond0525/article/details/22794221/
    private static class MyNotificationHolder{
        private static MyNotification instance=new MyNotification();
    }

    private MyNotification(){

    }

    public static MyNotification getInstance(){
        return MyNotificationHolder.instance;
    }

    public void init(Context context){
        myContext=context;
        mNotificationManager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder=new NotificationCompat.Builder(context);
        mBuilder.setOngoing(false);
        mBuilder.setDefaults(Notification.DEFAULT_ALL);
        mBuilder.setWhen(System.currentTimeMillis());
        mBuilder.setSmallIcon(R.mipmap.ic_desktop_icon);
        systemTime = System.currentTimeMillis();               //现在时间
    }

    //初始化两点的通知
    public  void initOne(Context context){
        oneIntent = new Intent(ALARM_ACTION);
        oneIntent.putExtra("flag", "one");
        oneIntent.putExtra("step", StepDetector.CURRENT_STEP);
        oneSender = PendingIntent.getBroadcast(context, 0, oneIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        oneFirstTime = SystemClock.elapsedRealtime();              // 开机之后到现在的运行时间(包括睡眠时间)

        oneCalendar = Calendar.getInstance();
        oneCalendar.setTimeInMillis(System.currentTimeMillis());
        // 这里时区需要设置一下，不然会有8个小时的时间差
        oneCalendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        oneCalendar.set(Calendar.MINUTE, 8);
        oneCalendar.set(Calendar.HOUR_OF_DAY, 14);
        oneCalendar.set(Calendar.SECOND, 0);
        // 选择的定时时间
        long selectTime = oneCalendar.getTimeInMillis();

        // 如果当前时间大于设置的时间，那么就从第二天的设定时间开始
        if (systemTime > selectTime) {
            Log.i(TAG, "init: 设置的时间小于当前时间");
            oneCalendar.add(Calendar.DAY_OF_MONTH, 1);
            selectTime = oneCalendar.getTimeInMillis();
        }
        // 计算现在时间到设定时间的时间差
        long time = selectTime - systemTime;
        oneFirstTime += time;
        oneAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            Log.i(TAG, "initOne: 版本大于1.9");
            oneAlarmManager.setWindow(AlarmManager.ELAPSED_REALTIME_WAKEUP,oneFirstTime,0,oneSender);
        }else {
            //1.9版本以前
            oneAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,oneFirstTime,DAY,oneSender);
        }
    }

    //初始化十点的通知
    public  void initTwo(Context context){
        twoIntent = new Intent(ALARM_ACTION_TEN);
        twoIntent.putExtra("flag", "two");
        twoIntent.putExtra("step", StepDetector.CURRENT_STEP);
        twoSender = PendingIntent.getBroadcast(context, 0, twoIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        twoFirstTime = SystemClock.elapsedRealtime();              // 开机之后到现在的运行时间(包括睡眠时间)

        twoCalendar = Calendar.getInstance();
        twoCalendar.setTimeInMillis(System.currentTimeMillis());
        // 这里时区需要设置一下，不然会有8个小时的时间差
        twoCalendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        twoCalendar.set(Calendar.MINUTE, 0);
        twoCalendar.set(Calendar.HOUR_OF_DAY, 22);
        twoCalendar.set(Calendar.SECOND, 0);
        // 选择的定时时间
        long selectTime = twoCalendar.getTimeInMillis();

        // 如果当前时间大于设置的时间，那么就从第二天的设定时间开始
        if (systemTime > selectTime) {
            Log.i(TAG, "init: 设置的时间小于当前时间");
            twoCalendar.add(Calendar.DAY_OF_MONTH, 1);
            selectTime = twoCalendar.getTimeInMillis();
        }
        // 计算现在时间到设定时间的时间差
        long time = selectTime - systemTime;
        twoFirstTime += time;
        twoAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            Log.i(TAG, "initTwo: 版本大于1.9");
            twoAlarmManager.setWindow(AlarmManager.ELAPSED_REALTIME_WAKEUP,twoFirstTime,0,twoSender);
        }else {
            //1.9版本以前
            twoAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,twoFirstTime,DAY,twoSender);
        }
    }

    public  void setAlarmTime(Context context, long timeInMillis, Intent intent) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent Sender = PendingIntent.getBroadcast(context, 0,intent, PendingIntent.FLAG_CANCEL_CURRENT);
        int interval = (int) intent.getLongExtra("intervalMillis", 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            am.setWindow(AlarmManager.ELAPSED_REALTIME_WAKEUP, timeInMillis, interval, Sender);
        }
    }

    //步数通知
    public void showStepNotification(String title,String content){
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(content);
        showNotification();
    }

    public void showSuccessNotification(){
        //达到目标
        mBuilder.setContentTitle(myContext.getResources().getString(R.string.Congratulations));
        mBuilder.setContentText(myContext.getResources().getString(R.string.CompleteTarget));
        showNotification();
    }

    private void showNotification(){
        Notification notification=mBuilder.build();
        mNotificationManager.notify(R.string.app_name,notification);
    }
}
