package com.example.musicstreaming;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.ArrayLinkedVariables;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.renderscript.ScriptGroup;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.musicstreaming.service.onclearfrompercentservice;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.ReferenceQueue;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.example.musicstreaming.login.SHARED_PREF;
import static com.example.musicstreaming.login.USERNAME;
import static com.example.musicstreaming.service.onclearfrompercentservice.POSITION_FAV_PLAYLIST;
import static com.example.musicstreaming.service.onclearfrompercentservice.TAG;
import static com.example.musicstreaming.service.onclearfrompercentservice.context;
import static com.example.musicstreaming.service.onclearfrompercentservice.isplaying;
import static com.example.musicstreaming.service.onclearfrompercentservice.isprepared;
import static com.example.musicstreaming.service.onclearfrompercentservice.ontrackpause;
import static com.example.musicstreaming.service.onclearfrompercentservice.ontrackplay;
import static com.example.musicstreaming.service.onclearfrompercentservice.position;

public class songsfromplaylist extends AppCompatActivity {
    /**
     * <h1>Ownership</h1>
     * <p>This Application is Owned by Ashish Kumar, Delhi Technology univerity, Btech ,Mechanical
     * Engineering ,2ndyear Student</p>
     * <p>Finished First version 1.0 on 17-Aug-2020</p>
     */

    int playlist_position;
    ImageView playlistimage;
    TextView playlistnmae;
    String TAG="showingsongs",playlist_id,message="",playlistname,urls="https://rentdetails.000webhostapp.com/musicplayer_files/showsongs.php",
            imageurl="",url_for_search_song="https://rentdetails.000webhostapp.com/musicplayer_files/search_song.php";
    public ListView listViewforsongs;
    public songadapter songadapter;
    listofsongs listofsongs;
    RelativeLayout relativeLayout;
    public static LinearLayout showsongdetails,another,topheader;
    public static TextView songname;
    public static ImageView playpausinbottom;
    public static boolean isfav=false;
    public SharedPreferences sharedPreferences;
    public static ProgressBar progressBars;
    public static String playlistnames;
    LottieAnimationView animationView;
    Animation frombottom,fromtop;
    RelativeLayout loading,main;
    ImageView fav_playlist;

