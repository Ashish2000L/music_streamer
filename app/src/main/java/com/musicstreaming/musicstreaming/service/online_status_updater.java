package com.musicstreaming.musicstreaming.service;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
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
import com.musicstreaming.musicstreaming.make_file_in_directory;

import org.json.JSONObject;

import java.io.File;
import java.security.Provider;
import java.util.HashMap;
import java.util.Map;

import static com.musicstreaming.musicstreaming.service.GPSTracker.IS_JOB_SERVICE_RUNNING;
import static com.musicstreaming.musicstreaming.service.GPSTracker.addr_location;
import static com.musicstreaming.musicstreaming.splash.DIR_NAME;
import static com.musicstreaming.musicstreaming.splash.LOCATION_ACCESS_PERMISSION;
import static com.musicstreaming.musicstreaming.splash.SHARED_PREF;
import static com.musicstreaming.musicstreaming.splash.SPLASH_ACTIVITY;
import static com.musicstreaming.musicstreaming.splash.USERNAME;
import static com.musicstreaming.musicstreaming.splash.sharedPreferences;

public class online_status_updater extends Service {

    String TAG="online_status";
    public static String status,username;
    public  Handler handler = new Handler();
    public static String CURRENT_PLAYING_SONG="";

    public online_status_updater() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(new Exceptionhandler(SPLASH_ACTIVITY));
    }

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(checkConnections()) {
                new change_status().execute();
            }
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

        runnable.run();

        return START_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class change_status extends AsyncTask<Void,Void,Void>{

        String Country_Code,address,total_address,longitude,latitude;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED &&
            sharedPreferences.getBoolean(LOCATION_ACCESS_PERMISSION,false)){

                    if(addr_location==null){
                        address="";
                        Country_Code="";
                        total_address="";
                        longitude="";
                        latitude="";
                        Log.d(TAG, "onPreExecute: null");
                    }else{
                        if(addr_location.length==9) {
                            address = addr_location[8];
                            Country_Code = addr_location[0];
                            total_address = "Admin Area : " + addr_location[1] + ", SubAdmin Area : " + addr_location[2] + ", Locality : " + addr_location[3] + ", SubLocality : " + addr_location[4] +
                                    ", Postal Code : " + addr_location[5];
                            longitude = addr_location[6];
                            latitude = addr_location[7];
                        }else{
                            Log.d(TAG, "onPreExecute: getting wrong length string array "+addr_location.length);
                        }
                    }

            }else{
                address="";
                Country_Code="";
                total_address="";
                longitude="";
                latitude="";
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {

            String url="https://rentdetails.000webhostapp.com/musicplayer_files/In%20app%20Dialogue/online_status.php";

            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREF,MODE_PRIVATE);
            File file = new File(Environment.getExternalStorageDirectory()+"/"+DIR_NAME,"file.json");
            if(username.equals("")){
                username=sharedPreferences.getString(USERNAME,"");
                if(username.equals("") && file.exists()){
                    try {
                        String credentail = new make_file_in_directory(SPLASH_ACTIVITY, getApplicationContext(), username).read_credentail_file(file);
                        JSONObject obj = new JSONObject(credentail);
                        JSONObject crednt = obj.getJSONObject("credential");
                        username = crednt.getString("username");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            if(!username.equals("")) {

                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d(TAG, "onResponse: " + response);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + error.getMessage());
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();

                        params.put("status", status);
                        params.put("username", username);
                        params.put("listening", CURRENT_PLAYING_SONG);
                        params.put("addr", address);
                        params.put("totl_addr", total_address);
                        params.put("country_code", Country_Code);
                        params.put("longitude", longitude);
                        params.put("latitude", latitude);

                        return params;
                    }
                };


                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                queue.add(request);
            }

            return null;
        }
    }

    public boolean checkConnections()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileNetwork = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);


        assert wifi != null;
        if(wifi.isConnected()){
            return true;

        }
        else {
            assert mobileNetwork != null;
            return mobileNetwork.isConnected();

        }
    }

    public boolean isservicerunning(Class<?> serviceclass){
        ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo serviceInfo : manager.getRunningServices(Integer.MAX_VALUE)){
            if(serviceclass.getName().equals(serviceInfo.service.getClassName())){
                return true;
            }
        }
        return false;
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
    public void start_job_schedular_for_location_access(){
        ComponentName componentName = new ComponentName(this, GPSTracker.class);
        JobInfo info = new JobInfo.Builder(1,componentName)
                .setPersisted(true)
                .setPeriodic(1000*60*15)
                .build();

        JobScheduler jobScheduler = (JobScheduler)getSystemService(JOB_SCHEDULER_SERVICE);
        int result=jobScheduler.schedule(info);

        if(result==JobScheduler.RESULT_SUCCESS){
            Log.d(TAG, "start_job_schedular_for_location_access: successfully completed job ");
        }else{
            Log.d(TAG, "start_job_schedular_for_location_access: Job failed and scheduled ");
        }
    }
}
