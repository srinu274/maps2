package com.example.srinivas.newmaps;

import android.location.Location;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Srinivas on 01-09-2016.
 */
public class PlacesSearchHelper {

    private static final String URL ="https://maps.googleapis.com/maps/api/place/textsearch/json?";
    private static final String API_KEY="AIzaSyBorQzHgmz4g42FSfXQ8ydZQJ7754LBsGA";

    private static String fetchPlacesData(double lat,double lng) {
        BufferedReader input = null;
        try {
            //fetching cafes within 100m radius
            String requestUrl= URL +"location="+lat+","+lng+"&radius=500&type=cafe&key="+API_KEY;
            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Log.i("getdata","Success");
                input = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                Log.i("getdata response code", connection.getResponseCode() + "");
                StringBuilder builder = new StringBuilder();
                int ch;
                while ((ch = input.read()) != -1) {
                    builder.append((char) ch);
                }
                Log.i("getData",builder.toString());
                return builder.toString();
            } else {
                Log.i("getdata","Failure");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (input != null) {
                try {
                    input.close();
                } catch (Exception e) {

                }
            }
        }
        return null;
    }

    private static ArrayList<LocationHandler> parseLocationHandlers(String json) {
        ArrayList<LocationHandler> handlers=new ArrayList<>();
        try {
            JSONObject object = new JSONObject(json);
            JSONArray array=object.getJSONArray("results");
            JSONObject present=null;
            String name;
            Double lat,lng;
            for(int i=0;i<array.length();i++) {
                present=array.getJSONObject(i);
                lat=present.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                lng=present.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                name=present.getString("name");
                handlers.add(new LocationHandler(name,lat,lng,System.currentTimeMillis()));
            }
        } catch (JSONException je) {
            Log.i("parseLocation error",je.toString());
            je.printStackTrace();
        }
        return handlers;
    }


    public static ArrayList<LocationHandler> getLocationHandlers(Location location) {
        if(location==null) return null;
        String json=fetchPlacesData(location.getLatitude(),location.getLongitude());
        if(TextUtils.isEmpty(json)) return null;
        return parseLocationHandlers(json);
    }
}
