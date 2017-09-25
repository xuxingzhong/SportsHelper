package com.example.root.sportshelper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.example.root.sportshelper.database.SettingRecord;
import com.example.root.sportshelper.fragment.RunningFragment;
import com.example.root.sportshelper.utils.Constant;
import com.example.root.sportshelper.utils.DbHelper;
import com.example.root.sportshelper.utils.MyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Setting extends AppCompatActivity implements View.OnClickListener{
    TextView title;
    ImageView back;
    TextView person_info;                   //个人信息
    LinearLayout sport_target;              //运动目标
    TextView RTStepTarget;
    ImageView SedentaryReminderSwitch;      //就坐提醒开关
    private Boolean SedentaryReminder;      //就坐提醒是否打开
    private Boolean lastSedentaryReminder;  //判断久坐提醒是否变化

    LinearLayout startTime;                 //开始时间
    TextView startTimeText;                 //开始时间显示
    LinearLayout endTime;                   //结束时间
    TextView endTimeText;                   //结束时间显示

    ImageView unDisturbSwitch;              //午休免打扰
    private Boolean unDisturb;              //午休免打扰是否打开

    ImageView AutoPauseSwitch;                    //自动暂停
    private Boolean AutoPause;                   //自动暂停是否打开

    ImageView screenOnSwitch;                     //运动中屏幕常亮
    private Boolean screenOn;                         //运动中屏幕常亮是否打开

    LinearLayout VoiceAnnouncements;                //语音播报
    TextView VoiceAnnouncementsSwitch;              //语音播报是否打开
    private Boolean openVoiceAnnouncements;         //语音播报是否打开显示

    LinearLayout RunningTarget;                     //跑步目标
    TextView RunningTargetText;                     //跑步目标显示

    private int oldSportTarget;                     //老的运动目标
    private int newSportTarget;                     //新的运动目标
    private Boolean isChangeStepTarget;                 //运动目标是否改变
    private String StartTime;                           //开始时间显示初始化
    private String EndTime;                             //结束时间显示初始化
    private int runningTarget;                          //跑步目标

    private Calendar startCalendar;                     //开始时间
    private Calendar endCalendar;                     //结束时间

    private String TAG="Setting";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        initView();
        initInfo();
        initComponent();
    }

    private void initView(){
        title=(TextView)findViewById(R.id.title);
        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(this);

        title.setText("设置");
        person_info=(TextView)findViewById(R.id.person_info);
        person_info.setOnClickListener(this);
        sport_target=(LinearLayout)findViewById(R.id.sport_target);
        sport_target.setOnClickListener(this);

        RTStepTarget=(TextView)findViewById(R.id.RTStepTarget);
        SedentaryReminderSwitch=(ImageView)findViewById(R.id.SedentaryReminderSwitch);
        SedentaryReminderSwitch.setOnClickListener(this);

        startTime=(LinearLayout)findViewById(R.id.startTime);
        startTime.setOnClickListener(this);
        startTimeText=(TextView)findViewById(R.id.startTimeText);
        endTime=(LinearLayout)findViewById(R.id.endTime);
        endTime.setOnClickListener(this);
        endTimeText=(TextView)findViewById(R.id.endTimeText);

        unDisturbSwitch=(ImageView)findViewById(R.id.unDisturbSwitch);
        unDisturbSwitch.setOnClickListener(this);

        AutoPauseSwitch=(ImageView)findViewById(R.id.AutoPauseSwitch);
        AutoPauseSwitch.setOnClickListener(this);

        screenOnSwitch=(ImageView)findViewById(R.id.screenOnSwitch);
        screenOnSwitch.setOnClickListener(this);

        VoiceAnnouncements=(LinearLayout)findViewById(R.id.VoiceAnnouncements);
        VoiceAnnouncements.setOnClickListener(this);
        VoiceAnnouncementsSwitch=(TextView)findViewById(R.id.VoiceAnnouncementsSwitch);

        RunningTarget=(LinearLayout)findViewById(R.id.RunningTarget);
        RunningTarget.setOnClickListener(this);
        RunningTargetText=(TextView)findViewById(R.id.RunningTargetText);
    }

    //初始化信息
    private void initInfo(){
        oldSportTarget=DbHelper.getRTStepTarget();
        isChangeStepTarget=false;
        SedentaryReminder=DbHelper.getOpenSedentaryReminder();
        lastSedentaryReminder=SedentaryReminder;
        StartTime=DbHelper.getStartTime();
        EndTime=DbHelper.getEndTime();
        unDisturb=DbHelper.getOpenUnDisturb();
        AutoPause=DbHelper.getOpenAutoPause();
        screenOn=DbHelper.getOpenScreenOn();
        openVoiceAnnouncements=DbHelper.getOpenVoiceAnnouncements();
        try {
            startCalendar=MyTime.changStringToCalendar(StartTime);
            endCalendar=MyTime.changStringToCalendar(EndTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    //初始化组件
    private void initComponent(){
        if(SedentaryReminder){
            //表示打开
            SedentaryReminderSwitch.setImageDrawable(getResources().getDrawable(R.mipmap.ic_switch_on));
        }else {
            SedentaryReminderSwitch.setImageDrawable(getResources().getDrawable(R.mipmap.ic_switch_off));
        }
        startTimeText.setText(StartTime);
        endTimeText.setText(EndTime);
        if(unDisturb){
            //免打扰打开
            unDisturbSwitch.setImageDrawable(getResources().getDrawable(R.mipmap.ic_switch_on));
        }else {
            unDisturbSwitch.setImageDrawable(getResources().getDrawable(R.mipmap.ic_switch_off));
        }
        if(AutoPause){
            //自动暂停打开
            AutoPauseSwitch.setImageDrawable(getResources().getDrawable(R.mipmap.ic_switch_on));
        }else {
            AutoPauseSwitch.setImageDrawable(getResources().getDrawable(R.mipmap.ic_switch_off));
        }
        if(screenOn){
            screenOnSwitch.setImageDrawable(getResources().getDrawable(R.mipmap.ic_switch_on));
        }else {
            screenOnSwitch.setImageDrawable(getResources().getDrawable(R.mipmap.ic_switch_off));
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                if(oldSportTarget!=newSportTarget){
                    isChangeStepTarget=true;
                }
                SettingRecord settingRecord=new SettingRecord();
                settingRecord.setChangeStepTarget(isChangeStepTarget);
                settingRecord.setOpenSedentaryReminder(SedentaryReminder);
                settingRecord.setStartTime(StartTime);
                settingRecord.setEndTime(EndTime);
                settingRecord.setOpenUnDisturb(unDisturb);
                settingRecord.setOpenAutoPause(AutoPause);
                settingRecord.setOpenScreenOn(screenOn);
                DbHelper.saveSettingRecord(settingRecord);
                Log.i(TAG, "存储成功");

                if(isChangeStepTarget){
                    Log.i(TAG, "onClick: 发送广播");
                    Intent intent=new Intent(Constant.UPDATESTEPTARGET);
                    sendOrderedBroadcast(intent,null);
                }
                //久坐提醒变化
                Log.i(TAG, "onClick: 发送久坐提醒广播");
                Intent intent=new Intent(Constant.SEDENTARYREMINDER);
                sendOrderedBroadcast(intent,null);
                finish();
                break;
            case R.id.person_info:
                Intent PersonInfo=new Intent(this,SettingOfPersonInfo.class);
                startActivity(PersonInfo);
                break;
            case R.id.sport_target:
                Intent sport_target=new Intent(this,SettingOfSportTarget.class);
                startActivity(sport_target);
                break;
            case R.id.SedentaryReminderSwitch:
                if(SedentaryReminder){
                    //true 表示打开
                    SedentaryReminderSwitch.setImageDrawable(getResources().getDrawable(R.mipmap.ic_switch_off));
                    SedentaryReminder=!SedentaryReminder;
                }else {
                    SedentaryReminderSwitch.setImageDrawable(getResources().getDrawable(R.mipmap.ic_switch_on));
                    SedentaryReminder=!SedentaryReminder;
                }
                break;
            case R.id.startTime:
                if(SedentaryReminder){
                    //时间选择器
                    TimePickerView pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
                        @Override
                        public void onTimeSelect(Date date, View v) {       //选中事件回调
                            StartTime=getTime(date);
                            try {
                                startCalendar=MyTime.changStringToCalendar(StartTime);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            startTimeText.setText(StartTime);
                        }
                    })
                            .setType(new boolean[]{false, false, false, true, true, false})// 默认全部显示
                            .setTitleText("开始时间")//标题文字
                            .setOutSideCancelable(false)//点击屏幕，点在控件外部范围时，是否取消显示
                            .isCyclic(false)//是否循环滚动
                            .setDate(startCalendar)
                            .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                            .build();
                    pvTime.show();
                }
                break;
            case R.id.endTime:
                if(SedentaryReminder){
                    //时间选择器
                    TimePickerView pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
                        @Override
                        public void onTimeSelect(Date date, View v) {       //选中事件回调
                            try {
                                if(MyTime.compareTime(StartTime,getTime(date))){
                                    Log.i(TAG, "结束时间大于起始时间");
                                    EndTime=getTime(date);
                                }else {
                                    Toast.makeText(Setting.this,"结束时间小于起始时间",Toast.LENGTH_SHORT).show();
                                    Log.i(TAG, "结束时间小于起始时间");
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            try {
                                endCalendar=MyTime.changStringToCalendar(EndTime);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            endTimeText.setText(EndTime);
                        }
                    })
                            .setType(new boolean[]{false, false, false, true, true, false})// 默认全部显示
                            .setTitleText("结束时间")//标题文字
                            .setOutSideCancelable(false)//点击屏幕，点在控件外部范围时，是否取消显示
                            .isCyclic(false)//是否循环滚动
                            .setDate(endCalendar)
                            .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                            .build();
                    pvTime.show();
                }
                break;
            case R.id.unDisturbSwitch:
                if(SedentaryReminder){
                    if(unDisturb){
                        //true 表示打开
                        unDisturbSwitch.setImageDrawable(getResources().getDrawable(R.mipmap.ic_switch_off));
                        unDisturb=!unDisturb;
                    }else {
                        //false 表示没有打开
                        unDisturbSwitch.setImageDrawable(getResources().getDrawable(R.mipmap.ic_switch_on));
                        unDisturb=!unDisturb;
                    }
                }
                break;
            case R.id.AutoPauseSwitch:
                if(AutoPause){
                    //true 表示打开
                    AutoPauseSwitch.setImageDrawable(getResources().getDrawable(R.mipmap.ic_switch_off));
                    AutoPause=!AutoPause;
                }else {
                    //false 表示没有打开
                    AutoPauseSwitch.setImageDrawable(getResources().getDrawable(R.mipmap.ic_switch_on));
                    AutoPause=!AutoPause;
                }
                break;
            case R.id.screenOnSwitch:
                if(screenOn){
                    //true 表示打开
                    screenOnSwitch.setImageDrawable(getResources().getDrawable(R.mipmap.ic_switch_off));
                    screenOn=!screenOn;
                }else {
                    //false 表示没有打开
                    screenOnSwitch.setImageDrawable(getResources().getDrawable(R.mipmap.ic_switch_on));
                    screenOn=!screenOn;
                }
                break;
            case R.id.VoiceAnnouncements:
                Intent VoiceAnnouncements=new Intent(this,SettingOfVoiceAnnouncements.class);
                startActivity(VoiceAnnouncements);
                break;
            case R.id.RunningTarget:
                Intent RunningTarget=new Intent(this,SettingOfRunningTarget.class);
                startActivity(RunningTarget);
                break;
        }
    }

    //activity在栈顶时会调用,刷新界面
    @Override
    protected void onResume() {
        super.onResume();
        RTStepTarget.setText(DbHelper.getRTStepTarget()+"步");
        newSportTarget=DbHelper.getRTStepTarget();

        openVoiceAnnouncements=DbHelper.getOpenVoiceAnnouncements();
        if(openVoiceAnnouncements){
            VoiceAnnouncementsSwitch.setText("开启");
        }else {
            VoiceAnnouncementsSwitch.setText("关闭");
        }
        runningTarget=DbHelper.getrunningTarget();
        if(runningTarget==1){
            RunningTargetText.setText("不设目标,自由跑步");
        }else if(runningTarget==2){
            RunningTargetText.setText(DbHelper.getdistanceTarget()+"公里");
        }else if(runningTarget==3){
            try {
                RunningTargetText.setText(MyTime.getTimeConvert(DbHelper.gettimeTarget()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(date);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(oldSportTarget!=newSportTarget){
                isChangeStepTarget=true;
            }
            SettingRecord settingRecord=new SettingRecord();
            settingRecord.setChangeStepTarget(isChangeStepTarget);
            settingRecord.setOpenSedentaryReminder(SedentaryReminder);
            settingRecord.setStartTime(StartTime);
            settingRecord.setEndTime(EndTime);
            settingRecord.setOpenUnDisturb(unDisturb);
            settingRecord.setOpenAutoPause(AutoPause);
            settingRecord.setOpenScreenOn(screenOn);
            DbHelper.saveSettingRecord(settingRecord);
            Log.i(TAG, "存储成功");

            if(isChangeStepTarget){
                Log.i(TAG, "onClick: 发送广播,改变运动目标");
                Intent intent=new Intent(Constant.UPDATESTEPTARGET);
                sendOrderedBroadcast(intent,null);
            }
            //久坐提醒变化
            Log.i(TAG, "onClick: 发送久坐提醒广播");
            Intent intent=new Intent(Constant.SEDENTARYREMINDER);
            sendOrderedBroadcast(intent,null);
            finish();
        }
        //返回值，该方法的返回值为一个boolean类型的变量，当返回true时，表示已经完整地处理了这个事件，并不希望其他的回调方法再次进行处理，
        // 而当返回false时，表示并没有完全处理完该事件，更希望其他回调方法继续对其进行处理，例如Activity中的回调方法。
        return true;
    }
}
