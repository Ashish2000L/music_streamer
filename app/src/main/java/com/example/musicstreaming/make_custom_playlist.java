package com.example.musicstreaming;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.ListView;

import static com.example.musicstreaming.MainActivity.MAIN_ACTIVITY;

public class make_custom_playlist extends AppCompatActivity {

    ListView custom_listview;
    listofplaylist playlist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_custom_playlist);

        Thread.setDefaultUncaughtExceptionHandler(new Exceptionhandler(this));



    }
}