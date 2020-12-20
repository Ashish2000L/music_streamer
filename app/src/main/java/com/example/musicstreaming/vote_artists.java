package com.example.musicstreaming;

import android.accessibilityservice.GestureDescription;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import static com.example.musicstreaming.login.SHARED_PREF;
import static com.example.musicstreaming.login.USERNAME;

public class vote_artists extends AsyncTask<String,Void,Void> {

    Context context;
    SharedPreferences sharedPreferences;
    String url="",song_id,playlist_id;
    public vote_artists(Context context1, String song_id,String playlist_id) {
        this.context=context1;
        this.playlist_id=playlist_id;
        this.song_id=song_id;
    }

    @Override
    protected Void doInBackground(String... strings) {

        sharedPreferences=context.getSharedPreferences(SHARED_PREF,Context.MODE_PRIVATE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String err="Error in checkserveravailability class"+error.getMessage();
                new internal_error_report(context,err,sharedPreferences.getString(USERNAME,"Unknown")).execute();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> params=new HashMap<String, String>();

                params.put("username",sharedPreferences.getString(USERNAME,""));
                params.put("song_id",song_id);
                params.put("playlist_id",playlist_id);

                return params;
            }
        };

        return null;
    }
}
