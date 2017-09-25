package com.example.root.sportshelper.database;


import com.amap.api.location.AMapLocation;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.Date;

/**
 * 位置记录
 * Created by root on 17-9-5.
 */

public class GpsRecord extends DataSupport implements Serializable {
    private RunningRecord runningRecord;
    private int id;
    private double lat, lng, alt;
    private float distance;
    private float speed;
    private Date date;
    //transient public Location loc;        // only for computing
    transient public AMapLocation loc;

    public GpsRecord(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public GpsRecord(Date date, AMapLocation l){
        super();
        this.lat = l.getLatitude();             //经度
        this.lng = l.getLongitude();           //维度
        this.alt = l.getAltitude();             //高度
        this.date = date;
        this.loc = l;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public double getAlt() {
        return alt;
    }


    public Date getDate() {
        return date;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setAlt(double alt) {
        this.alt = alt;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public AMapLocation getLoc() {
        return loc;
    }

    public void setLoc(AMapLocation loc) {
        this.loc = loc;
    }

    public RunningRecord getRunningRecord() {
        return runningRecord;
    }

    public void setRunningRecord(RunningRecord runningRecord) {
        this.runningRecord = runningRecord;
    }

    @Override
    public String toString() {
        return String.format("Location: %s, %.6f, %.6f, %.2f, dist=%.2f, sp=%f", date, lat, lng, alt, distance, speed);
    }
}
