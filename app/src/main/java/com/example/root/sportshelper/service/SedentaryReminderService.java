package com.example.root.sportshelper.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.LocationSource;
import com.example.root.sportshelper.R;
import com.example.root.sportshelper.ShowCountDown;
import com.example.root.sportshelper.ShowRemind;
import com.example.root.sportshelper.database.GpsRecord;
import com.example.root.sportshelper.utils.AudioUtils;
import com.example.root.sportshelper.utils.Constant;
import com.example.root.sportshelper.utils.DbHelper;
import com.example.root.sportshelper.utils.MiscUtil;
import com.example.root.sportshelper.utils.MyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

//久坐提醒服务,使用步来做,开始使用timer实现，现在用AlarmManager实现
public class SedentaryReminderService extends Service{
    private String TAG="SedentaryReminderService";
    private Boolean SedentaryReminder;      //就坐提醒是否打开
    private String startTime;               //起始时间
    private String endTime;                   //结束时间
    private Boolean unDisturb;              //午休免打扰是否打开
    private BroadcastReceiver openSedentaryReminder;

    private int flag;                                                   //表示第几次调用计时器
    private int lastStep;
    private AlarmManager manager;

    @Override
    public void onCreate() {                //服务创建时调用
        super.onCreate();
        Log.i(TAG, "久坐提醒服务打开");
        //初始化广播
        initBroadcastReceiver();
        initInfo();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {      //每次服务启动时调用
        if(SedentaryReminder){
            checkSedentary();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void initBroadcastReceiver(){
        //定义意图过滤器
        final IntentFilter filter=new IntentFilter();
        filter.addAction(Constant.SEDENTARYREMINDER);
        openSedentaryReminder=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action =intent.getAction();
                if(action.equals(Constant.SEDENTARYREMINDER)){
                    Log.i(TAG, "onReceive: 接收到久坐提醒变化广播");
                    initInfo();
                    if(SedentaryReminder){
                        if(manager==null){
                            Log.i(TAG, "onReceive: 开启久坐提醒");
                            checkSedentary();
                        }
                    }else {
                        if(manager!=null){
                            Log.i(TAG, "onReceive: 取消久坐提醒");
                            Intent i=new Intent(SedentaryReminderService.this,SedentaryReminderService.class);
                            PendingIntent pi=PendingIntent.getService(SedentaryReminderService.this,0,i,0);
                            manager.cancel(pi);
                            manager=null;
                        }
                    }
                }
                abortBroadcast();           //截断广播
            }
        };
        registerReceiver(openSedentaryReminder,filter);
    }

    private void initInfo(){
        flag=1;
        SedentaryReminder= DbHelper.getOpenSedentaryReminder();
        startTime=DbHelper.getStartTime();
        endTime=DbHelper.getEndTime();
        unDisturb=DbHelper.getOpenUnDisturb();
        lastStep=StepDetector.CURRENT_STEP;             //上一次步数
    }

    //检查是否久坐
    private void checkSedentary(){
//        timer=new Timer(true);
//        task=new TimerTask() {
//            @Override
//            public void run() {
//                try {
//                    if(timeAccordCondition(startTime,endTime)&&!timeAccordunDisturb()){
//                        //当前时间在开始结束时间之间，而且不在打扰时间内
//                        if(flag==1){
//                            flag++;
//                        }else {
//                            //判断步数变化是否小于5
//                            if(Math.abs(lastStep-StepDetector.CURRENT_STEP)<5){
//                                Log.i(TAG, "run: 久坐");
//                                //小于5，则说明久坐了
//                                Intent intent=new Intent(SedentaryReminderService.this, ShowRemind.class);
//                                startActivity(intent);
//
//                            }else {
//                                lastStep=StepDetector.CURRENT_STEP;
//                            }
//                        }
//                    } else {
//                        lastStep=StepDetector.CURRENT_STEP;
//                    }
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        timer.schedule(task,0,3600000);         //每隔一小时检查一下

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(timeAccordCondition(startTime,endTime)&&!timeAccordunDisturb()){
                        //当前时间在开始结束时间之间，而且不在打扰时间内
                        if(flag==1){
                            flag++;
                        }else {
                            //判断步数变化是否小于5
                            if(Math.abs(lastStep-StepDetector.CURRENT_STEP)<5){
                                Log.i(TAG, "run: 久坐");
                                //小于5，则说明久坐了
                                Intent intent=new Intent(SedentaryReminderService.this, ShowRemind.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                            }else {
                                lastStep=StepDetector.CURRENT_STEP;
                            }
                        }
                    } else {
                        lastStep=StepDetector.CURRENT_STEP;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        manager=(AlarmManager)getSystemService(ALARM_SERVICE);
        int anHour=60*60*1000;                  //一个小时的毫秒数
        long triggerAtTime= SystemClock.elapsedRealtime()+anHour;
        Intent i=new Intent(this,SedentaryReminderService.class);
        PendingIntent pi=PendingIntent.getService(this,0,i,0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
    }

    //判断当前时间在开始结束时间之间,true表示在范围内，
    private Boolean timeAccordCondition(String begin,String last) throws ParseException {
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");
        Date startDate,endDate,nowDate;
        Calendar start,end,now;
        startDate=sdf.parse(begin);
        endDate=sdf.parse(last);
        nowDate=sdf.parse(MyTime.getTodayTime());
        start=Calendar.getInstance();
        end=Calendar.getInstance();
        now=Calendar.getInstance();

        start.setTime(startDate);
        end.setTime(endDate);
        now.setTime(nowDate);

        if(SedentaryReminder){
            if(now.after(start)&&now.before(end)){
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }


    //判断当前时间在免打扰时间之间,true表示在范围内，
    private Boolean timeAccordunDisturb() throws ParseException {
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");
        Date startDate,endDate,nowDate;
        Calendar start,end,now;
        startDate=sdf.parse("12:00");
        endDate=sdf.parse("14:00");
        nowDate=sdf.parse(MyTime.getTodayTime());
        start=Calendar.getInstance();
        end=Calendar.getInstance();
        now=Calendar.getInstance();

        start.setTime(startDate);
        end.setTime(endDate);
        now.setTime(nowDate);

        if(SedentaryReminder&&unDisturb){
            if(now.after(start)&&now.before(end)){
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }

    @Override
    public void onDestroy(){                    //服务销毁时调用
        Log.i(TAG, "onDestroy: 久坐服务停止");
        unregisterReceiver(openSedentaryReminder);
        Intent intent=new Intent(this,SedentaryReminderService.class);
        startService(intent);
        super.onDestroy();
    }

}
