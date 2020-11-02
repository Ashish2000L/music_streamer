package com.example.musicstreaming.service;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.SyncStateContract;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.musicstreaming.Exceptionhandler;
import com.example.musicstreaming.MainActivity;
import com.example.musicstreaming.R;
import com.example.musicstreaming.internal_error_report;
import com.example.musicstreaming.playselectedsong;
import com.example.musicstreaming.songsfromplaylist;
import com.example.musicstreaming.track;
import com.google.android.gms.common.internal.Constants;

import java.io.IOException;
import java.util.Objects;
import java.util.Random;

import static com.example.musicstreaming.MainActivity.changeplaypauseimgs;
import static com.example.musicstreaming.login.SHARED_PREF;
import static com.example.musicstreaming.login.USERNAME;
import static com.example.musicstreaming.playselectedsong.SONG_ACTIVITY;
import static com.example.musicstreaming.playselectedsong.context1;
import static com.example.musicstreaming.playselectedsong.like;
import static com.example.musicstreaming.playselectedsong.loop;
import static com.example.musicstreaming.playselectedsong.makebackground;
import static com.example.musicstreaming.playselectedsong.notificationManager;
import static com.example.musicstreaming.playselectedsong.play_next;
import static com.example.musicstreaming.playselectedsong.play_previous;
import static com.example.musicstreaming.playselectedsong.playsong;
import static com.example.musicstreaming.playselectedsong.seekBar;
import static com.example.musicstreaming.playselectedsong.singername;
import static com.example.musicstreaming.playselectedsong.song_image;
import static com.example.musicstreaming.playselectedsong.songname;
import static com.example.musicstreaming.playselectedsong.strt_duration;
import static com.example.musicstreaming.playselectedsong.total_duration;
import static com.example.musicstreaming.playselectedsong.tracks;
import static com.example.musicstreaming.playselectedsong.updaterunnable;
import static com.example.musicstreaming.playselectedsong.updateseek;
import static com.example.musicstreaming.playselectedsong.updateseekdetail.check;
import static com.example.musicstreaming.songsfromplaylist.changeplaypauseimg;
import static com.example.musicstreaming.songsfromplaylist.showdetail;

public class onclearfrompercentservice extends Service implements AudioManager.OnAudioFocusChangeListener {
    /**
     * <h1>Ownership</h1>
     * <p>This Application is Owned by Ashish Kumar, Delhi Technology univerity, Btech ,Mechanical
     * Engineering ,2ndyear Student</p>
     * <p>Finished First version on 17-Aug-2020</p>
     */
    
    public static String TAG="serviceformusic";
    public static boolean isplaying=false;
    public static int position=0;
    public static boolean TERMINATION_STATUS=false;
    public static MediaPlayer mediaPlayer;
    public static boolean IS_RUNNING_SERVICE=false;
    public static boolean IS_PAUSED_SONG=false;
    public static boolean IS_PLAYING_SONG=false;
    public static Context SERVICE_CONTEXT;
    public static int POSITION_FAV_PLAYLIST=0;

    public static final String CHHANEL_ID="channel1";
    public static final String ACTION_PREVIOUS="actionprevious";
    public static final String ACTION_PLAY="actionplay";
    public static final String ACTION_NEXT="actionnext";

    public static boolean isprepared=false;
    public static Bitmap icon;
    public static Notification notification;
    public static Context context;
    public static boolean ispreparing=false;
    public static String lastplaylist_id="-1";
    public static int percentupdate;
    public static Random random = new Random();
    public static int randomvalue=0;
    AudioManager audioManager;
    public static SharedPreferences sharedPreferences;
    public static String IS_RECEIVER_REGISTERED="IS_RECEIVER_REGISTERED";
    public static Handler handler = new Handler();

    
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        lastplaylist_id="-1";
        Thread.setDefaultUncaughtExceptionHandler(new Exceptionhandler(SONG_ACTIVITY));
        context=getApplicationContext();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        position=intent.getIntExtra("position",0);

        IS_RUNNING_SERVICE=true;
        SERVICE_CONTEXT=this;

