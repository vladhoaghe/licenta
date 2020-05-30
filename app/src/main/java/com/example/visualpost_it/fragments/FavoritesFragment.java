package com.example.visualpost_it.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visualpost_it.R;
import com.example.visualpost_it.adapters.FavoritePlacesRecyclerAdapter;
import com.example.visualpost_it.dtos.Place;
import com.example.visualpost_it.util.GetFavoritePlaces;
import com.example.visualpost_it.util.PlaceComparator;
import com.example.visualpost_it.util.SetPlacePhoto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class FavoritesFragment extends Fragment {

    private static final String TAG = "FavoritesFragment";
    private FirebaseFirestore mDb;
    private List<Place> userFavoritePlaces = new ArrayList<>();
    private RecyclerView mFavoriteFragmentRecyclerView;


    public FavoritesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDb = FirebaseFirestore.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        getFavoritePlaces(view);

        mFavoriteFragmentRecyclerView = view.findViewById(R.id.favorite_places_recycler_view);

        return view;
    }

    private void getFavoritePlaces(View view) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String[] typesArray = view.getResources().getStringArray(R.array.place_type);

        HashMap<String, Query> firestoreQueries = new HashMap<>();

        for (String s : typesArray) {
            if (!s.equals("")) {
                Query placeReference = mDb
                        .collection(view.getContext().getString(R.string.collection_favorite_places))
                        .document(userId)
                        .collection(s);
                firestoreQueries.put(s, placeReference);
            }
        }

        for (Query firestoreQuery : firestoreQueries.values()) {
            if (firestoreQuery != null) {
                firestoreQuery.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userFavoritePlaces.clear();
                        ArrayList<Place> ceva = new ArrayList<>();
                        List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();
                        for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                            Place favoritePlace = documentSnapshot.toObject(Place.class);

                            Object[] dataTransfer = new Object[1];
                            dataTransfer[0] = documentSnapshot;
                            ceva.add(favoritePlace);

                        }


                        initPlacesListRecyclerView(ceva);

                    }
                });
            }
        }
    }

    private void initPlacesListRecyclerView(ArrayList<Place> ceva) {
        for(Place p : userFavoritePlaces) {
            Log.d(TAG, "initPlacesListRecyclerView: " + p.toString());
        }
        for(Place p : ceva){
            Log.d(TAG, "initPlacesListRecyclerView: array" + p.toString());
        }

        FavoritePlacesRecyclerAdapter mFavoritePlacesRecyclerAdapter = new FavoritePlacesRecyclerAdapter(ceva);
        mFavoriteFragmentRecyclerView.smoothScrollToPosition(0);
        mFavoriteFragmentRecyclerView.setAdapter(mFavoritePlacesRecyclerAdapter);
        mFavoriteFragmentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }


}
