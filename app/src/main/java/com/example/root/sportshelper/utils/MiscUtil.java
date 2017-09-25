package com.example.root.sportshelper.utils;

import android.content.Context;
import android.graphics.Paint;
import android.location.GpsStatus;
import android.util.Log;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps2d.model.LatLng;

/**
 * Created by root on 17-8-7.
 */

public class MiscUtil {
    private static String TAG="MiscUtil";
    private  static long lastClickTime=0;//上次点击的时间
    private  static int spaceTime = 1000;//时间间隔
    /**
     * 测量 View
     *
     * @param measureSpec
     * @param defaultSize View 的默认大小
     * @return
     */
    public static int measure(int measureSpec, int defaultSize) {
        int result = defaultSize;
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);

        if (specMode == View.MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == View.MeasureSpec.AT_MOST) {
            result = Math.min(result, specSize);
        }
        return result;
    }

    /**
     * dip 转换成px
     *
     * @param dip
     * @return
     */
    public static int dipToPx(Context context, float dip) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f * (dip >= 0 ? 1 : -1));
    }

    //sp转换为px（像素）
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获取数值精度格式化字符串
     *
     * @param precision
     * @return
     */
    public static String getPrecisionFormat(int precision) {
        return "%." + precision + "f";
    }

    /**
     * 反转数组
     *
     * @param arrays
     * @param <T>
     * @return
     */
    public static <T> T[] reverse(T[] arrays) {
        if (arrays == null) {
            return null;
        }
        int length = arrays.length;
        for (int i = 0; i < length / 2; i++) {
            T t = arrays[i];
            arrays[i] = arrays[length - i - 1];
            arrays[length - i - 1] = t;
        }
        return arrays;
    }

    /**
     * 测量文字高度
     * @param paint
     * @return
     */
    public static float measureTextHeight(Paint paint) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return (Math.abs(fontMetrics.ascent) - fontMetrics.descent);
    }

    //避免连续点击
    public  static boolean isFastClick() {
        long currentTime = System.currentTimeMillis();//当前系统时间
        boolean isAllowClick;//是否允许点击
        if (currentTime - lastClickTime > spaceTime) {
            isAllowClick= false;
        } else {
            isAllowClick = true;
        }
        lastClickTime = currentTime;
        return isAllowClick;
    }

    //获取配速，参考 https://baike.baidu.com/item/%E9%85%8D%E9%80%9F/3904006?fr=aladdin，配速是时间单位
    public static float getSpeed(float speed, float avgSpeed) {
        if (speed < 0.001) return 0;
        if (speed<avgSpeed/2)
            speed = avgSpeed/2;                 //avoid too big pace
        return 1000/(60*speed);                 //speed现在是m/s，结果为分钟，如7.5
    }

    public static String getSpeedString(float speed) {
        float sp = getSpeed(speed,0);
        return String.format("%02d:%02d", (int)Math.floor(sp), Math.round(60 * (sp - Math.floor(sp))));
    }

    //转换为公里
    public static float getDistance(float distance) {
        return distance/1000f;
    }

    //高德地图计算两点距离
    public static float getDistance(LatLng start, LatLng end){
        double lon1 = (Math.PI / 180) * start.longitude;
        double lon2 = (Math.PI / 180) * end.longitude;
        double lat1 = (Math.PI / 180) * start.latitude;
        double lat2 = (Math.PI / 180) * end.latitude;

        float result=0;
        // 地球半径
        double R = 6371;

        // 两点间距离 km，如果想要米的话，结果*1000就可以了
        double d = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1))
                * R;
        result=(float) (d*1000);
        return result;
    }

    //根据AMapLocation获得LatLng
    public static LatLng getLatLng(AMapLocation aMapLocation){
        LatLng latLng=new LatLng(aMapLocation.getAltitude(),aMapLocation.getLongitude());
        return latLng;
    }


}
