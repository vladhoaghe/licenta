package com.example.visualpost_it.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.example.visualpost_it.R;
import com.example.visualpost_it.dtos.Place;
import com.example.visualpost_it.fragments.HomeFragment;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class NearbyPlace extends AsyncTask<String, Void, ArrayList<Place>> {

    private static final String TAG = "NearbyPlace";
    private String googlePlacesData;
    private Context mContext;

    public interface AsyncResponse {
        void processFinish(ArrayList<Place> mPlacesList);
    }

    private AsyncResponse delegate = null;

    public NearbyPlace(Context context, AsyncResponse delegate){
        this.mContext = context;
        this.delegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected ArrayList<Place> doInBackground(String... params) {

        String url = params[0];
        String latitude = params[1];
        String longitude = params[2];
        String type = params[3];

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

        return showNearbyPlaces(nearbyPlaceList, latitude, longitude, type);
    }

    @Override
    protected void onPostExecute(ArrayList<Place> places) {
//        super.onPostExecute(places);
        delegate.processFinish(places);
        Log.d(TAG, "onPostExecute: " + places.toString());
    }

    private ArrayList<Place> showNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList, String userLatitude, String userLongitude, String type){
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

            String photoReference = googlePlace.get("photo_reference");
            String placeId = googlePlace.get("reference");

            localPlaces.add(new Place(placeName, placeLatitude, placeLongitude, distance, type, photoReference, placeId));
        }

        for(Place place : localPlaces){
            setPlacePhoto(place);
        }

        for(Place p : localPlaces) {
            Log.d(TAG, "showNearbyPlaces: local " + p.toString());
        }

        return localPlaces;
    }

    private void setPlacePhoto(Place place) {

        PlacesClient placesClient = Places.createClient(mContext);

        Log.d(TAG, "setPlacePhoto: placeRef: " + place.getPlaceReference());

        List<com.google.android.libraries.places.api.model.Place.Field> fields = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.PHOTO_METADATAS);
        FetchPlaceRequest placeRequest = FetchPlaceRequest.newInstance(place.getPlaceReference(), fields);

        Log.d(TAG, "setPlacePhoto: placeReqId: " + placeRequest.getPlaceId());

        if(place.getPhotoReference() != null){
            placesClient.fetchPlace(placeRequest).addOnSuccessListener((response) -> {
                com.google.android.libraries.places.api.model.Place googlePlace = response.getPlace();
                Log.d(TAG, "setPlacePhopai to: " + response.getPlace().getName());
                PhotoMetadata photoMetadata = googlePlace.getPhotoMetadatas().get(0);
                String attributions = photoMetadata.getAttributions();

                FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                        .build();

                placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                    Bitmap bitmap = fetchPhotoResponse.getBitmap();
                    place.setPlacePhoto(bitmap);
                }).addOnFailureListener((exception) -> {
                    if(exception instanceof ApiException){
                        ApiException apiException = (ApiException) exception;
                        int statusCode = apiException.getStatusCode();

                        Log.e(TAG, "setPlaceIcon: Place not found: " + exception.getMessage() );
                    }
                });
            });
        } else {
            switch(place.getType()){
                case "museum":
                    place.setPlacePhoto(getBitmapFromVectorDrawable(mContext, R.drawable.ic_museum_120dp));
                    break;
                case "park":
                    place.setPlacePhoto(getBitmapFromVectorDrawable(mContext, R.drawable.ic_park_120dp));
                    break;
                case "gas":
                    place.setPlacePhoto(getBitmapFromVectorDrawable(mContext, R.drawable.ic_gas_120dp));
                    break;
                case "castle":
                    place.setPlacePhoto(getBitmapFromVectorDrawable(mContext, R.drawable.ic_fortress_120dp));
                    break;
                case "restaurant":
                    place.setPlacePhoto(getBitmapFromVectorDrawable(mContext, R.drawable.ic_restaurant_120dp));
                    break;
                default:
                    place.setPlacePhoto(getBitmapFromVectorDrawable(mContext, R.drawable.ic_user_120dp));
                    break;
            }
        }
    }

    private static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
