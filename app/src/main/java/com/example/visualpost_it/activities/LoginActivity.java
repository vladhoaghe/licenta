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
import com.example.visualpost_it.dtos.User;
import com.example.visualpost_it.util.UserClientSingleton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
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

    TextInputLayout nicknameBox;
    TextInputLayout passwordBox;

    ProgressBar progressBar;
    Button loginButton;

    String currentUser_nickname;
    String currentUser_password;
    String currentUser_fullname;
    String currentUser_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d(TAG, "onCreate: LoginActivity");

        toolbar = findViewById(R.id.toolbar_login);
        toolbar.setTitle(TAG);

        progressBar = findViewById(R.id.progressBar_login);
        progressBar.setVisibility(View.GONE);

        nicknameBox = findViewById(R.id.login_nickname_box);
        passwordBox = findViewById(R.id.login_password_box);

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
        Log.d(TAG, "attemptLogin: ");
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
            passwordBox.setError("Password is required");
            valid = false;
        } else {
            passwordBox.setError(null);
        }

        return valid;
    }

    private void setProfileDetails(String currentUser_nickname, String currentUser_email, String currentUser_password, String currentUser_fullname) {

        User user = new User(currentUser_nickname, currentUser_email, currentUser_password, currentUser_fullname);
        ((UserClientSingleton) getApplicationContext()).setUser(user);

        Log.d(TAG, "setProfileDetails: " + currentUser_nickname);
        Log.d(TAG, "setProfileDetails: " + currentUser_email);
        Log.d(TAG, "setProfileDetails: " + currentUser_fullname);
        Log.d(TAG, "setProfileDetails: " + currentUser_password);
    }

    private void signIn() {
        String userEnteredNickname = nicknameField.getText().toString().trim();
        String userEnteredPassword = passwordField.getText().toString().trim();

        Log.d(TAG, "signIn with user: " + userEnteredNickname + " and password: " + userEnteredPassword );

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        Query checkUser = reference.orderByChild("nickname").equalTo(userEnteredNickname);
        checkUser.keepSynced(true);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "signIn: " + dataSnapshot.toString());

                if (dataSnapshot.exists()){
                    nicknameBox.setError(null);

                    Log.d(TAG, "signIn: dataSnapshot: " + dataSnapshot.getValue());
                    String value = dataSnapshot.getValue().toString();
                    String userId = value.substring(value.indexOf("{") + 1, value.indexOf("="));
                    Log.d(TAG, "signIn: userID: " + userId);

                    String passwordFromDB = dataSnapshot.child(userId).child("password").getValue(String.class);

                    Log.d(TAG, "signIn: data snapshot: " + dataSnapshot.child(userId));
                    Log.d(TAG, "signIn: passwordFromDB: " + passwordFromDB);

                    if(Objects.requireNonNull(passwordFromDB).equals(userEnteredPassword)){
                        String emailFromDB = dataSnapshot.child(userId).child("email").getValue(String.class);
                        Log.d(TAG, "signIn: emailFromDB: " + emailFromDB);
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(emailFromDB,
                                passwordFromDB)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        Log.d(TAG, "onSuccess: Ducati tati");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        String fullnameFromDB = dataSnapshot.child(userId).child("fullName").getValue(String.class);

                        Intent intent = new Intent(LoginActivity.this, PermissionsActivity.class);

                        setProfileDetails(userEnteredNickname, emailFromDB, userEnteredPassword, fullnameFromDB);
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
                        passwordBox.setError("Wrong Password");
                        passwordBox.requestFocus();
                    }
                } else {
                    stopProgressMode(progressBar);
                    nicknameBox.setError("No such user exists");
                    nicknameBox.requestFocus();
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
