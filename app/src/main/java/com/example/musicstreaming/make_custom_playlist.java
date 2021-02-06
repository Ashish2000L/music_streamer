package com.example.musicstreaming;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import static com.example.musicstreaming.MainActivity.MAIN_ACTIVITY;

public class make_custom_playlist extends AppCompatActivity {

    /**
     * <h1>Ownership</h1>
     * <p>This Application is Owned by Ashish Kumar, Delhi Technology univerity, Btech ,Mechanical
     * Engineering ,2ndyear Student</p>
     */

    public static Activity MAKE_NEW_PLAYLIST;
    public static String PLAYLIST_ID=""; //playlist id-> which we get after we make new playlist
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_custom_playlist);

        MAKE_NEW_PLAYLIST=this;
        Toolbar toolbar = findViewById(R.id.new_playlist_toolbar);

        setSupportActionBar(toolbar);

        getSupportFragmentManager().beginTransaction().replace(R.id.custom_frame_layout, new select_songs_for_playlist()).commit();

        if(getIntent().getBooleanExtra("IS_MODIFYING",false)){

            change_title(toolbar);

        }

    }

    public static void change_title(Toolbar toolbar){

        toolbar.setTitle("Add New Songs");

    }


}