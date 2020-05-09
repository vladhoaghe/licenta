package com.example.visualpost_it.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.example.visualpost_it.adapters.UserRecyclerAdapter;
import com.example.visualpost_it.fragments.HistoryFragment;
import com.example.visualpost_it.fragments.HomeFragment;
import com.example.visualpost_it.fragments.ProfileFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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
    UserRecyclerAdapter userRecyclerAdapter;

    String currentUser_nickname;
    String currentUser_email;
    String currentUser_fullname;
    String currentUser_password;

    @Override
    protected void onResume() {
        super.onResume();
        openFragment(new HomeFragment());
        if(checkMapServices()){
            if(mLocationPermissionGranted){
                getUserDetails();
                Log.d(TAG, "onResume: " + currentUser_email);
                Log.d(TAG, "onResume: " + currentUser_fullname);
                Log.d(TAG, "onResume: " + currentUser_nickname);
                openFragment(HomeFragment.newInstance(currentUser_nickname, currentUser_email, currentUser_fullname, currentUser_password));
            } else {
                getLocationPermission();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        getUserDetails();
        Log.d(TAG, "onCreate: " + currentUser_email);
        Log.d(TAG, "onCreate: " + currentUser_fullname);
        Log.d(TAG, "onCreate: " + currentUser_nickname);
        openFragment(HomeFragment.newInstance(currentUser_nickname, currentUser_email, currentUser_fullname, currentUser_password));


        fullnameField = findViewById(R.id.profile_fullname);
        emailField = findViewById(R.id.profile_email);
        nicknameField = findViewById(R.id.profile_nickname);
        passwordField = findViewById(R.id.profile_password);
    }

    private boolean checkMapServices(){
        if(isServicesOK()){
            if(isMapsEnabled()){
                return true;
            }
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
            getUserDetails();
            Log.d(TAG, "getLocationPermission: " + currentUser_email);
            Log.d(TAG, "getLocationPermission: " + currentUser_fullname);
            Log.d(TAG, "getLocationPermission: " + currentUser_nickname);
            openFragment(HomeFragment.newInstance(currentUser_nickname, currentUser_email, currentUser_fullname, currentUser_password));
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
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if(mLocationPermissionGranted){
                    getUserDetails();
                    Log.d(TAG, "onActivityResult: " + currentUser_email);
                    Log.d(TAG, "onActivityResult: " + currentUser_fullname);
                    Log.d(TAG, "onActivityResult: " + currentUser_nickname);
                    openFragment(HomeFragment.newInstance(currentUser_nickname, currentUser_email, currentUser_fullname, currentUser_password));
                }
                else{
                    getLocationPermission();
                }
            }
        }

    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void getUserDetails(){
        Intent intent = getIntent();
        currentUser_nickname = intent.getStringExtra("nickname");
        currentUser_email = intent.getStringExtra("email");
        currentUser_fullname = intent.getStringExtra("fullname");
        currentUser_password = intent.getStringExtra("password");
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch(menuItem.getItemId()) {
                        case R.id.navigation_home:
                            Log.d(TAG, "Switched to home");
                            getUserDetails();
                            openFragment(HomeFragment.newInstance(currentUser_nickname, currentUser_email, currentUser_fullname, currentUser_password));
                            return true;
                        case R.id.navigation_profile:
                            Log.d(TAG, "switched to profile");
                            getUserDetails();
                            openFragment(ProfileFragment.newInstance(currentUser_nickname, currentUser_email, currentUser_fullname, currentUser_password));
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
