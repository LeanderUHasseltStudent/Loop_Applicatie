package com.example.vanca.loop_application;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by Van Cappellen Leander
 */

public class ChildActivity_LoopData extends AppCompatActivity {
    private TextView textViewName;
    private TextView textViewDistance;
    private TextView textViewTime;
    private TextView textViewVelocity;
    private TextView textViewMaxAltitude;
    private TextView textViewMinAltitude;
    private String eenheid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child__loop_data);

        textViewDistance = (TextView)findViewById(R.id.distance);
        textViewTime = (TextView)findViewById(R.id.time);
        textViewVelocity = (TextView)findViewById(R.id.velocity);
        textViewMaxAltitude = (TextView)findViewById(R.id.maxAltitude);
        textViewMinAltitude = (TextView)findViewById(R.id.minAltitude);

        Intent intent = getIntent();
        String nameText = intent.getStringExtra("Intent.EXTRA_TEXT1");
        String distanceText = intent.getStringExtra("Intent.EXTRA_TEXT2");
        String timeText = intent.getStringExtra("Intent.EXTRA_TEXT3");
        String velocityText = intent.getStringExtra("Intent.EXTRA_TEXT4");
        String minAltitudeText = intent.getStringExtra("Intent.EXTRA_TEXT5");
        String maxAltitudeText = intent.getStringExtra("Intent.EXTRA_TEXT6");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String sortBy = prefs.getString("PREF_LIST", "Meter");

        if (sortBy.equals("Kilometer")){
            double d = Double.parseDouble(distanceText);
            double distanceInKm = (d/1000);
            distanceText = Double.toString(distanceInKm);

            double f = Double.parseDouble(minAltitudeText);
            double minAltitudeInKm = (f/1000);
            minAltitudeText = Double.toString(minAltitudeInKm);

            double g = Double.parseDouble(maxAltitudeText);
            double maxAltitudeInKm = (g/1000);
            maxAltitudeText = Double.toString(maxAltitudeInKm);

            eenheid = "km";
        }
        else if(sortBy.equals("Mile")){
            double d = Double.parseDouble(distanceText);
            double distanceInMile = (d*0.000621371192);
            distanceText = Double.toString(distanceInMile);

            double f = Double.parseDouble(minAltitudeText);
            double minAltitudeInKm = (f*0.000621371192);
            minAltitudeText = Double.toString(minAltitudeInKm);

            double g = Double.parseDouble(maxAltitudeText);
            double maxAltitudeInKm = (g*0.000621371192);
            maxAltitudeText = Double.toString(maxAltitudeInKm);

            eenheid = "mile";
        }
        else {
            eenheid = "m";
        }

        double d = Double.parseDouble(velocityText);
        double velocityKmPerU = (d*3.6);
        velocityText = Double.toString(velocityKmPerU);

        setTitle(nameText);
        textViewDistance.setText(distanceText + " " + eenheid);
        textViewTime.setText(timeText);
        textViewVelocity.setText(velocityText +" km/u");
        textViewMaxAltitude.setText(minAltitudeText + " " + eenheid);
        textViewMinAltitude.setText(maxAltitudeText + " " + eenheid);
    }
}
