package com.jackchan.qquidemo.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by ChenZuJie on 2015/11/24.
 */
public class HeaderScrollView extends LinearLayout implements IPersonalScrollView{
    private static final String TAG = "HeaderScrollView";
    private View mHeaderView = null;
    private Context mContext = null;
    private ScrollItemListView mListView = null;
    private int mOriginalHeaderHeight = 0;
    private int mHeaderHeight = 0;

    private int mLastInterceptX = 0;
    private int mLastInterceptY = 0;

    private int mDownHeaderHeight = 0;

    private boolean mNeedScrollToNormal = false;

    private boolean mDisallowedIntercept = false;

    public HeaderScrollView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public HeaderScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public HeaderScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init(){
        setOrientation(VERTICAL);
        if(null == mHeaderView){
            mHeaderView = new TextView(mContext);
            ((TextView)mHeaderView).setText("This is header view");
            ((TextView)mHeaderView).setTextSize(35);
        }
        addView(mHeaderView, 0);
        mListView = new ScrollItemListView(mContext);
        addView(mListView);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mHeaderView == null) {
            return;
        }
        measureChild(mHeaderView, widthMeasureSpec, heightMeasureSpec);
        if(0 == mOriginalHeaderHeight) {
            mOriginalHeaderHeight = mHeaderView.getMeasuredHeight();
            mHeaderHeight = mOriginalHeaderHeight;
        }
    }

    private boolean isFirstListItemTotallySeen(){
        View view = mListView.getChildAt(0);
        return view.getTop() >= 0;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        Log.d(TAG, "onInterceptTouchEvent ev:" + ev.getAction());
        //ListView有Item被滑动到最左边，需要滑动回正常位置
        if(mDisallowedIntercept){
            return false;
        }
        mNeedScrollToNormal = mListView.getNeedScrollToNormal(ev);
        if(mNeedScrollToNormal){
            return true;
        }
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        boolean intercept = false;
        if(ev.getAction() == MotionEvent.ACTION_DOWN){
            mLastInterceptX = (int) ev.getX();
            mLastInterceptY = (int) ev.getY();
            mDownHeaderHeight = mHeaderView.getMeasuredHeight();
        }
        else if(ev.getAction() == MotionEvent.ACTION_MOVE){
            int deltaX = x - mLastInterceptX;
            int deltaY = y - mLastInterceptY;
            if(Math.abs(deltaX) < Math.abs(deltaY)){
                if(getHeaderHeight() > 0){
                    intercept = true;
                }
                else if(getHeaderHeight() == 0 && deltaY >=0 && isFirstListItemTotallySeen()){
                    intercept = true;
                }
            }
        }
        return intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
//        Log.d(TAG, "onTouchEvent ev:" + ev.getAction());
        if(mDisallowedIntercept){
            return false;
        }
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        if(mNeedScrollToNormal){
            mListView.scrollToNormal(ev);
            return true;
        }
       if(ev.getAction() == MotionEvent.ACTION_MOVE) {
            int deltaX = x - mLastInterceptX;
            int deltaY = y - mLastInterceptY;
            if (Math.abs(deltaX) < Math.abs(deltaY) && getHeaderHeight() >= 0) {
                setHeaderHeight(mDownHeaderHeight, deltaY);
                return true;
            }
        }
        else if(ev.getAction() == MotionEvent.ACTION_UP){
           boolean up = getHeaderHeight() < mOriginalHeaderHeight * 0.5;
                   new ScrollToEndTask(up?ScrollToEndTask.SCROLL_UP:ScrollToEndTask.SCROLL_DOWN).execute();
       }
        return super.onTouchEvent(ev);
    }

    @Override
    public void disallowedIntercept(boolean disallowed) {
        mDisallowedIntercept = disallowed;
    }

    private class ScrollToEndTask extends AsyncTask<Void, Integer, Void> {
        public static final int SCROLL_UP = 1;
        public static final int SCROLL_DOWN = 2;
        int startHeight = getHeaderHeight();
        int direction = 0;
        public ScrollToEndTask(int direction) {
            super();
            this.direction = direction;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            int deltaY = direction == SCROLL_UP? -5: 5;
            try {
                while (getHeaderHeight() > 0 && getHeaderHeight() < mOriginalHeaderHeight){
                    publishProgress(deltaY);
                    Thread.sleep(10);
                    deltaY+=deltaY;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            setHeaderHeight(startHeight, values[0]);
        }
    }

    private void setHeaderHeight(int startHeight, int deltaY){
        mHeaderHeight = startHeight + deltaY;
        if(mHeaderHeight < 0){
            mHeaderHeight = 0;
        }
        else if(mHeaderHeight > mOriginalHeaderHeight){
            mHeaderHeight = mOriginalHeaderHeight;
        }
        ViewGroup.LayoutParams params = mHeaderView.getLayoutParams();
        params.height = mHeaderHeight;
        mHeaderView.setLayoutParams(params);

    }

    private int getHeaderHeight(){
        return mHeaderHeight;
    }

    public void setAdapter(BaseAdapter adapter){
        if(null != mListView){
            mListView.setAdapter(adapter);
        }
    }

    public void setHeaderView(View view){
        if(mHeaderView == view || null == view){
            return;
        }
        removeView(mHeaderView);
        mHeaderView = view;
        mHeaderHeight = 0;
        addView(mHeaderView, 0);
        invalidate();
    }

}
