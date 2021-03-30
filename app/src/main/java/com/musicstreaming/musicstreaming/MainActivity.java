package com.musicstreaming.musicstreaming;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
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
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.navigation.NavigationView;
import com.musicstreaming.musicstreaming.service.get_fav_song_list;
import com.musicstreaming.musicstreaming.service.online_status_updater;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static com.musicstreaming.musicstreaming.login.IMAGE;
import static com.musicstreaming.musicstreaming.login.NAME;
import static com.musicstreaming.musicstreaming.login.SHARED_PREF;
import static com.musicstreaming.musicstreaming.login.USERNAME;
import static com.musicstreaming.musicstreaming.service.get_fav_song_list.listofplaylistArrayList_for_shortcut;
import static com.musicstreaming.musicstreaming.service.onclearfrompercentservice.IS_RUNNING_SERVICE;
import static com.musicstreaming.musicstreaming.service.onclearfrompercentservice.isplaying;
import static com.musicstreaming.musicstreaming.service.onclearfrompercentservice.isprepared;
import static com.musicstreaming.musicstreaming.service.onclearfrompercentservice.ontrackpause;
import static com.musicstreaming.musicstreaming.service.onclearfrompercentservice.ontrackplay;
import static com.musicstreaming.musicstreaming.service.onclearfrompercentservice.position;
import static com.musicstreaming.musicstreaming.service.online_status_updater.child_items;
import static com.musicstreaming.musicstreaming.service.online_status_updater.expandableListAdapter;
import static com.musicstreaming.musicstreaming.service.online_status_updater.group_item;
import static com.musicstreaming.musicstreaming.splash.DIR_NAME;
import static com.musicstreaming.musicstreaming.splash.PASSWORD;
import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {
    /**
     * <h1>Ownership</h1>
     * <p>This Application is Owned by Ashish Kumar, Delhi Technology univerity, Btech ,Mechanical
     * Engineering ,2ndyear Student</p>
     * <p>Finished First version 1.0 on 17-Aug-2020</p>
     */
    NavigationView navigationView;
    public static DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    Fragment fragment;
    ImageView profileimage;
    TextView name,version;
    int k=0,fragment_id=0,lastExpandedGroup=-1;;
    String TAG="thisisprofileimage";
    boolean IS_FRAGMENT_SET=false;
    public static LinearLayout showdetailses,thisisit;
    public static TextView songname;
    public static ImageView playpause;
    public static ProgressBar progressBar;
    public static Activity MAIN_ACTIVITY;
    public static SharedPreferences sharedPreferences;
    Toolbar toolbar;
    public static Context MAIN_ACTIVITY_CONTEXT;
    public static ExpandableListView expandableListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MAIN_ACTIVITY=this;
        MAIN_ACTIVITY_CONTEXT=this;
        online_status_updater.CURRENT_ACTIVITY_CONTEXT=MAIN_ACTIVITY_CONTEXT;

        //error handler
        Thread.setDefaultUncaughtExceptionHandler(new Exceptionhandler(this));

        sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);

        //initilizations
        navigationView = findViewById(R.id.navmenu);
        drawerLayout = findViewById(R.id.drawair);
         toolbar = findViewById(R.id.toolbar);
        showdetailses=findViewById(R.id.showdetailofsonginplaylist);
        songname=findViewById(R.id.songnameinplaylist);
        playpause=findViewById(R.id.playpausinplaylist);
        thisisit=findViewById(R.id.thisisit);
        progressBar=findViewById(R.id.songprogress);
        expandableListView=findViewById(R.id.expandableListView);
        songname.setSelected(true);
        setSupportActionBar(toolbar);

        String url="https://rentdetails.000webhostapp.com/musicplayer_files/favouratemusic.php",
                username;
        username=sharedPreferences.getString(USERNAME,"");

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N_MR1) {
            new get_fav_song_list(this).execute(url, username);
        }

