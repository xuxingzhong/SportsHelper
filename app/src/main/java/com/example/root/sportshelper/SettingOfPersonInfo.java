package com.example.root.sportshelper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.root.sportshelper.database.PersonInfo;
import com.example.root.sportshelper.ruler.OnRulerChangeListener;
import com.example.root.sportshelper.ruler.RulerView;
import com.example.root.sportshelper.utils.DbHelper;

public class SettingOfPersonInfo extends AppCompatActivity implements View.OnClickListener,OnRulerChangeListener {
    TextView title;
    ImageView back;

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

    private String TAG="SettingOfPersonInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_of_person_info);

        initView();
    }

    private void initView(){
        title=(TextView)findViewById(R.id.title);
        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(this);

        title.setText("个人信息");

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
    }

    @Override
    public void onChanged(int id, int newValue) {
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
            case R.id.back:
                int sex=1;//性别：1为男；-1为女
                if (manTextView.getCurrentTextColor()==getResources().getColor(R.color.color_ff333333) ){
                    sex=-1;
                }else{
                    sex=1;
                }
                if(DbHelper.isUpdateSex(sex)){
                    Log.i(TAG, "性别改变");
                }
                if(DbHelper.isUpdateHeight(Integer.parseInt(bodyHeight.getText().toString()))){
                    Log.i(TAG, "身高改变");
                }
                if(DbHelper.isUpdateWeight(Integer.parseInt(bodyWeight.getText().toString()))){
                    Log.i(TAG, "体重改变");
                }
                if(DbHelper.isUpdateAge(Integer.parseInt(bodyAge.getText().toString()))){
                    Log.i(TAG, "年龄改变");
                }
                finish();
                break;
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
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            int sex=1;//性别：1为男；-1为女
            if (manTextView.getCurrentTextColor()==getResources().getColor(R.color.color_ff333333) ){
                sex=-1;
            }else{
                sex=1;
            }
            if(DbHelper.isUpdateSex(sex)){
                Log.i(TAG, "性别改变");
            }
            if(DbHelper.isUpdateHeight(Integer.parseInt(bodyHeight.getText().toString()))){
                Log.i(TAG, "身高改变");
            }
            if(DbHelper.isUpdateWeight(Integer.parseInt(bodyWeight.getText().toString()))){
                Log.i(TAG, "体重改变");
            }
            if(DbHelper.isUpdateAge(Integer.parseInt(bodyAge.getText().toString()))){
                Log.i(TAG, "年龄改变");
            }
            finish();
        }
        //返回值，该方法的返回值为一个boolean类型的变量，当返回true时，表示已经完整地处理了这个事件，并不希望其他的回调方法再次进行处理，
        // 而当返回false时，表示并没有完全处理完该事件，更希望其他回调方法继续对其进行处理，例如Activity中的回调方法。
        return true;
    }
}
