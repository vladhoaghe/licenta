package com.example.visualpost_it.adapters;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Locale;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class FavoritePlacesRecyclerAdapter extends RecyclerView.Adapter<FavoritePlacesRecyclerAdapter.ViewHolder> {

    private static final String TAG = "FavoritePlacesRecyclerA";

    private ArrayList<Place> mFavoritePlaces;

    public FavoritePlacesRecyclerAdapter(ArrayList<Place> mFavoritePlaces) {
        for(Place p : mFavoritePlaces){
            Log.d(TAG, "FavoritePlacesRecyclerAdapter: param: " + p.toString());
        }
        this.mFavoritePlaces = mFavoritePlaces;
        for(Place p: this.mFavoritePlaces){
            Log.d(TAG, "FavoritePlacesRecyclerAdapter: local: " + p.toString());
        }
    }

    @Override
    public int getItemCount() {
        return mFavoritePlaces.size();
    }

    @NonNull
    @Override
    public FavoritePlacesRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_places_list_item, parent, false);

        for(Place p : mFavoritePlaces){
            Log.d(TAG, "onCreateViewHolder: " + p.toString());
        }

        final FavoritePlacesRecyclerAdapter.ViewHolder holder = new FavoritePlacesRecyclerAdapter.ViewHolder(view, mFavoritePlaces);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritePlacesRecyclerAdapter.ViewHolder holder, int position) {

        holder.placeNameTextView.setText(mFavoritePlaces.get(position).getPlaceName());
        String distanceFormat = String.format(Locale.US, "%.2f", mFavoritePlaces.get(position).getDistance()) +
                "km";
        holder.distanceTextView.setText(distanceFormat);

        holder.goLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseNavigationService(v, mFavoritePlaces.get(position));
            }
        });

        holder.favoritesBtn.setBackgroundResource(R.drawable.btn_favorite_20dp);

        holder.placePhoto.setImageBitmap(mFavoritePlaces.get(position).getPlacePhoto());

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

    }
}