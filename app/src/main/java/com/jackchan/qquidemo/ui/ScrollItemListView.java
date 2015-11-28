package com.jackchan.qquidemo.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

/**
 * Created by ChenZuJie on 2015/11/22.
 * A ListView which its item can be scroll horizontal
 */
public class ScrollItemListView extends ListView{

    private static final String TAG = "ScrollItemListView";
    public static final String DELETE_TAG = "delete";
    public static final String NOR_DELETE_TAG = "nor_delete";
    private int mLastInterceptX = 0;
    private int mLastInterceptY = 0;
    //做移动最大距离
    private int mMaxLeftMargin = 0;
    //左移View
    private View mScrollView = null;
    //Down点击时左边View的marginLeft值
    private int mDownLeftMargin = 0;

    private int mHasScrollItemPos = -1;

    private boolean mNeedScrollToNormal = false;

    public ScrollItemListView(Context context) {
        super(context);
    }

    public ScrollItemListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollItemListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
    //    Log.d(TAG, "onTouchEvent ev:" + ev.getAction());
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        int pos = pointToPosition(x, y);
        if(ev.getAction() == MotionEvent.ACTION_MOVE) {
            int deltaX = x - mLastInterceptX;
            int deltaY = y - mLastInterceptY;
            if (Math.abs(deltaX) > Math.abs(deltaY)) {
                dragItem(x, pos);
                return true;
            }
        }
        else if(ev.getAction() == MotionEvent.ACTION_UP){
            int nowLeftMargin = mDownLeftMargin + x - mLastInterceptX;
            scrollToEnd(nowLeftMargin);
            if(nowLeftMargin < mMaxLeftMargin*0.5){
                setNeedScrollToNormal(true, pos);
            }
        }
        return super.onTouchEvent(ev);
    }

    private void scrollToEnd(int nowLeftMargin){
        boolean flag = (Math.abs(nowLeftMargin)  < Math.abs(mMaxLeftMargin) * 0.5);
        new ScrollToEndTask(mScrollView,
                flag? ScrollToEndTask.SCROLL_RIGHT: ScrollToEndTask.SCROLL_LEFT).execute();
    }

    private void setNeedScrollToNormal(boolean flag, int pos){
        mNeedScrollToNormal = flag;
        mHasScrollItemPos = pos;
    }
    public boolean getNeedScrollToNormal(MotionEvent ev){
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        int pos = pointToPosition(x, y);
        Log.d(TAG, "ev:" + ev.getAction() + " pos:" + pos + " mHasScrollItemPos:" + mHasScrollItemPos);
        if(ev.getAction() == MotionEvent.ACTION_DOWN  && pos == mHasScrollItemPos){
            setNeedScrollToNormal(false, -1);
            return false;
        }
        return mNeedScrollToNormal;
    }

    public void scrollToNormal(MotionEvent ev){
        if(ev.getAction() == MotionEvent.ACTION_DOWN){
            new ScrollToEndTask(mScrollView, ScrollToEndTask.SCROLL_RIGHT).execute();
        }
        else if(ev.getAction() == MotionEvent.ACTION_UP){
            if(mNeedScrollToNormal) {
                mNeedScrollToNormal = false;
                mHasScrollItemPos = -1;
            }
        }
    }

    /**
     * 获取滑动所需的数据
     * @param pos
     */
    private void getScrollData(int pos){
        if(pos > -1){
            View view = getChildAt(pos - getFirstVisiblePosition());
            mScrollView = view.findViewWithTag(NOR_DELETE_TAG);
            View delete = view.findViewWithTag(DELETE_TAG);
            if(null != delete) {
                mMaxLeftMargin = delete.getWidth() * (-1);
            }
            if(null != mScrollView) {
                MarginLayoutParams params = (MarginLayoutParams)mScrollView.getLayoutParams();
                params.width = mScrollView.getWidth();
                mDownLeftMargin = params.leftMargin;
            }
        }
    }

    private void dragItem(int nowX, int pos) {
        if (pos > -1 && null != mScrollView) {
            MarginLayoutParams params = (MarginLayoutParams)mScrollView.getLayoutParams();
            mScrollView.setLayoutParams(params);
            int leftMargin = mDownLeftMargin + nowX - mLastInterceptX;
            leftMargin = Math.min(0, leftMargin);
            leftMargin = Math.max(leftMargin, mMaxLeftMargin);//leftMargin 介于0和mMaxLeftMargin之间
            params.leftMargin = leftMargin;
        }
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        Log.d(TAG, "onInterceptTouchEvent ev:" + ev.getAction());
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        int pos = pointToPosition(x, y);
        boolean intercept = false;
        if(ev.getAction() == MotionEvent.ACTION_DOWN){
            mLastInterceptX = (int) ev.getX();
            mLastInterceptY = (int) ev.getY();
            getScrollData(pos);
        }
        else if(ev.getAction() == MotionEvent.ACTION_MOVE){
            int deltaX = x - mLastInterceptX;
            int deltaY = y - mLastInterceptY;
            if(Math.abs(deltaX) > Math.abs(deltaY)){
                intercept = true;
            }
        }
        return intercept;
    }

    private class ScrollToEndTask extends AsyncTask<Void, Integer, Void>{
        public static final int SCROLL_LEFT = 1;
        public static final int SCROLL_RIGHT = 2;
        View view;
        MarginLayoutParams marginLayoutParams = null;
        int endMargin = 0;
        int direction = 0;
        public ScrollToEndTask(View view, int direction) {
            super();
            this.view = view;
            this.marginLayoutParams = (MarginLayoutParams)view.getLayoutParams();
            this.direction = direction;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            int margin = marginLayoutParams.leftMargin;
            try {
                while (margin < 0 && margin >= mMaxLeftMargin){
                    if(direction == SCROLL_LEFT){
                        margin-=10;
                    }
                    else if(direction == SCROLL_RIGHT){
                        margin+=10;
                    }

                        Thread.sleep(10);
                    publishProgress(margin);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int margin = (int)(values[0]);
            if(margin > 0){
                margin = 0;
            }
            else if(margin < mMaxLeftMargin){
                margin = mMaxLeftMargin;
            }
            marginLayoutParams.leftMargin = margin;
            view.setLayoutParams(marginLayoutParams);
        }
    }
}
