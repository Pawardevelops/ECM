package com.example.mec.candidate;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mec.R;
import com.example.mec.services.NavigationService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class candidateLogin extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginButton;
    private TextView forgotPassword, signUpLink;
    private ProgressBar loginProgressBar;

    // Firebase authentication and Realtime Database instance
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_candidate_login);

        // Initialize Firebase Auth and Realtime Database
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("candidates");

        // Initialize UI components
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.start);
        forgotPassword = findViewById(R.id.forget_candidate_password);
        signUpLink = findViewById(R.id.candidate_login);
        loginProgressBar = findViewById(R.id.loginProgressBar);

        // Handle the login button click
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get input from the user
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                // Validate the email and password fields
                if (validateLogin(email, password)) {
                    // Show loading state
                    setLoadingState(true);
                    // Attempt to log in with Firebase Auth
                    loginCandidate(email, password);
                } else {
                    // Show validation error
                    Toast.makeText(candidateLogin.this, "Please fill in both email and password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Handle forgot password text click
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement forgot password logic here
                Toast.makeText(candidateLogin.this, "Forgot password logic here", Toast.LENGTH_SHORT).show();
                // Example: NavigationService.navigateToActivity(candidateLogin.this, ForgotPasswordActivity.class);
            }
        });

        // Handle the sign-up text click
        signUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the Candidate Sign-Up page
                NavigationService.navigateToActivity(candidateLogin.this, CandidateSignup.class);
            }
        });
    }

    // Validate email and password fields
    private boolean validateLogin(String email, String password) {
        return !email.isEmpty() && !password.isEmpty();
    }

    // Method to log in the candidate using Firebase Authentication
    private void loginCandidate(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Check user role in Realtime Database
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            checkUserRole(user.getUid());
                        }
                    } else {
                        // Login failed, show error message
                        setLoadingState(false);
                        Toast.makeText(candidateLogin.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

    }

    // Check if the logged-in user is a candidate using Realtime Database
    private void checkUserRole(String uid) {
        dbRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String role = dataSnapshot.child("role").getValue(String.class);
                    if ("candidate".equals(role)) {
                        // Navigate to candidate dashboard
                        setLoadingState(false);
                        Toast.makeText(candidateLogin.this, "Login successful", Toast.LENGTH_SHORT).show();
                        NavigationService.navigateToActivity(candidateLogin.this, candidateDashboard.class);
                    } else {
                        // If not a candidate, show error
                        setLoadingState(false);
                        Toast.makeText(candidateLogin.this, "Access denied: You are not a candidate", Toast.LENGTH_LONG).show();
                        FirebaseAuth.getInstance().signOut();  // Sign out the user if they are not a candidate
                    }
                } else {
                    // No user data found
                    setLoadingState(false);
                    Toast.makeText(candidateLogin.this, "No user data found", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                setLoadingState(false);
                Toast.makeText(candidateLogin.this, "Failed to retrieve user role", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to show/hide loading state
    private void setLoadingState(boolean isLoading) {
        if (isLoading) {
            loginProgressBar.setVisibility(View.VISIBLE);
            loginButton.setEnabled(false);
            loginButton.setVisibility(View.INVISIBLE);
        } else {
            loginProgressBar.setVisibility(View.GONE);
            loginButton.setEnabled(true);
            loginButton.setVisibility(View.VISIBLE);
        }
    }
}
