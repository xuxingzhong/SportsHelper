package com.example.root.sportshelper;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.example.root.sportshelper.ruler.GifView;

public class ShowRemind extends AppCompatActivity {
    private String TAG="ShowRemind";
    GifView gifView;
    PowerManager powerManager;
    PowerManager.WakeLock wakeLock;
    Handler mTimeHandler=new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_remind);

        powerManager=(PowerManager)getSystemService(Context.POWER_SERVICE);
        wakeLock=powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.SCREEN_DIM_WAKE_LOCK,"TAG");
        wakeLock.acquire();             //屏幕会持续点亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        //然后定时
        mTimeHandler.postDelayed(new Runnable(){
            public void run(){
                wakeLock.release();//
            }
        }, 10*1000);//延时10秒灭屏
        gifView=(GifView)findViewById(R.id.gifView);
        gifView.setGifResource(R.mipmap.walk);
        initInfo();
        gifView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initInfo(){
        int width = gifView.getGifWidth();
        int height = gifView.getGifHeight();
        int screenWidth = getScreenSize().width();
        int screenHeight = getScreenSize().height();

        if (width > 0 && height > 0) {
            float wScale = (float) screenWidth / width;
            float hScale = (float) screenHeight / height;
            if (wScale < 1 || hScale < 1) {
                // 如果图片的宽或高大于屏幕的宽或高，则图片会自动缩小至全屏
            } else if (wScale <= hScale) {
                Log.i(TAG, "initInfo: 宽度全屏");
                // 宽度全屏
                gifView.setScaleX(wScale);
                gifView.setScaleY(wScale);

            } else {
                Log.i(TAG, "initInfo: 高度全屏");
                // 高度全屏
                gifView.setScaleX(hScale);
                gifView.setScaleY(hScale);
            }
        }
    }

    /**
     * 得到屏幕的尺寸
     *
     * @return 包含屏幕尺寸的Rect对象
     */
    private Rect getScreenSize() {
        Rect localRect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        return localRect;
    }
}
