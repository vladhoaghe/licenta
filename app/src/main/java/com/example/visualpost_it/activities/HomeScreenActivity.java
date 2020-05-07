package com.example.visualpost_it.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        openFragment(new HomeFragment());
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
                            openFragment(new ProfileFragment());
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
