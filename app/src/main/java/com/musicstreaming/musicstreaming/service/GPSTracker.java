package com.musicstreaming.musicstreaming.service;


import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.musicstreaming.musicstreaming.splash.SPLASH_ACTIVITY;

public class GPSTracker implements LocationListener {

    String TAG = "gps_location";
    LocationManager locationManager;
    boolean last_output=false;
    public static String[] addr_location;
    Context context;
    Handler handler = new Handler();

    public GPSTracker(Context context) {
        this.context=context;

        runnable.run();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Get_Address();

            handler.postDelayed(this,50000);
        }
    };

    public void Get_Address(){
        Check_Location_Enabled();
    }

    @Override
    public void onLocationChanged(Location location) {

        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            Log.d(TAG, "onLocationChanged: address "+addresses.get(0).getAddressLine(0));

            addr_location = new String[]{addresses.get(0).getCountryCode(),addresses.get(0).getAdminArea(),addresses.get(0).getSubAdminArea(),
                    addresses.get(0).getLocality(), addresses.get(0).getSubLocality(),addresses.get(0).getPostalCode(),
                    ""+addresses.get(0).getLongitude(),""+addresses.get(0).getLatitude(),addresses.get(0).getAddressLine(0)};

        }catch (IOException e){
            Log.d(TAG, "onLocationChanged: "+e.getMessage());
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
     public boolean Check_Location_Permission(){
        return (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED );
     }

     public void Get_Location(){

        try{
            locationManager=(LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String bestprovider = locationManager.getBestProvider(criteria,true);
            locationManager.requestLocationUpdates(bestprovider,1000 * 60,5,this);
            
//            Location location = locationManager.getLastKnownLocation(bestprovider);
//            if (location!=null){
//                onLocationChanged(location);
//            }else{
//                Log.d(TAG, "Get_Location: null location found ");
//            }
        }catch (SecurityException e){
            Log.d(TAG, "Get_Location: "+e.getMessage());
        }

     }
    public void Check_Location_Enabled(){

        LocationManager locationManager =(LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gpsenabled=false;
        boolean networkenabled = false;


        try{
            gpsenabled=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch (Exception e){
            Log.d(TAG, "Check_Location_Enabled: gpslocation "+e.getMessage());
        }

        try{
            networkenabled=locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch (Exception e){
            Log.d(TAG, "Check_Location_Enabled: networkenable "+e.getMessage());
        }

        if(!gpsenabled && !networkenabled){

            new android.app.AlertDialog.Builder(context)
                    .setCancelable(false)
                    .setTitle("Enable GPS Location ")
                    .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            Get_Location();
                        }
                    })
                    .setNegativeButton("Later", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        }else{
            Get_Location();
        }
    }

}
