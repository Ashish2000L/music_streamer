package com.example.musicstreaming;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.musicstreaming.R;
import com.google.android.material.navigation.NavigationView;

import static com.example.musicstreaming.login.EMAIL;
import static com.example.musicstreaming.login.IMAGE;
import static com.example.musicstreaming.login.NAME;
import static com.example.musicstreaming.login.SHARED_PREF;
import static com.example.musicstreaming.login.USERNAME;
import static com.example.musicstreaming.service.onclearfrompercentservice.IS_RUNNING_SERVICE;
import static com.example.musicstreaming.service.onclearfrompercentservice.isplaying;
import static com.example.musicstreaming.service.onclearfrompercentservice.isprepared;
import static com.example.musicstreaming.service.onclearfrompercentservice.ontrackpause;
import static com.example.musicstreaming.service.onclearfrompercentservice.ontrackplay;
import static com.example.musicstreaming.service.onclearfrompercentservice.position;

public class MainActivity extends AppCompatActivity {
    /**
     * <h1>Ownership</h1>
     * <p>This Application is Owned by Ashish Kumar, Delhi Technology univerity, Btech ,Mechanical
     * Engineering ,2ndyear Student</p>
     * <p>Finished First version 1.0 on 17-Aug-2020</p>
     */
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    Fragment fragment;
    ImageView profileimage;
    TextView name,version;
    int k=0;
    String TAG="thisisprofileimage";
    public static LinearLayout showdetailses,thisisit;
    public static TextView songname;
    public static ImageView playpause;
    public static ProgressBar progressBar;
    public static Activity MAIN_ACTIVITY;
    public static SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MAIN_ACTIVITY=this;

        //error handler
        Thread.setDefaultUncaughtExceptionHandler(new Exceptionhandler(this));

        //initilizations
        navigationView = findViewById(R.id.navmenu);
        drawerLayout = findViewById(R.id.drawair);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        showdetailses=findViewById(R.id.showdetailofsonginplaylist);
        songname=findViewById(R.id.songnameinplaylist);
        playpause=findViewById(R.id.playpausinplaylist);
        thisisit=findViewById(R.id.thisisit);
        progressBar=findViewById(R.id.songprogress);
        songname.setSelected(true);
        setSupportActionBar(toolbar);

        sharedPreferences=getSharedPreferences(SHARED_PREF,MODE_PRIVATE);
        String names = "",emails,urls;

        //handling profile detail in the drawable section
        try {
            View navview = navigationView.inflateHeaderView(R.layout.navheader);
            profileimage = navview.findViewById(R.id.profile);
            name = navview.findViewById(R.id.username);
            version = findViewById(R.id.version);
            names=sharedPreferences.getString(NAME,"");
            name.setText(names);
        }catch (Exception e){
            String err="Error in Navigation Drawable inflation"+e.getMessage();
            new internal_error_report(MAIN_ACTIVITY,err,sharedPreferences.getString(USERNAME,"")).execute();
        }

        String vers=BuildConfig.VERSION_NAME;
        version.setText(vers);

        urls=sharedPreferences.getString(IMAGE,"");
        loadimage(urls);

        toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //click listner on T.V for opening 'now playing'
        songname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,playselectedsong.class)
                .putExtra("position",1000));
            }
        });

        //defination in the bottom
        showdetail(isprepared);
        changeplaypauseimgs(isplaying);

        //click listener for the play/pause img
        playpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isplaying){
                    playpause.setImageResource(R.drawable.normal_play);
                    ontrackpause();
                }else{
                    playpause.setImageResource(R.drawable.normal_pause);
                    ontrackplay(1000);
                }
            }
        });

        //setting initial fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.framelayout,new homefragment()).commit();
        navigationView.setCheckedItem(R.id.home);

        //switch between fragments
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {



            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {


                switch (menuItem.getItemId()){
                    case R.id.home:
                        fragment =new homefragment();
                        navigationView.setCheckedItem(R.id.home);
                        toolbar.setTitle("Music Streaming");
                        break;

                    case R.id.playlist:
                        fragment = new playlistfragment();
                        navigationView.setCheckedItem(R.id.playlist);
                        toolbar.setTitle("Favourates");
                        break;

                    case R.id.settings:
                        fragment = new settingfragment(MainActivity.this);
                        navigationView.setCheckedItem(R.id.settings);
                        toolbar.setTitle("Profile");
                        break;

                    case R.id.credits:
                        fragment=new credits();
                        navigationView.setCheckedItem(R.id.credits);
                        toolbar.setTitle("Music Streaming");
                        break;

                    case R.id.now_plyng:
                        if(IS_RUNNING_SERVICE){
                            startActivity(new Intent(MainActivity.this,playselectedsong.class)
                                    .putExtra("position",1000));
                        }else{
                            Toast.makeText(MainActivity.this, "No playlist is selected!!", Toast.LENGTH_SHORT).show();
                        }

                        break;

                    case R.id.mylibrary:
                        if(BuildConfig.DEBUG){
                            //fragment=new make_own_playlist_fragment();
                            //navigationView.setCheckedItem(R.id.mylibrary);
                            //toolbar.setTitle("My Library");
                            startActivity(new Intent(MainActivity.this,new_playlist.class));
                        }else{
                            Toast.makeText(MainActivity.this, "We are working on it please wait till next Update!", Toast.LENGTH_SHORT).show();
                        }
                        break;

                }
                if(fragment!=null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, fragment).commit();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,profile_img.class));
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

    }

    //backpress handler
    @Override
    public void onBackPressed() {
        //Fragment check=new homefragment();
        if(k==0) {
            k=1;
            fragment = new homefragment();
            navigationView.setCheckedItem(R.id.home);
            getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, fragment).commit();
        }else{
            finishAffinity();
        }
    }

    //changing song details i.e song_name & artist
    public static void showdetail(boolean isplaying){
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) showdetailses.getLayoutParams();
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) thisisit.getLayoutParams();

        if(isplaying){
            try {
                showdetailses.setVisibility(View.VISIBLE);
                String songnames = playselectedsong.tracks.get(position).getTitle();
                String artist = playselectedsong.tracks.get(position).getAlbum();
                String finalname = songnames + " (" + artist + ")";
                songname.setText(finalname);
                layoutParams.weight = 9.1f;
                params.weight = 0.9f;
            }catch (Exception e){
                e.printStackTrace();
            }

        }else{
            showdetailses.setVisibility(View.GONE);
            layoutParams.weight=10.0f;
            params.weight=0.0f;
        }

    }

    //change play/pause in the home screen bottom bar
    public static void changeplaypauseimgs(boolean playing){
        if(playing){
            playpause.setImageResource(R.drawable.normal_pause);
        }else{
            playpause.setImageResource(R.drawable.normal_play);
        }
    }

    //loading users profile image
    public void loadimage(String url){
        final CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(this);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(40f);
        circularProgressDrawable.start();

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(circularProgressDrawable);
        requestOptions.skipMemoryCache(true);
        requestOptions.circleCrop();
        requestOptions.priority(Priority.HIGH);
        requestOptions.fitCenter();

        Log.d(TAG, "loadimage: your requested url is "+url);

        Glide.with(this)
                .load(url)
                .apply(requestOptions)
                .apply(RequestOptions.circleCropTransform())
                .transition(DrawableTransitionOptions.withCrossFade())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                        Toast.makeText(MainActivity.this, "Fail to load Image ", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onLoadFailed: fail to load image "+e.getMessage());
                        String err="Error in loading profile Image "+e.getMessage();
                        new internal_error_report(MAIN_ACTIVITY,err,MainActivity.sharedPreferences.getString(USERNAME,"")).execute();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                        Log.d(TAG, "onResourceReady: image loading successful ");

                        return false;
                    }
                })
                .into(profileimage);
    }

    public static void setsongprogress(int progress){
        progressBar.setProgress(progress);
    }

}
