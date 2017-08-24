package com.example.kienmaingoc.countdownapp;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView timerTextView;
    SeekBar timerSeekBar;
    Button controlButton;
    boolean isCounting = false;
    CountDownTimer countDownTimer;

    public void updateTime(int secondsUntilFinish) {
        int minutes = (int) secondsUntilFinish / 60;
        int seconds = secondsUntilFinish - minutes * 60;
        String secondsString = Integer.toString(seconds);
        if (seconds <= 9) {
            secondsString = "0" + secondsString;
        }
        timerTextView.setText(Integer.toString(minutes) + ":" + secondsString);
        timerSeekBar.setProgress(secondsUntilFinish);
    }

    public void pauseTime() {
        controlButton.setText("START");
        timerSeekBar.setEnabled(true);
    }

    public void controlTime(View view) {
        controlButton = (Button) findViewById(R.id.controlButton);
        timerSeekBar = (SeekBar) findViewById(R.id.timerSeekBar);
        if(!isCounting) {
            isCounting = true;
            timerSeekBar.setEnabled(false);
            controlButton.setText("STOP");
            countDownTimer = new CountDownTimer(timerSeekBar.getProgress() * 1000 + 100, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    updateTime((int) millisUntilFinished / 1000);
                }

                @Override
                public void onFinish() {
                    updateTime(0);
                    pauseTime();
                }
            }.start();
        } else {
            isCounting = false;
            pauseTime();
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerTextView = (TextView) findViewById(R.id.timerTextView);
        timerSeekBar = (SeekBar) findViewById(R.id.timerSeekBar);
        timerSeekBar.setMax(600);
        timerSeekBar.setProgress(30);
        timerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateTime(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
