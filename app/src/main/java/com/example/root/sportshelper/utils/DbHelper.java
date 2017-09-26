package com.example.root.sportshelper.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.root.sportshelper.R;
import com.example.root.sportshelper.database.BadgeRecord;
import com.example.root.sportshelper.database.PersonInfo;
import com.example.root.sportshelper.database.SettingRecord;
import com.example.root.sportshelper.database.SportsRecord;
import com.example.root.sportshelper.database.TypeAndStepCount;

import org.litepal.crud.DataSupport;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 17-8-17.
 */

public class DbHelper {
    private static final String TAG="DbHelper";

    //根据日期对计步记录进行存储
    public static void saveByDateToStepCount(SportsRecord stepCountRecord) throws ParseException {
        List<SportsRecord> sportsRecordList=queryByDateToStepCount(stepCountRecord.getDate());
        if(sportsRecordList.size()==0||sportsRecordList.isEmpty()){
            stepCountRecord.save();
        }else if(sportsRecordList.size()==1){
            //时间之和为当前持续时间+已有的持续时间
            if(sportsRecordList.get(0).getPersistTime()!=null&&stepCountRecord.getPersistTime()!=null){
                String timeSum=MyTime.getTimeSum(stepCountRecord.getPersistTime(),sportsRecordList.get(0).getPersistTime());
                stepCountRecord.setPersistTime(timeSum);
            }
            stepCountRecord.update(sportsRecordList.get(0).getId());
        }
    }

    //根据日期对计步记录进行查询
    public static List<SportsRecord> queryByDateToStepCount(String date){
         List<SportsRecord> sportsRecordList= DataSupport.where("date = ?",date).find(SportsRecord.class);
         return sportsRecordList;
    }

    //获得目标id
    public static int getbodyId(){
        PersonInfo firstPersonInfo= DataSupport.findFirst(PersonInfo.class);
        return firstPersonInfo.getId();
    }

    //获得目标步数
    public static int getRTStepTarget(){
        PersonInfo firstPersonInfo= DataSupport.findFirst(PersonInfo.class);
        return firstPersonInfo.getRTStepTarget();
    }

    //获得人物身高
    public static int getbodyHeight(){
        PersonInfo firstPersonInfo= DataSupport.findFirst(PersonInfo.class);
        return firstPersonInfo.getBodyHeight();
    }

    //获得人物体重
    public static int getbodyWeight(){
        PersonInfo firstPersonInfo= DataSupport.findFirst(PersonInfo.class);
        return firstPersonInfo.getBodyWeight();
    }

    //获得人物性别
    public static int getbodySex(){
        PersonInfo firstPersonInfo= DataSupport.findFirst(PersonInfo.class);
        return firstPersonInfo.getSex();
    }

    //获得人物年龄
    public static int getbodyAge(){
        PersonInfo firstPersonInfo= DataSupport.findFirst(PersonInfo.class);
        return firstPersonInfo.getBodyAge();
    }

    //根据id对计步记录进行查询
    public static SportsRecord queryById(int id){
        SportsRecord mySportRecord=DataSupport.find(SportsRecord.class,id);
        return mySportRecord;
    }

