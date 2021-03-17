package com.musicstreaming.musicstreaming.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.os.BuildCompat;

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
import com.musicstreaming.musicstreaming.BuildConfig;
import com.musicstreaming.musicstreaming.MainActivity;
import com.musicstreaming.musicstreaming.child_item_expandable_listview;
import com.musicstreaming.musicstreaming.internal_error_report;
import com.musicstreaming.musicstreaming.listofplaylist;
import com.musicstreaming.musicstreaming.songsfromplaylist;
import com.musicstreaming.musicstreaming.splash;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;

import static com.musicstreaming.musicstreaming.MainActivity.MAIN_ACTIVITY_CONTEXT;
import static com.musicstreaming.musicstreaming.login.SHARED_PREF;
import static com.musicstreaming.musicstreaming.login.USERNAME;
import static com.musicstreaming.musicstreaming.playlistfragment.listofplaylistArrayList_for_fav;
import static java.lang.Thread.sleep;

public class get_fav_song_list extends AsyncTask<String,Void,Void> {

    Context context;
    SharedPreferences sharedPreferences;
    public static ArrayList<listofplaylist> listofplaylistArrayList_for_shortcut=new ArrayList<>();
    String username,TAG="get_fav_list";
    Bitmap imgBitmap1,imgBitmap2,imgBitmap3;
    boolean IS_DONE=false;

    public get_fav_song_list(Context context) {
        this.context=context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        sharedPreferences=context.getSharedPreferences("sharedpref",Context.MODE_PRIVATE);

    }

    @Override
    protected Void doInBackground(String... strings) {

        String url = strings[0];
        username=sharedPreferences.getString(USERNAME,"");
        if(username.equals(""))
            username=strings[1];

        if(!username.equals("")) {
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                listofplaylistArrayList_for_shortcut.clear();

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    if (success.equals("1")) {
                        for (int i =0 ; i <jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            String id = object.getString("id");
                            String name = object.getString("name");
                            String note = object.getString("note");
                            String image = object.getString("image");
                            String likes = object.getString("likes");
                            String totl_like = object.getString("totl_like");

                            listofplaylist listofplaylist_for_fav = new listofplaylist(id,name,image,likes,note,totl_like);
                            listofplaylistArrayList_for_shortcut.add(listofplaylist_for_fav);
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                listofplaylistArrayList_for_fav=listofplaylistArrayList_for_shortcut;
                Log.d("getting_fav_songs", "onResponse: done "+listofplaylistArrayList_for_shortcut.size());

                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N_MR1)
                    createShortcutForAppIcon();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String err="Error in getdata in playlistfragment "+error.getMessage();
                new internal_error_report(context,err, MainActivity.sharedPreferences.getString(USERNAME,"")).execute();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<String, String>();

                params.put("username", username);

                return params;
            }
        };


            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(request);
        }else {
            Log.d("getting_fav_songs", "doInBackground: username is found null, pls check");
        }

