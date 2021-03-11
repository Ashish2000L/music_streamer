package com.musicstreaming.musicstreaming;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.musicstreaming.musicstreaming.MainActivity.MAIN_ACTIVITY;
import static com.musicstreaming.musicstreaming.login.SHARED_PREF;
import static com.musicstreaming.musicstreaming.login.USERNAME;
import static com.musicstreaming.musicstreaming.service.onclearfrompercentservice.POSITION_FAV_PLAYLIST;


public class playlistfragment extends Fragment {
    /**
     * <h1>Ownership</h1>
     * <p>This Application is Owned by Ashish Kumar, Delhi Technology univerity, Btech ,Mechanical
     * Engineering ,2ndyear Student</p>
     * <p>Finished First version 1.0 on 17-Aug-2020</p>
     */

    ListView listview_of_fav_playlist;
    Context context;
    String TAG="favourateplaylists",url="https://rentdetails.000webhostapp.com/musicplayer_files/favouratemusic.php";
    public static ArrayList<listofplaylist> listofplaylistArrayList_for_fav=new ArrayList<>();
    playlistadapter playlistadapter_for_fav;
    listofplaylist listofplaylist_for_fav;
    public SharedPreferences sharedPreferences;
    Animation frombottom;
    LottieAnimationView animationView;
    RelativeLayout loading;
    Boolean IS_FROM_SHORTCUT=false;
    int PLAYLIST_POSITION_FROM_SHORTCUT=0;


    public playlistfragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_playlistfragment, container, false);

        listview_of_fav_playlist=view.findViewById(R.id.listview_for_favourates);
        context=view.getContext();

        Thread.setDefaultUncaughtExceptionHandler(new Exceptionhandler(MAIN_ACTIVITY));

        sharedPreferences=context.getSharedPreferences(SHARED_PREF,Context.MODE_PRIVATE);

        try{
            IS_FROM_SHORTCUT=getActivity().getIntent().getBooleanExtra("is_from_shortcut",false);
            PLAYLIST_POSITION_FROM_SHORTCUT=getActivity().getIntent().getIntExtra("playlist_position",0);
            POSITION_FAV_PLAYLIST=PLAYLIST_POSITION_FROM_SHORTCUT; 
        }catch (Exception e){
            e.printStackTrace();
        }

        frombottom= AnimationUtils.loadAnimation(context,R.anim.frombottom);
        animationView=view.findViewById(R.id.animation_view);
        loading=view.findViewById(R.id.loading);

        playlistadapter_for_fav=new playlistadapter(context,listofplaylistArrayList_for_fav);
        listview_of_fav_playlist.setAdapter(playlistadapter_for_fav);

        listview_of_fav_playlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                POSITION_FAV_PLAYLIST=position;

                startActivity(new Intent(context,songsfromplaylist.class)
                .putExtra("isfav",true).putExtra("positions",POSITION_FAV_PLAYLIST)
                .putExtra("id",listofplaylistArrayList_for_fav.get(position).getId())
                .putExtra("name",listofplaylistArrayList_for_fav.get(position).getName())
                .putExtra("imageurl",listofplaylistArrayList_for_fav.get(position).getImage()));
            }
        });
        
        new getdata().execute( url);

        return view;
    }

    public class getdata extends AsyncTask<String ,Void,Void> {
        ProgressDialog progressDialog;
        String message="";
        int number=0;

        @Override
        protected void onPreExecute() {
            loading.setVisibility(View.VISIBLE);
            listview_of_fav_playlist.setVisibility(View.GONE);
            animationView.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... strings) {

            String url=strings[0];
            final String username=sharedPreferences.getString(USERNAME,"");
            Log.d(TAG, "doInBackground: url is "+url);

            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    listofplaylistArrayList_for_fav.clear();
                    message="progress done";
                    Log.d(TAG, "onResponse: your response is "+response);

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

                                Log.d(TAG, "onResponse: id is "+id);

                                //String customurl = "https://rentdetails.000webhostapp.com/musicplayer_files/musicimages/"+image;
                                //Toast.makeText(ListOfRentersForAdmin.this, usernames, Toast.LENGTH_LONG).show();
                                listofplaylist_for_fav = new listofplaylist(id,name,image,likes,note,totl_like);
                                listofplaylistArrayList_for_fav.add(listofplaylist_for_fav);
                                playlistadapter_for_fav.notifyDataSetChanged();
                            }

                            if(IS_FROM_SHORTCUT) {
                                if(PLAYLIST_POSITION_FROM_SHORTCUT!=0) {
                                    startActivity(new Intent(context, songsfromplaylist.class)
                                            .putExtra("isfav", true).putExtra("positions", PLAYLIST_POSITION_FROM_SHORTCUT)
                                            .putExtra("id", listofplaylistArrayList_for_fav.get(PLAYLIST_POSITION_FROM_SHORTCUT).getId())
                                            .putExtra("name", listofplaylistArrayList_for_fav.get(PLAYLIST_POSITION_FROM_SHORTCUT).getName())
                                            .putExtra("imageurl", listofplaylistArrayList_for_fav.get(PLAYLIST_POSITION_FROM_SHORTCUT).getImage()));
                                }
                            }
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        message="failed ";
                        //new erroinfetch().execute(e.getMessage());
                        startActivity(new Intent(context,MainActivity.class)); 
                        Log.d(TAG, "onResponse: error in json is "+e.getMessage());
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    message="error in fetching playlist";

                    if(error.getMessage()!=null){
                        Log.d(TAG, "onErrorResponse: error in connection is "+error.getMessage());
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }else{
                        Log.d(TAG, "onErrorResponse: null error found ");
                    }

                    String err="Error in getdata in playlistfragment "+error.getMessage();
                    new internal_error_report(context,err,MainActivity.sharedPreferences.getString(USERNAME,"")).execute();

                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params=new HashMap<String, String>();
                    if(!username.equals("")) {
                        params.put("username", username);
                    }
                    return params;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(context);
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
                        animationView.setVisibility(View.GONE);
                        listview_of_fav_playlist.setVisibility(View.VISIBLE);
                        listview_of_fav_playlist.startAnimation(frombottom);
                        if(!message.equalsIgnoreCase("progress done"))
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }else{
                        Log.d(TAG, "run: running for : "+number++);
                        handler.postDelayed(this,1000);
                    }
                }
            };
            runnable.run();
        }
    }

}