    static ArrayList<listofsongs> listofsongsArrayLisr = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songsfromplaylist);

        Thread.setDefaultUncaughtExceptionHandler(new Exceptionhandler(this));

        Intent intent = getIntent();
        playlist_id= intent.getExtras().getString("id","");
        playlist_position=intent.getExtras().getInt("positions");
        isfav=intent.getExtras().getBoolean("isfav",false);
        Log.d(TAG, "onCreate: id for the song is "+playlist_id);

        playlistimage = findViewById(R.id.playlistimagesinsongslist);
        playlistnmae = findViewById(R.id.playlistnameinsonglist);
        relativeLayout=findViewById(R.id.setbackround);
        songname=findViewById(R.id.songnameatbottom);
        playpausinbottom=findViewById(R.id.playpausinbottom);
        topheader=findViewById(R.id.toplinearheader);
        showsongdetails=findViewById(R.id.bottomsongshowing);
        another=findViewById(R.id.settheheight);
        progressBars=findViewById(R.id.songprogresses);
        animationView=findViewById(R.id.animation_view);
        fav_playlist=findViewById(R.id.fav_playlist);
        frombottom= AnimationUtils.loadAnimation(this,R.anim.frombottom);
        fromtop= AnimationUtils.loadAnimation(this,R.anim.fromtop);
        loading=findViewById(R.id.loading);
        main=findViewById(R.id.main);
        songname.setSelected(true);



        imageurl=intent.getExtras().getString("imageurl","");
        playlistname=intent.getExtras().getString("name","");

        boolean is_from_search = intent.getBooleanExtra("is_from_search",false);
        String song_url = intent.getExtras().getString("song_url","");

        playlistnames=playlistname;
        if(imageurl!=null)
        loadimage(imageurl);

        if(isfav){
            if(playlistfragment.listofplaylistArrayList_for_fav.get(POSITION_FAV_PLAYLIST).getId().equals("101")) {
                fav_playlist.setVisibility(View.GONE);
            }else{
                fav_playlist.setVisibility(View.VISIBLE);
                if(playlistfragment.listofplaylistArrayList_for_fav.get(POSITION_FAV_PLAYLIST).getLikes().equals("1")){
                    fav_playlist.setImageResource(R.drawable.favourate_full);
                }else if(playlistfragment.listofplaylistArrayList_for_fav.get(POSITION_FAV_PLAYLIST).getLikes().equals("0")){
                    fav_playlist.setImageResource(R.drawable.favourate);
                }
            }
        }else{
            fav_playlist.setVisibility(View.VISIBLE);
        }

        sharedPreferences=getSharedPreferences(SHARED_PREF,MODE_PRIVATE);

        showdetail(isprepared);
        changeplaypauseimg(isplaying);

        if(is_from_search) {
            new get_songs().execute(url_for_search_song, song_url);
        }else {

            if (!isfav) {
                if (homefragment.listofplaylistArrayList.get(playlist_position).getLikes().equals("1")) {
                    fav_playlist.setImageResource(R.drawable.favourate_full);
                } else if (homefragment.listofplaylistArrayList.get(playlist_position).getLikes().equals("0")) {
                    fav_playlist.setImageResource(R.drawable.favourate);
                }
            }

            fav_playlist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String like = homefragment.listofplaylistArrayList.get(playlist_position).getLikes();
                    if (like == "1") {
                        fav_playlist.setImageResource(R.drawable.favourate);
                    } else if (like == "0") {
                        fav_playlist.setImageResource(R.drawable.favourate_full);
                    }
                    new likeordislike(like).execute();
                }
            });

            songname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(songsfromplaylist.this, playselectedsong.class)
                            .putExtra("position", 1001)
                            .putExtra("playlist_pos", playlist_position)
                            .putExtra("playlistname", playlistname)
                            .putExtra("playlistid", playlist_id).putExtra("playlist_img_url", imageurl));
                }
            });

            playlistnmae.setText(intent.getStringExtra("name"));
            Log.d(TAG, "onCreate: image url is " + intent.getStringExtra("imageurl"));

            playpausinbottom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isplaying) {
                        playpausinbottom.setImageResource(R.drawable.normal_play);
                        ontrackpause();
                    } else {
                        playpausinbottom.setImageResource(R.drawable.normal_pause);
                        ontrackplay(1000);
                    }
                }
            });

            songadapter = new songadapter(this, listofsongsArrayLisr);
            listViewforsongs = findViewById(R.id.listvieforsongs);
            listViewforsongs.setAdapter(songadapter);

            listViewforsongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                    Log.d(onclearfrompercentservice.TAG, "onItemClick: all variables set to false ");

                    startActivity(new Intent(songsfromplaylist.this, playselectedsong.class)
                            .putExtra("position", position)
                            .putExtra("playlist_pos", playlist_position)
                            .putExtra("playlistname", playlistname)
                            .putExtra("playlistid", playlist_id).putExtra("playlist_img_url", imageurl)
                    );
                }
            });

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.centerCrop();
            requestOptions.override(600, 600);

            Log.d(TAG, "onCreate: your imahe is " + imageurl);

            Glide.with(songsfromplaylist.this)
                    .load(imageurl)
                    .apply(requestOptions)
                    .placeholder(R.drawable.playlist_1)
                    .addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                            Log.d(TAG, "onResourceReady: failed to load image " + e.getMessage());
                            String err = "Error in loading playlist image in songfromplaylist " + e.getMessage();
                            new internal_error_report(getApplicationContext(), err, MainActivity.sharedPreferences.getString(USERNAME, "")).execute();

                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                            Log.d(TAG, "onLoadFailed: image loaded successfully " + resource);

                            return false;
                        }
                    })
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            relativeLayout.setBackground(resource);
                            Log.d(TAG, "onResourceReady: your drawable resource is " + resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            Log.d(TAG, "onLoadCleared: placeholder wiile loading is " + placeholder);
                            relativeLayout.setBackground(placeholder);
                        }

                    });

            if (!isfav) {
                if (listofsongsArrayLisr.isEmpty()) {
                    new getsongs().execute(urls);
                } else {
                    listViewforsongs.startAnimation(frombottom);
                    topheader.startAnimation(fromtop);
                }
            } else if (isfav) {
                if (playlistfragment.listofplaylistArrayList_for_fav.get(POSITION_FAV_PLAYLIST).getId().equals("101")) {
                    String url = "https://rentdetails.000webhostapp.com/musicplayer_files/showfavsongs.php";
                    new favsongs(url).execute();
                } else {
                    new get_fav_songs().execute("https://rentdetails.000webhostapp.com/musicplayer_files/show_fav_playlist_songs.php");
                }
            }
        }
    }

    public  void  loadimage(String urles){

        final CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(songsfromplaylist.this);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(40f);
        circularProgressDrawable.start();

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(circularProgressDrawable);
        requestOptions.skipMemoryCache(true);
        requestOptions.onlyRetrieveFromCache(false);
        requestOptions.priority(Priority.HIGH);
        requestOptions.fitCenter();

        Glide.with(songsfromplaylist.this)
                .load(urles)
                .apply(requestOptions)
                .transform(new RoundedCorners(10))
                .placeholder(R.drawable.playlist_1)
                .transition(DrawableTransitionOptions.withCrossFade())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        assert e != null;

                        String err="Error in loadimage in songfromplaylist "+e.getMessage();
                        new internal_error_report(getApplicationContext(),err,MainActivity.sharedPreferences.getString(USERNAME,"")).execute();
                        circularProgressDrawable.stop();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                        circularProgressDrawable.stop();
                        Log.d(TAG, "onResourceReady: loaded image");
                        return false;
                    }
                })
                .into(playlistimage);
    }

    public class getsongs extends AsyncTask<String,Void,Void>{

        int number=0;

        @Override
        protected void onPreExecute() {
            listViewforsongs.setVisibility(View.GONE);
            loading.setVisibility(View.VISIBLE);
            main.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(final String... strings) {

            String url = strings[0];
            final String username=sharedPreferences.getString(USERNAME,"");

            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    listofsongsArrayLisr.clear();
                    message="Done progress";

                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String success = jsonObject.getString("success");
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        if (success.equals("1")) {
                            for (int i =0 ; i <jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);

                                String id =object.getString("id");
                               String name = object.getString("name");
                               String songurl = object.getString("url");
                               String image = object.getString("image");
                               String like = object.getString("like");
                               String singer = object.getString("singer");
                               String bkcolor=object.getString("bkcolor");

                                listofsongs = new listofsongs(id,name,songurl,image,like,singer,bkcolor);
                                listofsongsArrayLisr.add(listofsongs);
                                songadapter.notifyDataSetChanged();

                            }
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        message="failed ";

                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error.getMessage()!=null){
                        Toast.makeText(songsfromplaylist.this, "error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(songsfromplaylist.this, "unknown error occured ", Toast.LENGTH_SHORT).show();
                    }

                    String err="Error in getsongs in songfromplaylist "+error.getMessage();
                    new internal_error_report(getApplicationContext(),err,MainActivity.sharedPreferences.getString(USERNAME,"")).execute();

                    message="error occured ";
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String ,String>();
                    params.put("id",playlist_id);

                    if(!username.equals("")){
                        params.put("username",username);
                    }

                    return params;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(songsfromplaylist.this);
            queue.add(request);


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            final Handler handler = new Handler();
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {

                    if(!message.equals("") && !message.isEmpty()){

                        loading.setVisibility(View.GONE);
                        main.setVisibility(View.VISIBLE);
                        listViewforsongs.setVisibility(View.VISIBLE);
                        listViewforsongs.startAnimation(frombottom);
                        topheader.startAnimation(fromtop);
                        if(!message.equalsIgnoreCase("Done progress")) {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            listofsongsArrayLisr.clear();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }

                    }else{
                        Log.d(TAG, "run: running for : "+number++);
                        handler.postDelayed(this,1000);
                    }
                }
            };
            runnable.run();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        listofsongsArrayLisr.clear();
        //homefragment.listofplaylistArrayList.clear();
        startActivity(new Intent(songsfromplaylist.this,MainActivity.class));
    }

    public static void showdetail(boolean isplaying){

        LinearLayout.LayoutParams params=(LinearLayout.LayoutParams)another.getLayoutParams();
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) showsongdetails.getLayoutParams();
        if(isplaying){
            try {
                showsongdetails.setVisibility(View.VISIBLE);
                String songnames = playselectedsong.tracks.get(position).getTitle();
                String artist = playselectedsong.tracks.get(position).getAlbum();
                String finalname = songnames + " (" + artist + ")";
                songname.setText(finalname);
                params.weight = 9.1f;
                layoutParams.weight = 0.9f;
            }catch (Exception e){
                e.printStackTrace();
            }

        }else{
            showsongdetails.setVisibility(View.GONE);
            params.weight=10.0f;
            layoutParams.weight=0.0f;
        }

    }

    public static void changeplaypauseimg(boolean playing){
        if(playing){
            playpausinbottom.setImageResource(R.drawable.normal_pause);
        }else{
            playpausinbottom.setImageResource(R.drawable.normal_play);
        }
    }

    public class favsongs extends AsyncTask<String, Void,Void>{

        String url;
        ProgressDialog progressDialog;
        int number=0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            listViewforsongs.setVisibility(View.GONE);
            loading.setVisibility(View.VISIBLE);
            main.setVisibility(View.GONE);
        }

        public favsongs(String url) {
            this.url = url;
        }

        @Override
        protected Void doInBackground(String... strings) {

            final String username=sharedPreferences.getString(USERNAME,"");

            Log.d(TAG, "doInBackground: playlist id is "+playlist_id);


            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    listofsongsArrayLisr.clear();
                    message="Done progress";

                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String success = jsonObject.getString("success");
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        if (success.equals("1")) {
                            for (int i =jsonArray.length()-1 ; i >=0; i--) {
                                JSONObject object = jsonArray.getJSONObject(i);

                                String id =object.getString("id");
                                String name = object.getString("name");
                                String songurl = object.getString("url");
                                String image = object.getString("image");
                                String like = object.getString("like");
                                String singer = object.getString("singer");
                                String bkcolor=object.getString("bkcolor");

                                listofsongs = new listofsongs(id,name,songurl,image,like,singer,bkcolor);
                                listofsongsArrayLisr.add(listofsongs);
                                songadapter.notifyDataSetChanged();

                            }
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        message="failed ";
                        String error = "ERROR in json extract in songfromplaylist "+e.getMessage();
                        new internal_error_report(getApplicationContext(),error,sharedPreferences.getString(USERNAME,"")).execute();
                        Log.d(TAG, "onResponse: error in json is "+e.getMessage());
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error.getMessage()!=null){
                        Toast.makeText(songsfromplaylist.this, "error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(songsfromplaylist.this, "unknown error occured ", Toast.LENGTH_SHORT).show();
                    }
                    Log.d(TAG, "onErrorResponse: error is : "+error.getMessage());

                    String err="Error in favsong in songfromplaylist "+error.getMessage();
                    new internal_error_report(getApplicationContext(),err,sharedPreferences.getString(USERNAME,"")).execute();

                    message="error occured ";
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();

                    params.put("username",username);
                    params.put("playlist",playlist_id);

                    return params;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(songsfromplaylist.this);
            queue.add(request);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            final Handler handler = new Handler();
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {

                    if(!message.equals("") && !message.isEmpty()){
                        loading.setVisibility(View.GONE);
                        main.setVisibility(View.VISIBLE);
                        listViewforsongs.setVisibility(View.VISIBLE);
                        listViewforsongs.startAnimation(frombottom);
                        topheader.startAnimation(fromtop);
                        if(!message.equalsIgnoreCase("Done progress")) {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            listofsongsArrayLisr.clear();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }
                    }else{
                        Log.d(TAG, "run: running for : "+number++);
                        handler.postDelayed(this,1000);
                    }
                }
            };
            runnable.run();
        }
    }

    public static void setprogressforsong(int progress){
        progressBars.setProgress(progress);
    }

    public class likeordislike extends AsyncTask<String,Void,Void>{

        String likes;
        ArrayList<listofplaylist> trackArrayList=new ArrayList<>();

        public likeordislike(String likes) {
            this.likes = likes;
        }

        @Override
        protected Void doInBackground(String... strings) {

            final String username=sharedPreferences.getString(USERNAME,"");

            shownewelements();
            Log.d(TAG, "doInBackground: ----------------------------------------");

            coplyelements();


            String url="https://rentdetails.000webhostapp.com/musicplayer_files/fav_playlist.php";
            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    if(response.equals("1")){
                        fav_playlist.setImageResource(R.drawable.favourate_full);

                        setelement("1");
                        Log.d(TAG, "onResponse: called positive ");
                        Toast.makeText(songsfromplaylist.this, "Liked playlist "+homefragment.listofplaylistArrayList.get(playlist_position).getName(), Toast.LENGTH_SHORT).show();

                    }else if(response.equals("0")){
                        fav_playlist.setImageResource(R.drawable.favourate);

                        setelement("0");
                        Toast.makeText(songsfromplaylist.this, "Disliked playlist "+homefragment.listofplaylistArrayList.get(playlist_position).getName(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onResponse: called negative ");

                    }else{
                        if(response!=null){
                            Toast.makeText(songsfromplaylist.this, "Error: "+response, Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(songsfromplaylist.this, "Unknown error occured ", Toast.LENGTH_SHORT).show();
                        }
                    }

                    shownewelements();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Log.d(TAG, "onErrorResponse: error is "+error.getMessage());
                    if(error.getMessage()!=null){
                        Toast.makeText(songsfromplaylist.this, "error is "+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(songsfromplaylist.this, "Unknown error occured ", Toast.LENGTH_SHORT).show();
                    }
                    if(likes.equals("1")){
                        fav_playlist.setImageResource(R.drawable.favourate_full);
                    }else{
                        fav_playlist.setImageResource(R.drawable.favourate);
                    }

                    String err="Error in likeordislike in songfromplaylist "+error.getMessage();
                    new internal_error_report(getApplicationContext(),err,MainActivity.sharedPreferences.getString(USERNAME,"")).execute();

                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();

                    params.put("like",likes);
                    params.put("playlist",homefragment.listofplaylistArrayList.get(playlist_position).getName());
                    if(!username.equals("")) {
                        params.put("username", username);
                    }

                    return params;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(songsfromplaylist.this);
            queue.add(request);

            return null;
        }

        private void coplyelements(){
            for(int i=0;i<homefragment.listofplaylistArrayList.size();i++){
                trackArrayList.add(new listofplaylist(homefragment.listofplaylistArrayList.get(i).getId(),homefragment.listofplaylistArrayList.get(i).getName(),homefragment.listofplaylistArrayList.get(i).getImage(),
                        homefragment.listofplaylistArrayList.get(i).getLikes(),homefragment.listofplaylistArrayList.get(i).getNote()));
            }
            Log.d(TAG, "coplyelements: elements copied ");
        }
        private void setelement(String likes){
            homefragment.listofplaylistArrayList.clear();
            for(int i=0;i<trackArrayList.size();i++){
                if(i!=playlist_position) {
                    homefragment.listofplaylistArrayList.add(new listofplaylist(trackArrayList.get(i).getId(), trackArrayList.get(i).getName(), trackArrayList.get(i).getImage(),
                            trackArrayList.get(i).getLikes(), trackArrayList.get(i).getNote()));
                }else if(i==playlist_position){
                    Log.d(TAG, "setelement: set the likes");
                    homefragment.listofplaylistArrayList.add(new listofplaylist(trackArrayList.get(i).getId(), trackArrayList.get(i).getName(), trackArrayList.get(i).getImage(),
                            likes, trackArrayList.get(i).getNote()));
                }
            }
            trackArrayList.clear();
        }

        private void shownewelements(){

            Log.d(TAG, "shownewelements: position is "+onclearfrompercentservice.position);

            for(int i=0;i<homefragment.listofplaylistArrayList.size();i++){
                Log.d(TAG, "shownewelements: id "+homefragment.listofplaylistArrayList.get(i).getId());
                Log.d(TAG, "shownewelements: name "+homefragment.listofplaylistArrayList.get(i).getName());
                Log.d(TAG, "shownewelements: image "+homefragment.listofplaylistArrayList.get(i).getImage());
                Log.d(TAG, "shownewelements: like "+homefragment.listofplaylistArrayList.get(i).getLikes());
                Log.d(TAG, "shownewelements: note "+homefragment.listofplaylistArrayList.get(i).getNote());
            }
        }
    }

    public class get_fav_songs extends AsyncTask<String,Void,Void>{

        int number=0;

        @Override
        protected void onPreExecute() {
            listViewforsongs.setVisibility(View.GONE);
            loading.setVisibility(View.VISIBLE);
            main.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(final String... strings) {

            String url = strings[0];
            final String username=sharedPreferences.getString(USERNAME,"");

            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    listofsongsArrayLisr.clear();
                    message="Done progress";

                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String success = jsonObject.getString("success");
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        if (success.equals("1")) {
                            for (int i =0 ; i <jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);

                                String id =object.getString("id");
                                String name = object.getString("name");
                                String songurl = object.getString("url");
                                String image = object.getString("image");
                                String like = object.getString("like");
                                String singer = object.getString("singer");
                                String bkcolor=object.getString("bkcolor");

                                listofsongs = new listofsongs(id,name,songurl,image,like,singer,bkcolor);
                                listofsongsArrayLisr.add(listofsongs);
                                songadapter.notifyDataSetChanged();

                            }
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        message="failed ";

                        Log.d(TAG, "onResponse: error in json is "+e.getMessage());
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error.getMessage()!=null){
                        Toast.makeText(songsfromplaylist.this, "error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(songsfromplaylist.this, "unknown error occured ", Toast.LENGTH_SHORT).show();
                    }

                    String err="Error in get_fav_song in songsfromplaylist "+error.getMessage();
                    new internal_error_report(getApplicationContext(),err,MainActivity.sharedPreferences.getString(USERNAME,"")).execute();

                    message="error occured ";
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String ,String>();
                    params.put("position",playlistfragment.listofplaylistArrayList_for_fav.get(POSITION_FAV_PLAYLIST).getId());

                    if(!username.equals("")){
                        params.put("username",username);
                    }

                    return params;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(songsfromplaylist.this);
            queue.add(request);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            final Handler handler = new Handler();
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {

                    if(!message.equals("") && !message.isEmpty()){

                        loading.setVisibility(View.GONE);
                        main.setVisibility(View.VISIBLE);
                        listViewforsongs.setVisibility(View.VISIBLE);
                        listViewforsongs.startAnimation(frombottom);
                        topheader.startAnimation(fromtop);
                        if(!message.equalsIgnoreCase("Done progress")) {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            listofsongsArrayLisr.clear();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }
                    }else{
                        Log.d(TAG, "run: running for : "+number++);
                        handler.postDelayed(this,1000);
                    }
                }
            };
            runnable.run();
        }
    }

    public class get_songs extends AsyncTask<String ,Void,Void> {
        String message="";
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(songsfromplaylist.this);
            progressDialog.setMessage("Getting Your Song ...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
            super.onPreExecute();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {

            String url=strings[0];
            final String song_url=strings[1];
            Log.d(TAG, "doInBackground: url is "+url);

            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    listofsongsArrayLisr.clear();
                    message="Done progress";
                    progressDialog.dismiss();
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String success = jsonObject.getString("success");
                        String playlist_name=jsonObject.getString("name");
                        String playlistid = jsonObject.getString("playlistid");
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        if (success.equals("1")) {
                            for (int i =0 ; i <jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);

                                String id = object.getString("id");
                                String name = object.getString("name");
                                String image = object.getString("image");
                                String url = object.getString("url");
                                String like = object.getString("like");
                                String singer = object.getString("singer");
                                String bkcolor = object.getString("bkcolor");//background colour when the song is playing;

                                listofsongs listofsong = new listofsongs(id,name,url,image,like,singer,bkcolor);
                                listofsongsArrayLisr.add(listofsong);
                            }

                            startActivity(new Intent(songsfromplaylist.this,playselectedsong.class)
                                    .putExtra("position",0)
                                    .putExtra("playlistname",playlist_name)
                                    .putExtra("is_from_search",true)
                            .putExtra("playlistid",playlistid));

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        message="failed ";
                        //new erroinfetch().execute(e.getMessage());
                        Log.d(TAG, "onResponse: error in json is "+e.getMessage());
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    message="error in fetching playlist";
                    progressDialog.dismiss();
                    if(error.getMessage()!=null){

                        Toast.makeText(songsfromplaylist.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }else{

                        Toast.makeText(songsfromplaylist.this, "Error occured!!", Toast.LENGTH_LONG).show();
                    }

                    String err="Error in getdata in homefragment "+error.getMessage();
                    new internal_error_report(songsfromplaylist.this,err,MainActivity.sharedPreferences.getString(USERNAME,"")).execute();

                    startActivity(new Intent(context,MainActivity.class));

                }
            }){

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String,String> params = new HashMap<String, String>();

                    params.put("url",song_url);
                    params.put("username",sharedPreferences.getString(USERNAME,""));

                    return params;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(songsfromplaylist.this);
            queue.add(request);

            return null;
        }
    }
}
