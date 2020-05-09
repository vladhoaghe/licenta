package com.example.visualpost_it.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.visualpost_it.R;
import com.example.visualpost_it.UserClient;
import com.example.visualpost_it.dtos.User;
import com.example.visualpost_it.dtos.UserLocation;
import com.example.visualpost_it.fragments.HistoryFragment;
import com.example.visualpost_it.fragments.HomeFragment;
import com.example.visualpost_it.fragments.ProfileFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
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
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.GeoPoint;

import static com.example.visualpost_it.Constants.ERROR_DIALOG_REQUEST;
import static com.example.visualpost_it.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.example.visualpost_it.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

public class HomeScreenActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;
    final String TAG = "HomeScreenActivity";
    TextView fullnameField;
    TextView emailField;
    TextView nicknameField;
    TextView passwordField;
    private boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationClient;
    private UserLocation mUserLocation;
    private FirebaseFirestore mDb;

    String currentUser_nickname;
    String currentUser_email;
    String currentUser_fullname;
    String currentUser_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mDb = FirebaseFirestore.getInstance();

        getProfileDetails();

        Log.d(TAG, "onCreate: " + currentUser_email);
        Log.d(TAG, "onCreate: " + currentUser_fullname);
        Log.d(TAG, "onCreate: " + currentUser_nickname);
        openFragment(HomeFragment.newInstance(currentUser_nickname, currentUser_email, currentUser_fullname, currentUser_password));
        getUserDetails();

        fullnameField = findViewById(R.id.profile_fullname);
        emailField = findViewById(R.id.profile_email);
        nicknameField = findViewById(R.id.profile_nickname);
        passwordField = findViewById(R.id.profile_password);
    }

    private void getProfileDetails() {
        Intent i = getIntent();
        currentUser_nickname  = i.getStringExtra("nickname");
        currentUser_email  = i.getStringExtra("email");
        currentUser_fullname  = i.getStringExtra("fullname");
        currentUser_password  = i.getStringExtra("password");

        User user = new User(currentUser_nickname, currentUser_email, currentUser_password, currentUser_fullname);
        ((UserClient) getApplicationContext()).setUser(user);

        Log.d(TAG, "getProfileDetails: " + currentUser_nickname);
        Log.d(TAG, "getProfileDetails: " + currentUser_email);
        Log.d(TAG, "getProfileDetails: " + currentUser_fullname);
        Log.d(TAG, "getProfileDetails: " + currentUser_password);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(checkMapServices()){
            if(mLocationPermissionGranted){
                getUserDetails();
                Log.d(TAG, "onResume: " + currentUser_email);
                Log.d(TAG, "onResume: " + currentUser_fullname);
                Log.d(TAG, "onResume: " + currentUser_nickname);
                openFragment(HomeFragment.newInstance(currentUser_nickname, currentUser_email, currentUser_fullname, currentUser_password));
                getUserDetails();
            } else {
                getLocationPermission();
            }
        }
    }
    
    public void getUserDetails(){
        if(mUserLocation == null){
            mUserLocation = new UserLocation();
            Intent i = getIntent();
            String userId = i.getStringExtra("userId");
            Log.d(TAG, "getUserDetails: " + userId);

            DocumentReference userRef = mDb
                    .collection(getString(R.string.collection_users))
                    .document(userId);

            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "onComplete: successfully got the user details");

                        User user = task.getResult().toObject(User.class);
                        Log.d(TAG, "onComplete: Home Screen" + user.toString());
                        mUserLocation.setUser(user);
                        ((UserClient) getApplicationContext()).setUser(user);
                        Log.d(TAG, "onComplete: user set: " + ((UserClient) getApplicationContext()).getUser().toString());
                        getLastKnownLocation();
                    }
                }
            });
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
                    .document(userId);
            
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

    private boolean checkMapServices(){
        if(isServicesOK()){
            return isMapsEnabled();
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            Log.d(TAG, "getLocationPermission: " + currentUser_email);
            Log.d(TAG, "getLocationPermission: " + currentUser_fullname);
            Log.d(TAG, "getLocationPermission: " + currentUser_nickname);
            openFragment(HomeFragment.newInstance(currentUser_nickname, currentUser_email, currentUser_fullname, currentUser_password));
            getUserDetails();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(HomeScreenActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(HomeScreenActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        if (requestCode == PERMISSIONS_REQUEST_ENABLE_GPS) {
            if (mLocationPermissionGranted) {
                Log.d(TAG, "onActivityResult: " + currentUser_email);
                Log.d(TAG, "onActivityResult: " + currentUser_fullname);
                Log.d(TAG, "onActivityResult: " + currentUser_nickname);
                openFragment(HomeFragment.newInstance(currentUser_nickname, currentUser_email, currentUser_fullname, currentUser_password));
                getUserDetails();
            } else {
                getLocationPermission();
            }
        }

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
                            openFragment(HomeFragment.newInstance(currentUser_nickname, currentUser_email, currentUser_fullname, currentUser_password));
                            getUserDetails();
                            return true;
                        case R.id.navigation_profile:
                            Log.d(TAG, "switched to profile");
                            getUserDetails();
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            Log.d(TAG, "onNavigationItemSelected: Current firebase user" + user.getUid());
                            Log.d(TAG, "onNavigationItemSelected: Current firebase user" + ((UserClient) getApplicationContext()).getUser().toString());

                            currentUser_nickname = ((UserClient) getApplicationContext()).getUser().getNickname();
                            currentUser_email = ((UserClient) getApplicationContext()).getUser().getEmail();
                            currentUser_fullname = ((UserClient) getApplicationContext()).getUser().getFullName();
                            currentUser_password = ((UserClient) getApplicationContext()).getUser().getPassword();

                            openFragment(ProfileFragment.newInstance(currentUser_nickname, currentUser_email, currentUser_fullname, currentUser_password, mUserLocation));

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