    //获得周的数据
    public static List<TypeAndStepCount> getWeekStepCount(List<SportsRecord> mySportsRecordList) throws ParseException {
        List<TypeAndStepCount> typeAndStepCountList=new ArrayList<>();
        int count=1;
        int i=1;
        SportsRecord newSportsRecord=null;
        for(SportsRecord sportsRecord:mySportsRecordList){
            if(MyTime.isNewWeek(sportsRecord.getDate())){
                //是新周
                if(newSportsRecord !=null){
                    newSportsRecord.setPersistTime(MyTime.getOccupyHour(newSportsRecord.getPersistTime(),-1));

                    DecimalFormat df = new DecimalFormat("0.0");//格式化小数，不足的补0
                    String mileage= df.format(newSportsRecord.getMileage());
                    newSportsRecord.setMileage(Float.valueOf(mileage));

                    TypeAndStepCount typeAndStepCount=new TypeAndStepCount(Constant.WEEK,false,newSportsRecord);
                    typeAndStepCountList.add(typeAndStepCount);
                    count=1;
                }
                newSportsRecord=new SportsRecord();
                newSportsRecord.setId(sportsRecord.getId());
                newSportsRecord.setDate(sportsRecord.getDate());
                newSportsRecord.setCalorie(sportsRecord.getCalorie());
                newSportsRecord.setMileage(sportsRecord.getMileage());
                newSportsRecord.setRealStep(sportsRecord.getRealStep());
                newSportsRecord.setTargetStep(sportsRecord.getTargetStep());

                if(sportsRecord.getPersistTime()==null){
                    newSportsRecord.setPersistTime("0:0:0");
                }else {
                    newSportsRecord.setPersistTime(sportsRecord.getPersistTime());
                }
                newSportsRecord.setEndTime(sportsRecord.getEndTime());
                newSportsRecord.setStartTime(sportsRecord.getStartTime());
                newSportsRecord.setPreviousStep(sportsRecord.getPreviousStep());

                if(MyTime.isThisWeek(sportsRecord.getDate())){
                    newSportsRecord.setDate("本周");
                }else if(MyTime.isLastWeek(sportsRecord.getDate())){
                    newSportsRecord.setDate("上周");
                }else {
                    String startWeek=MyTime.changeFormatOther(MyTime.getBeginDayOfWeek(sportsRecord.getDate()));
                    String endWeek=MyTime.changeFormatOther(MyTime.getEndDayOfWeek(sportsRecord.getDate()));
                    newSportsRecord.setDate(startWeek+"-"+endWeek);
                }
                if(i==mySportsRecordList.size()){
                    newSportsRecord.setPersistTime(MyTime.getOccupyHour(newSportsRecord.getPersistTime(),-1));

                    DecimalFormat df = new DecimalFormat("0.0");//格式化小数，不足的补0
                    String mileage= df.format(newSportsRecord.getMileage());
                    newSportsRecord.setMileage(Float.valueOf(mileage));

                    TypeAndStepCount typeAndStepCount=new TypeAndStepCount(Constant.WEEK,false,newSportsRecord);
                    typeAndStepCountList.add(typeAndStepCount);
                    return typeAndStepCountList;
                }
            }else {
                newSportsRecord.setRealStep(newSportsRecord.getRealStep()+sportsRecord.getRealStep());
                newSportsRecord.setCalorie(newSportsRecord.getCalorie()+sportsRecord.getCalorie());
                newSportsRecord.setMileage(newSportsRecord.getMileage()+sportsRecord.getMileage());
                if(sportsRecord.getPersistTime()==null){
                    sportsRecord.setPersistTime("0:0:0");
                }else {
                    newSportsRecord.setPersistTime(MyTime.getTimeSum(newSportsRecord.getPersistTime(),sportsRecord.getPersistTime()));
                }
                if(sportsRecord.getRealStep()!=0){
                    count++;
                }
                if(i==mySportsRecordList.size()){
                    newSportsRecord.setPersistTime(MyTime.getOccupyHour(newSportsRecord.getPersistTime(),-1));

                    DecimalFormat df = new DecimalFormat("0.0");//格式化小数，不足的补0
                    String mileage= df.format(newSportsRecord.getMileage());
                    newSportsRecord.setMileage(Float.valueOf(mileage));

                    TypeAndStepCount typeAndStepCount=new TypeAndStepCount(Constant.WEEK,false,newSportsRecord);
                    typeAndStepCountList.add(typeAndStepCount);
                    return typeAndStepCountList;
                }
            }
            i++;
        }
        Log.i(TAG, "typeAndStepCountList: "+typeAndStepCountList.size());
        return typeAndStepCountList;
    }

