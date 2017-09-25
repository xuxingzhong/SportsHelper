package com.example.root.sportshelper.ruler;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.root.sportshelper.R;
import com.example.root.sportshelper.utils.Constant;
import com.example.root.sportshelper.utils.MiscUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 长按暂停按钮动画
 * Created by root on 17-9-4.
 */

public class LongpressCircle extends View{
    private Context mContext;
    private static final String TAG = LongpressCircle.class.getSimpleName();
    //默认大小
    private int mDefaultSize;

    //绘制文字暂停
    private TextPaint mPauseTextPaint;
    private CharSequence mPause;         //类似string
    private int mPauseColor;
    private float mPauseSize;
    private float mPauseOffset;

    private Paint PauseBackgroudPaint;              //暂停背景(圆形)
    private int mPauseBackgroudColor;               //背景颜色
    private RectF bgRectF;                           //绘制背景的矩形区域

    private Paint PauseProgressBgPaint;               //进度条背景（圆弧）
    private int PauseProgressBgColor;               //进度条背景颜色
    private float mBgArcWidth;

    private Paint PauseProgressPaint;               //进度条（圆弧）
    private int PauseProgressColor;                 //进度条颜色
    private float mArcWidth;                        //圆弧宽度
    private float mStartAngle, mSweepAngle;        //圆弧起始角度，3点钟方向为0，顺时针递增，小于0或大于360进行取余;    圆弧度数
    private RectF mRectF;                          //绘制圆弧的矩形区域

    private float mValue;
    private float mMaxValue;

    //当前进度，[0.0f,1.0f]
    private float mPercent=0.0f;
    //动画时间
    private long mAnimTime;
    //属性动画
    private ValueAnimator mAnimator;

    //圆心坐标，半径
    private Point mCenterPoint;
    private float mRadius;
    private Typeface blackSimpleFont;          //方正兰亭黑简体

    private int flag=1;         //次数

//    /**
//     * 长按的回调接口
//     */
//    private LongTouchListener mListener;
//
//    /**
//     * 按钮长按时 间隔多少毫秒来处理 回调方法
//     */
//    private int mtime;
//
//    /**
//     * 记录当前自定义Btn是否按下
//     */
//    private boolean clickdown = false;

    // 是否释放了
    private boolean isReleased;
    // 计数器，防止多次点击导致最后一次形成longpress的时间变短
    private int mCounter;
    // 长按的runnable
    private Runnable mLongPressRunnable;

    private Timer timer;

    private int num=0;

    public static final int UPDATE=1;
    public LongpressCircle(Context mContext, AttributeSet attrs){
        super(mContext,attrs);
        init(mContext,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        mDefaultSize = MiscUtil.dipToPx(mContext, 150);
        mAnimator = new ValueAnimator();
        mRectF = new RectF();
        bgRectF=new RectF();
        mCenterPoint = new Point();
        initAttrs(attrs);
        initPaint(context);
        setValue(mValue);

        mLongPressRunnable = new Runnable() {
            @Override
            public void run() {
                mCounter--;
                // 计数器大于0，说明当前执行的Runnable不是最后一次down产生的。
                if (mCounter > 0 || isReleased )
                    return;
                performLongClick();// 回调长按事件
            }
        };
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar);

        mPause = typedArray.getString(R.styleable.CircleProgressBar_PauseText);
        mPauseColor = typedArray.getColor(R.styleable.CircleProgressBar_PauseTextColor, getResources().getColor(R.color.color_21202cFF));
        mPauseSize = typedArray.getDimension(R.styleable.CircleProgressBar_PauseTextSize, 16);

        mValue = typedArray.getFloat(R.styleable.CircleProgressBar_value, 0);
        mMaxValue = typedArray.getFloat(R.styleable.CircleProgressBar_maxValue, 60);

        mArcWidth = typedArray.getDimension(R.styleable.CircleProgressBar_arcWidth, 15);
        mStartAngle = typedArray.getFloat(R.styleable.CircleProgressBar_startAngle, 270);
        mSweepAngle = typedArray.getFloat(R.styleable.CircleProgressBar_sweepAngle, 360);

        PauseProgressBgColor = typedArray.getColor(R.styleable.CircleProgressBar_bgArcColor,getResources().getColor(R.color.color_33ffffff));
        mBgArcWidth = typedArray.getDimension(R.styleable.CircleProgressBar_bgArcWidth, 15);

        //mPercent = typedArray.getFloat(R.styleable.CircleProgressBar_percent, 0);
        mPercent=0;
        mAnimTime = typedArray.getInt(R.styleable.CircleProgressBar_animTime, 100);

        PauseProgressColor=typedArray.getColor(R.styleable.CircleProgressBar_arcColors,Color.WHITE);
        mPauseBackgroudColor=typedArray.getColor(R.styleable.CircleProgressBar_arcColors,Color.WHITE);

        typedArray.recycle();

    }

