package com.example.mec.candidate;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mec.R;
import com.example.mec.services.NavigationService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class CandidateSignup extends AppCompatActivity {
    private EditText firstNameInput, lastNameInput, emailInput, registrationNoInput, passwordInput, confirmPasswordInput, sloganInput;
    private Button signUpButton;
    private TextView alreadyHaveAccount;

    // Firebase instances
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_candidate_signup);

        // Initialize Firebase Auth and Realtime Database
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("candidates");

        // Initialize UI components
        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        emailInput = findViewById(R.id.emailInput);
        registrationNoInput = findViewById(R.id.registrationNoInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        sloganInput = findViewById(R.id.sloganInput);
        signUpButton = findViewById(R.id.candidateSignUp);
        alreadyHaveAccount = findViewById(R.id.candidate_login);

        // Set click listener for the sign-up button
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get input values
                String firstName = firstNameInput.getText().toString();
                String lastName = lastNameInput.getText().toString();
                String email = emailInput.getText().toString();
                String registrationNo = registrationNoInput.getText().toString();
                String password = passwordInput.getText().toString();
                String confirmPassword = confirmPasswordInput.getText().toString();
                String slogan = sloganInput.getText().toString();

                // Validate inputs
                if (validateInputs(firstName, lastName, email, registrationNo, password, confirmPassword, slogan)) {
                    // Show loading on button
                    setLoadingState(true);

                    // Register the user with Firebase Authentication
                    registerCandidate(firstName, lastName, email, registrationNo, password, slogan);
                }
            }
        });

        // Set click listener for "Already have an account?" text
        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to login screen
                NavigationService.navigateToActivity(CandidateSignup.this, candidateLogin.class);
            }
        });
    }

    // Method to validate the inputs
    private boolean validateInputs(String firstName, String lastName, String email, String registrationNo, String password, String confirmPassword, String slogan) {
        if (firstName.isEmpty()) {
            firstNameInput.setError("First name is required");
            return false;
        }
        if (lastName.isEmpty()) {
            lastNameInput.setError("Last name is required");
            return false;
        }
        if (email.isEmpty()) {
            emailInput.setError("Email is required");
            return false;
        }
        if (registrationNo.isEmpty()) {
            registrationNoInput.setError("Registration number is required");
            return false;
        }
        if (password.isEmpty()) {
            passwordInput.setError("Password is required");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordInput.setError("Passwords do not match");
            return false;
        }
        if (slogan.isEmpty()) {
            sloganInput.setError("Slogan is required");
            return false;
        }
        return true;
    }

    // Method to register the candidate using Firebase Authentication and Realtime Database
    private void registerCandidate(String firstName, String lastName, String email, String registrationNo, String password, String slogan) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Get the current Firebase user
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();

                            // Create a map for the candidate profile
                            Map<String, Object> candidateProfile = new HashMap<>();
                            candidateProfile.put("firstName", firstName);
                            candidateProfile.put("lastName", lastName);
                            candidateProfile.put("email", email);
                            candidateProfile.put("registrationNo", registrationNo);
                            candidateProfile.put("slogan", slogan);
                            candidateProfile.put("role", "candidate");

                            // Store the candidate profile in Realtime Database
                            dbRef.child(userId).setValue(candidateProfile)
                                    .addOnSuccessListener(aVoid -> {
                                        // Registration success
                                        Toast.makeText(CandidateSignup.this, "Sign-up successful", Toast.LENGTH_SHORT).show();

                                        // Navigate to success screen
                                        NavigationService.navigateToActivity(CandidateSignup.this, CandidateSuccessfullyRegistered.class);
                                    })
                                    .addOnFailureListener(e -> {
                                        // Registration failed in Realtime Database
                                        Toast.makeText(CandidateSignup.this, "Failed to register candidate: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        setLoadingState(false); // Re-enable the button after failure
                                    });
                        }
                    } else {
                        // Firebase Authentication failed
                        Toast.makeText(CandidateSignup.this, "Sign-up failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        setLoadingState(false); // Re-enable the button after failure
                    }
                });
    }

    // Method to set the loading state for the sign-up button
    private void setLoadingState(boolean isLoading) {
        if (isLoading) {
            signUpButton.setEnabled(false);
            signUpButton.setText("Signing Up...");  // Show loading text
        } else {
            signUpButton.setEnabled(true);
            signUpButton.setText("Sign Up");  // Reset to default text
        }
    }
}
