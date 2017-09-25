package com.example.root.sportshelper.database;

import org.litepal.crud.DataSupport;

/**
 * Created by root on 17-8-1.
 */

public class PersonInfo extends DataSupport{
    private int id;
    private int sex;            //性别 1为男；-1为女
    private int bodyHeight;     //身高
    private int bodyWeight;     //体重
    private int bodyAge;        //年龄
    private int RTStepTarget;   //实时步数目标

    public int getRTStepTarget() {
        return RTStepTarget;
    }

    public void setRTStepTarget(int RTStepTarget) {
        this.RTStepTarget = RTStepTarget;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSex() {
        return sex;
    }

    public int getBodyHeight() {
        return bodyHeight;
    }

    public int getBodyWeight() {
        return bodyWeight;
    }

    public int getBodyAge() {
        return bodyAge;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public void setBodyHeight(int bodyHeight) {
        this.bodyHeight = bodyHeight;
    }

    public void setBodyWeight(int bodyWeight) {
        this.bodyWeight = bodyWeight;
    }

    public void setBodyAge(int bodyAge) {
        this.bodyAge = bodyAge;
    }
}
