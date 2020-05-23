package com.example.visualpost_it.adapters;

import android.content.Intent;
import android.media.Image;
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
import androidx.recyclerview.widget.RecyclerView;

import com.example.visualpost_it.R;
import com.example.visualpost_it.dtos.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Locale;

public class PlacesRecyclerAdapter extends RecyclerView.Adapter<PlacesRecyclerAdapter.ViewHolder> {

    private ArrayList<Place> placesList;
    private GoogleMap mMap;
    private static final String TAG = "PlacesRecyclerAdapter";

    public PlacesRecyclerAdapter(ArrayList<Place> placesList, GoogleMap mMap) {
        this.placesList = placesList;
        this.mMap = mMap;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_places_list_item, parent, false);
        final PlacesRecyclerAdapter.ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.placeNameTextView.setText(placesList.get(position).getPlaceName());
        String distanceFormat = String.format(Locale.US, "%.2f", placesList.get(position).getDistance()) +
                "km";
        holder.distanceTextView.setText(distanceFormat);

        holder.goLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "waze://?ll=" + placesList.get(position).getLatLng().latitude + ", " + placesList.get(position).getLatLng().longitude + "&navigate=yes";
                Intent intentWaze = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intentWaze.setPackage("com.waze");

                String uriGoogle = "google.navigation:q=" + placesList.get(position).getLatLng().latitude + ", " + placesList.get(position).getLatLng().longitude;
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
        });

        holder.placeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(placesList.get(position).getLatLng().latitude, placesList.get(position).getLatLng().longitude), 18));
            }
        });

        holder.favoritesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!holder.isFavorite){
                    v.setBackgroundResource(R.drawable.btn_not_favorite_20dp);
                    removePlace(placesList.get(position));
                    Log.d(TAG, "onClick: Place not favorite");
                } else {
                    v.setBackgroundResource(R.drawable.btn_favorite_20dp);
                    savePlace(placesList.get(position));
                    Log.d(TAG, "onClick: Place favorite");
                }

                holder.isFavorite = !holder.isFavorite;
            }
        });

    }

    private void removePlace(Place place) {
    }

    private void savePlace(Place place) {

    }

    @Override
    public int getItemCount() {
        return placesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView placeNameTextView, distanceTextView;
        ImageView placePhoto;
        private static final String TAG = "UserRAViewHolder";

        //buttons
        private ImageButton favoritesBtn;
        private ImageButton seenBtn;
        private TextView goLink;
        private RelativeLayout placeContainer;

        private boolean isFavorite = false;
        private boolean isSeen = false;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            placePhoto = itemView.findViewById(R.id.place_img);
            placeNameTextView = itemView.findViewById(R.id.place_name);
            distanceTextView = itemView.findViewById(R.id.distance);

            //buttons
            favoritesBtn = itemView.findViewById(R.id.place_favorite_btn);

//            seenBtn = itemView.findViewById(R.id.place_not_seen_btn);
//            seenBtn.setOnClickListener(mToggleSeenButton);

            goLink = itemView.findViewById(R.id.go_to_link);
            placeContainer = itemView.findViewById(R.id.place_container_layout);
        }

        View.OnClickListener mToggleFavoriteButton = new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(!isFavorite){
                    v.setBackgroundResource(R.drawable.btn_not_favorite_20dp);
                    Log.d(TAG, "onClick: Place not favorite");
                } else {
                    v.setBackgroundResource(R.drawable.btn_favorite_20dp);
                    Log.d(TAG, "onClick: Place favorite");
                }

                isFavorite = !isFavorite;
            }
        };

        View.OnClickListener mToggleSeenButton = new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(!isSeen){

                    v.setBackgroundResource(R.drawable.btn_place_not_seen_20dp);

                    Log.d(TAG, "onClick: Place not seen");
                } else {
                    v.setBackgroundResource(R.drawable.ic_check_filled_15dp);
                    Log.d(TAG, "onClick: Place seen");
                }

                isSeen = !isSeen;
            }
        };
    }
}
