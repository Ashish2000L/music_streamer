package com.musicstreaming.musicstreaming;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import static com.musicstreaming.musicstreaming.service.onclearfrompercentservice.TAG;

public class songadapter extends ArrayAdapter<listofsongs> {
    /**
     * <h1>Ownership</h1>
     * <p>This Application is Owned by Ashish Kumar, Delhi Technology univerity, Btech ,Mechanical
     * Engineering ,2ndyear Student</p>
     * <p>Finished First version 1.0 on 17-Aug-2020</p>
     */

    List<listofsongs> arrayofsongs;
    public static Context context1;
    TextView songname,singer;
    public ImageView like;
    public static String songnames,username;

    public songadapter(@NonNull Context context, List<listofsongs> arrayofsongs) {
        super(context, R.layout.custom_songs,arrayofsongs);

        this.arrayofsongs=arrayofsongs;
        context1=context;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_songs,null,true);

        songname = view.findViewById(R.id.songname);
        like=view.findViewById(R.id.likesong);
        singer = view.findViewById(R.id.singername);

        songnames=arrayofsongs.get(position).getName();
        final String likes=arrayofsongs.get(position).getLikes();

        if(arrayofsongs.get(position).getLikes().equals("1")){
            like.setImageResource(R.drawable.favourate_full);
        }else if(arrayofsongs.get(position).getLikes().equals("0")){
            like.setImageResource(R.drawable.favourate);
        }

        Log.d(TAG, "getView: name: "+songnames+" like: "+likes);

        songname.setText(arrayofsongs.get(position).getName());
        singer.setText(arrayofsongs.get(position).getSinger());

        return view;
    }
}
