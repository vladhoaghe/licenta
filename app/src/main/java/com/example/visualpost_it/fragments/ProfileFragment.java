package com.example.visualpost_it.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.visualpost_it.R;
import com.example.visualpost_it.activities.HomeScreenActivity;
import com.example.visualpost_it.activities.LoginActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    Button signOut;
    private FirebaseAuth mAuth;
    EditText emailField;
    EditText passwordField;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
//        signOut = view.findViewById(R.id.btn_sign_out);
//        signOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                attemptSignOut(v);
//            }
//        });
        // Inflate the layout for this fragment
        return view;
    }

//    private void attemptSignOut(View v) {
//
//        if(v.getId() == R.id.btn_sign_out){
//            AuthUI.getInstance().signOut(v.getContext()).addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    HomeScreenActivity activity = (HomeScreenActivity) getActivity();
//                    Intent switchToLoginActivity = new Intent(activity, LoginActivity.class);
//                    startActivity(switchToLoginActivity);
//                }
//            });
//        }
//    }
}
