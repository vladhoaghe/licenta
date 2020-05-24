package com.example.visualpost_it.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.visualpost_it.R;
import com.example.visualpost_it.util.UserClientSingleton;
import com.example.visualpost_it.dtos.User;
import com.example.visualpost_it.dtos.UserLocation;
import com.example.visualpost_it.fragments.HistoryFragment;
import com.example.visualpost_it.fragments.HomeFragment;
import com.example.visualpost_it.fragments.ProfileFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

public class HomeScreenActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;
    final String TAG = "HomeScreenActivity";
    TextView fullnameField;
    TextView emailField;
    TextView nicknameField;
    TextView passwordField;
    private FusedLocationProviderClient mFusedLocationClient;
    private UserLocation mUserLocation;
    private FirebaseFirestore mDb;
    private FirebaseAuth mAuth;

    String currentUser_nickname;
    String currentUser_email;
    String currentUser_fullname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();

        getUserDetails();
        Log.d(TAG, "onCreate: " + currentUser_email);
        Log.d(TAG, "onCreate: " + currentUser_fullname);
        Log.d(TAG, "onCreate: " + currentUser_nickname);
        openFragment(new HomeFragment());

        fullnameField = findViewById(R.id.profile_fullname);
        emailField = findViewById(R.id.profile_email);
        nicknameField = findViewById(R.id.profile_nickname);
        passwordField = findViewById(R.id.profile_password);
    }

    public void getUserDetails(){
        if(mUserLocation == null){
            mUserLocation = new UserLocation();
//            Log.d(TAG, "getUserDetails: " + FirebaseAuth.getInstance().getCurrentUser().getUid());

            FirebaseUser firebaseUser = mAuth.getCurrentUser();
            if(firebaseUser != null){
                DocumentReference userRef = mDb
                        .collection(getString(R.string.collection_users))
                        .document(firebaseUser.getUid());

                userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Log.d(TAG, "onComplete: task: " + task.isSuccessful());
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: successfully got the user details");

                            User user = task.getResult().toObject(User.class);
                            Log.d(TAG, "onComplete: Home Screen" + user.toString());
                            mUserLocation.setUser(user);
                            Log.d(TAG, "onComplete: user set: " + ((UserClientSingleton) getApplicationContext()).getUser().toString());
                            getLastKnownLocation();
                        }
                    }
                });
            }
        } else {
            Log.d(TAG, "getUserDetails: user Location null");
            getLastKnownLocation();
        }
    }

    private void saveUserLocation(){
        if(mUserLocation != null){
            Intent i = getIntent();
            String userId = i.getStringExtra("userId");
            Log.d(TAG, "saveUserLocation: " + userId);
            DocumentReference locationReference = mDb
                    .collection(getString(R.string.collection_user_locations))
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid());
            
            locationReference.set(mUserLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "saveUserLocation: \n inserted user location into database." +
                                "\n latitude: " + mUserLocation.getGeoPoint().getLatitude() +
                                "\n longitude: " + mUserLocation.getGeoPoint().getLongitude());
                    }
                }
            });
        }
    }

    private void getLastKnownLocation(){
        Log.d(TAG, "getLastKnownLocation: called");

        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful()){
                    Location location = task.getResult();
                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    Log.d(TAG, "onComplete: latitude: " + geoPoint.getLatitude());
                    Log.d(TAG, "onComplete: longitude: " + geoPoint.getLongitude());

                    mUserLocation.setGeoPoint(geoPoint);
                    mUserLocation.setTimestamp(null);
                    saveUserLocation();
                }
            }

        });
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_cnt, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch(menuItem.getItemId()) {
                        case R.id.navigation_home:
                            Log.d(TAG, "Switched to home");
                            openFragment(new HomeFragment());
                            return true;
                        case R.id.navigation_profile:
                            Log.d(TAG, "switched to profile");
                            getUserDetails();
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            Log.d(TAG, "onNavigationItemSelected: Current firebase user: " + user.getUid());
                            Log.d(TAG, "onNavigationItemSelected: Current firebase user: " + ((UserClientSingleton) getApplicationContext()).getUser().toString());
                            Log.d(TAG, "onNavigationItemSelected: User Location: " + mUserLocation);

                            openFragment(ProfileFragment.newInstance(mUserLocation));

                            return true;
                        case R.id.navigation_history:
                            Log.d(TAG, "switched to history");
                            openFragment(new HistoryFragment());
                            return true;
                    }

                    return false;
                }
            };

}
