package com.example.musicstreaming;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import static com.example.musicstreaming.MainActivity.MAIN_ACTIVITY;

public class make_custom_playlist extends AppCompatActivity {

    public static Activity MAKE_NEW_PLAYLIST;
    Fragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_custom_playlist);

        MAKE_NEW_PLAYLIST=this;
        Toolbar toolbar = findViewById(R.id.new_playlist_toolbar);
        setSupportActionBar(toolbar);
        getSupportFragmentManager().beginTransaction().replace(R.id.custom_frame_layout,new select_songs_for_playlist()).commit();
    }


}