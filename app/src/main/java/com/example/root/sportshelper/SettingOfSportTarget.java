package com.example.root.sportshelper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.root.sportshelper.ruler.OnStateChangeListener;
import com.example.root.sportshelper.ruler.RulerView;
import com.example.root.sportshelper.ruler.StepsTarget;
import com.example.root.sportshelper.utils.DbHelper;

import java.text.DecimalFormat;

public class SettingOfSportTarget extends AppCompatActivity implements View.OnClickListener,OnStateChangeListener {
    private String TAG="SettingOfSportTarget";
    TextView title;
    ImageView back;

    private  int sex;                //1为男；-1为女
    private int bodyHeight;          //身高
    private int bodyWeight;          //体重
    private int bodyAge;            //年龄
    private int suggestStep;            //推荐步数
    TextView BMI;                    //体型标准
    private double BMIText;         //BMI值
    StepsTarget stepsTarget;
    TextView suggestStepTarget;       //步数目标
    TextView kilometer;             //千里
    TextView calorie;               //卡路里
    TextView time;                  //花费时间
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_of_sport_target);

        initView();
        getInfo();
        setBMIText();

        suggestStep=stepRecommend();
        suggestStepTarget.setText(suggestStep+"");
        stepsTarget.setProgressValue(suggestStep);
        setKilometer(suggestStep);
        setcalorie(suggestStep);
        settime(suggestStep);
    }

    private void initView(){
        title=(TextView)findViewById(R.id.title);
        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(this);

        title.setText("运动目标");

        suggestStepTarget=(TextView)findViewById(R.id.suggestStep);
        stepsTarget=(StepsTarget)findViewById(R.id.stepsTarget);
        kilometer=(TextView)findViewById(R.id.Kilometer);
        calorie=(TextView)findViewById(R.id.calorie);
        time=(TextView)findViewById(R.id.time);
        stepsTarget.setOnStateChangeListener(this);


    }

    private void getInfo(){
        sex=DbHelper.getbodySex();
        bodyHeight=DbHelper.getbodyHeight();
        bodyWeight=DbHelper.getbodyWeight();
        bodyAge=DbHelper.getbodyAge();
    }

    private void setBMIText(){
        BMI=(TextView)findViewById(R.id.BMI);
        double BMIText=bodyWeight/Math.pow((double)bodyHeight/100,2);
        DecimalFormat df = new DecimalFormat("0.00");//格式化小数，不足的补0
        String finalBMIText = df.format(BMIText);//返回的是String类型的
        BMI.setText("体型标准（BMI："+finalBMIText+"）");
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.back:
                if(DbHelper.isUpdateRTStepTarget(Integer.parseInt(suggestStepTarget.getText().toString()))){
                    Log.i(TAG, "运动目标改变");
                }
                finish();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(DbHelper.isUpdateRTStepTarget(Integer.parseInt(suggestStepTarget.getText().toString()))){
                Log.i(TAG, "运动目标改变");
            }
            finish();
        }
        //返回值，该方法的返回值为一个boolean类型的变量，当返回true时，表示已经完整地处理了这个事件，并不希望其他的回调方法再次进行处理，
        // 而当返回false时，表示并没有完全处理完该事件，更希望其他回调方法继续对其进行处理，例如Activity中的回调方法。
        return true;
    }

    private void setKilometer(int progress){
        double KM=(progress/1000)*((double)bodyHeight)/100*0.45;
        DecimalFormat df = new DecimalFormat("0.00");//格式化小数，不足的补0
        String finalKM = df.format(KM);//返回的是String类型的
        kilometer.setText(finalKM+"公里");
    }

    private void setcalorie(int progress){
        //运动多少千米，现将步数转换为千米
        double KM=(progress/1000)*((double)bodyHeight)/100*0.45;
        double costcalorie=bodyWeight*1.036*KM;
//        DecimalFormat df = new DecimalFormat("0.00");//格式化小数，不足的补0
//        String finalcalorie = df.format(costcalorie);//返回的是String类型的
        double finalcalorie=Math.rint(costcalorie);
        calorie.setText((int)finalcalorie+"大卡");
    }

    private void settime(int progress){
        double costTime=(progress/1000)*5;
        double x=costTime/60;
        DecimalFormat df = new DecimalFormat("0.00");//格式化小数，不足的补0
        String finalTime = df.format(x);//返回的是String类型的
        time.setText(finalTime+"小时");
    }

    @Override
    public void OnStateChangeListener(int progress) {
        suggestStepTarget.setText(progress+"");
        setKilometer(progress);
        setcalorie(progress);
        settime(progress);
    }

    @Override
    public void onStopTrackingTouch(int progress) {
        suggestStepTarget.setText(progress+"");
        setKilometer(progress);
        setcalorie(progress);
        settime(progress);
    }

    private int stepRecommend(){
        int step=0;
        if(sex==1){
            if(BMIText<18.5){
                if(bodyAge<=17){
                    step=5000;
                }else if(bodyAge<=40){
                    step=8000;
                }else if(bodyAge<=65){
                    step=6000;
                }else if(bodyAge<=84){
                    step=3000;
                }else if(bodyAge>=85){
                    step=2000;
                }
            }else if(BMIText<=23.9){
                if(bodyAge<=17){
                    step=6000;
                }else if(bodyAge<=40){
                    step=9000;
                }else if(bodyAge<=65){
                    step=6000;
                }else if(bodyAge<=84){
                    step=4000;
                }else if(bodyAge>=85){
                    step=2000;
                }
            }else if(BMIText<=27.9){
                if(bodyAge<=17){
                    step=8000;
                }else if(bodyAge<=40){
                    step=11000;
                }else if(bodyAge<=65){
                    step=8000;
                }else if(bodyAge<=84){
                    step=6000;
                }else if(bodyAge>=85){
                    step=3000;
                }
            }else if(BMIText>=28){
                if(bodyAge<=17){
                    step=10000;
                }else if(bodyAge<=40){
                    step=12000;
                }else if(bodyAge<=65){
                    step=10000;
                }else if(bodyAge<=84){
                    step=6000;
                }else if(bodyAge>=85){
                    step=3000;
                }
            }
        }else if(sex==-1){
            if(BMIText<18.5){
                if(bodyAge<=17){
                    step=4000;
                }else if(bodyAge<=40){
                    step=6000;
                }else if(bodyAge<=65){
                    step=5000;
                }else if(bodyAge<=84){
                    step=2000;
                }else if(bodyAge>=85){
                    step=2000;
                }
            }else if(BMIText<=23.9){
                if(bodyAge<=17){
                    step=5000;
                }else if(bodyAge<=40){
                    step=7000;
                }else if(bodyAge<=65){
                    step=6000;
                }else if(bodyAge<=84){
                    step=3000;
                }else if(bodyAge>=85){
                    step=2000;
                }
            }else if(BMIText<=27.9){
                if(bodyAge<=17){
                    step=8000;
                }else if(bodyAge<=40){
                    step=9000;
                }else if(bodyAge<=65){
                    step=8000;
                }else if(bodyAge<=84){
                    step=5000;
                }else if(bodyAge>=85){
                    step=2000;
                }
            }else if(BMIText>=28){
                if(bodyAge<=17){
                    step=80000;
                }else if(bodyAge<=40){
                    step=10000;
                }else if(bodyAge<=65){
                    step=8000;
                }else if(bodyAge<=84){
                    step=5000;
                }else if(bodyAge>=85){
                    step=2000;
                }
            }
        }
        return step;
    }
}
