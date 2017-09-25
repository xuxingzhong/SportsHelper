package com.example.root.sportshelper.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.root.sportshelper.R;
import com.example.root.sportshelper.database.SportsRecord;
import com.example.root.sportshelper.ruler.HStepRecord;

import java.util.List;

/**
 * Created by root on 17-8-10.
 */

public class HStepRecordAdapter extends RecyclerView.Adapter<HStepRecordAdapter.ViewHolder> implements View.OnClickListener{
    private List<SportsRecord> mSportRecordList;

    private OnItemClickListener mOnItemClickListener = null;
    //define interface
    public  interface OnItemClickListener {
        void onItemClick(View view , int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView TorFImage;
        HStepRecord hStepRecord;

        public ViewHolder(View view){
            super(view);
            TorFImage=(ImageView)view.findViewById(R.id.TorF);
            hStepRecord=(HStepRecord)view.findViewById(R.id.HStepRecord);
        }
    }

    public HStepRecordAdapter(List<SportsRecord> sportsRecords){
        mSportRecordList=sportsRecords;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.hstep_record,parent,false);
        ViewHolder holder=new ViewHolder(view);
        view.setOnClickListener(this);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SportsRecord sportsRecord=mSportRecordList.get(position);
        holder.itemView.setTag(position);

        if(sportsRecord.getRealStep()>=sportsRecord.getTargetStep()){
            holder.TorFImage.setImageResource(R.mipmap.ic_step_success);
        }else {
            holder.TorFImage.setImageResource(R.mipmap.ic_step_failed);
        }
        holder.hStepRecord.setMaxStep(sportsRecord.getTargetStep());
        holder.hStepRecord.setProgressValue(sportsRecord.getRealStep());
        holder.hStepRecord.setMyDate(sportsRecord.getDate());
    }

    @Override
    public int getItemCount() {
        return mSportRecordList.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取position
            mOnItemClickListener.onItemClick(v,(int)v.getTag());
        }
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}
