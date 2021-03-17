package com.musicstreaming.musicstreaming;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.DownloadManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.musicstreaming.musicstreaming.service.GPSTracker;
import com.musicstreaming.musicstreaming.service.get_fav_song_list;
import com.musicstreaming.musicstreaming.service.onclearfrompercentservice;
import com.musicstreaming.musicstreaming.service.online_status_updater;
import com.musicstreaming.musicstreaming.service.phone_state_broadcast_reciever;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.Manifest.permission.ACCESS_BACKGROUND_LOCATION;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.musicstreaming.musicstreaming.checkserveravailability.connection;
import static com.musicstreaming.musicstreaming.checkserveravailability.iscomplete;
import static com.musicstreaming.musicstreaming.checkserveravailability.strings;
import static com.musicstreaming.musicstreaming.login.SHARED_PREF;
import static com.musicstreaming.musicstreaming.playlistfragment.listofplaylistArrayList_for_fav;
import static com.musicstreaming.musicstreaming.service.get_fav_song_list.listofplaylistArrayList_for_shortcut;
import static com.musicstreaming.musicstreaming.service.onclearfrompercentservice.POSITION_FAV_PLAYLIST;
import static com.musicstreaming.musicstreaming.service.online_status_updater.CURRENT_ACTIVITY_CONTEXT;
import static java.lang.Thread.sleep;

public class splash extends AppCompatActivity {
    /**
     * <h1>Ownership</h1>
     * <p>This Application is Owned by Ashish Kumar, Delhi Technology univerity, Btech ,Mechanical
     * Engineering ,2ndyear Student</p>
     * <p>Finished First version 1.0 on 17-Aug-2020</p>
     */
    public static String TAG="this_is_a_splash",USERNAME="username",PASSWORD="password",NAME="name",IMAGE="image",
            EMAIL="email",TELEPHONE_STATE_CHANGE_PERMISSION="tell_state_change",DIR_NAME="Music_Streaming",NIGHT_MODE="night_mode",SHARED_PREF="sharedpref",
            LOCATION_ACCESS_PERMISSION="location_access_permission";
    FirebaseRemoteConfig firebaseRemoteConfig;
    private static final String VersionCode = "versioncodes";
    private static final String force_update = "force_update";
    private static final String maintain="maintain";
    private static final String Url = "url";
    private static final String share_msg="share_msg";
    private Uri path;
    private Context context;
    private DownloadManager downloadManager;
    long download,contentlength;
    WebView webView;
    TextView status;
    ProgressBar seekBar;
    String message="";
    LottieAnimationView lottieAnimationView;
    Handler stathandler=new Handler();
    final Handler handler = new Handler();
    int num=0,SHORTCUT_TAP_VALUE=0;
    public static Activity SPLASH_ACTIVITY;
    public static SharedPreferences sharedPreferences;
    BroadcastReceiver downloads;
    Button btn_refresh;
    LinearLayout top_layout,bottom_layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SPLASH_ACTIVITY=this;
        CURRENT_ACTIVITY_CONTEXT=this;

        Thread.setDefaultUncaughtExceptionHandler(new Exceptionhandler(this));

        new checkserveravailability(this).execute();
        sharedPreferences=getSharedPreferences(SHARED_PREF,MODE_PRIVATE);

        if (!isservicerunning(online_status_updater.class)) {
            startService(new Intent(getBaseContext(), online_status_updater.class)
                    .putExtra("username", sharedPreferences.getString(USERNAME,""))
            .putExtra("status","online"));
        }

        Intent intent = getIntent();
        SHORTCUT_TAP_VALUE=intent.getIntExtra("shortcut_tap_value",0);

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(true).build(); //.setDeveloperModeEnabled(BuildConfig.DEBUG)
        firebaseRemoteConfig.setConfigSettings(configSettings);
        firebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        webView=findViewById(R.id.webview);
        status=findViewById(R.id.status);
        seekBar=findViewById(R.id.seekbar);
        top_layout=findViewById(R.id.sample);
        bottom_layout=findViewById(R.id.bottom_layout);

        seekBar.setMax(100);
        stat.run();
        lottieAnimationView=findViewById(R.id.animation_view);

        long total=lottieAnimationView.getDuration();

        float finas=(float)(80/143)*100;

        lottieAnimationView.setMinAndMaxProgress(0.0f,0.513f);

        btn_refresh=findViewById(R.id.refresh);

//        get_telephone_state_change_permission();

