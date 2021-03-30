package com.musicstreaming.musicstreaming;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import static com.musicstreaming.musicstreaming.login.NAME;
import static com.musicstreaming.musicstreaming.login.SHARED_PREF;

public class make_custom_playlist extends AppCompatActivity {

    /**
     * <h1>Ownership</h1>
     * <p>This Application is Owned by Ashish Kumar, Delhi Technology univerity, Btech ,Mechanical
     * Engineering ,2ndyear Student</p>
     */

    public static Activity MAKE_NEW_PLAYLIST;
    public static String PLAYLIST_ID=""; //playlist id-> which we get after we make new playlist
    boolean IS_CUSTOM_PLAYLIST=false,IS_QR_CODE=false;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_custom_playlist);

        MAKE_NEW_PLAYLIST=this;
        Toolbar toolbar = findViewById(R.id.new_playlist_toolbar);
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF,MODE_PRIVATE);
        setSupportActionBar(toolbar);

        if(getIntent().getBooleanExtra("IS_QR_CODE",false)){

            getSupportFragmentManager().beginTransaction().replace(R.id.custom_frame_layout, new qr_code()).commit();
            toolbar.setTitle(sharedPreferences.getString(NAME,"Music Streaming"));
            IS_QR_CODE=true;
        }else {
            IS_CUSTOM_PLAYLIST=true;
            getSupportFragmentManager().beginTransaction().replace(R.id.custom_frame_layout, new select_songs_for_playlist()).commit();

            if(getIntent().getBooleanExtra("IS_MODIFYING",false)){

                SharedPreferences.Editor editor =sharedPreferences.edit();
                editor.putBoolean("IS_MODIFYING",true);
                editor.apply();

                change_title(toolbar);
            }
        }

    }

    public static void change_title(Toolbar toolbar){

        toolbar.setTitle("Add New Songs");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(IS_QR_CODE){
            startActivity(new Intent(this,MainActivity.class).putExtra("fragment_id",3));
        }else
            if(IS_CUSTOM_PLAYLIST){
                startActivity(new Intent(this,MainActivity.class).putExtra("fragment_id",6));
            }
    }
}