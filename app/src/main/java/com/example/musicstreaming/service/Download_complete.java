package com.example.musicstreaming.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.example.musicstreaming.BuildConfig;
import com.example.musicstreaming.R;

import java.io.File;
import java.net.FileNameMap;

public class Download_complete extends BroadcastReceiver {

    /**
     * <h1>Ownership</h1>
     * <p>This Application is Owned by Ashish Kumar, Delhi Technology univerity, Btech ,Mechanical
     * Engineering ,2ndyear Student</p>
     * <p>Finished First version on 17-Aug-2020</p>
     */



    @Override
    public void onReceive(Context context, Intent intent) {

        //context.sendBroadcast(new Intent("DOWNLOAD_COMPLETE"));
        //Log.d(TAG, "onReceive: into the reciver");
        //String file=intent.getStringExtra("path");
        //File files = new File(file);
        //Uri path = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", files);
        //String mimetype = intent.getStringExtra("mimetype");
        //final Intent pdfOpenintent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
        //pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //pdfOpenintent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //pdfOpenintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //pdfOpenintent.setDataAndType(path, mimetype);
        //context.startActivity(pdfOpenintent);
        //context.unregisterReceiver(this);
    }
}
