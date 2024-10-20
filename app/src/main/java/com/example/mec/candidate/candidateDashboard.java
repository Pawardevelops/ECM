package com.example.mec.candidate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mec.Help;
import com.example.mec.ProfileActivity;
import com.example.mec.R;
import com.example.mec.voters.voters_allCandidate_result;
import com.example.mec.voters.voters_all_candidates;

public class candidateDashboard extends AppCompatActivity {


    private Button campaignManagementButton;
    private Button supporterInteractionButton;
    LinearLayout profileButton,resultsButton, candidatesButton, helpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_candidate_dashboard);

        // Initialize buttons
        profileButton = findViewById(R.id.profileButton);
        resultsButton = findViewById(R.id.resultsButton);
        candidatesButton = findViewById(R.id.candidatesButton);
        helpButton = findViewById(R.id.helpButton);
        campaignManagementButton = findViewById(R.id.campaignManagementButton);
        supporterInteractionButton = findViewById(R.id.supporterInteractionButton);

        // Set click listeners for each button
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(candidateDashboard.this, ProfileActivity.class);
                intent.putExtra("db", "candidates"); // Send "candidates" as the value for the "db" key
                startActivity(intent);
            }
        });

        resultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(candidateDashboard.this, voters_allCandidate_result.class);
                startActivity(intent);
            }
        });

        candidatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(candidateDashboard.this, voters_all_candidates.class);
                startActivity(intent);
            }
        });

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(candidateDashboard.this, Help.class);
                startActivity(intent);
            }
        });

        campaignManagementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(candidateDashboard.this, CampaignManagementActivity.class);
//                startActivity(intent);
                Toast.makeText(candidateDashboard.this,"test",Toast.LENGTH_LONG).show();
            }
        });

        supporterInteractionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(candidateDashboard.this, SupporterInteractionActivity.class);
//                startActivity(intent);
                Toast.makeText(candidateDashboard.this,"test",Toast.LENGTH_LONG).show();

            }
        });
    }
}
