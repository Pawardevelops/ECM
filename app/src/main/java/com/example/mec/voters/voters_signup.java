package com.example.mec.voters;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.mec.R;
import com.example.mec.services.NavigationService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class voters_signup extends AppCompatActivity {
    private EditText firstNameEditText, lastNameEditText, emailEditText, registrationNoEditText, passwordEditText, confirmPasswordEditText;
    private AppCompatButton signupButton;
    private TextView loginTextView;

    // Firebase Authentication and Firestore instances
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voters_signup);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        firstNameEditText = findViewById(R.id.candidate_sign_input_fields1).findViewById(R.id.first_name);
        lastNameEditText = findViewById(R.id.candidate_sign_input_fields1).findViewById(R.id.last_name);
        emailEditText = findViewById(R.id.candidate_sign_input_fields2).findViewById(R.id.email);
        registrationNoEditText = findViewById(R.id.candidate_sign_input_fields2).findViewById(R.id.registration_no);
        passwordEditText = findViewById(R.id.candidate_sign_input_fields3).findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.candidate_sign_input_fields3).findViewById(R.id.confirm_password);
        signupButton = findViewById(R.id.candidateSignUp);
        loginTextView = findViewById(R.id.candidate_login);

        // Set click listener for sign-up button
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = firstNameEditText.getText().toString().trim();
                String lastName = lastNameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String registrationNo = registrationNoEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || registrationNo.isEmpty() ||
                        password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(voters_signup.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(voters_signup.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                } else {
                    // Perform user registration with Firebase
                    registerUser(firstName, lastName, email, registrationNo, password);
                }
            }
        });

        // Set click listener for "Login"
        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationService.navigateToActivity(voters_signup.this, voters_login.class);
            }
        });
    }

    // Register the user using Firebase Authentication and Firestore
    private void registerUser(String firstName, String lastName, String email, String registrationNo, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(voters_signup.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                        // Redirect to success page
                        NavigationService.navigateToActivity(voters_signup.this, voter_registered_successfully.class);
                    } else {
                        Exception exception = task.getException();
                        Toast.makeText(voters_signup.this, "Registration failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
