package com.example.root.sportshelper;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.root.sportshelper.service.AppWidgetService;
import com.example.root.sportshelper.service.StepDetector;
import com.example.root.sportshelper.utils.Constant;

import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of App Widget functionality.
 */
public class ShowInfoWidget extends AppWidgetProvider {
    private static final String TAG="ShowInfoWidget";
    // 启动AppWidgetService服务对应的action
    private Intent SERVICE_INTENT=new Intent("android.appWidget.action.APP_WIDGET_SERVICE");
    // 更新 widget 的广播对应的action
    private final String ACTION_UPDATE_ALL = "com.example.root.SportsHelper.UPDATE_ALL";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {


        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.show_info_widget);
        //views.setTextViewText(R.id.appwidget_text, widgetText);

        views.setImageViewResource(R.id.step,R.mipmap.ic_run_list_calories);
        views.setImageViewResource(R.id.Calorie,R.mipmap.ic_daily_goal_calories);
        views.setTextViewText(R.id.showStep, String.valueOf(Constant.CURR_STEP)+"步");
        views.setTextViewText(R.id.showCalorie,String.valueOf(Constant.CURR_CALORIE)+"大卡");
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        // 根据updatePeriodMillis属性定义的时间进行更新Widget，用户添加Widget时也会调用
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        // 在第一个 widget 被创建时，开启服务
        SERVICE_INTENT.setPackage(context.getPackageName());

        context.startService(SERVICE_INTENT);
        //context.startService(new Intent(context,AppWidgetService.class));
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        // 在最后一个 widget 被删除时，终止服务
        SERVICE_INTENT.setPackage(context.getPackageName());

        context.stopService(SERVICE_INTENT);
        //context.stopService(new Intent(context,AppWidgetService.class));
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        final String action = intent.getAction();
        Log.i(TAG, "OnReceive:Action: " + action);

        if(ACTION_UPDATE_ALL.equals(action)){
            // “更新”广播
            RemoteViews remoteViews=new RemoteViews(context.getPackageName(),R.layout.show_info_widget);
            remoteViews.setTextViewText(R.id.showStep, String.valueOf(Constant.CURR_STEP)+"步");
            remoteViews.setTextViewText(R.id.showCalorie,String.valueOf(Constant.CURR_CALORIE)+"大卡");

            AppWidgetManager appWidgetManager=AppWidgetManager.getInstance(context);
            ComponentName componentName=new ComponentName(context,ShowInfoWidget.class);

            // 更新appWidget
            appWidgetManager.updateAppWidget(componentName, remoteViews);
        }
        super.onReceive(context, intent);
    }

}

