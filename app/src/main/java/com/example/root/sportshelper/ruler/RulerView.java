package com.example.root.sportshelper.ruler;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Scroller;
import com.example.root.sportshelper.R;
import com.example.root.sportshelper.utils.MiscUtil;

/**
 * Created by root on 17-7-29.
 */

public class RulerView extends View{
    //id值为1表示身高,2表示体重,3表示年龄
    private int id;
    //最大刻度
    private int maxValue;
    //最小刻度
    private int minValue;
    //刻度字体大小
    private int scaleTextSize;
    //刻度字体颜色
    private int scaleTextColor;
    //刻度超过颜色
    private int scaleSelectColor;
    //刻度超过背景
    private int scaleSelectBackgroundColor;
    //刻度未超颜色
    private int scaleUnSelectColor;
    //标记颜色
    private int cursorColor;
    //标尺开始位置
    private int currLocation = 0;
    //一屏显示Item
    private int showItemSize;
    //一个刻度的大小
    private int oneItemValue;
    //画线Paint
    private Paint paint;
    //画字Paint
    private Paint paintText;
    //屏幕宽度
    private int screenWidth;
    //尺子控件总宽度
    private float viewWidth;
    //尺子控件总高度
    private float viewHeight;
    //刻度宽度
    private int scaleWidth;
    //刻度高度
    private float scaleHeight = 32;
    //滚动器
    private Scroller scroller;
    // 手势识别
    private GestureDetector gestureDetector;

    //滚动偏移量
    private int scrollingOffset;
    // 是否在滚动
    private boolean isScrollingPerformed;
    // 最低速度
    private static final int MIN_DELTA_FOR_SCROLLING = 1;
    //最后一次滚动到哪
    private int lastScrollX;
    // 消息
    private final int MESSAGE_SCROLL = 0;
    //回调函数
    private OnRulerChangeListener onRulerChangeListener;

    private int m_max_scrollX;
    private Typeface newFont;      //字体类型

    public void setOnRulerChangeListener(OnRulerChangeListener onRulerChangeListener) {
        this.onRulerChangeListener = onRulerChangeListener;
    }

    public void setCurrLocation(int currLocation) {
        this.currLocation = currLocation;
    }

    public RulerView(Context context){
        this(context,null);
    }

    public RulerView(Context context,AttributeSet attrs){
        this(context, attrs,0);
    }

    public RulerView(Context context, AttributeSet attrs,int defStyleAttr) {
        super(context, attrs,defStyleAttr);
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;               //获得手机分辨率

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RulerView);

