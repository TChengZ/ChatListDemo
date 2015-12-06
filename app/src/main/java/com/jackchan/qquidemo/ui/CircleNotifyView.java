package com.jackchan.qquidemo.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

/**
 * Created by ChenZuJie on 2015/11/28.
 *
 */
public class CircleNotifyView extends RelativeLayout {
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
    WindowManager wmManager=null;
    private ViewGroup mDectorView = null;
    private void init(Context context){
        setWillNotDraw(false);
        mContext = context;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, mContext.getResources().getDisplayMetrics()));
        wmManager=(WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mDectorView = (ViewGroup)((Activity)mContext).getWindow().getDecorView().findViewById(android.R.id.content);
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
        canvas.drawText(content, x, y, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "x:" + event.getX() + " y:" + event.getY());
        Log.d(TAG, "rawx:" + event.getRawX() + " rawy:" + event.getRawY());
        if(mDectorView.getChildCount() == 1){
            CircleNotifyView view = new CircleNotifyView(mContext);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(this.getWidth(), this.getHeight());
            view.setLayoutParams(params);
            view.setX(event.getRawX() - 10);
            view.setX(event.getRawY() - 10);
            view.setNumber(this.mNumber);
            mDectorView.addView(view,1);
        }
        else{
            CircleNotifyView view = (CircleNotifyView)mDectorView.getChildAt(1);
            view.setX(event.getRawX());
            view.setY(event.getRawY());
        }
        return true;
    }
}
