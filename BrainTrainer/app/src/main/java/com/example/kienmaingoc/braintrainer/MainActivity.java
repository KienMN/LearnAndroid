package com.example.kienmaingoc.braintrainer;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    TextView sumTextView;
    TextView resultTextView;
    TextView scoreTextView;
    TextView timerTextView;
    TextView finalResultTextView;
    RelativeLayout playRelativeLayout;
    RelativeLayout playAgainRelativeLayout;
    ArrayList<Integer> answers = new ArrayList<Integer>();
    Button button0;
    Button button1;
    Button button2;
    Button button3;
    int locationOfCorrectAnswer;
    int incorrectAnswer;
    int score = 0;
    int numberOfQuestions = 0;
    int typeOfOperation = 0;
    final int SUM = 0;
    final int MULTIPLY = 1;
    final int SUBTRACT = 2;
    final int DIVISION = 3;

    public void start(View view) {
        view.setVisibility(View.INVISIBLE);
        playAgain(findViewById(R.id.playAgainButton));
        playRelativeLayout.setVisibility(RelativeLayout.VISIBLE);
    }

    public void playAgain(View view) {
        score = 0;
        numberOfQuestions = 0;
        generateQuestion();
        timerTextView.setText("30s");
        scoreTextView.setText("0/0");
        playRelativeLayout.setVisibility(RelativeLayout.VISIBLE);
        playAgainRelativeLayout.setVisibility(RelativeLayout.INVISIBLE);
        new CountDownTimer(30100, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                timerTextView.setText(String.valueOf(millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                timerTextView.setText("0");
                finalResultTextView.setText("Your result: " + Integer.toString(score) + "/" + Integer.toString(numberOfQuestions));
                playRelativeLayout.setVisibility(RelativeLayout.INVISIBLE);
                playAgainRelativeLayout.setVisibility(RelativeLayout.VISIBLE);
            }
        }.start();
    }

    public void chooseAnswer(View view) {
        if (view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))) {
            score ++;
            resultTextView.setText("Correct!");

        } else {
            resultTextView.setText("Wrong!");
        }

        numberOfQuestions ++;
        scoreTextView.setText(Integer.toString(score) + "/" + Integer.toString(numberOfQuestions));
        generateQuestion();
    }

    public void generateQuestion() {
        Random rand = new Random();
        typeOfOperation = rand.nextInt(4);

        if (typeOfOperation == SUM) {
            int a = rand.nextInt(21);
            int b = rand.nextInt(21);


            sumTextView.setText(Integer.toString(a) + " + " + Integer.toString(b));
            locationOfCorrectAnswer = rand.nextInt(4);
            answers.clear();

            for (int i = 0; i < 4; i++) {
                if (i == locationOfCorrectAnswer) {
                    answers.add(a + b);
                } else {
                    incorrectAnswer = rand.nextInt(41);
                    while (incorrectAnswer == a + b) {
                        incorrectAnswer = rand.nextInt(41);
                    }
                    answers.add(incorrectAnswer);
                }
            }
        } else if (typeOfOperation == MULTIPLY) {
            int a = rand.nextInt(6);
            int b = rand.nextInt(6);


            sumTextView.setText(Integer.toString(a) + " * " + Integer.toString(b));
            locationOfCorrectAnswer = rand.nextInt(4);
            answers.clear();

            for (int i = 0; i < 4; i++) {
                if (i == locationOfCorrectAnswer) {
                    answers.add(a * b);
                } else {
                    incorrectAnswer = rand.nextInt(26);
                    while (incorrectAnswer == a * b) {
                        incorrectAnswer = rand.nextInt(26);
                    }
                    answers.add(incorrectAnswer);
                }
            }
        } else if (typeOfOperation == SUBTRACT) {
            int a = rand.nextInt(21);
            int b = rand.nextInt(21);


            sumTextView.setText(Integer.toString(a) + " - " + Integer.toString(b));
            locationOfCorrectAnswer = rand.nextInt(4);
            answers.clear();

            for (int i = 0; i < 4; i++) {
                if (i == locationOfCorrectAnswer) {
                    answers.add(a - b);
                } else {
                    incorrectAnswer = rand.nextInt(41) - 20;
                    while (incorrectAnswer == a - b) {
                        incorrectAnswer = rand.nextInt(41) - 20;
                    }
                    answers.add(incorrectAnswer);
                }
            }
        } else if (typeOfOperation == DIVISION) {
            int a = rand.nextInt(41);
            int b = rand.nextInt(20) + 1;

            while (a % b != 0) {
                a = rand.nextInt(41);
                b = rand.nextInt(20) + 1;
            }

            sumTextView.setText(Integer.toString(a) + " / " + Integer.toString(b));
            locationOfCorrectAnswer = rand.nextInt(4);
            answers.clear();

            for (int i = 0; i < 4; i++) {
                if (i == locationOfCorrectAnswer) {
                    answers.add(a / b);
                } else {
                    incorrectAnswer = rand.nextInt(41);
                    while (incorrectAnswer == a / b) {
                        incorrectAnswer = rand.nextInt(41);
                    }
                    answers.add(incorrectAnswer);
                }
            }
        }

        button0.setText(Integer.toString(answers.get(0)));
        button1.setText(Integer.toString(answers.get(1)));
        button2.setText(Integer.toString(answers.get(2)));
        button3.setText(Integer.toString(answers.get(3)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button0 = (Button) findViewById(R.id.button0);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);

        resultTextView = (TextView) findViewById(R.id.resultTextView);
        sumTextView = (TextView) findViewById(R.id.sumTextView);
        scoreTextView = (TextView) findViewById(R.id.scoreTextView);
        timerTextView = (TextView) findViewById(R.id.timerTextView);
        finalResultTextView = (TextView) findViewById(R.id.finalResultTextView);

        playRelativeLayout = (RelativeLayout) findViewById(R.id.playRelativeLayout);
        playAgainRelativeLayout = (RelativeLayout) findViewById(R.id.playAgainRelativeLayout);



    }
}
