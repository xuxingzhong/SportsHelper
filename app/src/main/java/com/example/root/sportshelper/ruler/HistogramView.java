package com.example.root.sportshelper.ruler;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.root.sportshelper.R;
import com.example.root.sportshelper.utils.Constant;
import com.example.root.sportshelper.utils.MiscUtil;

/**
 * 柱形图
 * Created by root on 17-8-22.
 */

public class HistogramView  extends View{
    private int type;                   //类型
    private Paint bottomPaint;          //底部线
    private Paint histogramPaint;       //柱形图
    private Paint dashedPaint;          //虚线
    private Path dashPath;              //虚线区域
    private TextPaint datePaint;        //日期
    private String myDate;              //日期数值
    private int bottomLineColor;        //底部线颜色
    private int unSelectColor;          //未选中时柱形图颜色
    private int selectColor;            //选中时颜色


    private float viewHeight;           //控件高度
    private float viewWidth;            //控件宽度
    private int progressValue;          //进度值
    private int minStep;                //最小的步数
    private int maxStep;                //最大的步数
    private int textSize;               //文字大小
    private int textColor;              //文字颜色
    private Typeface blackSimpleFont;          //方正兰亭黑简体
    private Typeface accidentalFont;            //accidentalPresidency字体
    private int suggestStep;                //建议步数
    private Boolean isSelect=false;               //是否选中

    private String TAG="HistogramView";
    private static int flag=1;

    public HistogramView(Context context){
        this(context,null);
    }

    public HistogramView(Context context, AttributeSet attrs){
        this(context,attrs,0);
    }

    public HistogramView(Context context,AttributeSet attrs,int defStyleAttr){
        super(context,attrs,defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HStepsCount);
        type=typedArray.getInteger(R.styleable.HStepsCount_type, Constant.DAY);
        bottomLineColor=typedArray.getColor(R.styleable.HStepsCount_bottomline_color,Color.parseColor("#c4e6f6"));
        unSelectColor=typedArray.getColor(R.styleable.HStepsCount_unselect_color,Color.parseColor("#c4e6f6"));
        selectColor=typedArray.getColor(R.styleable.HStepsCount_select_color,Color.parseColor("#ffffff"));
        minStep=typedArray.getInteger(R.styleable.HStepsCount_minstep,0);
        maxStep=typedArray.getInteger(R.styleable.HStepsCount_maxstep,15000);
        textSize=typedArray.getDimensionPixelSize(R.styleable.HStepsCount_mytextSize,30);
        textColor=typedArray.getColor(R.styleable.HStepsCount_mytextColor,Color.parseColor("#e5e5e5"));
        progressValue=typedArray.getInteger(R.styleable.HStepsCount_myprogressValue,0);
        suggestStep=typedArray.getInteger(R.styleable.HStepsCount_suggestStep,8000);
        typedArray.recycle();
        initPaint(context);
    }

    private void initPaint(Context context){
        blackSimpleFont=Typeface.createFromAsset(context.getAssets(),"fonts/Black_Simplified.ttf");
        accidentalFont=Typeface.createFromAsset(context.getAssets(),"fonts/AccidentalPresidency.ttf");

        bottomPaint=new Paint();                            //底线
        bottomPaint.setAntiAlias(true);
        bottomPaint.setStrokeWidth(2);
        bottomPaint.setStyle(Paint.Style.STROKE);
        bottomPaint.setStrokeCap(Paint.Cap.SQUARE);          //圆形笔触
        bottomPaint.setColor(bottomLineColor);

        histogramPaint=new Paint();                         //柱形图
        histogramPaint.setAntiAlias(true);
        histogramPaint.setStrokeWidth(20);
        histogramPaint.setStyle(Paint.Style.FILL);
        histogramPaint.setStrokeCap(Paint.Cap.SQUARE);          //直角笔触


        dashedPaint=new Paint();
        dashedPaint.setAntiAlias(true);
        dashedPaint.setStrokeWidth(3);
        dashedPaint.setStyle(Paint.Style.STROKE);
        dashedPaint.setStrokeCap(Paint.Cap.ROUND);          //圆形笔触
        dashedPaint.setColor(bottomLineColor);
        dashedPaint.setPathEffect(new DashPathEffect(new float[]{5,5},0));
        dashPath=new Path();

        datePaint=new TextPaint();
        datePaint.setTextSize(MiscUtil.sp2px(context,15));

        datePaint.setTextAlign(Paint.Align.CENTER);     // 从中间向两边绘制，不需要再次计算文字
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        viewWidth =MeasureSpec.getSize(widthMeasureSpec);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        drawText(canvas);                //画日期
        drawBottomLine(canvas);         //画底部线
        drawRect(canvas);               //画柱形图
        drawDasheLine(canvas);          //画目标虚线

    }

    private void drawText(Canvas canvas){
        if(isSelect){
            datePaint.setColor(selectColor);
        }else {
            datePaint.setColor(textColor);
        }

        if(myDate.equals("今天")||myDate.equals("昨天")){
            datePaint.setTypeface(blackSimpleFont);
        }else {
            datePaint.setTypeface(accidentalFont);
        }
        canvas.drawText(myDate,viewWidth/2,viewHeight-5,datePaint);
    }

    private  void drawBottomLine(Canvas canvas){
//        Rect myRect=new Rect();
//        bottomPaint.getTextBounds(myDate,0,myDate.length(),myRect);
        canvas.drawLine(0,viewHeight-35,viewWidth,viewHeight-35,bottomPaint);
    }

    private void drawRect(Canvas canvas){

        if(isSelect){
            histogramPaint.setColor(selectColor);
        }else {
            histogramPaint.setColor(unSelectColor);
        }

        Rect myRect=new Rect();
        int left=0;
        int right=0;
        if(type==Constant.DAY){
            if(isSelect){
                left=5;
                right=(int)viewWidth-5;
            }else {
                left=10;
                right=(int)viewWidth-10;
            }
        }else if(type==Constant.WEEK){
            left=5;
            right=(int)viewWidth-5;
        }else if(type==Constant.MONTH){
            left=2;
            right=(int)viewWidth-2;
        }

        double top=progressValue*(viewHeight-35)/maxStep;
        if(top<1){
            top=1;
        }else {
            top=Math.rint(top);
        }
        int bottom=(int)viewHeight-35;
        int mytop=(int)(bottom-top);
        myRect.set(left,mytop,right,bottom);
        canvas.drawRect(left,mytop,right,bottom,histogramPaint);
    }
    private void drawDasheLine(Canvas canvas){
        double top=suggestStep*(viewHeight-35)/maxStep;
        int myEnd=(int)(viewHeight-35-top);
        dashPath.moveTo(0,myEnd);
        dashPath.lineTo(viewWidth,myEnd);
        canvas.drawPath(dashPath,dashedPaint);

    }

    public void setProgressValue(int progressValue){
        this.progressValue = progressValue;
    }

    public void setMyDate(String myDate){
        this.myDate=myDate;
    }

    public void setType(int type){
        this.type=type;
    }

    public void setSuggestStep(int step){
        this.suggestStep=step;
    }
    public void setIsSelect(Boolean TorF){
        this.isSelect=TorF;
    }
}
