package com.example.visualpost_it.dtos;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class UserLocation implements Parcelable {

    private GeoPoint geoPoint;
    private @ServerTimestamp Date timestamp;
    private User user;

    public UserLocation(GeoPoint geoPoint, Date timestamp, User user) {
        this.geoPoint = geoPoint;
        this.timestamp = timestamp;
        this.user = user;
    }

    public UserLocation() {
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "UserLocation{" +
                "geoPoint=" + geoPoint +
                ", timestamp='" + timestamp + '\'' +
                ", user=" + user +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
