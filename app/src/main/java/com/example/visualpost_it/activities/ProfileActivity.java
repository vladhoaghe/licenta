package com.example.visualpost_it.activities;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.visualpost_it.R;
import com.example.visualpost_it.fragments.ProfileFragment;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    private ProgressBar progressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.fragment_profile);
        Log.d(TAG, "onCreate: started");

        init();
    }

    private void init(){
        Log.d(TAG, "init: inflating" + R.string.profile_fragment);
        ProfileFragment profileFragment = new ProfileFragment();
        FragmentTransaction fragmentTransaction = ProfileActivity.this.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, profileFragment);
        fragmentTransaction.addToBackStack(getString(R.string.profile_fragment));
        fragmentTransaction.commit();
    }
}
