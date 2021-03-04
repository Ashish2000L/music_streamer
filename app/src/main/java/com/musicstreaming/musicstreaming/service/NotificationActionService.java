package com.musicstreaming.musicstreaming.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

public class NotificationActionService extends BroadcastReceiver {
    /**
     * <h1>Ownership</h1>
     * <p>This Application is Owned by Ashish Kumar, Delhi Technology univerity, Btech ,Mechanical
     * Engineering ,2ndyear Student</p>
     * <p>Finished First version on 17-Aug-2020</p>
     */

    @Override
    public void onReceive(Context context, Intent intent) {

        context.sendBroadcast(new Intent("TRACKS_TRACKS")
        .putExtra("actionname",intent.getAction()));

    }
}
