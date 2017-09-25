package com.example.root.sportshelper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.root.sportshelper.database.SettingRecord;
import com.example.root.sportshelper.ruler.RulerView;
import com.example.root.sportshelper.utils.DbHelper;

public class SettingOfVoiceAnnouncements extends AppCompatActivity implements View.OnClickListener{
    private String TAG="SettingOfVoiceAnnouncements";
    TextView title;
    ImageView back;

    ImageView openVoiceAnnouncementsSwitch;             //开启语音播报
    private Boolean openVoiceAnnouncements;             //语音播报是否开启

    LinearLayout maleVoice;                                 //男声
    LinearLayout femaleVoice;                               //女声
    ImageView maleVoiceDone;                                //男声选择
    ImageView femaleVoiceDone;                              //女声选择

    LinearLayout halfkilometers;                            //0.5公里
    ImageView halfkilometersDone;                           //0.5公里选择
    LinearLayout onekilometers;                             //1公里
    ImageView onekilometersDone;                            //1公里选择
    LinearLayout twokilometers;                             //2公里
    ImageView twokilometersDone;                            //2公里选择
    LinearLayout threekilometers;                           //3公里
    ImageView threekilometersDone;                          //3公里选择

    LinearLayout Fiveminutes;                               //5分钟
    ImageView FiveminutesDone;                              //5分钟选择
    LinearLayout tenminutes;                                //10分钟
    ImageView tenminutesDone;                               //10分钟选择
    LinearLayout Twentyminutes;                             //20分钟
    ImageView TwentyminutesDone;                            //20分钟选择
    LinearLayout Thirtyminutes;                             //30分钟
    ImageView ThirtyminutesDone;                            //30分钟选择

