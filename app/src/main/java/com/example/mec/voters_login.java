package com.example.mec;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mec.services.NavigationService;

public class voters_login extends AppCompatActivity {
    private EditText emailEditText, passwordEditText;
    private AppCompatButton loginButton;
    private TextView forgotPasswordTextView, signupTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_voters_login);

        emailEditText = findViewById(R.id.emailEditText);  // Assuming the EditText IDs are appropriately named in the XML
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.start);
        forgotPasswordTextView = findViewById(R.id.forget_candidate_password);
        signupTextView = findViewById(R.id.candidate_login);

        // Set click listener for login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(voters_login.this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Perform login authentication (pseudo-authentication for now)
                    if (authenticateUser(email, password)) {
                        NavigationService.navigateToActivity(voters_login.this,VotersDashboard.class);


                    } else {
                        Toast.makeText(voters_login.this, "Invalid login credentials", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Set click listener for "Forgot Password"
        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(voters_login.this, "forget password", Toast.LENGTH_SHORT).show();

                //NavigationService.navigateToActivity(voters_login.this,VotersDashboard.class);

            }
        });

        // Set click listener for "Sign Up"
        signupTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationService.navigateToActivity(voters_login.this,voters_signup.class);
            }
        });
    }

    // Mock authentication method (replace this with real authentication logic)
    private boolean authenticateUser(String email, String password) {
        // TODO: Implement real authentication logic here
        return email.equals("voter@example.com") && password.equals("password123");  // Example hardcoded credentials
    }
}