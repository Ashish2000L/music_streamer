package com.example.musicstreaming;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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

import static com.example.musicstreaming.MainActivity.MAIN_ACTIVITY;
import static com.example.musicstreaming.login.SHARED_PREF;
import static com.example.musicstreaming.login.USERNAME;

public class new_playlist extends AppCompatActivity implements View.OnClickListener {

    /**
     * <h1>Ownership</h1>
     * <p>This Application is Owned by Ashish Kumar, Delhi Technology univerity, Btech ,Mechanical
     * Engineering ,2ndyear Student</p>
     */

    public String encodedimage,types="";
    public Bitmap bitmap;
    SharedPreferences sharedPreferences;
    public EditText playlist_name;
    ImageView playlist_img;
    Button btn;
    RadioGroup group;
    RadioButton type;
    Boolean IS_IMG_SElECTED=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_playlist);

        sharedPreferences=getSharedPreferences(SHARED_PREF,MODE_PRIVATE);

        btn=findViewById(R.id.upload_img);
        playlist_name=findViewById(R.id.new_playlist_name);
        playlist_img=findViewById(R.id.new_playlist_img);
        group=findViewById(R.id.radio_grp);
        btn.setOnClickListener(this);
        playlist_img.setOnClickListener(this);

        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id=group.getCheckedRadioButtonId();

                type=findViewById(id);
                types=type.getText().toString().toLowerCase().trim();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode==200 && resultCode== Activity.RESULT_OK && data!=null && data.getData()!=null)
        {
            Uri filepath = data.getData();
            Log.d("hello", "selected image uri "+filepath);
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
                Log.d("hello", "uri of croped image is "+resultUri);
                File file = new File(resultUri.toString());
                if(file.exists()){
                    Toast.makeText(this, "length using file method "+file.length(), Toast.LENGTH_SHORT).show();
                }
                try {
                    InputStream inputStream = getContentResolver().openInputStream(resultUri);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    playlist_img.setImageBitmap(bitmap);
                    IS_IMG_SElECTED=true;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                playlist_img.setImageURI(resultUri);
                Log.d("hello", "onActivityResult: "+bitmap);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d("hello", "onActivityResult: "+ error.getMessage());
                Toast.makeText(this, "failed!", Toast.LENGTH_SHORT).show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.upload_img) {

            if(IS_IMG_SElECTED) {
                new compressimage().execute(bitmap);
            }else {
                //Toast.makeText(this,"Select your playlist image!",Toast.LENGTH_LONG).show();
                startActivity(new Intent(this,make_custom_playlist.class));
            }

        }else if(v.getId()==R.id.new_playlist_img){

            Dexter.withActivity(this)
                    .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {

                            Intent intents = new Intent(Intent.ACTION_PICK);
                            intents.setType("image/*");
                            startActivityForResult(Intent.createChooser(intents,"Select Image"),200);

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
    }

    public class compressimage extends AsyncTask<Bitmap,Void,Void> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(new_playlist.this);
            progressDialog.setMessage("Processing Image...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Bitmap... bitmaps) {

            Bitmap bitmap = bitmaps[0];
            Log.d("hello", "doInBackground: "+bitmap);
            int quality=80,lengths;
            String encodedfiles;

            do {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
                byte[] imagebytes = byteArrayOutputStream.toByteArray();
                lengths=imagebytes.length;
                Log.d("hello", "storeimage: size for quality "+quality+" is " + lengths);
                quality-=5;
                encodedfiles=android.util.Base64.encodeToString(imagebytes, Base64.DEFAULT);

            }while (quality>10 && lengths>300000);

            encodedimage = encodedfiles;
            Log.d("hello",encodedimage);


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            progressDialog.dismiss();
            final String url = "https://rentdetails.000webhostapp.com/musicplayer_files/custom_playlist.php",
                    name=playlist_name.getText().toString();
            new Upload_imag().execute(name,url);

        }
    }

    public class Upload_imag extends AsyncTask<String,Void,Void> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(new_playlist.this);
            progressDialog.setMessage("Setting your playlist...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            Log.d("hello", "onPreExecute: "+encodedimage);
        }

        @Override
        protected Void doInBackground(String... strings) {
            final String name=strings[0],url=strings[1];

            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    Toast.makeText(new_playlist.this, response, Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(new_playlist.this, "Failed!", Toast.LENGTH_LONG).show();
                    new internal_error_report(new_playlist.this, error.getMessage(),sharedPreferences.getString(USERNAME,"") );
                    progressDialog.dismiss();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> params = new HashMap<String, String>();

                    params.put("name",name);
                    params.put("img",encodedimage);
                    params.put("type",types);
                    params.put("username",sharedPreferences.getString(USERNAME,""));

                    return params;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(new_playlist.this);
            queue.add(request);

            return null;
        }
    }
}
