package com.example.mec.admin;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.mec.R;
import com.example.mec.services.NavigationService;

public class AdminHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_home_page);
        AppCompatButton loginButton = findViewById(R.id.loginButton);
        AppCompatButton signupButton = findViewById(R.id.signupButton);

        // Set onClickListener for login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to AdminLogin
                NavigationService.navigateToActivity(AdminHome.this, AdminSignin.class);
            }
        });

        // Set onClickListener for signup button
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to SignUpActivity
                //NavigationService.navigateToActivity(AdminHome.this, SignUpActivity.class);
            }
        });
    }
}