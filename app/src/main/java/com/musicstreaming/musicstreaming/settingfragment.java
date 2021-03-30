package com.musicstreaming.musicstreaming;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.musicstreaming.musicstreaming.service.online_status_updater;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.musicstreaming.musicstreaming.MainActivity.MAIN_ACTIVITY;
import static com.musicstreaming.musicstreaming.login.EMAIL;
import static com.musicstreaming.musicstreaming.login.IMAGE;
import static com.musicstreaming.musicstreaming.login.NAME;
import static com.musicstreaming.musicstreaming.login.PASSWORD;
import static com.musicstreaming.musicstreaming.login.SHARED_PREF;
import static com.musicstreaming.musicstreaming.login.USERNAME;
import static com.musicstreaming.musicstreaming.service.onclearfrompercentservice.TAG;
import static com.musicstreaming.musicstreaming.service.onclearfrompercentservice.TERMINATION_STATUS;
import static com.musicstreaming.musicstreaming.service.onclearfrompercentservice.handler;
import static com.musicstreaming.musicstreaming.service.onclearfrompercentservice.terminate_all_process;
import static com.musicstreaming.musicstreaming.splash.DIR_NAME;

public class settingfragment extends Fragment {
    /**
     * <h1>Ownership</h1>
     * <p>This Application is Owned by Ashish Kumar, Delhi Technology univerity, Btech ,Mechanical
     * Engineering ,2ndyear Student</p>
     * <p>Finished First version 1.0 on 17-Aug-2020</p>
     */

    ImageView profileimg;
    EditText names,email,username,password;
    Context context;
    SharedPreferences sharedPreferences;
    Button doneupdate;
    RelativeLayout appear_anim;
    Animation appear;
    ProgressDialog progressDialog;
    TextView advance_setting,scan_qr;

