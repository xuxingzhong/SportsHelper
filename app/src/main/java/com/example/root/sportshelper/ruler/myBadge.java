package com.example.root.sportshelper.ruler;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.root.sportshelper.R;
import com.example.root.sportshelper.utils.DbHelper;
import com.example.root.sportshelper.utils.MiscUtil;

/**
 * 徽章
 * Created by root on 17-9-19.
 */

public class myBadge extends View{
    private String TAG="myBadge";
    private Boolean whetherGet;             //是否获得徽章
    private Bitmap picture;                 //文字背景图片
    private String myBadgeText;             //原始文字
    private String changeMyBadgeText;       //修改厚度文字
    private int myBadgeTextSize;            //文字大小
    private int myBadgeTextClor;            //文字颜色

    private TextPaint myBadgeTextPaint;     //文字画笔
    private Paint picturePaint;             //图片画笔
    private Typeface blackSimpleFont;          //方正兰亭黑简体

    private float viewHeight;   //控件高度
    private float viewWidth;   //控件宽度

    public myBadge(Context context){
        this(context,null);
    }

    public myBadge(Context context, AttributeSet attributeSet){
        this(context,attributeSet,0);
    }

    public myBadge(Context context,AttributeSet attributeSet,int defStyleAttr){
        super(context,attributeSet,defStyleAttr);
        TypedArray typedArray=context.obtainStyledAttributes(attributeSet, R.styleable.myBadge);
        myBadgeText=typedArray.getString(R.styleable.myBadge_myBadgeText);
        myBadgeTextSize=typedArray.getDimensionPixelSize(R.styleable.myBadge_myBadgeTextSize, MiscUtil.sp2px(context,10));
        myBadgeTextClor=typedArray.getColor(R.styleable.myBadge_myBadgeTextClor,getResources().getColor(R.color.color_80ffffff));


        initPaint(context);

        if(myBadgeText!=null){
            whetherGet= DbHelper.getwhetherGet(myBadgeText);
            picture= DbHelper.getBitmapFromWhetherGet(myBadgeText,whetherGet,context);
            String text[]=myBadgeText.split(",");
            changeMyBadgeText=text[0];
        }

    }

    private void initPaint(Context context) {
        blackSimpleFont= Typeface.createFromAsset(context.getAssets(),"fonts/Black_Simplified.ttf");
        myBadgeTextPaint = new TextPaint();
        // 设置抗锯齿,会消耗较大资源，绘制图形速度会变慢。
        myBadgeTextPaint.setAntiAlias(true);
        // 设置绘制文字大小
        myBadgeTextPaint.setTextSize(myBadgeTextSize);
        // 设置画笔颜色
        myBadgeTextPaint.setColor(myBadgeTextClor);
        // 从中间向两边绘制，不需要再次计算文字
        myBadgeTextPaint.setTextAlign(Paint.Align.CENTER);
        myBadgeTextPaint.setTypeface(blackSimpleFont);


        picturePaint = new Paint();
//        picturePaint.setAntiAlias(true);
//        // 设置画笔的样式，为FILL，FILL_OR_STROKE，或STROKE
//        picturePaint.setStyle(Paint.Style.STROKE);
//        // 设置画笔粗细
//        picturePaint.setStrokeWidth(myBadgeTextSize);
//        // 当画笔样式为STROKE或FILL_OR_STROKE时，设置笔刷的图形样式，如圆形样式
//        // Cap.ROUND,或方形样式 Cap.SQUARE
//        picturePaint.setStrokeCap(Paint.Cap.ROUND);
//        picturePaint.setColor(Color.TRANSPARENT);               //颜色为透明
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        viewWidth =MeasureSpec.getSize(widthMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawPicture(canvas);
        drawText(canvas);
    }

    //画图片
    private void drawPicture(Canvas canvas){
        canvas.drawBitmap(picture,0,0,picturePaint);
    }

    //画文字
    private void drawText(Canvas canvas){
        canvas.drawText(changeMyBadgeText,viewWidth/2,viewHeight/4*3,myBadgeTextPaint);
    }

    public String getMyBadgeText(){
        //返回原始文字
        return myBadgeText;
    }

    public String getChangeMyBadgeText(){
        //返回修改后文字
        return changeMyBadgeText;
    }

    public void setMyBadgeText(String badgeText){
        this.myBadgeText=badgeText;
        String text[]=myBadgeText.split(",");
        changeMyBadgeText=text[0];
    }
    public void setChangeMyBadgeText(String text){
        this.changeMyBadgeText=text;
    }

    public void setPicture(Bitmap bitmap){
        this.picture=bitmap;
    }
}
