package com.example.root.sportshelper.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.root.sportshelper.R;
import com.example.root.sportshelper.ShowBadgeInfo;
import com.example.root.sportshelper.adapter.HStepCountAdapter;
import com.example.root.sportshelper.database.BadgeRecord;
import com.example.root.sportshelper.database.SportsRecord;
import com.example.root.sportshelper.database.TypeAndStepCount;
import com.example.root.sportshelper.ruler.myBadge;
import com.example.root.sportshelper.utils.Constant;
import com.example.root.sportshelper.utils.MyTime;

import org.litepal.crud.DataSupport;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * 计步徽章
 * Created by root on 17-8-25.
 */

public class StepCountBadgeFragment extends Fragment implements View.OnClickListener{
    private View rootView;
    private String TAG="StepCountBadgeFragment";
    myBadge ChallengeTenThousandSteps;              //挑战10000步
    myBadge ChallengeTwentyThousandSteps;
    myBadge ChallengeThirtyThousandSteps;

    myBadge ForThreeDays;
    myBadge ForFiveDays;
    myBadge ForTenDays;
    myBadge ForTwentyDays;
    myBadge ForFiftyDays;
    myBadge ForOneHundredDays;

    myBadge TotalTenkilometers;
    myBadge TotalTwentykilometers;
    myBadge TotalFiftykilometers;
    myBadge TotalOneHundredkilometers;
    myBadge TotalFiveHundredkilometers;
    myBadge TotalThousandkilometers;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //BadgeRecord.deleteAll(BadgeRecord.class);
        if(rootView!=null){
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if(parent!=null){
                parent.removeView(rootView);
            }
        }else {
            rootView=inflater.inflate(R.layout.stepcountbadge_fragment,container,false);
            initView(rootView);

        }
        return rootView;
    }

    private void initView(View v){
        ChallengeTenThousandSteps=(myBadge)v.findViewById(R.id.ChallengeTenThousandSteps);
        ChallengeTenThousandSteps.setOnClickListener(this);

        ChallengeTwentyThousandSteps=(myBadge)v.findViewById(R.id.ChallengeTwentyThousandSteps);
        ChallengeTwentyThousandSteps.setOnClickListener(this);

        ChallengeThirtyThousandSteps=(myBadge)v.findViewById(R.id.ChallengeThirtyThousandSteps);
        ChallengeThirtyThousandSteps.setOnClickListener(this);

        ForThreeDays=(myBadge)v.findViewById(R.id.ForThreeDays);
        ForThreeDays.setOnClickListener(this);

        ForFiveDays=(myBadge)v.findViewById(R.id.ForFiveDays);
        ForFiveDays.setOnClickListener(this);

        ForTenDays=(myBadge)v.findViewById(R.id.ForTenDays);
        ForTenDays.setOnClickListener(this);

        ForTwentyDays=(myBadge)v.findViewById(R.id.ForTwentyDays);
        ForTwentyDays.setOnClickListener(this);

        ForFiftyDays=(myBadge)v.findViewById(R.id.ForFiftyDays);
        ForFiftyDays.setOnClickListener(this);

        ForOneHundredDays=(myBadge)v.findViewById(R.id.ForOneHundredDays);
        ForOneHundredDays.setOnClickListener(this);

        TotalTenkilometers=(myBadge)v.findViewById(R.id.TotalTenkilometers);
        TotalTenkilometers.setOnClickListener(this);

        TotalTwentykilometers=(myBadge)v.findViewById(R.id.TotalTwentykilometers);
        TotalTwentykilometers.setOnClickListener(this);

        TotalFiftykilometers=(myBadge)v.findViewById(R.id.TotalFiftykilometers);
        TotalFiftykilometers.setOnClickListener(this);

        TotalOneHundredkilometers=(myBadge)v.findViewById(R.id.TotalOneHundredkilometers);
        TotalOneHundredkilometers.setOnClickListener(this);

        TotalFiveHundredkilometers=(myBadge)v.findViewById(R.id.TotalFiveHundredkilometers);
        TotalFiveHundredkilometers.setOnClickListener(this);

        TotalThousandkilometers=(myBadge)v.findViewById(R.id.TotalThousandkilometers);
        TotalThousandkilometers.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ChallengeTenThousandSteps:
                Intent intent=new Intent(getActivity(), ShowBadgeInfo.class);
                intent.putExtra("title",ChallengeTenThousandSteps.getMyBadgeText());
                startActivity(intent);
                break;
            case R.id.ChallengeTwentyThousandSteps:
                Intent ChallengeTwentyThousandSteps_intent=new Intent(getActivity(), ShowBadgeInfo.class);
                ChallengeTwentyThousandSteps_intent.putExtra("title",ChallengeTwentyThousandSteps.getMyBadgeText());
                startActivity(ChallengeTwentyThousandSteps_intent);
                break;
            case R.id.ChallengeThirtyThousandSteps:
                Intent ChallengeThirtyThousandSteps_intent=new Intent(getActivity(), ShowBadgeInfo.class);
                ChallengeThirtyThousandSteps_intent.putExtra("title",ChallengeThirtyThousandSteps.getMyBadgeText());
                startActivity(ChallengeThirtyThousandSteps_intent);
                break;
            case R.id.ForThreeDays:
                Intent ForThreeDays_intent=new Intent(getActivity(), ShowBadgeInfo.class);
                ForThreeDays_intent.putExtra("title",ForThreeDays.getMyBadgeText());
                startActivity(ForThreeDays_intent);
                break;
            case R.id.ForFiveDays:
                Intent ForFiveDays_intent=new Intent(getActivity(), ShowBadgeInfo.class);
                ForFiveDays_intent.putExtra("title",ForFiveDays.getMyBadgeText());
                startActivity(ForFiveDays_intent);
                break;
            case R.id.ForTenDays:
                Intent ForTenDays_intent=new Intent(getActivity(), ShowBadgeInfo.class);
                ForTenDays_intent.putExtra("title",ForTenDays.getMyBadgeText());
                startActivity(ForTenDays_intent);
                break;
            case R.id.ForTwentyDays:
                Intent ForTwentyDays_intent=new Intent(getActivity(), ShowBadgeInfo.class);
                ForTwentyDays_intent.putExtra("title",ForTwentyDays.getMyBadgeText());
                startActivity(ForTwentyDays_intent);
                break;
            case R.id.ForFiftyDays:
                Intent ForFiftyDays_intent=new Intent(getActivity(), ShowBadgeInfo.class);
                ForFiftyDays_intent.putExtra("title",ForFiftyDays.getMyBadgeText());
                startActivity(ForFiftyDays_intent);
                break;
            case R.id.ForOneHundredDays:
                Intent ForOneHundredDays_intent=new Intent(getActivity(), ShowBadgeInfo.class);
                ForOneHundredDays_intent.putExtra("title",ForOneHundredDays.getMyBadgeText());
                startActivity(ForOneHundredDays_intent);
                break;
            case R.id.TotalTenkilometers:
                Intent TotalTenkilometers_intent=new Intent(getActivity(), ShowBadgeInfo.class);
                TotalTenkilometers_intent.putExtra("title",TotalTenkilometers.getMyBadgeText());
                startActivity(TotalTenkilometers_intent);
                break;
            case R.id.TotalTwentykilometers:
                Intent TotalTwentykilometers_intent=new Intent(getActivity(), ShowBadgeInfo.class);
                TotalTwentykilometers_intent.putExtra("title",TotalTwentykilometers.getMyBadgeText());
                startActivity(TotalTwentykilometers_intent);
                break;
            case R.id.TotalFiftykilometers:
                Intent TotalFiftykilometers_intent=new Intent(getActivity(), ShowBadgeInfo.class);
                TotalFiftykilometers_intent.putExtra("title",TotalFiftykilometers.getMyBadgeText());
                startActivity(TotalFiftykilometers_intent);
                break;
            case R.id.TotalOneHundredkilometers:
                Intent TotalOneHundredkilometers_intent=new Intent(getActivity(), ShowBadgeInfo.class);
                TotalOneHundredkilometers_intent.putExtra("title",TotalOneHundredkilometers.getMyBadgeText());
                startActivity(TotalOneHundredkilometers_intent);
                break;
            case R.id.TotalFiveHundredkilometers:
                Intent TotalFiveHundredkilometers_intent=new Intent(getActivity(), ShowBadgeInfo.class);
                TotalFiveHundredkilometers_intent.putExtra("title",TotalFiveHundredkilometers.getMyBadgeText());
                startActivity(TotalFiveHundredkilometers_intent);
                break;
            case R.id.TotalThousandkilometers:
                Intent TotalThousandkilometers_intent=new Intent(getActivity(), ShowBadgeInfo.class);
                TotalThousandkilometers_intent.putExtra("title",TotalThousandkilometers.getMyBadgeText());
                startActivity(TotalThousandkilometers_intent);
                break;
        }
    }
}
