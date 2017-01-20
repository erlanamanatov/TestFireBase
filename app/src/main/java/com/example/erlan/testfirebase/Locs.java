package com.example.erlan.testfirebase;

/**
 * Created by erlan on 20.01.2017.
 */

public class Locs {
    private double latitude;
    private double longitude;


    public Locs(){

    }

    public Locs(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
