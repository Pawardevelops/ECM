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

public class voted_successfully extends AppCompatActivity {
    private TextView titleTextView, subtitleTextView, voteStatusTextView;
    private ImageView ballotBoxImageView;
    private Button doneButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_voted_successfully);

        titleTextView = findViewById(R.id.tv_title);
        subtitleTextView = findViewById(R.id.tv_subtitle);
        voteStatusTextView = findViewById(R.id.tv_voted_successfully);
        ballotBoxImageView = findViewById(R.id.iv_ballot_box);
        doneButton = findViewById(R.id.btn_done);

        // Set "Done" button click listener
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the main dashboard or exit the voting activity
                NavigationService.navigateToActivity(voted_successfully.this,VotersDashboard.class);
            }
        });
    }
}