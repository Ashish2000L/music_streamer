package com.example.musicstreaming;

import android.content.Context;
import android.graphics.ColorSpace;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.Model;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.List;
import java.util.Locale;

public class playlistadapter extends ArrayAdapter<listofplaylist> {
    /**
     * <h1>Ownership</h1>
     * <p>This Application is Owned by Ashish Kumar, Delhi Technology univerity, Btech ,Mechanical
     * Engineering ,2ndyear Student</p>
     * <p>Finished First version 1.0 on 17-Aug-2020</p>
     */
    Context context1;
    List<listofplaylist> arraylistofplaylist;
    TextView playlist_name,note;
    ImageView playlist_image;
    String TAG="loaginanimage";

    public playlistadapter(@NonNull Context context, List<listofplaylist> arraylistofplaylist) {
        super(context,R.layout.custom_playlist,arraylistofplaylist);
        this.context1=context;
        this.arraylistofplaylist=arraylistofplaylist;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_playlist,null,true);

        playlist_name = view.findViewById(R.id.playlist_name);
        playlist_image = view.findViewById(R.id.playlist_image);
        note = view.findViewById(R.id.note_playlist);

        playlist_name.setText(arraylistofplaylist.get(position).getName());
        note.setText(arraylistofplaylist.get(position).getNote());
        loadimage(arraylistofplaylist.get(position).getImage());

        return view;
    }

    public  void  loadimage(String url){

        final CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context1);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(circularProgressDrawable);
        requestOptions.skipMemoryCache(true);
        requestOptions.onlyRetrieveFromCache(false);
        requestOptions.priority(Priority.HIGH);
        requestOptions.fitCenter();

        Glide.with(context1)
                .load(url)
                .apply(requestOptions)
                .transform(new RoundedCorners(20))
                .placeholder(R.drawable.playlist_1)
                .transition(DrawableTransitionOptions.withCrossFade())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        assert e != null;
                        //Toast.makeText(contexts, e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onLoadFailed: "+e.getMessage());
                        circularProgressDrawable.stop();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        //Toast.makeText(contexts, "Image Loaded", Toast.LENGTH_SHORT).show();
                        circularProgressDrawable.stop();
                        Log.d(TAG, "onResourceReady: loaded image");
                        return false;
                    }
                })
                .into(playlist_image);
    }
}
