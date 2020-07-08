package com.example.timecountdownapp;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

/**
 * Created by longlk on 8/7/2020
 * */
public class TimeCountDownLayout extends LinearLayout {

    private ViewGroup mVgDay;

    private TimeCountDownView mTvDay;
    private TimeCountDownView mTvHour;
    private TimeCountDownView mTvMinute;
    private TimeCountDownView mTvSecond;

    private long mTotalSeconds;
    private int mSeconds;
    private int mMinutes;
    private int mHours;
    private int mDays;

    private CountDownTimer mCountDownTimer;

    public TimeCountDownLayout(Context context) {
        this(context, null);
    }

    public TimeCountDownLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeCountDownLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.layout_time_count_down, this);

        mVgDay = findViewById(R.id.vgDay);

        mTvDay = findViewById(R.id.tvDayCount);
        mTvHour = findViewById(R.id.tvHourCount);
        mTvMinute = findViewById(R.id.tvMinuteCount);
        mTvSecond = findViewById(R.id.tvSecondCount);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopTimerAndAnimation();
    }

    /**
     * @param countdownInterval in milliseconds
     * */
    public void setCountdownInterval(long countdownInterval) {
        mTotalSeconds = countdownInterval / 1000;
        if (!checkIfCouldHandleTime(mTotalSeconds))
            return;

        updateTimes();

        if (mDays <= 0) {
            mVgDay.setVisibility(View.GONE);
        }
        startTimer();
    }

    private boolean checkIfCouldHandleTime(long totalSeconds) {
        int days = (int) (totalSeconds / 86400);
        return days < 100;
    }

    private void updateTimes() {
        mSeconds = (int) (mTotalSeconds % 60); // Never exceeds 60
        mMinutes = (int) ((mTotalSeconds / 60) % 60); // Never exceeds 60
        mHours = (int) ((mTotalSeconds / 3600) % 24); // Never exceeds 24
        mDays = (int) (mTotalSeconds / 86400);

        mTvDay.setNextTime(mDays);
        mTvHour.setNextTime(mHours);
        mTvMinute.setNextTime(mMinutes);
        mTvSecond.setNextTime(mSeconds);
    }

    public void startTimer() {
        if (!checkIfCouldHandleTime(mTotalSeconds) || mCountDownTimer != null) return;

        mCountDownTimer = new CountDownTimer(mTotalSeconds * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTotalSeconds -= 1;
                updateTimes();
            }

            @Override
            public void onFinish() {

            }
        };
        mCountDownTimer.start();
    }

    public void stopTimerAndAnimation() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
        mTvDay.stopAnimator();
        mTvHour.stopAnimator();
        mTvMinute.stopAnimator();
        mTvSecond.stopAnimator();
    }
}
