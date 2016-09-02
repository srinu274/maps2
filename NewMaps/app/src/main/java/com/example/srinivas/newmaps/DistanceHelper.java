package com.example.srinivas.newmaps;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.LauncherApps;
import android.location.Location;
import android.preference.PreferenceManager;

/**
 * Created by Srinivas on 01-09-2016.
 */
public class DistanceHelper {

    private static final String LOCATION_DISTANCE = "locationdistance";
    private static final String LOCATION_PREVIOUS_LATITUDE ="locationPreviousLatitude";
    private static final String LOCATION_PREVIOUS_LONGITUDE="locationPreviousLongitude";

    public static int getDistanceTravled(Context context) {
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(LOCATION_DISTANCE,0);
    }

    public static void setLocationDistance(Context context,int distance) {
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putInt(LOCATION_DISTANCE,distance).apply();
    }

    public static void setLocationPrevious(Context context,Location location) {
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(LOCATION_PREVIOUS_LATITUDE,location.getLatitude()+"").apply();
        sharedPreferences.edit().putString(LOCATION_PREVIOUS_LONGITUDE,location.getLongitude()+"").apply();
    }

    public static void setLocationPrevious(Context context,LocationHandler location) {
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(LOCATION_PREVIOUS_LATITUDE,location.getLatitude()+"").apply();
        sharedPreferences.edit().putString(LOCATION_PREVIOUS_LONGITUDE,location.getLongitude()+"").apply();
    }

    public static LocationHandler getLocationPrevious(Context context) {
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
        double latitude=Double.valueOf(sharedPreferences.getString(LOCATION_PREVIOUS_LATITUDE,"0"));
        double longitude=Double.valueOf(sharedPreferences.getString(LOCATION_PREVIOUS_LONGITUDE,"0"));
        return new LocationHandler(latitude,longitude);
    }
}
