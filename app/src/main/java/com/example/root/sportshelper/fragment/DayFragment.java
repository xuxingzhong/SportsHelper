package com.example.root.sportshelper.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.root.sportshelper.R;
import com.example.root.sportshelper.adapter.HStepCountAdapter;
import com.example.root.sportshelper.database.SportsRecord;
import com.example.root.sportshelper.database.TypeAndStepCount;
import com.example.root.sportshelper.utils.Constant;
import com.example.root.sportshelper.utils.MyTime;

import org.litepal.crud.DataSupport;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 17-8-25.
 */

public class DayFragment extends Fragment {
    TextView stepNumber;                //步数
    TextView expendCalorie;             //消耗的卡路里
    TextView continueTime;              //运动时间
    TextView mymileage;                 //里程
    RecyclerView HDayStepCount;
    private View rootView;
    private int type;       //日期类型
    private String TAG="DayFragment";
    private List<TypeAndStepCount> typeAndStepCountList=new ArrayList<>();
    private List<SportsRecord> mySportsRecordList;


    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Constant.TIME_CONSUMING:
                    initRecycleView();
                    break;
            }
        }
    };
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if(rootView!=null){
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if(parent!=null){
                parent.removeView(rootView);
            }
        }else {
            rootView=inflater.inflate(R.layout.dayfragment,container,false);
            initView(rootView);
            typeAndStepCountList.clear();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    mySportsRecordList= DataSupport.findAll(SportsRecord.class);
                    Message message=new Message();
                    message.what=Constant.TIME_CONSUMING;
                    handler.sendMessage(message);
                }
            }).start();

            initRecycleView();
        }
        return rootView;
    }

    private void initView(View v){
        stepNumber=(TextView)v.findViewById(R.id.step_number);
        expendCalorie=(TextView)v.findViewById(R.id.expend_calorie);
        continueTime=(TextView)v.findViewById(R.id.continue_time);
        mymileage=(TextView)v.findViewById(R.id.mymileage);
        HDayStepCount=(RecyclerView)v.findViewById(R.id.HDayStepCount);
    }

    private void initRecycleView(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutManager.setStackFromEnd(true);
        HDayStepCount.setLayoutManager(layoutManager);
        final HStepCountAdapter hStepCountAdapter = new HStepCountAdapter(typeAndStepCountList);
        HDayStepCount.setAdapter(hStepCountAdapter);

        if(mySportsRecordList!=null) {
            initTypeAndStepCount(Constant.DAY, mySportsRecordList.size() - 1);
            initInformation(mySportsRecordList.get(mySportsRecordList.size() - 1));

            HDayStepCount.smoothScrollToPosition(mySportsRecordList.size() - 1);
            hStepCountAdapter.setMyOnItemClickListener(new HStepCountAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
//                    typeAndStepCountList.clear();
//                    initTypeAndStepCount(Constant.DAY, position);
                    initTypeAndStepCountSelect(Constant.DAY, position);
                    initInformation(typeAndStepCountList.get(position).getTypeSportsRecord());
                    hStepCountAdapter.refresh();
                }
            });
        }
    }

    private void initTypeAndStepCount(int mytype,int isSelectPos){
        this.type=mytype;
        int i=0;
        if(type== Constant.DAY){
            for(SportsRecord mySportsRecord:mySportsRecordList){
                if(mySportsRecord.getDate().equals(MyTime.getTodayDate())){
                    mySportsRecord.setDate("今天");
                }else try {
                    if(MyTime.isYesterday(mySportsRecord.getDate())){
                        mySportsRecord.setDate("昨天");
                    }else {
                        mySportsRecord.setDate(MyTime.changeFormatOther(mySportsRecord.getDate()));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                TypeAndStepCount typeAndStepCount=null;
                if(i==isSelectPos){
                    typeAndStepCount =new TypeAndStepCount(type,true,mySportsRecord);
                }else {
                    typeAndStepCount=new TypeAndStepCount(type,false,mySportsRecord);
                }
                i++;
                typeAndStepCountList.add(typeAndStepCount);
            }
        }
    }

    //设置选择项
    private void initTypeAndStepCountSelect(int mytype,int isSelectPos){
        this.type=mytype;
        int i=0;
        if(type== Constant.DAY){
            for(TypeAndStepCount typeAndStepCount:typeAndStepCountList){
                if(i==isSelectPos){
                    typeAndStepCount.setSelect(true);
                }else {
                    typeAndStepCount.setSelect(false);
                }
                i++;
            }
        }
    }

    private void initInformation(SportsRecord sportsRecord){
        stepNumber.setText(sportsRecord.getRealStep()+"");
        expendCalorie.setText(sportsRecord.getCalorie()+"");
        if(sportsRecord.getPersistTime()==null){
            continueTime.setText("0");
        }else {
            try {
                continueTime.setText(MyTime.getOccupyHour(sportsRecord.getPersistTime(),-1));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        mymileage.setText(sportsRecord.getMileage()+"");
    }
}
