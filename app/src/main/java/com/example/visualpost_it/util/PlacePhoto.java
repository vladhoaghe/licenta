package com.example.visualpost_it.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.example.visualpost_it.adapters.PlacesRecyclerAdapter;
import com.example.visualpost_it.dtos.Place;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlacePhoto extends AsyncTask<Object, Void, Place> {
    private static final String TAG = "PlacePhoto";
    private Context mContext;
    
    public interface AsyncPhotoResponse {
        void processFinish(Place placeWithPhoto);
    }

    private AsyncPhotoResponse delegate = null;

    public PlacePhoto(Context context, AsyncPhotoResponse delegate) {
        mContext = context;
        this.delegate = delegate;
    }



    @Override
    protected Place doInBackground(Object... params) {

        String placeReference = (String) params[0];
        Place place = (Place) params[1]; 
        
        PlacesClient placesClient = Places.createClient(mContext);

        Log.d(TAG, "setPlaceIcon: placeRef: " + placeReference);

        List<com.google.android.libraries.places.api.model.Place.Field> fields = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.PHOTO_METADATAS);
        FetchPlaceRequest placeRequest = FetchPlaceRequest.newInstance(placeReference, fields);

        Log.d(TAG, "setPlaceIcon: placeReqId: " + placeRequest.getPlaceId());

        placesClient.fetchPlace(placeRequest).addOnSuccessListener((response) -> {
            com.google.android.libraries.places.api.model.Place googlePlace = response.getPlace();
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
        
        return place;
    }

    @Override
    protected void onPostExecute(Place placeWithPhoto) {
//        super.onPostExecute(placeWithPhoto);
        delegate.processFinish(placeWithPhoto);
        Log.d(TAG, "onPostExecute: ");
    }
    
}
