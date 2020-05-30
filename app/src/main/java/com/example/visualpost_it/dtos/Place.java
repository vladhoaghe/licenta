package com.example.visualpost_it.dtos;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class Place {

    private static final String TAG = "Place";

    private String placeName;
    private String type;
    private String placeId;
    private String placeReference;
    private String photoReference;
    private double latitude;
    private double longitude;
    private float distance;
    private boolean isFavorite;
    private Bitmap placePhoto;

    public Place(){
    }

    public Place(String placeName, double latitude, double longitude, float distance,
                 String type, String photoReference, String placeReference) {
        this.placeName = placeName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.type = type;
        this.photoReference = photoReference;
        this.placeReference = placeReference;
    }

    //constructor without distance
    public Place(String placeName, double latitude, double longitude, String type, String photoReference, String placeReference) {
        this.placeName = placeName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
        this.placeReference = placeReference;
        this.photoReference = photoReference;
    }

    public Bitmap getPlacePhoto() {
        return placePhoto;
    }

    public void setPlacePhoto(Bitmap placePhoto) {
        this.placePhoto = placePhoto;
    }

    public String getPlaceReference() {
        return placeReference;
    }

    public void setPlaceReference(String placeReference) {
        this.placeReference = placeReference;
    }

    public String getPhotoReference() {
        return photoReference;
    }

    public void setPhotoReference(String photoReference) {
        this.photoReference = photoReference;
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

    @NotNull
    @Override
    public String toString() {
        return "Place{" +
                "placeName='" + placeName + '\'' +
                ", type='" + type + '\'' +
                ", placeId='" + placeId + '\'' +
                ", placeReference='" + placeReference + '\'' +
                ", photoReference='" + photoReference + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", distance=" + distance +
                ", isFavorite=" + isFavorite +
                ", placePhoto=" + placePhoto +
                '}';
    }
}