        return null;
    }

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    private void createShortcutForAppIcon() {


        if(listofplaylistArrayList_for_shortcut.size()>0) {
            Log.d(TAG, "run: entered loop, pass 1");

            final ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);

            Log.d(TAG, "run: entered loop, pass 2");

            final Intent intent1 = new Intent(context, splash.class).putExtra("shortcut_tap_value", 1);
            intent1.setAction(Intent.ACTION_VIEW);
            final Intent intent2 = new Intent(context, splash.class).putExtra("shortcut_tap_value", 2);
            intent2.setAction(Intent.ACTION_VIEW);
            final Intent intent3 = new Intent(context, splash.class).putExtra("shortcut_tap_value", 3);
            intent3.setAction(Intent.ACTION_VIEW);

            Log.d(TAG, "run: entered loop, pass 3");


            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {

                        if (listofplaylistArrayList_for_shortcut.size() > 1) {
                            URL urls = new URL(listofplaylistArrayList_for_shortcut.get(1).getImage());
                            imgBitmap1 = BitmapFactory.decodeStream(urls.openConnection().getInputStream());

                            if (listofplaylistArrayList_for_shortcut.size() > 2) {
                                urls = new URL(listofplaylistArrayList_for_shortcut.get(2).getImage());
                                imgBitmap2 = BitmapFactory.decodeStream(urls.openConnection().getInputStream());

                                if (listofplaylistArrayList_for_shortcut.size() > 3) {
                                    urls = new URL(listofplaylistArrayList_for_shortcut.get(3).getImage());
                                    imgBitmap3 = BitmapFactory.decodeStream(urls.openConnection().getInputStream());

                                    IS_DONE = true;
                                } else {
                                    IS_DONE = true;
                                }
                            } else {
                                IS_DONE = true;
                            }
                        } else {
                            IS_DONE = true;
                        }
                        if (IS_DONE)
                            return;
                        sleep(100);
                    } catch (Exception e) {
                        Log.d(TAG, "run: errorr " + e.getMessage());
                    }

                }
            }).start();

            final Handler handler = new Handler();

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (IS_DONE) {
                        try {
                            if (listofplaylistArrayList_for_shortcut.size() > 1) {
                                ShortcutInfo shortcutInfo1 = new ShortcutInfo.Builder(context, "playlist_1")
                                        .setShortLabel(listofplaylistArrayList_for_shortcut.get(1).getName())
                                        .setLongLabel(listofplaylistArrayList_for_shortcut.get(1).getName())
                                        .setIcon(Icon.createWithBitmap(imgBitmap1))
                                        .setIntent(intent1)
                                        .build();

                                if (listofplaylistArrayList_for_shortcut.size() > 2) {
                                    ShortcutInfo shortcutInfo2 = new ShortcutInfo.Builder(context, "playlist_2")
                                            .setShortLabel(listofplaylistArrayList_for_shortcut.get(2).getName())
                                            .setLongLabel(listofplaylistArrayList_for_shortcut.get(2).getName())
                                            .setIcon(Icon.createWithBitmap(imgBitmap2))
                                            .setIntent(intent2)
                                            .build();

                                    if (listofplaylistArrayList_for_shortcut.size() > 3) {
                                        ShortcutInfo shortcutInfo3 = new ShortcutInfo.Builder(context, "playlist_3")
                                                .setShortLabel(listofplaylistArrayList_for_shortcut.get(3).getName())
                                                .setLongLabel(listofplaylistArrayList_for_shortcut.get(3).getName())
                                                .setIcon(Icon.createWithBitmap(imgBitmap3))
                                                .setIntent(intent3)
                                                .build();

                                        shortcutManager.setDynamicShortcuts(Arrays.asList(shortcutInfo3, shortcutInfo2, shortcutInfo1));
                                        Log.d(TAG, "run: entered loop, set 3 playlists ");
                                    } else {
                                        shortcutManager.setDynamicShortcuts(Arrays.asList(shortcutInfo2, shortcutInfo1));
                                        Log.d(TAG, "run: entered loop, set 2 playlists ");
                                    }
                                } else {
                                    shortcutManager.setDynamicShortcuts(Arrays.asList(shortcutInfo1));
                                    Log.d(TAG, "run: entered loop, set 1 playlists ");
                                }
                            }else {
                                shortcutManager.disableShortcuts(Arrays.asList("playlist_1"),"No Longer Exists");
                                Log.d(TAG, "run: entered loop, set 0 playlists ");
                            }
                        }catch (Exception e){
                            Log.d(TAG, "run: hey i got an error, lol you idiot "+e.getMessage());
                        }
                        Log.d(TAG, "run: entered loop, pass 5");

                        Log.d(TAG, "run: found  size " + listofplaylistArrayList_for_shortcut.size());
                    } else {
                        handler.postDelayed(this, 1000);
                    }
                }
            };

            runnable.run();

            Log.d(TAG, "createShortcutForAppIcon: exited lop");
        }

    }


}
