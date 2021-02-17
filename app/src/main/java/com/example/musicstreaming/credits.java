package com.example.musicstreaming;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class credits extends Fragment {

    /**
     * <h1>Ownership</h1>
     * <p>This Application is Owned by Ashish Kumar, Delhi Technology univerity, Btech ,Mechanical
     * Engineering ,2ndyear Student</p>
     * <p>Finished First version 1.0 on 17-Aug-2020</p>
     */

    RelativeLayout contact_info;
    Animation appear;
    Context context;

    public credits() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_credits, container, false);

       context=view.getContext();

       contact_info=view.findViewById(R.id.contact_info);
       appear= AnimationUtils.loadAnimation(context,R.anim.appear);
       contact_info.startAnimation(appear);

        return view;
    }
    
}
