package com.example.root.sportshelper.database;

import org.litepal.crud.DataSupport;

/**
 * 徽章记录
 * Created by root on 17-9-19.
 */

public class BadgeRecord extends DataSupport{
    private int id;
    private String title;                   //标题,如挑战10000米
    private String describe;                //对标题的描述
    private Boolean whetherGet;             //是否得到该徽章

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public Boolean getWhetherGet() {
        return whetherGet;
    }

    public void setWhetherGet(Boolean whetherGet) {
        this.whetherGet = whetherGet;
    }
}
