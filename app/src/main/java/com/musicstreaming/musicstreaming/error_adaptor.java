package com.musicstreaming.musicstreaming;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class error_adaptor extends ArrayAdapter<listoferror> {

    List<listoferror> arrayoferrors;
    public  Context context1;
    TextView songname,singer;
    public ImageView like;
    public static String songnames,username;
    LinearLayout main_layout;

    public error_adaptor(@NonNull Context context, List<listoferror> arrayoferrors) {
        super(context, R.layout.custom_songs,arrayoferrors);

        this.arrayoferrors=arrayoferrors;
        context1=context;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_songs,null,true);

        songname = view.findViewById(R.id.songname);
        like=view.findViewById(R.id.likesong);
        singer = view.findViewById(R.id.singername);
        main_layout=view.findViewById(R.id.main_layout);

        songnames=arrayoferrors.get(position).getName();
//        final String likes=arrayoferrors.get(position).getLikes();

        like.setVisibility(View.INVISIBLE);
        if(arrayoferrors.get(position).getStatus().equals("0")){
            makebackground("#99E64D4D");
        }else if(arrayoferrors.get(position).getStatus().equals("1")){
            makebackground("#9900E676");
        }

        songname.setText(arrayoferrors.get(position).getName());
        singer.setText(arrayoferrors.get(position).getSinger());

        return view;
    }

    public void makebackground(String color){

        int[] colors = new int[3];
        colors[0] = Color.parseColor(color);
        colors[1] = Color.parseColor("#00FFFFFF");
        colors[2]=Color.parseColor("#00FFFFFF");

        GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, colors);
        gd.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gd.setGradientRadius(300f);
        gd.setCornerRadius(0f);
        main_layout.setBackground(gd);

    }
}
