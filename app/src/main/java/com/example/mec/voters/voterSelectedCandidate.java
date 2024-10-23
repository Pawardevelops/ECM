package com.example.mec.voters;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.mec.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;

// other imports...

public class voterSelectedCandidate extends AppCompatActivity {

    // Define views for UI components
    private ImageView profileImageView;
    private TextView candidateNameTextView;
    private TextView registrationNumberTextView;
    private TextView sloganTextView;
    private TextView phoneNumberTextView;
    private TextView departmentTextView;
    private TextView sectionTextView;
    private TextView courseTextView;
    private TextView semesterTextView;
    private Button voteBtn;
    private LinearLayout chipContainer; // New linear layout to hold chips

    private FirebaseAuth mAuth;
    private DatabaseReference votesRef;
    private DatabaseReference candidatesRef;
    private DatabaseReference electionVotesRef;
    private DatabaseReference electionsRef; // New reference for elections

    private String electionId; // Store election ID for later use
    private boolean hasVoted; // Track if the user has already voted
    private String electionStatus; // Track the status of the election

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voter_selected_candidate);

        // Initialize views
        profileImageView = findViewById(R.id.profile_image);
        candidateNameTextView = findViewById(R.id.candidate_username);
        registrationNumberTextView = findViewById(R.id.candidate_registration_number);
        sloganTextView = findViewById(R.id.slogan);
        phoneNumberTextView = findViewById(R.id.phone_number);
        departmentTextView = findViewById(R.id.department_value);
        sectionTextView = findViewById(R.id.section_value);
        courseTextView = findViewById(R.id.course_value);
        semesterTextView = findViewById(R.id.sem_value);
        voteBtn = findViewById(R.id.voteButton);
        chipContainer = findViewById(R.id.chip); // Initialize chip container

        // Firebase initialization
        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();  // Logged in voter ID
        candidatesRef = FirebaseDatabase.getInstance().getReference("candidates");
        votesRef = FirebaseDatabase.getInstance().getReference("votes");
        electionsRef = FirebaseDatabase.getInstance().getReference("elections"); // Initialize elections reference

        // Get candidate ID and election ID from the intent extras
        String candidateId = getIntent().getStringExtra("CANDIDATE_UID");
        electionId = getIntent().getStringExtra("ELECTION_ID");

        if (candidateId != null && electionId != null) {
            // Fetch candidate data
            fetchCandidateData(candidateId);
            // Check election status
            fetchElectionStatus(electionId);
            // Set up vote button click listener
            voteBtn.setOnClickListener(view -> {
                // Check if the user has already voted
                checkIfAlreadyVoted(userId, electionId, candidateId);
            });
        } else {
            Log.e("Firebase", "Candidate ID or Election ID is null");
        }
    }

    // Method to fetch candidate data from Firebase using the candidate ID
    private void fetchCandidateData(String candidateId) {
        candidatesRef.child(candidateId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Extract candidate data from snapshot
                    String candidateName = dataSnapshot.child("firstName").getValue(String.class);
                    String registrationNumber = dataSnapshot.child("registrationNo").getValue(String.class);
                    String slogan = dataSnapshot.child("slogan").getValue(String.class);
                    String phoneNumber = dataSnapshot.child("email").getValue(String.class);
                    String department = dataSnapshot.child("department").getValue(String.class);
                    String section = dataSnapshot.child("section").getValue(String.class);
                    String course = dataSnapshot.child("course").getValue(String.class);
                    String semester = dataSnapshot.child("semester").getValue(String.class);
                    String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);

                    // Update the UI with the fetched data
                    candidateNameTextView.setText(candidateName);
                    registrationNumberTextView.setText(registrationNumber);
                    sloganTextView.setText(slogan);
                    phoneNumberTextView.setText(phoneNumber);
                    departmentTextView.setText(department);
                    sectionTextView.setText(section);
                    courseTextView.setText(course);
                    semesterTextView.setText(semester);

                    // Load the image using Glide
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Glide.with(voterSelectedCandidate.this)
                                .load(imageUrl)
                                .placeholder(R.drawable.profile) // Placeholder image in case loading fails
                                .error(R.drawable.profile)      // Image to display on error
                                .into(profileImageView);
                    } else {
                        profileImageView.setImageResource(R.drawable.profile); // Default image if no URL
                    }
                } else {
                    Log.e("Firebase", "Candidate not found for ID: " + candidateId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error fetching data: " + databaseError.getMessage());
            }
        });
    }

    // Method to fetch election status from Firebase
    private void fetchElectionStatus(String electionId) {
        electionsRef.child(electionId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    electionStatus = dataSnapshot.child("status").getValue(String.class);
                    updateElectionStatusChips();
                } else {
                    Log.e("Firebase", "Election not found for ID: " + electionId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error fetching election status: " + databaseError.getMessage());
            }
        });
    }

    // Method to update UI with election status chips
    private void updateElectionStatusChips() {
        if ("not_started".equals(electionStatus)) {
            addChip("Election Not Started", Color.GRAY);
            voteBtn.setEnabled(false); // Disable voting
        } else if ("started".equals(electionStatus)) {
            addChip("Election Started", Color.GREEN);
        } else if ("completed".equals(electionStatus)) {
            addChip("Election Completed", Color.RED);
            voteBtn.setEnabled(false); // Disable voting
        }
    }

    // Method to add chip to the UI
    private void addChip(String label, int color) {
        TextView chip = new TextView(this);
        chip.setText(label);
        chip.setBackgroundColor(color);
        chip.setTextColor(Color.WHITE);
        chip.setPadding(16, 8, 16, 8);
        chip.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        chipContainer.addView(chip);
    }

    // Method to check if the user has already voted in the election
    private void checkIfAlreadyVoted(String userId, String electionId, String candidateId) {
        // Check in the votes collection
        votesRef.child(electionId).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hasVoted = dataSnapshot.exists(); // Set hasVoted based on the result
                if (hasVoted) {
                    // The user has already voted
                    addChip("You Have Already Voted", Color.RED);
                    voteBtn.setEnabled(false); // Disable voting
                } else {
                    // The user has not voted yet, show confirmation dialog if status allows
                    if (!"not_started".equals(electionStatus)) {
                        showVoteConfirmationDialog(userId, candidateId, electionId);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error checking vote status: " + databaseError.getMessage());
            }
        });
    }

    // Method to show vote confirmation dialog
    private void showVoteConfirmationDialog(String userId, String candidateId, String electionId) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Vote")
                .setMessage("Are you sure you want to vote for this candidate?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Save the vote to Firebase
                    saveVote(userId, candidateId, electionId);
                })
                .setNegativeButton("No", null)
                .show();
    }

    // Method to save vote to Firebase
    // Method to save vote to Firebase and increment candidate's vote count
    private void saveVote(String userId, String candidateId, String electionId) {
        // Save the vote
        votesRef.child(electionId).child(userId).setValue(candidateId)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Increment the candidate's vote count atomically
                        candidatesRef.child(candidateId).child("voteCount").runTransaction(new Transaction.Handler() {
                            @NonNull
                            @Override
                            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                Integer currentVotes = mutableData.getValue(Integer.class);
                                if (currentVotes == null) {
                                    mutableData.setValue(1);
                                } else {
                                    mutableData.setValue(currentVotes + 1);
                                }
                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, boolean committed, @Nullable DataSnapshot dataSnapshot) {
                                if (committed) {
                                    Toast.makeText(voterSelectedCandidate.this, "Vote cast successfully!", Toast.LENGTH_SHORT).show();
                                    finish(); // Close the activity after voting
                                } else {
                                    Toast.makeText(voterSelectedCandidate.this, "Error updating vote count.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(voterSelectedCandidate.this, "Error casting vote.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
