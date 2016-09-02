package com.example.srinivas.newmaps;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.util.Log;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Srinivas on 26-04-2016.
 */
public class PlacesSearchService extends IntentService {
    private static final String TAG="PlacesSearchService";
    public static final String EXTRA_LOCATION="FusedLocation.Extra";
    public static final String ACTION_PLACES="com.example.srinivas.newmaps.places";
    public static final String EXTRA_PLACES="extra_places";

    public PlacesSearchService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("service loc","loca");
        Location location=intent.getParcelableExtra(EXTRA_LOCATION);
        if(location!=null) {
            Log.i("service loc",""+location.getLongitude());
            ArrayList<LocationHandler> locations=PlacesSearchHelper.getLocationHandlers(location);
            Log.i("p received","location size"+(locations!=null?locations.size():0));
            Intent i=new Intent(ACTION_PLACES);
            i.putExtra(EXTRA_PLACES,locations);
            sendBroadcast(i);
        } else {
            Log.i("service loc","failed");
        }
    }


}
