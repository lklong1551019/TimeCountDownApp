package com.example.timecountdownapp;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class TimeCountDownLayout extends LinearLayout {

    private ViewGroup mVgDay;
    private ViewGroup mVgHour;
    private ViewGroup mVgMinute;
    private ViewGroup mVgSecond;

    private TextView mTvDay;
    private TextView mTvHour;
    private TextView mTvMinute;
    private TimeCountDownView mTvSecond;

    private long mTotalSeconds;
    private long mSeconds;
    private long mMinutes;
    private long mHours;
    private long mDays;

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
        mVgHour = findViewById(R.id.vgHour);
        mVgMinute = findViewById(R.id.vgMinute);
        mVgSecond = findViewById(R.id.vgSecond);

        mTvDay = findViewById(R.id.tvDayCount);
        mTvHour = findViewById(R.id.tvHourCount);
        mTvMinute = findViewById(R.id.tvMinuteCount);
        mTvSecond = findViewById(R.id.tvSecondCount);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopTimer();
    }

    public void setCountdownTime(long countdownTimeInMilliseconds) {
        mTotalSeconds = countdownTimeInMilliseconds / 1000;
        Log.i("debug_test","--> " + mTotalSeconds);
        updateTimes();

        if (mDays <= 0) {
            mVgDay.setVisibility(View.GONE);
        }
        startTimer();

        Log.i("debug_test"," " + mDays + " : " + mHours + " : " + mMinutes + " : " + mSeconds);
    }

    private void updateTimes() {
        mSeconds = mTotalSeconds % 60;
        mMinutes = (mTotalSeconds / 60) % 60;
        mHours = (mTotalSeconds / 3600) % 24;
        mDays = mTotalSeconds / 86400;

        mTvDay.setText(String.valueOf(mDays));
        mTvHour.setText(String.valueOf(mHours));
        mTvMinute.setText(String.valueOf(mMinutes));
        mTvSecond.setNextTime((int) mSeconds);
    }

    public void startTimer() {
        if (mCountDownTimer != null) return;

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

    public void stopTimer() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
    }
}
