package com.musicstreaming.musicstreaming;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.logging.Handler;

import static com.musicstreaming.musicstreaming.login.SHARED_PREF;
import static com.musicstreaming.musicstreaming.login.USERNAME;
import static com.musicstreaming.musicstreaming.service.onclearfrompercentservice.TAG;
import static com.musicstreaming.musicstreaming.splash.SPLASH_ACTIVITY;

public class checkserveravailability extends AsyncTask<Void,Void,Void> {
    /**
     * <h1>Ownership</h1>
     * <p>This Application is Owned by Ashish Kumar, Delhi Technology univerity, Btech ,Mechanical
     * Engineering ,2ndyear Student</p>
     * <p>Finished First version 1.0 on 17-Aug-2020</p>
     */
    String url="https://rentdetails.000webhostapp.com/musicplayer_files/checkserverconnection.php";
    public static boolean iscomplete=false;
    Context context;
    Handler handler;
    public static boolean connection=false;
    public static String strings="";

    public checkserveravailability(Context context) {
        this.context = context;

        Thread.setDefaultUncaughtExceptionHandler(new Exceptionhandler(SPLASH_ACTIVITY));

    }

    @Override
    protected Void doInBackground(Void... voids) {
        final SharedPreferences sharedPreferences = SPLASH_ACTIVITY.getSharedPreferences(SHARED_PREF,Context.MODE_PRIVATE);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                iscomplete=true;
                strings=response;
                Log.d(TAG, "onResponse: response is "+response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                iscomplete=true;
                strings="Connection Failed!!";

                String err="Error in checkserveravailability class"+error.getMessage();
                new internal_error_report(context,err,sharedPreferences.getString(USERNAME,"Unknown")).execute();

            }
        });

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        final android.os.Handler handler = new android.os.Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {

                if (iscomplete){
                    if(strings!=null){
                        Log.d(TAG, "run: the response is "+strings);
                        if(strings.equals("you are connected")) {
                            connection = true;
                            Log.d(TAG, "run: connection is positive " + connection);
                        }else{
                            connection=false;
                            Log.d(TAG, "run: this is negative "+strings);
                        }
                    }else{
                        connection=false;
                        Log.d(TAG, "run: connection is negative "+connection);
                    }
                }else{
                    handler.postDelayed(this,500);
                    Log.d(TAG, "run: running dandler toma ");
                }
            }
        };
        runnable.run();
    }
}
