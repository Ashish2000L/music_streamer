package com.musicstreaming.musicstreaming;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ShareCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Environment;
import android.os.Handler;
import android.text.Html;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.musicstreaming.musicstreaming.MainActivity.MAIN_ACTIVITY;
import static com.musicstreaming.musicstreaming.MainActivity.get_credits;
import static com.musicstreaming.musicstreaming.login.SHARED_PREF;
import static com.musicstreaming.musicstreaming.login.USERNAME;
import static com.musicstreaming.musicstreaming.service.onclearfrompercentservice.POSITION_FAV_PLAYLIST;
import static com.musicstreaming.musicstreaming.splash.DIR_NAME;


public class homefragment extends Fragment {
    /**
     * <h1>Ownership</h1>
     * <p>This Application is Owned by Ashish Kumar, Delhi Technology univerity, Btech ,Mechanical
     * Engineering ,2ndyear Student</p>
     * <p>Finished First version 1.0 on 17-Aug-2020</p>
     */
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
    boolean IS_IN_SEARCHVIEW=false;
    String SEARCH_VIEW_NAME;
    String TAG="loadingplaylist",TOKEN="TOKEN_FOR_IN_APP_MSG", urls="https://rentdetails.000webhostapp.com/musicplayer_files/showplaylist.php";

    String[] names_playlist;
    ArrayList<listofplaylist> arrayList=new ArrayList<>();

    public homefragment() {

        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_homefragment, container, false);

        Thread.setDefaultUncaughtExceptionHandler(new Exceptionhandler(MAIN_ACTIVITY));

        listviewforplaylist = view.findViewById(R.id.listofplaylist_listview);
        swipeRefreshLayout = view.findViewById(R.id.swipe_ref_listofplaylist);
        context=view.getContext();

        frombottom= AnimationUtils.loadAnimation(context,R.anim.frombottom);
        disapper=AnimationUtils.loadAnimation(context,R.anim.disapper);

        sharedPreferences=context.getSharedPreferences(SHARED_PREF,Context.MODE_PRIVATE);

        relativeLayout = view.findViewById(R.id.loading);
        lottieAnimationView = view.findViewById(R.id.animation_view);
        playlistadapter = new playlistadapter(view.getContext(),listofplaylistArrayList);
        listviewforplaylist.setAdapter(playlistadapter);

        final String url_for_dialogue="https://rentdetails.000webhostapp.com/musicplayer_files/In%20app%20Dialogue/dialogue_handling.php";
        new show_dialogue(BuildConfig.VERSION_CODE,sharedPreferences.getString(USERNAME,""),sharedPreferences.getInt(TOKEN,0),url_for_dialogue).execute();

        if(listofplaylistArrayList.isEmpty()){
            new getdata(1).execute(urls);
        }else{
            listviewforplaylist.startAnimation(frombottom);
        }

        Log.d("arraylistsize", "onCreateView: arraylistsize "+listofplaylistArrayList.size());

        listviewforplaylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(!IS_IN_SEARCHVIEW) {
                    POSITION_FAV_PLAYLIST=position;
                    startActivity(new Intent(context, songsfromplaylist.class)
                            .putExtra("positions", position)
                            .putExtra("imageurl", listofplaylistArrayList.get(position).getImage())
                            .putExtra("name", listofplaylistArrayList.get(position).getName())
                            .putExtra("id", listofplaylistArrayList.get(position).getId()));
                }else {
                    SEARCH_VIEW_NAME = names_playlist[position];
                    if (SEARCH_VIEW_NAME != null) {
                        position = find_pos(SEARCH_VIEW_NAME);
                        POSITION_FAV_PLAYLIST = position;
                        startActivity(new Intent(context, songsfromplaylist.class)
                                .putExtra("positions", position)
                                .putExtra("imageurl", listofplaylistArrayList.get(position).getImage())
                                .putExtra("name", listofplaylistArrayList.get(position).getName())
                                .putExtra("id", listofplaylistArrayList.get(position).getId()));

                    }else{
                        Toast.makeText(context, "ERR_NAM_NUL", Toast.LENGTH_SHORT).show();
                        new internal_error_report(context,"ERROR NULL POINT IN find_pos: size= "+names_playlist.length+" position: "+position,sharedPreferences.getString(USERNAME,"")).execute();
                        startActivity(new Intent(context,MainActivity.class));
                    }
                }

            }
        });

        setHasOptionsMenu(true);
        listviewforplaylist.setTextFilterEnabled(true);

        swipeRefreshLayout.setColorSchemeColors(Color.CYAN);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new getdata(1).execute(urls);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return view;
    }

    public int find_pos(String name){
        for(int i=0;i<listofplaylistArrayList.size();i++){
            try {
                if (name.equals(listofplaylistArrayList.get(i).getName())) {
                    return i;
                }
            }catch (Exception e){
                Toast.makeText(context, "ERR_UNKN_fND_POS", Toast.LENGTH_SHORT).show();
                new internal_error_report(context,"ERROR find_pos, homefragment : "+e.getMessage(),sharedPreferences.getString(USERNAME,"")).execute();
            }
        }
        return 1000;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.home_frag_menu_main,menu);

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

                    //playlistadapter1.clear();
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
                            Log.d("ellos", "onQueryTextChange: called +" + newText);

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
            if(item.getItemId()==R.id.share){

                String sharebody = sharedPreferences.getString("share_msg","");

                ShareCompat.IntentBuilder.from(MAIN_ACTIVITY)
                        .setType("text/plain")
                        .setChooserTitle("Share Via")
                        .setSubject("Join me on Music Streaming Now!")
                        .setText(sharebody)
                        .startChooser();

            }
            else
                if(item.getItemId()==R.id.contactus){
                        Intent intent = new Intent (Intent.ACTION_SEND);
                        intent.setType("message/rfc822");
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"musicstreamingforall@gmail.com"});
                        intent.setPackage("com.google.android.gm");
                        if (intent.resolveActivity(context.getPackageManager())!=null)
                            startActivity(intent);
                        else
                            Toast.makeText(context,"Gmail App is not installed",Toast.LENGTH_LONG).show();
                }else
                    if(item.getItemId()==R.id.credits){

                        get_credits(MAIN_ACTIVITY);

                }


        return true;
    }

    public void filter(String text){
        IS_IN_SEARCHVIEW=true;
        names_playlist=new String[arrayList.size()];
        text=text.toLowerCase(Locale.getDefault());
        Log.d("ellos", "onQueryTextChange: called text "+text);

        playlistadapter1.clear();
        arrayList=listofplaylistArrayList;
        //playlistadapter1=playlistadapter;
        if(text.length()==0){
            for (int i=0;i<arrayList.size();i++){
                names_playlist[i]=arrayList.get(i).getName();
            }

        }else {

            Log.d("ellos", "onQueryTextChange: called cool "+arrayList.size());
            int k=0;
            for ( int i=0;i<arrayList.size();i++){
                Log.d("ellos", "filter: "+arrayList.get(i).getName().toLowerCase(Locale.getDefault()));
                if(arrayList.get(i).getName().toLowerCase(Locale.getDefault()).contains(text)){
                    Log.d("ellos", "filter: "+arrayList.get(i).getName());
                    playlistadapter1.add(arrayList.get(i));
                    names_playlist[k]=arrayList.get(i).getName();
                    k++;
                }
            }
        }
        playlistadapter1.notifyDataSetChanged();
    }

    public class getdata extends AsyncTask<String ,Void,Void>{
        String message="";
        int number=0,loop;

        public getdata(int loop) {
            this.loop = loop;
        }

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
            Log.d(TAG, "doInBackground: url is "+url);

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
                                String totl_like = object.getString("totl_like");
                                Log.d(TAG, "onResponse: id is "+id);

                                //String customurl = "https://rentdetails.000webhostapp.com/musicplayer_files/musicimages/"+image;
                                //Toast.makeText(ListOfRentersForAdmin.this, usernames, Toast.LENGTH_LONG).show();
                                listofplaylist = new listofplaylist(id,name,image,likes,note,totl_like);
                                listofplaylistArrayList.add(listofplaylist);
                                playlistadapter.notifyDataSetChanged();
                            }
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
                    if(error.getMessage()!=null){
                        Log.d(TAG, "onErrorResponse: error in connection is "+error.getMessage());
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }else{
                        Log.d(TAG, "onErrorResponse: null error found ");
                    }

                    String err="Error in getdata in homefragment "+error.getMessage();
                    new internal_error_report(context,err,MainActivity.sharedPreferences.getString(USERNAME,"")).execute();

                    if(loop>0)
                    new getdata(loop-1).execute(urls);

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
                        //relativeLayout.startAnimation(disapper);
                        relativeLayout.setVisibility(View.GONE);
                    }else{
                        Log.d(TAG, "run: running for : "+number++);
                        handler.postDelayed(this,1000);
                    }
                }
            };
            runnable.run();
        }
    }


    // for showing custom dialogue
    public void custom_dialod(String title, String bodys, int token){
        TextView close,heading,body;
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        View mview = getLayoutInflater().inflate(R.layout.custom_dialogue_inappmessaging,null);

        close = mview.findViewById(R.id.close_dialogue);
        heading = mview.findViewById(R.id.title_for_dialogue);
        body = mview.findViewById(R.id.body_for_dialogue);

        alert.setView(mview);

        heading.setText(Html.fromHtml(title));
        body.setText(Html.fromHtml(bodys));

        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(TOKEN,token);
        editor.apply();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();

    }

    public class show_dialogue extends AsyncTask<Void,Void,Void>{

        int version, token;
        String username,message="",url;
        public show_dialogue(int version, String username, int token,String url) {

            this.username=username;
            this.token=token;
            this.version=version;
            this.url=url;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    message="progress done";

                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String success = jsonObject.getString("success");
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        if (success.equals("1")) {
                                JSONObject object = jsonArray.getJSONObject(0);

                                String title = object.getString("title");
                                String body = object.getString("body");
                                String token = object.getString("token");

                            Log.d(TAG, "onResponse: title & body "+title+body);
                                if(!title.equals("not")){

                                    if(new File(Environment.getExternalStorageDirectory()+"/"+DIR_NAME,"versions").exists()) {
                                        custom_dialod(title, body, Integer.parseInt(token));
                                    }else{
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putInt(TOKEN,Integer.parseInt(token));
                                        editor.apply();

                                    }

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
                    message="error in fetching playlist";
                    if(error.getMessage()!=null){
                        Log.d(TAG, "onErrorResponse: error in connection is "+error.getMessage());
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }else{
                        Log.d(TAG, "onErrorResponse: null error found ");
                    }

                    String err="Error in getdata in homefragment "+error.getMessage();
                    new internal_error_report(context,err,MainActivity.sharedPreferences.getString(USERNAME,"")).execute();

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    if (!sharedPreferences.getString(USERNAME,"").equals("")) {
                        params.put("username", sharedPreferences.getString(USERNAME,""));
                    }

                    params.put("version",version+"");
                    params.put("token",token+"");

                    return params;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(request);

            return null;
        }
    }

}
