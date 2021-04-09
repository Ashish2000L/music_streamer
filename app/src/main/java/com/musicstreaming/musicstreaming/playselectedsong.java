package com.musicstreaming.musicstreaming;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
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
import com.bumptech.glide.request.target.Target;
import com.musicstreaming.musicstreaming.service.onclearfrompercentservice;
import com.musicstreaming.musicstreaming.service.online_status_updater;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.musicstreaming.musicstreaming.MainActivity.setsongprogress;
import static com.musicstreaming.musicstreaming.login.SHARED_PREF;
import static com.musicstreaming.musicstreaming.login.USERNAME;
import static com.musicstreaming.musicstreaming.playselectedsong.updateseekdetail.check;
import static com.musicstreaming.musicstreaming.service.onclearfrompercentservice.IS_RUNNING_SERVICE;
import static com.musicstreaming.musicstreaming.service.onclearfrompercentservice.SERVICE_CONTEXT;
import static com.musicstreaming.musicstreaming.service.onclearfrompercentservice.broadcastReceiver;
import static com.musicstreaming.musicstreaming.service.onclearfrompercentservice.context;
import static com.musicstreaming.musicstreaming.service.onclearfrompercentservice.isplaying;
import static com.musicstreaming.musicstreaming.service.onclearfrompercentservice.isprepared;
import static com.musicstreaming.musicstreaming.service.onclearfrompercentservice.ispreparing;
import static com.musicstreaming.musicstreaming.service.onclearfrompercentservice.lastplaylist_id;
import static com.musicstreaming.musicstreaming.service.onclearfrompercentservice.loopmusic;
import static com.musicstreaming.musicstreaming.service.onclearfrompercentservice.mediaPlayer;
import static com.musicstreaming.musicstreaming.service.onclearfrompercentservice.mediaplayerloadingprogress;
import static com.musicstreaming.musicstreaming.service.onclearfrompercentservice.millisecond;
import static com.musicstreaming.musicstreaming.service.onclearfrompercentservice.ontracknext;
import static com.musicstreaming.musicstreaming.service.onclearfrompercentservice.ontrackpause;
import static com.musicstreaming.musicstreaming.service.onclearfrompercentservice.ontrackplay;
import static com.musicstreaming.musicstreaming.service.onclearfrompercentservice.ontrackprevious;
import static com.musicstreaming.musicstreaming.service.onclearfrompercentservice.preparesong;
import static com.musicstreaming.musicstreaming.service.onclearfrompercentservice.unpluged_headset;
import static com.musicstreaming.musicstreaming.service.online_status_updater.allFriends;
import static com.musicstreaming.musicstreaming.service.online_status_updater.frd_usernames;
import static com.musicstreaming.musicstreaming.songsfromplaylist.isfav;
import static com.musicstreaming.musicstreaming.songsfromplaylist.listofsongsArrayLisr;
import static com.musicstreaming.musicstreaming.songsfromplaylist.playlistnames;
import static com.musicstreaming.musicstreaming.songsfromplaylist.setprogressforsong;
import static com.musicstreaming.musicstreaming.songsfromplaylist.showdetail;
import static com.musicstreaming.musicstreaming.splash.DIR_NAME;

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
    public static TextView songname,strt_duration,total_duration,playlistnameontop,singername;
    public static SeekBar seekBar;
    public static Handler updateseek=new Handler();
    public static Runnable updaterunnable;
    public static Context context1;
    public static boolean isplaylistcomplete=false, is_from_search=false;
    public static String playlistname,playlistid,playlistimg,CURRENT_PLAYLIST_NAME="currentplaylistname";
    public static int dontusethis;
    public static MotionLayout backgroung_for_music;
    public static SharedPreferences sharedPreferences;
    public static Activity SONG_ACTIVITY;

    int position=0,PLAYLIST_POS;
    ImageView down,menu_image;
    String TAG="playselectedsong";
    ConstraintLayout blur,menu_items,frd_container;
    TextView share,report,back_to_list;
    ListView frd_list;
    Context SONG_CONTEXT;
    LottieAnimationView lottieAnimationView;
    Animation animation1;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playselectedsong);

        SONG_ACTIVITY=this;
        SONG_CONTEXT=this;

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
        blur=findViewById(R.id.blur);
        menu_items=findViewById(R.id.menu_items);
        frd_container=findViewById(R.id.frd_container);
        menu_image=findViewById(R.id.menu_image);
        share=findViewById(R.id.share_song);
        report=findViewById(R.id.report);
        frd_list=findViewById(R.id.list_of_friends);
        back_to_list=findViewById(R.id.back_to_list);
        lottieAnimationView=findViewById(R.id.anim_parcel_pack);
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

                new make_file_in_directory(playselectedsong.this,getApplicationContext(),sharedPreferences.getString(splash.USERNAME,"")).write_track_file(new File(Environment.getExternalStorageDirectory()+"/"+DIR_NAME,"track"),tracks);

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

        //managing frd list in the custom menu
        ArrayAdapter<String> all_friends = new ArrayAdapter<String>(playselectedsong.this,android.R.layout.simple_list_item_1,allFriends){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view=super.getView(position, convertView, parent);

                TextView textView = view.findViewById(android.R.id.text1);
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(22);

                return view;
            }
        };
        frd_list.setAdapter(all_friends);
        all_friends.notifyDataSetChanged();

        //testing for the checking transition listner of motion layout
        backgroung_for_music.setTransitionListener(new MotionLayout.TransitionListener() {

            int val1,val2;

            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int i, int i1) {
                val1=i;
                val2=i1;

                if(motionLayout.getProgress()>0.85){
                    Log.d(TAG, "onTransitionStarted: it is moving down");
                }else{
                    blur.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onTransitionStarted: it is moving up");
                }
                
//                Log.d(TAG, "onTransitionStarted: --------------------------motion started be aware---------------------------i="+i+" i1= "+i1+" get_prog "+motionLayout.getProgress());
            }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int i, int i1, float v) {

//                Log.d(TAG, "onTransitionChange: --------------------------motion state changing be aware---------------------------i="+i+" i1= "+i1+" v= "+v);

            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int i) {

//                Log.d(TAG, "onTransitionCompleted: --------------------------motion completed be aware---------------------------i="+i);

                if(i==val1){
                    Log.d(TAG, "onTransitionCompleted: moving down");
                    blur.setVisibility(View.GONE);
                }else
                    if(i==val2){
                        Log.d(TAG, "onTransitionCompleted: moving up");
                }

            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int i, boolean b, float v) {

//                Log.d(TAG, "onTransitionTrigger: --------------------------motion trigerred be aware---------------------------i="+i+" b= "+b+" v= "+v);

            }
        });

        //managing custom menu view
        menu_items.setVisibility(View.VISIBLE);
        frd_container.setVisibility(View.GONE);

        //onclick handlers

        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listofsongsArrayLisr.clear();
                startActivity(new Intent(playselectedsong.this,MainActivity.class));
            }
        });

        blur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu_image.performClick();
            }
        });
        
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu_image.performClick();
                Toast.makeText(playselectedsong.this, "We are working on it, will be available in next update!", Toast.LENGTH_SHORT).show();
            }
        });

        menu_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu_items.setVisibility(View.VISIBLE);
                frd_container.setVisibility(View.GONE);
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                menu_items.setVisibility(View.GONE);
                frd_container.setVisibility(View.VISIBLE);
            }
        });

        back_to_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu_items.setVisibility(View.VISIBLE);
                frd_container.setVisibility(View.GONE);
            }
        });


        frd_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {

                String frd_user = frd_usernames.get(pos), mainUsername = sharedPreferences.getString(USERNAME,""),
                songId=tracks.get(onclearfrompercentservice.position).getId(),playlistId=playlistid,
                url="https://rentdetails.000webhostapp.com/musicplayer_files/friends_folder/send_song_to_frd.php";

                menu_image.performClick();

                new shareSongToFriend(url,mainUsername,frd_user,songId,playlistId,pos).execute();
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

        if(tracks.size()==0){
            new make_file_in_directory(playselectedsong.this,getApplicationContext(),sharedPreferences.getString(splash.USERNAME,"")).read_track_file(new File(Environment.getExternalStorageDirectory()+"/"+DIR_NAME,"track"));
        }

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

            NotificationChannel channel = new NotificationChannel(onclearfrompercentservice.CHHANEL_ID,"Music Notification",
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

    public class  shareSongToFriend extends AsyncTask<Void,Void,Void>{

        String mainUsername, frdUsername,songId,playlistId,url;
        int pos;
        public shareSongToFriend(String url,String mainUsername, String frdUsername, String songId, String playlistId,int pos) {
            this.frdUsername=frdUsername;
            this.mainUsername = mainUsername;
            this.songId=songId;
            this.playlistId=playlistId;
            this.url=url;
            this.pos=pos;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Toast.makeText(playselectedsong.this, "Preparing your package...", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    if(!response.equals("successful")){

                        Toast.makeText(playselectedsong.this, response, Toast.LENGTH_LONG).show();

                    }else{
                        Toast.makeText(playselectedsong.this, "Song "+tracks.get(onclearfrompercentservice.position).getTitle()+ " Successfully Shared with "+allFriends.get(pos), Toast.LENGTH_LONG).show();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String err="Error in getdata in playlistfragment "+error.getMessage();
                    new internal_error_report(playselectedsong.this,err, sharedPreferences.getString(login.USERNAME,"")).execute();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    HashMap<String,String> params = new HashMap<String, String>();

                    params.put("frdUsername",frdUsername);
                    params.put("mainUsername",mainUsername);
                    params.put("songId",songId);
                    params.put("playlistId",playlistId);

                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(playselectedsong.this);
            requestQueue.add(stringRequest);

            return null;
        }
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
                        online_status_updater.CURRENT_PLAYING_SONG="";
                        if(dontusethis==1000 || is_from_search){
                            context1.startActivity(new Intent(context1,MainActivity.class));
                        }else {
                            context1.startActivity(new Intent(context1, songsfromplaylist.class)
                                    .putExtra("name", playlistname)
                                    .putExtra("id", playlistid).putExtra("imageurl", playlistimg).putExtra("is_custom",splash.sharedPreferences.getBoolean("is_custom",false)));
                        }

                        File track = new File(Environment.getExternalStorageDirectory()+"/"+DIR_NAME,"track");
                        if(track.exists()){
                            track.delete();
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
                            online_status_updater.CURRENT_PLAYING_SONG=sharedPreferences.getString(CURRENT_PLAYLIST_NAME,"");
                            Log.d(onclearfrompercentservice.TAG, "updatesek: wea re makin this work in here "+online_status_updater.CURRENT_PLAYING_SONG);
                            updateseek.postDelayed(updaterunnable,1000);
                        }else{
                            updateseek.postDelayed(updaterunnable,1000);

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
//            online_status_updater.CURRENT_PLAYING_SONG=tracks.get(onclearfrompercentservice.position).getTitle();
//            online_status_updater.CURRENT_PLAYING_SONG=sharedPreferences.getString(CURRENT_PLAYLIST_NAME,"");
//            Log.d(onclearfrompercentservice.TAG, "updatesek: wea re makin this work in here "+sharedPreferences.getString(CURRENT_PLAYLIST_NAME,""));
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
            .putExtra("positions",PLAYLIST_POS)
            .putExtra("is_custom",sharedPreferences.getBoolean("is_custom",false)));
        }else {
            startActivity(new Intent(context1, songsfromplaylist.class)
                    .putExtra("name", playlistname)
                    .putExtra("id", playlistid).putExtra("imageurl", playlistimg)
            .putExtra("isfav",isfav)
            .putExtra("positions",PLAYLIST_POS)
            .putExtra("is_custom",sharedPreferences.getBoolean("is_custom",false)));
        }
    }
}
