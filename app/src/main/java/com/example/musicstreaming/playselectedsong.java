package com.example.musicstreaming;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.bumptech.glide.request.target.Target;
import com.example.musicstreaming.service.onclearfrompercentservice;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.musicstreaming.MainActivity.setsongprogress;
import static com.example.musicstreaming.login.SHARED_PREF;
import static com.example.musicstreaming.login.USERNAME;
import static com.example.musicstreaming.playselectedsong.updateseekdetail.check;
import static com.example.musicstreaming.service.onclearfrompercentservice.IS_RUNNING_SERVICE;
import static com.example.musicstreaming.service.onclearfrompercentservice.SERVICE_CONTEXT;
import static com.example.musicstreaming.service.onclearfrompercentservice.broadcastReceiver;
import static com.example.musicstreaming.service.onclearfrompercentservice.context;
import static com.example.musicstreaming.service.onclearfrompercentservice.isplaying;
import static com.example.musicstreaming.service.onclearfrompercentservice.isprepared;
import static com.example.musicstreaming.service.onclearfrompercentservice.ispreparing;
import static com.example.musicstreaming.service.onclearfrompercentservice.lastplaylist_id;
import static com.example.musicstreaming.service.onclearfrompercentservice.loopmusic;
import static com.example.musicstreaming.service.onclearfrompercentservice.mediaPlayer;
import static com.example.musicstreaming.service.onclearfrompercentservice.mediaplayerloadingprogress;
import static com.example.musicstreaming.service.onclearfrompercentservice.millisecond;
import static com.example.musicstreaming.service.onclearfrompercentservice.ontracknext;
import static com.example.musicstreaming.service.onclearfrompercentservice.ontrackpause;
import static com.example.musicstreaming.service.onclearfrompercentservice.ontrackplay;
import static com.example.musicstreaming.service.onclearfrompercentservice.ontrackprevious;
import static com.example.musicstreaming.service.onclearfrompercentservice.preparesong;
import static com.example.musicstreaming.service.onclearfrompercentservice.unpluged_headset;
import static com.example.musicstreaming.songsfromplaylist.isfav;
import static com.example.musicstreaming.songsfromplaylist.listofsongsArrayLisr;
import static com.example.musicstreaming.songsfromplaylist.playlistnames;
import static com.example.musicstreaming.songsfromplaylist.setprogressforsong;
import static com.example.musicstreaming.songsfromplaylist.showdetail;
import static com.example.musicstreaming.splash.DIR_NAME;

public class playselectedsong extends AppCompatActivity{
    /**
     * <h1>Ownership</h1>
     * <p>This Application is Owned by Ashish Kumar, Delhi Technology univerity, Btech ,Mechanical
     * Engineering ,2ndyear Student</p>
     * <p>Finished First version 1.0 on 17-Aug-2020</p>
     */

