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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "Login";

    Toolbar toolbar;
    EditText nicknameField;
    EditText passwordField;

    TextView linkToSignUp;
    TextView linkToForgotPassword;

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

        nicknameField = findViewById(R.id.login_nickname);
        passwordField = findViewById(R.id.login_password);

        loginButton = findViewById(R.id.btn_login);

        linkToSignUp = findViewById(R.id.link_signup);
        linkToForgotPassword = findViewById(R.id.link_forgot_password);

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

    private void attemptLogin() {

        if(!formCompletedAccordingly()){
            Log.w(TAG, "Form not completed accordingly");
            return;
        } else {
            enterProgressMode(progressBar);
            signIn();
        }
    }

    private boolean formCompletedAccordingly() {

        boolean valid = true;

//        String email = emailField.getText().toString();
//        if(!email.contains("@") || !email.contains(".")){
//            emailField.setError("Incorrect email format");
//            valid=false;
//        } else {
//            emailField.setError(null);
//        }

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

    private void signIn() {

        String userEnteredNickname = nicknameField.getText().toString().trim();
        String userEnteredPassword = passwordField.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        Query checkUser = reference.orderByChild("nickname").equalTo(userEnteredNickname);
        checkUser.keepSynced(true);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: " + dataSnapshot.toString());

                if (dataSnapshot.exists()){
                    nicknameField.setError(null);

                    Log.d(TAG, "onDataChange: dataSnapshot: " + dataSnapshot.getValue());
                    String value = dataSnapshot.getValue().toString();
                    String userId = value.substring(value.indexOf("{") + 1, value.indexOf("="));
                    Log.d(TAG, "onDataChange: userID: " + userId);

                    String passwordFromDB = dataSnapshot.child(userId).child("password").getValue(String.class);

                    Log.d(TAG, "onDataChange: data snapshot: " + dataSnapshot.child(userId));

                    if(Objects.requireNonNull(passwordFromDB).equals(userEnteredPassword)){
                        String emailFromDB = dataSnapshot.child(userId).child("email").getValue(String.class);
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(emailFromDB,
                                passwordFromDB)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        Log.d(TAG, "onComplete: Ducati tati");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                        String fullnameFromDB = dataSnapshot.child(userId).child("fullName").getValue(String.class);

                        Intent intent = new Intent(getApplicationContext(), HomeScreenActivity.class);

                        Log.d(TAG, "onDataChange: User Id: " + userId);
                        Log.d(TAG, "onDataChange: userEnteredNickname: " + userEnteredNickname);
                        Log.d(TAG, "onDataChange: fullnameFromDB: " + fullnameFromDB);
                        Log.d(TAG, "onDataChange: emailFromDB: " + emailFromDB);
                        Log.d(TAG, "onDataChange: userEnteredPassword: " + userEnteredPassword);

                        intent.putExtra("userId", userId);
                        intent.putExtra("nickname", userEnteredNickname);
                        intent.putExtra("fullname", fullnameFromDB);
                        intent.putExtra("email", emailFromDB);
                        intent.putExtra("password", userEnteredPassword);
                        stopProgressMode(progressBar);

                        startActivity(intent);
                    } else {
                        stopProgressMode(progressBar);
                        passwordField.setError("Wrong Password");
                        passwordField.requestFocus();
                    }
                } else {
                    stopProgressMode(progressBar);
                    nicknameField.setError("No such user exists");
                    nicknameField.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: databaseError" + databaseError.toString());
            }
        });
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

    private void enterProgressMode(ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void stopProgressMode(ProgressBar progressBar) {
        progressBar.setVisibility(View.GONE);
    }

}
