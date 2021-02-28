package com.musicstreaming.musicstreaming;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class update_application {

    Context context;
    public update_application(Context context) {
        this.context=context;
    }

    public void samsung_store_update(){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://galaxy.store/streaming"));
        context.startActivity(browserIntent);
    }
}
