package com.example.musicstreaming;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import static com.example.musicstreaming.login.SHARED_PREF;
import static com.example.musicstreaming.login.USERNAME;
import static com.example.musicstreaming.service.onclearfrompercentservice.TAG;
import static com.example.musicstreaming.songsfromplaylist.listofsongsArrayLisr;

public class search_song_fragment extends Fragment {

    ListView custom_listview;
    list_of_all_songs songlist;
    all_song_adaptor songsadaptor,songadapter1;
    public static ArrayList<list_of_all_songs> listofallsongsArrayList = new ArrayList<>();
    Context context;
    SearchView searchView;
    boolean IS_IN_SEARCHVIEW=false;
    ArrayList<list_of_all_songs> arrayList = new ArrayList<>();
    String urls="https://rentdetails.000webhostapp.com/musicplayer_files/get_all_songs.php";
    String[] names_playlist;
    MenuItem mitem;
    SharedPreferences sharedPreferences;

    public search_song_fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_search_song_fragment,container,false);

        Thread.setDefaultUncaughtExceptionHandler(new Exceptionhandler(make_custom_playlist.MAKE_NEW_PLAYLIST));

        context=view.getContext();

        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        sharedPreferences=context.getSharedPreferences(SHARED_PREF,Context.MODE_PRIVATE);

        custom_listview=view.findViewById(R.id.search_song_listview);
        setHasOptionsMenu(true);
        custom_listview.setTextFilterEnabled(true);

        songsadaptor = new all_song_adaptor(view.getContext(),listofallsongsArrayList);
        custom_listview.setAdapter(songsadaptor);

        new getdata().execute(urls);

        custom_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int positions=position;
                if(!IS_IN_SEARCHVIEW) {
                    startActivity(new Intent(context,songsfromplaylist.class)
                            .putExtra("is_from_search",true)
                    .putExtra("song_url",listofallsongsArrayList.get(positions).getUrl()));
                }else{
                    String SEARCH_VIEW_NAME=names_playlist[position];

                    if(SEARCH_VIEW_NAME==null){
                        positions=position;
                    }else {
                        positions = find_pos(SEARCH_VIEW_NAME);
                    }

                    try {
                        startActivity(new Intent(context,songsfromplaylist.class)
                                .putExtra("is_from_search",true)
                                .putExtra("song_url",listofallsongsArrayList.get(positions).getUrl()));
                    }catch (Exception e){

                        Toast.makeText(context, "Failed "+e.getMessage(), Toast.LENGTH_LONG).show();

                    }
                }
            }
        });

        return view;
    }

    public int find_pos(String name){
        for(int i=0;i<listofallsongsArrayList.size();i++){
            if(name.equals(listofallsongsArrayList.get(i).getName())){
                return i;
            }
        }
        return 1000;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.home_frag_menu,menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        mitem=item;

            searchView = (SearchView) item.getActionView();
            searchView.setQueryHint("Search Your Songs");
            searchView.setIconifiedByDefault(false);
            searchView.setIconified(false);


//            searchView.onActionViewExpanded();

            item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    IS_IN_SEARCHVIEW = true;

                    if (songadapter1 == null) {
                        songadapter1 = new all_song_adaptor(context, arrayList);
                    } else {
                        songadapter1 = null;
                        arrayList = new ArrayList<>();
                        songadapter1 = new all_song_adaptor(context, arrayList);
                    }

                    if (arrayList.isEmpty()) {
                        arrayList.addAll(listofallsongsArrayList);
                    } else {
                        arrayList = new ArrayList<>();
                        arrayList.addAll(listofallsongsArrayList);
                    }

                    custom_listview.setAdapter(songadapter1);
                    custom_listview.cancelLongPress();

                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {

                            if (TextUtils.isEmpty(newText)) {
                                IS_IN_SEARCHVIEW=false;
                                filter("/././.");
                            } else {
                                IS_IN_SEARCHVIEW=true;
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
                    custom_listview.setAdapter(songsadaptor);
                    songsadaptor.notifyDataSetChanged();
//                    startActivity(new Intent(context,MainActivity.class));
                    return true;
                }
            });
        return true;
    }

    public void filter(String text){

        names_playlist=new String[arrayList.size()];
        text=text.toLowerCase(Locale.getDefault());
        Log.d("ellos", "onQueryTextChange: called text "+text);

        songadapter1.clear();
        arrayList=listofallsongsArrayList;
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
                    songadapter1.add(arrayList.get(i));
                    names_playlist[k]=arrayList.get(i).getName();
                    k++;
                }
            }
        }
        songadapter1.notifyDataSetChanged();
    }

    public class getdata extends AsyncTask<String ,Void,Void> {
        String message="";
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Please wait ...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {

            String url=strings[0];
            Log.d(TAG, "doInBackground: url is "+url);

            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    listofallsongsArrayList.clear();
                    message="progress done";
                    progressDialog.dismiss();
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String success = jsonObject.getString("success");
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        if (success.equals("1")) {
                            for (int i =0 ; i <jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);

                                String id = object.getString("id");
                                String name = object.getString("name");
                                String image = object.getString("image");
                                String url = object.getString("url");
                                String singer = object.getString("singer");
                                String bkcolor = object.getString("bkcolor");//background colour when the song is playing;

                                songlist= new list_of_all_songs(id,name,image,url,singer,bkcolor);
                                listofallsongsArrayList.add(songlist);
                                Collections.shuffle(listofallsongsArrayList,new Random());
                                songsadaptor.notifyDataSetChanged();
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
                    progressDialog.dismiss();
                    if(error.getMessage()!=null){

                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }else{

                        Toast.makeText(context, "Error occured!!", Toast.LENGTH_SHORT).show();
                    }

                    Log.d(TAG, "onErrorResponse: "+ error.networkResponse.statusCode);

                    String err="Error in getdata in homefragment "+error.getMessage();
                    new internal_error_report(context,err,MainActivity.sharedPreferences.getString(USERNAME,"")).execute();

                }
            }){

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String,String> params = new HashMap<String, String>();

                    params.put("is_search","1");

                    return params;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(request);

            return null;
        }
    }

