package com.example.kienmaingoc.timerdemo;

import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Handler demo
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.i("Notification", "1 second has passed");
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(runnable);
        */

        //Countdown Demo
        new CountDownTimer(10000, 1000) {
            public void onTick(long milisecondsUntilDone) {
                Log.i("Notification", String.valueOf(milisecondsUntilDone/1000));
            }
            public void onFinish() {
                Log.i("Notification", "Done");
            }
        }.start();
    }
}