        id = typedArray.getInteger(R.styleable.RulerView_id, 1);
        maxValue = typedArray.getInteger(R.styleable.RulerView_max_value, 200);
        minValue = typedArray.getInteger(R.styleable.RulerView_min_value, 20);
        scaleTextSize = typedArray.getDimensionPixelOffset(R.styleable.RulerView_scale_text_size, MiscUtil.sp2px(context,10));
        scaleTextColor = typedArray.getColor(R.styleable.RulerView_scale_text_color, getResources().getColor(R.color.color_ffb9b9b9));
        scaleSelectColor = typedArray.getColor(R.styleable.RulerView_scale_select_color, getResources().getColor(R.color.color_76e4ff));
        scaleSelectBackgroundColor = typedArray.getColor(R.styleable.RulerView_scale_select_background_color, getResources().getColor(R.color.color_6676e4ff));
        scaleUnSelectColor = typedArray.getColor(R.styleable.RulerView_scale_unselect_color, getResources().getColor(R.color.color_d8d8d8));
        cursorColor = typedArray.getColor(R.styleable.RulerView_tag_color, getResources().getColor(R.color.color_ff5555));
        if (currLocation == 0) {
            currLocation = typedArray.getDimensionPixelOffset(R.styleable.RulerView_start_location, 0);
        }
        showItemSize = typedArray.getInteger(R.styleable.RulerView_show_item_size, 5);
        oneItemValue = typedArray.getInteger(R.styleable.RulerView_show_item_size, 1);
        typedArray.recycle();
        //一个刻度的宽度
        scaleWidth = (screenWidth / (showItemSize * 10));
        //尺子长度总的个数*一个的宽度
        viewWidth = maxValue / oneItemValue * scaleWidth;
        //滚动计算器
        scroller = new Scroller(context);
        //手势解析器
        gestureDetector = new GestureDetector(context, gestureListener);
        gestureDetector.setIsLongpressEnabled(false);
        newFont = Typeface.createFromAsset(context.getAssets(), "fonts/Black_Simplified.ttf");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawScale(canvas);          //画刻度
        //drawCursor(canvas);         //画中线
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        m_max_scrollX=MeasureSpec.getSize(widthMeasureSpec);
    }
    private void drawCursor(Canvas canvas) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(3);
        paint.setColor(cursorColor);
        canvas.drawLine(screenWidth / 2, viewHeight / 5, screenWidth / 2, viewHeight, paint);
    }

    private void drawScale(Canvas canvas) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(2);
        //计算游标开始绘制的位置
        float startLocation = (screenWidth / 2) - ((scaleWidth * (currLocation / oneItemValue)))-75;
        for (int i = minValue / oneItemValue; i <= maxValue / oneItemValue; i++) {
            //判断当前刻度是否小于当前刻度
            if (i * oneItemValue <= currLocation) {
                paint.setColor(scaleSelectColor);
            } else {
                paint.setColor(scaleUnSelectColor);
            }
            float location = startLocation + i * scaleWidth;
            if (i % 5 == 0) {
                canvas.drawLine(location, viewHeight - scaleHeight-25, location, viewHeight-25, paint);
                paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
                paintText.setTextSize(scaleTextSize);
                paintText.setTypeface(newFont);
                if (i * oneItemValue <= currLocation) {
                    paintText.setColor(scaleSelectColor);
                } else {
                    paintText.setColor(scaleTextColor);
                }
                String drawStr = oneItemValue * i + "";
                Rect bounds = new Rect();
                paintText.getTextBounds(drawStr, 0, drawStr.length(), bounds);      //获得绘制文本的宽和高
                canvas.drawText(drawStr, location - bounds.width() / 2, viewHeight-5, paintText);
            } else {
                canvas.drawLine(location, viewHeight - scaleHeight+6-25, location, viewHeight-6-25, paint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        gestureDetector.onTouchEvent(event);

        return true;
    }


    private GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {

        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            if (!isScrollingPerformed) {        //是否滚动
                isScrollingPerformed = true;
            }
//            int scrollx=getScrollX();
//            int minScrollX=-scrollx;
//            int maxScrollY=m_max_scrollX-scrollx;
//            if(Math.abs(distanceX)>maxScrollY){
//                distanceX=maxScrollY;
//            }else if(Math.abs(distanceX)<minScrollX){
//                distanceX=minScrollX;
//            }
            doScroll((int) -distanceX);
            invalidate();
            return true;
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            lastScrollX = getCurrentItem() * getItemWidth() + scrollingOffset;
            int maxX = getItemsCount()
                    * getItemWidth();
            int minX = 0;
            scroller.fling(lastScrollX, 0, (int) (-velocityX / 1.5), 0, minX, maxX, 0, 0);
            setNextMessage(MESSAGE_SCROLL);
            return true;
        }
    };

    private void setNextMessage(int message) {
        animationHandler.removeMessages(MESSAGE_SCROLL);
        animationHandler.sendEmptyMessage(message);
    }

    // 动画处理
    private Handler animationHandler = new Handler() {
        public void handleMessage(Message msg) {
            scroller.computeScrollOffset();
            int currX = scroller.getCurrX();
            int delta = lastScrollX - currX;
            lastScrollX = currX;
            if (delta != 0) {
                doScroll(delta);
            }
            // 滚动还没有完成，到最后，完成手动
            if (Math.abs(currX - scroller.getFinalX()) < MIN_DELTA_FOR_SCROLLING) {
                scroller.forceFinished(true);
            }
            if (!scroller.isFinished()) {
                animationHandler.sendEmptyMessage(msg.what);
            } else {
                finishScrolling();
            }
        }
    };


    private void finishScrolling() {
        if (isScrollingPerformed) {
            isScrollingPerformed = false;
        }
        invalidate();
    }

    private void doScroll(int delta) {
        //偏移量叠加
        scrollingOffset += delta;
        //总共滚动了多少个Item
        int count = scrollingOffset / getItemWidth();
        //当前位置
        int pos = getCurrentItem() - count;
        //限制滚到范围
        if (isScrollingPerformed) {
            if (pos < getminCount()) {
                count = 0;
                pos = getminCount();
                scrollingOffset=0;
            } else if (pos >= getItemsCount()) {
                count = getCurrentItem() - getItemsCount() + 1;
                pos = getItemsCount();
                scrollingOffset=0;
            }
        }
        int offset = scrollingOffset;
        //移动了一个Item的距离，就更新页面
        if (pos != getCurrentItem()) {
            setCurrentItem(pos);
        }else if(pos==getItemsCount()){
            setCurrentItem(pos);
        }else if(pos==getminCount()){
            setCurrentItem(pos);
        }
        // 重新更新一下偏移量
        scrollingOffset = offset - count * getItemWidth();

    }

    public int getItemWidth() {
        return scaleWidth;
    }

    public int getCurrentItem() {
        return currLocation / oneItemValue;
    }

    public int getItemsCount() {
        return maxValue / oneItemValue;
    }

    public int getminCount() {
        return minValue / oneItemValue;
    }

    public void setCurrentItem(int index) {
        scrollingOffset = 0;
        currLocation = index * oneItemValue;
        invalidate();
        if (onRulerChangeListener != null) {
            onRulerChangeListener.onChanged(id,currLocation);
        }
    }

    public int getCurrLocation() {
        return currLocation;
    }
}
