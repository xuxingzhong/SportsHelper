package com.example.root.sportshelper.ruler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.example.root.sportshelper.R;

/**
 * 只画左边线
 * Created by root on 17-8-29.
 */

public class HRunningRecord extends View {
    private Paint leftPaint;        //左边线
    private float viewHeight;   //控件高度
    private float viewWidth;   //控件宽度
    private int leftColor;      //虚线颜色
    private int pointColor;     //点的颜色


    public HRunningRecord(Context context){
        this(context,null);
    }

    public HRunningRecord(Context context, AttributeSet attrs){
        this(context,attrs,0);
    }

    public HRunningRecord(Context context,AttributeSet attrs,int defStyleAttr){
        super(context,attrs,defStyleAttr);
        leftColor=getResources().getColor(R.color.color_e7e7e7);
        pointColor=getResources().getColor(R.color.color_40e5f9);
        initPaint(context);
    }

    private void initPaint(Context context){
        leftPaint=new Paint();
        leftPaint.setAntiAlias(true);
        leftPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        viewWidth =MeasureSpec.getSize(widthMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawLeftLine(canvas);       //画左边线
    }

    private void drawLeftLine(Canvas canvas){      //画左边线
        leftPaint.setColor(leftColor);
        leftPaint.setStrokeWidth(5);
        canvas.drawLine(7,0,7,viewHeight/2-15,leftPaint);
        canvas.drawLine(7,viewHeight/2+15,7,viewHeight,leftPaint);
        leftPaint.setStrokeWidth(15);
        leftPaint.setColor(pointColor);
        canvas.drawPoint(7,viewHeight/2,leftPaint);
    }
}
