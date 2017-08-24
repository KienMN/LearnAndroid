package com.example.kienmaingoc.sharedpreferencesdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.kienmaingoc.sharedpreferencesdemo", Context.MODE_PRIVATE);

//        sharedPreferences.edit().putString("username", "kien mn").apply();

        String username = sharedPreferences.getString("username", "");

        Log.i("username", username);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        Log.i("ActionButton", "Tapped");

        if (id == R.id.add) {


            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
