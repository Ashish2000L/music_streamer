package com.musicstreaming.musicstreaming;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.CompoundButton;

import java.io.File;

import static com.musicstreaming.musicstreaming.MainActivity.MAIN_ACTIVITY;
import static com.musicstreaming.musicstreaming.login.PASSWORD;
import static com.musicstreaming.musicstreaming.login.SHARED_PREF;
import static com.musicstreaming.musicstreaming.service.onclearfrompercentservice.TERMINATION_STATUS;
import static com.musicstreaming.musicstreaming.service.onclearfrompercentservice.handler;
import static com.musicstreaming.musicstreaming.service.onclearfrompercentservice.isplaying;
import static com.musicstreaming.musicstreaming.service.onclearfrompercentservice.isprepared;
import static com.musicstreaming.musicstreaming.service.onclearfrompercentservice.ispreparing;
import static com.musicstreaming.musicstreaming.service.onclearfrompercentservice.terminate_all_process;
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

        Thread.setDefaultUncaughtExceptionHandler(new Exceptionhandler(this));

        switchCompat=findViewById(R.id.night_mode);

        sharedPreferences=getSharedPreferences(SHARED_PREF,MODE_PRIVATE);

        night_mode_setting();

    }

    public void night_mode_setting(){
        switchCompat.setChecked(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES);

        final SharedPreferences.Editor editor = sharedPreferences.edit();

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {

                terminate_all_process.run();
                Runnable check_termination_status = new Runnable() {
                    @Override
                    public void run() {
                        if(TERMINATION_STATUS) {

                            if(isChecked){
                                switchCompat.setChecked(true);
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                                editor.putBoolean(NIGHT_MODE,true);
                                editor.apply();


                                new make_file_in_directory(setting_advance.this,getApplicationContext(),sharedPreferences.getString(USERNAME,"")).write_night_mode(new File(Environment.getExternalStorageDirectory()+"/"+DIR_NAME,NIGHT_MODE),true);

                                Log.d("testing_size", "run: homefragment: "+homefragment.listofplaylistArrayList.size()+" songsfromplaylist : "+songsfromplaylist.listofsongsArrayLisr.size());

                            }else{

                                switchCompat.setChecked(false);
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                                editor.putBoolean(NIGHT_MODE,false);
                                editor.apply();

                                new make_file_in_directory(setting_advance.this,getApplicationContext(),sharedPreferences.getString(USERNAME,"")).write_night_mode(new File(Environment.getExternalStorageDirectory()+"/"+DIR_NAME,NIGHT_MODE),false);

                                Log.d("testing_size", "run: homefragment: "+homefragment.listofplaylistArrayList.size()+" songsfromplaylist : "+songsfromplaylist.listofsongsArrayLisr.size());
                            }

                            startActivity(new Intent(setting_advance.this,MainActivity.class));
                            finish();

                        }else{
                            handler.postDelayed(this,1000);
                        }
                    }
                };

                check_termination_status.run();

            }
        });
    }

}