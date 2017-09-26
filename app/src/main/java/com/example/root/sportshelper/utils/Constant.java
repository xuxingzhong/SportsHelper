package com.example.root.sportshelper.utils;

import android.content.Intent;

/**
 * Created by root on 17-8-17.
 */

public class Constant {
    //用于发送消息
    public static final int MSG_FROM_CLIENT=0;
    public static final int MSG_FROM_SERVER=1;
    public static final int REQUEST_SERVER=2;
    //用于分辨日周月
    public static final int DAY=0;
    public static final int WEEK=1;
    public static final int MONTH=2;

    public static int LOCATION_ACCURACY = 50;
    public static int INTERVAL_LOCATION = 10;               //位置间隔
    public static final int INVALID_ALT = -999;             //无效的高度
    public static int SPEED_AVG = 5;
    public static int MIN_DISTANCE = 3;

    public static final String BC_INTENT = "UpdateRecords";
    public static final String ACTION_DATE_CHANGED = Intent.ACTION_DATE_CHANGED;       //监听日期变化
    public static final String UPDATESTEPCOUNT = "UpdataeStepCount";

    public static final String UPDATESTEPTARGET="UpdateStepTarget";                    //改变计步目标
    public static final String SEDENTARYREMINDER="SedentaryReminder";                   //久坐提醒

    public static int CURR_STEP=0;                      //当前步数
    public static int CURR_CALORIE=0;                  //当前卡路里

    public static final int TIME_CONSUMING=1;                   //耗时操作
}
