package com.musicstreaming.musicstreaming;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static com.musicstreaming.musicstreaming.login.IMAGE;
import static com.musicstreaming.musicstreaming.login.SHARED_PREF;
import static com.musicstreaming.musicstreaming.login.USERNAME;

public class profile_img extends AppCompatActivity {
    /**
     * <h1>Ownership</h1>
     * <p>This Application is Owned by Ashish Kumar, Delhi Technology univerity, Btech ,Mechanical
     * Engineering ,2ndyear Student</p>
     * <p>Finished First version 1.0 on 17-Aug-2020</p>
     */

    String TAG="helloworldisthis",encodedimage,urls="https://rentdetails.000webhostapp.com/musicplayer_files/uploadprofileimage.php";
    Bitmap bitmap;
    ImageView imageView;
    SharedPreferences sharedPreferences;
    public static Context PROFILE_IMG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new Exceptionhandler(this));
        PROFILE_IMG=this;

        setContentView(R.layout.activity_profile_img);
        Toolbar toolbar = findViewById(R.id.toolbars);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sharedPreferences=getSharedPreferences(SHARED_PREF,MODE_PRIVATE);
        imageView = findViewById(R.id.profile_image);

        String url=sharedPreferences.getString(IMAGE,"");
        if(!url.equals("")){
            loadimage(url);
        }else{
            Toast.makeText(this, "Image url is null ", Toast.LENGTH_SHORT).show();
        }

    }

    public void loadimage(String url){
        Glide.with(this)
                .load(url)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                        Log.d(TAG, "onLoadFailed: fail to load image "+e.getMessage());

                        if(e.getMessage()!=null){
                            Toast.makeText(profile_img.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(profile_img.this, "Unknown error occured", Toast.LENGTH_SHORT).show();
                        }
                        String err="Error in loading profile Image in profile_img "+e.getMessage();
                        new internal_error_report(PROFILE_IMG,err,MainActivity.sharedPreferences.getString(USERNAME,"")).execute();

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                        //Toast.makeText(profile_img.this, "Image loaded ", Toast.LENGTH_SHORT).show();

                        return false;
                    }
                })
                .into(imageView);
    }


    private void selectimagefromgalary() {

        Dexter.withActivity(profile_img.this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        Intent intents = new Intent(Intent.ACTION_PICK);
                        intents.setType("image/*");
                        startActivityForResult(Intent.createChooser(intents,"Select Image"),100);

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profilemenu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.selectimg:

                selectimagefromgalary();

                break;
            case  R.id.done:

                if(bitmap!=null) {
                    new compressimage().execute(bitmap);
                }else {
                    Toast.makeText(PROFILE_IMG, "Please Select An Image.", Toast.LENGTH_SHORT).show();
                }

                break;

            case android.R.id.home:
                startActivity(new Intent(this,MainActivity.class));
                break;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode==100 && resultCode== Activity.RESULT_OK && data!=null && data.getData()!=null)
        {
            Uri filepath = data.getData();
            Log.d(TAG, "selected image uri "+filepath);
            CropImage.activity(filepath)
                    .setCropMenuCropButtonTitle("Crop Image")
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setFixAspectRatio(true)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                assert result != null;
                Uri resultUri = result.getUri();
                Log.d(TAG, "uri of croped image is "+resultUri);
                File file = new File(resultUri.toString());
                if(file.exists()){
                    Toast.makeText(this, "length using file method "+file.length(), Toast.LENGTH_SHORT).show();
                }
                try {
                    InputStream inputStream = getContentResolver().openInputStream(resultUri);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    imageView.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                imageView.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Toast.makeText(this, "failed!", Toast.LENGTH_SHORT).show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    public class compressimage extends AsyncTask<Bitmap,Void,Void> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(profile_img.this);
            progressDialog.setMessage("Processing Image...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Bitmap... bitmaps) {

            Bitmap bitmap = bitmaps[0];

            int quality=100,lengths;
            String encodedfiles;

            do {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
                byte[] imagebytes = byteArrayOutputStream.toByteArray();
                lengths=imagebytes.length;
                Log.d(TAG, "storeimage: size for quality "+quality+" is " + lengths);
                quality-=5;
                encodedfiles=android.util.Base64.encodeToString(imagebytes, Base64.DEFAULT);

            }while (quality>20 && lengths>500000);

            encodedimage = encodedfiles;

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            progressDialog.dismiss();

            if(encodedimage!=null) {
                new uploadimagetoserver().execute(urls);
//              new UPLOAD_IMAGE(sharedPreferences.getString(USERNAME, ""), encodedimage, urls).execute();
            }else {
                Toast.makeText(profile_img.this, "ERROR: ENOD_IMG_NULL", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public class uploadimagetoserver extends AsyncTask<String,Void,Void>{

        final ProgressDialog progressDialog = new ProgressDialog(profile_img.this);
        String message="";
        Handler handler = new Handler();
        int num=0;

        @Override
        protected void onPreExecute() {

            progressDialog.setMessage("Uploading image...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {

            String url = strings[0];
            final String username=sharedPreferences.getString(USERNAME,"");
            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                        if(!response.equalsIgnoreCase("failed")) {
                            String iage = "https://rentdetails.000webhostapp.com/musicplayer_files/musicimages/" + response;
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(IMAGE, iage);
                            editor.apply();
                            message="successful ";
                        }else{
                            message=response;
                        }

                    }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    getSupportActionBar().setTitle("Upload Failed...");

                    if(error.getMessage()!=null) {
                        message = error.getMessage();
                        Toast.makeText(profile_img.this, " "+error.getMessage(), Toast.LENGTH_LONG).show();
                    }else{
                        message = "Unknown error occured!";
                        Toast.makeText(profile_img.this, "Failed ", Toast.LENGTH_LONG).show();
                    }

                    String err="Error in uploadimagetoserver in progile_img "+error.getMessage();
                    new internal_error_report(PROFILE_IMG,err,MainActivity.sharedPreferences.getString(USERNAME,"")).execute();

                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String,String> params = new HashMap<String, String>();
                    params.put("image",encodedimage);
                    if(!username.equals("")) {
                        params.put("username", username);
                    }
                    return params;
                }
            };

            request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS*2,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestQueue queue = Volley.newRequestQueue(profile_img.this);
            queue.add(request);


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if(!message.equals(""))
                    {
                        progressDialog.dismiss();
                        Toast.makeText(profile_img.this, message, Toast.LENGTH_LONG).show();
                    }else{
                        handler.postDelayed(this,1000);

                        if(num>300){
                            progressDialog.dismiss();
                            uploadimagetoserver uploadimagetoserver = new uploadimagetoserver();
                            uploadimagetoserver.cancel(true);
                            if(uploadimagetoserver.isCancelled()){
                                Toast.makeText(profile_img.this, "Exceeded max time!", Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(profile_img.this, "Failed...395", Toast.LENGTH_LONG).show();
                            }

                            handler.removeCallbacks(this);
                        }
                    }
                }
            };
            runnable.run();
        }
    }

    public class UPLOAD_IMAGE extends AsyncTask<Void,Void,Void>{

        String encodeimage,username,url;
        ProgressDialog progressDialog;

        public UPLOAD_IMAGE(String username,String encodeimage,String url) {
            this.username=username;
            this.encodeimage=encodeimage;
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(profile_img.this);
            progressDialog.setMessage("Uploading Image...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            HashMap<String,String> params = new HashMap<String, String>();
            params.put("image",encodedimage);
            if(!username.equals("")) {
                params.put("username", username);
            }

            HttpUtility.newRequest(getApplicationContext(), url, HttpUtility.METHOD_POST, params, new HttpUtility.Callback() {
                @Override
                public void OnSuccess(String response) {
                    progressDialog.dismiss();
                    Toast.makeText(profile_img.this, "got response "+response, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void OnError(int status_code, String message) {
                    progressDialog.dismiss();
                    Toast.makeText(profile_img.this, "Error occured "+status_code+" "+message, Toast.LENGTH_SHORT).show();
                }
            });

            return null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(profile_img.this,MainActivity.class));

    }
}
