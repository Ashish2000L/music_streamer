package com.example.musicstreaming;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.example.musicstreaming.MainActivity.MAIN_ACTIVITY;
import static com.example.musicstreaming.login.SHARED_PREF;
import static com.example.musicstreaming.login.USERNAME;


public class make_own_playlist_fragment extends Fragment implements View.OnClickListener {

    Button btn;
    Context context;
    public EditText playlist_name;
    ImageView playlist_img;
    SharedPreferences sharedPreferences;
    Bitmap bitmap;
    String encodedimage;
    RadioGroup group;
    RadioButton type;
    String types="";

    public make_own_playlist_fragment() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View viw=inflater.inflate(R.layout.fragment_make_own_playlist_fragment, container, false);
        context=viw.getContext();

        sharedPreferences=context.getSharedPreferences(SHARED_PREF,Context.MODE_PRIVATE);

        btn=viw.findViewById(R.id.upload_img);
        playlist_name=viw.findViewById(R.id.new_playlist_name);
        playlist_img=viw.findViewById(R.id.new_playlist_img);
        group=viw.findViewById(R.id.radio_grp);
        btn.setOnClickListener(this);
        playlist_img.setOnClickListener(this);

        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id=group.getCheckedRadioButtonId();

                type=viw.findViewById(id);
                types=type.getText().toString().toLowerCase().trim();

            }
        });

        return viw;
    }

    @Override
    public void onClick(View v) {


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode==200 && resultCode== RESULT_OK && data!=null && data.getData()!=null)
        {
            Uri filepath = data.getData();
            Log.d("hello", "selected image uri "+filepath);
            CropImage.activity(filepath)
                    .setCropMenuCropButtonTitle("Crop Image")
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setFixAspectRatio(true)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(MAIN_ACTIVITY);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                assert result != null;
                Uri resultUri = result.getUri();
                Log.d("hello", "uri of croped image is "+resultUri);
                File file = new File(resultUri.toString());
                if(file.exists()){
                    Toast.makeText(context, "length using file method "+file.length(), Toast.LENGTH_SHORT).show();
                }
                try {
                    InputStream inputStream = context.getContentResolver().openInputStream(resultUri);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    playlist_img.setImageBitmap(bitmap);
                    Log.d("hello", "onActivityResult: "+bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                playlist_img.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d("hello", "onActivityResult: "+ error.getMessage());
                Toast.makeText(context, "failed!", Toast.LENGTH_SHORT).show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    public class compressimage extends AsyncTask<Bitmap,Void,Void> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Processing Image...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Bitmap... bitmaps) {

            Bitmap bitmap = bitmaps[0];
            Log.d("hello", "doInBackground: "+bitmap);
            int quality=100,lengths;
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

    public class Upload_imag extends AsyncTask<String,Void,Void>{
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("U Image...");
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

                    Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, "Failed!", Toast.LENGTH_LONG).show();
                    new internal_error_report(context, error.getMessage(),sharedPreferences.getString(USERNAME,"") );
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

            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(request);

            return null;
        }
    }
}
