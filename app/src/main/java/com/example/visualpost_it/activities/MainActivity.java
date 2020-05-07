package com.example.visualpost_it.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.widget.Toolbar;

import com.example.visualpost_it.R;

public class MainActivity extends Activity {

    private static final String TAG = "Virtual Post-It";

    Button login_button;
    Button signUp_button;

    Toolbar toolbar;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle(TAG);

        progressBar = findViewById(R.id.progressBar_main);
        progressBar.setVisibility(View.GONE);

        login_button = findViewById(R.id.login_button);
        signUp_button = findViewById(R.id.signup_button);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterProgressMode(progressBar);
                openLoginActivity();
            }
        });

        signUp_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterProgressMode(progressBar);
                openSignUpActivity();
            }
        });
    }

    public void openSignUpActivity() {
        Intent switchToSignUpScreen = new Intent(this, SignupActivity.class);
        stopProgressMode(progressBar);
        startActivity(switchToSignUpScreen);
    }

    public void openLoginActivity(){
        Intent switchToLoginScreen = new Intent(this, LoginActivity.class);
        stopProgressMode(progressBar);
        startActivity(switchToLoginScreen);
    }

    private void enterProgressMode(ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
    }


    private void stopProgressMode(ProgressBar progressBar) {
        progressBar.setVisibility(View.GONE);
    }


}
