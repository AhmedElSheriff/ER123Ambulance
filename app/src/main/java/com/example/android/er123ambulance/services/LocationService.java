package com.example.android.er123ambulance.services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.example.android.er123ambulance.firebase.FirebaseHandler;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;

import de.greenrobot.event.EventBus;

/**
 * Created by Abshafi on 4/9/2017.
 */

public class LocationService extends Service {


    private LocationManager locationManager;
    private MyLocationListener myLocationListener;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("TAGKEY2","Inside On Create LocationService");

        this.locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        myLocationListener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, myLocationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0, myLocationListener);

    }

    public class MyLocationListener implements LocationListener {


        @Override
        public void onLocationChanged(Location location) {

            HashMap<String,Object> hashMap = new HashMap<>();
            hashMap.put("latPosition",Double.toString(location.getLatitude()));
            hashMap.put("longPosition",Double.toString(location.getLongitude()));
            FirebaseHandler.updateLocation(hashMap, FirebaseAuth.getInstance().getCurrentUser().getEmail());
            Log.e("TAGKEY2","Inside Location Service");
            EventBus.getDefault().post(location);

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
    }
}
