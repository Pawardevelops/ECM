package com.example.mec;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mec.services.NavigationService;

public class CandidateSuccessfullyRegistered extends AppCompatActivity {

    private ImageView successImageView;
    private TextView welcomeMessage;
    private Button startButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_candidate_successfully_registered);

        successImageView = findViewById(R.id.imageView);
        welcomeMessage = findViewById(R.id.welcome_candidate);
        startButton = findViewById(R.id.start);

        // Set up "Start" button click listener
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the candidate dashboard or main app activity
                NavigationService.navigateToActivity(CandidateSuccessfullyRegistered.this, candidateDashboard.class);
            }
        });
    }
}