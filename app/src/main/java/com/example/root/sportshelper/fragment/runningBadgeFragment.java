package com.example.root.sportshelper.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.root.sportshelper.R;
import com.example.root.sportshelper.ShowBadgeInfo;
import com.example.root.sportshelper.ruler.myBadge;

/**
 * 跑步徽章
 * Created by root on 17-8-25.
 */

public class runningBadgeFragment extends Fragment implements View.OnClickListener{

    private View rootView;
    private String TAG="StepCountBadgeFragment";

    myBadge SpeedFivekilometers;
    myBadge SpeedEightkilometers;
    myBadge SpeedTenkilometers;

    myBadge ChallengeFivekilometers;
    myBadge ChallengeTenkilometers;
    myBadge ChallengeTwentykilometers;
    myBadge ChallengeThirtykilometers;
    myBadge ChallengeFortykilometers;
    myBadge ChallengeFiftykilometers;

    myBadge TotalRunFiftykilometers;
    myBadge TotalRunOneHundredkilometers;
    myBadge TotalRunThreeHundredkilometers;
    myBadge TotalRunFiveHundredkilometers;
    myBadge TotalRunEightHundredkilometers;
    myBadge TotalRunThousandkilometers;

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
            rootView=inflater.inflate(R.layout.runningbadge_fragment,container,false);
            initView(rootView);

        }
        return rootView;
    }

    private void initView(View v){
        SpeedFivekilometers=(myBadge)v.findViewById(R.id.SpeedFivekilometers);
        SpeedFivekilometers.setOnClickListener(this);

        SpeedEightkilometers=(myBadge)v.findViewById(R.id.SpeedEightkilometers);
        SpeedEightkilometers.setOnClickListener(this);

        SpeedTenkilometers=(myBadge)v.findViewById(R.id.SpeedTenkilometers);
        SpeedTenkilometers.setOnClickListener(this);

        ChallengeFivekilometers=(myBadge)v.findViewById(R.id.ChallengeFivekilometers);
        ChallengeFivekilometers.setOnClickListener(this);

        ChallengeTenkilometers=(myBadge)v.findViewById(R.id.ChallengeTenkilometers);
        ChallengeTenkilometers.setOnClickListener(this);

        ChallengeTwentykilometers=(myBadge)v.findViewById(R.id.ChallengeTwentykilometers);
        ChallengeTwentykilometers.setOnClickListener(this);

        ChallengeThirtykilometers=(myBadge)v.findViewById(R.id.ChallengeThirtykilometers);
        ChallengeThirtykilometers.setOnClickListener(this);

        ChallengeFortykilometers=(myBadge)v.findViewById(R.id.ChallengeFortykilometers);
        ChallengeFortykilometers.setOnClickListener(this);

        ChallengeFiftykilometers=(myBadge)v.findViewById(R.id.ChallengeFiftykilometers);
        ChallengeFiftykilometers.setOnClickListener(this);

        TotalRunFiftykilometers=(myBadge)v.findViewById(R.id.TotalRunFiftykilometers);
        TotalRunFiftykilometers.setOnClickListener(this);

        TotalRunOneHundredkilometers=(myBadge)v.findViewById(R.id.TotalRunOneHundredkilometers);
        TotalRunOneHundredkilometers.setOnClickListener(this);

        TotalRunThreeHundredkilometers=(myBadge)v.findViewById(R.id.TotalRunThreeHundredkilometers);
        TotalRunThreeHundredkilometers.setOnClickListener(this);

        TotalRunFiveHundredkilometers=(myBadge)v.findViewById(R.id.TotalRunFiveHundredkilometers);
        TotalRunFiveHundredkilometers.setOnClickListener(this);

        TotalRunEightHundredkilometers=(myBadge)v.findViewById(R.id.TotalRunEightHundredkilometers);
        TotalRunEightHundredkilometers.setOnClickListener(this);

        TotalRunThousandkilometers=(myBadge)v.findViewById(R.id.TotalRunThousandkilometers);
        TotalRunThousandkilometers.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.SpeedFivekilometers:
                Intent intent=new Intent(getActivity(), ShowBadgeInfo.class);
                intent.putExtra("title",SpeedFivekilometers.getMyBadgeText());
                startActivity(intent);
                break;
            case R.id.SpeedEightkilometers:
                Intent SpeedEightkilometers_intent=new Intent(getActivity(), ShowBadgeInfo.class);
                SpeedEightkilometers_intent.putExtra("title",SpeedEightkilometers.getMyBadgeText());
                startActivity(SpeedEightkilometers_intent);
                break;
            case R.id.SpeedTenkilometers:
                Intent SpeedTenkilometers_intent=new Intent(getActivity(), ShowBadgeInfo.class);
                SpeedTenkilometers_intent.putExtra("title",SpeedTenkilometers.getMyBadgeText());
                startActivity(SpeedTenkilometers_intent);
                break;
            case R.id.ChallengeFivekilometers:
                Intent ChallengeFivekilometers_intent=new Intent(getActivity(), ShowBadgeInfo.class);
                ChallengeFivekilometers_intent.putExtra("title",ChallengeFivekilometers.getMyBadgeText());
                startActivity(ChallengeFivekilometers_intent);
                break;
            case R.id.ChallengeTenkilometers:
                Intent ChallengeTenkilometers_intent=new Intent(getActivity(), ShowBadgeInfo.class);
                ChallengeTenkilometers_intent.putExtra("title",ChallengeTenkilometers.getMyBadgeText());
                startActivity(ChallengeTenkilometers_intent);
                break;
            case R.id.ChallengeTwentykilometers:
                Intent ChallengeTwentykilometers_intent=new Intent(getActivity(), ShowBadgeInfo.class);
                ChallengeTwentykilometers_intent.putExtra("title",ChallengeTwentykilometers.getMyBadgeText());
                startActivity(ChallengeTwentykilometers_intent);
                break;
            case R.id.ChallengeThirtykilometers:
                Intent ChallengeThirtykilometers_intent=new Intent(getActivity(), ShowBadgeInfo.class);
                ChallengeThirtykilometers_intent.putExtra("title",ChallengeThirtykilometers.getMyBadgeText());
                startActivity(ChallengeThirtykilometers_intent);
                break;
            case R.id.ChallengeFortykilometers:
                Intent ChallengeFortykilometers_intent=new Intent(getActivity(), ShowBadgeInfo.class);
                ChallengeFortykilometers_intent.putExtra("title",ChallengeFortykilometers.getMyBadgeText());
                startActivity(ChallengeFortykilometers_intent);
                break;
            case R.id.ChallengeFiftykilometers:
                Intent ChallengeFiftykilometers_intent=new Intent(getActivity(), ShowBadgeInfo.class);
                ChallengeFiftykilometers_intent.putExtra("title",ChallengeFiftykilometers.getMyBadgeText());
                startActivity(ChallengeFiftykilometers_intent);
                break;
            case R.id.TotalRunFiftykilometers:
                Intent TotalRunFiftykilometers_intent=new Intent(getActivity(), ShowBadgeInfo.class);
                TotalRunFiftykilometers_intent.putExtra("title",TotalRunFiftykilometers.getMyBadgeText());
                startActivity(TotalRunFiftykilometers_intent);
                break;
            case R.id.TotalRunOneHundredkilometers:
                Intent TotalRunOneHundredkilometers_intent=new Intent(getActivity(), ShowBadgeInfo.class);
                TotalRunOneHundredkilometers_intent.putExtra("title",TotalRunOneHundredkilometers.getMyBadgeText());
                startActivity(TotalRunOneHundredkilometers_intent);
                break;
            case R.id.TotalRunThreeHundredkilometers:
                Intent TotalRunThreeHundredkilometers_intent=new Intent(getActivity(), ShowBadgeInfo.class);
                TotalRunThreeHundredkilometers_intent.putExtra("title",TotalRunThreeHundredkilometers.getMyBadgeText());
                startActivity(TotalRunThreeHundredkilometers_intent);
                break;
            case R.id.TotalRunFiveHundredkilometers:
                Intent TotalRunFiveHundredkilometers_intent=new Intent(getActivity(), ShowBadgeInfo.class);
                TotalRunFiveHundredkilometers_intent.putExtra("title",TotalRunFiveHundredkilometers.getMyBadgeText());
                startActivity(TotalRunFiveHundredkilometers_intent);
                break;
            case R.id.TotalRunEightHundredkilometers:
                Intent TotalRunEightHundredkilometers_intent=new Intent(getActivity(), ShowBadgeInfo.class);
                TotalRunEightHundredkilometers_intent.putExtra("title",TotalRunEightHundredkilometers.getMyBadgeText());
                startActivity(TotalRunEightHundredkilometers_intent);
                break;
            case R.id.TotalRunThousandkilometers:
                Intent TotalRunThousandkilometers_intent=new Intent(getActivity(), ShowBadgeInfo.class);
                TotalRunThousandkilometers_intent.putExtra("title",TotalRunThousandkilometers.getMyBadgeText());
                startActivity(TotalRunThousandkilometers_intent);
                break;
        }
    }
}
