package com.musicstreaming.musicstreaming;

import android.content.Context;
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


public class internal_error_report extends AsyncTask<String,Void,Void> {

    String error,username,url="https://rentdetails.000webhostapp.com/musicplayer_files/error.php";
    Context context;

    public internal_error_report(Context context,String error, String username) {

        this.error=error;
        this.username=username;
        this.context=context;

    }

    @Override
    protected Void doInBackground(String... strings) {

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<String, String>();
                if(!username.equals("")) {
                    params.put("username", username);
                }
                params.put("error",error);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);

        return null;
    }
}