        File night_mode=new File(Environment.getExternalStorageDirectory()+"/"+DIR_NAME,NIGHT_MODE);
        if(night_mode.exists()) {
            if(new make_file_in_directory(splash.this,getApplicationContext(),sharedPreferences.getString(USERNAME,"")).read_night_mode(night_mode)){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        }else
        if(sharedPreferences.getBoolean(NIGHT_MODE,false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        runnable.run();

        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new checkserveravailability(splash.this).execute();
                iscomplete=false;
                btn_refresh.setVisibility(View.GONE);
                stat.run();
                runnable.run();
                Log.d(TAG, "onClick: clicked");
            }
        });

    }

    int count=0;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
//            seekBar.setVisibility(View.VISIBLE);
            message="Checking Internet Connection ";
            seekBar.setProgress(10);

            if(checkConnections()){
                message="Connecting to Server ";
                seekBar.setProgress(30);
                handler.removeCallbacks(this);

                Runnable runnable1 = new Runnable() {
                    @Override
                    public void run() {
                        if(iscomplete){
                            seekBar.setProgress(40);
                            Log.d(TAG, "run: g");
                            if(strings.equals("you are connected")||connection){
                                message="Connection Established ";
                                seekBar.setProgress(50);

                                Check_Location_Enabled();

                                Log.d(TAG, "run: connection successful ");
                            }else{
                                stathandler.removeCallbacks(stat);
//                                seekBar.setVisibility(View.INVISIBLE);
                                if(count<2) {
                                    message = "Fail to connect!! ";
                                    status.setText(message);
                                    count++;
                                }else {
                                    status.setText(strings);
                                }
                                handler.removeCallbacks(this);
                                btn_refresh.setVisibility(View.VISIBLE);

                            }

                        }else{
                            handler.postDelayed(this,1000);
                        }
                    }
                };
                runnable1.run();

            }else{
                handler.postDelayed(this,1000);
            }
        }
    };

    public void make_dir()
    {

        File dir=new File(Environment.getExternalStorageDirectory(),DIR_NAME);

        if(!dir.exists()){
            if(dir.mkdirs()){
                Log.d(TAG, "make_dir: ");
            }
        }

        File track = new File(Environment.getExternalStorageDirectory()+"/"+DIR_NAME,"track");
        if(track.exists()){
            track.delete();
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

    private void getdetails()
    {   message="Collecting resources ";

        make_dir();

        boolean is_using_developerMode = firebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled();
        final int catchExpiration;
        if (is_using_developerMode) {

            catchExpiration = 0;

        } else {
            catchExpiration = 1000;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                firebaseRemoteConfig.fetch(0).addOnCompleteListener(splash.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {

                        if (task.isSuccessful()) {
                            firebaseRemoteConfig.activate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
                                @Override
                                public void onComplete(@NonNull Task<Boolean> tasks) {
                                    if(tasks.isSuccessful()){
                                        if(BuildConfig.DEBUG){
                                            check_for_update();
                                            seekBar.setProgress(70);
                                            Log.d(TAG, "onComplete: fetch complete ");
                                        }else {
                                            if (!firebaseRemoteConfig.getBoolean(maintain)) {
                                                check_for_update();
                                                seekBar.setProgress(70);
                                                Log.d(TAG, "onComplete: fetch complete ");
                                            } else {
                                                top_layout.setVisibility(View.GONE);
                                                bottom_layout.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(splash.this, "Fetch Failed",
                                    Toast.LENGTH_LONG).show();
                            final Intent transfer= new Intent(splash.this,login.class); //change this after testing
                            startActivity(transfer);
                            finish();
                        }
                    }
                });

            }
        },0);

    }

    Runnable stat = new Runnable() {
        @Override
        public void run() {
            //Log.d(TAG, "run: num value is "+num);
            if(num==0){
                status.setText(message+".");
            }else if(num==1){
                status.setText(message+"..");
            }else if(num==2){
                status.setText(message+"...");
            }
            num++;
            if(num==3){
                num=0;
            }
            stathandler.postDelayed(this,1000);
        }
    };

    private void check_for_update()
    {
        SharedPreferences.Editor edi = sharedPreferences.edit();
        edi.putString(share_msg,firebaseRemoteConfig.getString(share_msg));
        edi.apply();

        message="Checking for update ";
        String versioncode = firebaseRemoteConfig.getString(VersionCode);
        int ver = Integer.parseInt(versioncode);
        Log.d(TAG, "check_for_update: version is "+ver);
        Log.d(TAG, "check_for_update: fetched version is "+versioncode);

        int version = BuildConfig.VERSION_CODE;
        if (ver == version) {
            String username,password;
            SharedPreferences sharedPreferences=getSharedPreferences(SHARED_PREF,MODE_PRIVATE);
            username=sharedPreferences.getString(USERNAME,"");
            password=sharedPreferences.getString(PASSWORD,"");
            File file = new File(Environment.getExternalStorageDirectory()+"/"+DIR_NAME,"file.json");
            if(username!="" && password!=""){
                message="Logging in User ";
                loginifexist(username,password);
                Log.d(TAG, "check_for_update: logging in user ");
                seekBar.setProgress(90);
            }else
            if(file.exists()){

                String credentail=new make_file_in_directory(SPLASH_ACTIVITY,getApplicationContext(),username).read_credentail_file(file);
                try {
                    JSONObject obj = new JSONObject(credentail);
                    JSONObject crednt = obj.getJSONObject("credential");
                    username=crednt.getString("username");
                    password=crednt.getString("password");

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(USERNAME,username);
                    editor.putString(PASSWORD,password);
                    editor.apply();

                    loginifexist(username,password);

                }catch (Exception e){
                    Log.d("json_file_writing", "onClick: "+e.getMessage());
                }

            }else{
                seekBar.setProgress(100);
                startActivity(new Intent(splash.this,login.class));
            }

        } else {
            Log.d(TAG, "check_for_update: need to update ");
            if (!firebaseRemoteConfig.getBoolean(force_update) && (Integer.parseInt(firebaseRemoteConfig.getString(versioncode))-BuildConfig.VERSION_CODE)<3) {
                displaywelcomemessagenotforce();
            } else  {
                updatebyforce();
            }
        }
    }

    private void updatebyforce()
    {
        Log.d(TAG, "updatebyforce: ");

        final String new_Url = firebaseRemoteConfig.getString(Url).trim();

        AlertDialog.Builder builder = new AlertDialog.Builder(splash.this);
        builder.setTitle("Update Available")
                .setMessage("A newer version available ...")
                .setPositiveButton("Update now", new DialogInterface.OnClickListener() {

                    @SuppressLint("SetJavaScriptEnabled")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        webView.setVisibility(View.VISIBLE);
                        webView.getSettings().setJavaScriptEnabled(true);
                        webView.loadUrl(new_Url);
                        webView.setWebViewClient(new WebViewClient());

                        if(Build.MANUFACTURER.toLowerCase().equals("samsung")) {
                            new update_application(getApplicationContext()).samsung_store_update();
                        }else {
                            checkforpermission();
                        }



                    }
                })
                .setCancelable(false)
                .create().show();
    }

    private void displaywelcomemessagenotforce()
    {
        final String new_Url =firebaseRemoteConfig.getString(Url).trim();
        final Intent intent = new Intent(splash.this, login.class);

        //giving dialog for an available update
        AlertDialog.Builder builder = new AlertDialog.Builder(splash.this);
        builder.setTitle("Update Available");
        builder.setMessage("A newer version is available ...");
        builder.setNegativeButton("Maybe later", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences sharedPreferences=getSharedPreferences(SHARED_PREF,MODE_PRIVATE);
                String username=sharedPreferences.getString(USERNAME,"");
                String password=sharedPreferences.getString(PASSWORD,"");
                File file = new File(Environment.getExternalStorageDirectory()+"/"+DIR_NAME,"file.json");
                if(username!="" && password!=""){
                    message="Logging in User ";
                    loginifexist(username,password);
                    Log.d(TAG, "check_for_update: logging in user ");
                    seekBar.setProgress(80);
                }else
                if(file.exists()){

                    String credentail=new make_file_in_directory(SPLASH_ACTIVITY,getApplicationContext(),username).read_credentail_file(file);
                    try {
                        JSONObject obj = new JSONObject(credentail);
                        JSONObject crednt = obj.getJSONObject("credential");
                        username=crednt.getString("username");
                        password=crednt.getString("password");

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(USERNAME,username);
                        editor.putString(PASSWORD,password);
                        editor.apply();

                        loginifexist(username,password);

                    }catch (Exception e){
                        Log.d("json_file_writing", "onClick: "+e.getMessage());
                    }

                }
                else{
                    seekBar.setProgress(100);
                    startActivity(new Intent(splash.this,login.class));
                    finish();
                }
            }
        });
        builder.setPositiveButton("Update now", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                webView.setVisibility(View.VISIBLE);

                webView.getSettings().setJavaScriptEnabled(true);
                webView.loadUrl(new_Url);
                webView.setWebViewClient(new WebViewClient());

                if(Build.MANUFACTURER.toLowerCase().equals("samsung")) {
                    new update_application(getApplicationContext()).samsung_store_update();
                }else{
                    checkforpermission();
                }

            }
        })
                .setCancelable(false)
                .create().show();
    }

    public void Check_Location_Enabled(){

        try {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            boolean gpsenabled = false;
            boolean networkenabled = false;


            try {
                gpsenabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception e) {
                Log.d(TAG, "Check_Location_Enabled: gpslocation " + e.getMessage());
            }

            try {
                networkenabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception e) {
                Log.d(TAG, "Check_Location_Enabled: networkenable " + e.getMessage());
            }

            if (!gpsenabled) {

                new AlertDialog.Builder(splash.this)
                        .setCancelable(false)
                        .setTitle("GPS enabling required!")
                        .setMessage("Enable GPS to give you better suggestion. For more detail please refer to privacy policy.")
                        .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                get_telephone_state_change_permission();
                            }
                        })
                        .setNeutralButton("Privacy Policy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent browser = new Intent(Intent.ACTION_VIEW,Uri.parse("http://free4all.ezyro.com/Music_streaming/privacy_policy.html"));
                                startActivity(browser);
                                finish();
                            }
                        })
                        .create()
                        .show();

            }else {
                get_telephone_state_change_permission();
            }
        }catch (Exception e){
            new internal_error_report(context,"Error in GPSTracker: "+e.getMessage(),sharedPreferences.getString(USERNAME,""));
        }
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

    public void get_telephone_state_change_permission()
    {
        String DENIED_FIRST_TIME="denied_first_time";
        if(ContextCompat.checkSelfPermission(splash.this, Manifest.permission.READ_PHONE_STATE)== PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(splash.this, WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(splash.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(splash.this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(splash.this,Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(splash.this,Manifest.permission.ACCESS_BACKGROUND_LOCATION)==PackageManager.PERMISSION_GRANTED) {

            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putBoolean(TELEPHONE_STATE_CHANGE_PERMISSION,true);
            editor.putBoolean(LOCATION_ACCESS_PERMISSION,true);
            editor.apply();

            if(!isservicerunning(GPSTracker.class)){

                start_job_schedular_for_location_access();

            }
            getdetails();
        }else{
            if(ActivityCompat.shouldShowRequestPermissionRationale(splash.this, Manifest.permission.READ_PHONE_STATE ) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(splash.this, WRITE_EXTERNAL_STORAGE )&&
                    ActivityCompat.shouldShowRequestPermissionRationale(splash.this, Manifest.permission.READ_EXTERNAL_STORAGE) &&
                            ActivityCompat.shouldShowRequestPermissionRationale(splash.this, ACCESS_COARSE_LOCATION )&&
                            ActivityCompat.shouldShowRequestPermissionRationale(splash.this, ACCESS_BACKGROUND_LOCATION )&&
                            ActivityCompat.shouldShowRequestPermissionRationale(splash.this, ACCESS_FINE_LOCATION )){

                if(sharedPreferences.getBoolean(DENIED_FIRST_TIME,true)) {
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putBoolean(DENIED_FIRST_TIME,false);
                    editor.apply();
                    new AlertDialog.Builder(splash.this)
                            .setTitle("Permission needed")
                            .setMessage("Permissions required to provide you best suggestions. Refer to Privacy Policy for more details.")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(splash.this, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE,ACCESS_FINE_LOCATION,ACCESS_BACKGROUND_LOCATION,ACCESS_COARSE_LOCATION}, 20);
                                }
                            })
                            .setNeutralButton("Privacy Policy", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent browser = new Intent(Intent.ACTION_VIEW,Uri.parse("http://free4all.ezyro.com/Music_streaming/privacy_policy.html"));
                                    startActivity(browser);
                                    finish();
                                }
                            })
                            .setCancelable(false)
                            .create().show();
                }else{
                    ActivityCompat.requestPermissions(splash.this, new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE,ACCESS_FINE_LOCATION,ACCESS_BACKGROUND_LOCATION,ACCESS_COARSE_LOCATION}, 20);
                }

            }else{
                ActivityCompat.requestPermissions(splash.this,new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE,ACCESS_FINE_LOCATION,ACCESS_BACKGROUND_LOCATION,ACCESS_COARSE_LOCATION},20);
            }
        }
    }

    public void checkforpermission()
    {
        if(ContextCompat.checkSelfPermission(splash.this, WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {

            webView.setDownloadListener(new DownloadListener() {
                @Override
                public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

                    contentlength=contentLength;
                    downloadupdatedapk downloadupdatedapk = new downloadupdatedapk();
                    downloadupdatedapk.execute(url,userAgent,contentDisposition,mimetype);
                }
            });
        }else{
            if(ActivityCompat.shouldShowRequestPermissionRationale(splash.this, WRITE_EXTERNAL_STORAGE)){

                new AlertDialog.Builder(splash.this)
                        .setTitle("Permission needed")
                        .setMessage("Please provide to permission to start installing latest update :)")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(splash.this,new String[]{WRITE_EXTERNAL_STORAGE},10);
                            }
                        })
                        .setCancelable(false)
                        .create().show();

            }else{
                ActivityCompat.requestPermissions(splash.this,new String[]{WRITE_EXTERNAL_STORAGE},10);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if(requestCode==10 ){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d(TAG, "onRequestPermissionsResult: ");

                webView.setDownloadListener(new DownloadListener() {
                    @Override
                    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                        contentlength=contentLength;
                        downloadupdatedapk downloadupdatedapk = new downloadupdatedapk();
                        downloadupdatedapk.execute(url,userAgent,contentDisposition,mimetype);

                    }
                });
            }else{

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(splash.this,login.class);
                        try {
                            Thread.sleep(3000);
                            startActivity(intent);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }else
        if(requestCode==20 ){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED && grantResults[2]==PackageManager.PERMISSION_GRANTED &&
            grantResults[3]==PackageManager.PERMISSION_GRANTED && grantResults[4]==PackageManager.PERMISSION_GRANTED && grantResults[5]==PackageManager.PERMISSION_GRANTED) {

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(LOCATION_ACCESS_PERMISSION,true);
                    editor.putBoolean(TELEPHONE_STATE_CHANGE_PERMISSION, true);
                    editor.apply();

                if(!isservicerunning(GPSTracker.class)){
                    start_job_schedular_for_location_access();
                }

                getdetails();
            }
//            else
//                if(grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1]==PackageManager.PERMISSION_DENIED || grantResults[2]==PackageManager.PERMISSION_DENIED){
//
//                    if(grantResults[0] == PackageManager.PERMISSION_DENIED)
//                        Toast.makeText(SPLASH_ACTIVITY, "Manage Phone permission Denied, Allow this permission to give you better experience.", Toast.LENGTH_LONG).show();
//                    if(grantResults[1] == PackageManager.PERMISSION_DENIED)
//                        Toast.makeText(SPLASH_ACTIVITY, "Manage Media permission Denied, Allow this permission to give you better experience.", Toast.LENGTH_LONG).show();
//                    if(grantResults[2] == PackageManager.PERMISSION_DENIED)
//                        Toast.makeText(SPLASH_ACTIVITY, "Manage Media permission Denied, Allow this permission to give you better experience.", Toast.LENGTH_LONG).show();
//                    if(grantResults[3] == PackageManager.PERMISSION_DENIED)
//                        Toast.makeText(SPLASH_ACTIVITY, "Manage Media permission Denied, Allow this permission to give you better experience.", Toast.LENGTH_LONG).show();
//                    if(grantResults[4] == PackageManager.PERMISSION_DENIED)
//                        Toast.makeText(SPLASH_ACTIVITY, "Manage Media permission Denied, Allow this permission to give you better experience.", Toast.LENGTH_LONG).show();
//                    getdetails();
//                }
            else{
                if(grantResults[0]!=PackageManager.PERMISSION_GRANTED) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(TELEPHONE_STATE_CHANGE_PERMISSION, false);
                    editor.apply();
                }else{
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(TELEPHONE_STATE_CHANGE_PERMISSION, true);
                    editor.apply();
                }
                    if(grantResults[3]!=PackageManager.PERMISSION_GRANTED && grantResults[5]!=PackageManager.PERMISSION_GRANTED)
                    {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(LOCATION_ACCESS_PERMISSION, false);
                        editor.apply();
                }else {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(LOCATION_ACCESS_PERMISSION, true);
                        editor.apply();

                        if(!isservicerunning(GPSTracker.class)){
                            start_job_schedular_for_location_access();
                        }

                    }
                getdetails();
            }
        }
    }

    public class downloadupdatedapk extends AsyncTask<String,Integer,Void>
    {

        @Override
        protected void onPreExecute() {
            Toast.makeText(splash.this, "Downloading...", Toast.LENGTH_SHORT).show();
            seekBar.setProgress(0);
            message="Downloading ";
        }

        @Override
        protected Void doInBackground(String... strings) {
            final String url = strings[0],useragent=strings[1],contentdepisition = strings[2],mimetype = strings[3];
            HttpURLConnection urlConnection=null;
            FileOutputStream fos=null;
            InputStream is=null;
            File file = null;

            try {
                URL url1=new URL(url);
                urlConnection=(HttpURLConnection)url1.openConnection();
                String cookie=CookieManager.getInstance().getCookie(url);
                urlConnection.setRequestProperty("Cookie",cookie);
                urlConnection.setRequestProperty("User-Agent",useragent);
                urlConnection.setDoInput(true);
                if(urlConnection.getResponseCode()!=HttpURLConnection.HTTP_OK){
                    urlConnection.connect();
                }

                Log.d(TAG, "doInBackground: content lenght is "+(long)urlConnection.getContentLength());
                //urlConnection.setFixedLengthStreamingMode(contentlength);

                 file= new File(Environment.getExternalStorageDirectory()+"/"+Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentdepisition, mimetype));

                if(file.exists()){
                    if(file.delete()){
                        Log.d(TAG, "doInBackground: file deleted ");
                    }else{
                        Log.d(TAG, "doInBackground: delete failed ");
                    }
                }else{
                    Log.d(TAG, "doInBackground: file not exist ");
                }

                fos = new FileOutputStream(new File(Environment.getExternalStorageDirectory()+"/"+Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentdepisition, mimetype)));
                is =urlConnection.getInputStream();

                updatedata(fos,is);

                fos.close();
                is.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.d(TAG, "doInBackground: malformation error "+e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "doInBackground: ioexception is "+e.getMessage());
            }finally {
                Log.d(TAG, "doInBackground: error in connection is " + Objects.requireNonNull(urlConnection).getErrorStream());
                urlConnection.disconnect();

                downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                long id=downloadManager.addCompletedDownload(URLUtil.guessFileName(url, contentdepisition, mimetype),"this is the file you downloaded",false,mimetype,
                        file.getAbsolutePath(),contentlength,true);

                Log.d(TAG, "doInBackground: id is "+id);

                try {
                    ParcelFileDescriptor file1=downloadManager.openDownloadedFile(id);
                    if(file1.canDetectErrors())
                        file1.checkError();
                    Log.d(TAG, "doInBackground: fd is "+file1.getFd());
                    Log.d(TAG, "doInBackground: size is "+file1.getStatSize());
                    Log.d(TAG, "doInBackground: content of this "+file1.describeContents());
                    file1.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                File files=new File("/storage/emulated/0/"+Environment.DIRECTORY_DOWNLOADS,URLUtil.guessFileName(url,contentdepisition,mimetype));
                path = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", files);

                try {
                    startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
                    //sendBroadcast(new Intent(getApplicationContext(),Download_complete.class).putExtra("path",files.getAbsolutePath()).putExtra("mimetype",mimetype));
                    //context.startActivity(new Intent(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                }catch (Exception e){
                    Log.d(TAG, "doInBackground: exception action download complee is "+e.getMessage());
                }
                if(!files.exists()) {
                    path = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", files);
                    Log.d(TAG, "DownloadFile: filepath is " + path);

                    final Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
                    //pdfOpenintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    message = "Download Complete ";
                    //pdfOpenintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //pdfOpenintent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    //pdfOpenintent.setType(mimetype);
                    //pdfOpenintent.putExtra(Intent.EXTRA_TEXT,Uri.fromFile(new File(Environment.getExternalStorageDirectory(),URLUtil.guessFileName(url,contentdepisition,mimetype))));
                    pdfOpenintent.setDataAndType(path, mimetype);
                    List<ResolveInfo> resolveInfos = getApplicationContext().getPackageManager().queryIntentActivities(pdfOpenintent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resolveInfos) {
                        getApplicationContext().grantUriPermission(getApplicationContext().getPackageName() + ".provider", path, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                    pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                    try {
                        //startActivity(pdfOpenintent);
                        sendBroadcast(new Intent(getApplicationContext(), phone_state_broadcast_reciever.class));
                    } catch (Exception e) {
                        Log.d(TAG, "doInBackground: exception is " + e.getMessage());
                    }

                }else{
                    Log.d(TAG, "doInBackground: file dosent exist");
                }
            }
            return null;
        }
        public void updatedata(FileOutputStream fos,InputStream is){

            byte[] buffer = new byte[1024];
            int len=0,total=0,percent;

            try {
                while ((len = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                    total += len;
                    percent=(int)(((float)total/(float)contentlength)*100);
                    Log.d(TAG, "doInBackground: writen data is "+percent);
                    message=percent+"%";
                    onProgressUpdate(percent);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            message="Download Complete ";
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            seekBar.setProgress(values[0]);
        }
    }

    public void loginifexist(final String username,final String password)
    {
        String url="https://rentdetails.000webhostapp.com/musicplayer_files/login.php";
        final String urlforusergetail="https://rentdetails.000webhostapp.com/musicplayer_files/getuserdata.php";

        make_dir();

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                if(response.equalsIgnoreCase("0")){
                    if(!sharedPreferences.getString(USERNAME,"").equals("")) {
                        Toast.makeText(splash.this, "Please verify your email! ", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(splash.this, register.class)
                                .putExtra("isverified", false)
                                .putExtra("username", username));
                    }else{
                        Toast.makeText(splash.this, "Please Login!!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(splash.this,login.class));
                    }
                    seekBar.setProgress(100);

                }else if(response.equalsIgnoreCase("1")){

                    message="Loading credentails ";
                    new getuserdetails(urlforusergetail,username,password).execute();
                    seekBar.setProgress(90);

                }else{
                    startActivity(new Intent(splash.this,login.class));
                    seekBar.setProgress(100);
                    finish();
                }
                Log.d(TAG, "onResponse: response is :"+response);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d(TAG, "onErrorResponse: error response "+error.getMessage());
                Toast.makeText(splash.this, "Unable to connect, Try again later...", Toast.LENGTH_SHORT).show();

                String err="Error in loginifexist in splash "+error.getMessage();
                new internal_error_report(SPLASH_ACTIVITY,err,sharedPreferences.getString(USERNAME,"")).execute();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();

                params.put("username",username);
                params.put("password",password);

                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    public class getuserdetails extends AsyncTask<String,Void,Void>
    {
        String url,username,password;

        public getuserdetails(String url,String username,String password) {
            this.url = url;
            this.username=username;
            this.password=password;
        }

        @Override
        protected Void doInBackground(String... strings) {

            message="Loading Credentials ";
            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String success = jsonObject.getString("success");
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        if (success.equals("1")) {
                                JSONObject object = jsonArray.getJSONObject(0);

                                String name = object.getString("name");
                                String email = object.getString("email");
                                String image = object.getString("image");

                                String customurl = "https://rentdetails.000webhostapp.com/musicplayer_files/musicimages/"+image;

                                SharedPreferences sharedPreferences=getSharedPreferences(SHARED_PREF,MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(USERNAME,username);
                                editor.putString(PASSWORD,password);
                                editor.putString(login.NAME,name);
                                editor.putString(IMAGE,customurl);
                                editor.putString(EMAIL,email);
                                editor.apply();

                            Log.d(TAG, "onResponse: name "+name);
                            Log.d(TAG, "onResponse: image "+image);
                            Log.d(TAG, "onResponse: email "+email);

                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        message="failed ";
                        //new erroinfetch().execute(e.getMessage());
                        Log.d(TAG, "onResponse: error in json is "+e.getMessage());
                    }
                    seekBar.setProgress(100);

                    if(SHORTCUT_TAP_VALUE==0) {
                        startActivity(new Intent(splash.this, MainActivity.class));
                    }else if(SHORTCUT_TAP_VALUE==1){
                        startActivity(new Intent(splash.this, MainActivity .class).putExtra("fragment_id",2)
                                .putExtra("is_from_shortcut",true)
                                .putExtra("playlist_position",1));
                    }else if(SHORTCUT_TAP_VALUE==2){
                        startActivity(new Intent(splash.this, MainActivity .class).putExtra("fragment_id",2)
                                .putExtra("is_from_shortcut",true)
                                .putExtra("playlist_position",2));
                    }else if(SHORTCUT_TAP_VALUE==3){
                        startActivity(new Intent(splash.this, MainActivity .class).putExtra("fragment_id",2)
                                .putExtra("is_from_shortcut",true)
                                .putExtra("playlist_position",3));
                    }else
                        if(SHORTCUT_TAP_VALUE==4){
                        startActivity(new Intent(splash.this,new_playlist.class));
                    }
                    finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Log.d(TAG, "onErrorResponse: error response "+error.getMessage());
                    Toast.makeText(splash.this, "Unable to connect, Try again later...", Toast.LENGTH_SHORT).show();
                    String err="Error in getuserdetails in splash "+error.getMessage();
                    new internal_error_report(SPLASH_ACTIVITY,err,MainActivity.sharedPreferences.getString(USERNAME,"")).execute();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();

                    params.put("username",username);

                    return params;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(splash.this);

            queue.add(request);

            return null;
        }
    }

    /**
     * <h1>Find the way to install the application to this mobile directly</h1>
     * <p>below functions no use until found the way to directly install the application into the mobile</p>
     * @param url
     * @param userAgent
     * @param contentDisposition
     * @param mimetype
     * @param contentLength
     */

    public void DownloadFile(String url, String userAgent, final String contentDisposition, final String mimetype, final long contentLength )
    {
        Log.d(TAG, "DownloadFile: ");

        message="Downloading Update ";
        seekBar.setProgress(80);
        String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
        String fileName = URLUtil.guessFileName(url, contentDisposition, mimetype);
        destination += fileName;
        final Uri new_uri = Uri.parse("file://" + destination);
        Log.d(TAG, "new uri "+new_uri);
        //text.setText("Downloading will start in a minute...");
        File file = new File(Environment.getExternalStorageDirectory()+"/"+Environment.DIRECTORY_DOWNLOADS
        ,URLUtil.guessFileName(url,contentDisposition,mimetype));

        if(file.exists()){
            if(file.delete()){
                Log.d(TAG, "DownloadFile: file deleted ");
            }else{
                Log.d(TAG, "DownloadFile: couldnot locate your file at "+file.getAbsolutePath());
            }
        }

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setMimeType(mimetype);
        String cookie = CookieManager.getInstance().getCookie(url);
        request.addRequestHeader("cookie", cookie);
        Log.d(TAG, "DownloadFile: cookie is "+cookie);
        request.addRequestHeader("User-Agent", userAgent);
        request.setDescription(fileName);
        request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimetype));
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationUri(new_uri);

        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        assert downloadManager != null;
        download = downloadManager.enqueue(request);

        Log.d(TAG, "DownloadFile: download"+download);

        File file_dir = new File("/storage/emulated/0/Download/", URLUtil.guessFileName(url, contentDisposition, mimetype));
        path = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file_dir);
        Log.d(TAG, "DownloadFile: filepath is "+path);

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "onReceive: into the reciver ");
                final Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
                pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pdfOpenintent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pdfOpenintent.setDataAndType(path, downloadManager.getMimeTypeForDownloadedFile(download));
                context.startActivity(pdfOpenintent);
                context.unregisterReceiver(this);

            }
        };

        registerReceiver(broadcastReceiver,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }

    private void CheckDwnloadStatus(){

        DownloadManager.Query query = new DownloadManager.Query();
        long id = download;
        query.setFilterById(id);
        Cursor cursor = downloadManager.query(query);

        if(cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
            int status = cursor.getInt(columnIndex);
            int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
            int reason = cursor.getInt(columnReason);

            switch(status){
                case DownloadManager.STATUS_FAILED:
                    String failedReason = "";
                    switch(reason){
                        case DownloadManager.ERROR_CANNOT_RESUME:
                            failedReason = "ERROR_CANNOT_RESUME";
                            break;
                        case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                            failedReason = "ERROR_DEVICE_NOT_FOUND";
                            break;
                        case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                            failedReason = "ERROR_FILE_ALREADY_EXISTS";
                            break;
                        case DownloadManager.ERROR_FILE_ERROR:
                            failedReason = "ERROR_FILE_ERROR";
                            break;
                        case DownloadManager.ERROR_HTTP_DATA_ERROR:
                            failedReason = "ERROR_HTTP_DATA_ERROR";
                            break;
                        case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                            failedReason = "ERROR_INSUFFICIENT_SPACE";
                            break;
                        case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                            failedReason = "ERROR_TOO_MANY_REDIRECTS";
                            break;
                        case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                            failedReason = "ERROR_UNHANDLED_HTTP_CODE";
                            break;
                        case DownloadManager.ERROR_UNKNOWN:
                            failedReason = "ERROR_UNKNOWN";
                            break;
                    }

                    Toast.makeText(splash.this,
                            "FAILED: " + failedReason,
                            Toast.LENGTH_LONG).show();
                    Log.d(TAG, "CheckDwnloadStatus: failed with reason "+failedReason);
                    break;
                case DownloadManager.STATUS_PAUSED:
                    String pausedReason = "";

                    switch(reason){
                        case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
                            pausedReason = "PAUSED_QUEUED_FOR_WIFI";
                            break;
                        case DownloadManager.PAUSED_UNKNOWN:
                            pausedReason = "PAUSED_UNKNOWN";
                            break;
                        case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
                            pausedReason = "PAUSED_WAITING_FOR_NETWORK";
                            break;
                        case DownloadManager.PAUSED_WAITING_TO_RETRY:
                            pausedReason = "PAUSED_WAITING_TO_RETRY";
                            break;
                    }

                    Toast.makeText(splash.this,
                            "PAUSED: " + pausedReason,
                            Toast.LENGTH_LONG).show();
                    Log.d(TAG, "CheckDwnloadStatus: paused with reason "+pausedReason);
                    break;
                case DownloadManager.STATUS_PENDING:
                    Toast.makeText(splash.this,
                            "PENDING",
                            Toast.LENGTH_LONG).show();
                    break;
                case DownloadManager.STATUS_RUNNING:
                    Toast.makeText(splash.this,
                            "RUNNING",
                            Toast.LENGTH_LONG).show();
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:

                    Toast.makeText(splash.this,
                            "SUCCESSFUL",
                            Toast.LENGTH_LONG).show();
                    break;
            }
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

}