    private void initPaint(Context context) {
        blackSimpleFont=Typeface.createFromAsset(context.getAssets(),"fonts/Black_Simplified.ttf");

        //画暂停文字
        mPauseTextPaint = new TextPaint();
        // 设置抗锯齿,会消耗较大资源，绘制图形速度会变慢。
        mPauseTextPaint.setAntiAlias(true);
        // 设置绘制文字大小
        mPauseTextPaint.setTextSize(mPauseSize);
        // 设置画笔颜色
        mPauseTextPaint.setColor(mPauseColor);
        // 从中间向两边绘制，不需要再次计算文字
        mPauseTextPaint.setTextAlign(Paint.Align.CENTER);
        mPauseTextPaint.setTypeface(blackSimpleFont);

        //画暂停文字的背景，为圆形
        PauseBackgroudPaint = new Paint();
        PauseBackgroudPaint.setAntiAlias(true);
        // 设置画笔的样式，为FILL，FILL_OR_STROKE，或STROKE
        PauseBackgroudPaint.setStyle(Paint.Style.FILL);
        // 设置画笔粗细
        PauseBackgroudPaint.setStrokeWidth(5);
        // 当画笔样式为STROKE或FILL_OR_STROKE时，设置笔刷的图形样式，如圆形样式
        // Cap.ROUND,或方形样式 Cap.SQUARE
        PauseBackgroudPaint.setStrokeCap(Paint.Cap.ROUND);
        PauseBackgroudPaint.setColor(mPauseBackgroudColor);


        //画背景圆弧
        PauseProgressBgPaint = new Paint();
        PauseProgressBgPaint.setAntiAlias(true);
        // 设置画笔的样式，为FILL，FILL_OR_STROKE，或STROKE
        PauseProgressBgPaint.setStyle(Paint.Style.STROKE);
        // 设置画笔粗细
        PauseProgressBgPaint.setStrokeWidth(mBgArcWidth);
        // 当画笔样式为STROKE或FILL_OR_STROKE时，设置笔刷的图形样式，如圆形样式
        // Cap.ROUND,或方形样式 Cap.SQUARE
        PauseProgressBgPaint.setStrokeCap(Paint.Cap.ROUND);
        PauseProgressBgPaint.setColor(PauseProgressBgColor);


        //画圆弧
        PauseProgressPaint = new Paint();
        PauseProgressPaint.setAntiAlias(true);
        PauseProgressPaint.setColor(PauseProgressColor);
        PauseProgressPaint.setStyle(Paint.Style.STROKE);
        PauseProgressPaint.setStrokeWidth(mArcWidth);
        PauseProgressPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MiscUtil.measure(widthMeasureSpec, mDefaultSize),
                MiscUtil.measure(heightMeasureSpec, mDefaultSize));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //求圆弧和背景圆弧的最大宽度
        float maxArcWidth = Math.max(mArcWidth, mBgArcWidth);
        //求最小值作为实际值
        int minSize = Math.min(w - getPaddingLeft() - getPaddingRight() - 2 * (int) maxArcWidth,
                h - getPaddingTop() - getPaddingBottom() - 2 * (int) maxArcWidth);
        //减去圆弧的宽度，否则会造成部分圆弧绘制在外围
        mRadius = minSize / 2;
        //获取圆的相关参数
        mCenterPoint.x = w / 2;
        mCenterPoint.y = h / 2;
        //绘制圆弧的边界
        mRectF.left = mCenterPoint.x - mRadius - maxArcWidth / 2;
        mRectF.top = mCenterPoint.y - mRadius - maxArcWidth / 2;
        mRectF.right = mCenterPoint.x + mRadius + maxArcWidth / 2;
        mRectF.bottom = mCenterPoint.y + mRadius + maxArcWidth / 2;

