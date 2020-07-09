package com.example.timecountdownapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private TimeCountDownLayout mLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLayout = findViewById(R.id.timeCountDown);
        mLayout.setCountdownInterval(86405000L);
        mLayout.startTimer();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // mLayout.setCountdownTime(86405000);
        mLayout.startTimer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLayout.stopTimerAndAnimation();
    }
}