    public settingfragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_settingfragment, container, false);

        Thread.setDefaultUncaughtExceptionHandler(new Exceptionhandler(MAIN_ACTIVITY));

        context=view.getContext();

        sharedPreferences = context.getSharedPreferences(SHARED_PREF,Context.MODE_PRIVATE);

        names=view.findViewById(R.id.name);
        email=view.findViewById(R.id.email);
        username=view.findViewById(R.id.usernames);
        password=view.findViewById(R.id.password);
        profileimg=view.findViewById(R.id.profileimage);
        doneupdate=view.findViewById(R.id.submit);
        advance_setting = view.findViewById(R.id.advance_setting);
        scan_qr=view.findViewById(R.id.scan_qr);

        names.setText(sharedPreferences.getString(login.NAME,""));
        email.setText(sharedPreferences.getString(EMAIL,""));
        username.setText(sharedPreferences.getString(USERNAME,""));
        password.setText(sharedPreferences.getString(PASSWORD,""));

        appear= AnimationUtils.loadAnimation(context,R.anim.appear);
        appear_anim=view.findViewById(R.id.lol);
        appear_anim.startAnimation(appear);

        progressDialog=new ProgressDialog(context);
        progressDialog.setCancelable(false);

        setHasOptionsMenu(true);

        doneupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceedupdate();
            }
        });
        String image=sharedPreferences.getString(IMAGE,"");
        if(!image.equalsIgnoreCase("")) {
            loadimage(image);
        }else{
            Toast.makeText(context, "Fail to load Image ", Toast.LENGTH_SHORT).show();
        }

        profileimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,profile_img.class));
            }
        });

        advance_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,setting_advance.class));
            }
        });

        scan_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,qr_scanner.class));
            }
        });

        return view;
    }
    public void loadimage(String url){
        final CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(40f);
        circularProgressDrawable.start();

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(circularProgressDrawable);
        requestOptions.skipMemoryCache(true);
        requestOptions.circleCrop();
        requestOptions.priority(Priority.HIGH);
        requestOptions.fitCenter();

        Log.d(TAG, "loadimage: your requested url is "+url);

        Glide.with(this)
                .load(url)
                .apply(requestOptions)
                .apply(RequestOptions.circleCropTransform())
                .transition(DrawableTransitionOptions.withCrossFade())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                        circularProgressDrawable.stop();
                        Toast.makeText(context, "Fail to load Image ", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onLoadFailed: fail to load image "+e.getMessage());
                        String err="Error in load image in settingfragment "+e.getMessage();
                        new internal_error_report(MAIN_ACTIVITY,err,MainActivity.sharedPreferences.getString(USERNAME,"")).execute();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                        circularProgressDrawable.stop();
                        Log.d(TAG, "onResourceReady: image loading successful ");


                        return false;
                    }
                })
                .into(profileimg);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.logout,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.logout){

            progressDialog.setMessage("Logging Out, Please wait...");
            progressDialog.show();

            online_status_updater.username="";

            terminate_all_process.run();
            Runnable check_termination_status = new Runnable() {
                @Override
                public void run() {
                    if(TERMINATION_STATUS) {
                        progressDialog.dismiss();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(USERNAME,"");
                        editor.putString(PASSWORD,"");
                        editor.apply();

                        new make_file_in_directory(MAIN_ACTIVITY,context,"").write_credential_file("","",new File(Environment.getExternalStorageDirectory()+"/"+DIR_NAME,"file.json"));

                        homefragment.listofplaylistArrayList.clear();
                        songsfromplaylist.listofsongsArrayLisr.clear();
                        startActivity(new Intent(context, login.class));

                    }else{
                        handler.postDelayed(this,1000);
                    }
                }
            };

            check_termination_status.run();
        }else
            if(item.getItemId()==R.id.qr_code){

                startActivity(new Intent(context,make_custom_playlist.class).putExtra("IS_QR_CODE",true));

            }

        return true;
    }

    private void proceedupdate() {

        final String name,emails,usernames,passwords,url="https://rentdetails.000webhostapp.com/musicplayer_files/updatedata.php";

        name=names.getText().toString().trim();
        usernames=username.getText().toString().trim();
        emails=email.getText().toString().trim();
        passwords=password.getText().toString().trim();

        if(name.isEmpty()){
            Toast.makeText(context, "Name Required", Toast.LENGTH_SHORT).show();
        }else if(usernames.isEmpty()){
            Toast.makeText(context, "Username Required", Toast.LENGTH_SHORT).show();
        }else if(emails.isEmpty()){
            Toast.makeText(context, "Email Required", Toast.LENGTH_SHORT).show();
        }else if(passwords.isEmpty()){
            Toast.makeText(context, "Password Required", Toast.LENGTH_SHORT).show();
        }else if(name.equalsIgnoreCase(sharedPreferences.getString(NAME,"").trim()) && passwords.equalsIgnoreCase(sharedPreferences.getString(PASSWORD,"").trim())){
            Toast.makeText(context, "No Change Found!!", Toast.LENGTH_SHORT).show();
        }else{

            final ProgressDialog progressDialog=new ProgressDialog(context);
            progressDialog.setMessage("Updating...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    progressDialog.dismiss();

                    if(response.equalsIgnoreCase("successful")){

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(NAME,name);
                        editor.putString(PASSWORD,passwords);
                        editor.apply();

                        new make_file_in_directory(MAIN_ACTIVITY,context,usernames).write_credential_file(usernames,passwords,new File(Environment.getExternalStorageDirectory()+"/"+DIR_NAME,"file.json"));

                        Toast.makeText(context, "Update successful ", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context, "Update Failed ", Toast.LENGTH_SHORT).show();
                    }

                    Log.d(TAG, "onResponse: your value is "+response);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    progressDialog.dismiss();
                    Toast.makeText(context, "Unknown error occured ", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onErrorResponse: error is "+ error.getMessage());

                    String err="Error in proceedupdate in settingfragment "+error.getMessage();
                    new internal_error_report(MAIN_ACTIVITY,err,MainActivity.sharedPreferences.getString(USERNAME,"")).execute();

                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params=new HashMap<String, String>();
                    params.put("name",name);
                    params.put("password",passwords);
                    params.put("username",usernames);

                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(request);

        }
    }

}
