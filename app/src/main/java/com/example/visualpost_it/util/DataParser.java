package com.example.visualpost_it.util;

import android.util.Log;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser {
    private static final String TAG = "Data Parser";

    private HashMap<String, String> getPlace(JSONObject googlePlaceJson){
        HashMap<String, String> googlePlacesMap = new HashMap<>();
        String placeName = "~NA~";
        String latitude = "";
        String longitude = "";
        String reference = "";
        String photoReference = "";

        try {
            Log.d(TAG, "getPlace: googlePlaceJSON: " + googlePlaceJson.getString("results"));
        } catch (JSONException e) {
            Log.d(TAG, "getPlace: json exception");
            e.printStackTrace();
        }
        try {
            if(!googlePlaceJson.isNull("name")){
                    placeName = googlePlaceJson.getString("name");
            }

            latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");
            reference = googlePlaceJson.getString("reference");

//            JSONArray photosArray = new JSONArray();
//            photosArray = googlePlaceJson.getJSONArray("photos");
//            photoReference = photosArray.getJSONObject(0).getString("photo_reference");

//            Log.d(TAG, "getPlace: " + placeName);
//            Log.d(TAG, "getPlace: " + latitude);
//            Log.d(TAG, "getPlace: " + longitude);
//            Log.d(TAG, "getPlace: " + reference);

            googlePlacesMap.put("place_name", placeName);
            googlePlacesMap.put("lat", latitude);
            googlePlacesMap.put("lng", longitude);
            googlePlacesMap.put("reference", reference);
//            googlePlacesMap.put("photo_reference", photoReference);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return googlePlacesMap;
    }

    private List<HashMap<String, String>>  getPlaces(JSONArray jsonArray){
        int count = jsonArray.length();
        List<HashMap<String, String>> placesList = new ArrayList<>();
        HashMap<String, String> placeMap = null;

        for(int i = 0; i < count; i++){
            try {
                placeMap = getPlace((JSONObject) jsonArray.get(i));
                placesList.add(placeMap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return placesList;
    }

    public List<HashMap<String, String>> parse(String jsonData){
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("results");
            Log.d(TAG, "parse: ");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getPlaces(jsonArray);
    }
}
