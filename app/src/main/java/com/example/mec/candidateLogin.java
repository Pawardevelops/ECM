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

public class candidateLogin extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginButton;
    private TextView forgotPassword, signUpLink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_candidate_login);


        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.start);
        forgotPassword = findViewById(R.id.forget_candidate_password);
        signUpLink = findViewById(R.id.candidate_login);

        // Handle the login button click
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate input and proceed with login logic (you would typically check email and password)
                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();

                if (validateLogin(email, password)) {
                    // Navigate to Candidate Dashboard Activity upon successful login
                    NavigationService.navigateToActivity(candidateLogin.this, candidateDashboard.class);
                } else {
                    // Show error or retry logic (this is just a placeholder)
                    emailInput.setError("Invalid email or password");
                }
            }
        });

        // Handle forgot password text click
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Forgot Password Activity
                Toast.makeText(candidateLogin.this, "forgot password logic here", Toast.LENGTH_SHORT).show();
                //NavigationService.navigateToActivity(candidateLogin.this, ForgotPasswordActivity.class);
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

    // Placeholder method for login validation
    private boolean validateLogin(String email, String password) {
        // Basic validation (you would add more robust validation here, like regex or backend checks)
        return !email.isEmpty() && !password.isEmpty();
    }
}