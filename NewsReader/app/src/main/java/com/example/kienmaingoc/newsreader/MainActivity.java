package com.example.kienmaingoc.newsreader;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase articlesDB = this.openOrCreateDatabase("Articles", MODE_PRIVATE, null);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        articlesDB.execSQL("CREATE TABLE IF NOT EXISTS articles(id INTEGER PRIMARY KEY, articleId INTEGER, url VARCHAR, title VARCHAR)");

        DownloadTask downloadTask = new DownloadTask();
        try {
            String result = downloadTask.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty").get();
            Log.i("result", result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        HttpURLConnection httpURLConnection;
        URL url;
        String result = "";

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);

                httpURLConnection = (HttpURLConnection) url.openConnection();

                InputStream inputStream = httpURLConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(inputStream);

                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += data;
                    data = reader.read();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

    }
}