        Log.d(TAG, "onStartCommand: your music url is "+playselectedsong.tracks.get(position).getUrl());
        registerReceiver(broadcastReceiver,new IntentFilter("TRACKS_TRACKS"));
        Log.d(TAG, "onStartCommand: position from the activity is "+position);
        createnotification(context,playselectedsong.tracks.get(position),R.drawable.exo_controls_pause,position,playselectedsong.tracks.size()-1);
        audioManager= (AudioManager) getSystemService(AUDIO_SERVICE);
        audioManager.requestAudioFocus(this,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);

        registerReceiver(unpluged_headset,new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));
        
        preparesong(position);


        startForeground(1, notification);

        
        return START_NOT_STICKY;
    }


    public static BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("actionname");

            switch (action){
                case ACTION_PREVIOUS:

                        ontrackprevious();
                        MainActivity.showdetail(false);
                    break;

                case ACTION_PLAY:
                    if(isplaying){
                        ontrackpause();
                        playselectedsong.playsong.setImageResource(R.drawable.play_white);
                        changeplaypauseimgs(false);
                        changeplaypauseimg(false);
                        isplaying=false;
                        IS_PAUSED_SONG=true;
                        IS_PLAYING_SONG=false;
                    }else{
                        ontrackplay(1000);
                        changeplaypauseimgs(true);
                        changeplaypauseimg(true);
                        playselectedsong.playsong.setImageResource(R.drawable.pause_white);
                        IS_PLAYING_SONG=true;
                        IS_PAUSED_SONG=false;
                    }
                    break;

                case ACTION_NEXT:
                        ontracknext();
                        MainActivity.showdetail(false);

                    break;
            }
        }
    };

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            notificationManager.cancelAll();
        }
        unregisterReceiver(broadcastReceiver);
        unregisterReceiver(unpluged_headset);
        Log.d(TAG, "onDestroy: ondestroy called");
        new playselectedsong.updateseekdetail().cancel(true);
        stopForeground(true);
        isplaying=false;
        ispreparing=false;
        isprepared=false;
        IS_RUNNING_SERVICE=false;
        IS_PAUSED_SONG=false;
        IS_PLAYING_SONG=false;
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer=null;
        audioManager.abandonAudioFocus(this);
        stopSelf();
    }

    public static void preparesong(final int positions){

        position=positions;
        randomvalue=random.nextInt(10);
        makebackground(randomvalue);
        ispreparing=true;
        check();
        if(positions==0){
            play_previous.setVisibility(View.INVISIBLE);
        }else if(positions==tracks.size()-1){
            play_next.setVisibility(View.INVISIBLE);
        }else{
            play_previous.setVisibility(View.VISIBLE);
            play_next.setVisibility(View.VISIBLE);
        }

        String likes=tracks.get(positions).getLike();
        if(likes.equals("1")){
            like.setImageResource(R.drawable.favourate_full);
        }else if(likes.equals("0")){
            like.setImageResource(R.drawable.favourate);
        }

        if(new playselectedsong.updateseekdetail().getStatus()==AsyncTask.Status.RUNNING){
            new playselectedsong.updateseekdetail().cancel(true);

        }

        if(isplaying || isprepared){
            mediaPlayer.reset();

            Log.d(TAG, "preparesong: is playing set to false and reset the mediaplayer ");
            isplaying=false;
            isprepared=false;
            
        }
        mediaPlayer=new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            String imageurl=playselectedsong.tracks.get(positions).getImurl();
            loadimage(imageurl);
            songname.setText(tracks.get(positions).getTitle());
            singername.setText(tracks.get(positions).getAlbum());
            String url=playselectedsong.tracks.get(positions).getUrl();
            
            mediaPlayer.setDataSource(url);
            Log.d(TAG, "preparesong: url set in mediaplayer ");
            mediaPlayer.prepareAsync();
            Log.d(TAG, "preparesong: prepareasync is called ");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "preparesong: ioexception is "+e.getMessage());
        }catch (IllegalStateException ex){
            Log.d(TAG, "preparesong: illegalexception is "+ex.getMessage());
        }

        mediaplayerloadingprogress();

        createnotification(context,tracks.get(positions),R.drawable.exo_controls_pause,positions,tracks.size()-1);
        playselectedsong.playsong.setImageResource(R.drawable.pause_white);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.d(TAG, "onPrepared: song prepared wow");
                mp.start();
                songsfromplaylist.showdetail(true);
                ispreparing=false;
                MainActivity.changeplaypauseimgs(true);
                changeplaypauseimg(true);
                MainActivity.showdetail(true);
                new playselectedsong.updateseekdetail().execute();
                isprepared=true;
                isplaying=true;
                playsong.setImageResource(R.drawable.pause_white);
            }
        });

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {

                mediaPlayer.reset();
                isprepared=false;
                ispreparing=false;
                isplaying=false;
                songsfromplaylist.showdetail(false);
                MainActivity.showdetail(true);
                //handler.removeCallbacks(runnable);
                showdetail(false);
                MainActivity.showdetail(false);

                updateseek.removeCallbacks(updaterunnable);
                if(what==-38) {
                    preparesong(positions);
                    Log.d(TAG, "onError: worling again on it ");
                }else{
                    Toast.makeText(context, "error occured : "+what, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onError: error is "+what+":"+extra);
                }
                if(new playselectedsong.updateseekdetail().getStatus()==AsyncTask.Status.RUNNING){
                    new playselectedsong.updateseekdetail().cancel(true);
                }
                return false;
            }
        });
    }

    public static BroadcastReceiver unpluged_headset=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())){

                ontrackpause();
                playsong.setImageResource(R.drawable.play_white);
                mediaPlayer.pause();
                changeplaypauseimg(false);
                changeplaypauseimgs(false);
                isplaying=false;
            }
        }
    };

    public static void mediaplayerloadingprogress(){

        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                Log.d(TAG, "onBufferingUpdate: secondary progress is set ");
                seekBar.setSecondaryProgress(percent);
                percentupdate=percent;
            }
        });

    if(percentupdate==100){
        seekBar.setSecondaryProgress(percentupdate);
    }
    }

    public static void ontracknext() {
        
        position++;
        if(isplaying && isprepared){
            mediaPlayer.stop();
            Log.d(TAG, "ontracknext: mediaplayer stopped and reset");
            mediaPlayer.reset();
            if(new playselectedsong.updateseekdetail().getStatus()==AsyncTask.Status.RUNNING){
                //new playselectedsong.updateseekdetail().cancel(true);
                Log.d(TAG, "ontracknext: cancled the async task --------------------");
            }
            new playselectedsong.updateseekdetail().cancel(true);
            if(new playselectedsong.updateseekdetail().isCancelled()){
                Log.d(TAG, "ontracknext: canceled the task manually from remote");
            }
        }else{
            Toast.makeText(context, "Please wait!", Toast.LENGTH_SHORT).show();
        }
        isplaying=false;
        isprepared=false;
        songsfromplaylist.showdetail(false);
        MainActivity.showdetail(false);
        Log.d(TAG, "ontracknext: preparing to play next song");
        Log.d(TAG, "ontracknext: position is "+position);
        Log.d(TAG, "ontracknext: size is "+tracks.size());
        if(position<tracks.size()) {
            Log.d(TAG, "ontracknext: position is "+position);
            Log.d(TAG, "ontracknext: size is "+tracks.size());
            preparesong(position);
            createnotification(context,playselectedsong.tracks.get(position),R.drawable.exo_controls_pause,position,playselectedsong.tracks.size()-1);
        }else{
            Log.d(TAG, "ontracknext: playlist is over ");
            mediaPlayer.release();
        }
    }

    public static boolean loopmusic(boolean doloop){
        if(isprepared){
            mediaPlayer.setLooping(doloop);
            if(doloop) {
                loop.setImageResource(R.drawable.looping);
            }else{
                loop.setImageResource(R.drawable.loop_white);
            }
            return true;
        }
        loop.setImageResource(R.drawable.loop_white);
        return false;
    }

    public static void ontrackplay(int positions) {

        if(positions==1000){
            positions=position;
            new playselectedsong.updateseekdetail().updatesek();
           // new playselectedsong.updateseekdetail().updatesek();
        }else{
            preparesong(positions);
        }
        changeplaypauseimg(true);
        createnotification(context,playselectedsong.tracks.get(positions),R.drawable.exo_controls_pause,position,playselectedsong.tracks.size()-1);
        if(!isplaying && isprepared){
            mediaPlayer.start();
            Log.d(TAG, "ontrackplay: mediaplayer started again");
            isplaying=true;
        }else if(ispreparing){
            Toast.makeText(context, "Please wait!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "making else", Toast.LENGTH_SHORT).show();
        }
        songsfromplaylist.showdetail(true);

    }

    public static void ontrackpause() {
        
        createnotification(context,playselectedsong.tracks.get(position),R.drawable.exo_controls_play,position,playselectedsong.tracks.size()-1);
        ispreparing=false;
        changeplaypauseimg(false);
        //handler.removeCallbacks(runnable);
        updateseek.removeCallbacks(updaterunnable);
        if(isplaying && isprepared){
            mediaPlayer.pause();
            Log.d(TAG, "ontrackpause: mediaplayer paused");
            isplaying=false;
            songsfromplaylist.showdetail(true);
        }else{
            Toast.makeText(context, "Please wait!", Toast.LENGTH_SHORT).show();
        }
    }

    public static void ontrackprevious() {
        position--;
        if(isplaying && isprepared){
            mediaPlayer.stop();
            mediaPlayer.reset();

        }else{
            Toast.makeText(context, "Please wait!", Toast.LENGTH_SHORT).show();
        }
        isplaying=false;
        isprepared=false;
        if(new playselectedsong.updateseekdetail().getStatus()==AsyncTask.Status.RUNNING){
            //new playselectedsong.updateseekdetail().cancel(true);
            Log.d(TAG, "ontrackprevious: cancled the async task --------------------");
        }

        songsfromplaylist.showdetail(false);
        MainActivity.showdetail(false);
        new playselectedsong.updateseekdetail().cancel(true);
        if(new playselectedsong.updateseekdetail().isCancelled()){
            Log.d(TAG, "ontrackprevious: canceled the task manually from remote");
        }
        Log.d(TAG, "ontrackprevious: preparing to play previous song ");
        preparesong(position);
        
        createnotification(context,playselectedsong.tracks.get(position),R.drawable.exo_controls_pause,position,playselectedsong.tracks.size()-1);

    }

    public static void createnotification(final Context context, final track track, final int play, int pos, int size){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){

            final NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            final MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(context,"tag");

            final PendingIntent pendingIntentprevious;
            final int drw_previous;
            if(pos== 0){
                pendingIntentprevious=null;
                drw_previous=0;
            }else{
                Intent intentprevious = new Intent(context, NotificationActionService.class).setAction(ACTION_PREVIOUS);
                pendingIntentprevious=PendingIntent.getBroadcast(context,0,intentprevious,PendingIntent.FLAG_UPDATE_CURRENT);
                drw_previous=R.drawable.exo_controls_previous;
            }

            Intent intentplay = new Intent(context, NotificationActionService.class).setAction(ACTION_PLAY);
            final PendingIntent pendingIntentplay=PendingIntent.getBroadcast(context,0,intentplay,PendingIntent.FLAG_UPDATE_CURRENT);

            final PendingIntent pendingIntentnext;
            final int drw_next;
            if(pos==size){
                pendingIntentnext=null;
                drw_next=0;
            }else{
                Intent intentnext = new Intent(context, NotificationActionService.class).setAction(ACTION_NEXT);
                pendingIntentnext=PendingIntent.getBroadcast(context,0,intentnext,PendingIntent.FLAG_UPDATE_CURRENT);
                drw_next=R.drawable.exo_controls_next;
            }


            Log.d(TAG, "createnotification: your iamge bitmap is "+track.getBitmap());

            //BitmapFactory.decodeResource(context.getResources(),track.getImage());
            Glide.with(context)
                    .asBitmap()
                    .load(track.getImurl())
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            icon=resource;
                            Log.d(TAG, "onResourceReady: hey we called this after loading your image of the notificaion, its cool ha!!");
                            notification = new NotificationCompat.Builder(context,CHHANEL_ID)
                                    .setSmallIcon(R.drawable.music)
                                    .setContentTitle(track.getTitle())
                                    .setContentText(track.getAlbum())
                                    .setLargeIcon(icon)
                                    .setOnlyAlertOnce(true)
                                    .addAction(drw_previous,"Previous",pendingIntentprevious)
                                    .addAction(play,"Play",pendingIntentplay)
                                    .addAction(drw_next,"Next",pendingIntentnext)
                                    .setShowWhen(false)
                                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                                            .setShowActionsInCompactView(0,1,2)
                                            .setMediaSession(mediaSessionCompat.getSessionToken()))
                                    .setPriority(NotificationCompat.PRIORITY_LOW)
                                    .build();

                            Log.d(TAG, "onResourceReady: resourse is "+icon);
                            notificationManagerCompat.notify(1,notification);

                        }
                    });

            notification = new NotificationCompat.Builder(context,CHHANEL_ID)
                    .setSmallIcon(R.drawable.music)
                    .setContentTitle(track.getTitle())
                    .setContentText(track.getAlbum())
                    .setLargeIcon(icon)
                    .setOnlyAlertOnce(true)
                    .addAction(drw_previous,"Previous",pendingIntentprevious)
                    .addAction(play,"Play",pendingIntentplay)
                    .addAction(drw_next,"Next",pendingIntentnext)
                    .setShowWhen(false)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(0,1,2)
                            .setMediaSession(mediaSessionCompat.getSessionToken()))
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .build();

            Log.d(TAG, "onResourceReady: resourse is "+icon);
            notificationManagerCompat.notify(1,notification);

        }else{
            final NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            final MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(context,"tag");

            final PendingIntent pendingIntentprevious;
            final int drw_previous;
            if(pos== 0){
                pendingIntentprevious=null;
                drw_previous=0;
            }else{
                Intent intentprevious = new Intent(context, NotificationActionService.class).setAction(ACTION_PREVIOUS);
                pendingIntentprevious=PendingIntent.getBroadcast(context,0,intentprevious,PendingIntent.FLAG_UPDATE_CURRENT);
                drw_previous=R.drawable.exo_controls_previous;
            }

            Intent intentplay = new Intent(context, NotificationActionService.class).setAction(ACTION_PLAY);
            final PendingIntent pendingIntentplay=PendingIntent.getBroadcast(context,0,intentplay,PendingIntent.FLAG_UPDATE_CURRENT);

            final PendingIntent pendingIntentnext;
            final int drw_next;
            if(pos==size){
                pendingIntentnext=null;
                drw_next=0;
            }else{
                Intent intentnext = new Intent(context, NotificationActionService.class).setAction(ACTION_NEXT);
                pendingIntentnext=PendingIntent.getBroadcast(context,0,intentnext,PendingIntent.FLAG_UPDATE_CURRENT);
                drw_next=R.drawable.exo_controls_next;
            }


            Log.d(TAG, "createnotification: your iamge bitmap is "+track.getBitmap());

            //BitmapFactory.decodeResource(context.getResources(),track.getImage());
            Glide.with(context)
                    .asBitmap()
                    .load(track.getImurl())
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            icon=resource;
                            Log.d(TAG, "onResourceReady: hey we called this after loading your image of the notificaion, its cool ha!!");
                            notification = new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.drawable.music)
                                    .setContentTitle(track.getTitle())
                                    .setContentText(track.getAlbum())
                                    .setLargeIcon(icon)
                                    .setOnlyAlertOnce(true)
                                    .addAction(drw_previous,"Previous",pendingIntentprevious)
                                    .addAction(play,"Play",pendingIntentplay)
                                    .addAction(drw_next,"Next",pendingIntentnext)
                                    .setShowWhen(false)
                                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                                            .setShowActionsInCompactView(0,1,2)
                                            .setMediaSession(mediaSessionCompat.getSessionToken()))
                                    .setPriority(NotificationCompat.PRIORITY_LOW)
                                    .build();

                            Log.d(TAG, "onResourceReady: resourse is "+icon);
                            notificationManagerCompat.notify(1,notification);

                        }
                    });

            notification = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.music)
                    .setContentTitle(track.getTitle())
                    .setContentText(track.getAlbum())
                    .setLargeIcon(icon)
                    .setOnlyAlertOnce(true)
                    .addAction(drw_previous,"Previous",pendingIntentprevious)
                    .addAction(play,"Play",pendingIntentplay)
                    .addAction(drw_next,"Next",pendingIntentnext)
                    .setShowWhen(false)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(0,1,2)
                            .setMediaSession(mediaSessionCompat.getSessionToken()))
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .build();

            Log.d(TAG, "onResourceReady: resourse is "+icon);
            notificationManagerCompat.notify(1,notification);
        }
    }

    public  static void  loadimage(String urles){

        final CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(40f);
        circularProgressDrawable.start();

        Log.d(TAG, "loadimage: got url is "+urles);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(circularProgressDrawable);
        requestOptions.skipMemoryCache(true);
        //requestOptions.circleCrop();

        requestOptions.priority(Priority.HIGH);


        Glide.with(context)
                .load(urles)
                .apply(requestOptions)
                .transform(new RoundedCorners(20))
                .transition(DrawableTransitionOptions.withCrossFade())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        assert e != null;
                        //Toast.makeText(contexts, e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onLoadFailed: "+e.getMessage());

                        String err="Error in loadimage in service "+e.getMessage();
                        new internal_error_report(context,err,MainActivity.sharedPreferences.getString(USERNAME,"")).execute();

                        circularProgressDrawable.stop();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        //Toast.makeText(contexts, "Image Loaded", Toast.LENGTH_SHORT).show();
                        circularProgressDrawable.stop();
                        Log.d(TAG, "onResourceReady: loaded image");
                        return false;
                    }
                })
                .into(song_image);
    }

    public static String millisecond(long millisecond){
        String timestring="";
        String secondstring;
        int hours=(int)(millisecond/(1000*60*60));
        int min=(int)(millisecond%(1000*60*60))/(1000*60);
        int second=(int)((millisecond%(1000*60*60))%(1000*60)/1000);

        if(hours>0){
            timestring=hours+":";
        }
        if(second<10){
            secondstring="0"+second;
        }else{
            secondstring=""+second;
        }
        timestring=timestring+min+":"+secondstring;
        return timestring;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {

        if(focusChange<=0){
            ontrackpause();
            changeplaypauseimg(false);
            changeplaypauseimgs(false);
            playsong.setImageResource(R.drawable.play_white);
        }else{
            ontrackplay(1000);
            changeplaypauseimg(true);
            changeplaypauseimgs(true);
            playsong.setImageResource(R.drawable.pause_white);
        }

    }

    public static Runnable terminate_all_process = new Runnable() {
        @Override
        public void run() {

            if(IS_RUNNING_SERVICE) {
                if (mediaPlayer != null && (isprepared || IS_PLAYING_SONG || IS_PAUSED_SONG)) {
                    new playselectedsong.updateseekdetail().cancel(true);
                    tracks.clear();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    isplaying=false;
                    isprepared=false;
                    ispreparing=false;
                    onclearfrompercentservice service = new onclearfrompercentservice();
                    service.terminate();
                } else {
                    handler.postDelayed(this, 1000);
                    TERMINATION_STATUS = false;
                }
            }else{
                TERMINATION_STATUS=true;
            }

        }
    };

    protected  void terminate(){
        onclearfrompercentservice clear = new onclearfrompercentservice();
        if(IS_RUNNING_SERVICE) {
            //unregisterReceiver(broadcastReceiver);
            Intent services=new Intent(context,onclearfrompercentservice.class);
            context.stopService(services);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationManager.cancelAll();
            }
        }
        TERMINATION_STATUS=true;
    }
}
