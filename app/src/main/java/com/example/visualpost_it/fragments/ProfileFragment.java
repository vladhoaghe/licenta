package com.example.visualpost_it.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatTextView;

import com.example.visualpost_it.R;
import com.example.visualpost_it.activities.HomeScreenActivity;
import com.example.visualpost_it.activities.LoginActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    private static final String TAG = "Profile Fragment";

    private static final String ARG_PARAM1 = "nickname";
    private static final String ARG_PARAM2 = "email";
    private static final String ARG_PARAM3 = "fullname";
    private static final String ARG_PARAM4 = "password";

    private String nickname;
    private String email;
    private String fullname;
    private String password;

    AppCompatTextView nicknameField;
    AppCompatTextView topFullnameField;
    EditText emailField;
    EditText passwordField;
    EditText fullnameField;


    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2, String param3, String param4) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putString(ARG_PARAM4, param4);
        Log.d(TAG, "newInstance: " +  args);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            nickname = getArguments().getString(ARG_PARAM1);
            email = getArguments().getString(ARG_PARAM2);
            fullname = getArguments().getString(ARG_PARAM3);
            password = getArguments().getString(ARG_PARAM4);
        }

        Log.d(TAG, "onCreate: " + nickname);
        Log.d(TAG, "onCreate: " + email);
        Log.d(TAG, "onCreate: " + fullname);
        Log.d(TAG, "onCreate: " + password);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        nicknameField = view.findViewById(R.id.profile_nickname);
        fullnameField = view.findViewById(R.id.profile_fullname);
        topFullnameField = view.findViewById(R.id.full_name);
        emailField = view.findViewById(R.id.profile_email);
        passwordField = view.findViewById(R.id.profile_password);

        nickname = "@"+nickname;
        topFullnameField.setText(fullname);
        nicknameField.setText(nickname);

        emailField.setText(email);
        fullnameField.setText(fullname);
        passwordField.setText(password);

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
