package com.example.mec.candidate;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mec.R;
import com.example.mec.services.NavigationService;

public class CandidateHomePage extends AppCompatActivity {

    private Button candidateLoginButton;
    private Button candidateSignUpButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_candidate_home_page);
        candidateLoginButton = findViewById(R.id.candidateLogin);
        candidateSignUpButton = findViewById(R.id.candidateSignUp);

        // Handle the candidate login button click
        candidateLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the actual Candidate Login Activity (or the appropriate page)
                NavigationService.navigateToActivity(CandidateHomePage.this, candidateLogin.class);
            }
        });

        // Handle the candidate sign-up button click
        candidateSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the candidate signup activity
                NavigationService.navigateToActivity(CandidateHomePage.this, CandidateSignup.class);
            }
        });
    }
}