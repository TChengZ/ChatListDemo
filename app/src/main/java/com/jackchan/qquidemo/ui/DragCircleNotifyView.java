package com.jackchan.qquidemo.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by ChenZuJie on 2015/12/7.
 */
public class DragCircleNotifyView extends View {
    private static final String TAG = "DragCircleNotifyView";

    private Context mContext = null;
    private Paint mPaint = null;

    private int mNumber = 0;

    private float mStartDragX = 0;
    private float mStartDragY = 0;
    private float mCurrentX = 0;
    private float mCurrentY = 0;

    private int mRadius = 0;

    private float mMaxDistance = 0;
    /**
     * 脱离原始距离与屏幕最小宽度3/4值的比例
     */
    private float mRatio = 0;

    public DragCircleNotifyView(Context context) {
        super(context);
        init(context);
    }

    public DragCircleNotifyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DragCircleNotifyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mContext = context;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, mContext.getResources().getDisplayMetrics()));
        WindowManager wm = ((Activity)mContext).getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        mMaxDistance = Math.min(width, height);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int width = getWidthSize(widthSpecMode, widthSpecSize);
        int height = getHeightSize(heightSpecMode, heightSpecSize);
        setMeasuredDimension(width, height);
    }

    private int getWidthSize(int widthSpecMode, int widthSpecSize){
        float m18dp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, mContext.getResources().getDisplayMetrics());
        int width;
        if(widthSpecMode == MeasureSpec.EXACTLY){
            width = widthSpecSize;
        }
        else{
            width = (int)(getPaddingLeft() + getPaddingRight() + m18dp);
        }
        return width;
    }

    private int getHeightSize(int heightSpecMode, int heightSpecSize){
        float m18dp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, mContext.getResources().getDisplayMetrics());
        int height;
        if(heightSpecMode == MeasureSpec.EXACTLY){
            height = heightSpecSize;
        }
        else{
            height = (int)(getPaddingTop() + getPaddingBottom() + m18dp);
        }
        return height;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.RED);
        if(mCurrentX > 0 && mCurrentY > 0) {
            float oldStrokeWidth = mPaint.getStrokeWidth();
            if(mStartDragX > 0 && mStartDragY > 0 && mRadius > 0){
                float distance = getDistance(mStartDragX, mStartDragY, mCurrentX, mCurrentY);
                mRatio = 1 - distance/(mMaxDistance*0.75f);//距离超过屏幕最小宽度的3/4就不划线
                if(mRatio > 0) {
                    mPaint.setStrokeWidth(mRadius * mRatio);
                    canvas.drawLine(mCurrentX, mCurrentY, mStartDragX, mStartDragY, mPaint);
                }
            }
            canvas.drawCircle(mCurrentX, mCurrentY, mRadius, mPaint);
            mPaint.setStrokeWidth(oldStrokeWidth);
            mPaint.setColor(Color.WHITE);
            String content = Integer.toString(mNumber);
            float x = mCurrentX - (mPaint.measureText(content)/2);
            float y = mCurrentY + (mPaint.measureText(content, 0, 1)/ 2);
            canvas.drawText(content, x, y, mPaint);
        }
    }

    private float getDistance(float startX, float startY, float endX, float endY){
        double xDis = Math.pow(Math.abs(startX - endX),2);
        double yDis  = Math.pow(Math.abs(startY - endY),2);
        return (float)Math.sqrt(xDis + yDis);
    }

    public void setNumber(int number){
        this.mNumber = number;
    }

    public void setStartPoint(float x, float y){
        this.mStartDragX = x;
        this.mStartDragY = y;
    }

    public void setCurrentPoint(float x, float y){
        this.mCurrentX = x;
        this.mCurrentY = y;
    }

    public void setRadius(int radius){
        this.mRadius = radius;
    }

    public float getRatio(){
        return mRatio;
    }

    public void refresh(){
        invalidate();
    }
}
