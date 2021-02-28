package com.musicstreaming.musicstreaming;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.musicstreaming.musicstreaming.login.SHARED_PREF;
import static com.musicstreaming.musicstreaming.login.USERNAME;

public class Exceptionhandler implements Thread.UncaughtExceptionHandler {
    /**
     * <h1>Ownership</h1>
     * <p>This Application is Owned by Ashish Kumar, Delhi Technology univerity, Btech ,Mechanical
     * Engineering ,2ndyear Student</p>
     * <p>Finished First version 1.0 on 17-Aug-2020</p>
     */

    Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
    public static Activity activity;
    String model,ids,brand,url="https://rentdetails.000webhostapp.com/musicplayer_files/uncaughterror.php";
    String report;
    public static String ERROR_REPORT="error";
    SharedPreferences sharedPreferences;

    public Exceptionhandler( Activity activity) {
        this.uncaughtExceptionHandler =Thread.getDefaultUncaughtExceptionHandler();
        Exceptionhandler.activity = activity;
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {

        model= Build.MODEL;
        ids=Build.ID;
        brand=Build.BRAND;
        sharedPreferences=activity.getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        StackTraceElement[] arr=e.getStackTrace();
        report=e.toString()+"\n\n";
        report+="----------------------------------------\n\n";
        for(int i=0;i<arr.length;i++){
            report+="   "+arr[i].toString()+"\n";
        }

        report+="-------------cause------------------------\n\n";
        Throwable cause=e.getCause();
        if(cause!=null){
            report+=cause.toString()+"\n\n";
            arr=cause.getStackTrace();
            for(int i=0;i<arr.length;i++){
                report+="    "+arr[i].toString()+"\n";
            }
        }

        Toast.makeText(activity, "Send error report", Toast.LENGTH_SHORT).show();
        try{
            FileOutputStream fos=activity.openFileOutput("stack.trace", MODE_PRIVATE);
            fos.write(report.getBytes());
            fos.close();
            editor.putBoolean(ERROR_REPORT,true);
            editor.apply();
            Log.d("thisisanerrorsochill", "uncaughtException: file created ");
        }catch (IOException ex){
            ex.printStackTrace();
            Log.d("thisisanerrorsochill", "uncaughtException: error is "+ex.getMessage());
        }
        activity.finishAffinity();
        Log.d("thisisanerrorsochill", "uncaughtException: "+report);
        //new senderroreport().execute("https://rentdetails.000webhostapp.com/musicplayer_files/uncaughterror.php");



        if(activity.deleteFile("stack.trace")){
            Toast.makeText(activity, "filee deleted", Toast.LENGTH_SHORT).show();
            Log.d("thisisanerrorsochill", "uncaughtException: file deleted");
        }
        //Log.d("thisisanerrorsochill", "doInBackground: "+url);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("thisisanerrorsochill", "onErrorResponse: error is "+error.getMessage());

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("brand",brand);
                params.put("mobileid",ids);
                params.put("model",model);
                params.put("username",sharedPreferences.getString(splash.USERNAME,""));
                params.put("error",report);

                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(activity);
        queue.add(request);
        //uncaughtException(t,e);
    }

//    public static class senderroreport extends AsyncTask<String,String,Void>{
//
//        String model,ids,brand,error="";
//        String line;
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            Log.d("thisisanerrorsochill", "onPreExecute: calledthid");
//            model= Build.MODEL;
//            ids=Build.ID;
//            brand=Build.BRAND;
//
//            try {
//                BufferedReader reader = new BufferedReader(new InputStreamReader(activity.openFileInput("stack.trace")));
//
//                while ((line=reader.readLine())!=null){
//                    error+=line+"\n";
//                }
//            } catch (FileNotFoundException ex){
//                new senderroreport().cancel(true);
//                Log.d("thisisanerrorsochill", "onPreExecute: error is "+ex.getMessage());
//            } catch (IOException e){
//                new senderroreport().cancel(true);
//                Log.d("thisisanerrorsochill", "onPreExecute: error is "+e.getMessage());
//            }
//
//        }
//
//        @Override
//        protected Void doInBackground(String... strings) {
//
//
//            final String url=strings[0];
//            //activity.deleteFile("stack.trace");
//            Log.d("thisisanerrorsochill", "doInBackground: error is \n"+error);
//            SharedPreferences sharedPreferences = activity.getSharedPreferences(SHARED_PREF,MODE_PRIVATE);
//            final SharedPreferences.Editor  editor = sharedPreferences.edit();
//            Log.d("thisisanerrorsochill", "doInBackground: "+url);
//            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//
//                    editor.putBoolean(ERROR_REPORT,false);
//                    editor.apply();
//                    Log.d("thisisanerrorsochill", "onResponse: your response is "+response);
//
//                    if(activity.deleteFile("stack.trace")){
//                        Log.d("thisisanerrorsochill", "onResponse: deleted file");
//                    }
//                    onProgressUpdate("Error Report sent!");
//                    //activity.finishAffinity();
//
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Log.d("thisisanerrorsochill", "onErrorResponse: error is "+error.getMessage());
//
//                    editor.putBoolean(ERROR_REPORT,true);
//                    editor.apply();
//
//                    activity.deleteFile("stack.trace");
//                    onProgressUpdate("Failed to send error Report!");
//                    //activity.finishAffinity();
//                }
//            }){
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    Map<String,String> params = new HashMap<String, String>();
//                    params.put("brand",brand);
//                    params.put("mobileid",ids);
//                    params.put("model",model);
//                    params.put("error",error);
//
//                    return params;
//                }
//            };
//
//            RequestQueue queue = Volley.newRequestQueue(activity);
//            queue.add(request);
//
//            return null;
//        }
//
//        @Override
//        protected void onProgressUpdate(String... values) {
//            super.onProgressUpdate(values);
//
//            Toast.makeText(activity, values[0], Toast.LENGTH_SHORT).show();
//
//        }
//    }
}
