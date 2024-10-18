package com.example.mec.admin;

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

public class AdminSignin extends AppCompatActivity {

    private EditText emailInput;
    private EditText passwordInput;
    private Button loginButton;
    private TextView forgotPasswordText;
    private TextView signupText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_signin);


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
                // Navigate to forgot password activity
                Toast.makeText(AdminSignin.this, "ForgetPassoword Logic here", Toast.LENGTH_SHORT).show();
                //NavigationService.navigateToActivity(AdminSignin.this, ForgotPasswordActivity.class);
            }
        });

        // Handle sign up text click
        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to signup activity
                Toast.makeText(AdminSignin.this, "SignUp Logic here", Toast.LENGTH_SHORT).show();

                //NavigationService.navigateToActivity(AdminSignin.this, SignUpActivity.class);
            }
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

        // Authenticate the user (this is just a placeholder, implement your own authentication logic)
        if (email.equals("admin@example.com") && password.equals("admin123")) {
            // If login successful, navigate to the Admin dashboard
            NavigationService.navigateToActivity(AdminSignin.this, admindashboard.class);
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
        } else {
            // Show error if credentials are incorrect
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }
}