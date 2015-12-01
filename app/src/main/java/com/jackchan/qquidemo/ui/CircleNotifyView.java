package com.jackchan.qquidemo.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by ChenZuJie on 2015/11/28.
 *
 */
public class CircleNotifyView extends View {
    private static final String TAG = "CircleNotifyView";

    private Context mContext = null;
    private Paint mPaint = null;

    private int mNumber = 0;

    public CircleNotifyView(Context context) {
        super(context);
        init(context);
    }

    public CircleNotifyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CircleNotifyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mContext = context;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, mContext.getResources().getDisplayMetrics()));
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

    public void setNumber(int number){
        this.mNumber = number;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int radius = Math.min(width, height)/2;
        mPaint.setColor(Color.RED);
        canvas.drawCircle(width / 2, height / 2, radius, mPaint);
        mPaint.setColor(Color.WHITE);
        String content = Integer.toString(mNumber);
        float x = (width - mPaint.measureText(content)) / 2;
        float y = (height + mPaint.measureText(content, 0, 1))/ 2;
        Log.d(TAG , "x , y:" + x + ", " + y);
        canvas.drawText(content, x, y, mPaint);
    }
}
