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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class VotersDashboard extends AppCompatActivity {

    private LinearLayout profileButton, resultsButton, candidatesButton, helpButton;
    private LinearLayout electionButtonsContainer;  // Dynamic container for buttons
    private DatabaseReference databaseReference;
    private ShimmerFrameLayout shimmerFrameLayout, shimmerFrameLayout1, shimmerFrameLayout2;

    // Firebase Authentication
    private FirebaseAuth mAuth;
    private String voterUid;
    private String voterSection;
    private String voterDepartment;
    private String voterCourse;

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
        shimmerFrameLayout1 = findViewById(R.id.shimmer_view_container1);
        shimmerFrameLayout2 = findViewById(R.id.shimmer_view_container2);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Get current logged-in voter ID (UID)
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            voterUid = currentUser.getUid();
        } else {
            // No user is signed in, redirect to login or show a message
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Firebase Database reference for elections and voter data
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Fetch voter profile data using the UID and then display relevant elections
        fetchVoterProfileAndDisplayElections();

        // Set click listeners for static buttons
        setStaticButtonListeners();
    }

    // Method to set listeners for static buttons
    private void setStaticButtonListeners() {
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
    }

    // Method to fetch voter profile data and then display elections
    private void fetchVoterProfileAndDisplayElections() {
        // Show shimmer layout and hide button container while loading
        shimmerFrameLayout.startShimmer(); // Start shimmer animation
        shimmerFrameLayout1.startShimmer();
        shimmerFrameLayout2.startShimmer();

        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout1.setVisibility(View.VISIBLE);
        shimmerFrameLayout2.setVisibility(View.VISIBLE);
        electionButtonsContainer.setVisibility(View.GONE);

        // Reference to the voter's data in Firebase Realtime Database (assuming voters are stored under "voters" node)
        DatabaseReference voterRef = databaseReference.child("voters").child(voterUid);

        voterRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Fetch voter's section, department, and course from the database
                    voterSection = dataSnapshot.child("section").getValue(String.class);
                    voterDepartment = dataSnapshot.child("department").getValue(String.class);
                    voterCourse = dataSnapshot.child("course").getValue(String.class);

                    // If all required data is available, fetch and display elections
                    if (voterSection != null && voterDepartment != null && voterCourse != null) {
                        fetchAndDisplayElectionsForVoter();
                    } else {
                        stopShimmerAndShowError("Incomplete voter data.");
                    }
                } else {
                    stopShimmerAndShowError("Voter data not found.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                stopShimmerAndShowError("Failed to fetch voter data.");
            }
        });
    }

    // Method to fetch elections based on voter's section, department, and course
    private void fetchAndDisplayElectionsForVoter() {
        // Query to get elections matching voter's section, department, and course
        DatabaseReference electionsRef = databaseReference.child("elections");
        electionsRef.orderByChild("section").equalTo(voterSection)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        electionButtonsContainer.removeAllViews();  // Clear previous views if any

                        for (DataSnapshot electionSnapshot : dataSnapshot.getChildren()) {
                            // Extract election data
                            String electionId = electionSnapshot.getKey();  // Assuming the election ID is the key
                            String electionTitle = electionSnapshot.child("title").getValue(String.class);
                            String section = electionSnapshot.child("section").getValue(String.class);
                            String department = electionSnapshot.child("department").getValue(String.class);
                            String course = electionSnapshot.child("course").getValue(String.class);

                            // Only add elections that match voter's department and course
                            if (voterDepartment.equals(department)) {
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
                                    // Create an intent to pass section, department, and election ID to voterCandidateSelection activity
                                    Intent intent = new Intent(VotersDashboard.this, voterCandidateSelection.class);
                                    intent.putExtra("SECTION", section);         // Pass section
                                    intent.putExtra("DEPARTMENT", department);   // Pass department
                                    intent.putExtra("ELECTION_ID", electionId);  // Pass election ID
                                    startActivity(intent);
                                });

                                // Add the button to the container
                                electionButtonsContainer.addView(electionButton);
                            }
                        }

                        // Hide shimmer and show buttons after loading is complete
                        stopShimmerAndShowButtons();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle possible errors
                        stopShimmerAndShowError("Failed to load elections.");
                    }
                });
    }

    // Helper method to stop shimmer and show an error message
    private void stopShimmerAndShowError(String errorMessage) {
        // Stop shimmer animation
        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout1.stopShimmer();
        shimmerFrameLayout2.stopShimmer();

        // Hide shimmer and show error message
        shimmerFrameLayout.setVisibility(View.GONE);
        shimmerFrameLayout1.setVisibility(View.GONE);
        shimmerFrameLayout2.setVisibility(View.GONE);

        Toast.makeText(VotersDashboard.this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    // Helper method to stop shimmer and show buttons
    private void stopShimmerAndShowButtons() {
        // Stop shimmer animation
        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout1.stopShimmer();
        shimmerFrameLayout2.stopShimmer();

        // Hide shimmer and show election buttons
        shimmerFrameLayout.setVisibility(View.GONE);
        shimmerFrameLayout1.setVisibility(View.GONE);
        shimmerFrameLayout2.setVisibility(View.GONE);
        electionButtonsContainer.setVisibility(View.VISIBLE);
    }
}
