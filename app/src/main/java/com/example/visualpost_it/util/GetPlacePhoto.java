package com.example.visualpost_it.util;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.example.visualpost_it.adapters.PlacesRecyclerAdapter;
import com.example.visualpost_it.dtos.Place;

public class GetPlacePhoto extends AsyncTask<Place, Void, Bitmap> {

    public interface GetAsyncResponse {
        void processFinish(Bitmap placePhoto);

    }

    private GetAsyncResponse delegate;
    public GetPlacePhoto(PlacesRecyclerAdapter.ViewHolder holder, GetAsyncResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected Bitmap doInBackground(Place... places) {

        Place p = places[0];
        return p.getPlacePhoto();
    }

    @Override
    protected void onPostExecute(Bitmap placePhoto) {
        delegate.processFinish(placePhoto);
    }
}
