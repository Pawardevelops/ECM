package com.example.mec.voters;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mec.R;
import com.example.mec.services.NavigationService;

public class voter_registered_successfully extends AppCompatActivity {
    private ImageView imageView;
    private TextView successMessage, infoText;
    private Button startButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_voter_registered_successfully);

        imageView = findViewById(R.id.imageView);
        successMessage = findViewById(R.id.welcome_candidate);
        infoText = findViewById(R.id.ECM_TEXT);
        startButton = findViewById(R.id.start);

        // Set button click listener
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the next activity (e.g., Voter Dashboard)
                NavigationService.navigateToActivity(voter_registered_successfully.this,voters_login.class);

            }
        });
    }
}