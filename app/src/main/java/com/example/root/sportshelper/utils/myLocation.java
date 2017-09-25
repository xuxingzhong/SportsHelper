package com.example.root.sportshelper.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;

import com.example.root.sportshelper.R;

/**
 * 位置服务
 * Created by root on 17-9-5.
 */

public class myLocation {

    //判断gps是否打开
    public static Boolean isOpenGps(final Context context){
        LocationManager manager=(LocationManager) context.getSystemService( Context.LOCATION_SERVICE );
        if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){                //判断指定提供程序是否能用
            return true;
        }
        //AlertDialog.Builder builder=new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_LIGHT);
        AlertDialog.Builder builder=new AlertDialog.Builder(context,R.style.NoBackGroundDialog);
        builder.setMessage(context.getResources().getString(R.string.gps_prompt))
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
        return false;
    }

    //获得gps信号

}
