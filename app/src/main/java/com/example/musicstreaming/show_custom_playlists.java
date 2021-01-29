package com.example.musicstreaming;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import java.util.Locale;
import java.util.Map;

import static com.example.musicstreaming.MainActivity.MAIN_ACTIVITY;
import static com.example.musicstreaming.login.SHARED_PREF;
import static com.example.musicstreaming.login.USERNAME;
import static com.example.musicstreaming.service.onclearfrompercentservice.POSITION_FAV_PLAYLIST;

public class show_custom_playlists extends Fragment {


    ListView listviewforplaylist;
    SwipeRefreshLayout swipeRefreshLayout;
    public static ArrayList<listofplaylist> listofplaylistArrayList = new ArrayList<>();
    listofplaylist listofplaylist;
    playlistadapter playlistadapter,playlistadapter1;
    Context context;
    Animation frombottom,disapper;
    RelativeLayout relativeLayout;
    LottieAnimationView lottieAnimationView;
    public SharedPreferences sharedPreferences;
    SearchView searchView;
    boolean IS_IN_SEARCHVIEW=false, IS_DELETING_DONE=false;
    String TAG="loadingplaylist",urls="https://rentdetails.000webhostapp.com/musicplayer_files/get_custom_playlist.php";
    String SEARCH_VIEW_NAME,FIRST_TIME_MESSAGE_DEL="first_time_dels";
    String[] names_playlist;
    ArrayList<listofplaylist> arrayList=new ArrayList<>();
    int positions=0;

