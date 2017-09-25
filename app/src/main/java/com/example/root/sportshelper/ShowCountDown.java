package com.example.root.sportshelper;

import android.content.Intent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.root.sportshelper.utils.AudioUtils;
import com.example.root.sportshelper.utils.CountDownTimer;
import com.example.root.sportshelper.utils.DbHelper;
import com.iflytek.cloud.SpeechUtility;


public class ShowCountDown extends AppCompatActivity {
    ImageView showCountDown;
    private TimeCount time;
    private int flag=1;
    private Boolean VoiceAnnouncements;                 //语音播报是否打开
    private int maleVoiceOrfemaleVoice;                 //男声或女声

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showcountdown);

        SpeechUtility.createUtility(getApplicationContext(), "appid=59bb8025");
        VoiceAnnouncements= DbHelper.getOpenVoiceAnnouncements();
        maleVoiceOrfemaleVoice=DbHelper.getmaleVoiceOrfemaleVoice();
        AudioUtils.getInstance().init(this,maleVoiceOrfemaleVoice);

        showCountDown=(ImageView)findViewById(R.id.showCountDown);
        time=new TimeCount(4050,1000);
        time.start();

    }

    class TimeCount extends CountDownTimer {
        /**
         * 构造函数
         * @param millisInFuture  倒计时时间
         * @param countDownInterval  倒计时时间间隔
         */
        public TimeCount(long millisInFuture,long countDownInterval){
            super(millisInFuture,countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if(flag==1){
                showCountDown.setImageResource(R.mipmap.ic_run_countdown_3);
                if(VoiceAnnouncements){
                    AudioUtils.getInstance().speakText("3");
                }
            }else if(flag==2){
                showCountDown.setImageResource(R.mipmap.ic_run_countdown_2);
                if(VoiceAnnouncements){
                    AudioUtils.getInstance().speakText("2");
                }
            }else if(flag==3){
                showCountDown.setImageResource(R.mipmap.ic_run_countdown_1);
                if(VoiceAnnouncements){
                    AudioUtils.getInstance().speakText("1");
                }
            }else if(flag==4){
                showCountDown.setImageResource(R.mipmap.ic_run_countdown_go);
                if(VoiceAnnouncements){
                    AudioUtils.getInstance().speakText("Go");
                }
            }
            flag++;
        }

        @Override
        public void onFinish() {
            Intent intent=new Intent(ShowCountDown.this,InRunning.class);
            startActivity(intent);
            ShowCountDown.this.finish();
        }
    }
}
