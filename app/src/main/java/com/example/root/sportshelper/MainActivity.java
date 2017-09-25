package com.example.root.sportshelper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.root.sportshelper.ruler.OnRulerChangeListener;
import com.example.root.sportshelper.ruler.RulerView;

public class MainActivity extends AppCompatActivity implements OnRulerChangeListener ,View.OnClickListener{
    LinearLayout man;
    LinearLayout women;
    TextView manTextView;
    TextView womenTextView;
    ImageView manImage;
    ImageView womenImage;
    TextView bodyHeight;
    TextView bodyWeight;
    TextView bodyAge;
    RulerView rulerView_BodyHeight;
    RulerView rulerView_BodyWeight;
    RulerView rulerView_BodyAge;
    public int bodyHeightCurrLocation=170;
    private int bodyWeightCurrLocation=60 ;
    private int bodyAgeCurrLocation=30 ;
    LinearLayout next;
    private boolean isFirstIn = false;
    public static MainActivity instance=null;
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance=this;

        SharedPreferences sharedPreferences=getSharedPreferences("first_pref",MODE_PRIVATE);
        isFirstIn=sharedPreferences.getBoolean("isFirstIn", true);      //是否为第一次登录
        if(isFirstIn==false){
            Intent intent=new Intent(MainActivity.this,SportMain.class);
            startActivity(intent);
            finish();
        }

        man=(LinearLayout)findViewById(R.id.man);
        women=(LinearLayout)findViewById(R.id.women);
        manTextView=(TextView)findViewById(R.id.manTextView);
        womenTextView=(TextView)findViewById(R.id.womenTextView);
        manImage=(ImageView)findViewById(R.id.manImage);
        womenImage=(ImageView)findViewById(R.id.womenImage);
        man.setOnClickListener(this);
        women.setOnClickListener(this);

        rulerView_BodyHeight = (RulerView) findViewById(R.id.rulerView_BodyHeight);
        rulerView_BodyHeight.setOnRulerChangeListener(this);
        rulerView_BodyHeight.setCurrLocation(bodyHeightCurrLocation);
        bodyHeight = (TextView) findViewById(R.id.bodyHeight);
        bodyHeight.setText(bodyHeightCurrLocation + "");

        rulerView_BodyWeight = (RulerView) findViewById(R.id.rulerView_BodyWeight);
        rulerView_BodyWeight.setOnRulerChangeListener(this);
        rulerView_BodyWeight.setCurrLocation(bodyWeightCurrLocation);
        bodyWeight = (TextView) findViewById(R.id.bodyWeight);
        bodyWeight.setText(bodyWeightCurrLocation + "");

        rulerView_BodyAge = (RulerView) findViewById(R.id.rulerView_BodyAge);
        rulerView_BodyAge.setOnRulerChangeListener(this);
        rulerView_BodyAge.setCurrLocation(bodyAgeCurrLocation);
        bodyAge = (TextView) findViewById(R.id.bodyAge);
        bodyAge.setText(bodyAgeCurrLocation + "");

        next=(LinearLayout)findViewById(R.id.next);
        next.setOnClickListener(this);
    }
    @Override
    public void onChanged(int id,int newValue) {
        switch (id){
            case 1:
                bodyHeight.setText(newValue+"");
                break;
            case 2:
                bodyWeight.setText(newValue+"");
                break;
            case 3:
                bodyAge.setText(newValue+"");
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.man:
                manTextView.setTextColor(getResources().getColor(R.color.color_2387f5));
                womenTextView.setTextColor(getResources().getColor(R.color.color_ff333333));
                manImage.setBackgroundDrawable(getResources().getDrawable(R.drawable.backgroudman));
                womenImage.setBackgroundDrawable(getResources().getDrawable(R.drawable.backgroundunselected));
                break;
            case R.id.women:
                manTextView.setTextColor(getResources().getColor(R.color.color_ff333333));
                womenTextView.setTextColor(getResources().getColor(R.color.color_ff3971));
                manImage.setBackgroundDrawable(getResources().getDrawable(R.drawable.backgroundunselected));
                womenImage.setBackgroundDrawable(getResources().getDrawable(R.drawable.backgroundwoman));
                break;
            case R.id.next:
                Intent sportTarget=new Intent(MainActivity.this,SportTarget.class);
                int sex=1;//性别：1为男；0为女
                if (manTextView.getCurrentTextColor()==getResources().getColor(R.color.color_ff333333) ){
                    sex=0;
                }else{
                    sex=1;
                }
                int height =Integer.parseInt(bodyHeight.getText().toString());
                int weight =Integer.parseInt(bodyWeight.getText().toString());
                int age =Integer.parseInt(bodyAge.getText().toString());
                sportTarget.putExtra("sex",sex);
                sportTarget.putExtra("bodyHeight",height);
                sportTarget.putExtra("bodyWeight",weight);
                sportTarget.putExtra("bodyAge",age);
                startActivity(sportTarget);
                break;
            default:
                break;
        }
    }
}
