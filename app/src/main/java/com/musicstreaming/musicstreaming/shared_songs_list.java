package com.musicstreaming.musicstreaming;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.musicstreaming.musicstreaming.service.online_status_updater;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.musicstreaming.musicstreaming.login.SHARED_PREF;
import static com.musicstreaming.musicstreaming.login.USERNAME;
import static com.musicstreaming.musicstreaming.service.online_status_updater.getSharedSongs.counts;
import static com.musicstreaming.musicstreaming.service.online_status_updater.shareSongItems;
import static com.musicstreaming.musicstreaming.service.online_status_updater.shareSongItemsArrayList;
import static com.musicstreaming.musicstreaming.service.online_status_updater.share_song_itemsArrayAdapter;

public class shared_songs_list extends Fragment {

    String url="https://rentdetails.000webhostapp.com/musicplayer_files/share_song/show_shared_song.php";
    public static TextView showText;
    public static ListView shareSongList;
    Context context;
    SharedPreferences sharedPreferences;
    public static Context SHARE_SONG_CONTEXT;

    public shared_songs_list() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_shared_songs_list, container, false);

        context=view.getContext();

        shareSongList=view.findViewById(R.id.list_of_shared_songs);
        showText=view.findViewById(R.id.show_text);

        sharedPreferences=context.getSharedPreferences(SHARED_PREF,Context.MODE_PRIVATE);

        share_song_itemsArrayAdapter=new share_song_adaptor(context,shareSongItemsArrayList);
        shareSongList.setAdapter(share_song_itemsArrayAdapter);

        if(counts>0){
            shareSongList.setVisibility(View.VISIBLE);
            showText.setVisibility(View.GONE);
        }else{
            shareSongList.setVisibility(View.GONE);
            showText.setVisibility(View.VISIBLE);
        }
//        new online_status_updater.getSharedSongs().execute();

        return  view;
    }

//    public class getSharedSongs extends AsyncTask<Void,Void,Void>{
//
//        String message="";
//        ProgressDialog progressDialog;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//            progressDialog=new ProgressDialog(context);
//            progressDialog.setMessage("Please Wait....");
//            progressDialog.setCancelable(false);
//            progressDialog.show();
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//
//            final String username=sharedPreferences.getString(USERNAME,"");
//
//            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//                    shareSongItemsArrayList.clear();
//                    message="progress done";
//                    progressDialog.dismiss();
//
//                    try {
//
//                        JSONObject jsonObject = new JSONObject(response);
//                        String success = jsonObject.getString("success");
//                        int counts=Integer.parseInt(jsonObject.getString("count"));
//                        if(counts>0) {
//                            shareSongList.setVisibility(View.VISIBLE);
//                            showText.setVisibility(View.GONE);
//                            JSONArray jsonArray = jsonObject.getJSONArray("data");
//                            if (success.equals("1")) {
//                                for (int i = 0; i < jsonArray.length(); i++) {
//                                    JSONObject object = jsonArray.getJSONObject(i);
//
//                                    String id = object.getString("id");
//                                    String frduser = object.getString("frduser");
//                                    String songid = object.getString("songid");
//                                    String status = object.getString("status");
//                                    String sendtime = object.getString("sendtime");
//                                    String songname = object.getString("songname");
//                                    String singer = object.getString("singer");
//                                    String url = object.getString("url");
//                                    String name = object.getString("name");
//
//                                    shareSongItems = new share_song_items(id,name,sendtime,songname,singer,frduser,songid,url,status);
//                                    shareSongItemsArrayList.add(shareSongItems);
//                                    share_song_itemsArrayAdapter.notifyDataSetChanged();
//                                }
//                            }
//                        }else{
//                            shareSongList.setVisibility(View.GONE);
//                            showText.setVisibility(View.VISIBLE);
//                        }
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        message="failed ";
//                        startActivity(new Intent(context,MainActivity.class));
//                        Log.d("getData", "onResponse: error in json is "+e.getMessage());
//                    }
//
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    message="error in fetching playlist";
//                    progressDialog.dismiss();
//
//                    if(error.getMessage()!=null){
//                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//
//                    String err="Error in getdata in playlistfragment "+error.getMessage();
//                    new internal_error_report(context,err,MainActivity.sharedPreferences.getString(USERNAME,"")).execute();
//
//                }
//            }){
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    Map<String,String> params=new HashMap<String, String>();
//
//                    params.put("username", username);
//
//                    return params;
//                }
//            };
//
//            RequestQueue queue = Volley.newRequestQueue(context);
//            queue.add(request);
//
//            return null;
//        }
//    }

}