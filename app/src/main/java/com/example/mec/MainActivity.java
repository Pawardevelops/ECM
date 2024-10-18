package com.example.mec;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.mec.admin.AdminHome;
import com.example.mec.candidate.CandidateHomePage;
import com.example.mec.candidate.candidateLogin;
import com.example.mec.services.NavigationService;
import com.example.mec.voters.voters_login;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        AppCompatButton adminButton = findViewById(R.id.enterAdminBttn);
        AppCompatButton candidateButton = findViewById(R.id.enterCandidateBttn);
        AppCompatButton voterButton = findViewById(R.id.enterVoterBttn);

        // Set onClickListeners for each button
        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to AdminLoginActivity
                NavigationService.navigateToActivity(MainActivity.this, AdminHome.class);

            }
        });

        candidateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to CandidateLoginActivity
                NavigationService.navigateToActivity(MainActivity.this, CandidateHomePage.class);

            }
        });

        voterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to VoterLoginActivity
                NavigationService.navigateToActivity(MainActivity.this, voters_login.class);

            }
        });
    }
}