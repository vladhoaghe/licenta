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
import com.example.visualpost_it.UserClient;
import com.example.visualpost_it.dtos.User;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "Login";

    Toolbar toolbar;
    EditText emailField;
    EditText passwordField;

    TextView linkToSignUp;
    TextView linkToForgotPassword;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore mDb;
    ProgressBar progressBar;
    private FirebaseFirestore db;
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
        mDb = FirebaseFirestore.getInstance();
        db = FirebaseFirestore.getInstance();

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
            updateUI();
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

//    private void setupFirebaseAuth(){
//        Log.d(TAG, "setupFirebaseAuth: started.");
//
//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                Log.d(TAG, "onAuthStateChanged: " + user.getUid());
//                if (user != null) {
//                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
//                    Toast.makeText(LoginActivity.this, "Authenticated with: " + user.getEmail(), Toast.LENGTH_SHORT).show();
//
//                    FirebaseFirestore db = FirebaseFirestore.getInstance();
//                    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
//                            .setTimestampsInSnapshotsEnabled(true)
//                            .build();
//                    db.setFirestoreSettings(settings);
//
//                    DocumentReference userRef = db.collection(getString(R.string.collection_users))
//                            .document(user.getUid());
//
//                    userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                            if(task.isSuccessful()){
//                                Log.d(TAG, "onComplete: successfully set the user client.");
//                                User user = task.getResult().toObject(User.class);
//                                Log.d(TAG, "onComplete: Login " + user.toString());
//                                ((UserClient)(getApplicationContext())).setUser(user);
//                            }
//                        }
//                    });
//
//                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
//                    finish();
//
//                } else {
//                    // User is signed out
//                    Log.d(TAG, "onAuthStateChanged:signed_out");
//                }
//                // ...
//            }
//        };
//    }

    private void updateUI() {

        String userEnteredNickname = emailField.getText().toString().trim();
        String userEnteredPassword = passwordField.getText().toString().trim();

//        DocumentReference newUserRef = mDb
//                .collection(getString(R.string.collection_users))
//                .document(FirebaseAuth.getInstance().getUid());
//
//        newUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                Log.d(TAG, "onComplete: " + task.getResult().toString());
//                User user = task.getResult().toObject(User.class);
//                Log.d(TAG, "onComplete: " + user.toString());
//                if(user.getNickname().equals(userEnteredNickname)){
//                    if(user.getPassword().equals(userEnteredPassword)){
//                        Intent intent = new Intent(getApplicationContext(), HomeScreenActivity.class);
//
//                        intent.putExtra("nickname", userEnteredNickname);
//                        intent.putExtra("fullname", user.getFullName());
//                        intent.putExtra("email", user.getEmail());
//                        intent.putExtra("password", userEnteredPassword);
//
//                        stopProgressMode(progressBar);
//                        startActivity(intent);
//                    } else {
//                        //TODO not working
//                        Log.d(TAG, "onComplete: Wrong Password");
//                        stopProgressMode(progressBar);
//                        passwordField.setError("Wrong Password");
//                        passwordField.requestFocus();
//                    }
//                } else {
//                    //TODO not working
//                    Log.d(TAG, "onComplete: User does not exist");
//                    stopProgressMode(progressBar);
//                    emailField.setError("No such user exists");
//                    emailField.requestFocus();
//                }
//            }
//        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        Query checkUser = reference.orderByChild("nickname").equalTo(userEnteredNickname);
        checkUser.keepSynced(true);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: " + dataSnapshot.toString());

                if (dataSnapshot.exists()){
                    emailField.setError(null);

                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    Log.d(TAG, "onDataChange: " + dataSnapshot.getValue());
                    String value = dataSnapshot.getValue().toString();
                    String userId = value.substring(value.indexOf("{") + 1, value.indexOf("="));
                    Log.d(TAG, "onDataChange: userID" + userId);

                    String passwordFromDB = dataSnapshot.child(userId).child("password").getValue(String.class);

                    Log.d(TAG, "onDataChange: data snapshot: " + dataSnapshot.child(userId));
                    Log.d(TAG, "onDataChange: password from db: " + passwordFromDB);
                    Log.d(TAG, "onDataChange: user id " + userId);

                    Log.d(TAG, "onDataChange: user password " + userEnteredPassword);
                    Log.d(TAG, "onDataChange: user nickname " + userEnteredNickname);

                    if(Objects.requireNonNull(passwordFromDB).equals(userEnteredPassword)){
                        String emailFromDB = dataSnapshot.child(userId).child("email").getValue(String.class);
                        String fullnameFromDB = dataSnapshot.child(userId).child("fullName").getValue(String.class);

                        Intent intent = new Intent(getApplicationContext(), HomeScreenActivity.class);

                        Log.d(TAG, "onDataChange: userId: " + userId);
                        Log.d(TAG, "onDataChange: " + userEnteredNickname);
                        Log.d(TAG, "onDataChange: " + fullnameFromDB);
                        Log.d(TAG, "onDataChange: " + emailFromDB);
                        Log.d(TAG, "onDataChange: " + userEnteredPassword);

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
                    emailField.setError("No such user exists");
                    emailField.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        Intent switchToHomeScreen = new Intent (this, HomeScreenActivity.class);
//        if(currentUser != null){
//            startActivity(switchToHomeScreen);
//        } else {
//            Toast.makeText(LoginActivity.this, "Authentication failed",
//                    Toast.LENGTH_SHORT).show();
//        }
    }



    private void enterProgressMode(ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
    }


    private void stopProgressMode(ProgressBar progressBar) {
        progressBar.setVisibility(View.GONE);
    }

}
