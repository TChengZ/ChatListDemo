package com.jackchan.qquidemo.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by ChenZuJie on 2015/11/28.
 */
public class CircleNofityView extends View {
    private static final String TAG = "CircleNofityView";

    private Context mContext = null;
    private Canvas mCanvas = null;
    private Paint mPaint = null;

    private int mNumber = 0;

    public CircleNofityView(Context context) {
        super(context);
        init(context);
    }

    public CircleNofityView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CircleNofityView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mContext = context;
        mCanvas = new Canvas();
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
