package com.example.visualpost_it.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.visualpost_it.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private static final String TAG = "Forgot Password";
    private Button resetPasswordButton;
    private EditText emailInput;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        toolbar = findViewById(R.id.toolbar_forgot_password);
        toolbar.setTitle(TAG);

        progressBar = findViewById(R.id.progressBar_forgot);
        progressBar.setVisibility(View.GONE);

        resetPasswordButton = findViewById(R.id.btn_reset_password);
        emailInput = findViewById(R.id.forgot_password_email);
        mAuth = FirebaseAuth.getInstance();

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = emailInput.getText().toString();

                if(TextUtils.isEmpty(userEmail)){
                    Toast.makeText(ForgotPasswordActivity.this,
                            "Write a valid email address", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    sendResetPasswordEmail(userEmail);
                }

            }
        });


    }

    @Override
    public void onBackPressed() {
        switchToLoginActivity();
        super.onBackPressed();
    }

    private void sendResetPasswordEmail(String userEmail) {

        mAuth.sendPasswordResetEmail(userEmail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        enterProgressMode(progressBar);
                        if(task.isSuccessful()){
                            Toast.makeText(ForgotPasswordActivity.this,
                                    "Please check your email account", Toast.LENGTH_LONG).show();
                            stopProgressMode(progressBar);

                            switchToLoginActivity();
                        } else {
                            String exception = task.getException().getMessage();
                            Toast.makeText(ForgotPasswordActivity.this,
                                    "Error occured: " + exception, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void enterProgressMode(ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
    }


    private void stopProgressMode(ProgressBar progressBar) {
        progressBar.setVisibility(View.GONE);
    }

    private void switchToLoginActivity() {

        startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
    }
}
