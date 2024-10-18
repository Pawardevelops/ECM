package com.example.mec.admin;

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
import com.example.mec.voters.voters_all_candidates;
import com.example.mec.voters.voters_section_menu_result;

public class admindashboard extends AppCompatActivity {
    private LinearLayout profileButton, resultsButton, candidatesButton, helpButton;
    private AppCompatButton campaignManagementButton, supporterInteractionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admindashboard);

        profileButton = findViewById(R.id.profileButton);
        resultsButton = findViewById(R.id.resultsButton);
        candidatesButton = findViewById(R.id.candidatesButton);
        helpButton = findViewById(R.id.helpButton);
        campaignManagementButton = findViewById(R.id.campaignManagementButton);
        supporterInteractionButton = findViewById(R.id.supporterInteractionButton);

        // Set click listeners and intents for navigation
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(admindashboard.this, "Profile page", Toast.LENGTH_SHORT).show();
//                NavigationService.navigateToActivity(FAQActivity.this,);
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

                NavigationService.navigateToActivity(admindashboard.this, Help.class);
            }
        });

        campaignManagementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(admindashboard.this, "campaignManagementButton page", Toast.LENGTH_SHORT).show();

                //NavigationService.navigateToCampaignManagement(MainActivity.this);
            }
        });

        supporterInteractionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(admindashboard.this, "campaignManagementButton page", Toast.LENGTH_SHORT).show();

                //NavigationService.navigateToSupporterInteraction(MainActivity.this);
            }
        });

    }
}