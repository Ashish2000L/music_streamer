package com.musicstreaming.musicstreaming;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.OnLifecycleEvent;

import java.util.List;

public class share_song_adaptor extends ArrayAdapter<share_song_items> {

    List<share_song_items> share_song_itemsList;
    Context context;
    public share_song_adaptor(@NonNull Context context, List<share_song_items> share_song_items) {
        super(context, R.layout.custom_share_song_list,share_song_items);

        this.share_song_itemsList=share_song_items;
    }

    public share_song_items getValues(int position){
        return share_song_itemsList.get(position);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_share_song_list,null,true);

        TextView markUnseen,senderName,sendTime,songName,singerName;
        Button markAsRead,play;

        context=view.getContext();



        markUnseen=view.findViewById(R.id.unseen_song);
        senderName=view.findViewById(R.id.sender_name);
        sendTime=view.findViewById(R.id.send_time);
        songName=view.findViewById(R.id.song_name);
        singerName=view.findViewById(R.id.singers_name);
        markAsRead=view.findViewById(R.id.markasread);
        play=view.findViewById(R.id.play);


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getValues(position).setStatus("1");
                notifyDataSetChanged();
                context.startActivity(new Intent(context,songsfromplaylist.class).putExtra("is_from_search",true)
                        .putExtra("song_url",getValues(position).getSongUrl()));
            }
        });

        if(Integer.parseInt(getValues(position).getStatus())==0){
            markUnseen.setVisibility(View.VISIBLE);
        }else{
            markUnseen.setVisibility(View.GONE);
        }

        senderName.setText(getValues(position).getFrdName());
        sendTime.setText(getValues(position).getDateOfShare());
        songName.setText(getValues(position).getSongName());
        singerName.setText(getValues(position).getSingerName());

        markAsRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getValues(position).setStatus("1");
                notifyDataSetChanged();
            }
        });

        return view;
    }
}
