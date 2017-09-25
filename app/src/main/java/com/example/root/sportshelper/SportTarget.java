package com.example.root.sportshelper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.ColorSpace;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.root.sportshelper.database.PersonInfo;
import com.example.root.sportshelper.ruler.OnStateChangeListener;
import com.example.root.sportshelper.ruler.StepsTarget;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.text.DecimalFormat;

public class SportTarget extends AppCompatActivity implements OnStateChangeListener,View.OnClickListener {
    private  int sex;            //1为男；-1为女
    private int bodyHeight;     //身高
    private int bodyWeight;     //体重
    private int bodyAge;       //年龄
    private int suggestStep;    //推荐步数
    TextView BMI;               //体型标准
    private double BMIText;     //BMI值
    StepsTarget stepsTarget;
    TextView suggestStepTarget;       //步数目标
    TextView kilometer;             //千里
    TextView calorie;               //卡路里
    TextView time;                  //花费时间
    LinearLayout previous;          //上一步
    LinearLayout done;              //完成

    private String TAG="SportTarget";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sport_target);
        suggestStepTarget=(TextView)findViewById(R.id.suggestStep);
        stepsTarget=(StepsTarget)findViewById(R.id.stepsTarget);
        kilometer=(TextView)findViewById(R.id.Kilometer);
        calorie=(TextView)findViewById(R.id.calorie);
        time=(TextView)findViewById(R.id.time);
        previous=(LinearLayout)findViewById(R.id.previous);
        done=(LinearLayout)findViewById(R.id.done);
        stepsTarget.setOnStateChangeListener(this);
        getInfo();
        setBMIText();
        databaseOperation();
        suggestStep=stepRecommend();
        suggestStepTarget.setText(suggestStep+"");
        stepsTarget.setProgressValue(suggestStep);
        setKilometer(suggestStep);
        setcalorie(suggestStep);
        settime(suggestStep);
        previous.setOnClickListener(this);
        done.setOnClickListener(this);
    }

    private void getInfo(){
        Intent intent=getIntent();
        sex=intent.getIntExtra("sex",1);
        bodyHeight=intent.getIntExtra("bodyHeight",170);
        bodyWeight=intent.getIntExtra("bodyWeight",60);
        bodyAge=intent.getIntExtra("bodyAge",30);
    }

    private void setBMIText(){
        BMI=(TextView)findViewById(R.id.BMI);
        double BMIText=bodyWeight/Math.pow((double)bodyHeight/100,2);
        DecimalFormat df = new DecimalFormat("0.00");//格式化小数，不足的补0
        String finalBMIText = df.format(BMIText);//返回的是String类型的
        BMI.setText("体型标准（BMI："+finalBMIText+"）");
    }

    private void databaseOperation(){
        Connector.getDatabase();
        PersonInfo personInfo=new PersonInfo();
        personInfo.setSex(sex);
        personInfo.setBodyHeight(bodyHeight);
        personInfo.setBodyWeight(bodyWeight);
        personInfo.setBodyAge(bodyAge);
        if(tableExist()){
            personInfo.updateAll();
        }else {
            personInfo.save();
        }
    }

    private Boolean tableExist(){
        PersonInfo firstPersonInfo= DataSupport.findFirst(PersonInfo.class);
        if (firstPersonInfo!=null){
            return true;
        }else {
            return false;
        }
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
        }else if(sex==0){
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.previous:
                finish();
                break;
            case R.id.done:
                Intent sportMain=new Intent(SportTarget.this, SportMain.class);
                int step_target=Integer.parseInt(suggestStepTarget.getText().toString());
                PersonInfo personInfo=new PersonInfo();
                personInfo.setRTStepTarget(step_target);
                personInfo.update(1);
                startActivity(sportMain);
                SharedPreferences.Editor editor=getSharedPreferences("first_pref",MODE_PRIVATE).edit();
                editor.putBoolean("isFirstIn", false);
                editor.apply();
                this.finish();
                MainActivity.instance.finish();
                break;
            default:
                break;
        }
    }
}
