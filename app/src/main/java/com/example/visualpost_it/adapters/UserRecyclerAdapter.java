package com.example.visualpost_it.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.visualpost_it.R;
import com.example.visualpost_it.dtos.User;

import java.util.ArrayList;

public class UserRecyclerAdapter extends RecyclerView.Adapter<UserRecyclerAdapter.ViewHolder>{

    private ArrayList<User> mUsers = new ArrayList<>();

    public UserRecyclerAdapter(ArrayList<User> users) {
        this.mUsers = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_list_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ((ViewHolder)holder).username.setText(mUsers.get(position).getNickname());
        ((ViewHolder)holder).email.setText(mUsers.get(position).getEmail());
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView username, email;
        private static final String TAG = "UserRAViewHolder";

        //buttons
        private ImageButton favoritesBtn;
        private ImageButton seenBtn;

        private boolean isFavorite = false;
        private boolean isSeen = false;

        public ViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            email = itemView.findViewById(R.id.email);

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

