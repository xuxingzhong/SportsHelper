package com.example.root.sportshelper.ruler;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.root.sportshelper.R;
import com.example.root.sportshelper.utils.MiscUtil;

/**
 * Created by root on 17-8-9.
 * 水平进度条显示记录
 */

public class HStepRecord extends View{
    private int backgroundColor;
    private int firstColor;
    private int lastColor;

    private Paint bottomPaint;      //底部线
    private Paint shadePaint;       //渐变线
    private Paint leftPaint;        //左边线
    private Paint dashedPaint;      //虚线
    private Path dashPath;
    private TextPaint datePaint;    //日期
    private String myDate;          //日期数值

    private float viewHeight;   //控件高度
    private float viewWidth;   //控件宽度
    private int progressValue;//进度值
    private int minStep;   //最小的步数
    private int maxStep;  //最大的步数
    private OnStateChangeListener onStateChangeListener;
    private int textSize;       //文字大小
    private int textColor;      //文字颜色
    private int i=1;
    private Typeface blackSimpleFont;          //方正兰亭黑简体

    public HStepRecord(Context context){
        this(context,null);
    }

    public HStepRecord(Context context, AttributeSet attrs){
        this(context,attrs,0);
    }

    public HStepRecord(Context context,AttributeSet attrs,int defStyleAttr){
        super(context,attrs,defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StepsTarget);
        backgroundColor=typedArray.getColor(R.styleable.StepsTarget_bottom_line_color, Color.parseColor("#e7e7e7"));
        firstColor=typedArray.getColor(R.styleable.StepsTarget_first_color, Color.parseColor("#40e5f9"));
        lastColor=typedArray.getColor(R.styleable.StepsTarget_last_color, Color.parseColor("#2387f5"));
        minStep=typedArray.getInteger(R.styleable.StepsTarget_min_step,0);
        maxStep=typedArray.getInteger(R.styleable.StepsTarget_max_step,30000);
        textSize=typedArray.getDimensionPixelSize(R.styleable.StepsTarget_textSize,30);
        textColor=typedArray.getColor(R.styleable.StepsTarget_textColor,Color.BLACK);

        if(progressValue==0){
            progressValue=typedArray.getInteger(R.styleable.StepsTarget_progressValue,8000);
        }
        typedArray.recycle();
        initPaint(context);
    }

    private void initPaint(Context context){
        blackSimpleFont=Typeface.createFromAsset(context.getAssets(),"fonts/Black_Simplified.ttf");

        bottomPaint=new Paint();
        bottomPaint.setAntiAlias(true);
        bottomPaint.setStrokeWidth(20);
        bottomPaint.setStyle(Paint.Style.STROKE);
        bottomPaint.setStrokeCap(Paint.Cap.ROUND);          //圆形笔触
        bottomPaint.setColor(backgroundColor);

        shadePaint=new Paint();
        shadePaint.setAntiAlias(true);
        shadePaint.setStrokeWidth(20);
        shadePaint.setStyle(Paint.Style.STROKE);
        shadePaint.setStrokeCap(Paint.Cap.ROUND);          //圆形笔触

        leftPaint=new Paint();
        leftPaint.setAntiAlias(true);
        leftPaint.setStrokeCap(Paint.Cap.ROUND);

        dashedPaint=new Paint();
        dashedPaint.setAntiAlias(true);
        dashedPaint.setStrokeWidth(3);
        dashedPaint.setStyle(Paint.Style.STROKE);
        dashedPaint.setStrokeCap(Paint.Cap.ROUND);          //圆形笔触
        dashedPaint.setColor(backgroundColor);
        dashedPaint.setPathEffect(new DashPathEffect(new float[]{5,5},0));
        dashPath=new Path();

        datePaint=new TextPaint();
        datePaint.setTextSize(MiscUtil.sp2px(context,12));
        datePaint.setColor(Color.BLACK);
        datePaint.setTextAlign(Paint.Align.CENTER);     // 从中间向两边绘制，不需要再次计算文字
        datePaint.setTypeface(blackSimpleFont);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        viewWidth =MeasureSpec.getSize(widthMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBottomLine(canvas);        //画底部线
        if(progressValue!=0){
            drawShadeLine(canvas);       //画渐变线
        }
        drawText(canvas);            //画日期
        drawLeftLine(canvas);       //画左边线
        drawDasheLine(canvas);      //画虚线
    }
    private void drawLeftLine(Canvas canvas){      //画左边线
        leftPaint.setColor(backgroundColor);
        leftPaint.setStrokeWidth(5);
        canvas.drawLine(10,0,10,viewHeight/2-15,leftPaint);
        canvas.drawLine(10,viewHeight/2+15,10,viewHeight,leftPaint);
        leftPaint.setStrokeWidth(15);
        leftPaint.setColor(firstColor);
        canvas.drawPoint(10,viewHeight/2,leftPaint);
    }
    private  void drawBottomLine(Canvas canvas){
        canvas.drawLine(40,viewHeight/2,viewWidth/7*5,viewHeight/2,bottomPaint);
    }
    private void drawShadeLine(Canvas canvas){

        int[] colors={firstColor,lastColor};
        LinearGradient shader;
        if(i==1){
            //hadePaint.setShader(null);
            i++;
        }else {
            //设置渐变色区域
            shader = new LinearGradient(0, 0, viewWidth , 0, colors, null,
                    Shader.TileMode.CLAMP);
            shadePaint.setShader(shader);
        }

        float scale=((float) progressValue-(float) minStep)/(maxStep-minStep);     //比例
        if(scale>=1){
            scale=1;
        }

        //画出渐变色进度条
        canvas.drawLine(40, viewHeight/2, viewWidth/14*9*scale+40, viewHeight/2, shadePaint);
    }

    private void drawText(Canvas canvas){

        canvas.drawText(myDate,viewWidth/7*6,viewHeight/2+8,datePaint);
    }
    private void drawDasheLine(Canvas canvas){
        float textWidth = dashedPaint.measureText(myDate.toString());
        dashPath.moveTo(40,viewHeight);
        dashPath.lineTo(viewWidth/7*6+textWidth/2,viewHeight);
        canvas.drawPath(dashPath,dashedPaint);
    }
    public void setOnStateChangeListener(OnStateChangeListener onStateChangeListener1){
        this.onStateChangeListener=onStateChangeListener1;
    }

    public void setProgressValue(int progressValue){
        this.progressValue = progressValue;
    }

    public void setMaxStep(int maxStep){
        this.maxStep=maxStep;
    }
    public void setMyDate(String myDate){
        this.myDate=myDate;
    }
}
