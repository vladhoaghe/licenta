package com.example.visualpost_it.adapters;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.example.visualpost_it.R;
import com.example.visualpost_it.dtos.Place;

import com.example.visualpost_it.util.GetPlacePhoto;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class PlacesRecyclerAdapter extends RecyclerView.Adapter<PlacesRecyclerAdapter.ViewHolder> {

    ArrayList<Place> userFavoritePlaces = new ArrayList<>();
    private ArrayList<Place> mPlacesList;
    private GoogleMap mMap;
    private FirebaseFirestore myDb;
    private HashMap<String, ArrayList<Place>> dictFavoritePlaces = new HashMap<>();
    private PlacesClient placesClient;
    private Places places;

    private static final String TAG = "PlacesRecyclerAdapter";

    public PlacesRecyclerAdapter(ArrayList<Place> placesList, GoogleMap mMap) {
        this.mPlacesList = placesList;
        this.mMap = mMap;
    }

    @Override
    public int getItemCount() {
        return mPlacesList.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_places_list_item, parent, false);
        myDb = FirebaseFirestore.getInstance();
        placesClient = Places.createClient(parent.getContext());
//        for(Place p : mPlacesList){
//            Log.d(TAG, "onCreateViewHolder: places: " + p.toString());
//        }

        final PlacesRecyclerAdapter.ViewHolder holder = new ViewHolder(view, mPlacesList);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.placeNameTextView.setText(mPlacesList.get(position).getPlaceName());
        String distanceFormat = String.format(Locale.US, "%.2f", mPlacesList.get(position).getDistance()) +
                "km";
        holder.distanceTextView.setText(distanceFormat);

        holder.goLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseNavigationService(v, mPlacesList.get(position));
            }
        });

        holder.placeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mPlacesList.get(position).getLatitude(), mPlacesList.get(position).getLongitude()), 18));
                Log.d(TAG, "onClick: container " + mPlacesList.get(position).toString());
            }
        });

        if(!holder.heartFull){
            holder.favoritesBtn.setBackgroundResource(R.drawable.btn_not_favorite_20dp);
        }

        Log.d(TAG, "ViewHolder: Place: " + mPlacesList.get(position).toString() + " has position: " + position);
        checkIfFavoritePlace(holder, mPlacesList.get(position));

        holder.favoritesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.heartFull = !holder.heartFull;

                Log.d(TAG, "onClick: Place: " + mPlacesList.get(position) + " with holder: " + holder.placeNameTextView.getText().toString() + " has favorite: " + holder.heartFull);
                if(!holder.heartFull){
                    holder.favoritesBtn.setBackgroundResource(R.drawable.btn_not_favorite_20dp);
                    removeFavoritePlace(v, mPlacesList.get(position));
                    Log.d(TAG, "onClick: not fav button " + mPlacesList.get(position).toString());
                } else {
                    holder.favoritesBtn.setBackgroundResource(R.drawable.btn_favorite_20dp);
                    saveFavoritePlace(v, mPlacesList.get(position));
                    Log.d(TAG, "onClick: fav button " + mPlacesList.get(position).toString());
                }
            }
        });

        GetPlacePhoto getPlacePhoto = (GetPlacePhoto) new GetPlacePhoto(holder, new GetPlacePhoto.GetAsyncResponse() {
            @Override
            public void processFinish(Bitmap placePhoto) {
                holder.placePhoto.setImageBitmap(placePhoto);
            }
        }).execute(mPlacesList.get(position));

