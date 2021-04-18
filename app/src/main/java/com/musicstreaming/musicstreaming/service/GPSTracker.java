package com.musicstreaming.musicstreaming.service;


import android.Manifest;
import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
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

import com.musicstreaming.musicstreaming.Exceptionhandler;
import com.musicstreaming.musicstreaming.internal_error_report;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.musicstreaming.musicstreaming.playselectedsong.SONG_ACTIVITY;
import static com.musicstreaming.musicstreaming.splash.SPLASH_ACTIVITY;
import static com.musicstreaming.musicstreaming.splash.USERNAME;
import static com.musicstreaming.musicstreaming.splash.sharedPreferences;

public class GPSTracker extends JobService implements LocationListener {

    String TAG = "gps_location";
    LocationManager locationManager;
    public static boolean IS_JOB_SERVICE_RUNNING=false;
    public static String[] addr_location;
    Context context;
    int MIN_TIME=30*60*1000, MIN_DISTANCE=50;
    JobParameters parameters;

    public GPSTracker() {
        Thread.setDefaultUncaughtExceptionHandler(new Exceptionhandler(SPLASH_ACTIVITY));
        context=SPLASH_ACTIVITY;
    }

    @Override
    public void onLocationChanged(Location location) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            if(location!=null) {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses.size() > 0) {
                    addr_location = new String[]{addresses.get(0).getCountryCode(), addresses.get(0).getAdminArea(), addresses.get(0).getSubAdminArea(),
                            addresses.get(0).getLocality(), addresses.get(0).getSubLocality(), addresses.get(0).getPostalCode(),
                            "" + addresses.get(0).getLongitude(), "" + addresses.get(0).getLatitude(), addresses.get(0).getAddressLine(0)};

                    Log.d(TAG, "onLocationChanged: location changed set 3");

                }
            }else {

                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                try {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                    if (location != null) {
                        geocoder = new Geocoder(context, Locale.getDefault());
                        try {
                            List<Address> addressess = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            if (addressess != null) {
                                if (addressess.size() > 0) {
                                    addr_location = new String[]{addressess.get(0).getCountryCode(), addressess.get(0).getAdminArea(), addressess.get(0).getSubAdminArea(),
                                            addressess.get(0).getLocality(), addressess.get(0).getSubLocality(), addressess.get(0).getPostalCode(),
                                            "" + addressess.get(0).getLongitude(), "" + addressess.get(0).getLatitude(), addressess.get(0).getAddressLine(0)};

                                    Log.d(TAG, "Get_Location: location changed set 4");
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (SecurityException ex) {
                    ex.printStackTrace();
                }
            }

        }catch (IOException  e){
            Log.d(TAG, "onLocationChanged: "+e.getMessage());
        }
        jobFinished(parameters,true);
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
            assert bestprovider != null;
            locationManager.requestLocationUpdates(bestprovider,MIN_TIME,MIN_DISTANCE,this);
            
            Location location = locationManager.getLastKnownLocation(bestprovider);
            if (location!=null){
                try {
                    Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                    if(addresses!=null){
                        if(addresses.size()>0){
                            addr_location = new String[]{addresses.get(0).getCountryCode(),addresses.get(0).getAdminArea(),addresses.get(0).getSubAdminArea(),
                                    addresses.get(0).getLocality(), addresses.get(0).getSubLocality(),addresses.get(0).getPostalCode(),
                                    ""+addresses.get(0).getLongitude(),""+addresses.get(0).getLatitude(),addresses.get(0).getAddressLine(0)};
                            Log.d(TAG, "Get_Location: location changed set 1");
                        }
                        else
                            if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME,MIN_DISTANCE,this);
                            location=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                            if(location!=null) {
                                geocoder = new Geocoder(context, Locale.getDefault());
                                try {
                                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    if(addresses!=null) {
                                        if (addresses.size() > 0) {
                                            addr_location = new String[]{addresses.get(0).getCountryCode(), addresses.get(0).getAdminArea(), addresses.get(0).getSubAdminArea(),
                                                    addresses.get(0).getLocality(), addresses.get(0).getSubLocality(), addresses.get(0).getPostalCode(),
                                                    "" + addresses.get(0).getLongitude(), "" + addresses.get(0).getLatitude(), addresses.get(0).getAddressLine(0)};

                                            Log.d(TAG, "Get_Location: location changed set 2");
                                        }
                                    }
                                }catch (IOException e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    }else
                        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME,MIN_DISTANCE,this);
                            location=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                            if(location!=null) {
                                geocoder = new Geocoder(context, Locale.getDefault());
                                try {
                                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    if(addresses!=null) {
                                        if (addresses.size() > 0) {
                                            addr_location = new String[]{addresses.get(0).getCountryCode(), addresses.get(0).getAdminArea(), addresses.get(0).getSubAdminArea(),
                                                    addresses.get(0).getLocality(), addresses.get(0).getSubLocality(), addresses.get(0).getPostalCode(),
                                                    "" + addresses.get(0).getLongitude(), "" + addresses.get(0).getLatitude(), addresses.get(0).getAddressLine(0)};
                                        }
                                    }
                                }catch (IOException e){
                                    e.printStackTrace();
                                }


                            }
                        }


                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{
                Log.d(TAG, "Get_Location: null location found ");
            }
        }catch (SecurityException e){
            Log.d(TAG, "Get_Location: "+e.getMessage());
        }

     }
    public void Check_Location_Enabled(){

        try {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            boolean gpsenabled = false;
            boolean networkenabled = false;


            try {
                gpsenabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception e) {
                Log.d(TAG, "Check_Location_Enabled: gpslocation " + e.getMessage());
            }

            try {
                networkenabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception e) {
                Log.d(TAG, "Check_Location_Enabled: networkenable " + e.getMessage());
            }

            if (gpsenabled && networkenabled) {

                Get_Location();
            }
        }catch (Exception e){
            new internal_error_report(context,"Error in GPSTracker: "+e.getMessage(),sharedPreferences.getString(USERNAME,""));
        }
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        parameters=params;
        IS_JOB_SERVICE_RUNNING=true;
        try {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Check_Location_Enabled();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
