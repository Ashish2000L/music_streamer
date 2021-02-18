package com.musicstreaming.musicstreaming;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import static com.musicstreaming.musicstreaming.login.SHARED_PREF;
import static com.musicstreaming.musicstreaming.login.USERNAME;

public class get_error_song extends AsyncTask<String,Void,Void> {

    Context context;
    SharedPreferences sharedPreferences;
    public static boolean RESPONSE_STATUS;
    String url="https://rentdetails.000webhostapp.com/musicplayer_files/songs_error_manager/get_error_detail_in_song.php",song_id,playlist_id;

    public get_error_song(Context context1, String song_id, String playlist_id) {
        this.context=context1;
        this.playlist_id=playlist_id;
        this.song_id=song_id;
        RESPONSE_STATUS=false;
    }

    @Override
    protected Void doInBackground(String... strings) {

        sharedPreferences=context.getSharedPreferences(SHARED_PREF,Context.MODE_PRIVATE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

//                Toast.makeText(context, "Response is "+response, Toast.LENGTH_SHORT).show();
                RESPONSE_STATUS=true;
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

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(stringRequest);

        return null;
    }
}
