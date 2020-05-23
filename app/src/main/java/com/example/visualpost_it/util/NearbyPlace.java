package com.example.visualpost_it.util;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.example.visualpost_it.dtos.Place;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class NearbyPlace extends AsyncTask<String, Void, ArrayList<Place>> {

    private static final String TAG = "NearbyPlace";
    private String googlePlacesData;

    public interface AsyncResponse {
        void processFinish(ArrayList<Place> mPlacesList);
    }

    private AsyncResponse delegate = null;

    public NearbyPlace(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected ArrayList<Place> doInBackground(String... params) {

        String url = params[0];
        String latitude = params[1];
        String longitude = params[2];

        Log.d(TAG, "doInBackground: " + url);

        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googlePlacesData = downloadUrl.readUrl(url);
            Log.d(TAG, "doInBackground: google Places data: " + googlePlacesData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<HashMap<String, String>> nearbyPlaceList = null;
        DataParser parser = new DataParser();
        nearbyPlaceList = parser.parse(googlePlacesData);

        return showNearbyPlaces(nearbyPlaceList, latitude, longitude);
    }

    @Override
    protected void onPostExecute(ArrayList<Place> places) {
//        super.onPostExecute(places);
        delegate.processFinish(places);
        Log.d(TAG, "onPostExecute: " + places.toString());
    }

    private ArrayList<Place> showNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList, String userLatitude, String userLongitude){
        ArrayList<Place> localPlaces = new ArrayList<>();

        for(int i = 0; i < nearbyPlacesList.size(); i++){
            HashMap<String, String> googlePlace = nearbyPlacesList.get(i);

            Log.d(TAG, "showNearbyPlaces: googlePlaces: " + googlePlace);

            String placeName = googlePlace.get("place_name");
            double placeLatitude = Double.parseDouble(Objects.requireNonNull(googlePlace.get("lat")));
            double placeLongitude = Double.parseDouble(Objects.requireNonNull(googlePlace.get("lng")));

            float[] results = new float[1];

            Location.distanceBetween(Double.parseDouble(userLatitude), Double.parseDouble(userLongitude), placeLatitude, placeLongitude, results);

            float distance = (results[0]/1000);

            LatLng latLng = new LatLng(placeLatitude, placeLongitude);
            localPlaces.add(new Place(placeName, latLng, distance));
        }

        for(Place place : localPlaces){
            Log.d(TAG, "showNearbyPlaces: local " + place.toString());
        }

        return localPlaces;
    }
}