//        expandableListAdapter=new MyExpandableListAdaptor(this,online_status_updater.child_items,group_item);
        expandableListView.setAdapter(expandableListAdapter);

        //setting initial fragment
        Intent intent = getIntent();
        fragment_id=intent.getIntExtra("fragment_id",0);
        k=intent.getIntExtra("k",1);

        if(fragment_id==0) {
            getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, new homefragment()).commit();
            navigationView.setCheckedItem(R.id.home);
        }else{
            replaceFrameLayout(fragment_id);
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, fragment).commit();
            }
        }


            String names = "", emails, urls;

                View navview = navigationView.inflateHeaderView(R.layout.navheader);
                if (sharedPreferences.getString(USERNAME, "").equals("aka") || sharedPreferences.getString(USERNAME, "").equals("dipcha")) {
                    navigationView.inflateMenu(R.menu.icon_menu_moderator);
                } else {
                    navigationView.inflateMenu(R.menu.iconmenu);
                }
                profileimage = navview.findViewById(R.id.profile);
                name = navview.findViewById(R.id.username);
                version = findViewById(R.id.version);
                names = sharedPreferences.getString(NAME, "");
                name.setText(names);

                String vers = BuildConfig.VERSION_NAME;
                version.setText(vers);

                 profileimage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, profile_img.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                    }
                });

            urls = sharedPreferences.getString(IMAGE, "");
            loadimage(urls);

            toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close){

                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                    expandableListView.setAdapter(expandableListAdapter);
                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    expandableListView.setAdapter(expandableListAdapter);
                }
            };
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();

            //click listner on T.V for opening 'now playing'
            songname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, playselectedsong.class)
                            .putExtra("position", 1000));
                }
            });

            //defination in the bottom
            showdetail(isprepared);
            changeplaypauseimgs(isplaying);

            expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                @Override
                public void onGroupExpand(int groupPosition) {
                    if(lastExpandedGroup!=-1 && groupPosition!=lastExpandedGroup){
                        expandableListView.collapseGroup(groupPosition);
                    }
                    lastExpandedGroup=groupPosition;
                }
            });

            expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                    Toast.makeText(MainActivity.this, "You clicked on "+expandableListAdapter.getChild(groupPosition, childPosition).getName(), Toast.LENGTH_SHORT).show();

                    return true;
                }
            });

            //click listener for the play/pause img
            playpause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isplaying) {
                        playpause.setImageResource(R.drawable.normal_play);
                        ontrackpause();
                    } else {
                        playpause.setImageResource(R.drawable.normal_pause);
                        ontrackplay(1000);
                    }
                }
            });

            //switch between fragments
            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {


                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {


                    switch (menuItem.getItemId()) {
                        case R.id.home:
                            fragment = new homefragment();
                            navigationView.setCheckedItem(R.id.home);
                            toolbar.setTitle("Music Streaming");
                            break;

                        case R.id.playlist:
                            fragment = new playlistfragment();
                            navigationView.setCheckedItem(R.id.playlist);
                            toolbar.setTitle("Favourates");
                            break;

                        case R.id.settings:
                            fragment = new settingfragment();
                            navigationView.setCheckedItem(R.id.settings);
                            toolbar.setTitle("Settings");
                            break;

                        case R.id.credits:
                            fragment = new credits();
                            navigationView.setCheckedItem(R.id.credits);
                            toolbar.setTitle("Music Streaming");
                            break;

                        case R.id.now_plyng:
                            if (IS_RUNNING_SERVICE) {
                                startActivity(new Intent(MainActivity.this, playselectedsong.class)
                                        .putExtra("position", 1000));
                            } else {
                                Toast.makeText(MainActivity.this, "No playlist is selected!!", Toast.LENGTH_SHORT).show();
                            }
                            break;

                        case R.id.mylibrary:
                            fragment = new show_custom_playlists();
                            navigationView.setCheckedItem(R.id.mylibrary);
                            toolbar.setTitle("My Library");

                            break;

                        case R.id.error_log:
                            fragment = new error_msgs();
                            navigationView.setCheckedItem(R.id.error_log);
                            toolbar.setTitle("Error Logs");
                            break;

                        case R.id.search:
                            fragment = new search_song_fragment();
                            navigationView.setCheckedItem(R.id.search);
                            toolbar.setTitle("Search Song");
                            break;

                    }
                    if (fragment != null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, fragment).commit();
                    }
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                }
            });

            File file = new File(Environment.getExternalStorageDirectory() + "/" + DIR_NAME, "file.json");

            new make_file_in_directory(this, this, sharedPreferences.getString(USERNAME, "")).write_credential_file(sharedPreferences.getString(USERNAME, ""), sharedPreferences.getString(PASSWORD, ""), file);

    }

    //backpress handler
    @Override
    public void onBackPressed() {
        //Fragment check=new homefragment();
        if(k==0) {
            startActivity(new Intent(this,MainActivity.class).putExtra("k",1));
            toolbar.setTitle("Music Streaming");
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

        try {
            Glide.with(this)
                    .load(url)
                    .apply(requestOptions)
                    .apply(RequestOptions.circleCropTransform())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                            Toast.makeText(MainActivity.this, "Fail to load Image ", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onLoadFailed: fail to load image " + e.getMessage());
                            String err = "Error in loading profile Image " + e.getMessage();
                            new internal_error_report(MAIN_ACTIVITY, err, MainActivity.sharedPreferences.getString(USERNAME, "")).execute();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                            Log.d(TAG, "onResourceReady: image loading successful ");

                            return false;
                        }
                    })
                    .into(profileimage);
        }catch (Exception e){
            new internal_error_report(MainActivity.this,"Error in MainActivity loading profile image : "+e.getMessage(),sharedPreferences.getString(USERNAME,"")).execute();
        }
    }

    public static void setsongprogress(int progress){
        progressBar.setProgress(progress);
    }

    public static void get_credits(Activity activity){

        ((FragmentActivity)activity).getSupportFragmentManager().beginTransaction().replace(R.id.framelayout,new credits()).commit();

    }

    public void replaceFrameLayout(int fragment_id){

        switch (fragment_id) {
            case 1:
                fragment = new homefragment();
                navigationView.setCheckedItem(R.id.home);
                toolbar.setTitle("Music Streaming");
                break;

            case 2:
                fragment = new playlistfragment();
                navigationView.setCheckedItem(R.id.playlist);
                toolbar.setTitle("Favourates");
                break;

            case 3:
                fragment = new settingfragment();
                navigationView.setCheckedItem(R.id.settings);
                toolbar.setTitle("Settings");
                break;

            case 4:
                fragment = new credits();
                navigationView.setCheckedItem(R.id.credits);
                toolbar.setTitle("Music Streaming");
                break;

            case 5:
                if (IS_RUNNING_SERVICE) {
                    startActivity(new Intent(MainActivity.this, playselectedsong.class)
                            .putExtra("position", 1000));
                } else {
                    Toast.makeText(MainActivity.this, "No playlist is selected!!", Toast.LENGTH_SHORT).show();
                }
               break;

            case 6:
                fragment = new show_custom_playlists();
                navigationView.setCheckedItem(R.id.mylibrary);
                toolbar.setTitle("My Library");

                break;

            case 7:
                fragment = new error_msgs();
                navigationView.setCheckedItem(R.id.error_log);
                toolbar.setTitle("Error Logs");
                break;

            case 8:
                fragment = new search_song_fragment();
                navigationView.setCheckedItem(R.id.search);
                toolbar.setTitle("Search Song");
                break;
        }


    }
}