    //获得月的数据
    public static List<TypeAndStepCount> getMonthStepCount(List<SportsRecord> mySportsRecordList) throws ParseException {
        List<TypeAndStepCount> typeAndStepCountList=new ArrayList<>();
        int count=1;
        int i=1;
        SportsRecord newSportsRecord=null;
        for(SportsRecord sportsRecord:mySportsRecordList){
            if(MyTime.isNewMonth(sportsRecord.getDate())){
                //是新月
                if(newSportsRecord !=null){
                    newSportsRecord.setPersistTime(MyTime.getOccupyHour(newSportsRecord.getPersistTime(),-1));

                    DecimalFormat df = new DecimalFormat("0.0");//格式化小数，不足的补0
                    String mileage= df.format(newSportsRecord.getMileage());
                    newSportsRecord.setMileage(Float.valueOf(mileage));

                    TypeAndStepCount typeAndStepCount=new TypeAndStepCount(Constant.MONTH,false,newSportsRecord);
                    typeAndStepCountList.add(typeAndStepCount);

                    count=1;
                }
                newSportsRecord=new SportsRecord();
                newSportsRecord.setId(sportsRecord.getId());
                newSportsRecord.setDate(sportsRecord.getDate());
                newSportsRecord.setCalorie(sportsRecord.getCalorie());
                newSportsRecord.setMileage(sportsRecord.getMileage());
                newSportsRecord.setRealStep(sportsRecord.getRealStep());
                newSportsRecord.setTargetStep(sportsRecord.getTargetStep());

                if(sportsRecord.getPersistTime()==null){
                    newSportsRecord.setPersistTime("0:0:0");
                }else {
                    newSportsRecord.setPersistTime(sportsRecord.getPersistTime());
                }
                newSportsRecord.setEndTime(sportsRecord.getEndTime());
                newSportsRecord.setStartTime(sportsRecord.getStartTime());
                newSportsRecord.setPreviousStep(sportsRecord.getPreviousStep());



                if(MyTime.isThisMonth(sportsRecord.getDate())){
                    newSportsRecord.setDate("本月");
                }else if(MyTime.isLastMonth(sportsRecord.getDate())){
                    newSportsRecord.setDate("上月");
                }else {
//                    String startMonth=MyTime.changeFormatOther(MyTime.getBeginDayOfMonth(sportsRecord.getDate()));
//                    String endMonth=MyTime.changeFormatOther(MyTime.getEndDayOfMonth(sportsRecord.getDate()));
//                    newSportsRecord.setDate(startMonth+"~"+endMonth);
                      newSportsRecord.setDate(MyTime.getMonthNumber(sportsRecord.getDate()));
                }
                if(i==mySportsRecordList.size()){
                    newSportsRecord.setPersistTime(MyTime.getOccupyHour(newSportsRecord.getPersistTime(),-1));

                    DecimalFormat df = new DecimalFormat("0.0");//格式化小数，不足的补0
                    String mileage= df.format(newSportsRecord.getMileage());
                    newSportsRecord.setMileage(Float.valueOf(mileage));

                    TypeAndStepCount typeAndStepCount=new TypeAndStepCount(Constant.MONTH,false,newSportsRecord);
                    typeAndStepCountList.add(typeAndStepCount);
                    return typeAndStepCountList;
                }
            }else {
                newSportsRecord.setRealStep(newSportsRecord.getRealStep()+sportsRecord.getRealStep());
                newSportsRecord.setCalorie(newSportsRecord.getCalorie()+sportsRecord.getCalorie());
                newSportsRecord.setMileage(newSportsRecord.getMileage()+sportsRecord.getMileage());
                if(sportsRecord.getPersistTime()==null){
                    sportsRecord.setPersistTime("0:0:0");
                }else {
                    newSportsRecord.setPersistTime(MyTime.getTimeSum(newSportsRecord.getPersistTime(),sportsRecord.getPersistTime()));
                }
                if(sportsRecord.getRealStep()!=0){
                    count++;
                }
                if(i==mySportsRecordList.size()){
                    newSportsRecord.setPersistTime(MyTime.getOccupyHour(newSportsRecord.getPersistTime(),-1));

                    DecimalFormat df = new DecimalFormat("0.0");//格式化小数，不足的补0
                    String mileage= df.format(newSportsRecord.getMileage());
                    newSportsRecord.setMileage(Float.valueOf(mileage));

                    TypeAndStepCount typeAndStepCount=new TypeAndStepCount(Constant.MONTH,false,newSportsRecord);
                    typeAndStepCountList.add(typeAndStepCount);
                    return typeAndStepCountList;
                }
            }
            i++;
        }
        Log.i(TAG, "typeAndStepCountList: "+typeAndStepCountList.size());
        return typeAndStepCountList;
    }

    //判断性别是否改变，如果改变直接更新
    public static Boolean isUpdateSex(int sex){
        int oldSex=getbodySex();
        if(oldSex==sex){
            return false;
        }else {
            int id=getbodyId();
            PersonInfo personInfo=new PersonInfo();
            personInfo.setSex(sex);
            personInfo.update(id);
            Log.i(TAG, "Sex: "+sex);
            Log.i(TAG, "性别为: "+getbodySex());
            return true;
        }
    }

