package com.example.visualpost_it.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.visualpost_it.R;
import com.example.visualpost_it.fragments.HistoryFragment;
import com.example.visualpost_it.fragments.HomeFragment;
import com.example.visualpost_it.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeScreenActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;
    final String TAG = "MainFragment";
    TextView fullnameField;
    TextView emailField;
    TextView nicknameField;
    TextView passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        openFragment(new HomeFragment());

        fullnameField = findViewById(R.id.profile_fullname);
        emailField = findViewById(R.id.profile_email);
        nicknameField = findViewById(R.id.profile_nickname);
        passwordField = findViewById(R.id.profile_password);


    }

    private void showAllUserData() {



    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
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
                            Intent intent = getIntent();
                            String currentUser_nickname = intent.getStringExtra("nickname");
                            String currentUser_email = intent.getStringExtra("email");
                            String currentUser_fullname = intent.getStringExtra("fullname");
                            String currentUser_password = intent.getStringExtra("password");
                            showAllUserData();
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
