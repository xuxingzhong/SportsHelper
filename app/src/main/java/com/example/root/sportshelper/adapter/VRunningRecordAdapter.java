package com.example.root.sportshelper.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.root.sportshelper.R;
import com.example.root.sportshelper.database.RunningRecord;


import java.util.List;

/**
 * 跑步记录适配器
 * Created by root on 17-8-29.
 */

public class VRunningRecordAdapter extends RecyclerView.Adapter<VRunningRecordAdapter.ViewHolder>implements View.OnClickListener{
    private List<RunningRecord> runningRecordList;
    private OnItemClickListener RunningOnItemClickListener=null;

    public interface OnItemClickListener{
        void onItemClick(View view , int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView myDate;                    //日期
        TextView startTime;                 //起始时间
        TextView runningRecord_Mileage;     //里程
        TextView runningRecord_PersistTime;     //持续时间
        TextView runningRecord_Calorie;     //卡路里

        public ViewHolder(View view){
            super(view);
            myDate=(TextView)view.findViewById(R.id.date);
            startTime=(TextView)view.findViewById(R.id.startTime);
            runningRecord_Mileage=(TextView)view.findViewById(R.id.runningRecord_mileage);
            runningRecord_PersistTime=(TextView)view.findViewById(R.id.runningRecord_persistTime);
            runningRecord_Calorie=(TextView)view.findViewById(R.id.runningRecord_calorie);
        }
    }

    public VRunningRecordAdapter(List<RunningRecord> runningRecords){
        this.runningRecordList=runningRecords;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.hrunning_record,parent,false);
        ViewHolder holder=new ViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RunningRecord runningRecord=runningRecordList.get(position);
        holder.itemView.setTag(position);
        if(runningRecord.getPersistTime()!=null){
            holder.myDate.setText(runningRecord.getDate()+"");
            holder.startTime.setText(runningRecord.getStartTime());
            holder.runningRecord_Mileage.setText(runningRecord.getMileage()+"");
            holder.runningRecord_PersistTime.setText(runningRecord.getPersistTime());
            holder.runningRecord_Calorie.setText(runningRecord.getCalorie()+"");
        }

    }

    @Override
    public int getItemCount() {
        return runningRecordList.size();
    }

    @Override
    public void onClick(View v) {
        if (RunningOnItemClickListener != null) {
            //注意这里使用getTag方法获取position
            RunningOnItemClickListener.onItemClick(v,(int)v.getTag());
        }
    }

    public void setRunningOnItemClickListener(OnItemClickListener onItemClickListener){
        this.RunningOnItemClickListener=onItemClickListener;
    }
}