    //判断身高是否改变，如果改变直接更新
    public static Boolean isUpdateHeight(int height){
        int oldHeight=getbodyHeight();
        if(oldHeight==height){
            return false;
        }else {
            int id=getbodyId();
            PersonInfo personInfo=new PersonInfo();
            personInfo.setBodyHeight(height);
            personInfo.update(id);
            return true;
        }
    }

    //判断体重是否改变，如果改变直接更新
    public static Boolean isUpdateWeight(int Weight){
        int oldWeight=getbodyWeight();
        if(oldWeight==Weight){
            return false;
        }else {
            int id=getbodyId();
            PersonInfo personInfo=new PersonInfo();
            personInfo.setBodyWeight(Weight);
            personInfo.update(id);
            return true;
        }
    }

    //判断年龄是否改变，如果改变直接更新
    public static Boolean isUpdateAge(int Age){
        int oldAge=getbodyAge();
        if(oldAge==Age){
            return false;
        }else {
            int id=getbodyId();
            PersonInfo personInfo=new PersonInfo();
            personInfo.setBodyAge(Age);
            personInfo.update(id);
            return true;
        }
    }

    //判断实时步数目标是否改变，如果改变直接更新
    public static Boolean isUpdateRTStepTarget(int RTStepTarget){
        int oldRTStepTarget=getRTStepTarget();
        if(oldRTStepTarget==RTStepTarget){
            return false;
        }else {
            int id=getbodyId();
            PersonInfo personInfo=new PersonInfo();
            personInfo.setRTStepTarget(RTStepTarget);
            personInfo.update(id);
            return true;
        }
    }

    //获得久坐提醒是否打开
    public static Boolean getOpenSedentaryReminder(){
        SettingRecord settingRecord=DataSupport.findFirst(SettingRecord.class);
        if(settingRecord==null){
            return false;
        }else {
            return settingRecord.getOpenSedentaryReminder();
        }
    }

    //获得开始时间
    public static String getStartTime(){
        SettingRecord settingRecord=DataSupport.findFirst(SettingRecord.class);
        if(settingRecord==null){
            return "08:00";
        }else {
            if(settingRecord.getStartTime()==null){
                return "08:00";
            }else {
                return settingRecord.getStartTime();
            }
        }
    }

    //获得结束时间
    public static String getEndTime(){
        SettingRecord settingRecord=DataSupport.findFirst(SettingRecord.class);
        if(settingRecord==null){
            return "18:00";
        }else {
            if(settingRecord.getEndTime()==null){
                return "18:00";
            }else {
                return settingRecord.getEndTime();
            }
        }
    }

    //获得午休免打扰是否打开
    public static Boolean getOpenUnDisturb(){
        SettingRecord settingRecord=DataSupport.findFirst(SettingRecord.class);
        if(settingRecord==null){
            return false;
        }else {
            return settingRecord.getOpenUnDisturb();
        }
    }

    //获得自动暂停是否打开
    public static Boolean getOpenAutoPause(){
        SettingRecord settingRecord=DataSupport.findFirst(SettingRecord.class);
        if(settingRecord==null){
            return false;
        }else {
            return settingRecord.getOpenAutoPause();
        }
    }

    //获得运动中屏幕常亮是否打开
    public static Boolean getOpenScreenOn(){
        SettingRecord settingRecord=DataSupport.findFirst(SettingRecord.class);
        if(settingRecord==null){
            return false;
        }else {
            return settingRecord.getOpenScreenOn();
        }
    }

    //获得语音播报是否打开
    public static Boolean getOpenVoiceAnnouncements(){
        SettingRecord settingRecord=DataSupport.findFirst(SettingRecord.class);
        if(settingRecord==null){
            return false;
        }else {
            return settingRecord.getOpenVoiceAnnouncements();
        }
    }

    //获得男声或者女声
    public static int getmaleVoiceOrfemaleVoice(){
        SettingRecord settingRecord=DataSupport.findFirst(SettingRecord.class);
        if(settingRecord==null){
            return 1;
        }else {
            if(settingRecord.getMaleVoiceOrfemaleVoice()==0){
                //0表示什么都没有选
                return 1;
            }else {
                return settingRecord.getMaleVoiceOrfemaleVoice();
            }
        }
    }

    //获得播报频率
    public static int getfrequency(){
        SettingRecord settingRecord=DataSupport.findFirst(SettingRecord.class);
        if(settingRecord==null){
            return 1;
        }else {
            if(settingRecord.getFrequency()==0){
                //0表示什么都没有选
                return 1;
            }else {
                return settingRecord.getFrequency();
            }
        }
    }

