package com.example.mec.voters;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.mec.Help;
import com.example.mec.ProfileActivity;
import com.example.mec.R;
import com.example.mec.services.NavigationService;
import com.example.mec.voters.voterCandidateSelection;
import com.example.mec.voters.voters_all_candidates;
import com.example.mec.voters.voters_section_menu_result;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class VotersDashboard extends AppCompatActivity {

    private LinearLayout profileButton, resultsButton, candidatesButton, helpButton;
    private LinearLayout electionButtonsContainer;  // Dynamic container for buttons
    private DatabaseReference databaseReference;
    private ShimmerFrameLayout shimmerFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voters_dashboard);

        profileButton = findViewById(R.id.profileButton);
        resultsButton = findViewById(R.id.resultsButton);
        candidatesButton = findViewById(R.id.candidatesButton);
        helpButton = findViewById(R.id.helpButton);
        electionButtonsContainer = findViewById(R.id.electionButtonsContainer);  // Container for dynamic buttons
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);

        // Initialize Firebase Database reference to get the elections node
        databaseReference = FirebaseDatabase.getInstance().getReference("elections");

        // Set click listeners for other static buttons
        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(VotersDashboard.this, ProfileActivity.class);
            intent.putExtra("db", "voters");
            startActivity(intent);
        });

        resultsButton.setOnClickListener(v -> {
            NavigationService.navigateToActivity(VotersDashboard.this, voters_section_menu_result.class);
        });

        candidatesButton.setOnClickListener(v -> {
            NavigationService.navigateToActivity(VotersDashboard.this, voters_all_candidates.class);
        });

        helpButton.setOnClickListener(v -> {
            NavigationService.navigateToActivity(VotersDashboard.this, Help.class);
        });

        // Fetch elections from Firebase and dynamically add buttons
        fetchAndDisplayElections();
    }

    // Method to fetch elections and display them as buttons dynamically
    private void fetchAndDisplayElections() {
        // Show shimmer layout and hide button container while loading
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        electionButtonsContainer.setVisibility(View.GONE);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                electionButtonsContainer.removeAllViews();  // Clear previous views if any

                for (DataSnapshot electionSnapshot : dataSnapshot.getChildren()) {
                    // Extract election data
                    String electionTitle = electionSnapshot.child("title").getValue(String.class);
                    String section = electionSnapshot.child("section").getValue(String.class);

                    // Dynamically create buttons for each election using a styled context
                    ContextThemeWrapper newContext = new ContextThemeWrapper(VotersDashboard.this, R.style.SolidButton);
                    AppCompatButton electionButton = new AppCompatButton(newContext);

                    // Set button text and apply background if needed
                    electionButton.setText(electionTitle + " : " + section + " Election");
                    electionButton.setBackgroundResource(R.drawable.solidbutton); // If you have a background drawable for the button

                    // Set margin for the button
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,  // Width
                            LinearLayout.LayoutParams.WRAP_CONTENT   // Height
                    );
                    params.setMargins(16, 16, 16, 16);  // Set left, top, right, bottom margins (in pixels)
                    electionButton.setLayoutParams(params);

                    // Set click listener for each button
                    electionButton.setOnClickListener(v -> {
                        Intent intent = new Intent(VotersDashboard.this, voterCandidateSelection.class);
                        intent.putExtra("SECTION", section);  // Pass the section from the database
                        startActivity(intent);
                    });

                    // Add the button to the container
                    electionButtonsContainer.addView(electionButton);
                }

                // Hide shimmer and show buttons after loading is complete
                shimmerFrameLayout.setVisibility(View.GONE);
                electionButtonsContainer.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
                shimmerFrameLayout.setVisibility(View.GONE);
                Toast.makeText(VotersDashboard.this, "Failed to load elections.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
