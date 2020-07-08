package com.example.timecountdownapp;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class TimeCountDownView extends AppCompatTextView {

    private static final String DEFAULT_TIME = "0";

    private int mFirstDigit = 0;
    private int mSecondDigit = 0;
    private String mFirstDigitText, mFirstNextDigitText;
    private String mSecondDigitText, mSecondNextDigitText;

    private Paint mTextPaint;
    private PointF mFirstDigitBaseline, mFirstNextDigitBaseline;
    private PointF mSecondDigitBaseline, mSecondNextDigitBaseline;
    private int mPreMeasuredWidth;
    private int mPreMeasuredHeight;
    private float mDefaultBaselineY;
    private float mDefaultOuterBaselineY;

    private boolean mHasFirstDigitChanged = false;
    private boolean mHasSecondDigitChanged = false;

    public TimeCountDownView(Context context) {
        this(context, null);
    }

    public TimeCountDownView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeCountDownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mFirstDigitBaseline = new PointF();
        mFirstNextDigitBaseline = new PointF();
        mSecondDigitBaseline = new PointF();
        mSecondNextDigitBaseline = new PointF();

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.textSize));
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        //mTextPaint.setColor(ContextCompat.getColor(context, R.color.textBadge));
        mTextPaint.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));

        calculateLayoutAndDrawParams();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.i("debug_test","--> " + mTextPaint.measureText(DEFAULT_TIME));
        setMeasuredDimension(mPreMeasuredWidth, mPreMeasuredHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mHasFirstDigitChanged) {
            // Go above top of this view
            mFirstDigitBaseline.y -= 16;
            mFirstNextDigitBaseline.y -= 16;
            if (mFirstNextDigitBaseline.y <= mDefaultBaselineY) {
                mHasFirstDigitChanged = false;
                // Swap again so the "next" digit will become the current settled digit :)
                mFirstDigitBaseline.y = mDefaultBaselineY;
                mFirstNextDigitBaseline.y = mDefaultOuterBaselineY;

                mFirstDigitText = mFirstNextDigitText;
            }
            canvas.drawText(mFirstNextDigitText, mFirstNextDigitBaseline.x, mFirstNextDigitBaseline.y, mTextPaint);
        }
        canvas.drawText(mFirstDigitText, mFirstDigitBaseline.x, mFirstDigitBaseline.y, mTextPaint);

        if (mHasSecondDigitChanged) {
            // Go above top of this view
            mSecondDigitBaseline.y -= 16;
            mSecondNextDigitBaseline.y -= 16;
            if (mSecondNextDigitBaseline.y <= mDefaultBaselineY) {
                mHasSecondDigitChanged = false;
                // Swap again so the "next" digit will become the current settled digit :)
                mSecondDigitBaseline.y = mDefaultBaselineY;
                mSecondNextDigitBaseline.y = mDefaultOuterBaselineY;

                mSecondDigitText = mSecondNextDigitText;
            }
            canvas.drawText(mSecondNextDigitText, mSecondNextDigitBaseline.x, mSecondNextDigitBaseline.y, mTextPaint);
        }
        canvas.drawText(mSecondDigitText, mSecondDigitBaseline.x, mSecondDigitBaseline.y, mTextPaint);

        postInvalidateDelayed(16);
    }

    public void setNextTime(int time) {
        int firstDigit = time / 10;
        int secondDigit = time % 10;

        if (mFirstDigit != firstDigit) {
            mHasFirstDigitChanged = true;
            mFirstDigit = firstDigit;
            mFirstNextDigitText = String.valueOf(firstDigit);

            mFirstDigitBaseline.y = mDefaultBaselineY;
            mFirstNextDigitBaseline.y = mDefaultOuterBaselineY;
        }
        if (mSecondDigit != secondDigit) {
            mHasSecondDigitChanged = true;
            mSecondDigit = secondDigit;
            mSecondNextDigitText = String.valueOf(secondDigit);

            mSecondDigitBaseline.y = mDefaultBaselineY;
            mSecondNextDigitBaseline.y = mDefaultOuterBaselineY;
        }

        invalidate();
    }

    public void setFirstTime(String firstTime) {
        mFirstDigitText = firstTime;
        invalidate();
    }

    public void setSecondTime(String secondTime) {
        mSecondDigitText = secondTime;
        invalidate();
    }

    private void calculateLayoutAndDrawParams() {
        mFirstDigitText = DEFAULT_TIME;
        mSecondDigitText = DEFAULT_TIME;

        int mTextHeight = (int) (mTextPaint.getFontMetrics().descent - mTextPaint.getFontMetrics().ascent);
        int mTextWidth = (int) mTextPaint.measureText(DEFAULT_TIME);
        mPreMeasuredWidth = mTextWidth * 2 + getPaddingLeft() + getPaddingRight();
        mPreMeasuredHeight = mTextHeight + getPaddingTop() + getPaddingBottom();

        mDefaultBaselineY = getPaddingTop() - mTextPaint.getFontMetrics().ascent;
        mDefaultOuterBaselineY = mDefaultBaselineY + mPreMeasuredHeight;

        // Current first digit
        mFirstDigitBaseline.x = getPaddingLeft();
        mFirstDigitBaseline.y = mDefaultBaselineY;
        // First next digit
        mFirstNextDigitBaseline.x = mFirstDigitBaseline.x;
        mFirstNextDigitBaseline.y = mDefaultOuterBaselineY;

        // Current second digit
        mSecondDigitBaseline.x = mFirstDigitBaseline.x + mTextWidth;
        mSecondDigitBaseline.y = mDefaultBaselineY;
        // Second next digit
        mSecondNextDigitBaseline.x = mSecondDigitBaseline.x;
        mSecondNextDigitBaseline.y = mDefaultOuterBaselineY;
    }
}