    //获得跑步目标
    public static int getrunningTarget(){
        SettingRecord settingRecord=DataSupport.findFirst(SettingRecord.class);
        if(settingRecord==null){
            return 1;
        }else {
            if(settingRecord.getRunningTarget()==0){
                //0表示什么都没有选
                return 1;
            }else {
                return settingRecord.getRunningTarget();
            }
        }
    }

    //获得距离目标
    public static String getdistanceTarget(){
        SettingRecord settingRecord=DataSupport.findFirst(SettingRecord.class);
        if(settingRecord==null){
            return "5";
        }else {
            if(settingRecord.getDistanceTarget()==null){
                return "5";
            }else {
                return settingRecord.getDistanceTarget();
            }

        }
    }

    //获得时间目标
    public static String gettimeTarget(){
        SettingRecord settingRecord=DataSupport.findFirst(SettingRecord.class);
        if(settingRecord==null){
            return "00:30";
        }else {
            if(settingRecord.getTimeTarget()==null){
                return "00:30";
            }else {
                return settingRecord.getTimeTarget();
            }

        }
    }

    //存储设置
    public static void saveSettingRecord(SettingRecord mySettingRecord){
        SettingRecord settingRecord=DataSupport.findFirst(SettingRecord.class);
        if(settingRecord==null){
            mySettingRecord.save();
        }else {
            mySettingRecord.update(settingRecord.getId());
        }
    }

    //根据title获得描述
    public static String getDescribe(String title, Context context){
        List<BadgeRecord> badgeRecordList=DataSupport.where("title=?",title).find(BadgeRecord.class);
        if(badgeRecordList.size()==0||badgeRecordList.isEmpty()){
            //没有记录则保存
            String describe=getDescribeFromTitle(title,context);            //获得描述
            BadgeRecord badgeRecord=new BadgeRecord();
            badgeRecord.setTitle(title);
            badgeRecord.setDescribe(describe);
            badgeRecord.setWhetherGet(false);
            saveBadgeRecord(badgeRecord);
            return describe;
        }else if(badgeRecordList.size()==1){
            return badgeRecordList.get(0).getDescribe();
        }else {
            Log.i(TAG, "getDescribe: 存储错误");
            return getDescribeFromTitle(title,context);            //获得描述
        }
    }

    //根据title获得是否获得徽章
    public static Boolean getwhetherGet(String title){
        List<BadgeRecord> badgeRecordList=DataSupport.where("title = ?",title).find(BadgeRecord.class);
        if(badgeRecordList.size()==0||badgeRecordList.isEmpty()){
            return false;
        }else {
            if(badgeRecordList.size()==1){
                return badgeRecordList.get(0).getWhetherGet();
            }else {
                Log.i(TAG, "getwhetherGet: 存储错误");
                return false;
            }
        }
    }

    //保存徽章记录
    public static void saveBadgeRecord(BadgeRecord badgeRecord){
        List<BadgeRecord> badgeRecordList=DataSupport.where("title=?",badgeRecord.getTitle()).find(BadgeRecord.class);
        if(badgeRecordList.size()==0||badgeRecordList.isEmpty()){
            //表示没有该记录
            badgeRecord.save();
        }else if(badgeRecordList.size()==1){
            //有一条记录则更新
            badgeRecord.update(badgeRecordList.get(0).getId());
        }
    }