//        holder.placePhoto.setImageBitmap(mPlacesList.get(position).getPlacePhoto());

        holder.placePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPhoto(holder);
            }
        });

    }

    private void loadPhoto(ViewHolder holder) {
        ImageView tempImageView = holder.placePhoto;

        AlertDialog.Builder imageDialog = new AlertDialog.Builder(holder.itemView.getContext());
        LayoutInflater inflater = (LayoutInflater) holder.itemView.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.custom_fullimage_dialog,
                holder.itemView.findViewById(R.id.layout_root));
        ImageView image = (ImageView) layout.findViewById(R.id.fullimage);
        TextView placeName = (TextView) layout.findViewById(R.id.cardview_placename);
        TextView distance = (TextView) layout.findViewById(R.id.cardview_distance);

        distance.setText(holder.distanceTextView.getText());
        placeName.setText(holder.placeNameTextView.getText());
        image.setImageDrawable(tempImageView.getDrawable());
        imageDialog.setView(layout);
        imageDialog.setPositiveButton(holder.itemView.getContext().getString(R.string.go_back), new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }

        });

        imageDialog.create();
        imageDialog.show();
    }

    private void checkIfFavoritePlace(ViewHolder holder, Place place){
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        CollectionReference placeReference = myDb
                .collection(holder.itemView.getContext().getString(R.string.collection_favorite_places))
                .document(userId)
                .collection(place.getType());

        placeReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    userFavoritePlaces.add(documentSnapshot.toObject(Place.class));
                    Log.d(TAG, "onSuccess: userFavoritePlaces: " + userFavoritePlaces);
                }

                dictFavoritePlaces.put(userId, userFavoritePlaces);
                Log.d(TAG, "onSuccess: dictFavoritePlaces entrySet: " + dictFavoritePlaces.entrySet());

                for(Map.Entry<String, ArrayList<Place>> entry : dictFavoritePlaces.entrySet()){
                    ArrayList<Place> favoritePlaces = entry.getValue();
                    Log.d(TAG, "checkIfFavoritePlace: " + favoritePlaces);

                    for(Place p : favoritePlaces){
                        if(place.getPlaceName().equals(p.getPlaceName())){
                            place.setFavorite(p.isFavorite());
                        }
                        Log.d(TAG, "onSuccess: Place: " + p.getPlaceName() + " is favorite: " + p.isFavorite());
                        if(p.getPlaceName().equals(place.getPlaceName()) && place.isFavorite()){
                            holder.heartFull = p.isFavorite();
                            Log.d(TAG, "if: fave place: " + p.getPlaceName() + ", fave holder: " + holder.placeNameTextView.getText().toString());
                            holder.favoritesBtn.setBackgroundResource(R.drawable.btn_favorite_20dp);
                        } else {
                            if(!place.isFavorite()){
                                Log.d(TAG, "elseIf: notFave place: " + place.getPlaceName() + ", fave holder: " + holder.placeNameTextView.getText().toString());
                                holder.favoritesBtn.setBackgroundResource(R.drawable.btn_not_favorite_20dp);
                            } else {
                                Log.d(TAG, "elseElse: fave place: " + place.getPlaceName() + ", fave holder: " + holder.placeNameTextView.getText().toString());
                                holder.favoritesBtn.setBackgroundResource(R.drawable.btn_favorite_20dp);
                            }
                        }
                    }
                }
            }
        });
    }

    private void removeFavoritePlace(View v, Place place) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ArrayList<Place> userFavoritePlaces = new ArrayList<>();

        CollectionReference placeCollectionReference = myDb
                .collection(v.getContext().getString(R.string.collection_favorite_places))
                .document(userId)
                .collection(place.getType());

        placeCollectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    userFavoritePlaces.add(documentSnapshot.toObject(Place.class));
                    Log.d(TAG, "onSuccess: userFavoritePlaces on Remove: " + userFavoritePlaces);
                }

                for(Place p: userFavoritePlaces){
                    if(place.getPlaceName().equals(p.getPlaceName())){
                        place.setPlaceId(p.getPlaceId());
                        Log.d(TAG, "onSuccess: Place to be removed has id: " + place.getPlaceId());

                        DocumentReference placeReference = myDb
                            .collection(v.getContext().getString(R.string.collection_favorite_places))
                            .document(userId)
                            .collection(place.getType())
                            .document(place.getPlaceId());

                        placeReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: ELIMINAREE: " + place.getPlaceId() + " placeName: " + place.getPlaceName());
                            }
                        });
                    }
                }
            }
        });
    }

    private void saveFavoritePlace(View v, Place place) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        CollectionReference placeReference = myDb
                .collection(v.getContext().getString(R.string.collection_favorite_places))
                .document(userId)
                .collection(place.getType());

        Place favoritePlace = new Place(place.getPlaceName(), place.getLatitude(), place.getLongitude(), place.getType(), place.getPhotoReference(), place.getPlaceReference());

        placeReference.add(favoritePlace).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()){
                    task.getResult().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            Log.d(TAG, "onComplete: altcva" + task.getResult().getId());
                            favoritePlace.setPlaceId(task.getResult().getId());
                            favoritePlace.setFavorite(true);
                            placeReference.document(task.getResult().getId()).set(favoritePlace).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.d(TAG, "onComplete: AM bagat place id ADEVARAAAAAAT: " + favoritePlace.toString());
                                }
                            });
                        }
                    });
                    Log.d(TAG, "onComplete: place added" +favoritePlace.toString());
                }
            }
        });
    }

    private void chooseNavigationService(View v, Place p) {
        String url = "waze://?ll=" + p.getLatitude() + ", " + p.getLongitude() + "&navigate=yes";
        Intent intentWaze = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intentWaze.setPackage("com.waze");

        String uriGoogle = "google.navigation:q=" + p.getLatitude() + ", " + p.getLongitude();
        Intent intentGoogleNav = new Intent(Intent.ACTION_VIEW, Uri.parse(uriGoogle));
        intentGoogleNav.setPackage("com.google.android.apps.maps");

        String title = v.getContext().getString(R.string.choose_direction_app);
        Intent chooserIntent = Intent.createChooser(intentGoogleNav, title);
        Intent[] arr = new Intent[1];
        arr[0] = intentWaze;
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arr);
        v.getContext().startActivity(chooserIntent);

        v.getContext().startActivity(chooserIntent);
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView placeNameTextView, distanceTextView;
        ImageView placePhoto;
        FirebaseFirestore mDb;
        PlacesClient placesClient;

        private static final String TAG = "ViewHolder";
        //buttons
        private ImageButton favoritesBtn;
        private TextView goLink;
        private RelativeLayout placeContainer;
        private boolean heartFull = false;
        private boolean isSeen = false;

        public ViewHolder(@NonNull View itemView, ArrayList<Place> placesList) {

            super(itemView);
            placePhoto = itemView.findViewById(R.id.place_img);
            placeNameTextView = itemView.findViewById(R.id.place_name);
            distanceTextView = itemView.findViewById(R.id.distance);
            mDb = FirebaseFirestore.getInstance();
            placesClient = Places.createClient(itemView.getContext());

            //buttons
            goLink = itemView.findViewById(R.id.go_to_link);
            placeContainer = itemView.findViewById(R.id.place_container_layout);

            favoritesBtn = itemView.findViewById(R.id.place_favorite_btn);
        }

//        View.OnClickListener mToggleFavoriteButton = new View.OnClickListener(){
//
//            @Override
//            public void onClick(View v) {
//                isFavorite = !isFavorite;
//
//                if(!isFavorite){
//                    v.setBackgroundResource(R.drawable.btn_not_favorite_20dp);
//
//                    Log.d(TAG, "onClick: Place not favorite");
//                } else {
//                    v.setBackgroundResource(R.drawable.btn_favorite_20dp);
//                    Log.d(TAG, "onClick: Place favorite");
//                }
//
//            }
//        };
    }
}
