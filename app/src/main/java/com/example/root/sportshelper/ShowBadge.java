package com.example.root.sportshelper;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.root.sportshelper.fragment.SportStepCountFragment;
import com.example.root.sportshelper.fragment.StepCountBadgeFragment;
import com.example.root.sportshelper.fragment.runningBadgeFragment;
import com.example.root.sportshelper.utils.MiscUtil;
//徽章界面
public class ShowBadge extends AppCompatActivity implements View.OnClickListener{
    ImageView back;
    TextView StepCountBadge;                                //计步徽章
    TextView runningBadge;                                  //跑步徽章
    StepCountBadgeFragment stepCountBadgeFragment;          //计步徽章fragment
    runningBadgeFragment  runningBadgeFragment;             //跑步徽章fragment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_badge);
        initView();
        setDefaultFragment();
    }

    private void initView(){
        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(this);
        StepCountBadge=(TextView)findViewById(R.id.StepCountBadge);
        runningBadge=(TextView)findViewById(R.id.runningBadge);
        StepCountBadge.setOnClickListener(this);
        runningBadge.setOnClickListener(this);
    }

    private void setDefaultFragment()
    {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        stepCountBadgeFragment = new StepCountBadgeFragment();
        transaction.replace(R.id.contentFragment, stepCountBadgeFragment);
        transaction.commit();
    }

    @Override
    public void onClick(View view) {
        FragmentManager fm=getFragmentManager();
        FragmentTransaction transaction=fm.beginTransaction();
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.StepCountBadge:
                if(!MiscUtil.isFastClick()){
                    StepCountBadge.setTextColor(getResources().getColor(R.color.color_ffffffff));
                    runningBadge.setTextColor(getResources().getColor(R.color.color_C0C0C0));
                    if(stepCountBadgeFragment==null){
                        stepCountBadgeFragment = new StepCountBadgeFragment();
                    }
                    transaction.replace(R.id.contentFragment,stepCountBadgeFragment);
                }
                break;
            case R.id.runningBadge:
                if(!MiscUtil.isFastClick()){
                    StepCountBadge.setTextColor(getResources().getColor(R.color.color_C0C0C0));
                    runningBadge.setTextColor(getResources().getColor(R.color.color_ffffffff));
                    if(runningBadgeFragment==null){
                        runningBadgeFragment = new runningBadgeFragment();
                    }
                    transaction.replace(R.id.contentFragment,runningBadgeFragment);
                }
                break;
        }
        // 事务提交
        transaction.commit();
    }
}
