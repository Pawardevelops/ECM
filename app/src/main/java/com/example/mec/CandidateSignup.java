package com.example.mec;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mec.services.NavigationService;

public class CandidateSignup extends AppCompatActivity {
    private EditText firstNameInput, lastNameInput, emailInput, registrationNoInput, passwordInput, confirmPasswordInput, sloganInput;
    private Button signUpButton;
    private TextView alreadyHaveAccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_candidate_signup);


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
                    // Proceed with sign-up logic (e.g., API call or database entry)
                    // Here we'll show a success message as a placeholder
                    Toast.makeText(CandidateSignup.this, "Sign-up successful", Toast.LENGTH_SHORT).show();

                    // After successful sign-up, navigate to login or dashboard
                    NavigationService.navigateToActivity(CandidateSignup.this, CandidateSuccessfullyRegistered.class);
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
}