package com.musicstreaming.musicstreaming.service;

import android.Manifest;
import android.app.ActivityManager;
import android.app.ProgressDialog;
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
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.musicstreaming.musicstreaming.Exceptionhandler;
import com.musicstreaming.musicstreaming.MainActivity;
import com.musicstreaming.musicstreaming.MyExpandableListAdaptor;
import com.musicstreaming.musicstreaming.child_item_expandable_listview;
import com.musicstreaming.musicstreaming.internal_error_report;
import com.musicstreaming.musicstreaming.login;
import com.musicstreaming.musicstreaming.make_file_in_directory;
import com.musicstreaming.musicstreaming.share_song_items;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.security.Provider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.musicstreaming.musicstreaming.MainActivity.MAIN_ACTIVITY_CONTEXT;
import static com.musicstreaming.musicstreaming.MainActivity.drawerLayout;
import static com.musicstreaming.musicstreaming.MainActivity.expandableListView;
import static com.musicstreaming.musicstreaming.MainActivity.fragment;
import static com.musicstreaming.musicstreaming.MainActivity.setSongCounter;
import static com.musicstreaming.musicstreaming.service.GPSTracker.IS_JOB_SERVICE_RUNNING;
import static com.musicstreaming.musicstreaming.service.GPSTracker.addr_location;
import static com.musicstreaming.musicstreaming.shared_songs_list.shareSongList;
import static com.musicstreaming.musicstreaming.shared_songs_list.showText;
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
    public static Context CURRENT_ACTIVITY_CONTEXT,PREVIOUS_ACTIVITY_CONTEXT;
    public static List<Integer> group_item=new ArrayList<>();
    public static List<child_item_expandable_listview> child_item0 = new ArrayList<>();
    public static List<child_item_expandable_listview> child_item1 = new ArrayList<>();
    public static Map<Integer,List<child_item_expandable_listview>> child_items=new HashMap<Integer, List<child_item_expandable_listview>>();
    public static MyExpandableListAdaptor expandableListAdapter;
    public static ArrayList<String> allFriends = new ArrayList<>();
    public static List<String> frd_usernames = new ArrayList<>();

    public static share_song_items shareSongItems;
    public static ArrayList<share_song_items> shareSongItemsArrayList = new ArrayList<>();
    public static ArrayAdapter<share_song_items> share_song_itemsArrayAdapter;
    public static int counter=0;
    public static String MAX_SHARE_SONG_ID="max_share_song_id";

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
                new getSharedSongs().execute();
            }
            handler.postDelayed(this,30000);
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

                        new get_friends_details().execute("https://rentdetails.000webhostapp.com/musicplayer_files/friends_folder/get_friends_status.php",username);


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + error.getMessage());
                        new get_friends_details().execute("https://rentdetails.000webhostapp.com/musicplayer_files/friends_folder/get_friends_status.php",username);
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

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        status="offline";
        CURRENT_PLAYING_SONG="";
        handler.removeCallbacks(runnable);
        new change_status().execute();
        stopSelf();
    }

    public class get_friends_details extends AsyncTask<String,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                expandableListAdapter = new MyExpandableListAdaptor(MainActivity.MAIN_ACTIVITY_CONTEXT,child_items,group_item);
            }catch (Exception e){
                expandableListAdapter = new MyExpandableListAdaptor(SPLASH_ACTIVITY,child_items,group_item);
            }
        }

        @Override
        protected Void doInBackground(String... strings) {

            String url = strings[0];
            username=sharedPreferences.getString(login.USERNAME,"");
            if(username.equals(""))
                username=strings[1];

            if(!username.equals("")) {
                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        child_item0.clear();
                        child_item1.clear();
                        allFriends.clear();
                        String[] status_values=new String[2];

                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            status_values[0]=jsonObject.getString("online");
                            status_values[1]=jsonObject.getString("offline");
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            if (success.equals("1")) {
                                for (int i =0 ; i <jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    String id = object.getString("id");
                                    String name = object.getString("name");
                                    String username = object.getString("username");
                                    String status = object.getString("status");
                                    String current_playlist = object.getString("now_playing");
                                    String image = object.getString("image");

                                    allFriends.add(name);
                                    frd_usernames.add(username);

                                    if(status.equals("0")) {
                                        child_item0.add(new child_item_expandable_listview(id, username, name, current_playlist, status, image));
                                    }else if(status.equals("1")){
                                        child_item1.add(new child_item_expandable_listview(id, username, name, current_playlist, status, image));
                                    }

                                    Log.d(TAG, "onResponse:  "+id+" : "+name+" : "+username+" : "+status+" : "+current_playlist+" : "+image);

                                }
                            }

                            child_items.put(0,child_item0);
                            child_items.put(1,child_item1);

                            populate_group(status_values);

                            if(expandableListView!=null && drawerLayout!=null)
                                if(!drawerLayout.isDrawerOpen(GravityCompat.END))
                                    expandableListView.setAdapter(expandableListAdapter);

                            expandableListAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        String err="Error in getdata in playlistfragment "+error.getMessage();
                        new internal_error_report(SPLASH_ACTIVITY,err, sharedPreferences.getString(login.USERNAME,"")).execute();

                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params=new HashMap<String, String>();

                        params.put("username", username);

                        return params;
                    }
                };


                RequestQueue queue = Volley.newRequestQueue(SPLASH_ACTIVITY);
                queue.add(request);
            }else {
                Log.d("getting_fav_songs", "doInBackground: username is found null, pls check");
            }

            return null;
        }
    }

    public void populate_group(String[] i){
        group_item.clear();
        for(String j : i)
            group_item.add(Integer.parseInt(j));
    }

    public static class getSharedSongs extends AsyncTask<Void,Void,Void>{

        String message="";
        boolean DATA_CHANGED=false;
        public static int counts=0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            counter=0;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            final String username=sharedPreferences.getString(login.USERNAME,""),url="https://rentdetails.000webhostapp.com/musicplayer_files/share_song/show_shared_song.php";

            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    shareSongItemsArrayList.clear();
                    message="progress done";

                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String success = jsonObject.getString("success");
                        counts=Integer.parseInt(jsonObject.getString("count"));
                        if(counts>0) {

                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            if (success.equals("1")) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    String id = object.getString("id");
                                    String frduser = object.getString("frduser");
                                    String songid = object.getString("songid");
                                    String status = object.getString("status");
                                    String sendtime = object.getString("sendtime");
                                    String songname = object.getString("songname");
                                    String singer = object.getString("singer");
                                    String url = object.getString("url");
                                    String name = object.getString("name");

                                    shareSongItems = new share_song_items(id,name,sendtime,songname,singer,frduser,songid,url,status);
                                    shareSongItemsArrayList.add(shareSongItems);

                                    if(Integer.parseInt(status)==0) {
                                        counter++;
                                    }

                                }

                            }

                            if(sharedPreferences.getInt(MAX_SHARE_SONG_ID,0)<Integer.parseInt(shareSongItemsArrayList.get(0).getId())){

                                if(share_song_itemsArrayAdapter!=null)
                                    share_song_itemsArrayAdapter.notifyDataSetChanged();

                                if(shareSongList!=null && showText!=null){
                                    shareSongList.setVisibility(View.VISIBLE);
                                    showText.setVisibility(View.INVISIBLE);
                                }

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putInt(MAX_SHARE_SONG_ID,Integer.parseInt(shareSongItemsArrayList.get(0).getId()));
                                editor.apply();

                                Log.d("counter_values", "onResponse: updated the list"+sharedPreferences.getInt(MAX_SHARE_SONG_ID,0)+" "+Integer.parseInt(shareSongItemsArrayList.get(0).getId()));

                            }else
                                if(shareSongItemsArrayList.size()==0){
                                    if(shareSongList!=null && showText!=null){
                                        shareSongList.setVisibility(View.INVISIBLE);
                                        showText.setVisibility(View.VISIBLE);
                                    }
                            }

                        }

                        setSongCounter(counter);

                        Log.d("counter_values ", "onResponse: counter_value"+counter);

                    } catch (Exception e) {
                        e.printStackTrace();
                        message="failed ";
                        Log.d("getData", "onResponse: error in json is "+e.getMessage());
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    message="error in fetching playlist";

                    String err="Error in getdata in playlistfragment "+error.getMessage();
                    new internal_error_report(SPLASH_ACTIVITY,err,sharedPreferences.getString(login.USERNAME,"")).execute();

                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params=new HashMap<String, String>();

                    params.put("username", username);

                    return params;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(SPLASH_ACTIVITY);
            queue.add(request);

            return null;
        }
    }
}
