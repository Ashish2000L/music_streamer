package com.musicstreaming.musicstreaming.service;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.musicstreaming.musicstreaming.Exceptionhandler;

import java.security.Provider;
import java.util.HashMap;
import java.util.Map;

import static com.musicstreaming.musicstreaming.service.GPSTracker.addr_location;
import static com.musicstreaming.musicstreaming.splash.SHARED_PREF;
import static com.musicstreaming.musicstreaming.splash.SPLASH_ACTIVITY;
import static com.musicstreaming.musicstreaming.splash.USERNAME;

public class online_status_updater extends Service {

    String TAG="online_status",status,username;
    Handler handler = new Handler();
    public static String CURRENT_PLAYING_SONG="";

    public online_status_updater() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(new Exceptionhandler(SPLASH_ACTIVITY));
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            new change_status().execute();
            handler.postDelayed(this,60000);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        status=intent.getStringExtra("status");
        username=intent.getStringExtra("username");

        try {
            handler.removeCallbacks(runnable);
        }catch (Exception e){
            Log.d(TAG, "onStartCommand: Exception "+e.getMessage());
        }
        new GPSTracker(getApplicationContext());

        runnable.run();

        return START_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class change_status extends AsyncTask<Void,Void,Void>{

        String[] values;
        String Country_Code,address,total_address,longitude,latitude;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){

                    if(addr_location==null){
                        address="";
                        Country_Code="";
                        total_address="";
                        longitude="";
                        latitude="";
                        Log.d(TAG, "onPreExecute: null");
                    }else{
                        Log.d(TAG, "onPreExecute: "+addr_location.length);
                        address=addr_location[8];
                        Country_Code=addr_location[0];
                        total_address="Admin Area : "+addr_location[1]+", SubAdmin Area : "+addr_location[2]+", Locality : "+addr_location[3]+", SubLocality : "+addr_location[4]+
                                ", Postal Code : "+addr_location[5];
                        longitude=addr_location[6];
                        latitude=addr_location[7];
                    }

            }else{
                Log.d(TAG, "doInBackground: Location permission not provided ");
            }

        }

        @Override
        protected Void doInBackground(Void... voids) {

            String url="https://rentdetails.000webhostapp.com/musicplayer_files/In%20app%20Dialogue/online_status.php";

            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREF,MODE_PRIVATE);
            if(username.equals("")){
                username=sharedPreferences.getString(USERNAME,"");
            }

            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    Log.d(TAG, "onResponse: "+response);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse: "+error.getMessage());
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();

                    params.put("status", status);
                    params.put("username",username);
                    params.put("listening",CURRENT_PLAYING_SONG);
                    params.put("addr",address);
                    params.put("totl_addr",total_address);
                    params.put("country_code",Country_Code);
                    params.put("longitude",longitude);
                    params.put("latitude",latitude);

                    return params;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(request);

            return null;
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        status="offline";
        CURRENT_PLAYING_SONG="";
        handler.removeCallbacks(runnable);
        new change_status().execute();
        stopSelf();
    }
}
