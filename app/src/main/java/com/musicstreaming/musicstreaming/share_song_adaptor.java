package com.musicstreaming.musicstreaming;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.OnLifecycleEvent;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.musicstreaming.musicstreaming.MainActivity.setSongCounter;
import static com.musicstreaming.musicstreaming.login.USERNAME;
import static com.musicstreaming.musicstreaming.service.online_status_updater.counter;

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

                if(Integer.parseInt(getValues(position).getStatus())==0) {
                    new changeStatusOfSharedSong(getValues(position).getId(), position).execute();
                    setSongCounter(counter-1);
                }
                context.startActivity(new Intent(context,songsfromplaylist.class).putExtra("is_from_search",true)
                        .putExtra("song_url",getValues(position).getSongUrl()));
            }
        });

        if(Integer.parseInt(getValues(position).getStatus())==0){
            markUnseen.setVisibility(View.VISIBLE);
            markAsRead.setVisibility(View.VISIBLE);
        }else{
            markUnseen.setVisibility(View.GONE);
            markAsRead.setVisibility(View.GONE);
        }

        senderName.setText(getValues(position).getFrdName());
        sendTime.setText(getValues(position).getDateOfShare());
        songName.setText(getValues(position).getSongName());
        singerName.setText(getValues(position).getSingerName());

        markAsRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new changeStatusOfSharedSong(getValues(position).getId(),position).execute();
                setSongCounter(counter-1);
            }
        });

        return view;
    }

    public class changeStatusOfSharedSong extends AsyncTask<Void,Void,Void>{

        String id;
        int pos;
        public changeStatusOfSharedSong(String id,int pos) {
            this.id=id;
            this.pos=pos;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            String url="https://rentdetails.000webhostapp.com/musicplayer_files/share_song/change_status.php";

            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    if(response.equals("success")) {
                        getValues(pos).setStatus("1");
                        notifyDataSetChanged();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    if(error.getMessage()!=null){
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    String err="Error in getdata in playlistfragment "+error.getMessage();
                    new internal_error_report(context,err,MainActivity.sharedPreferences.getString(USERNAME,"")).execute();

                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params=new HashMap<String, String>();

                    params.put("id",id );

                    return params;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(request);

            return null;
        }
    }

}