    //根据是否获得徽章来获得相应的图片
    public static Bitmap getBitmapFromWhetherGet(String title,Boolean whetherGet,Context context){
        Bitmap picture;
        if(title.equals(context.getResources().getString(R.string.ChallengeTenThousandSteps))||
                    title.equals(context.getResources().getString(R.string.ChallengeTwentyThousandSteps))||
                    title.equals(context.getResources().getString(R.string.ChallengeThirtyThousandSteps))){
                //挑战10000步,挑战20000步,挑战30000步
                if(whetherGet){
                    //如果获得
                    picture= BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_step_medal_steps_on);
                }else {
                    picture= BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_step_medal_steps_off);
                }
        }else if(title.equals(context.getResources().getString(R.string.ForThreeDays))||
                title.equals(context.getResources().getString(R.string.ForFiveDays))||
                title.equals(context.getResources().getString(R.string.ForTenDays))||
                title.equals(context.getResources().getString(R.string.ForTwentyDays))||
                title.equals(context.getResources().getString(R.string.ForFiftyDays))||
                title.equals(context.getResources().getString(R.string.ForOneHundredDays))){
            //连续3天 连续5天 连续10天 连续20天 连续50天 连续100天
            if(whetherGet){
                //如果获得
                picture= BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_step_medal_day_on);
            }else {
                picture= BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_step_medal_day_off);
            }
        }else if(title.equals(context.getResources().getString(R.string.TotalTenkilometers))||
                title.equals(context.getResources().getString(R.string.TotalTwentykilometers))||
                title.equals(context.getResources().getString(R.string.TotalFiftykilometers))||
                title.equals(context.getResources().getString(R.string.TotalOneHundredkilometers))||
                title.equals(context.getResources().getString(R.string.TotalFiveHundredkilometers))||
                title.equals(context.getResources().getString(R.string.TotalThousandkilometers))){
            //累计10公里 累计20公里 累计50公里 累计100公里 累计500公里 累计1000公里
            if(whetherGet){
                //如果获得
                picture= BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_step_medal_km_on);
            }else {
                picture= BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_step_medal_km_off);
            }
        }else if(title.equals(context.getResources().getString(R.string.SpeedFivekilometers))||
                title.equals(context.getResources().getString(R.string.SpeedEightkilometers))||
                title.equals(context.getResources().getString(R.string.SpeedTenkilometers))){
            //极速5公里 极速8公里 极速10公里
            if(whetherGet){
                //如果获得
                picture= BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_run_medal_speed_on);
            }else {
                picture= BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_run_medal_speed_off);
            }
        }else if(title.equals(context.getResources().getString(R.string.ChallengeFivekilometers))||
                title.equals(context.getResources().getString(R.string.ChallengeTenkilometers))||
                title.equals(context.getResources().getString(R.string.ChallengeTwentykilometers))||
                title.equals(context.getResources().getString(R.string.ChallengeThirtykilometers))||
                title.equals(context.getResources().getString(R.string.ChallengeFortykilometers))||
                title.equals(context.getResources().getString(R.string.ChallengeFiftykilometers))){
            //挑战5公里 挑战10公里 挑战20公里 挑战30公里 挑战40公里 挑战50公里
            if(whetherGet){
                //如果获得
                picture= BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_run_medal_challeges_on);
            }else {
                picture= BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_run_medal_challeges_off);
            }
        }else {
            //累计跑步达到50公里  累计跑步达到100公里 累计跑步达到300公里  累计跑步达到500公里 累计跑步达到800公里  累计跑步达到1000公里
            if(whetherGet){
                //如果获得
                picture= BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_run_medal_accumulation_on);
            }else {
                picture= BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_run_medal_accumulation_off);
            }
        }
        return picture;
    }

    //获得描述
    public static String getDescribeFromTitle(String title,Context context){
        if(title.equals(context.getResources().getString(R.string.ChallengeTenThousandSteps))){
            //挑战10000步
            return context.getResources().getString(R.string.ChallengeTenThousandStepsDescribe);
        }else if(title.equals(context.getResources().getString(R.string.ChallengeTwentyThousandSteps))){
            //挑战20000步
            return context.getResources().getString(R.string.ChallengeTwentyThousandStepsDescribe);
        }else if(title.equals(context.getResources().getString(R.string.ChallengeThirtyThousandSteps))){
            //挑战30000步
            return context.getResources().getString(R.string.ChallengeThirtyThousandStepsDescribe);
        }else if(title.equals(context.getResources().getString(R.string.ForThreeDays))){
            //连续3天
            return context.getResources().getString(R.string.ForThreeDaysDescribe);
        }else if(title.equals(context.getResources().getString(R.string.ForFiveDays))){
            //连续5天
            return context.getResources().getString(R.string.ForFiveDaysDescribe);
        }else if(title.equals(context.getResources().getString(R.string.ForTenDays))){
            //连续10天
            return context.getResources().getString(R.string.ForTenDaysDescribe);
        }else if(title.equals(context.getResources().getString(R.string.ForTwentyDays))){
            //连续20天
            return context.getResources().getString(R.string.ForTwentyDaysDescribe);
        }else if(title.equals(context.getResources().getString(R.string.ForFiftyDays))){
            //连续50天
            return context.getResources().getString(R.string.ForFiftyDaysDescribe);
        }else if(title.equals(context.getResources().getString(R.string.ForOneHundredDays))){
            //连续100天
            return context.getResources().getString(R.string.ForOneHundredDaysDescribe);
        }else if(title.equals(context.getResources().getString(R.string.TotalTenkilometers))){
            //累计10公里
            return context.getResources().getString(R.string.TotalTenkilometersDescribe);
        }else if(title.equals(context.getResources().getString(R.string.TotalTwentykilometers))){
            //累计20公里
            return context.getResources().getString(R.string.TotalTwentykilometersDescribe);
        }else if(title.equals(context.getResources().getString(R.string.TotalFiftykilometers))){
            //累计50公里
            return context.getResources().getString(R.string.TotalFiftykilometersDescribe);
        }else if(title.equals(context.getResources().getString(R.string.TotalOneHundredkilometers))){
            //累计100公里
            return context.getResources().getString(R.string.TotalOneHundredkilometersDescribe);
        }else if(title.equals(context.getResources().getString(R.string.TotalFiveHundredkilometers))){
            //累计500公里
            return context.getResources().getString(R.string.TotalFiveHundredkilometersDescribe);
        }else if(title.equals(context.getResources().getString(R.string.TotalThousandkilometers))){
            //累计1000公里
            return context.getResources().getString(R.string.TotalThousandkilometersDescribe);
        }else if(title.equals(context.getResources().getString(R.string.SpeedFivekilometers))){
            //极速5公里
            return context.getResources().getString(R.string.SpeedFivekilometersDescribe);
        }else if(title.equals(context.getResources().getString(R.string.SpeedEightkilometers))){
            //极速8公里
            return context.getResources().getString(R.string.SpeedEightkilometersDescribe);
        }else if(title.equals(context.getResources().getString(R.string.SpeedTenkilometers))){
            //极速10公里
            return context.getResources().getString(R.string.SpeedTenkilometersDescribe);
        }else if(title.equals(context.getResources().getString(R.string.ChallengeFivekilometers))){
            //挑战5公里
            return context.getResources().getString(R.string.ChallengeFivekilometersDescribe);
        }else if(title.equals(context.getResources().getString(R.string.ChallengeTenkilometers))){
            //挑战10公里
            return context.getResources().getString(R.string.ChallengeTenkilometersDescribe);
        }else if(title.equals(context.getResources().getString(R.string.ChallengeTwentykilometers))){
            //挑战20公里
            return context.getResources().getString(R.string.ChallengeTwentykilometersDescribe);
        }else if(title.equals(context.getResources().getString(R.string.ChallengeThirtykilometers))){
            //挑战30公里
            return context.getResources().getString(R.string.ChallengeThirtykilometersDescribe);
        }else if(title.equals(context.getResources().getString(R.string.ChallengeFortykilometers))){
            //挑战40公里
            return context.getResources().getString(R.string.ChallengeFortykilometersDescribe);
        }else if(title.equals(context.getResources().getString(R.string.ChallengeFiftykilometers))){
            //挑战50公里
            return context.getResources().getString(R.string.ChallengeFiftykilometersDescribe);
        }else if(title.equals(context.getResources().getString(R.string.TotalRunFiftykilometers))){
            //累计跑步达到50公里
            return context.getResources().getString(R.string.TotalRunFiftykilometersDescribe);
        }else if(title.equals(context.getResources().getString(R.string.TotalRunOneHundredkilometers))){
            //累计跑步达到100公里
            return context.getResources().getString(R.string.TotalRunOneHundredkilometersDescribe);
        }else if(title.equals(context.getResources().getString(R.string.TotalRunThreeHundredkilometers))){
            //累计跑步达到300公里
            return context.getResources().getString(R.string.TotalRunThreeHundredkilometersDescribe);
        }else if(title.equals(context.getResources().getString(R.string.TotalRunFiveHundredkilometers))){
            //累计跑步达到500公里
            return context.getResources().getString(R.string.TotalRunFiveHundredkilometersDescribe);
        }else if(title.equals(context.getResources().getString(R.string.TotalRunEightHundredkilometers))){
            //累计跑步达到800公里
            return context.getResources().getString(R.string.TotalRunEightHundredkilometersDescribe);
        }else {
//            if(title.equals(context.getResources().getString(R.string.TotalRunThousandkilometers)))
            //累计跑步达到1000公里
            return context.getResources().getString(R.string.TotalRunThousandkilometersDescribe);
        }
    }
}
