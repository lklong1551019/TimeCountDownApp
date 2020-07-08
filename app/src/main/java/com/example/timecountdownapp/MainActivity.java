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
        mLayout.setCountdownTime(86405000);
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
        mLayout.stopTimer();
    }
}