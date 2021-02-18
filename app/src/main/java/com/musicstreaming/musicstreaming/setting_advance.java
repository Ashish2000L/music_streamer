package com.musicstreaming.musicstreaming;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.widget.CompoundButton;

import java.io.File;

import static com.musicstreaming.musicstreaming.login.SHARED_PREF;
import static com.musicstreaming.musicstreaming.splash.DIR_NAME;
import static com.musicstreaming.musicstreaming.splash.NIGHT_MODE;
import static com.musicstreaming.musicstreaming.splash.USERNAME;

public class setting_advance extends AppCompatActivity {

    SwitchCompat switchCompat;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_advance);

        switchCompat=findViewById(R.id.night_mode);

        sharedPreferences=getSharedPreferences(SHARED_PREF,MODE_PRIVATE);
        night_mode_setting();

    }

    public void night_mode_setting(){
        switchCompat.setChecked(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES);

        final SharedPreferences.Editor editor = sharedPreferences.edit();

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    switchCompat.setChecked(true);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor.putBoolean(NIGHT_MODE,true);
                    editor.apply();

                    new make_file_in_directory(setting_advance.this,getApplicationContext(),sharedPreferences.getString(USERNAME,"")).write_night_mode(new File(Environment.getExternalStorageDirectory()+"/"+DIR_NAME,NIGHT_MODE),true);

                }else{
                    switchCompat.setChecked(false);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor.putBoolean(NIGHT_MODE,false);
                    editor.apply();

                    new make_file_in_directory(setting_advance.this,getApplicationContext(),sharedPreferences.getString(USERNAME,"")).write_night_mode(new File(Environment.getExternalStorageDirectory()+"/"+DIR_NAME,NIGHT_MODE),false);

                }

            }
        });
    }

}