    public show_custom_playlists() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_custom_playlists, container, false);

        Thread.setDefaultUncaughtExceptionHandler(new Exceptionhandler(MAIN_ACTIVITY));

        listviewforplaylist=view.findViewById(R.id.custom_playlist_listview);
        swipeRefreshLayout = view.findViewById(R.id.swipe_ref_listofplaylist);
        context=view.getContext();

        frombottom= AnimationUtils.loadAnimation(context,R.anim.frombottom);
        disapper=AnimationUtils.loadAnimation(context,R.anim.disapper);

        sharedPreferences=context.getSharedPreferences(SHARED_PREF,Context.MODE_PRIVATE);

        relativeLayout = view.findViewById(R.id.loading);
        lottieAnimationView = view.findViewById(R.id.animation_view);
        playlistadapter = new playlistadapter(view.getContext(),listofplaylistArrayList);
        listviewforplaylist.setAdapter(playlistadapter);

        setHasOptionsMenu(true);
        listviewforplaylist.setTextFilterEnabled(true);

        listView_operations();

        swipeRefreshLayout.setColorSchemeColors(Color.CYAN);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new getdata().execute(urls);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        if(sharedPreferences.getBoolean(FIRST_TIME_MESSAGE_DEL,true)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context)
                    .setTitle("INSTRUCTION")
                    .setMessage("For Deleting playlist make long press on the playlist!")
                    .setNeutralButton("Got it!",null);

            builder.show();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(FIRST_TIME_MESSAGE_DEL,false);
            editor.apply();

        }

        return view;
    }

    public void listView_operations(){

        new getdata().execute(urls);

        listviewforplaylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!IS_IN_SEARCHVIEW) {
//                    POSITION_FAV_PLAYLIST=position;
                    startActivity(new Intent(context, songsfromplaylist.class)
                            .putExtra("positions", position)
                            .putExtra("imageurl", listofplaylistArrayList.get(position).getImage())
                            .putExtra("name", listofplaylistArrayList.get(position).getName())
                            .putExtra("id", listofplaylistArrayList.get(position).getId()));
                }else{
                    SEARCH_VIEW_NAME=names_playlist[position];
                    position=find_pos(SEARCH_VIEW_NAME);
//                    POSITION_FAV_PLAYLIST=position;
                    if(position!=1000) {
                        startActivity(new Intent(context, songsfromplaylist.class)
                                .putExtra("positions", position)
                                .putExtra("imageurl", listofplaylistArrayList.get(position).getImage())
                                .putExtra("name", listofplaylistArrayList.get(position).getName())
                                .putExtra("id", listofplaylistArrayList.get(position).getId()));
                    }else{
                        Toast.makeText(context, "Fail to Proceed!", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });


        listviewforplaylist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                String[] sample= {"Delete Playlist","Change Playlist"};
                if(!IS_IN_SEARCHVIEW) {
//                    POSITION_FAV_PLAYLIST=position;
                    positions=position;
                    AlertDialog.Builder alert = new AlertDialog.Builder(context)
                            .setItems(sample,  new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(which==0){
                                        new delete_playlist(listofplaylistArrayList.get(positions).getId(),sharedPreferences.getString(USERNAME,"")).execute();
                                    }else
                                        if(which==1){

                                            startActivity(new Intent(context,make_custom_playlist.class).putExtra("PLAYLIST_ID",listofplaylistArrayList.get(positions).getId())
                                            .putExtra("IS_MODIFYING",true));
                                        }
                                }
                            });
                    alert.show();

                }else{
                    SEARCH_VIEW_NAME=names_playlist[position];
                    positions=find_pos(SEARCH_VIEW_NAME);
//                    POSITION_FAV_PLAYLIST=position;
                    if(positions!=1000) {

                        AlertDialog.Builder alert = new AlertDialog.Builder(context)
                                .setItems(sample, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(which==0){
                                            new delete_playlist(listofplaylistArrayList.get(positions).getId(),sharedPreferences.getString(USERNAME,"")).execute();
                                        }else
                                        if(which==1){

                                            startActivity(new Intent(context,make_custom_playlist.class).putExtra("PLAYLIST_ID",listofplaylistArrayList.get(positions).getId())
                                                    .putExtra("IS_MODIFYING",true));
                                        }
                                    }
                                });
                        alert.show();

                        if(IS_DELETING_DONE){
                            listofplaylistArrayList.remove(positions);

                        }


                    }else{
                        Toast.makeText(context, "Fail to Proceed!", Toast.LENGTH_SHORT).show();
                    }

                }

                return true;
            }
        });

    }

    public int find_pos(String name){
        for(int i=0;i<listofplaylistArrayList.size();i++){
            if(name.equals(listofplaylistArrayList.get(i).getName())){
                return i;
            }
        }
        return 1000;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.show_custom_playlist_menu,menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.search_playlist) {
            searchView = (SearchView) item.getActionView();
            searchView.setQueryHint("Search Your Playlist");
            searchView.setIconifiedByDefault(false);
            searchView.setIconified(false);
            searchView.scheduleLayoutAnimation();

            item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    IS_IN_SEARCHVIEW = true;

                    if (playlistadapter1 == null) {
                        playlistadapter1 = new playlistadapter(context, arrayList);
                    } else {
                        playlistadapter1 = null;
                        arrayList = new ArrayList<>();
                        playlistadapter1 = new playlistadapter(context, arrayList);
                    }

                    if (arrayList.isEmpty()) {
                        arrayList.addAll(listofplaylistArrayList);
                    } else {
                        arrayList = new ArrayList<>();
                        arrayList.addAll(listofplaylistArrayList);
                    }

                    listviewforplaylist.setAdapter(playlistadapter1);

                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {

                            if (TextUtils.isEmpty(newText)) {
                                filter("/./.");
                            } else {
                                filter(newText);
                            }

                            return true;
                        }
                    });

                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    IS_IN_SEARCHVIEW = false;
                    listviewforplaylist.setAdapter(playlistadapter);
                    playlistadapter.notifyDataSetChanged();
                    return true;
                }
            });
        }else
            if(item.getItemId()==R.id.add_new_playlist){

                startActivity(new Intent(context,new_playlist.class));

            }


        return true;
    }

    public void filter(String text){
        IS_IN_SEARCHVIEW=true;
        names_playlist=new String[arrayList.size()];
        text=text.toLowerCase(Locale.getDefault());


        playlistadapter1.clear();
        arrayList=listofplaylistArrayList;

        if(text.length()==0){
            for (int i=0;i<arrayList.size();i++){
                names_playlist[i]=arrayList.get(i).getName();
            }

        }else {

            int k=0;
            for ( int i=0;i<arrayList.size();i++){

                if(arrayList.get(i).getName().toLowerCase(Locale.getDefault()).contains(text)){

                    playlistadapter1.add(arrayList.get(i));
                    names_playlist[k]=arrayList.get(i).getName();
                    k++;
                }
            }
        }
        playlistadapter1.notifyDataSetChanged();
    }

    public class getdata extends AsyncTask<String ,Void,Void> {
        String message="";
        int number=0;

        @Override
        protected void onPreExecute() {
            relativeLayout.setVisibility(View.VISIBLE);
            listviewforplaylist.setVisibility(View.GONE);
            lottieAnimationView.setProgress(0);
            lottieAnimationView.setVisibility(View.VISIBLE);
            listviewforplaylist.setAdapter(playlistadapter);
        }

        @Override
        protected Void doInBackground(String... strings) {

            String url=strings[0];

            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    listofplaylistArrayList.clear();
                    message="progress done";

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

                                listofplaylist = new listofplaylist(id,name,image,likes,note);
                                listofplaylistArrayList.add(listofplaylist);
                                playlistadapter.notifyDataSetChanged();
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
                    message="error in fetching playlist";
                    if(error.getMessage()!=null){
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }else{
                        Log.d(TAG, "onErrorResponse: null error found ");
                    }

                    String err="Error in getdata in show_custom_playlist "+error.getMessage();
                    new internal_error_report(context,err,sharedPreferences.getString(USERNAME,"")).execute();

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    if (!sharedPreferences.getString(USERNAME,"").equals("")) {
                        params.put("username", sharedPreferences.getString(USERNAME,""));
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
                        if(!message.equalsIgnoreCase("progress done"))
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        listviewforplaylist.startAnimation(frombottom);
                        listviewforplaylist.setVisibility(View.VISIBLE);
                        lottieAnimationView.setVisibility(View.GONE);
                        relativeLayout.setVisibility(View.GONE);
                    }else{
                        handler.postDelayed(this,1000);
                    }
                }
            };
            runnable.run();
        }
    }

    public class delete_playlist extends AsyncTask<String,Void,Void>{

        String playlist_id, username,url="https://rentdetails.000webhostapp.com/musicplayer_files/delete_custom_playlist.php";
        ProgressDialog progressDialog;
        public delete_playlist(String id,String username) {
            this.playlist_id=id;
            this.username=username;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Deleting Playlist...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    progressDialog.dismiss();
                    if(response.equals("successful")){
                        new getdata().execute(urls);
                    }else{
                        check_for_error(response);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    progressDialog.dismiss();
                    if(error.getMessage()!=null){
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }else{
                        Log.d(TAG, "onErrorResponse: null error found ");
                    }

                    String err="Error in getdata in show_custom_playlist "+error.getMessage();
                    new internal_error_report(context,err,sharedPreferences.getString(USERNAME,"")).execute();

                }
            }){

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params=new HashMap<String,String>();

                    params.put("playlist_id",playlist_id);
                    params.put("username",username);

                    return params;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(stringRequest);

            return null;
        }

        public void check_for_error(String respo){

            switch (respo){

                case "failed_10":
                    Toast.makeText(context, "Error: FAIL_DEL_FAV_SCPJ", Toast.LENGTH_SHORT).show();
                    break;
                case "failed_20":
                    Toast.makeText(context, "Error: FAIL_DEL_PLIST_SCPJ", Toast.LENGTH_SHORT).show();
                    break;
                case "failed_30":
                    Toast.makeText(context, "Error: FAIL_GET_NM_SCPJ", Toast.LENGTH_SHORT).show();
                    break;
                case "failed_40":
                    Toast.makeText(context, "Error: FAIL_DEL_SNG_CSTM_SCPJ", Toast.LENGTH_SHORT).show();
                    break;
                case "failed_50":
                    Toast.makeText(context, "Error: FAIL_GET_UNAME_SCPJ", Toast.LENGTH_SHORT).show();
                    break;
                case "failed_60":
                    Toast.makeText(context, "Error: FAIL_GET_PLID_SCPJ", Toast.LENGTH_SHORT).show();
                    break;
                case "failed_70":
                    Toast.makeText(context, "Error: ACCESS_FAIL_DB_DENIAL", Toast.LENGTH_SHORT).show();
                    break;
                case "failed_80":
                    Toast.makeText(context, "Error: FAIL_UNLINK", Toast.LENGTH_SHORT).show();
                    break;
                case "failed_90":
                    Toast.makeText(context, "Error: PATH_NOT_FOUND", Toast.LENGTH_SHORT).show();
                    break;

            }

        }
    }

}