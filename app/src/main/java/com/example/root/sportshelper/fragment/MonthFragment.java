package com.example.root.sportshelper.fragment;

import android.app.Fragment;
import android.os.Bundle;
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
import com.example.root.sportshelper.utils.DbHelper;

import org.litepal.crud.DataSupport;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * 月份
 * Created by root on 17-8-28.
 */

public class MonthFragment extends Fragment {
    TextView stepNumber;                //步数
    TextView expendCalorie;             //消耗的卡路里
    TextView continueTime;              //运动时间
    TextView mymileage;                 //里程
    RecyclerView HMonthStepCount;
    private View rootView;
    private List<TypeAndStepCount> typeAndStepCountList=new ArrayList<>();
    private List<SportsRecord> mysportsRecordList=new ArrayList<>();
    private String TAG="MonthFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if(rootView!=null){
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if(parent!=null){
                parent.removeView(rootView);
            }
        }else {
            //重用weekfragment视图
            rootView=inflater.inflate(R.layout.dayfragment,container,false);
            initView(rootView);
            typeAndStepCountList.clear();
            mysportsRecordList.clear();
            initRecycleView();
        }
        return rootView;
    }

    private void initView(View v){
        stepNumber=(TextView)v.findViewById(R.id.step_number);
        expendCalorie=(TextView)v.findViewById(R.id.expend_calorie);
        continueTime=(TextView)v.findViewById(R.id.continue_time);
        mymileage=(TextView)v.findViewById(R.id.mymileage);
        HMonthStepCount=(RecyclerView)v.findViewById(R.id.HDayStepCount);
    }

    private void initRecycleView(){
        mysportsRecordList= DataSupport.findAll(SportsRecord.class);

        try {
            typeAndStepCountList= DbHelper.getMonthStepCount(mysportsRecordList);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        initTypeAndStepCount(typeAndStepCountList,typeAndStepCountList.size()-1);

        initInformation(typeAndStepCountList.get(typeAndStepCountList.size()-1).getTypeSportsRecord());

        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        HMonthStepCount.setLayoutManager(layoutManager);
        final HStepCountAdapter hStepCountAdapter=new HStepCountAdapter(typeAndStepCountList);
        HMonthStepCount.setAdapter(hStepCountAdapter);
        HMonthStepCount.smoothScrollToPosition(mysportsRecordList.size()-1);
        hStepCountAdapter.setMyOnItemClickListener(new HStepCountAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                initTypeAndStepCount(typeAndStepCountList,position);
                initInformation(typeAndStepCountList.get(position).getTypeSportsRecord());
                hStepCountAdapter.refresh();
            }
        });
    }

    private void initTypeAndStepCount(List<TypeAndStepCount> typeAndStepCountList1,int isSelectPos){
        int i=0;
        for(TypeAndStepCount typeAndStepCount:typeAndStepCountList1){
            if(i==isSelectPos){
                typeAndStepCount.setSelect(true);
            }else {
                typeAndStepCount.setSelect(false);
            }
            i++;
        }
    }

    private void initInformation(SportsRecord sportsRecord){
        stepNumber.setText(sportsRecord.getRealStep()+"");
        expendCalorie.setText(sportsRecord.getCalorie()+"");
        if(sportsRecord.getPersistTime()==null){
            continueTime.setText("0");
        }else {
            continueTime.setText(sportsRecord.getPersistTime());
        }
        mymileage.setText(sportsRecord.getMileage()+"");
    }
}
