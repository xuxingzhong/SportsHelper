package com.example.root.sportshelper.database;

/**
 * Created by root on 17-8-23.
 */

public class TypeAndStepCount {
    private int type;
    private Boolean isSelect;
    SportsRecord typeSportsRecord;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public SportsRecord getTypeSportsRecord() {
        return typeSportsRecord;
    }

    public void setTypeSportsRecord(SportsRecord typeSportsRecord) {
        this.typeSportsRecord = typeSportsRecord;
    }
    public TypeAndStepCount(int myType,Boolean isSelect,SportsRecord mySportsRecord){
        this.type=myType;
        this.isSelect=isSelect;
        this.typeSportsRecord=mySportsRecord;
    }

    public Boolean getSelect() {
        return isSelect;
    }

    public void setSelect(Boolean select) {
        isSelect = select;
    }
}
