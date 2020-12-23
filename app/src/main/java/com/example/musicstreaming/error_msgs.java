package com.example.musicstreaming;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.musicstreaming.MainActivity.MAIN_ACTIVITY;
import static com.example.musicstreaming.MainActivity.thisisit;
import static com.example.musicstreaming.login.SHARED_PREF;
import static com.example.musicstreaming.login.USERNAME;


public class error_msgs extends Fragment {

    public ListView error_list_view;
    listoferror listoferror;
    RelativeLayout relativeLayout;
    public SharedPreferences sharedPreferences;
    error_adaptor error_adaptor;
    Context context;
    static ArrayList<listoferror> listoferrorArrayList = new ArrayList<>();


    public error_msgs() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_error_msgs, container, false);
        context=view.getContext();
        Thread.setDefaultUncaughtExceptionHandler(new Exceptionhandler(MAIN_ACTIVITY));

        sharedPreferences = context.getSharedPreferences(SHARED_PREF,Context.MODE_PRIVATE);
        error_list_view=view.findViewById(R.id.list_of_error);
        error_adaptor=new error_adaptor(context,listoferrorArrayList);
        error_list_view.setAdapter(error_adaptor);

        new get_error_log(context).execute();

        error_list_view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                String[] options={"Fixed!","Check Song","Error ID"};
                AlertDialog.Builder alert = new AlertDialog.Builder(context)
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                switch (which){

                                    case 0 :
                                        new song_fixed(context,listoferrorArrayList.get(position).getError_id()).execute();
                                        break;
                                    case 1:
                                            new transfer_song(context,listoferrorArrayList.get(position).getError_id()).execute();
                                        break;

                                    case 2:
                                        Toast.makeText(context, "ERROR ID: "+listoferrorArrayList.get(position).getError_id(), Toast.LENGTH_SHORT).show();

                                }

                            }
                        });

                alert.show();

                return false;
            }
        });


        return view;
    }

    public class transfer_song extends  AsyncTask<String,Void,Void>{

        String error_id,url="https://rentdetails.000webhostapp.com/musicplayer_files/songs_error_manager/set_song_to_playlist_for_testing.php";
        Context context;
        ProgressDialog progressDialog;
        public transfer_song(Context context,String error_id) {
            this.context = context;
            this.error_id=error_id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Setting Playlist...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {

            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    progressDialog.dismiss();
                    if(response.equals("1")){
                        startActivity(new Intent(context,MainActivity.class));
                    }else{
                        Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    if(error.getMessage()!=null){
                        Toast.makeText(context, "error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context, "unknown error occured ", Toast.LENGTH_SHORT).show();
                    }

                    String err="Error in getsongs in songfromplaylist "+error.getMessage();
                    new internal_error_report(context,err,MainActivity.sharedPreferences.getString(USERNAME,"")).execute();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String,String> params = new HashMap<String, String>();

                    params.put("id",error_id);

                    return params;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(request);

            return null;
        }
    }

    public class song_fixed extends  AsyncTask<String,Void,Void>{

        String error_id,url="https://rentdetails.000webhostapp.com/musicplayer_files/songs_error_manager/change_error_status.php";
        Context context;
        ProgressDialog progressDialog;
        public song_fixed(Context context,String error_id) {
            this.context = context;
            this.error_id=error_id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("WAIT..");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {
//            Toast.makeText(context, "working....", Toast.LENGTH_SHORT).show();
            Log.d("werdone", "doInBackground: this is going on ");
            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    progressDialog.dismiss();
                    if(response.equals("1")){
                        new get_error_log(context).execute();
                    }else{
                        Toast.makeText(context, "Rspo "+response, Toast.LENGTH_SHORT).show();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    if(error.getMessage()!=null){
                        Toast.makeText(context, "error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context, "unknown error occured ", Toast.LENGTH_SHORT).show();
                    }

                    String err="Error in getsongs in songfromplaylist "+error.getMessage();
                    new internal_error_report(context,err,MainActivity.sharedPreferences.getString(USERNAME,"")).execute();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String,String> params = new HashMap<String, String>();

                    params.put("id",error_id);

                    return params;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(request);

            return null;
        }
    }

    public class get_error_log extends AsyncTask<String,Void,Void>{


        Context context;
        String url="https://rentdetails.000webhostapp.com/musicplayer_files/songs_error_manager/get_error_detail_for_moderator.php";
        ProgressDialog progressDialog;
        public get_error_log(Context context) {
            this.context=context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Please Wait");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {

            listoferrorArrayList.clear();
            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    listoferrorArrayList.clear();
                    progressDialog.dismiss();
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String success = jsonObject.getString("success");
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        if (success.equals("1")) {
                            for (int i =0 ; i <jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);

                                String id =object.getString("id");
                                String name = object.getString("name");
                                String songurl = object.getString("url");
                                String image = object.getString("image");
                                String error_id = object.getString("error_id");
                                String singer = object.getString("singer");
                                String bkcolor=object.getString("bkcolor");
                                String status= object.getString("status");

                                listoferror = new listoferror(id,name,songurl,image,error_id,singer,bkcolor,status);
                                listoferrorArrayList.add(listoferror);
                                error_adaptor.notifyDataSetChanged();

                            }
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Error occured!", Toast.LENGTH_SHORT).show();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    if(error.getMessage()!=null){
                        Toast.makeText(context, "error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context, "unknown error occured ", Toast.LENGTH_SHORT).show();
                    }

                    String err="Error in getsongs in songfromplaylist "+error.getMessage();
                    new internal_error_report(context,err,MainActivity.sharedPreferences.getString(USERNAME,"")).execute();

                }
            });

            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(request);


            return null;
        }
    }

}