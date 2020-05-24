package com.example.visualpost_it.dtos;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class PlaceFavorite {
    private @ServerTimestamp Date timestamp;
    private String placeName;
    private double latitude;
    private double longitude;
    private String type;
    private String placeId;
    private boolean isFavorite;

    public PlaceFavorite(Date timestamp, String placeName, double latitude, double longitude, String type, String placeId, boolean isFavorite) {
        this.timestamp = timestamp;
        this.placeName = placeName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
        this.placeId = placeId;
        this.isFavorite = isFavorite;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
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

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    @Override
    public String toString() {
        return "PlaceFavorite{" +
                "timestamp=" + timestamp +
                ", placeName='" + placeName + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", type='" + type + '\'' +
                ", placeId='" + placeId + '\'' +
                ", isFavorite=" + isFavorite +
                '}';
    }
}
