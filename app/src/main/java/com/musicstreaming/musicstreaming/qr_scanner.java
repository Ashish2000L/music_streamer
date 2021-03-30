package com.musicstreaming.musicstreaming;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static com.musicstreaming.musicstreaming.MainActivity.MAIN_ACTIVITY;
import static com.musicstreaming.musicstreaming.MainActivity.sharedPreferences;
import static com.musicstreaming.musicstreaming.login.USERNAME;
import static com.musicstreaming.musicstreaming.playselectedsong.SONG_ACTIVITY;
import static com.musicstreaming.musicstreaming.playselectedsong.playlistid;
import static com.musicstreaming.musicstreaming.qr_code.validateQRImage;
import static com.musicstreaming.musicstreaming.qr_code.validateUerDataFromServer;
import static com.musicstreaming.musicstreaming.service.onclearfrompercentservice.TAG;

public class qr_scanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    ZXingScannerView zXingScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        zXingScannerView=new ZXingScannerView(this);
        setContentView(zXingScannerView);


        Thread.setDefaultUncaughtExceptionHandler(new Exceptionhandler(this));

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        zXingScannerView.startCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(qr_scanner.this, "Permission Denied!", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(qr_scanner.this,make_custom_playlist.class).putExtra("IS_QR_CODE",true));
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

    }


    @Override
    public void handleResult(Result rawResult) {
        if(rawResult.getText().split("/")[1].equals("1")) {
            Decript_QRCode(rawResult.getText());
        }else {
            Toast.makeText(this, "Failed To Access!!", Toast.LENGTH_SHORT).show();
        }
    }

    public void Decript_QRCode(String encodedTxt){

        if(validateQRImage(encodedTxt)) {
            String username = new Encription.Decription().Decript(encodedTxt);

            startActivity(new Intent(this, make_custom_playlist.class).putExtra("IS_QR_CODE", true).putExtra("USERNAME_QR", username).putExtra("IS_SCANNING_DONE", true));
        }else {
            Toast.makeText(this, "Failed to scan, Try Again!", Toast.LENGTH_LONG).show();
//            heading_tv.setText(encodedTxt);
            startActivity(new Intent(this, make_custom_playlist.class).putExtra("IS_QR_CODE", true));
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        zXingScannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        zXingScannerView.setResultHandler(this);
        zXingScannerView.startCamera();
    }

    public static class getUserDetails extends AsyncTask<Void,Void,Void>{

        Context context;
        public static String username,name,image,plylst_count,MainUserName,frd_status;
        public static List<String> playlist_names;
        public static boolean IS_COMPLETED_LOADING=false;
        ProgressDialog progressDialog;

        public getUserDetails(Context context) {
            this.context = context;
            playlist_names=new ArrayList<String>();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Getting User Info....");
            progressDialog.setCancelable(false);
            progressDialog.show();

            MainUserName=sharedPreferences.getString(USERNAME,"");
        }

        @Override
        protected Void doInBackground(Void... voids) {

            String url="https://rentdetails.000webhostapp.com/musicplayer_files/friends_folder/get_friends_details.php";

            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    playlist_names.clear();
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String success = jsonObject.getString("success");
                        if (success.equals("1")) {
                            name =jsonObject.getString("name");
                            image = jsonObject.getString("image");
                            plylst_count= jsonObject.getString("playlist_count");
                            frd_status=jsonObject.getString("frd");

                            if(Integer.parseInt(plylst_count)>0) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    String plylst_name=object.getString("name");
                                    String plylst_img=object.getString("image");

                                    playlist_names.add(plylst_name);

                                }
                                IS_COMPLETED_LOADING=true;
                                progressDialog.dismiss();
                            }else{
                                IS_COMPLETED_LOADING=true;
                                progressDialog.dismiss();
                            }

//                            validateUerDataFromServer();
                        }else{
                            toastMessage(jsonObject.getString("error"));
                            IS_COMPLETED_LOADING=true;
                            progressDialog.dismiss();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(TAG, "onResponse: error in json is "+e.getMessage());
                        IS_COMPLETED_LOADING=true;
                        progressDialog.dismiss();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    IS_COMPLETED_LOADING=true;
                    progressDialog.dismiss();
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
                    params.put("mainuser",MainUserName);

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(qr_scanner.this,make_custom_playlist.class).putExtra("IS_QR_CODE",true));
    }
}