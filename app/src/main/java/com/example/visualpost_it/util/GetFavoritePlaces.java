package com.example.visualpost_it.util;

import android.content.Context;
import android.os.AsyncTask;
import com.example.visualpost_it.dtos.Place;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class GetFavoritePlaces extends AsyncTask<List<DocumentSnapshot>, Void, ArrayList<Place>> {

    private static final String TAG = "GetFavoritePlaces";
    private ArrayList<Place> userFavoritePlaces = new ArrayList<>();
    private Context mContext;


    public interface GetAsyncResponse {
        void processFinish(ArrayList<Place> mFavoritesPlaces);
    }

    private GetAsyncResponse delegate;

    public GetFavoritePlaces() {
    }

    public GetFavoritePlaces(Context context, GetAsyncResponse delegate) {
        this.mContext = context;
        this.delegate = delegate;
    }

    @Override
    protected ArrayList<Place> doInBackground(List<DocumentSnapshot>... lists) {

        List<DocumentSnapshot> favoritePlaces = lists[0];


        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Place> favoritePlaces) {
//        super.onPostExecute(favoritePlaces);
        delegate.processFinish(favoritePlaces);
    }


}
