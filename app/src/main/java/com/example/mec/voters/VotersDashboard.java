package com.example.mec.voters;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.mec.Help;
import com.example.mec.R;
import com.example.mec.services.NavigationService;

public class VotersDashboard extends AppCompatActivity {

    private LinearLayout profileButton, resultsButton, candidatesButton, helpButton;
    private AppCompatButton campaignManagementButton, supporterInteractionButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_voters_dashboard);

        profileButton = findViewById(R.id.profileButton);
        resultsButton = findViewById(R.id.resultsButton);
        candidatesButton = findViewById(R.id.candidatesButton);
        helpButton = findViewById(R.id.helpButton);
        campaignManagementButton = findViewById(R.id.campaignManagementButton);
        supporterInteractionButton = findViewById(R.id.supporterInteractionButton);

        // Set click listeners for each button to navigate to respective activities
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(VotersDashboard.this, "Profile page", Toast.LENGTH_SHORT).show();
                //NavigationService.navigateToActivity(VotersDashboard.this,voters_login.class);
            }
        });

        resultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationService.navigateToActivity(VotersDashboard.this,voters_section_menu_result.class);
            }
        });

        candidatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NavigationService.navigateToActivity(VotersDashboard.this,voters_all_candidates.class);

            }
        });

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NavigationService.navigateToActivity(VotersDashboard.this, Help.class);

            }
        });

        campaignManagementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(VotersDashboard.this, "campaignManagementButton page", Toast.LENGTH_SHORT).show();

                //NavigationService.navigateToActivity(VotersDashboard.this,voters_all_candidates.class);

            }
        });

        supporterInteractionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(VotersDashboard.this, "supporterInteractionButton page", Toast.LENGTH_SHORT).show();

                //NavigationService.navigateToActivity(VotersDashboard.this,voters_all_candidates.class);
            }
        });
    }
}