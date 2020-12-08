package com.example.musicstreaming;

import android.content.Context;
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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import java.util.List;

public class all_song_adaptor extends ArrayAdapter<list_of_all_songs> {

    Context context;
    List<list_of_all_songs> arraylistofsongs;
    ImageView img_view;
    TextView name,singer;
    String TAG="all_songs";


    public all_song_adaptor(@NonNull Context context, List<list_of_all_songs> arraylistofsongs) {
        super(context,R.layout.custom_make_custom_playlist,arraylistofsongs);
        this.arraylistofsongs=arraylistofsongs;
        this.context=context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_make_custom_playlist,null,true);

        img_view=view.findViewById(R.id.song_img);
        name=view.findViewById(R.id.song_name);
        singer=view.findViewById(R.id.singer_name);

        loadimage(arraylistofsongs.get(position).getImage());
        name.setText(arraylistofsongs.get(position).getName());
        singer.setText(arraylistofsongs.get(position).getSinger());

        return view;
    }

    public  void  loadimage(String url){

        final CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(circularProgressDrawable);
        requestOptions.skipMemoryCache(true);
        requestOptions.onlyRetrieveFromCache(false);
        //requestOptions.circleCrop();
        requestOptions.priority(Priority.HIGH);
        requestOptions.fitCenter();

        Glide.with(context)
                .load(url)
                .apply(requestOptions)
                .transform(new RoundedCorners(20))
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
                .into(img_view);
    }

}
