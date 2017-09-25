package com.example.root.sportshelper.database;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;

/**
 * Created by root on 17-8-29.
 */

public class RunningRecord extends DataSupport{
    private int id;
    private int calorie;                        //卡路里
    private float mileage;                      //公里
    private String date;                        //日期
    private String startTime;                   //开始时间
    private String endTime;                     //结束时间
    private String persistTime;                    //持续时间
    private String speed;                           //配速
    private ArrayList<GpsRecord> gpsRecords=new ArrayList<GpsRecord>();        //运动点的记录

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCalorie() {
        return calorie;
    }

    public void setCalorie(int calorie) {
        this.calorie = calorie;
    }

    public float getMileage() {
        return mileage;
    }

    public void setMileage(float mileage) {
        this.mileage = mileage;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getPersistTime() {
        return persistTime;
    }

    public void setPersistTime(String persistTime) {
        this.persistTime = persistTime;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public ArrayList<GpsRecord> getGpsRecords() {
        return gpsRecords;
    }

//    public void setGpsRecords(ArrayList<GpsRecord> gpsRecords) {
//        //this.gpsRecords = gpsRecords;
//        this.gpsRecords=(ArrayList<GpsRecord>)gpsRecords.clone();
//        //this.gpsRecords=new ArrayList<GpsRecord>(gpsRecords);
//    }

    public void setGpsRecords(ArrayList<GpsRecord> gpsRecords) {
        this.gpsRecords = gpsRecords;
    }
}
