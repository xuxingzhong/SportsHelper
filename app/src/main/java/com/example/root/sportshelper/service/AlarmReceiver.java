package com.example.root.sportshelper.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import com.example.root.sportshelper.utils.DbHelper;
import com.example.root.sportshelper.utils.MyNotification;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 两点定时接收器
 * Created by root on 17-9-22.
 */

public class AlarmReceiver extends BroadcastReceiver {
    private String TAG="AlarmReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle=intent.getExtras();
        int step=bundle.getInt("step");
        String title="今日走了"+step+"步";
        int diff=DbHelper.getRTStepTarget()-step;

        String content="距离目标还差"+ diff+"步,加油！";

        Log.i(TAG, "onReceive: 两点的通知");

        if(diff>0){
            MyNotification.getInstance().init(context);
            MyNotification.getInstance().showStepNotification(title,content);
        }


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            //initOne();
            //MyNotification.getInstance().initOne(context);
            Calendar calendar=Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.DAY_OF_MONTH,1);

            long systemTime = System.currentTimeMillis();               //现在时间
            long oneFirstTime = SystemClock.elapsedRealtime();              // 开机之后到现在的运行时间(包括睡眠时间)
            long selectTime = calendar.getTimeInMillis();
            long time = selectTime - systemTime;
            oneFirstTime += time;
            MyNotification.getInstance().setAlarmTime(context,oneFirstTime,intent);
        }
    }
}
