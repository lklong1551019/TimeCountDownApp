package com.example.timecountdownapp;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * Created by longlk on 8/7/2020
 *
 * This class handles animation transition for time digit, it will always display time as XX
 * <str>Note: </str>
 * */
public class TimeCountDownView extends AppCompatTextView {

    /* Use to pre-measure this view's dimension */
    private static final String DEFAULT_TIME_DIGIT = "0";

    private int mFirstDigit = -1;
    private int mSecondDigit = -1;
    private String mFirstDigitText;
    private String mFirstNextDigitText;
    private String mSecondDigitText;
    private String mSecondNextDigitText;

    private final Paint mTextPaint;
    private final PointF mFirstDigitBaseline;
    private final PointF mFirstNextDigitBaseline;
    private final PointF mSecondDigitBaseline;
    private final PointF mSecondNextDigitBaseline;
    private int mPreMeasuredWidth;
    private int mPreMeasuredHeight;
    private float mDefaultBaselineY;
    private float mDefaultOuterBaselineY;

    private boolean mHasFirstDigitChanged = false;
    private boolean mHasSecondDigitChanged = false;
    private ValueAnimator mTimeAnimator;

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

        mFirstDigitText = DEFAULT_TIME_DIGIT;
        mFirstNextDigitText = DEFAULT_TIME_DIGIT;
        mSecondDigitText = DEFAULT_TIME_DIGIT;
        mSecondNextDigitText = DEFAULT_TIME_DIGIT;

        calculateLayoutAndDrawParams();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mTimeAnimator != null) {
            mTimeAnimator.removeAllListeners();
            mTimeAnimator.removeAllUpdateListeners();
            mTimeAnimator.end();
        }
        super.onDetachedFromWindow();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mPreMeasuredWidth, mPreMeasuredHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawText(mFirstDigitText, mFirstDigitBaseline.x, mFirstDigitBaseline.y, mTextPaint);
        canvas.drawText(mFirstNextDigitText, mFirstNextDigitBaseline.x, mFirstNextDigitBaseline.y, mTextPaint);

        canvas.drawText(mSecondDigitText, mSecondDigitBaseline.x, mSecondDigitBaseline.y, mTextPaint);
        canvas.drawText(mSecondNextDigitText, mSecondNextDigitBaseline.x, mSecondNextDigitBaseline.y, mTextPaint);
    }

    public void setNextTime(int time) {
        if (time < 0 || time > 99) throw new RuntimeException("Time must not smaller than 0 or bigger than 99");

        int firstDigit = time / 10;
        int secondDigit = time % 10;

        if (mFirstDigit != firstDigit) {
            mHasFirstDigitChanged = true;
            mFirstDigit = firstDigit;
            mFirstNextDigitText = String.valueOf(firstDigit);
        }
        if (mSecondDigit != secondDigit) {
            mHasSecondDigitChanged = true;
            mSecondDigit = secondDigit;
            mSecondNextDigitText = String.valueOf(secondDigit);
        }

        if (mHasFirstDigitChanged || mHasSecondDigitChanged) {
            startAnimator();
        }
    }

    private void startAnimator() {
        if (mTimeAnimator != null && mTimeAnimator.isRunning()) {
            mTimeAnimator.cancel();
        }
        // Perhaps due to some rounding issues when use canvas.drawText (floating number)
        //  that some digit exceeds 1 pixel at the top, so make it go to -1 instead of 0 :)
        mTimeAnimator = ValueAnimator.ofFloat(mDefaultBaselineY, -1);
        mTimeAnimator.setInterpolator(new OvershootInterpolator(1f));
        mTimeAnimator.setDuration(600);
        mTimeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float newY = (float) animation.getAnimatedValue();

                if (mHasFirstDigitChanged) {
                    // Go above top of this view
                    mFirstDigitBaseline.y = newY;
                    // Move gradually to the normal state
                    mFirstNextDigitBaseline.y = mFirstDigitBaseline.y + mDefaultBaselineY;
                }

                if (mHasSecondDigitChanged) {
                    // Go above top of this view
                    mSecondDigitBaseline.y = newY;
                    // Move gradually to the normal state
                    mSecondNextDigitBaseline.y = mSecondDigitBaseline.y + mDefaultBaselineY;
                }

                invalidate();
            }
        });
        mTimeAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (mHasFirstDigitChanged) {
                    mFirstDigitBaseline.y = mDefaultBaselineY;
                    mFirstNextDigitBaseline.y = mDefaultOuterBaselineY;
                }
                if (mHasSecondDigitChanged) {
                    mSecondDigitBaseline.y = mDefaultBaselineY;
                    mSecondNextDigitBaseline.y = mDefaultOuterBaselineY;
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mHasFirstDigitChanged = false;
                // Swap again so the "next" digit will become the current settled digit :)
                mFirstDigitBaseline.y = mDefaultBaselineY;
                mFirstNextDigitBaseline.y = mDefaultOuterBaselineY;
                mFirstDigitText = mFirstNextDigitText;

                mHasSecondDigitChanged = false;
                // Swap again so the "next" digit will become the current settled digit :)
                mSecondDigitBaseline.y = mDefaultBaselineY;
                mSecondNextDigitBaseline.y = mDefaultOuterBaselineY;
                mSecondDigitText = mSecondNextDigitText;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        mTimeAnimator.start();
    }

    public void stopAnimator() {
        if (mTimeAnimator != null) {
            mTimeAnimator.end();
        }
    }

    private void calculateLayoutAndDrawParams() {
        int mTextHeight = (int) (mTextPaint.getFontMetrics().descent - mTextPaint.getFontMetrics().ascent);
        int mTextWidth = (int) mTextPaint.measureText(DEFAULT_TIME_DIGIT);
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
