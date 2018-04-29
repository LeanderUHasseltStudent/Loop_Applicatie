package com.example.vanca.loop_application;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;

import static com.example.vanca.loop_application.MainActivity_Loop_Applicatie.BROADCAST_ACTION;

/**
 * Created by vanca on 4/23/2018.
 */

public class LocationService extends Service {
    private LocationListener listener;
    private LocationManager locationManager;
    private ArrayList<Location> locations = new ArrayList<Location>();
    private int updateSnelheid;
    private String nauwkeurigheid;
    boolean isRunning = true;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {

        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle("nkDroid Music Player")
                    .setTicker("nkDroid Music Player")
                    .setContentText("nkDroid Music")
                    .setOngoing(true).build();

            startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
        }

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                Log.d("error", "///////////////////////////////////////////////////////////onStatusChanged: ");
            }

            @Override
            public void onProviderEnabled(String s) {
                Log.d("error", "///////////////////////////////////////////////////////////provider enabled: ");
            }

            @Override
            public void onProviderDisabled(String s) {
                Log.d("error", "///////////////////////////////////////////////////////////provider disabled: ");
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        //noinspection MissingPermission
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,listener);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        nauwkeurigheid = prefs.getString("PREF_LIST2", "Normal");
        Log.d("lol", "///////////////////////////////////////////////////////////provider disabled: " + nauwkeurigheid);

        if (nauwkeurigheid.equals("Low")){
            updateSnelheid = 30000;
        }
        else if (nauwkeurigheid.equals("High")){
            updateSnelheid = 10000;
        }
        else{
            updateSnelheid = 20000;
        }

        new Thread(new Runnable() {
            public void run(){
                while(isRunning == true) {
                    Intent broadCastIntent = new Intent();
                    broadCastIntent.setAction(BROADCAST_ACTION);
                    Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    broadCastIntent.putExtra("data", loc);
                    sendBroadcast(broadCastIntent);
                    try {
                        Thread.sleep(updateSnelheid);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }).start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        if(locationManager != null){
            //noinspection MissingPermission
            locationManager.removeUpdates(listener);
        }
        stopForeground(true);
    }
}
