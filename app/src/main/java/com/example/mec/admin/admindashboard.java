package com.example.mec.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.mec.Help;
import com.example.mec.ProfileActivity;
import com.example.mec.R;
import com.example.mec.services.NavigationService;
import com.example.mec.voters.VotersDashboard;
import com.example.mec.voters.voters_all_candidates;
import com.example.mec.voters.voters_section_menu_result;

public class admindashboard extends AppCompatActivity {
    private LinearLayout profileButton, resultsButton, candidatesButton, helpButton;
    private AppCompatButton candidatesForApproval, createElection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admindashboard);

        profileButton = findViewById(R.id.profileButton);
        resultsButton = findViewById(R.id.resultsButton);
        candidatesButton = findViewById(R.id.candidatesButton);
        helpButton = findViewById(R.id.helpButton);
        candidatesForApproval = findViewById(R.id.candidatesForApproval);
        createElection = findViewById(R.id.electionCreate);

        // Set click listeners and intents for navigation
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                NavigationService.navigateToActivity(FAQActivity.this,);
                Intent intent = new Intent(admindashboard.this, adminProfile.class);
                intent.putExtra("db", "admins"); // Send "candidates" as the value for the "db" key
                startActivity(intent);
            }
        });

        resultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationService.navigateToActivity(admindashboard.this, voters_section_menu_result.class);
            }
        });

        candidatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationService.navigateToActivity(admindashboard.this, voters_all_candidates.class);
            }
        });

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NavigationService.navigateToActivity(admindashboard.this, adminElectionList.class);
            }
        });

        candidatesForApproval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationService.navigateToActivity(admindashboard.this, adminCandidateApproval.class);

                //NavigationService.navigateToCampaignManagement(MainActivity.this);
            }
        });

        createElection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NavigationService.navigateToActivity(admindashboard.this, adminElections.class);
            }
        });

    }
}