    private int maleVoiceOrfemaleVoice;                     //男声或者女声
    private int frequency;                                  //播报频率

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_of_voice_announcements);

        initView();
        initInfo();
        initComponent();
    }

    private void initView(){
        title=(TextView)findViewById(R.id.title);
        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(this);

        title.setText("语音播报");

        openVoiceAnnouncementsSwitch=(ImageView)findViewById(R.id.openVoiceAnnouncementsSwitch);
        openVoiceAnnouncementsSwitch.setOnClickListener(this);

        maleVoice=(LinearLayout)findViewById(R.id.maleVoice);
        maleVoice.setOnClickListener(this);
        femaleVoice=(LinearLayout)findViewById(R.id.femaleVoice);
        femaleVoice.setOnClickListener(this);
        maleVoiceDone=(ImageView)findViewById(R.id.maleVoiceDone);
        femaleVoiceDone=(ImageView)findViewById(R.id.femaleVoiceDone);

        halfkilometers=(LinearLayout)findViewById(R.id.halfkilometers);
        halfkilometersDone=(ImageView)findViewById(R.id.halfkilometersDone);
        halfkilometers.setOnClickListener(this);

        onekilometers=(LinearLayout)findViewById(R.id.onekilometers);
        onekilometersDone=(ImageView)findViewById(R.id.onekilometersDone);
        onekilometers.setOnClickListener(this);

        twokilometers=(LinearLayout)findViewById(R.id.twokilometers);
        twokilometersDone=(ImageView)findViewById(R.id.twokilometersDone);
        twokilometers.setOnClickListener(this);

        threekilometers=(LinearLayout)findViewById(R.id.threekilometers);
        threekilometersDone=(ImageView)findViewById(R.id.threekilometersDone);
        threekilometers.setOnClickListener(this);

        Fiveminutes=(LinearLayout)findViewById(R.id.Fiveminutes);
        FiveminutesDone=(ImageView)findViewById(R.id.FiveminutesDone);
        Fiveminutes.setOnClickListener(this);

        tenminutes=(LinearLayout)findViewById(R.id.tenminutes);
        tenminutesDone=(ImageView)findViewById(R.id.tenminutesDone);
        tenminutes.setOnClickListener(this);

        Twentyminutes=(LinearLayout)findViewById(R.id.Twentyminutes);
        TwentyminutesDone=(ImageView)findViewById(R.id.TwentyminutesDone);
        Twentyminutes.setOnClickListener(this);

        Thirtyminutes=(LinearLayout)findViewById(R.id.Thirtyminutes);
        ThirtyminutesDone=(ImageView)findViewById(R.id.ThirtyminutesDone);
        Thirtyminutes.setOnClickListener(this);

    }

    //初始化信息
    private void initInfo(){
        openVoiceAnnouncements=DbHelper.getOpenVoiceAnnouncements();
        maleVoiceOrfemaleVoice=DbHelper.getmaleVoiceOrfemaleVoice();
        frequency=DbHelper.getfrequency();
    }

    //初始化组件
    private void initComponent(){
        if(openVoiceAnnouncements){
            //true 表示打开
            openVoiceAnnouncementsSwitch.setImageDrawable(getResources().getDrawable(R.mipmap.ic_switch_on));
        }else {
            openVoiceAnnouncementsSwitch.setImageDrawable(getResources().getDrawable(R.mipmap.ic_switch_off));
        }
        if(maleVoiceOrfemaleVoice==1){
            maleVoiceDone.setVisibility(View.VISIBLE);
            femaleVoiceDone.setVisibility(View.GONE);
        }else if(maleVoiceOrfemaleVoice==2){
            maleVoiceDone.setVisibility(View.GONE);
            femaleVoiceDone.setVisibility(View.VISIBLE);
        }
        if(frequency==1){
            halfkilometersDone.setVisibility(View.VISIBLE);
            onekilometersDone.setVisibility(View.GONE);
            twokilometersDone.setVisibility(View.GONE);
            threekilometersDone.setVisibility(View.GONE);
            FiveminutesDone.setVisibility(View.GONE);
            tenminutesDone.setVisibility(View.GONE);
            TwentyminutesDone.setVisibility(View.GONE);
            ThirtyminutesDone.setVisibility(View.GONE);
        }else if(frequency==2){
            halfkilometersDone.setVisibility(View.GONE);
            onekilometersDone.setVisibility(View.VISIBLE);
            twokilometersDone.setVisibility(View.GONE);
            threekilometersDone.setVisibility(View.GONE);
            FiveminutesDone.setVisibility(View.GONE);
            tenminutesDone.setVisibility(View.GONE);
            TwentyminutesDone.setVisibility(View.GONE);
            ThirtyminutesDone.setVisibility(View.GONE);
        }else if(frequency==3){
            halfkilometersDone.setVisibility(View.GONE);
            onekilometersDone.setVisibility(View.GONE);
            twokilometersDone.setVisibility(View.VISIBLE);
            threekilometersDone.setVisibility(View.GONE);
            FiveminutesDone.setVisibility(View.GONE);
            tenminutesDone.setVisibility(View.GONE);
            TwentyminutesDone.setVisibility(View.GONE);
            ThirtyminutesDone.setVisibility(View.GONE);
        }else if(frequency==4){
            halfkilometersDone.setVisibility(View.GONE);
            onekilometersDone.setVisibility(View.GONE);
            twokilometersDone.setVisibility(View.GONE);
            threekilometersDone.setVisibility(View.VISIBLE);
            FiveminutesDone.setVisibility(View.GONE);
            tenminutesDone.setVisibility(View.GONE);
            TwentyminutesDone.setVisibility(View.GONE);
            ThirtyminutesDone.setVisibility(View.GONE);
        }else if(frequency==5){
            halfkilometersDone.setVisibility(View.GONE);
            onekilometersDone.setVisibility(View.GONE);
            twokilometersDone.setVisibility(View.GONE);
            threekilometersDone.setVisibility(View.GONE);
            FiveminutesDone.setVisibility(View.VISIBLE);
            tenminutesDone.setVisibility(View.GONE);
            TwentyminutesDone.setVisibility(View.GONE);
            ThirtyminutesDone.setVisibility(View.GONE);
        }else if(frequency==6){
            halfkilometersDone.setVisibility(View.GONE);
            onekilometersDone.setVisibility(View.GONE);
            twokilometersDone.setVisibility(View.GONE);
            threekilometersDone.setVisibility(View.GONE);
            FiveminutesDone.setVisibility(View.GONE);
            tenminutesDone.setVisibility(View.VISIBLE);
            TwentyminutesDone.setVisibility(View.GONE);
            ThirtyminutesDone.setVisibility(View.GONE);
        }else if(frequency==7){
            halfkilometersDone.setVisibility(View.GONE);
            onekilometersDone.setVisibility(View.GONE);
            twokilometersDone.setVisibility(View.GONE);
            threekilometersDone.setVisibility(View.GONE);
            FiveminutesDone.setVisibility(View.GONE);
            tenminutesDone.setVisibility(View.GONE);
            TwentyminutesDone.setVisibility(View.VISIBLE);
            ThirtyminutesDone.setVisibility(View.GONE);
        }else if(frequency==8){
            halfkilometersDone.setVisibility(View.GONE);
            onekilometersDone.setVisibility(View.GONE);
            twokilometersDone.setVisibility(View.GONE);
            threekilometersDone.setVisibility(View.GONE);
            FiveminutesDone.setVisibility(View.GONE);
            tenminutesDone.setVisibility(View.GONE);
            TwentyminutesDone.setVisibility(View.GONE);
            ThirtyminutesDone.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                SettingRecord settingRecord=new SettingRecord();
                settingRecord.setOpenVoiceAnnouncements(openVoiceAnnouncements);
                settingRecord.setMaleVoiceOrfemaleVoice(maleVoiceOrfemaleVoice);
                settingRecord.setFrequency(frequency);
                DbHelper.saveSettingRecord(settingRecord);
                Log.i(TAG, "存储成功");
                finish();
                break;
            case R.id.openVoiceAnnouncementsSwitch:
                if(openVoiceAnnouncements){
                    //true 表示打开
                    openVoiceAnnouncementsSwitch.setImageDrawable(getResources().getDrawable(R.mipmap.ic_switch_off));
                    openVoiceAnnouncements=!openVoiceAnnouncements;
                }else {
                    //false 表示没有打开
                    openVoiceAnnouncementsSwitch.setImageDrawable(getResources().getDrawable(R.mipmap.ic_switch_on));
                    openVoiceAnnouncements=!openVoiceAnnouncements;
                }
                break;
            case R.id.maleVoice:
                maleVoiceOrfemaleVoice=1;
                maleVoiceDone.setVisibility(View.VISIBLE);
                femaleVoiceDone.setVisibility(View.GONE);
                break;
            case R.id.femaleVoice:
                maleVoiceOrfemaleVoice=2;
                maleVoiceDone.setVisibility(View.GONE);
                femaleVoiceDone.setVisibility(View.VISIBLE);
                break;
            case R.id.halfkilometers:
                frequency=1;
                halfkilometersDone.setVisibility(View.VISIBLE);
                onekilometersDone.setVisibility(View.GONE);
                twokilometersDone.setVisibility(View.GONE);
                threekilometersDone.setVisibility(View.GONE);
                FiveminutesDone.setVisibility(View.GONE);
                tenminutesDone.setVisibility(View.GONE);
                TwentyminutesDone.setVisibility(View.GONE);
                ThirtyminutesDone.setVisibility(View.GONE);
                break;
            case R.id.onekilometers:
                frequency=2;
                halfkilometersDone.setVisibility(View.GONE);
                onekilometersDone.setVisibility(View.VISIBLE);
                twokilometersDone.setVisibility(View.GONE);
                threekilometersDone.setVisibility(View.GONE);
                FiveminutesDone.setVisibility(View.GONE);
                tenminutesDone.setVisibility(View.GONE);
                TwentyminutesDone.setVisibility(View.GONE);
                ThirtyminutesDone.setVisibility(View.GONE);
                break;
            case R.id.twokilometers:
                frequency=3;
                halfkilometersDone.setVisibility(View.GONE);
                onekilometersDone.setVisibility(View.GONE);
                twokilometersDone.setVisibility(View.VISIBLE);
                threekilometersDone.setVisibility(View.GONE);
                FiveminutesDone.setVisibility(View.GONE);
                tenminutesDone.setVisibility(View.GONE);
                TwentyminutesDone.setVisibility(View.GONE);
                ThirtyminutesDone.setVisibility(View.GONE);
                break;
            case R.id.threekilometers:
                frequency=4;
                halfkilometersDone.setVisibility(View.GONE);
                onekilometersDone.setVisibility(View.GONE);
                twokilometersDone.setVisibility(View.GONE);
                threekilometersDone.setVisibility(View.VISIBLE);
                FiveminutesDone.setVisibility(View.GONE);
                tenminutesDone.setVisibility(View.GONE);
                TwentyminutesDone.setVisibility(View.GONE);
                ThirtyminutesDone.setVisibility(View.GONE);
                break;
            case R.id.Fiveminutes:
                frequency=5;
                halfkilometersDone.setVisibility(View.GONE);
                onekilometersDone.setVisibility(View.GONE);
                twokilometersDone.setVisibility(View.GONE);
                threekilometersDone.setVisibility(View.GONE);
                FiveminutesDone.setVisibility(View.VISIBLE);
                tenminutesDone.setVisibility(View.GONE);
                TwentyminutesDone.setVisibility(View.GONE);
                ThirtyminutesDone.setVisibility(View.GONE);
                break;
            case R.id.tenminutes:
                frequency=6;
                halfkilometersDone.setVisibility(View.GONE);
                onekilometersDone.setVisibility(View.GONE);
                twokilometersDone.setVisibility(View.GONE);
                threekilometersDone.setVisibility(View.GONE);
                FiveminutesDone.setVisibility(View.GONE);
                tenminutesDone.setVisibility(View.VISIBLE);
                TwentyminutesDone.setVisibility(View.GONE);
                ThirtyminutesDone.setVisibility(View.GONE);
                break;
            case R.id.Twentyminutes:
                frequency=7;
                halfkilometersDone.setVisibility(View.GONE);
                onekilometersDone.setVisibility(View.GONE);
                twokilometersDone.setVisibility(View.GONE);
                threekilometersDone.setVisibility(View.GONE);
                FiveminutesDone.setVisibility(View.GONE);
                tenminutesDone.setVisibility(View.GONE);
                TwentyminutesDone.setVisibility(View.VISIBLE);
                ThirtyminutesDone.setVisibility(View.GONE);
                break;
            case R.id.Thirtyminutes:
                frequency=8;
                halfkilometersDone.setVisibility(View.GONE);
                onekilometersDone.setVisibility(View.GONE);
                twokilometersDone.setVisibility(View.GONE);
                threekilometersDone.setVisibility(View.GONE);
                FiveminutesDone.setVisibility(View.GONE);
                tenminutesDone.setVisibility(View.GONE);
                TwentyminutesDone.setVisibility(View.GONE);
                ThirtyminutesDone.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            SettingRecord settingRecord=new SettingRecord();
            settingRecord.setOpenVoiceAnnouncements(openVoiceAnnouncements);
            settingRecord.setMaleVoiceOrfemaleVoice(maleVoiceOrfemaleVoice);
            settingRecord.setFrequency(frequency);
            DbHelper.saveSettingRecord(settingRecord);
            Log.i(TAG, "存储成功");
            finish();
        }
        //返回值，该方法的返回值为一个boolean类型的变量，当返回true时，表示已经完整地处理了这个事件，并不希望其他的回调方法再次进行处理，
        // 而当返回false时，表示并没有完全处理完该事件，更希望其他回调方法继续对其进行处理，例如Activity中的回调方法。
        return true;
    }
}
