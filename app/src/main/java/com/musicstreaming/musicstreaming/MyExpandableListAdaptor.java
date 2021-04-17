package com.musicstreaming.musicstreaming;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.musicstreaming.musicstreaming.MainActivity.MAIN_ACTIVITY;
import static com.musicstreaming.musicstreaming.MainActivity.MAIN_ACTIVITY_CONTEXT;
import static com.musicstreaming.musicstreaming.login.SHARED_PREF;
import static com.musicstreaming.musicstreaming.login.USERNAME;
import static com.musicstreaming.musicstreaming.make_custom_playlist.MAKE_NEW_PLAYLIST;
import static com.musicstreaming.musicstreaming.service.online_status_updater.CURRENT_ACTIVITY_CONTEXT;

public class MyExpandableListAdaptor extends BaseExpandableListAdapter {

    Context context;
    Map<Integer,List<child_item_expandable_listview>> child_items;
    List<Integer> group_item;
    String TAG="getlostofhere";

    public MyExpandableListAdaptor(Context context, Map<Integer,List<child_item_expandable_listview>> child_items, List<Integer> group_item) {
        this.context = context;
        this.child_items=child_items;
        this.group_item = group_item;
        notifyDataSetChanged();

//        Thread.setDefaultUncaughtExceptionHandler(new Exceptionhandler(MAIN_ACTIVITY));
    }

    @Override
    public int getGroupCount() {
        return group_item.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return child_items.get(groupPosition).size();
    }

    @Override
    public Integer getGroup(int groupPosition) {
        return group_item.get(groupPosition);
    }

    @Override
    public child_item_expandable_listview getChild(int groupPosition, int childPosition) {
        return child_items.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        if(CURRENT_ACTIVITY_CONTEXT==MAIN_ACTIVITY_CONTEXT && group_item.size()>0) {

            convertView = LayoutInflater.from(MAIN_ACTIVITY_CONTEXT).inflate(R.layout.group_item_expandable_listview, null, true);


            TextView tvstatus = convertView.findViewById(R.id.tvStatus);
            TextView noOfOnline = convertView.findViewById(R.id.noOfOnline);
            tvstatus.setTypeface(null, Typeface.BOLD);
            if (groupPosition == 0) {
                tvstatus.setText("Online");
            } else {
                tvstatus.setText("Offline");
            }
            noOfOnline.setText(""+group_item.get(groupPosition));
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        if(CURRENT_ACTIVITY_CONTEXT==MAIN_ACTIVITY_CONTEXT && child_items.size()>0) {

            convertView = LayoutInflater.from(MAIN_ACTIVITY_CONTEXT).inflate(R.layout.child_items_expandable_listview, null, true);

            final Context context = convertView.getContext();

            final SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF,Context.MODE_PRIVATE);

            TextView name = convertView.findViewById(R.id.frd_name);
            ImageView profile_image = convertView.findViewById(R.id.profile_image_frd);
            TextView status_viewer = convertView.findViewById(R.id.status_shower);
            TextView frd_playlist = convertView.findViewById(R.id.frd_playlist);
            ImageButton unfriend = convertView.findViewById(R.id.unfrd);



            if(childPosition<getChildrenCount(groupPosition)) {

                Log.d(TAG, "getChildView: executed this line of code "+getChildrenCount(groupPosition)+" : "+groupPosition+" : "+String.valueOf(getChild(groupPosition, childPosition).getName()==null));

                try {
                    Log.d(TAG, "getChildView: getting over sized grp pos : " + groupPosition + "child pos: " + childPosition + " child size: " + getChildrenCount(groupPosition) + " name: " + getChild(groupPosition, childPosition).getName());

                    name.setText(getChild(groupPosition, childPosition).getName());
                    frd_playlist.setText(getChild(groupPosition, childPosition).getPlaylist_name());

                    if (getChild(groupPosition, childPosition).getStatus().equals("0")) {
                        status_viewer.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    } else if (getChild(groupPosition, childPosition).getStatus().equals("1")) {
                        status_viewer.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                    }
                    loadimage(getChild(groupPosition, childPosition).getImage(), profile_image);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else {
                Log.d(TAG, "getChildView: ERR getting over sized grp pos : "+groupPosition+ "child pos: "+childPosition+" child size: "+getChildrenCount(groupPosition));
            }

         final int grpos=groupPosition,chpos=childPosition;
            unfriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new AlertDialog.Builder(context)
                            .setMessage("Do you want to unfriend "+getChild(grpos,chpos).getName())
                            .setCancelable(true)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new qr_code.makeFriend(context,getChild(grpos,chpos).getUsername(),sharedPreferences.getString(USERNAME,""),"1",MAIN_ACTIVITY_CONTEXT).execute();
                                    List<child_item_expandable_listview> child= child_items.get(grpos);
                                    child.remove(chpos);
                                    group_item.set(grpos,getGroup(grpos)-1);
                                    notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("No",null)
                            .create()
                            .show();


                }
            });

        }else{
            Log.d("checking_data ", "getChildView: child size is "+child_items.size());
        }
            return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void loadimage(String url,ImageView profileimage){
        final CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(MAIN_ACTIVITY_CONTEXT);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(40f);
        circularProgressDrawable.start();

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(circularProgressDrawable);
        requestOptions.skipMemoryCache(true);
        requestOptions.circleCrop();
        requestOptions.priority(Priority.HIGH);
        requestOptions.fitCenter();

        try {
            Glide.with(MAIN_ACTIVITY_CONTEXT)
                    .load(url)
                    .apply(requestOptions)
                    .apply(RequestOptions.circleCropTransform())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                            String err = "Error in loading profile Image " + e.getMessage();
                            Log.d("check_online_status", "onLoadFailed: error loading image "+err);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                            Log.d("check_online_status", "onResourceReady: ready to move");

                            return false;
                        }
                    })
                    .into(profileimage);
        }catch (Exception e){
            Log.d("check_online_status", "loadimage: "+e.getMessage());
//            new internal_error_report(MAIN_ACTIVITY_CONTEXT,"Error in MainActivity loading profile image : "+e.getMessage(),context.sharedPreferences.getString(USERNAME,"")).execute();
        }
    }
}
