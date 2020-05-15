package com.example.visualpost_it.dtos;

import com.google.android.gms.maps.model.LatLng;

public class Place{

    private String placeName;
    private LatLng latLng;
    private float distance;

    public Place(String placeName, LatLng latLng, float distance) {
        this.placeName = placeName;
        this.latLng = latLng;
        this.distance = distance;
    }


    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    @Override
    public String toString() {
        return "Place{" +
                "placeName='" + placeName + '\'' +
                ", lat=" +  latLng.latitude +
                ", lng=" +  latLng.longitude +
                ", distance=" +  distance +
                '}';
    }
}
