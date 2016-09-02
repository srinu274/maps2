package com.example.srinivas.newmaps;

import java.io.Serializable;

/**
 * Created by Srinivas on 29-06-2016.
 */
public class LocationHandler implements Serializable{
    private double latitude;
    private double longitude;
    private long time;
    private String name;

    public LocationHandler() {

    }

    public LocationHandler(String name,double latitude,double longitude,long time) {
        this.name=name;
        this.latitude=latitude;
        this.longitude=longitude;
        this.time=time;
    }

    public LocationHandler(double latitude,double longitude) {
        this.latitude=latitude;
        this.longitude=longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public long getTime() {
        return time;
    }

    public void setLatitude(double latitude) {
        this.latitude=latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude=longitude;
    }

    public void setTime(long time) {
        this.time=time;
    }

    public void setName(String name) {this.name=name; }

    public String getName() {return name;}
}
