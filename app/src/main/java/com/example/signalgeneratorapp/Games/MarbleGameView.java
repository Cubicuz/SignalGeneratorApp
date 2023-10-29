package com.example.signalgeneratorapp.Games;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class MarbleGameView extends View {
    private float minMax(float min, float value, float max) {
        return Math.max(min, (Math.min(value, max)));
    }

    private Drawable mExampleDrawable;

    private Paint mPaint;

    private Rect mField;
    private int mBallX=40;
    private int mBallY=60;
    private final int mBallRadius=20;
    private int mFieldWidth;
    private int mFieldHeight;
    private final int mPadding = 5;

    private float mBallXf, mBallYf;

    private int paddingLeft = 0;
    private int paddingTop = 0;
    private int contentWidth = 0;
    private int contentHeight = 0;

    public MarbleGameView(Context context) {
        super(context);
        init();
    }

    public MarbleGameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MarbleGameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mField = new Rect();
    }

    /**
     * parameters have to be between -1 and 1
     */
    public void update(float ballx, float bally) {
        // validation
        mBallXf = minMax(-1, ballx, 1);
        mBallYf = minMax(-1, bally, 1);
        calculateBallPosition();
    }

    private void calculateBallPosition() {
        mBallX = (int) (((mBallXf+1)/2) * (mFieldWidth - 2 * mBallRadius) + mBallRadius);
        mBallY = (int) (((mBallYf+1)/2) * (mFieldHeight - 2 * mBallRadius) + mBallRadius);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(0xffffffff);


        mField.set(mPadding, mPadding, contentWidth - mPadding - mPadding, contentHeight - mPadding - mPadding);
        mPaint.setColor(0xff000000);
        canvas.drawRect(mField, mPaint);
        mPaint.setColor(0xffff0000);
        canvas.drawCircle(mBallX + mPadding, mBallY + mPadding, mBallRadius, mPaint);

        // Draw the example drawable on top of the text.
        if (mExampleDrawable != null) {
            mExampleDrawable.setBounds(paddingLeft, paddingTop,
                    paddingLeft + contentWidth, paddingTop + contentHeight);
            mExampleDrawable.draw(canvas);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mFieldHeight = h - (2 * mPadding);
        mFieldWidth = w - (2 * mPadding);

        paddingLeft = getPaddingLeft();
        paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();
        contentWidth = getWidth() - paddingLeft - paddingRight;
        contentHeight = getHeight() - paddingTop - paddingBottom;

        calculateBallPosition();
    }
}