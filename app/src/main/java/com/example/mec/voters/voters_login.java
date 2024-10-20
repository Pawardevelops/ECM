package com.example.mec.voters;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.mec.R;
import com.example.mec.forgotPassword;
import com.example.mec.services.NavigationService;
import com.example.mec.services.SharedPreferenceHelper;
import com.example.mec.voters.voters_signup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class voters_login extends AppCompatActivity {
    private EditText emailEditText, passwordEditText;
    private AppCompatButton loginButton;
    private TextView forgotPasswordTextView, signupTextView;
    private ProgressBar progressBar;  // ProgressBar instance

    // Firebase Authentication instance
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voters_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.start);
        signupTextView = findViewById(R.id.candidate_login);
        progressBar = findViewById(R.id.progressBar);  // Initialize the ProgressBar
        forgotPasswordTextView = findViewById(R.id.forgotPassword);


        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                if (email.isEmpty()) {
                    Toast.makeText(voters_login.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                } else {
                    sendPasswordResetEmail(email);
                }
            }
        });

        // Set click listener for login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(voters_login.this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Show progress bar and disable login button
                    progressBar.setVisibility(View.VISIBLE);
                    loginButton.setVisibility(View.GONE);

                    // Perform login authentication using Firebase
                    loginUser(email, password);
                }
            }
        });

        // Set click listener for "Sign Up"
        signupTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationService.navigateToActivity(voters_login.this, voters_signup.class);
            }
        });
    }

    // Login user using Firebase Authentication
    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    loginButton.setVisibility(View.VISIBLE);  // Re-enable the login button

                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {

                            progressBar.setVisibility(View.GONE);
                            loginButton.setVisibility(View.VISIBLE);
                            // For example, when an admin logs in:
                            SharedPreferenceHelper.saveLoginInfo(voters_login.this, true, "voter");

                            NavigationService.navigateToActivity(voters_login.this, VotersDashboard.class);
                        }
                    } else {

                        progressBar.setVisibility(View.GONE);
                        loginButton.setVisibility(View.VISIBLE);
                        Toast.makeText(voters_login.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void sendPasswordResetEmail(String email) {
        progressBar.setVisibility(View.VISIBLE);
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(voters_login.this, "Password reset email sent.", Toast.LENGTH_SHORT).show();
                        // Navigate to the forgot password activity if needed
                        NavigationService.navigateToActivity(voters_login.this, forgotPassword.class);
                    } else {
                        Toast.makeText(voters_login.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

