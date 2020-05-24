package com.example.visualpost_it.dtos;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class Place {

    private String placeName;
    private double latitude;
    private double longitude;
    private float distance;
    private String type;
    private String placeId;
    public boolean isFavorite;

    public Place(){
    }

    public Place(String placeName, double latitude, double longitude, float distance, String type) {
        this.placeName = placeName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.type = type;
    }

    public Place(String placeName, double latitude, double longitude, String type) {
        this.placeName = placeName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
//
//    public boolean isFavorite() {
//        return isFavorite;
//    }
//
//    public void setFavorite(boolean favorite) {
//        isFavorite = favorite;
//    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
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
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", distance=" + distance +
                ", type='" + type + '\'' +
                ", placeId='" + placeId + '\'' +
                ", isFavorite=" + isFavorite +
                '}';
    }
}