//    public class get_songs extends AsyncTask<String ,Void,Void> {
//        String message="";
//        ProgressDialog progressDialog;
//
//        @Override
//        protected void onPreExecute() {
//            progressDialog = new ProgressDialog(context);
//            progressDialog.setMessage("Please wait ...");
//            progressDialog.setCanceledOnTouchOutside(false);
//            progressDialog.setCancelable(false);
//            progressDialog.show();
//            super.onPreExecute();
//        }
//
//        @Override
//        protected Void doInBackground(String... strings) {
//
//            String url=strings[0];
//            final String song_url=strings[1];
//            Log.d(TAG, "doInBackground: url is "+url);
//
//            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//                    listofallsongsArrayList.clear();
//                    message="progress done";
//                    progressDialog.dismiss();
//                    try {
//
//                        JSONObject jsonObject = new JSONObject(response);
//                        String success = jsonObject.getString("success");
//                        String playlist_name=jsonObject.getString("name");
//                        String note = jsonObject.getString("note");
//                        JSONArray jsonArray = jsonObject.getJSONArray("data");
//                        if (success.equals("1")) {
//                            for (int i =0 ; i <jsonArray.length(); i++) {
//                                JSONObject object = jsonArray.getJSONObject(i);
//
//                                String id = object.getString("id");
//                                String name = object.getString("name");
//                                String image = object.getString("image");
//                                String url = object.getString("url");
//                                String like = object.getString("like");
//                                String singer = object.getString("singer");
//                                String bkcolor = object.getString("bkcolor");//background colour when the song is playing;
//
//                                listofsongs listofsong = new listofsongs(id,name,url,image,like,singer,bkcolor);
//                                listofsongsArrayLisr.add(listofsong);
//                            }
//
//                            startActivity(new Intent(context,playselectedsong.class)
//                            .putExtra("playlistname",playlist_name)
//                            .putExtra("is_from_search",true));
//
//                        }
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        message="failed ";
//                        //new erroinfetch().execute(e.getMessage());
//                        Log.d(TAG, "onResponse: error in json is "+e.getMessage());
//                    }
//
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    message="error in fetching playlist";
//                    progressDialog.dismiss();
//                    if(error.getMessage()!=null){
//
//                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
//                    }else{
//
//                        Toast.makeText(context, "Error occured!!", Toast.LENGTH_LONG).show();
//                    }
//
//                    String err="Error in getdata in homefragment "+error.getMessage();
//                    new internal_error_report(context,err,MainActivity.sharedPreferences.getString(USERNAME,"")).execute();
//
//                }
//            }){
//
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//
//                    Map<String,String> params = new HashMap<String, String>();
//
//                    params.put("url",song_url);
//                    params.put("username",sharedPreferences.getString(USERNAME,""));
//
//                    return params;
//                }
//            };
//            RequestQueue queue = Volley.newRequestQueue(context);
//            queue.add(request);
//
//            return null;
//        }
//    }
}