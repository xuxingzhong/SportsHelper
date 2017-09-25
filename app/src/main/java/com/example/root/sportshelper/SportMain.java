package com.example.root.sportshelper;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.root.sportshelper.fragment.RunningFragment;
import com.example.root.sportshelper.fragment.SportStepCountFragment;
import com.example.root.sportshelper.utils.MiscUtil;


public class SportMain extends AppCompatActivity implements View.OnClickListener {
    ImageView badge;                //徽章
    ImageView setting;              //设置
    TextView sportStepCount;        //计步
    TextView running;               //跑步
    SportStepCountFragment sportStepCountFragment;      //计步fragment
    RunningFragment runningFragment;                    //跑步fragment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sport_main);
        initView();
        setDefaultFragment();
    }

    private void initView(){
        badge=(ImageView)findViewById(R.id.badge);
        setting=(ImageView)findViewById(R.id.Setting);
        sportStepCount=(TextView)findViewById(R.id.sport_step_count);
        running=(TextView)findViewById(R.id.running);
        badge.setOnClickListener(this);
        setting.setOnClickListener(this);
        sportStepCount.setOnClickListener(this);
        running.setOnClickListener(this);
    }

    private void setDefaultFragment()
    {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        sportStepCountFragment = new SportStepCountFragment();
        transaction.replace(R.id.contentFragment, sportStepCountFragment);
        transaction.commit();
    }

    @Override
    public void onClick(View view) {
        FragmentManager fm=getFragmentManager();
        FragmentTransaction transaction=fm.beginTransaction();

        switch (view.getId()){
            case R.id.sport_step_count:
                if(!MiscUtil.isFastClick()){
                    sportStepCount.setTextColor(getResources().getColor(R.color.color_ffffffff));
                    running.setTextColor(getResources().getColor(R.color.color_C0C0C0));
                    if(sportStepCountFragment==null){
                        sportStepCountFragment = new SportStepCountFragment();
                    }
                    transaction.replace(R.id.contentFragment,sportStepCountFragment);
                }
                break;
            case R.id.running:
                if(!MiscUtil.isFastClick()){
                    sportStepCount.setTextColor(getResources().getColor(R.color.color_C0C0C0));
                    running.setTextColor(getResources().getColor(R.color.color_ffffffff));
                    if(runningFragment==null){
                        runningFragment = new RunningFragment();
                    }
                    transaction.replace(R.id.contentFragment,runningFragment);
                }
                break;
            case R.id.Setting:
                Intent Setting=new Intent(this,Setting.class);
                startActivity(Setting);
                break;
            case R.id.badge:
                Intent ShowBadge=new Intent(this,ShowBadge.class);
                startActivity(ShowBadge);
                break;
        }
        // 事务提交
        transaction.commit();
    }

    @Override
    public void onBackPressed() {        //android中后退键
        moveTaskToBack(true);       //将activity 退到后台，注意不是finish()退出
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
