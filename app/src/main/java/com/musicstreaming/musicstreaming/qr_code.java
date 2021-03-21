package com.musicstreaming.musicstreaming;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import static com.musicstreaming.musicstreaming.splash.SHARED_PREF;
import static com.musicstreaming.musicstreaming.splash.USERNAME;


public class qr_code extends Fragment {


    ImageView qr_codeIV;
    SharedPreferences sharedPreferences;
    Context context;
    String TAG="qr_code_generation";
    Bitmap QRBitmap;

    public qr_code() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_qr_code, container, false);

        context=view.getContext();

        qr_codeIV=view.findViewById(R.id.qr_code_image);

        setHasOptionsMenu(true);

        sharedPreferences=context.getSharedPreferences(SHARED_PREF,Context.MODE_PRIVATE);
        if(!sharedPreferences.getString(USERNAME,"").equals("")){
            String username=sharedPreferences.getString(USERNAME,""),encripted;
            encripted=new Encription().Encript(username);

            if(encripted.split("/")[1].equals("1")) {
                Generate_QRCode(encripted.split("/")[0]);
                Log.d(TAG, "onCreateView: encripted username is "+encripted.split("/")[0]);

            }else{
                Log.d(TAG, "onCreateView: Invalid text");
            }
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

        return view;
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
    public void custom_dialod(String title, String bodys, int token){
        TextView close,heading,body;
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        View mview = getLayoutInflater().inflate(R.layout.custom_dialogue_inappmessaging,null);

        close = mview.findViewById(R.id.close_dialogue);
        heading = mview.findViewById(R.id.title_for_dialogue);
        body = mview.findViewById(R.id.body_for_dialogue);

        alert.setView(mview);

        heading.setText(Html.fromHtml(title));
        body.setText(Html.fromHtml(bodys));

        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("TOKEN",token);
        editor.apply();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();

    }

}