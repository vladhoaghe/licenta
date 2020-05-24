package com.example.visualpost_it.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatTextView;

import com.example.visualpost_it.R;
import com.example.visualpost_it.activities.HomeScreenActivity;
import com.example.visualpost_it.activities.LoginActivity;
import com.example.visualpost_it.activities.MainActivity;
import com.example.visualpost_it.dtos.UserLocation;
import com.example.visualpost_it.util.UserClientSingleton;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private static final String TAG = "Profile Fragment";

    private static final String ARG_PARAM5 = "userLocation";

    FirebaseAuth firebaseAuth;

    private String nickname;
    private String email;
    private String fullname;
    private String password;

    private Context profileContext;

    private UserLocation mUserLocation;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(UserLocation userLocation) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();

        args.putParcelable(ARG_PARAM5, userLocation);
        Log.d(TAG, "newInstance: " +  args);
        fragment.setArguments(args);
        return fragment;
    }

    private void setProfileDetails(){
        nickname = ((UserClientSingleton) getActivity().getApplicationContext()).getUser().getNickname();
        email = ((UserClientSingleton) getActivity().getApplicationContext()).getUser().getEmail();
        fullname = ((UserClientSingleton) getActivity().getApplicationContext()).getUser().getFullName();
        password = ((UserClientSingleton) getActivity().getApplicationContext()).getUser().getPassword();


        Log.d(TAG, "setProfileDetails: " + nickname);
        Log.d(TAG, "setProfileDetails: " + email);
        Log.d(TAG, "setProfileDetails: " + fullname);
        Log.d(TAG, "setProfileDetails: " + password);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserLocation = getArguments().getParcelable(ARG_PARAM5);
        }

        setProfileDetails();

        firebaseAuth = FirebaseAuth.getInstance();

        Log.d(TAG, "onCreate: " + mUserLocation);
    }

    private String getAddress(UserLocation mUserLocation){

        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(mUserLocation.getGeoPoint().getLatitude(), mUserLocation.getGeoPoint().getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getAddress: Address" + addresses.get(0));
        String address = addresses.get(0).getAddressLine(0);
        String location = address.substring(address.indexOf(", ") + 1);
        Log.d(TAG, "getAddress: Location" + location);

        return location.trim();
    }

    private void setupFirebaseListener(){
        Log.d(TAG, "setupFirebaseListener: setting up the auth state listener");
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    Log.d(TAG, "onAuthStateChanged: signed_in: " + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged: signed_out");
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mAuthStateListener != null){
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        profileContext = container.getContext();

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        AppCompatTextView nicknameField = view.findViewById(R.id.profile_nickname);
        AppCompatTextView topFullnameField = view.findViewById(R.id.full_name);

        EditText fullnameField = view.findViewById(R.id.profile_fullname);
        EditText emailField = view.findViewById(R.id.profile_email);
        EditText passwordField = view.findViewById(R.id.profile_password);
        EditText locationField = view.findViewById(R.id.profile_location);

        Button signOut = view.findViewById(R.id.sign_out_profile);

        nickname = "@"+nickname;
        topFullnameField.setText(fullname);
        nicknameField.setText(nickname);

        emailField.setText(email);
        fullnameField.setText(fullname);
        locationField.setText(getAddress(mUserLocation));
        passwordField.setText(password);

        setupFirebaseListener();

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to sign out the user.");
                Log.d(TAG, "onClick: signOut instance: " + FirebaseAuth.getInstance().getCurrentUser().getUid());
//                FirebaseAuth.getInstance().signOut();
                attemptSignOut(v);
            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    public void alertsignout()
    {
        AlertDialog.Builder alertDialog2 = new
                AlertDialog.Builder(
                profileContext);

        // Setting Dialog Title
        alertDialog2.setTitle("Confirm SignOut");

        // Setting Dialog Message
        alertDialog2.setMessage("Are you sure you want to Signout?");

        // Setting Positive "Yes" Btn
        alertDialog2.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog

                        FirebaseAuth.getInstance().signOut();
                        Intent i = new Intent(profileContext,
                                LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }
                });
        // Setting Negative "NO" Btn
        alertDialog2.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        Toast.makeText(profileContext.getApplicationContext(),
                                "You clicked on NO", Toast.LENGTH_SHORT)
                                .show();
                        dialog.cancel();
                    }
                });

        // Showing Alert Dialog
        alertDialog2.show();


    }

    private void attemptSignOut(View v) {

        if(v.getId() == R.id.sign_out_profile){
//            Log.d(TAG, "attemptSignOut: " + AuthUI.getInstance().);
//            FirebaseAuth.getInstance().signOut(getActivity().getApplicationContext()).addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    HomeScreenActivity activity = (HomeScreenActivity) getActivity();
//                    Intent switchToLoginActivity = new Intent(activity, LoginActivity.class);
//                    startActivity(switchToLoginActivity);
//                }
//            });
            Log.d(TAG, "attemptSignOut: ");
            alertsignout();
        }
    }
}
