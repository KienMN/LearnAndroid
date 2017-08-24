package com.example.kienmaingoc.guesscelebrities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ActivityChooserView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Button button0;
    Button button1;
    Button button2;
    Button button3;
    ArrayList<String> clubUrls = new ArrayList<String>();
    ArrayList<String> clubNames = new ArrayList<String>();
    ArrayList<String> answers = new ArrayList<String>();
    int correctClubNumber = 0;
    int locationOfCorrectAnswer = 0;
    int incorrectClubNumber = 0;

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            URL url;
            HttpURLConnection connection = null;
            String result = "";

            try {
                url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
//                InputStreamReader reader = new InputStreamReader(inputStream);
//
//                int data = reader.read();
//                while (data != -1) {
//                    char c = (char) data;
//                    result += c;
//                    data = reader.read();
//                }
//                return result;

                final int bufferSize = 1024;
                final char[] buffer = new char[bufferSize];
                final StringBuilder out = new StringBuilder();
                Reader in = new InputStreamReader(inputStream, "UTF-8");
                for (; ; ) {
                    int rsz = in.read(buffer, 0, buffer.length);
                    if (rsz < 0)
                        break;
                    out.append(buffer, 0, rsz);
                }
                return out.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            URL url;
            HttpURLConnection connection = null;
            Bitmap bitmap = null;

            try {
                url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public void chooseClubName(View view) {

        if (view.getTag().toString().equals(String.valueOf(locationOfCorrectAnswer))) {
            Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Wrong! Is was " + clubNames.get(correctClubNumber), Toast.LENGTH_SHORT).show();
        }

        generateQuestion();
    }

    public void generateQuestion() {

        try {

            Random rand = new Random();

            correctClubNumber = rand.nextInt(clubNames.size() - 2);

            ImageDownloader imageDownloader = new ImageDownloader();

            Bitmap bitmap = imageDownloader.execute(clubUrls.get(correctClubNumber)).get();

            imageView.setImageBitmap(bitmap);

            locationOfCorrectAnswer = rand.nextInt(4);

            answers.clear();

            for (int i = 0; i < 4; i++) {

                if (i == locationOfCorrectAnswer) {

                    answers.add(clubNames.get(correctClubNumber));

                } else {

                    incorrectClubNumber = rand.nextInt(clubNames.size() - 2);

                    while (incorrectClubNumber == correctClubNumber) {

                        incorrectClubNumber = rand.nextInt(clubNames.size() - 2);

                    }

                    answers.add(clubNames.get(incorrectClubNumber));

                }

            }

            button0.setText(answers.get(0));
            button1.setText(answers.get(1));
            button2.setText(answers.get(2));
            button3.setText(answers.get(3));

        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);
        button0 = (Button) findViewById(R.id.button0);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);

        DownloadTask downloadTask = new DownloadTask();
        try {
            String result = downloadTask.execute("http://www.totalsportek.com/money/richest-football-clubs/").get();

            String[] splitResult = result.split("<table");

            Pattern p = Pattern.compile("src=\"(.*?)\" alt=\"");

            Matcher m = p.matcher(splitResult[1]);

            while (m.find()) {

                clubUrls.add(m.group(1));

            }

            p = Pattern.compile("alt=\"(.*?)\" width=\"");

            m = p.matcher(splitResult[1]);

            while (m.find()) {

                clubNames.add(m.group(1));

            }

//            Log.i("Result", String.valueOf(clubUrls.size()) + " + " + String.valueOf(clubNames.size()));

            generateQuestion();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
