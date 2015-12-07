package com.jackchan.qquidemo.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ScrollView;

/**
 * Created by ChenZuJie on 2015/11/28.
 *
 */
public class CircleNotifyView extends View {
    private static final String TAG = "CircleNotifyView";

    private Context mContext = null;
    private Paint mPaint = null;

    private int mNumber = 0;

    private ViewGroup mDecorView = null;

    private int mStatusHeight = 0;

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
        mDecorView = (ViewGroup)((Activity)mContext).getWindow().getDecorView().findViewById(android.R.id.content);
        mStatusHeight = getStatusBarHeight();
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
            Log.d(TAG, "status_bar_height:" + result);
        }
        result += mDecorView.getTop();
        Log.d(TAG, "getTop:" + mDecorView.getTop());
        Log.d(TAG, "result height:" + result);
        return result;
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
        Log.d(TAG, "rawX:" + event.getRawX() + " rawY:" + event.getRawY());
        Log.d(TAG, "mStatusHeight:" + mStatusHeight);
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            if(mDecorView.getChildCount() == 1){
                setScrollParentDisallowIntercept(true);
                addNotify(event.getRawX(), event.getRawY() - mStatusHeight);
            }
        }
        else if(event.getAction() == MotionEvent.ACTION_MOVE){
            updateNotify(event.getRawX(), event.getRawY() - mStatusHeight);
        }
        else if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL){
            setScrollParentDisallowIntercept(false);
            removeNotify();
        }
        return true;
    }

    private void setScrollParentDisallowIntercept(boolean disallowIntercept){
        View target = this;
        while (true) {
            View parent;
            try {
                parent = (View) target.getParent();
            } catch (Exception e) {
                return;
            }
            if (parent == null)
                return;
            if (parent instanceof ListView || parent instanceof ScrollView) {
                ((ViewGroup)parent).requestDisallowInterceptTouchEvent(disallowIntercept);
            }
            else if(parent instanceof IPersonalScrollView){
                ((IPersonalScrollView)parent).disallowedIntercept(disallowIntercept);
            }
            target = parent;
        }
    }

    private void addNotify(float x, float y){
        Log.d(TAG, "x:" + x + " y:" + y);
        CircleNotifyView view = new CircleNotifyView(mContext);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(this.getWidth(), this.getHeight());
        view.setLayoutParams(params);
        view.setX(x);
        view.setY(y);
        view.setNumber(this.mNumber);
        mDecorView.addView(view,1);
    }

    private void updateNotify(float x, float y){
        Log.d(TAG, "x:" + x + " y:" + y);
        CircleNotifyView view = (CircleNotifyView) mDecorView.getChildAt(1);
        view.setX(x);
        view.setY(y);
    }

    private void removeNotify(){
        mDecorView.removeViewAt(1);
    }

}
