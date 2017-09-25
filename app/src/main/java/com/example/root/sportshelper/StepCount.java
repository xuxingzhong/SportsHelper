package com.example.root.sportshelper;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.root.sportshelper.fragment.DayFragment;
import com.example.root.sportshelper.fragment.MonthFragment;
import com.example.root.sportshelper.fragment.WeekFragment;

import com.example.root.sportshelper.utils.MiscUtil;
import com.example.root.sportshelper.utils.MyTime;


public class StepCount extends AppCompatActivity implements View.OnClickListener{
    TextView title;
    ImageView back;
    TextView day;                           //日的按钮
    TextView week;                          //周的按钮
    TextView month;                         //月的按钮
    private DayFragment dayFragment;        //日
    private WeekFragment weekFragment;      //周
    private MonthFragment monthFragment;    //月

    private int id;         //点击记录的id
    private String TAG="StepCount";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step_count);
        title=(TextView)findViewById(R.id.title);
        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(this);

        day=(TextView)findViewById(R.id.day);
        week=(TextView)findViewById(R.id.week);
        month=(TextView)findViewById(R.id.month);
        day.setOnClickListener(this);
        week.setOnClickListener(this);
        month.setOnClickListener(this);
        getInfo();
        title.setText("步数统计");

        // 设置默认的Fragment
        setDefaultFragment();

    }

    private void getInfo(){
        Intent intent=getIntent();
        id=intent.getIntExtra("id",1);
    }

    private void setDefaultFragment()
    {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        dayFragment = new DayFragment();
        transaction.replace(R.id.DayWeekMonth, dayFragment);
        transaction.commit();
    }

    @Override
    public void onClick(View view) {
        FragmentManager fm=getFragmentManager();
        FragmentTransaction transaction=fm.beginTransaction();

        switch (view.getId()){
            case R.id.back:
                Intent intent=new Intent();
                intent.putExtra("continueOrStop","myContinue");
                setResult(RESULT_OK,intent);
                finish();
                break;
            case R.id.day:
                if(!MiscUtil.isFastClick()){
                    MyTime.resetWeekNumberAndMonthNumber();
                    day.setTextColor(getResources().getColor(R.color.color_2387f5));
                    week.setTextColor(getResources().getColor(R.color.color_ffffffff));
                    month.setTextColor(getResources().getColor(R.color.color_ffffffff));
                    day.setBackgroundDrawable(getResources().getDrawable(R.drawable.select_shape));
                    week.setBackgroundDrawable(getResources().getDrawable(R.drawable.unselect_shape));
                    month.setBackgroundDrawable(getResources().getDrawable(R.drawable.unselect_shape));
                    if(dayFragment==null){
                        dayFragment=new DayFragment();
                    }
                    transaction.replace(R.id.DayWeekMonth,dayFragment);
                }
                break;
            case R.id.week:
                if(!MiscUtil.isFastClick()){
                    MyTime.resetWeekNumberAndMonthNumber();
                    day.setTextColor(getResources().getColor(R.color.color_ffffffff));
                    week.setTextColor(getResources().getColor(R.color.color_2387f5));
                    month.setTextColor(getResources().getColor(R.color.color_ffffffff));
                    day.setBackgroundDrawable(getResources().getDrawable(R.drawable.unselect_shape));
                    week.setBackgroundDrawable(getResources().getDrawable(R.drawable.select_shape));
                    month.setBackgroundDrawable(getResources().getDrawable(R.drawable.unselect_shape));
                    if(weekFragment==null){
                        weekFragment=new WeekFragment();
                    }
                    transaction.replace(R.id.DayWeekMonth,weekFragment);
                }
                break;
            case R.id.month:
                if(!MiscUtil.isFastClick()){
                    MyTime.resetWeekNumberAndMonthNumber();
                    day.setTextColor(getResources().getColor(R.color.color_ffffffff));
                    week.setTextColor(getResources().getColor(R.color.color_ffffffff));
                    month.setTextColor(getResources().getColor(R.color.color_2387f5));
                    day.setBackgroundDrawable(getResources().getDrawable(R.drawable.unselect_shape));
                    week.setBackgroundDrawable(getResources().getDrawable(R.drawable.unselect_shape));
                    month.setBackgroundDrawable(getResources().getDrawable(R.drawable.select_shape));
                    if(monthFragment==null){
                        monthFragment=new MonthFragment();
                    }
                    transaction.replace(R.id.DayWeekMonth,monthFragment);
                }
                break;
        }
        // 事务提交
        transaction.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            Intent intent=new Intent();
                intent.putExtra("continueOrStop","myContinue");
            setResult(RESULT_OK,intent);
            finish();
        }
        return false;
    }
}
