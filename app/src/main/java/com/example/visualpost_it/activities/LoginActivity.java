package com.example.visualpost_it.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.visualpost_it.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "Login";

    Toolbar toolbar;
    EditText emailField;
    EditText passwordField;

    TextView linkToSignUp;
    TextView linkToForgotPassword;

    private FirebaseAuth mAuth;
    ProgressBar progressBar;

    Button loginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toolbar = findViewById(R.id.toolbar_login);
        toolbar.setTitle(TAG);

        progressBar = findViewById(R.id.progressBar_login);
        progressBar.setVisibility(View.GONE);

        emailField = findViewById(R.id.login_email);
        passwordField = findViewById(R.id.login_password);

        loginButton = findViewById(R.id.btn_login);

        linkToSignUp = findViewById(R.id.link_signup);
        linkToForgotPassword = findViewById(R.id.link_forgot_password);

        mAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                attemptLogin();
            }
        });

        linkToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterProgressMode(progressBar);
                switchToSignUpActivity();
            }
        });

        linkToForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterProgressMode(progressBar);
                switchToForgotPasswordActivity();
            }
        });
    }

    @Override
    public void onBackPressed() {
        switchToMainActivity();
        super.onBackPressed();
    }

    private void switchToMainActivity() {
        Intent switchToMainActivity = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(switchToMainActivity);
    }

    private void switchToForgotPasswordActivity() {
        Intent switchToForgotPassword = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        stopProgressMode(progressBar);
        startActivity(switchToForgotPassword);
    }

    private void switchToSignUpActivity() {
        Intent switchToSignUp = new Intent(LoginActivity.this, SignupActivity.class);
        stopProgressMode(progressBar);
        startActivity(switchToSignUp);
    }

    private void attemptLogin() {

        String email;
        String password;

        if(!formCompletedAccordingly()){
            Log.w(TAG, "Form not completed accordingly");
            return;
        } else {
            enterProgressMode(progressBar);
            email = emailField.getText().toString();
            password = passwordField.getText().toString();
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            Log.d(TAG, "signInWithEmail:succes");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            stopProgressMode(progressBar);
                        } else {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }

    private boolean formCompletedAccordingly() {

        boolean valid = true;

        String email = emailField.getText().toString();
        if(!email.contains("@") || !email.contains(".")){
            emailField.setError("Incorrect email format");
            valid=false;
        } else {
            emailField.setError(null);
        }

        String password = passwordField.getText().toString();
        if(TextUtils.isEmpty(password)){
            Toast.makeText(LoginActivity.this, "Password required",
                    Toast.LENGTH_LONG).show();
            valid = false;
        } else {
            passwordField.setError(null);
        }

        return valid;
    }

//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser != null){
//            updateUI(currentUser);
//        }
//    }

    private void updateUI(FirebaseUser currentUser) {
        Intent switchToHomeScreen = new Intent (this, HomeScreenActivity.class);
        if(currentUser != null){
            startActivity(switchToHomeScreen);
        } else {
            Toast.makeText(LoginActivity.this, "Authentication failed",
                    Toast.LENGTH_SHORT).show();
        }
    }



    private void enterProgressMode(ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
    }


    private void stopProgressMode(ProgressBar progressBar) {
        progressBar.setVisibility(View.GONE);
    }

}
