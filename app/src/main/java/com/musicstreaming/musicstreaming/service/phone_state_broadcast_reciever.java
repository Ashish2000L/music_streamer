package com.musicstreaming.musicstreaming.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class phone_state_broadcast_reciever extends BroadcastReceiver {

    /**
     * <h1>Ownership</h1>
     * <p>This Application is Owned by Ashish Kumar, Delhi Technology univerity, Btech ,Mechanical
     * Engineering ,2ndyear Student</p>
     * <p>Finished First version on 17-Aug-2020</p>
     */



    @Override
    public void onReceive(Context context, Intent intent) {


        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(new onclearfrompercentservice.check_for_phone_call(context), PhoneStateListener.LISTEN_CALL_STATE);


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
