package com.example.mec.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mec.R;
import com.example.mec.forgotPassword;
import com.example.mec.services.NavigationService;
import com.example.mec.services.SharedPreferenceHelper;
import com.example.mec.voters.voterSelectedCandidate;
import com.example.mec.voters.voters_login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminSignin extends AppCompatActivity {

    private EditText emailInput;
    private EditText passwordInput;
    private Button loginButton;
    private TextView forgotPasswordText;
    private TextView signupText;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_signin);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("admins");

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        forgotPasswordText = findViewById(R.id.forgotPassword);
        signupText = findViewById(R.id.signupText);

        // Handle login button click
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleLogin();
            }
        });

        // Handle forgot password click
        forgotPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailInput.getText().toString().trim();
                if (email.isEmpty()) {
                    Toast.makeText(AdminSignin.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                } else {
                    sendPasswordResetEmail(email);
                }
            }
        });

        // Handle sign up text click
        signupText.setOnClickListener(view -> {
            // Navigate to the AdminSignup activity
            Intent intent = new Intent(AdminSignin.this, adminSignup.class); // Replace AdminSignup with the actual signup activity class name
            startActivity(intent);
        });
    }

    // Method to handle login action
    private void handleLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Sign in the user with Firebase Authentication
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Get the authenticated user
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // Check if the user has an admin role in the database

                    checkAdminRole(user.getUid());
                }
            } else {
                // Show error if authentication fails
                Toast.makeText(AdminSignin.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to check if the user has an Admin role
    private void checkAdminRole(String userId) {
        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve the user role from the database
                    String role = dataSnapshot.child("role").getValue(String.class);
                    if (true) {
                        // Navigate to the Admin dashboard if the role is Admin
                        // For example, when an admin logs in:
                        SharedPreferenceHelper.saveLoginInfo(AdminSignin.this, true, "admin");

                        NavigationService.navigateToActivityAfterLogin(AdminSignin.this, admindashboard.class);

                        Toast.makeText(AdminSignin.this, "Login successful!", Toast.LENGTH_SHORT).show();

                    } else {
                        // Show error if the user is not an Admin
                        Toast.makeText(AdminSignin.this, "You are not authorized as Admin", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AdminSignin.this, "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
                Toast.makeText(AdminSignin.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void sendPasswordResetEmail(String email) {
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AdminSignin.this, "Password reset email sent.", Toast.LENGTH_SHORT).show();
                        // Navigate to the forgot password activity if needed
                        NavigationService.navigateToActivity(AdminSignin.this, forgotPassword.class);
                    } else {
                        Toast.makeText(AdminSignin.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
