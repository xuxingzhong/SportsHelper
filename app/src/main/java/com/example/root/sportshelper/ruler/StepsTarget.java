package com.example.root.sportshelper.ruler;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.root.sportshelper.R;
import com.example.root.sportshelper.utils.MiscUtil;

import java.text.DecimalFormat;


/**
 * Created by root on 17-8-2.
 */

public class StepsTarget extends View {
    private int backgroundColor;
    private int firstColor;
    private int lastColor;
    private Paint mPaint;
    private float viewHeight;   //控件高度
    private float viewWidth;   //控件宽度
    private int progressValue;//进度值
    private int minStep;   //最小的步数
    private int maxStep;  //最大的步数
    private OnStateChangeListener onStateChangeListener;
    private int textSize;       //文字大小
    private int textColor;      //文字颜色
    private Bitmap textBackground; //文字背景图片
    private float mBgWidth, mBgHeight;//图片长宽
    private int fixedProgressValue;     //固定建议值
    public StepsTarget(Context context){
        this(context,null);
    }

    public StepsTarget(Context context, AttributeSet attrs){
        this(context,attrs,0);
    }

    public StepsTarget(Context context,AttributeSet attrs,int defStyleAttr){
        super(context,attrs,defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StepsTarget);
        backgroundColor=typedArray.getColor(R.styleable.StepsTarget_bottom_line_color, getResources().getColor(R.color.color_e7e7e7));
        firstColor=typedArray.getColor(R.styleable.StepsTarget_first_color, getResources().getColor(R.color.color_40e5f9));
        lastColor=typedArray.getColor(R.styleable.StepsTarget_last_color, getResources().getColor(R.color.color_2387f5));
        minStep=typedArray.getInteger(R.styleable.StepsTarget_min_step,1000);
        maxStep=typedArray.getInteger(R.styleable.StepsTarget_max_step,30000);
        textSize=typedArray.getDimensionPixelSize(R.styleable.StepsTarget_textSize, MiscUtil.sp2px(context,14));
        textColor=typedArray.getColor(R.styleable.StepsTarget_textColor,Color.WHITE);
        int bgResId=typedArray.getResourceId(R.styleable.StepsTarget_textBackground,R.mipmap.ic_information_height_bg);
        textBackground= BitmapFactory.decodeResource(getResources(),bgResId);
        mBgWidth=textBackground.getWidth();
        mBgHeight=textBackground.getHeight();
        if(progressValue==0){
            progressValue=typedArray.getInteger(R.styleable.StepsTarget_progressValue,8000);
        }

        mPaint = new Paint();
        fixedProgressValue=progressValue;
    }
    public void setProgressValue(int progressValue){
        this.progressValue = progressValue;
        fixedProgressValue=progressValue;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(15);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(Color.TRANSPARENT);     //颜色为透明

        drawBottomLine(mPaint,canvas);     //画底部线
        drawShadeLine(mPaint,canvas);       //画渐变线
        drawCircle(mPaint,canvas);          //画圆
        drawText(mPaint,canvas);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        viewWidth =MeasureSpec.getSize(widthMeasureSpec);
    }
    private  void drawBottomLine(Paint mPaint,Canvas canvas){
        mPaint.setColor(backgroundColor);
        canvas.drawLine(0,viewHeight/2+15,viewWidth,viewHeight/2+15,mPaint);
    }
    private void drawShadeLine(Paint mPaint,Canvas canvas){
        int[] colors={firstColor,lastColor};
        //设置渐变色区域
        LinearGradient shader = new LinearGradient(0, 0, viewWidth , 0, colors, null,
                Shader.TileMode.CLAMP);
        mPaint.setShader(shader);
        float scale=((float) progressValue-(float) minStep)/(maxStep-minStep);     //比例
        //画出渐变色进度条
        canvas.drawLine(0, viewHeight/2+15, viewWidth*scale, viewHeight/2+15, mPaint);
    }
    private void drawCircle(Paint mPaint,Canvas canvas){
        //渐变色外圆
        mPaint.setStrokeWidth(3);
        mPaint.setColor(lastColor);
        mPaint.setStyle(Paint.Style.FILL);
        float scale=((float) progressValue-(float) minStep)/(maxStep-minStep);     //比例
        canvas.drawCircle(viewWidth*scale, viewHeight/2+15, 20, mPaint);
        //绘制两条斜线，使外圆到进度条的连接更自然
        if(progressValue>1800){
            mPaint.setStrokeWidth(5);
            canvas.drawLine(viewWidth*scale-40,viewHeight/2+15,viewWidth*scale,viewHeight/2-18+15, mPaint);
            canvas.drawLine(viewWidth*scale-40,viewHeight/2+15,viewWidth*scale,viewHeight/2+18+15, mPaint);
        }
        //白色内圆
        mPaint.setShader(null);
        mPaint.setColor(Color.WHITE);
        canvas.drawCircle(viewWidth*scale, viewHeight/2+15, 10, mPaint);//白色内圆
    }
    private void drawText(Paint mPaint,Canvas canvas){      //画建议值
        float scale=((float) fixedProgressValue-(float) minStep)/(maxStep-minStep);     //比例
        canvas.drawBitmap(textBackground,viewWidth*scale-mBgWidth/2,0,mPaint);
        mPaint.setShader(null);
        mPaint.setColor(textColor);
        mPaint.setTextSize(textSize);
        String drawStr = "建议值";
        Rect bounds = new Rect();
        mPaint.getTextBounds(drawStr, 0, drawStr.length(), bounds);      //获得绘制文本的宽和高
        canvas.drawText(drawStr, viewWidth*scale-mBgWidth/2+15, 40, mPaint);
        mPaint.setColor(lastColor);
        canvas.drawText("1k", 0, viewHeight, mPaint);
        canvas.drawText("30k", viewWidth-50, viewHeight, mPaint);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
//        float y=(x/viewWidth)*(maxStep+1000);     //比例
//        double z=Math.rint(((x/viewWidth)*(maxStep+1000))/1000);      //取整
        progressValue=(int)(Math.rint(((x/viewWidth)*(maxStep+1000))/1000))*1000;
        if(progressValue<minStep){
            progressValue=minStep;
        }else if(progressValue>maxStep){
            progressValue=maxStep;
        }
        switch(event.getAction()) {
            case 0://ACTION_DOWN
                Log.i("StepsTarget", "onTouchEvent: x: "+x+" progressValue : "+progressValue);
                break;
            case 1://ACTION_UP
                if (onStateChangeListener!=null){
                    onStateChangeListener.onStopTrackingTouch(progressValue);
                }
                break;
            case 2://ACTION_MOVE
                if (onStateChangeListener!=null){
                    onStateChangeListener.OnStateChangeListener(progressValue);
                }
                this.invalidate();
                break;
        }
        return true;
    }
    public void setOnStateChangeListener(OnStateChangeListener onStateChangeListener1){
        this.onStateChangeListener=onStateChangeListener1;
    }
}
