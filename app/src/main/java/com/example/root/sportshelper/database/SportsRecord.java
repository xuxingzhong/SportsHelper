package com.example.root.sportshelper.database;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.DataSupport;

import java.util.Date;

/**
 *计步记录
 * Created by root on 17-8-9.
 */

public class SportsRecord extends DataSupport {
    private int id;
    private int targetStep;         //目标步数
    private int realStep;           //实际步数
    private int calorie;            //卡路里
    private float mileage;          //公里
    private String date;              //日期
    private String startTime;          //开始时间
    private String endTime;            //结束时间
    private String persistTime;        //持续时间

    private int previousStep;       //上次记录的步数

    public int getId() {
        return id;
    }

    public int getTargetStep() {
        return targetStep;
    }

    public int getRealStep() {
        return realStep;
    }

    public int getCalorie() {
        return calorie;
    }

    public float getMileage() {
        return mileage;
    }

    public String getDate() {
        return date;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTargetStep(int targetStep) {
        this.targetStep = targetStep;
    }

    public void setRealStep(int realStep) {
        this.realStep = realStep;
    }

    public void setCalorie(int calorie) {
        this.calorie = calorie;
    }

    public void setMileage(float mileage) {
        this.mileage = mileage;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getPreviousStep() {
        return previousStep;
    }

    public void setPreviousStep(int previousStep) {
        this.previousStep = previousStep;
    }

    public String getPersistTime() {
        return persistTime;
    }

    public void setPersistTime(String persistTime) {
        this.persistTime = persistTime;
    }
}
