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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "Sign Up";

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    EditText fullnameField;
    EditText nicknameField;
    EditText emailField;
    EditText passwordField;
    EditText verifyPasswordField;

    TextView linkToLogin;

    Button createAccount;
    Toolbar toolbar;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        toolbar = findViewById(R.id.toolbar_signup);
        toolbar.setTitle(TAG);

        progressBar = findViewById(R.id.progressBar_signup);
        progressBar.setVisibility(View.GONE);

        fullnameField = findViewById(R.id.fullname_signup);
        nicknameField = findViewById(R.id.nickname_signup);
        emailField = findViewById(R.id.email_signup);
        passwordField = findViewById(R.id.password_signup);
        verifyPasswordField = findViewById(R.id.verify_password_signup);

        createAccount = findViewById(R.id.btn_create_account);

        linkToLogin = findViewById(R.id.link_login);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });

        linkToLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                enterProgressMode(progressBar);
                switchToLoginActivity();
            }
        });

    }

    @Override
    public void onBackPressed() {
        switchToMainActivity();
        super.onBackPressed();
    }


    private void createAccount() {
        final String nickname;
        final String email;
        final String password;
        final String fullname;

        if(!formCompletedAccordingly()){
            return;
        } else {
            enterProgressMode(progressBar);
            fullname = fullnameField.getText().toString().trim();
            nickname = nicknameField.getText().toString().trim();
            email = emailField.getText().toString().trim();
            password = passwordField.getText().toString().trim();
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        Log.d(TAG, "onComplete: " + currentUser.getEmail());
                        Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());
                        if(task.isSuccessful()){
                            onAuthSucces(Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getUser()), nickname, password, fullname);
                            stopProgressMode(progressBar);
                        } else {
                            Toast.makeText(SignupActivity.this, "Sign Up Failed",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    private void onAuthSucces(FirebaseUser user, String nickname, String password, String fullname) {
        writeNewUser(user.getUid(), nickname, user.getEmail(), password, fullname);
    }

    private void writeNewUser(String userId, String nickname, String email, String password, String fullname){
        User user = new User(nickname, email, password, fullname);

        Log.d(TAG, "writeNewUser: " + user.getEmail());
        Log.d(TAG, "writeNewUser: " + user.getFullName());
        Log.d(TAG, "writeNewUser: " + user.getNickname());
        Log.d(TAG, "writeNewUser: " + user.getPassword());
        Log.d(TAG, "writeNewUser: " + user.getEmail());

        mDatabase.child(userId).setValue(user);

        startActivity(new Intent(SignupActivity.this, MainActivity.class));
        finish();
    }

    private void switchToMainActivity() {
        Intent switchToMainActivity = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(switchToMainActivity);
    }

    private void switchToLoginActivity() {
        stopProgressMode(progressBar);
        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
    }

    private boolean formCompletedAccordingly() {
        boolean valid = true;

        String nickname = nicknameField.getText().toString();
        if(TextUtils.isEmpty(nickname)){
            nicknameField.setError("Nickname required!");
            valid = false;
        } else {
            nicknameField.setError(null);
        }

        String email = emailField.getText().toString();
        if(!email.contains("@") || TextUtils.isEmpty(email)){
            emailField.setError("Invalid email format");
            valid = false;
        } else {
            emailField.setError(null);
        }

        String password = passwordField.getText().toString().trim();
        String verifyPassword = verifyPasswordField.getText().toString().trim();

        if (!password.equals(verifyPassword)){
            verifyPasswordField.setError("Passwords are not the same");
            Log.w(TAG, "Password are not the same");
            valid = false;
        } else {
            Log.d(TAG, "Parola" + password);
            Log.d(TAG, "verificare" + verifyPassword);
            verifyPasswordField.setError(null);
        }

        return valid;
    }

    private void enterProgressMode(ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
    }


    private void stopProgressMode(ProgressBar progressBar) {
        progressBar.setVisibility(View.GONE);
    }
}