    public static ImageView playsong,play_next,play_previous,song_image,loop,like;
    public static NotificationManager notificationManager;
    public static List<track> tracks;
    int position=0,PLAYLIST_POS;
    public static TextView songname,strt_duration,total_duration;
    public static SeekBar seekBar;
    String TAG="playselectedsong";
    public static Handler updateseek=new Handler();
    public static Runnable updaterunnable;
    public static Context context1;
    public static boolean isplaylistcomplete=false, is_from_search=false;
    public static String playlistname,playlistid,playlistimg;
    public static int dontusethis;
    public static LinearLayout backgroung_for_music;
    public SharedPreferences sharedPreferences;
    public static Activity SONG_ACTIVITY;
    ImageView down;
    public static TextView playlistnameontop,singername;
    public static String CURRENT_PLAYLIST_NAME="currentplaylistname";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playselectedsong);

        SONG_ACTIVITY=this;

        Thread.setDefaultUncaughtExceptionHandler(new Exceptionhandler(SONG_ACTIVITY));

        playsong = findViewById(R.id.playsong);
        play_next = findViewById(R.id.play_next);
        play_previous = findViewById(R.id.play_previous);
        song_image = findViewById(R.id.songimage);
        songname= findViewById(R.id.songname);
        strt_duration=findViewById(R.id.strtduration);
        total_duration=findViewById(R.id.totalduration);
        seekBar=findViewById(R.id.songseekbar);
        backgroung_for_music=findViewById(R.id.backgroung_for_music);
        loop=findViewById(R.id.play_loop);
        like=findViewById(R.id.like);
        down=findViewById(R.id.down);
        playlistnameontop=findViewById(R.id.playlistname);
        singername=findViewById(R.id.singername);
        seekBar.setMax(100);
        context1=this;

        sharedPreferences=getSharedPreferences(SHARED_PREF,MODE_PRIVATE);
        isplaylistcomplete=false;

        new make_file_in_directory(this,this,sharedPreferences.getString(USERNAME,"")).write_version_file(new File(Environment.getExternalStorageDirectory()+"/"+DIR_NAME,"versions"));

        Intent intent = getIntent();
        position=intent.getExtras().getInt("position",0);
        dontusethis=position;

        if(position!=1000) {
            playlistid = intent.getExtras().getString("playlistid","");
            playlistname = intent.getExtras().getString("playlistname","");
            playlistimg = intent.getExtras().getString("playlist_img_url","");
            PLAYLIST_POS=intent.getExtras().getInt("playlist_pos",0);
            is_from_search = intent.getBooleanExtra("is_from_search",false);

            Log.d("checkingplaylistid", "onCreate: this is playlist id "+playlistid);

            if(position!=1001) {

                SharedPreferences.Editor editor = sharedPreferences.edit();
                if(!is_from_search) {
                    editor.putString(CURRENT_PLAYLIST_NAME, playlistnames);

                    playlistnameontop.setText(playlistnames);

                }else{
                    editor.putString(CURRENT_PLAYLIST_NAME, playlistname);
                }
                editor.apply();

                if (!lastplaylist_id.equals(playlistid)) {
                    preparelist();
                    lastplaylist_id = playlistid;
                    Log.d(TAG, "onCreate: preparing the list for playlist " + lastplaylist_id);
                }

                if (!isservicerunning(onclearfrompercentservice.class)) {
                    startService(new Intent(getBaseContext(), onclearfrompercentservice.class)
                            .putExtra("position", position));
                } else {
                    //preparesong(onclearfrompercentservice.position);
                    preparesong(position);
                }
            }
        }

        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listofsongsArrayLisr.clear();
                startActivity(new Intent(playselectedsong.this,MainActivity.class));
            }
        });

        loop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isLooping()) {
                    if(!loopmusic(false)){
                        Toast.makeText(playselectedsong.this, "Please wait to start song! ", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(playselectedsong.this, tracks.get(onclearfrompercentservice.position).getTitle()+" set to Normal ", Toast.LENGTH_SHORT).show();
                    }
                }else if (!mediaPlayer.isLooping()){
                    if(!loopmusic(true)){
                        Toast.makeText(playselectedsong.this, "Please Wait to start song! ", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(playselectedsong.this, tracks.get(onclearfrompercentservice.position).getTitle()+" set to Looping", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        final String likes=tracks.get(onclearfrompercentservice.position).getLike();
        if(likes.equals("1")){
            like.setImageResource(R.drawable.favourate_full);
        }else if(likes.equals("0")){
            like.setImageResource(R.drawable.favourate);
        }

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String likeses=tracks.get(onclearfrompercentservice.position).getLike();
                if(likeses.equals("1")){
                    like.setImageResource(R.drawable.favourate);
                }else if(likeses.equals("0")){
                    like.setImageResource(R.drawable.favourate_full);
                }

                new likeordislike(likeses).execute();

            }
        });

        String playlistnme=sharedPreferences.getString(CURRENT_PLAYLIST_NAME,"");
        playlistnameontop.setText(playlistnme);

        check();
        play_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strt_duration.setText("0:00");
                seekBar.setProgress(0);
                total_duration.setText("0:00");
                seekBar.setSecondaryProgress(0);
                ontracknext();
            }
        });

        play_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strt_duration.setText("0:00");
                seekBar.setProgress(0);
                total_duration.setText("0:00");
                seekBar.setSecondaryProgress(0);
                ontrackprevious();
            }
        });

        playsong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(onclearfrompercentservice.isplaying){
                    ontrackpause();
                   playsong.setImageResource(R.drawable.play_white);
                }else if(isplaylistcomplete){
                    //ontrackplay(position);
                    if(position==1000 || is_from_search) {
                        startActivity(new Intent(playselectedsong.this,MainActivity.class));
                    }else {
                        startActivity(new Intent(playselectedsong.this, songsfromplaylist.class)
                                .putExtra("name", playlistname)
                                .putExtra("positions",PLAYLIST_POS)
                                .putExtra("id", playlistid).putExtra("imageurl", playlistimg)
                                .putExtra("isfav",isfav));
                    }


                    Toast.makeText(playselectedsong.this, "Playlist completed, Select another one", Toast.LENGTH_SHORT).show();
                }else{
                    ontrackplay(1000);
                    playsong.setImageResource(R.drawable.pause_white);
                }

            }
        });

        String songimage=tracks.get(onclearfrompercentservice.position).getImurl();
        loadimage(songimage);


        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){

            NotificationChannel channel = new NotificationChannel(onclearfrompercentservice.CHHANEL_ID,"this is a channel ",
                    NotificationManager.IMPORTANCE_LOW);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel.setLightColor(Color.GREEN);
            channel.enableLights(true);
            notificationManager = getSystemService(NotificationManager.class);
            if(notificationManager!=null){
                notificationManager.createNotificationChannel(channel);
            }

            if(position==1000 || position==1001){
                Log.d(TAG, "onCreate: this is called "+isplaying);
                if(isplaying){
                    playsong.setImageResource(R.drawable.pause_white);
                }else{
                    playsong.setImageResource(R.drawable.play_white);
                }
            }
            else
            {
;
                playsong.setImageResource(R.drawable.pause_white);
            }



            seekBar.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    SeekBar seekBar1 = (SeekBar)v;
                    int playposition=(mediaPlayer.getDuration()/100)*seekBar1.getProgress();
                    mediaPlayer.seekTo(playposition);
                    strt_duration.setText(millisecond(mediaPlayer.getCurrentPosition()));


                    return false;
                }
            });
        }else{
            if(position==1000 || position==1001){
                Log.d(TAG, "onCreate: this is called "+isplaying);
                if(isplaying){
                    playsong.setImageResource(R.drawable.pause_white);
                }else{
                    playsong.setImageResource(R.drawable.play_white);
                }
            }
            else
            {

                playsong.setImageResource(R.drawable.pause_white);
            }



            seekBar.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    SeekBar seekBar1 = (SeekBar)v;
                    int playposition=(mediaPlayer.getDuration()/100)*seekBar1.getProgress();
                    mediaPlayer.seekTo(playposition);
                    strt_duration.setText(millisecond(mediaPlayer.getCurrentPosition()));

                    return false;
                }
            });
        }

        makebackground(tracks.get(onclearfrompercentservice.position).getBkcolor());
        songname.setText(tracks.get(onclearfrompercentservice.position).getTitle());
        singername.setText(tracks.get(onclearfrompercentservice.position).getAlbum());

    }

    //TODO: need to look for the id of song on server side
    public class likeordislike extends AsyncTask<String,Void,Void>{

        String likes;
        ArrayList<track> trackArrayList=new ArrayList<>();

        public likeordislike(String likes) {
            this.likes = likes;
        }

        @Override
        protected Void doInBackground(String... strings) {

            final String username=sharedPreferences.getString(USERNAME,"");

            final String songnames=tracks.get(onclearfrompercentservice.position).getTitle();

            coplyelements();

            String url="https://rentdetails.000webhostapp.com/musicplayer_files/likesong.php";
            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {


                    if(response.equals("1")){
                        tracks.clear();
                        like.setImageResource(R.drawable.favourate_full);
                        Toast.makeText(playselectedsong.this, "Song "+songnames+" liked", Toast.LENGTH_SHORT).show();

                        setelement("1");

                    }else if(response.equals("0")){
                        tracks.clear();
                        like.setImageResource(R.drawable.favourate);
                        Toast.makeText(playselectedsong.this, "Song "+songnames+" disliked", Toast.LENGTH_SHORT).show();

                        setelement("0");

                    }else{
                        trackArrayList.clear();
                        if(response!=null){
                            Toast.makeText(playselectedsong.this, "Error: "+response, Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(playselectedsong.this, "Unknown error occured ", Toast.LENGTH_SHORT).show();
                        }
                        if(likes.equals("1")){
                            like.setImageResource(R.drawable.favourate_full);
                        }else{
                            like.setImageResource(R.drawable.favourate);
                        }
                    }
                    trackArrayList.clear();

                    //shownewelements();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Log.d(TAG, "onErrorResponse: error is "+error.getMessage());
                    if(error.getMessage()!=null){
                        Toast.makeText(context1, "error is "+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context1, "Unknown error occured ", Toast.LENGTH_SHORT).show();
                    }
                    if(likes.equals("1")){
                        like.setImageResource(R.drawable.favourate_full);
                    }else{
                        like.setImageResource(R.drawable.favourate);
                    }

                    String err="Error in likeordislike in playselected song "+error.getMessage();
                    new internal_error_report(context1,err,MainActivity.sharedPreferences.getString(USERNAME,"")).execute();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();

                    params.put("like",likes);
                    params.put("songname",songnames);
                    if(!username.equals("")) {
                        params.put("username", username);
                    }
                    params.put("playlist_id", playlistid);
                    params.put("song_id",tracks.get(onclearfrompercentservice.position).getId());

                    return params;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(context1);
            queue.add(request);

            return null;
        }

        private void coplyelements(){
            for(int i=0;i<tracks.size();i++){
                trackArrayList.add(new track(tracks.get(i).getId(),tracks.get(i).getTitle(),tracks.get(i).getAlbum(),tracks.get(i).getUrl(),
                        tracks.get(i).getImurl(),tracks.get(i).getBkcolor(),tracks.get(i).getLike()));
            }

        }
        private void setelement(String likes){
            for(int i=0;i<trackArrayList.size();i++){
                if(i!=onclearfrompercentservice.position) {
                    tracks.add(new track(trackArrayList.get(i).getId(),trackArrayList.get(i).getTitle(), trackArrayList.get(i).getAlbum(), trackArrayList.get(i).getUrl(),
                            trackArrayList.get(i).getImurl(), trackArrayList.get(i).getBkcolor(), trackArrayList.get(i).getLike()));
                }else if(i==onclearfrompercentservice.position){
                    tracks.add(new track(trackArrayList.get(i).getId(),trackArrayList.get(i).getTitle(), trackArrayList.get(i).getAlbum(), trackArrayList.get(i).getUrl(),
                            trackArrayList.get(i).getImurl(), trackArrayList.get(i).getBkcolor(),likes));
                }
            }
            trackArrayList.clear();
        }

        private void shownewelements(){

            Log.d(TAG, "shownewelements: position is "+onclearfrompercentservice.position);

            for(int i=0;i<tracks.size();i++){
                Log.d(TAG, "shownewelements: title "+tracks.get(i).getTitle());
                Log.d(TAG, "shownewelements: singer "+tracks.get(i).getAlbum());
                Log.d(TAG, "shownewelements: url "+tracks.get(i).getUrl());
                Log.d(TAG, "shownewelements: image "+tracks.get(i).getImurl());
                Log.d(TAG, "shownewelements: like "+tracks.get(i).getLike());
            }
        }
    }

    public static void preparelist(){
        tracks = new ArrayList<>();
        for(int i=0;i<songsfromplaylist.listofsongsArrayLisr.size();i++){
            final  String id= listofsongsArrayLisr.get(i).getId();
            final String name=songsfromplaylist.listofsongsArrayLisr.get(i).getName();
            final String singer = songsfromplaylist.listofsongsArrayLisr.get(i).getSinger();
            final String image=songsfromplaylist.listofsongsArrayLisr.get(i).getImage();
            final String url = songsfromplaylist.listofsongsArrayLisr.get(i).getUrl();
            final String like = songsfromplaylist.listofsongsArrayLisr.get(i).getLikes();
            final String bkcolor=listofsongsArrayLisr.get(i).bkcolor;

            tracks.add(new track(id,name,singer,url,image,bkcolor,like));
        }

        Log.d(onclearfrompercentservice.TAG, "preparelist: completed the loading to track ");

    }

    public  void  loadimage(String urles){

        final CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(playselectedsong.this);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(40f);
        circularProgressDrawable.start();

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(circularProgressDrawable);
        requestOptions.skipMemoryCache(true);
        requestOptions.priority(Priority.HIGH);
        requestOptions.fitCenter();

        Glide.with(playselectedsong.this)
                .load(urles)
                .apply(requestOptions)
                .transform( new RoundedCorners(20))
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.music_2)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        assert e != null;

                        String err="Error in loadimage in playselected song "+e.getMessage();
                        new internal_error_report(context1,err,MainActivity.sharedPreferences.getString(USERNAME,"")).execute();
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
                .into(song_image);
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

    public static void makebackground(String color){

        int[] colors = new int[2];
        colors[0] = Color.parseColor(color);
        colors[1] = Color.parseColor("#000000");

        GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
        gd.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gd.setGradientRadius(300f);
        gd.setCornerRadius(0f);
        backgroung_for_music.setBackground(gd);

    }

    public static class updateseekdetail extends AsyncTask<Void,Long,Void> {

        String totaltime,currenttime;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            strt_duration.setText("0:00");
            total_duration.setText(millisecond(mediaPlayer.getDuration()));
            seekBar.setProgress(0);
            seekBar.setSecondaryProgress(0);
            totaltime=millisecond(mediaPlayer.getDuration());
            final onclearfrompercentservice service=new onclearfrompercentservice();

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                    strt_duration.setText("0:00");
                    total_duration.setText("0:00");
                    seekBar.setProgress(0);
                    seekBar.setSecondaryProgress(0);

                    if(onclearfrompercentservice.position<(tracks.size()-1)) {
                        if(!mediaPlayer.isLooping()) {
                            ontracknext();
                        }else{
                            Log.d(onclearfrompercentservice.TAG, "onCompletion: it is set to looping ");
                        }

                    }else{
                        isplaylistcomplete=true;
                        isplaying=false;
                        ispreparing=false;
                        isprepared=false;

                        IS_RUNNING_SERVICE=false;
                        onclearfrompercentservice.position=0;

                        SERVICE_CONTEXT.unregisterReceiver(broadcastReceiver);

                        SERVICE_CONTEXT.unregisterReceiver(unpluged_headset);
                        Intent stopforground=new Intent(context1,onclearfrompercentservice.class);
                        SERVICE_CONTEXT.stopService(stopforground);
                        //service.stopForeground(true);
                        playsong.setImageResource(R.drawable.play_white);
                        new updateseekdetail().cancel(true);
                        Toast.makeText(context, "Playlist ended", Toast.LENGTH_SHORT).show();
                        tracks.clear();
                        showdetail(false);
                        MainActivity.showdetail(false);
                        if(dontusethis==1000 || is_from_search){
                            context1.startActivity(new Intent(context1,MainActivity.class));
                        }else {
                            context1.startActivity(new Intent(context1, songsfromplaylist.class)
                                    .putExtra("name", playlistname)
                                    .putExtra("id", playlistid).putExtra("imageurl", playlistimg));
                        }
                    }

                }
            });

        }

        @Override
        protected Void doInBackground(Void... voids) {


                updaterunnable = new Runnable() {
                    @Override
                    public void run() {
                        if(onclearfrompercentservice.isplaying && isprepared) {

                            long duration = mediaPlayer.getCurrentPosition();
                            publishProgress(duration);
                            Log.d(onclearfrompercentservice.TAG, "run: entered into the loop ");
                            updateseek.postDelayed(updaterunnable,1000);
                        }else{
                            updateseek.postDelayed(updaterunnable,1000);
                            Log.d(onclearfrompercentservice.TAG, "run: not entering into the loop ");
                        }
                    }
                };
                updaterunnable.run();

            return null;
        }

        @Override
        protected void onProgressUpdate(Long... duration) {
            super.onProgressUpdate(duration);
            isplaying=true;
            totaltime=millisecond(mediaPlayer.getDuration());
            int progress=(int)(((float)mediaPlayer.getCurrentPosition()/mediaPlayer.getDuration())*100);
            strt_duration.setText(millisecond(duration[0]));
            currenttime=millisecond(duration[0]);
            mediaplayerloadingprogress();
            total_duration.setText(totaltime);
            seekBar.setProgress(progress);
            setsongprogress(progress);
            setprogressforsong(progress);
        }

        public void updatesek(){

            updateseek.postDelayed(updaterunnable,1000);
            Log.d(onclearfrompercentservice.TAG, "updatesek: wea re makin this work in here ");
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            updateseek.removeCallbacks(updaterunnable);
            updateseek.removeCallbacksAndMessages(null);
            Log.d(onclearfrompercentservice.TAG, "run: removed few calbacks");
        }

        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);

            updateseek.removeCallbacks(updaterunnable);
            updateseek.removeCallbacksAndMessages(null);
            Log.d(onclearfrompercentservice.TAG, "run: removed few calbacks");

        }

        public static void check(){
            int size=tracks.size();
            Log.d(onclearfrompercentservice.TAG, "check: your position is original "+onclearfrompercentservice.position);
            Log.d(onclearfrompercentservice.TAG, "check: your track size is "+size);
            if(onclearfrompercentservice.position==0){
                hideprevious(true);
            }else{
                hideprevious(false);
            }

            if(size-1<=onclearfrompercentservice.position){
                hidenext(true);
            }else{
                hidenext(false);
            }
        }

        public static void hideprevious(boolean hide){

            if(hide){
                play_previous.setVisibility(View.INVISIBLE);
            }else{
                play_previous.setVisibility(View.VISIBLE);
            }

        }
        public static void  hidenext(boolean hide){

            if(hide){
                play_next.setVisibility(View.INVISIBLE);
            }else{
                play_next.setVisibility(View.VISIBLE);
            }

        }

        public static void back_to_playlist(){
            try {
                if(is_from_search){
                    context1.startActivity(new Intent(context, MainActivity.class));
                }else {
                    context1.startActivity(new Intent(context, songsfromplaylist.class)
                            .putExtra("name", playlistname)
                            .putExtra("id", playlistid).putExtra("imageurl", playlistimg));
                }
            }catch (Exception e){
                Toast.makeText(context1, "Error: "+e.getMessage(), Toast.LENGTH_LONG).show();
                String err="Error in service: "+e.getMessage();
                new internal_error_report(context1,err,MainActivity.sharedPreferences.getString(USERNAME,""));
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(position==1000 || is_from_search){
            startActivity(new Intent(playselectedsong.this,MainActivity.class));
        }else if(isfav){
            startActivity(new Intent(context1, songsfromplaylist.class)
                    .putExtra("name", playlistname)
                    .putExtra("id", playlistid).putExtra("imageurl", playlistimg)
                    .putExtra("isfav",isfav)
            .putExtra("positions",PLAYLIST_POS));
        }else {
            startActivity(new Intent(context1, songsfromplaylist.class)
                    .putExtra("name", playlistname)
                    .putExtra("id", playlistid).putExtra("imageurl", playlistimg)
            .putExtra("isfav",isfav)
            .putExtra("positions",PLAYLIST_POS));
        }
    }
}
