package com.example.visualpost_it.adapters;

import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visualpost_it.R;
import com.example.visualpost_it.dtos.Place;
import com.example.visualpost_it.util.Constants;

import java.util.ArrayList;
import java.util.Locale;

public class PlacesRecyclerAdapter extends RecyclerView.Adapter<PlacesRecyclerAdapter.ViewHolder> {

    private ArrayList<Place> placesList;
    ImageView googlePhoto;
    private static final String TAG = "PlacesRecyclerAdapter";

    public PlacesRecyclerAdapter(ArrayList<Place> placesList) {
        this.placesList = placesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_places_list_item, parent, false);
        final PlacesRecyclerAdapter.ViewHolder holder = new PlacesRecyclerAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.placeNameTextView.setText(placesList.get(position).getPlaceName());
        String distanceFormat = String.format(Locale.US, "%.2f", placesList.get(position).getDistance()) +
                "km";
        holder.distanceTextView.setText(distanceFormat);
    }

    @Override
    public int getItemCount() {
        return placesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView placeNameTextView, distanceTextView;
        ImageView placePhoto;
        private static final String TAG = "UserRAViewHolder";

        //buttons
        private ImageButton favoritesBtn;
        private ImageButton seenBtn;

        private boolean isFavorite = false;
        private boolean isSeen = false;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            placePhoto = itemView.findViewById(R.id.place_img);
            placeNameTextView = itemView.findViewById(R.id.place_name);
            distanceTextView = itemView.findViewById(R.id.distance);

            //buttons
            favoritesBtn = itemView.findViewById(R.id.place_favorite_btn);
            favoritesBtn.setOnClickListener(mToggleFavoriteButton);

            seenBtn = itemView.findViewById(R.id.place_not_seen_btn);
            seenBtn.setOnClickListener(mToggleSeenButton);
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
