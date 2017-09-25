package com.example.root.sportshelper.utils;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;


/**
 * 语音播放工具类
 * Created by root on 17-9-15.
 */

public class AudioUtils {
    private static AudioUtils audioUtils;
    private SpeechSynthesizer mySynthesizer;

    public AudioUtils(){

    }

    /**
     * 描述:单例
     * http://www.jianshu.com/p/8fd3bcc33104
     */
    public static AudioUtils getInstance() {
        if (audioUtils == null) {
            synchronized (AudioUtils.class) {
                if (audioUtils == null) {
                    audioUtils = new AudioUtils();
                }
            }
        }
        return audioUtils;
    }

    private InitListener myInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d("mySynthesiezer:", "InitListener init() code = " + code);
        }
    };

    /**
     * 描述:初始化语音配置
     *
     */
    public void init(Context context,int maleVoiceOrfemaleVoice) {
        //处理语音合成关键类
        mySynthesizer = SpeechSynthesizer.createSynthesizer(context, myInitListener);
        if(maleVoiceOrfemaleVoice==1){
            //男声
            //设置发音人
            mySynthesizer.setParameter(SpeechConstant.VOICE_NAME, "xiaoyu");
        }else if(maleVoiceOrfemaleVoice==2){
            //女声
            mySynthesizer.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
        }
        //设置音调
        mySynthesizer.setParameter(SpeechConstant.PITCH, "80");
        //设置语速
        mySynthesizer.setParameter(SpeechConstant.SPEED,"80");
        //设置音量
        mySynthesizer.setParameter(SpeechConstant.VOLUME, "90");

    }

    /**
     * 描述:根据传入的文本转换音频并播放
     */
    public void speakText(String content) {
        int code = mySynthesizer.startSpeaking(content, new SynthesizerListener() {
            @Override
            public void onSpeakBegin() {

            }

            @Override
            public void onBufferProgress(int i, int i1, int i2, String s) {

            }

            @Override
            public void onSpeakPaused() {

            }

            @Override
            public void onSpeakResumed() {

            }

            @Override
            public void onSpeakProgress(int i, int i1, int i2) {

            }

            @Override
            public void onCompleted(SpeechError speechError) {

            }

            @Override
            public void onEvent(int i, int i1, int i2, Bundle bundle) {

            }
        });
    }

}
