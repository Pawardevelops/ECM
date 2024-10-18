package com.example.mec.voters;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.mec.R;
import com.example.mec.services.NavigationService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class voters_login extends AppCompatActivity {
    private EditText emailEditText, passwordEditText;
    private AppCompatButton loginButton;
    private TextView forgotPasswordTextView, signupTextView;

    // Firebase Authentication instance
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_voters_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

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
                    // Perform login authentication using Firebase
                    loginUser(email, password);
                }
            }
        });

        // Set click listener for "Forgot Password"
        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(voters_login.this, "Forget password clicked", Toast.LENGTH_SHORT).show();
                // Implement forgot password functionality or navigation here
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
                    if (task.isSuccessful()) {
                        // Login success
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Redirect to VotersDashboard after successful login
                            NavigationService.navigateToActivity(voters_login.this, VotersDashboard.class);
                        }
                    } else {
                        // If login fails, display a message to the user.
                        Toast.makeText(voters_login.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
