package com.example.root.sportshelper.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.root.sportshelper.R;
import com.example.root.sportshelper.database.SportsRecord;
import com.example.root.sportshelper.database.TypeAndStepCount;
import com.example.root.sportshelper.ruler.HistogramView;
import com.example.root.sportshelper.utils.Constant;
import com.example.root.sportshelper.utils.DbHelper;
import com.example.root.sportshelper.utils.MyTime;

import java.text.ParseException;
import java.util.List;

/**
 * Created by root on 17-8-22.
 */

public class HStepCountAdapter extends RecyclerView.Adapter<HStepCountAdapter.ViewHolder> implements View.OnClickListener{
    private List<TypeAndStepCount> typeAndStepCountList;

    private OnItemClickListener myOnItemClickListener=null;

    public interface OnItemClickListener{
        void onItemClick(View view , int position);
    }
    static class ViewHolder extends RecyclerView.ViewHolder{
        HistogramView histogramView;

        public ViewHolder(View view){
            super(view);
            histogramView=(HistogramView)view.findViewById(R.id.Histogram);
        }
    }
    public HStepCountAdapter(List<TypeAndStepCount> typeAndStepCounts){
        this.typeAndStepCountList=typeAndStepCounts;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.hstep_count,parent,false);
        ViewHolder holder=new ViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TypeAndStepCount myTypeAndStepCount=typeAndStepCountList.get(position);
        holder.itemView.setTag(position);

        SportsRecord mySportRecord=myTypeAndStepCount.getTypeSportsRecord();

        //根据type设置宽度
        int type=myTypeAndStepCount.getType();
        ViewGroup.LayoutParams layoutParams=holder.histogramView.getLayoutParams();
        if(type== Constant.DAY){
            layoutParams.width=80;
        }else if(type==Constant.WEEK){
            layoutParams.width=100;
        }else if(type==Constant.MONTH){
            layoutParams.width=144;
        }
        holder.histogramView.setLayoutParams(layoutParams);

        holder.histogramView.setType(myTypeAndStepCount.getType());
        holder.histogramView.setMyDate(mySportRecord.getDate());
        holder.histogramView.setProgressValue(mySportRecord.getRealStep());
        //holder.histogramView.setProgressValue(1000);
        holder.histogramView.setSuggestStep(DbHelper.getRTStepTarget());                //使用的是全体目标
        holder.histogramView.setIsSelect(myTypeAndStepCount.getSelect());
    }

    @Override
    public int getItemCount() {
        return typeAndStepCountList.size();
    }

    @Override
    public void onClick(View v) {
        if (myOnItemClickListener != null) {
            //注意这里使用getTag方法获取position
            myOnItemClickListener.onItemClick(v,(int)v.getTag());
        }
    }
    public void setMyOnItemClickListener(HStepCountAdapter.OnItemClickListener listener) {
        this.myOnItemClickListener = listener;
    }

    public void refresh(){
        notifyDataSetChanged();
    }
}
