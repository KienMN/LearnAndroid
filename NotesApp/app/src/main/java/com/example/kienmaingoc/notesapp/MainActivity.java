package com.example.kienmaingoc.notesapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    static ArrayList<String> notes = new ArrayList<String>();
    static ArrayAdapter arrayAdapter;
    static Set<String> set;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.kienmaingoc.notesapp", Context.MODE_PRIVATE);

        set = sharedPreferences.getStringSet("notes", null);

        notes.clear();

        if (set != null) {

            notes.addAll(set);

        } else {

            notes.add("New note");
            set = new HashSet<String>();
            set.addAll(notes);
            sharedPreferences.edit().remove("notes").apply();
            sharedPreferences.edit().putStringSet("notes", set).apply();

        }

        Log.i("references", sharedPreferences.getStringSet("notes", null).toString());

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, notes);

        ListView notesListView = (ListView) findViewById(R.id.notesListView);

        notesListView.setAdapter(arrayAdapter);

        notesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), EditNote.class);
                i.putExtra("noteId", position);
                startActivity(i);
            }
        });

        notesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Delete?")
                        .setMessage("Are you about to delete?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                notes.remove(position);
                                arrayAdapter.notifyDataSetChanged();

                                if (set == null) {

                                    set = new HashSet<String>();

                                } else {

                                    set.clear();

                                }
                                set.addAll(notes);
                                sharedPreferences.edit().remove("notes").apply();
                                sharedPreferences.edit().putStringSet("notes", set).apply();

                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.add) {

            notes.add("");
            Intent i = new Intent(getApplicationContext(), EditNote.class);
            i.putExtra("noteId", notes.size() - 1);
            startActivity(i);

            return true;

        }

        return super.onOptionsItemSelected(item);
    }

}
