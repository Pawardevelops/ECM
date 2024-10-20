package com.example.mec;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.mec.admin.AdminHome;
import com.example.mec.admin.admindashboard;
import com.example.mec.candidate.CandidateHomePage;
import com.example.mec.candidate.candidateDashboard;
import com.example.mec.services.NavigationService;
import com.example.mec.services.SharedPreferenceHelper;
import com.example.mec.voters.VotersDashboard;
import com.example.mec.voters.voters_login;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Check if the user is already logged in
        boolean isLoggedIn = SharedPreferenceHelper.isLoggedIn(this);
        String userRole = SharedPreferenceHelper.getUserRole(this);

        if (isLoggedIn) {
            // User is logged in, redirect to appropriate dashboard based on the role
            navigateToDashboard(userRole);
        }

        // Display the login options if not logged in
        AppCompatButton adminButton = findViewById(R.id.enterAdminBttn);
        AppCompatButton candidateButton = findViewById(R.id.enterCandidateBttn);
        AppCompatButton voterButton = findViewById(R.id.enterVoterBttn);

        // Set onClickListeners for each button
        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationService.navigateToActivity(MainActivity.this, AdminHome.class);
            }
        });

        candidateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationService.navigateToActivity(MainActivity.this, CandidateHomePage.class);
            }
        });

        voterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationService.navigateToActivity(MainActivity.this, voters_login.class);
            }
        });
    }

    private void navigateToDashboard(String role) {
        Intent intent;

        switch (role) {
            case "admin":
                intent = new Intent(MainActivity.this, admindashboard.class);
                break;

            case "candidate":
                intent = new Intent(MainActivity.this, candidateDashboard.class);
                break;

            case "voter":
                intent = new Intent(MainActivity.this, VotersDashboard.class);
                break;

            default:
                return;
        }

        startActivity(intent);
        finish(); // Close MainActivity so the user can't navigate back
    }
}
