package com.example.root.sportshelper.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 开机完成广播
 * Created by root on 17-8-18.
 */

public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
        Intent i=new Intent(context,StepService.class);
        context.startService(i);
        Intent x=new Intent(context,SedentaryReminderService.class);
        context.startService(x);
    }
}
