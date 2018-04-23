package com.example.vanca.loop_application;

import android.location.Location;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by vanca on 4/18/2018.
 */

public class DataHandler {
    private ArrayList<Location> locations;
    private ArrayList<Double> altitude;

    public DataHandler( ArrayList<Location> locations, ArrayList<Double> altitude){
        this.locations = locations;
        this.altitude = altitude;
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
        public static double distance(double lat1, double lat2, double lon1,
        double lon2, double el1, double el2) {
            final int R = 6371; // Radius of the earth

            double latDistance = Math.toRadians(lat2 - lat1);
            double lonDistance = Math.toRadians(lon2 - lon1);
            double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                    + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                    * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double distance = R * c * 1000; // convert to meters

            double height = el1 - el2;

            distance = Math.pow(distance, 2) + Math.pow(height, 2);

            return Math.sqrt(distance);
        }
}
