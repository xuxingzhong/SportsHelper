package com.example.root.sportshelper.ruler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.root.sportshelper.R;
import com.example.root.sportshelper.database.PersonInfo;
import com.example.root.sportshelper.utils.MiscUtil;

import org.litepal.crud.DataSupport;

import java.text.DecimalFormat;

/**
 * Created by root on 17-7-31.
 */

public class WeightLine extends View  {
    private int thinnish;       //偏瘦
    private int criterion;      //标准
    private int slightlyFat;    //偏胖
    private int fat;            //肥胖
    private float viewHeight;   //控件高度
    private float viewWidth;   //控件宽度
    private Paint paint;         //画线Paint
    private Paint paintText;       //画字Paint
    private Paint paintTriangle;    //画小三角形
    private  Typeface newFont;      //字体类型
    public WeightLine(Context context){
        this(context,null);
    }
    public WeightLine(Context context, AttributeSet attrs){
        this(context,attrs,0);
    }
    public WeightLine(Context context,AttributeSet attrs,int defStyleAttr){
        super(context,attrs,defStyleAttr);

        thinnish=getResources().getColor(R.color.color_2387f5);
        criterion=getResources().getColor(R.color.color_ff63c70b);
        slightlyFat=getResources().getColor(R.color.color_ffffbf01);
        fat=getResources().getColor(R.color.color_ffff781e);
        newFont = Typeface.createFromAsset(context.getAssets(), "fonts/Black_Simplified.ttf");
        initPaint(context);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        drawLine(canvas);       //画线
        drawText(canvas);          //画文字
        drawTriangle(canvas);       //画三角形
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        viewWidth =MeasureSpec.getSize(widthMeasureSpec);
    }
    private void initPaint(Context context){
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(15);

        paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText.setTextSize(MiscUtil.sp2px(context,10));
        paintText.setColor(Color.GRAY);
        paintText.setTypeface(newFont);

        paintTriangle=new Paint();
        paintTriangle.setStyle(Paint.Style.FILL);
        paintTriangle.setAntiAlias(true);     //去锯齿
        paintTriangle.setStrokeWidth(3);
    }
    private void drawLine(Canvas canvas){
        paint.setColor(thinnish);
        canvas.drawLine(0, viewHeight/2, viewWidth/4, viewHeight/2, paint);
        paint.setColor(criterion);
        canvas.drawLine(viewWidth/4, viewHeight/2, viewWidth/4*2, viewHeight/2, paint);
        paint.setColor(slightlyFat);
        canvas.drawLine(viewWidth/4*2, viewHeight/2, viewWidth/4*3, viewHeight/2, paint);
        paint.setColor(fat);
        canvas.drawLine(viewWidth/4*3, viewHeight/2, viewWidth, viewHeight/2, paint);
    }
    private void drawText(Canvas canvas){


        String drawStr = "偏瘦";
        Rect bounds = new Rect();
        paintText.getTextBounds(drawStr, 0, drawStr.length(), bounds);      //获得绘制文本的宽和高
        canvas.drawText(drawStr, viewWidth/8 - bounds.width() / 2, viewHeight-2, paintText);

        drawStr = "53.5公斤";
        paintText.getTextBounds(drawStr, 0, drawStr.length(), bounds);      //获得绘制文本的宽和高
        canvas.drawText(drawStr, viewWidth/8*2 - bounds.width() / 2, 20, paintText);

        drawStr = "标准";
        paintText.getTextBounds(drawStr, 0, drawStr.length(), bounds);      //获得绘制文本的宽和高
        canvas.drawText(drawStr, viewWidth/8*3 - bounds.width() / 2, viewHeight-2, paintText);

        drawStr = "69.4公斤";
        paintText.getTextBounds(drawStr, 0, drawStr.length(), bounds);      //获得绘制文本的宽和高
        canvas.drawText(drawStr, viewWidth/8*4 - bounds.width() / 2, 20, paintText);

        drawStr = "偏胖";
        paintText.getTextBounds(drawStr, 0, drawStr.length(), bounds);      //获得绘制文本的宽和高
        canvas.drawText(drawStr, viewWidth/8*5 - bounds.width() / 2, viewHeight-2, paintText);

        drawStr = "80.9公斤";
        paintText.getTextBounds(drawStr, 0, drawStr.length(), bounds);      //获得绘制文本的宽和高
        canvas.drawText(drawStr, viewWidth/8*6 - bounds.width() / 2, 20, paintText);

        drawStr = "肥胖";
        paintText.getTextBounds(drawStr, 0, drawStr.length(), bounds);      //获得绘制文本的宽和高
        canvas.drawText(drawStr, viewWidth/8*7 - bounds.width() / 2, viewHeight-2, paintText);
    }
    private  void drawTriangle(Canvas canvas){
        PersonInfo firstPersonInfo= DataSupport.findFirst(PersonInfo.class);
        int badyWeight=firstPersonInfo.getBodyWeight();


        Path path=new Path();
        double scale;       //比例
        if(badyWeight<=53.5){
            paintTriangle.setColor(thinnish);
            if(badyWeight<=33.5){
                path.moveTo(0,viewHeight/2-20);
                path.lineTo(20,viewHeight/2-20);
                path.lineTo(0,viewHeight/2-7);
                path.close();
                canvas.drawPath(path,paintTriangle);

                path.moveTo(0,viewHeight/2+20);
                path.lineTo(20,viewHeight/2+20);
                path.lineTo(0,viewHeight/2+7);
                path.close();
                canvas.drawPath(path,paintTriangle);
            }else {
                scale=(badyWeight-33.5)/(4*(53.5-33.5));
                float i=(float)scale;
                path.moveTo(viewWidth*i,viewHeight/2-7);
                path.lineTo(viewWidth*i-20,viewHeight/2-20);
                path.lineTo(viewWidth*i+20,viewHeight/2-20);
                path.close();
                canvas.drawPath(path,paintTriangle);

                path.moveTo(viewWidth*i,viewHeight/2+7);
                path.lineTo(viewWidth*i-20,viewHeight/2+20);
                path.lineTo(viewWidth*i+20,viewHeight/2+20);
                path.close();
                canvas.drawPath(path,paintTriangle);
            }
        }else if(badyWeight<=69.4){
            paintTriangle.setColor(criterion);
            scale=(badyWeight-53.5)/(4*(69.4-53.5));
            float i=(float)scale;
            path.moveTo(viewWidth*i+viewWidth/4,viewHeight/2-7);
            path.lineTo(viewWidth*i-20+viewWidth/4,viewHeight/2-20);
            path.lineTo(viewWidth*i+20+viewWidth/4,viewHeight/2-20);
            path.close();
            canvas.drawPath(path,paintTriangle);

            path.moveTo(viewWidth*i+viewWidth/4,viewHeight/2+7);
            path.lineTo(viewWidth*i-20+viewWidth/4,viewHeight/2+20);
            path.lineTo(viewWidth*i+20+viewWidth/4,viewHeight/2+20);
            path.close();
            canvas.drawPath(path,paintTriangle);
        }else if(badyWeight<=80.9){
            paintTriangle.setColor(slightlyFat);
            scale=(badyWeight-69.4)/(4*(80.9-69.4));
            float i=(float)scale;
            path.moveTo(viewWidth*i+viewWidth/4*2,viewHeight/2-7);
            path.lineTo(viewWidth*i-20+viewWidth/4*2,viewHeight/2-20);
            path.lineTo(viewWidth*i+20+viewWidth/4*2,viewHeight/2-20);
            path.close();
            canvas.drawPath(path,paintTriangle);

            path.moveTo(viewWidth*i+viewWidth/4*2,viewHeight/2+7);
            path.lineTo(viewWidth*i-20+viewWidth/4*2,viewHeight/2+20);
            path.lineTo(viewWidth*i+20+viewWidth/4*2,viewHeight/2+20);
            path.close();
            canvas.drawPath(path,paintTriangle);
        }else {
            paintTriangle.setColor(fat);
            if(badyWeight<=100){
                scale=(badyWeight-80.9)/(4*(100-80.9));
                float i=(float)scale;
                path.moveTo(viewWidth*i+viewWidth/4*3,viewHeight/2-7);
                path.lineTo(viewWidth*i-20+viewWidth/4*3,viewHeight/2-20);
                path.lineTo(viewWidth*i+20+viewWidth/4*3,viewHeight/2-20);
                path.close();
                canvas.drawPath(path,paintTriangle);

                path.moveTo(viewWidth*i+viewWidth/4*3,viewHeight/2+7);
                path.lineTo(viewWidth*i-20+viewWidth/4*3,viewHeight/2+20);
                path.lineTo(viewWidth*i+20+viewWidth/4*3,viewHeight/2+20);
                path.close();
                canvas.drawPath(path,paintTriangle);
            }else {
                path.moveTo(viewWidth,viewHeight/2-7);
                path.lineTo(viewWidth-20,viewHeight/2-20);
                path.lineTo(viewWidth,viewHeight/2-20);
                path.close();
                canvas.drawPath(path,paintTriangle);

                path.moveTo(viewWidth,viewHeight/2+7);
                path.lineTo(viewWidth-20,viewHeight/2+20);
                path.lineTo(viewWidth,viewHeight/2+20);
                path.close();
                canvas.drawPath(path,paintTriangle);
            }
        }

    }

}
