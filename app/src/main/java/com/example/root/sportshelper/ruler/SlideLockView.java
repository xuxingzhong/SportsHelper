package com.example.root.sportshelper.ruler;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.root.sportshelper.R;

/**
 * 滑动解锁
 * Created by root on 17-9-12.
 */

public class SlideLockView extends View {
    private Bitmap mLockBitmap;
    private int mLockDrawableId;                        //滑动解锁的图片id
    private Paint mPaint;                               //画图片
    private TextPaint mSlideLockPaint;                       //画文字滑动解锁
    private int mLockRadius;                            //圆弧半径
    private String mTipText;                            //文字滑动解锁
    private int mTipsTextSize;                          //文字大小
    private int mTipsTextColor;                         //文字颜色
    private Rect mTipsTextRect = new Rect();

    private float viewHeight;           //控件高度
    private float viewWidth;            //控件宽度

    private float mLocationX;                           //位置
    private boolean mIsDragable = false;                //是否拖动
    private OnLockListener mLockListener;

    private Typeface blackSimpleFont;          //方正兰亭黑简体

    public SlideLockView(Context context) {
        this(context, null);

    }

    public SlideLockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideLockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray tp = context.obtainStyledAttributes(attrs, R.styleable.SlideLockView, defStyleAttr, 0);
        mLockDrawableId = tp.getResourceId(R.styleable.SlideLockView_lock_drawable, -1);
        mLockRadius = tp.getDimensionPixelOffset(R.styleable.SlideLockView_lock_radius, 1);
        mTipText = tp.getString(R.styleable.SlideLockView_lock_tips_tx);
        mTipsTextSize = tp.getDimensionPixelOffset(R.styleable.SlideLockView_locl_tips_tx_size,12);
        mTipsTextColor = tp.getColor(R.styleable.SlideLockView_lock_tips_tx_color, Color.BLACK);

        tp.recycle();

        if (mLockDrawableId == -1) {
            throw new RuntimeException("未设置滑动解锁图片");
        }

        init(context);

    }

    private void init(Context context) {
        blackSimpleFont=Typeface.createFromAsset(context.getAssets(),"fonts/Black_Simplified.ttf");
        mPaint = new Paint();
        mPaint.setAntiAlias(true);                                          //设置抗锯齿
        mPaint.setTextSize(mTipsTextSize);
        mPaint.setColor(mTipsTextColor);

        mSlideLockPaint=new TextPaint();
        mSlideLockPaint.setAntiAlias(true);
        mSlideLockPaint.setTextSize(mTipsTextSize);
        mSlideLockPaint.setColor(mTipsTextColor);
        mSlideLockPaint.setTypeface(blackSimpleFont);

        mLockBitmap = BitmapFactory.decodeResource(context.getResources(), mLockDrawableId);
        int oldSize = mLockBitmap.getHeight();
        int newSize = mLockRadius * 2;
        float scale = newSize * 1.0f / oldSize;
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        mLockBitmap = Bitmap.createBitmap(mLockBitmap, 0, 0, oldSize, oldSize, matrix, true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        viewWidth =MeasureSpec.getSize(widthMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        canvas.getClipBounds(mTipsTextRect);                                //得到裁剪的边界
//        int cHeight = mTipsTextRect.height();
//        int cWidth = mTipsTextRect.width();
//        mSlideLockPaint.setTextAlign(Paint.Align.LEFT);
//        mSlideLockPaint.getTextBounds(mTipText, 0, mTipText.length(), mTipsTextRect);
//        float x = cWidth / 2f - mTipsTextRect.width() / 2f - mTipsTextRect.left;
//        float y = cHeight / 2f + mTipsTextRect.height() / 2f - mTipsTextRect.bottom;

        mSlideLockPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(mTipText, viewWidth/2+10, viewHeight/2+5, mSlideLockPaint);

        int rightMax = getWidth() - mLockRadius * 2;                        //最右边
        if (mLocationX < 0) {
            canvas.drawBitmap(mLockBitmap, 0, 0, mPaint);
        } else if (mLocationX > rightMax) {
            canvas.drawBitmap(mLockBitmap, rightMax, 0, mPaint);
        } else {
            canvas.drawBitmap(mLockBitmap, mLocationX, 0, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                float xPos = event.getX();
                float yPos = event.getY();
                if (isTouchLock(xPos, yPos)) {
                    mLocationX = xPos - mLockRadius;
                    mIsDragable = true;
                    invalidate();
                } else {
                    mIsDragable = false;
                }
                return true;
            }
            case MotionEvent.ACTION_MOVE: {

                if (!mIsDragable) return true;

                int rightMax = getWidth() - mLockRadius * 2;
                resetLocationX(event.getX(),rightMax);
                invalidate();

                if (mLocationX >= rightMax){
                    mIsDragable = false;
                    mLocationX = 0;
                    invalidate();
                    if (mLockListener != null){
                        mLockListener.onOpenLockSuccess();
                    }
                    Log.e("AnimaterListener","解锁成功");
                }

                return true;
            }
            case MotionEvent.ACTION_UP: {
                if (!mIsDragable) return true;
                resetLock();
                break;
            }
        }
        return super.onTouchEvent(event);
    }

    //重置位置
    private void resetLock(){
        ValueAnimator anim = ValueAnimator.ofFloat(mLocationX,0);
        anim.setDuration(300);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mLocationX = (Float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        anim.start();
    }

    //更新LocationX位置
    private void resetLocationX(float eventXPos,float rightMax){

        float xPos = eventXPos;
        mLocationX = xPos - mLockRadius;
        if (mLocationX < 0) {
            mLocationX = 0;
        }else if (mLocationX >= rightMax) {
            mLocationX = rightMax;
        }
    }

    //判断是不是点击锁
    private boolean isTouchLock(float xPos, float yPox) {
        float centerX = mLocationX + mLockRadius;
        float diffX = xPos - centerX;
        float diffY = yPox - mLockRadius;

        return diffX * diffX + diffY * diffY < mLockRadius * mLockRadius;
    }


    public void setmLockListener(OnLockListener mLockListener) {
        this.mLockListener = mLockListener;
    }

    public interface OnLockListener{
        void onOpenLockSuccess();
    }
}
