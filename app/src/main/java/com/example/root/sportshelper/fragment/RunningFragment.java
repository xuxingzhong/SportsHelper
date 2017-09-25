package com.example.root.sportshelper.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.root.sportshelper.R;
import com.example.root.sportshelper.ShowCountDown;
import com.example.root.sportshelper.ShowMapRecord;
import com.example.root.sportshelper.StepCount;
import com.example.root.sportshelper.adapter.HStepRecordAdapter;
import com.example.root.sportshelper.adapter.VRunningRecordAdapter;
import com.example.root.sportshelper.database.GpsRecord;
import com.example.root.sportshelper.database.RunningRecord;
import com.example.root.sportshelper.database.SportsRecord;
import com.example.root.sportshelper.ruler.CircleProgress;
import com.example.root.sportshelper.utils.Constant;
import com.example.root.sportshelper.utils.DbHelper;
import com.example.root.sportshelper.utils.MyTime;
import com.example.root.sportshelper.utils.myLocation;

import org.litepal.crud.DataSupport;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 17-8-29.
 */

public class RunningFragment extends Fragment implements View.OnClickListener{
    private String TAG="RunningFragment";
    private View rootView;
    TextView totalMileage;              //总里程
    TextView runningTime;               //总跑步时间
    TextView totalCalorie;              //总消耗的卡路里
    TextView start;                     //start按钮
    RecyclerView runningRecord;         //跑步记录

    private float mileage=0.0f;              //总里程
    private String totalTime="0:0:0";           //总跑步时间
    private int calorie=0;                //总消耗的卡路里

    private Context context;                //上下文

    private UpdateRecordReceiver updateRecordReceiver=new UpdateRecordReceiver();
    private IntentFilter intentFilter=new IntentFilter(Constant.BC_INTENT);

    List<RunningRecord> runningRecordList=new ArrayList<>();        //跑步记录
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //DataSupport.deleteAll(RunningRecord.class);
        if(rootView!=null){
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if(parent!=null){
                parent.removeView(rootView);
            }
            getActivity().registerReceiver(updateRecordReceiver,intentFilter);
        }else {
            rootView=inflater.inflate(R.layout.running_fragment,container,false);
            initView(rootView);
            try {
                initComponent();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            initRunningRecord();
            initRecycleView();
            getActivity().registerReceiver(updateRecordReceiver,intentFilter);

        }
        return rootView;
    }

    private void initView(View v){
        totalMileage=(TextView) v.findViewById(R.id.Total_mymileage);
        runningTime=(TextView)v.findViewById(R.id.runningtime);
        totalCalorie=(TextView)v.findViewById(R.id.calorie);
        start=(TextView)v.findViewById(R.id.start);
        start.setOnClickListener(this);
        runningRecord=(RecyclerView)v.findViewById(R.id.running_record);
        context=getActivity();
    }

    private void initComponent() throws ParseException {
        mileage=0.0f;              //总里程
        totalTime="0:0:0";           //总跑步时间
        calorie=0;                //总消耗的卡路里

        List<RunningRecord> list= DataSupport.findAll(RunningRecord.class);
        for(RunningRecord runningRecord:list){
            mileage=mileage+runningRecord.getMileage();
            calorie=calorie+runningRecord.getCalorie();
            if(runningRecord.getPersistTime()!=null){
                totalTime=MyTime.getTimeSum(totalTime,runningRecord.getPersistTime());
            }
        }
        totalMileage.setText(mileage+"");
        runningTime.setText(totalTime);
        totalCalorie.setText(calorie+"");
    }

    private void initRunningRecord()  {
        runningRecordList.clear();
        List<RunningRecord> runningRecords= DataSupport
                .limit(30)
                .order("id desc")
                .find(RunningRecord.class);
        for(RunningRecord myRunningRecord:runningRecords){
            if(myRunningRecord.getDate().equals(MyTime.getTodayDate())){
                myRunningRecord.setDate("今天");
            }else try {
                if(MyTime.isYesterday(myRunningRecord.getDate())){
                    myRunningRecord.setDate("昨天");
                }else {
                    myRunningRecord.setDate(MyTime.changeFormat(myRunningRecord.getDate()));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            try {
                myRunningRecord.setStartTime(MyTime.getTimeOffsecond(myRunningRecord.getStartTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            DecimalFormat decimalFormat =new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
            String myMileage=decimalFormat.format(myRunningRecord.getMileage());
            myRunningRecord.setMileage(Float.valueOf(myMileage));
            runningRecordList.add(myRunningRecord);
        }
    }

    private void initRecycleView(){
        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
        runningRecord.setLayoutManager(layoutManager);
        VRunningRecordAdapter vRunningRecordAdapter=new VRunningRecordAdapter(runningRecordList);
        runningRecord.setAdapter(vRunningRecordAdapter);

        vRunningRecordAdapter.setRunningOnItemClickListener(new VRunningRecordAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                RunningRecord runningRecord=runningRecordList.get(position);
//                String id=String.valueOf(runningRecord.getId());
//                List<GpsRecord> gpsRecords=DataSupport.where("runningRecord_id =?",id).find(GpsRecord.class);
//                Log.i(TAG, "runningRecord: "+gpsRecords.size());
//                if(gpsRecords.size()>0){
//                    if(gpsRecords.get(0).getLoc()==null){
//                        Log.i(TAG, "onItemClick: 没有AMapLocation");
//                    }
//                }
//                Log.i(TAG, "runningRecord: "+runningRecord.getId());

                Intent ShowMapRecord=new Intent(getActivity(),ShowMapRecord.class);
                ShowMapRecord.putExtra("id",runningRecord.getId());
                startActivity(ShowMapRecord);
            }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.start:
                if(myLocation.isOpenGps(context)){
                    Intent intent=new Intent(getActivity(), ShowCountDown.class);
                    startActivity(intent);
                }
                break;

        }
    }

    public class UpdateRecordReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive: 接收记录");

            try {
                initComponent();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            initRunningRecord();
            initRecycleView();
            abortBroadcast();           //截断广播
        }
    }

    @Override
    public void onDestroyView() {
        getActivity().unregisterReceiver(updateRecordReceiver);
        super.onDestroyView();

    }
}
