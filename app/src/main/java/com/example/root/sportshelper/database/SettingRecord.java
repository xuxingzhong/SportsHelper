package com.example.root.sportshelper.database;

import org.litepal.crud.DataSupport;

/**
 * 设置信息
 * Created by root on 17-9-14.
 */

public class SettingRecord extends DataSupport {
    private int id;
    private Boolean ChangeStepTarget;                 //运动目标是否改变
    private Boolean OpenSedentaryReminder;            //久坐提醒是否打开
    private String StartTime;                           //开始时间
    private String EndTime;                             //结束时间
    private Boolean OpenUnDisturb;                    //午休免打扰是否打开
    private Boolean OpenAutoPause;                    //自动暂停是否打开
    private Boolean OpenScreenOn;                     //运动中屏幕常亮是否打开
    private Boolean OpenVoiceAnnouncements;           //语音播报是否打开
    private int maleVoiceOrfemaleVoice;                 //男声或者女声，-1表示什么没有选择，1为男声，2为女声
    private int frequency;                              //播报频率
    private int runningTarget;                          //跑步目标
    private String distanceTarget;                      //距离目标
    private String timeTarget;                          //时间目标

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Boolean getChangeStepTarget() {
        return ChangeStepTarget;
    }

    public void setChangeStepTarget(Boolean changeStepTarget) {
        ChangeStepTarget = changeStepTarget;
    }

    public Boolean getOpenSedentaryReminder() {
        return OpenSedentaryReminder;
    }

    public void setOpenSedentaryReminder(Boolean openSedentaryReminder) {
        OpenSedentaryReminder = openSedentaryReminder;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public Boolean getOpenUnDisturb() {
        return OpenUnDisturb;
    }

    public void setOpenUnDisturb(Boolean openUnDisturb) {
        OpenUnDisturb = openUnDisturb;
    }

    public Boolean getOpenAutoPause() {
        return OpenAutoPause;
    }

    public void setOpenAutoPause(Boolean openAutoPause) {
        OpenAutoPause = openAutoPause;
    }

    public Boolean getOpenScreenOn() {
        return OpenScreenOn;
    }

    public void setOpenScreenOn(Boolean openScreenOn) {
        OpenScreenOn = openScreenOn;
    }

    public Boolean getOpenVoiceAnnouncements() {
        return OpenVoiceAnnouncements;
    }

    public void setOpenVoiceAnnouncements(Boolean openVoiceAnnouncements) {
        OpenVoiceAnnouncements = openVoiceAnnouncements;
    }

    public int getMaleVoiceOrfemaleVoice() {
        return maleVoiceOrfemaleVoice;
    }

    public void setMaleVoiceOrfemaleVoice(int maleVoiceOrfemaleVoice) {
        this.maleVoiceOrfemaleVoice = maleVoiceOrfemaleVoice;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getRunningTarget() {
        return runningTarget;
    }

    public void setRunningTarget(int runningTarget) {
        this.runningTarget = runningTarget;
    }

    public String getDistanceTarget() {
        return distanceTarget;
    }

    public void setDistanceTarget(String distanceTarget) {
        this.distanceTarget = distanceTarget;
    }

    public String getTimeTarget() {
        return timeTarget;
    }

    public void setTimeTarget(String timeTarget) {
        this.timeTarget = timeTarget;
    }
}
