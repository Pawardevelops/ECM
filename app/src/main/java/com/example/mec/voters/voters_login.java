package com.example.mec.voters;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.mec.R;
import com.example.mec.forgotPassword;
import com.example.mec.services.NavigationService;
import com.example.mec.services.SharedPreferenceHelper;
import com.example.mec.voters.voters_signup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class voters_login extends AppCompatActivity {
    private EditText emailEditText, passwordEditText;
    private AppCompatButton loginButton;
    private TextView forgotPasswordTextView, signupTextView;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;  // Firebase Database reference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voters_login);

        // Initialize Firebase Auth and Database Reference
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("voters");

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.start);
        signupTextView = findViewById(R.id.candidate_login);
        progressBar = findViewById(R.id.progressBar);
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

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(voters_login.this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    loginButton.setVisibility(View.GONE);
                    loginUser(email, password);
                }
            }
        });

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
                    loginButton.setVisibility(View.VISIBLE);

                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            checkUserRole(user.getUid());
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        loginButton.setVisibility(View.VISIBLE);
                        Toast.makeText(voters_login.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Check if the logged-in user is a voter using Realtime Database
    private void checkUserRole(String uid) {
        dbRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String role = dataSnapshot.child("role").getValue(String.class);
                    if ("voter".equals(role)) {
                        // Save login state and navigate to the Voters Dashboard
                        SharedPreferenceHelper.saveLoginInfo(voters_login.this, true, "voter");
                        NavigationService.navigateToActivityAfterLogin(voters_login.this, VotersDashboard.class);
                        Toast.makeText(voters_login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(voters_login.this, "Access Denied: Not a voter", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(voters_login.this, "No user data found", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(voters_login.this, "Failed to retrieve user role", Toast.LENGTH_SHORT).show();
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
                        NavigationService.navigateToActivity(voters_login.this, forgotPassword.class);
                    } else {
                        Toast.makeText(voters_login.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
