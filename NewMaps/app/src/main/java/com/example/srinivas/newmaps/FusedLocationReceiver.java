package com.example.srinivas.newmaps;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.request.DisableFitRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Srinivas on 26-04-2016.
 */
public class FusedLocationReceiver extends BroadcastReceiver {

    private static GoogleApiClient mGoogleApiClient;
    public static final String ACTION_FUSED_LOCATION = "FusedLocationReceiver.Location";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("FusedLocationReceiver","in receiver");
        if(!LocationResult.hasResult(intent)) {
            Log.i("FusedLocationReceiver","no data "+intent.toString());
            return;
        }
        Log.i("FusedLocationReceiver","received ");
        LocationResult result= LocationResult.extractResult(intent);
        Location location=result.getLastLocation();
        if(location!=null) {
            setDistanceAndLocation(context,location);
            Intent service = new Intent(context, PlacesSearchService.class);
            service.putExtra(PlacesSearchService.EXTRA_LOCATION,location);
            context.startService(service);
            Log.i("FusedLocationReceiver","true");
        } else {
            Log.i("FusedLocationReceiver","false");
        }
    }

    public static void setDistanceAndLocation(Context context,Location location) {
        DistanceHelper.setLocationPrevious(context,location);
        int distance=DistanceHelper.getDistanceTravled(context);
        DistanceHelper.setLocationDistance(context,distance+100);
    }
}
