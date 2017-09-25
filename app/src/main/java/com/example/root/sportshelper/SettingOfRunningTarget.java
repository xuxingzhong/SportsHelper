package com.example.root.sportshelper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.example.root.sportshelper.database.SettingRecord;
import com.example.root.sportshelper.utils.DbHelper;
import com.example.root.sportshelper.utils.MyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SettingOfRunningTarget extends AppCompatActivity implements View.OnClickListener{
    private String TAG="SettingOfRunningTarget";
    TextView title;
    ImageView back;

    LinearLayout noTarget;              //自由跑步
    ImageView noTargetDone;             //自由跑步选择
    LinearLayout TargetOfDisdance;      //距离目标
    ImageView TargetOfDisdanceDone;     //距离目标选择
    LinearLayout TargetOfTime;          //时长目标
    ImageView TargetOfTimeDone;         //时长目标选择
    TextView Disdance;                  //距离选择
    TextView Time;                      //时长选择

    private int runningTarget;              //跑步目标
    private String distanceTarget;          //距离目标
    private String timeTarget;              //时间目标
    private Calendar myTimeTarget;


    private static final List<String> optionsItems = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_of_running_target);

        initView();
        initInfo();
        initComponent();
    }

    private void initView(){
        title=(TextView)findViewById(R.id.title);
        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(this);

        title.setText("跑步目标");

        noTarget=(LinearLayout)findViewById(R.id.noTarget);
        noTargetDone=(ImageView)findViewById(R.id.noTargetDone);
        noTarget.setOnClickListener(this);

        TargetOfDisdance=(LinearLayout)findViewById(R.id.TargetOfDisdance);
        TargetOfDisdanceDone=(ImageView)findViewById(R.id.TargetOfDisdanceDone);
        TargetOfDisdance.setOnClickListener(this);

        TargetOfTime=(LinearLayout)findViewById(R.id.TargetOfTime);
        TargetOfTimeDone=(ImageView)findViewById(R.id.TargetOfTimeDone);
        TargetOfTime.setOnClickListener(this);

        Disdance=(TextView)findViewById(R.id.Disdance);
        Disdance.setOnClickListener(this);
        Time=(TextView)findViewById(R.id.Time);
        Time.setOnClickListener(this);
    }

    //初始化信息
    private void initInfo(){
        runningTarget=DbHelper.getrunningTarget();
        distanceTarget=DbHelper.getdistanceTarget();
        timeTarget=DbHelper.gettimeTarget();
        try {
            myTimeTarget=MyTime.changStringToCalendar(timeTarget);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        optionsItems.clear();
        optionsItems.add("0.5");
        optionsItems.add("1.0");
        optionsItems.add("1.5");
        optionsItems.add("2.0");
        optionsItems.add("2.5");
        optionsItems.add("3.0");
        optionsItems.add("3.5");
        optionsItems.add("4.0");
        optionsItems.add("4.5");
        optionsItems.add("5.0");
        optionsItems.add("5.5");
        optionsItems.add("6.0");
        optionsItems.add("6.5");
        optionsItems.add("7.0");
        optionsItems.add("7.5");
        optionsItems.add("8.0");
        optionsItems.add("8.5");
        optionsItems.add("9.0");
        optionsItems.add("9.5");
        optionsItems.add("10.0");
    }

    //初始化组件
    private void initComponent(){
        if(runningTarget==1){
            noTargetDone.setVisibility(View.VISIBLE);
            TargetOfDisdanceDone.setVisibility(View.GONE);
            TargetOfTimeDone.setVisibility(View.GONE);
            Disdance.setTextColor(getResources().getColor(R.color.color_808080));
            Time.setTextColor(getResources().getColor(R.color.color_808080));
        }else if(runningTarget==2){
            noTargetDone.setVisibility(View.GONE);
            TargetOfDisdanceDone.setVisibility(View.VISIBLE);
            TargetOfTimeDone.setVisibility(View.GONE);
            Disdance.setTextColor(getResources().getColor(R.color.color_ff191919));
            Time.setTextColor(getResources().getColor(R.color.color_808080));
        }else if(runningTarget==3){
            noTargetDone.setVisibility(View.GONE);
            TargetOfDisdanceDone.setVisibility(View.GONE);
            TargetOfTimeDone.setVisibility(View.VISIBLE);
            Disdance.setTextColor(getResources().getColor(R.color.color_808080));
            Time.setTextColor(getResources().getColor(R.color.color_ff191919));
        }
        Disdance.setText(distanceTarget+"公里");
        try {
            Time.setText(MyTime.getTimeConvert(timeTarget));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                SettingRecord settingRecord=new SettingRecord();
                settingRecord.setRunningTarget(runningTarget);
                settingRecord.setDistanceTarget(distanceTarget);
                settingRecord.setTimeTarget(timeTarget);
                DbHelper.saveSettingRecord(settingRecord);
                Log.i(TAG, "存储成功");
                finish();
                break;
            case R.id.noTarget:
                noTargetDone.setVisibility(View.VISIBLE);
                TargetOfDisdanceDone.setVisibility(View.GONE);
                TargetOfTimeDone.setVisibility(View.GONE);
                Disdance.setTextColor(getResources().getColor(R.color.color_808080));
                Time.setTextColor(getResources().getColor(R.color.color_808080));
                runningTarget=1;
                break;
            case R.id.TargetOfDisdance:
                noTargetDone.setVisibility(View.GONE);
                TargetOfDisdanceDone.setVisibility(View.VISIBLE);
                TargetOfTimeDone.setVisibility(View.GONE);
                Disdance.setTextColor(getResources().getColor(R.color.color_ff191919));
                Time.setTextColor(getResources().getColor(R.color.color_808080));
                runningTarget=2;
                break;
            case R.id.TargetOfTime:
                noTargetDone.setVisibility(View.GONE);
                TargetOfDisdanceDone.setVisibility(View.GONE);
                TargetOfTimeDone.setVisibility(View.VISIBLE);
                Disdance.setTextColor(getResources().getColor(R.color.color_808080));
                Time.setTextColor(getResources().getColor(R.color.color_ff191919));
                runningTarget=3;
                break;
            case R.id.Disdance:
                if(runningTarget==2){
                    OptionsPickerView pvOptions=new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
                        @Override
                        public void onOptionsSelect(int options1, int options2, int options3, View v) {
                            distanceTarget=optionsItems.get(options1);
                            Disdance.setText(distanceTarget+"公里");
                        }
                    })
                            .setTitleText("设定距离目标(公里)")
                            .setOutSideCancelable(false)//点击屏幕，点在控件外部范围时，是否取消显示
                            .setSelectOptions(10)
                            .build();
                    pvOptions.setPicker(optionsItems);
                    pvOptions.show();
                }
                break;
            case R.id.Time:
                if(runningTarget==3){
                    //时间选择器
                    TimePickerView pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
                        @Override
                        public void onTimeSelect(Date date, View v) {       //选中事件回调
                            timeTarget=getTimeOther(date);
                            try {
                                myTimeTarget=MyTime.changStringToCalendar(timeTarget);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Time.setText(getTime(date));
                        }
                    })
                            .setType(new boolean[]{false, false, false, true, true, false})// 默认全部显示
                            .setTitleText("结束时间")//标题文字
                            .setOutSideCancelable(false)//点击屏幕，点在控件外部范围时，是否取消显示
                            .isCyclic(false)//是否循环滚动
                            .setDate(myTimeTarget)
                            .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                            .build();
                    pvTime.show();
                }
                break;

        }
    }


    public String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat hourSdf=new SimpleDateFormat("HH");
        String flag=hourSdf.format(date);
        if(Integer.valueOf(flag)==0){
            SimpleDateFormat MinuteSdf=new SimpleDateFormat("mm分钟");
            return MinuteSdf.format(date);
        }else {
            SimpleDateFormat hourAndMinuteSdf=new SimpleDateFormat("H时mm分钟");
            return hourAndMinuteSdf.format(date);
        }
    }

    public String getTimeOther(Date date) {
        SimpleDateFormat Sdf=new SimpleDateFormat("HH:mm");
        return Sdf.format(date);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            SettingRecord settingRecord=new SettingRecord();
            settingRecord.setRunningTarget(runningTarget);
            settingRecord.setDistanceTarget(distanceTarget);
            settingRecord.setTimeTarget(timeTarget);
            DbHelper.saveSettingRecord(settingRecord);
            Log.i(TAG, "存储成功");
            finish();
        }
        //返回值，该方法的返回值为一个boolean类型的变量，当返回true时，表示已经完整地处理了这个事件，并不希望其他的回调方法再次进行处理，
        // 而当返回false时，表示并没有完全处理完该事件，更希望其他回调方法继续对其进行处理，例如Activity中的回调方法。
        return true;
    }
}
