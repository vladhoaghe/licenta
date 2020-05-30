package com.example.visualpost_it.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.example.visualpost_it.R;
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

public class SetPlacePhoto extends AsyncTask<ArrayList<Place>, Void, ArrayList<Place>> {

    private static final String TAG = "SetPlacePhoto";

    public interface SetAsyncResponse{
        void processFinish(ArrayList<Place> places);
    }

    private SetAsyncResponse delegate;
    private Context mContext;

    public SetPlacePhoto(Context context, SetAsyncResponse delegate) {
        this.mContext = context;
        this.delegate = delegate;
    }

    @Override
    protected ArrayList<Place> doInBackground(ArrayList<Place>... arrayLists) {

        ArrayList<Place> userFavoritePlaces = arrayLists[0];
        ArrayList<Place> result = new ArrayList<>(userFavoritePlaces.size());

        for(Place p : userFavoritePlaces){
            Place placeWithPhoto = setPlacePhoto(p);
            result.add(placeWithPhoto);
        }

        return result;
    }

    @Override
    protected void onPostExecute(ArrayList<Place> favoritePlacesWithPhotos) {
//        super.onPostExecute(places);
        delegate.processFinish(favoritePlacesWithPhotos);
    }

    private Place setPlacePhoto(Place place) {

        PlacesClient placesClient = Places.createClient(mContext);

        Log.d(TAG, "setPlacePhoto: placeRef: " + place.getPlaceId());

        List<com.google.android.libraries.places.api.model.Place.Field> fields = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.PHOTO_METADATAS);
        FetchPlaceRequest placeRequest = FetchPlaceRequest.newInstance(place.getPlaceId(), fields);

        Log.d(TAG, "setPlacePhoto: placeReqId: " + placeRequest.getPlaceId());

        if (place.getPhotoReference() != null) {
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
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        int statusCode = apiException.getStatusCode();

                        Log.e(TAG, "setPlaceIcon: Place not found: " + exception.getMessage());
                    }
                });
            });
        } else {
            switch (place.getType()) {
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
        return place;
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
