package com.musicstreaming.musicstreaming;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.os.Handler;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import static com.musicstreaming.musicstreaming.MainActivity.MAIN_ACTIVITY;
import static com.musicstreaming.musicstreaming.MainActivity.MAIN_ACTIVITY_CONTEXT;
import static com.musicstreaming.musicstreaming.qr_scanner.getUserDetails.IS_COMPLETED_LOADING;
import static com.musicstreaming.musicstreaming.qr_scanner.getUserDetails.image;
import static com.musicstreaming.musicstreaming.qr_scanner.getUserDetails.name;
import static com.musicstreaming.musicstreaming.qr_scanner.getUserDetails.playlist_names;
import static com.musicstreaming.musicstreaming.qr_scanner.getUserDetails.plylst_count;
import static com.musicstreaming.musicstreaming.splash.SHARED_PREF;
import static com.musicstreaming.musicstreaming.splash.USERNAME;
import static java.lang.Thread.sleep;


public class qr_code extends Fragment {


    ImageView qr_codeIV;
    SharedPreferences sharedPreferences;
    Context context;
    String TAG="qr_code_generation",QRCodeScannedData;
    Bitmap QRBitmap;
    Button getImageFromGallery;

    public qr_code() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_qr_code, container, false);

        context=view.getContext();

        qr_codeIV=view.findViewById(R.id.qr_code_image);
        getImageFromGallery=view.findViewById(R.id.getImageFromGallery);

        setHasOptionsMenu(true);

        sharedPreferences=context.getSharedPreferences(SHARED_PREF,Context.MODE_PRIVATE);
        if(!sharedPreferences.getString(USERNAME,"").equals("")){
            String username=sharedPreferences.getString(USERNAME,""),encripted;
            encripted=new Encription().Encript(username);

            Generate_QRCode(encripted);

            Log.d(TAG, "onCreateView: "+encripted);
        }
        Intent intent = getActivity().getIntent();
        if(intent!=null){

            if(intent.getBooleanExtra("IS_SCANNING_DONE",false)){

                String username = intent.getStringExtra("USERNAME_QR");
                if(username!=null) {
                    qr_scanner.getUserDetails.username = username;

                    new qr_scanner.getUserDetails(context).execute();

                }

            }

        }
        qr_codeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,qr_scanner.class));
            }
        });

        getImageFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(MAIN_ACTIVITY)
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
                                Toast.makeText(context, "Pemission Denied!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==100 && resultCode== Activity.RESULT_OK && data!=null && data.getData()!=null){

            Uri uri = data.getData();
            try{
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                if(bitmap==null){
                    Toast.makeText(context, "ERR_IMG_SELECTION", Toast.LENGTH_SHORT).show();
                    return;
                }
                int width = bitmap.getWidth(), height=bitmap.getHeight();
                int[] pixels = new int[width*height];
                bitmap.getPixels(pixels,0,width,0,0,width,height);
                bitmap.recycle();
                bitmap=null;
                RGBLuminanceSource source = new RGBLuminanceSource(width,height,pixels);
                BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

                MultiFormatReader reader = new MultiFormatReader();
                try{
                    Result result = reader.decode(binaryBitmap);

                    String value=result.getText();
                    Log.d(TAG, "onActivityResult: "+value);

                    if(validateQRImage(value)){
                        qr_scanner.getUserDetails.username = new Encription.Decription().Decript(value);

                        new qr_scanner.getUserDetails(context).execute();

                        custom_dialod(context);
                    }else{
                        Toast.makeText(context, "Failed to scan, Try Again!", Toast.LENGTH_SHORT).show();
                    }
                }catch (NotFoundException ex){
                    ex.printStackTrace();
                    Log.d(TAG, "onActivityResult: "+ex.getMessage());
                    Toast.makeText(context, "ERR_QR_NULL", Toast.LENGTH_SHORT).show();
                }
            }catch (FileNotFoundException e){
                e.printStackTrace();
                Log.d(TAG, "onActivityResult: "+e.getMessage());
                Toast.makeText(context, "ERR_QR_PTH_NUL", Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(context, "Null Found", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onActivityResult: this is null");
        }

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.qrimage_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.share_qr){

            if(QRBitmap!=null){

                String path = MediaStore.Images.Media.insertImage(context.getContentResolver(),QRBitmap,sharedPreferences.getString(USERNAME,""),null);
                Uri uri = Uri.parse(path);

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM,uri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setType("image/png");
                startActivity(Intent.createChooser(intent,"Share with"));

            }

        }

        return true;
    }

    public  void  Generate_QRCode(String encripted_text){
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        try{
            BitMatrix matrix = multiFormatWriter.encode(encripted_text, BarcodeFormat.QR_CODE,300,300);

            BarcodeEncoder encoder = new BarcodeEncoder();

            Bitmap bitmap = encoder.createBitmap(matrix);

            QRBitmap=setIconToQRImage(bitmap);

            qr_codeIV.setImageBitmap(QRBitmap);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Bitmap setIconToQRImage(Bitmap QRBitmap){

        Bitmap icon =BitmapFactory.decodeResource(context.getResources(),R.drawable.round_image);

        Bitmap combine = Bitmap.createBitmap(QRBitmap.getWidth(),QRBitmap.getHeight(),QRBitmap.getConfig());
        Canvas canvas = new Canvas(combine);
        int canvasWight = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        canvas.drawBitmap(QRBitmap,new Matrix(),null);

        Bitmap logoResize = Bitmap.createScaledBitmap(icon,canvasWight/5,canvasHeight/5,true);
        int centerX = (canvasWight-logoResize.getWidth())/2;
        int centerY = (canvasHeight-logoResize.getHeight())/2;
        canvas.drawBitmap(logoResize,centerX,centerY,null);

        return combine;
    }

    //TODO: update this and work on custom dialogue for the making friend
    public void custom_dialod(Context context){
        final TextView frd_name;
        final Button close, send_req;
        final ImageView frd_image;
        final ListView listView;
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        final View mview = getLayoutInflater().inflate(R.layout.custom_frd_details,null);

        close = mview.findViewById(R.id.btn_close);
        send_req = mview.findViewById(R.id.send_req);
        frd_name = mview.findViewById(R.id.friend_name);
        frd_image = mview.findViewById(R.id.frd_profile_img);

        final Handler handler = new Handler();
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(IS_COMPLETED_LOADING){

                    String name = qr_scanner.getUserDetails.name;
                    String image = qr_scanner.getUserDetails.image;
                    String playlist = qr_scanner.getUserDetails.plylst_count;
                    ArrayAdapter<String> names_plyst= qr_scanner.getUserDetails.playlist_names;

                    alert.setView(mview);

                    frd_name.setText(name);
                    loadimage(image,frd_image);

                    listView.setAdapter(names_plyst);

                    final AlertDialog alertDialog = alert.create();
                    alertDialog.setCanceledOnTouchOutside(false);

                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });

                    alertDialog.show();
                    Log.d(TAG, "run: executed this line");
                }else{
                   handler.postDelayed(this,500);
                }
            }
        }).start();


    }

    public void loadimage(String url, ImageView profileimage){
        final CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(40f);
        circularProgressDrawable.start();

        final RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(circularProgressDrawable);
        requestOptions.skipMemoryCache(true);
        requestOptions.circleCrop();
        requestOptions.priority(Priority.HIGH);
        requestOptions.fitCenter();

        Log.d(TAG, "loadimage: your requested url is "+url);

        try {
            Glide.with(this)
                    .load(url)
                    .apply(requestOptions)
                    .apply(RequestOptions.circleCropTransform())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                            Toast.makeText(context, "Fail to load Image ", Toast.LENGTH_SHORT).show();
                            circularProgressDrawable.setVisible(false,false);
                            Log.d(TAG, "onLoadFailed: fail to load image " + e.getMessage());
                            String err = "Error in loading profile Image " + e.getMessage();
                            new internal_error_report(MAIN_ACTIVITY, err, MainActivity.sharedPreferences.getString(login.USERNAME, "")).execute();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                            Log.d(TAG, "onResourceReady: image loading successful ");

                            return false;
                        }
                    })
                    .into(profileimage);
        }catch (Exception e){
            new internal_error_report(context,"Error in MainActivity loading profile image : "+e.getMessage(),sharedPreferences.getString(login.USERNAME,"")).execute();
        }
    }

    public static boolean validateQRImage(String data){
        try {
            if (data.split("/")[1].equals("1") && data.length()==290) {
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return false;
    }

    public static void validateUerDataFromServer(){

        String TAG="validating_info";
        String name = qr_scanner.getUserDetails.name;
        String image = qr_scanner.getUserDetails.image;
        String playlist = qr_scanner.getUserDetails.plylst_count;
        List<String> names_plyst= qr_scanner.getUserDetails.playlist_names;

        try {
            Log.d(TAG, "validateUerDataFromServer: name " + name);
            Log.d(TAG, "validateUerDataFromServer: image "+image);
            Log.d(TAG, "validateUerDataFromServer: playlist "+playlist);
            Log.d(TAG, "validateUerDataFromServer: count_list "+names_plyst.size());
        }catch (Exception e){
            Log.d(TAG, "validateUerDataFromServer: Got an error in "+e.getMessage());
        }

    }
}