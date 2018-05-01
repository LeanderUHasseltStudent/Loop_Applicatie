package com.example.vanca.loop_application;

import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Van Cappellen Leander
 */

public class DataHandler {
    private ArrayList<Location> locations;
    private ArrayList<Double> altitude;
    private Long time;
    private double totalDistance;
    private double maxAltitude;
    private double minAltitude;

    public DataHandler( ArrayList<Location> locations, ArrayList<Double> altitude, Long time){
        this.locations = locations;
        this.altitude = altitude;
        this.time = time;
    }

    public double getDistance() {
        double distance = 0;
        double latitude1 = 0;
        double latitude2 = 0;
        double longitude1 = 0;
        double longitude2 = 0;
        double elevation1 = 0;
        double elevation2 = 0;
        int teller = 0;

        for(Location location : locations) {
            latitude1 = location.getLatitude();
            longitude1 = location.getLongitude();
            if (teller <= altitude.size()){
                elevation1 = altitude.get(teller);
            }
            if (teller > 0) {
                distance = distance + distance(latitude1, latitude2, longitude1, longitude2, elevation1, elevation2);
            }
            latitude2 = latitude1;
            longitude2 = longitude1;
            elevation2 = elevation1;
            teller = teller+1;
        }
        return distance;
    }
    public double distance(double lat1, double lat2, double lon1, double lon2, double el1, double el2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000;
        double height = el1 - el2;
        distance = Math.pow(distance, 2) + Math.pow(height, 2);
        totalDistance = distance;
        return Math.sqrt(distance);
        }

    public double getVelocity(){
        double velocity;
        velocity = (totalDistance*1000)/time;
        return velocity;
    }

    public String getTime(){
        int seconds = (int) (time / 1000) % 60 ;
        int minutes = (int) ((time / (1000*60)) % 60);
        int hours   = (int) ((time / (1000*60*60)) % 24);
        return String.format("%d:%02d:%02d", hours, minutes, seconds);
    }

    public void checkAltitude(){
        int teller = 0;
        for(Double Hight : altitude){
            if (teller == 0){
                maxAltitude = Hight;
                minAltitude = Hight;
            }
            if (Hight > maxAltitude){
                maxAltitude = Hight;
            }
            if (Hight < minAltitude){
                minAltitude = Hight;
            }
            teller++;
        }
    }

    public double getMaxAltitude(){
        return maxAltitude;
    }

    public double getMinAltitude(){
        return minAltitude;
    }
}
