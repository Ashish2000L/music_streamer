package com.musicstreaming.musicstreaming;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static com.musicstreaming.musicstreaming.login.USERNAME;
import static com.musicstreaming.musicstreaming.service.onclearfrompercentservice.TAG;

public class qr_scanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    ZXingScannerView zXingScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        zXingScannerView=new ZXingScannerView(this);
        setContentView(zXingScannerView);
    }


    @Override
    public void handleResult(Result rawResult) {
        if(rawResult.getText().split("/")[1].equals("1")) {
            Decript_QRCode(rawResult.getText().split("/")[0]);
        }else {
            Toast.makeText(this, "Failed To Access!!", Toast.LENGTH_SHORT).show();
        }
    }

    public void Decript_QRCode(String encodedTxt){

        String username = new Encription.Decription().Decript(encodedTxt);

        startActivity(new Intent(this,make_custom_playlist.class).putExtra("IS_QR_CODE",true).putExtra("USERNAME_QR",username).putExtra("IS_SCANNING_DONE",true));

    }

    public static class getUserDetails extends AsyncTask<Void,Void,Void>{

        Context context;
        public static String username,name,image,plylst_count;
        public static List<String> playlist_names=new ArrayList<>();
        public getUserDetails(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            String url="https://rentdetails.000webhostapp.com/musicplayer_files/friends_folder/get_friends_details.php";

            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String success = jsonObject.getString("success");
                        if (success.equals("1")) {
                            name =jsonObject.getString("name");
                            image = jsonObject.getString("image");
                            plylst_count= jsonObject.getString("playlist_count");

                            if(Integer.parseInt(plylst_count)>0) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    String plylst_name=object.getString("name");
                                    String plylst_img=object.getString("image");

                                    playlist_names.add(plylst_name);

                                }
                            }
                        }else{
                            toastMessage(jsonObject.getString("error"));
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(TAG, "onResponse: error in json is "+e.getMessage());
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    if(error.getMessage()!=null){

                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }else{

                        Toast.makeText(context, "Error occured!!", Toast.LENGTH_SHORT).show();
                    }

                    Log.d(TAG, "onErrorResponse: "+ error.networkResponse.statusCode);

                    String err="Error in getdata in homefragment "+error.getMessage();
                    new internal_error_report(context,err,username).execute();

                }
            }){

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String,String> params = new HashMap<String, String>();

                    params.put("username",username);

                    return params;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(request);

            return null;
        }

        public void  toastMessage(String msg){
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        }
    }

}