        mPauseOffset = mCenterPoint.y + getBaselineOffsetFromY(mPauseTextPaint);
        Log.i(TAG, "onSizeChanged: 控件大小 = " + "(" + w + ", " + h + ")"
                + "圆心坐标 = " + mCenterPoint.toString()
                + ";圆半径 = " + mRadius
                + ";圆的外接矩形 = " + mRectF.toString());
    }

    private float getBaselineOffsetFromY(Paint paint) {
        return MiscUtil.measureTextHeight(paint) / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawCircle(canvas);
        drawArc(canvas);
        drawText(canvas);
    }

    //画背景圆形
    private void drawCircle(Canvas canvas){
        canvas.drawCircle(mCenterPoint.x,mCenterPoint.y,mRadius,PauseBackgroudPaint);
    }

    /**
     * 绘制内容文字
     *
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        // 计算文字宽度，由于Paint已设置为居中绘制，故此处不需要重新计算
        // float textWidth = mValuePaint.measureText(mValue.toString());
        // float x = mCenterPoint.x - textWidth / 2;
        canvas.drawText(mPause.toString(), mCenterPoint.x, mPauseOffset, mPauseTextPaint);

    }

    private void drawArc(Canvas canvas) {
        // 绘制背景圆弧
        // 从进度圆弧结束的地方开始重新绘制，优化性能
        canvas.save();
        float currentAngle = mSweepAngle * mPercent;
        canvas.rotate(mStartAngle, mCenterPoint.x, mCenterPoint.y);     //旋转 起点位置还是之前画布里面的x,y点
        canvas.drawArc(mRectF, currentAngle, mSweepAngle - currentAngle, false, PauseProgressBgPaint);
        // 第一个参数 oval 为 RectF 类型，即圆弧显示区域
        // startAngle 和 sweepAngle  均为 float 类型，分别表示圆弧起始角度和圆弧度数
        // 3点钟方向为0度，顺时针递增
        // 如果 startAngle < 0 或者 > 360,则相当于 startAngle % 360
        // useCenter:如果为True时，在绘制圆弧时将圆心包括在内，通常用来绘制扇形
        canvas.drawArc(mRectF, 0, currentAngle, false, PauseProgressPaint);
        canvas.restore();
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if(event.getAction() == MotionEvent.ACTION_DOWN)
//        {
//            clickdown = true;
//            new LongTouchTask().execute();
//
//            Log.i("huahua", "按下");
//        }
//        else if(event.getAction() == MotionEvent.ACTION_UP)
//        {
//            clickdown = false;
//            Log.i("huahua", "弹起");
//        }
//        return super.onTouchEvent(event);
//    }
//
//    /**
//     * 使当前线程睡眠指定的毫秒数。
//     *
//     * @param time
//     *            指定当前线程睡眠多久，以毫秒为单位
//     */
//    private void sleep(int time) {
//        try {
//            Thread.sleep(time);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    class LongTouchTask extends AsyncTask<Void,Integer,Void>{
//        @Override
//        protected Void doInBackground(Void... voids) {
//            while(clickdown)
//            {
//                sleep(mtime);
//                publishProgress(0);             //更新主线程中的UI
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//
//        }
//
//        @Override
//        protected void onProgressUpdate(Integer... values) {
//            mListener.onLongTouch();
//        }
//    }


    public boolean dispatchTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mCounter++;
                isReleased = false;
                updateUi();
                postDelayed(mLongPressRunnable, 3100);// 按下 3秒后调用线程
                break;
            case MotionEvent.ACTION_UP:
                // 释放了
                isReleased = true;
                break;
        }
        return true;
    }

    public void updateUi(){
        timer=new Timer(true);
        TimerTask timerTask=new TimerTask() {
            @Override
            public void run() {
                if(!isReleased){
                    Message message=new Message();
                    message.what=UPDATE;
                    num=num+2;
//                    if(num==getMaxValue()){
//                        timer.cancel();
//                    }
                    handler.sendMessage(message);
                }else {
                    num=0;
                    Message message=new Message();
                    message.what=UPDATE;
                    timer.cancel();
                    handler.sendMessage(message);
                }
            }
        };
        timer.schedule(timerTask,0,100);
    }

    private Handler handler=new Handler(){

        public void handleMessage(Message message){
            switch (message.what){
                case UPDATE:
                    setValue(num);
                    break;
            }
        }
    };
    public float getValue() {
        return mValue;
    }

    /**
     * 设置当前值
     *
     * @param value
     */
    public void setValue(float value) {
        if (value > mMaxValue) {
            value = mMaxValue;
        }
        float start = mPercent;
        float end = value / mMaxValue;
        startAnimator(start, end, mAnimTime);
    }

    private void startAnimator(float start, float end, long animTime) {
        mAnimator = ValueAnimator.ofFloat(start, end);
        mAnimator.setDuration(animTime);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mPercent = (float) animation.getAnimatedValue();
                mValue = mPercent * mMaxValue;
                invalidate();
            }
        });
        mAnimator.start();
    }

    /**
     * 获取最大值
     *
     * @return
     */
    public float getMaxValue() {
        return mMaxValue;
    }

    /**
     * 设置最大值
     *
     * @param maxValue
     */
    public void setMaxValue(float maxValue) {
        mMaxValue = maxValue;
    }



    public long getAnimTime() {
        return mAnimTime;
    }

    public void setAnimTime(long animTime) {
        mAnimTime = animTime;
    }

    /**
     * 重置
     */
    public void reset() {
        startAnimator(mPercent, 0.0f, 1000L);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //释放资源
    }

//    /**
//     * 给长按btn控件注册一个监听器。
//     *
//     * @param listener
//     *            监听器的实现。
//     * @param time
//     *            多少毫秒时间间隔 来处理一次回调方法
//     */
//    public void setOnLongTouchListener(LongTouchListener listener, int time) {
//        mListener = listener;
//        mtime = time;
//
//    }
//
//    /**
//     * 长按监听接口，使用按钮长按的地方应该注册此监听器来获取回调。
//     */
//    public interface LongTouchListener {
//
//        /**
//         * 处理长按的回调方法
//         */
//        void onLongTouch();
//    }

}
