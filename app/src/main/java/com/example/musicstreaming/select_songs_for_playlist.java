package com.example.musicstreaming;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import static com.example.musicstreaming.homefragment.listofplaylistArrayList;
import static com.example.musicstreaming.login.USERNAME;
import static com.example.musicstreaming.service.onclearfrompercentservice.POSITION_FAV_PLAYLIST;
import static com.example.musicstreaming.service.onclearfrompercentservice.TAG;

public class select_songs_for_playlist extends Fragment {

    ListView custom_listview;
    list_of_all_songs songlist;
    all_song_adaptor songsadaptor,songadapter1;
    public static ArrayList<list_of_all_songs> listofallsongsArrayList = new ArrayList<>();
    Context context;
    SearchView searchView;
    boolean IS_IN_SEARCHVIEW=false;
    ArrayList<list_of_all_songs> arrayList = new ArrayList<>();
    ArrayList<list_of_all_songs> playlist_songs = new ArrayList<>();
    String urls="https://rentdetails.000webhostapp.com/musicplayer_files/get_all_songs.php";
    String[] names_playlist;
    MenuItem mitem;
    String PLAYLIST_IDS;

    public select_songs_for_playlist() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_songs_for_playlist, container, false);

        Thread.setDefaultUncaughtExceptionHandler(new Exceptionhandler(make_custom_playlist.MAKE_NEW_PLAYLIST));

        context=view.getContext();

        Intent intent = getActivity().getIntent();
        PLAYLIST_IDS=intent.getExtras().getString("PLAYLIST_ID","");

        custom_listview=view.findViewById(R.id.make_custom_playlist_listview);
        setHasOptionsMenu(true);
        custom_listview.setTextFilterEnabled(true);

        make_new_playlist();

        return view;
    }

    // function for making new playlist
    public void make_new_playlist()
    {
        songsadaptor=new all_song_adaptor(context, playlist_songs);
        custom_listview.setAdapter(songsadaptor);

        if(!playlist_songs.isEmpty())
        {
            playlist_songs.clear();
        }
        new getdata().execute(urls);

        //select the song from the filtered & unfiltered listview
        custom_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(!IS_IN_SEARCHVIEW) {
                    POSITION_FAV_PLAYLIST=position;

                    if(position<playlist_songs.size()){
                        Toast.makeText(context, "Selected " + playlist_songs.get(position).getName(), Toast.LENGTH_LONG).show();
                    }

                }else{
                    try {
                        if (urls.equals("/././.")){

                            if(playlist_songs.size()<=30) {

                                playlist_songs.add(new list_of_all_songs(listofallsongsArrayList.get(position).getId(), listofallsongsArrayList.get(position).getName(), listofallsongsArrayList.get(position).getImage(),
                                        listofallsongsArrayList.get(position).getUrl(), listofallsongsArrayList.get(position).getSinger(), listofallsongsArrayList.get(position).getBkcolor()));

                                listofallsongsArrayList.remove(position);

                            }else{

                                Toast.makeText(context, "Maximum limit reached! Try removing songs", Toast.LENGTH_SHORT).show();

                            }

                        }else {
                            String SEARCH_VIEW_NAME = names_playlist[position];
                            position = find_pos(SEARCH_VIEW_NAME);
                            POSITION_FAV_PLAYLIST = position;

                            if (position != 1000) {

                                if(playlist_songs.size()<=30) {
                                    playlist_songs.add(new list_of_all_songs(listofallsongsArrayList.get(position).getId(), listofallsongsArrayList.get(position).getName(), listofallsongsArrayList.get(position).getImage(),
                                            listofallsongsArrayList.get(position).getUrl(), listofallsongsArrayList.get(position).getSinger(), listofallsongsArrayList.get(position).getBkcolor()));

                                    listofallsongsArrayList.remove(position);
                                }else{

                                    Toast.makeText(context, "Maximum limit reached! Try removing songs", Toast.LENGTH_SHORT).show();

                                }

                            }
                        }
                    }catch (Exception e){

                        Toast.makeText(context, "Fail to make your request!", Toast.LENGTH_SHORT).show();
                    }

                    mitem.collapseActionView();

                }

            }
        });


        // to remove the selected songs from the playlist
        custom_listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                String[] st={"Remove"};

                AlertDialog.Builder alert = new AlertDialog.Builder(context)
                        .setCancelable(true)
                        .setItems(st, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if(which==0){

                                    listofallsongsArrayList.add(new list_of_all_songs(playlist_songs.get(position).getId(),playlist_songs.get(position).getName(),playlist_songs.get(position).getImage()
                                    ,playlist_songs.get(position).getUrl(),playlist_songs.get(position).getSinger(),playlist_songs.get(position).getBkcolor()));
                                    playlist_songs.remove(position);
                                    songsadaptor.notifyDataSetChanged();

                                }

                            }
                        });
                alert.show();

                return false;
            }
        });

    }

    //getting value of the selected item after having filetred listview
    public int find_pos(String name){
        try {
            for (int i = 0; i < listofallsongsArrayList.size(); i++) {
                if (name.equals(listofallsongsArrayList.get(i).getName())) {
                    return i;
                }
            }
        }catch (Exception e){
            Toast.makeText(context,"Unable to locate selection, try making another selection!",Toast.LENGTH_LONG).show();
        }
        return 1000;
    }

    //inflating menu
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.select_songs_from_playlist_menu,menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    //handeler for the menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        mitem=item;

        if(item.getItemId()==R.id.search_playlist) {
            searchView = (SearchView) item.getActionView();
            searchView.setQueryHint("Search Your Songs");
            searchView.setIconifiedByDefault(false);
            searchView.setIconified(false);
            searchView.scheduleLayoutAnimation();

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

                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {

                            if (TextUtils.isEmpty(newText)) {
                                filter("/././.");
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
                    custom_listview.setAdapter(songsadaptor);
                    songsadaptor.notifyDataSetChanged();
                    return true;
                }
            });
        }else
            if(item.getItemId()==R.id.done_selecting_song){

            if(playlist_songs.size()<3){

                Toast.makeText(context, "Select minimum 3 songs", Toast.LENGTH_SHORT).show();

            }else{

                String str=make_array_for_the_song_id();
//                Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
                if(!PLAYLIST_IDS.equals("")) {
                    new send_playlist("https://rentdetails.000webhostapp.com/musicplayer_files/set_custom_playlist.php", PLAYLIST_IDS, str).execute();
                }else{
                    Toast.makeText(context, "error: NOT_FOUND_PLAYLIST_SSFPJ", Toast.LENGTH_SHORT).show();
                }
            }

        }
        return true;
    }

    //making array of the selected songs id
    public String make_array_for_the_song_id(){

    String str="";
    int i=0;

    for( list_of_all_songs val :playlist_songs){
        i++;
        str+=val.getId();
        if(i<playlist_songs.size()){
            str+=",";
        }
    }
        return str;
    }

    //filtering the text with each change in the char
    public void filter(String text){
        names_playlist=new String[arrayList.size()];
        text=text.toLowerCase(Locale.getDefault());

        urls=text;

        songadapter1.clear();
        arrayList= listofallsongsArrayList;

        if(text.length()==0){

            for (int i=0;i<arrayList.size();i++){
                names_playlist[i]=arrayList.get(i).getName();
            }

        }else {

            int k=0;
            for ( int i=0;i<arrayList.size();i++){

                if(arrayList.get(i).getName().toLowerCase(Locale.getDefault()).contains(text)){

                    songadapter1.add(arrayList.get(i));
                    names_playlist[k]=arrayList.get(i).getName();
                    k++;
                }
            }
        }
        songadapter1.notifyDataSetChanged();
    }

    //getting all songs listed on the server
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

                                Log.d(TAG, "onResponse: id is "+id);

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

                    String err="Error in getdata in homefragment "+error.getMessage();
                    new internal_error_report(context,err,MainActivity.sharedPreferences.getString(USERNAME,"")).execute();

                }
            });
            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(request);

            return null;
        }
    }

    //sending playlist data to the server
    public class send_playlist extends AsyncTask<String,Void,Void>{

        String url,playlist_id,playlist;
        ProgressDialog progressDialog;
        public send_playlist(String url, String playlist_id,String playlist) {

            this.url=url;
            this.playlist_id=playlist_id;
            this.playlist=playlist;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Making your playlist ...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {

            final StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    progressDialog.dismiss();
                    if(response.equals("")) {
                        startActivity(new Intent(context, MainActivity.class));
                    }else{
                        Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    progressDialog.dismiss();
                    if(error.getMessage()!=null){

                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }else{

                        Toast.makeText(context, "Error occured!!", Toast.LENGTH_SHORT).show();
                    }

                    String err="Error in getdata in homefragment "+error.getMessage();
                    new internal_error_report(context,err,MainActivity.sharedPreferences.getString(USERNAME,"")).execute();

                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String ,String>();

                    params.put("playlist_id",playlist_id);
                    if(!MainActivity.sharedPreferences.getString(USERNAME,"").equals("")) {
                        params.put("username", MainActivity.sharedPreferences.getString(USERNAME, ""));
                    }
                    params.put("playlist",playlist);

                    return params;
                }
            };;
            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(request);

            return null;
        }
    }

}