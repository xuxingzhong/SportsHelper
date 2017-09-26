package com.example.root.sportshelper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.root.sportshelper.ruler.myBadge;
import com.example.root.sportshelper.utils.DbHelper;

//点击后显示徽章的具体信息
public class ShowBadgeInfo extends Activity {
    private String TAG="ShowBadgeInfo";
    myBadge myBadgeLable;
    TextView describeText;
    TextView whetherGetText;

    private String title;                   //标题
    private String describe;                //描述
    private Boolean whetherGet;             //是否获得
    private Bitmap picture;                 //背景图片
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_badge_info);

        initInfo();
        initView();
    }

    private void initInfo(){
        Intent intent=getIntent();
        title=intent.getStringExtra("title");
        describe= DbHelper.getDescribe(title,this);
        whetherGet=DbHelper.getwhetherGet(title);
        picture=DbHelper.getBitmapFromWhetherGet(title,whetherGet,this);
    }

    private void initView(){
        myBadgeLable=(myBadge)findViewById(R.id.myBadgeLable);
        describeText=(TextView)findViewById(R.id.describeText);
        whetherGetText=(TextView)findViewById(R.id.whetherGetText);

        myBadgeLable.setPicture(picture);
        myBadgeLable.setMyBadgeText(title);

        describeText.setText(describe);
        if(whetherGet){
            whetherGetText.setText(getResources().getString(R.string.get));
        }else {
            whetherGetText.setText(getResources().getString(R.string.noGet));
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
