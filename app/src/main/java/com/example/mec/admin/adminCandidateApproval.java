package com.example.mec.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.mec.R;
import com.example.mec.services.Candidate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class adminCandidateApproval extends AppCompatActivity {
    private LinearLayout candidatesContainer; // This will hold the candidates
    private DatabaseReference candidatesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_candidate_approval);

        candidatesContainer = findViewById(R.id.candidates_container); // Make sure this ID is in your activity layout

        // Initialize Firebase Database reference
        candidatesRef = FirebaseDatabase.getInstance().getReference("candidates");

        // Fetch and display candidates
        fetchCandidates();
    }

    private void fetchCandidates() {
        candidatesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                candidatesContainer.removeAllViews(); // Clear existing views

                List<Candidate> pendingCandidates = new ArrayList<>();
                List<Candidate> approvedCandidates = new ArrayList<>();
                List<Candidate> canceledCandidates = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Candidate candidate = snapshot.getValue(Candidate.class);
                    if (candidate != null) {
                        candidate.setUid(snapshot.getKey()); // Set UID from the snapshot key
                        switch (candidate.getStatus()) {
                            case "Pending":
                                pendingCandidates.add(candidate);
                                break;
                            case "approved":
                                approvedCandidates.add(candidate);
                                break;
                            case "canceled":
                                canceledCandidates.add(candidate);
                                break;
                        }
                    }
                }

                // Display each group of candidates
                displayCandidates(pendingCandidates, "Pending Candidates");
                displayCandidates(approvedCandidates, "Approved Candidates");
                displayCandidates(canceledCandidates, "Cancelled Candidates");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(adminCandidateApproval.this, "Failed to load candidates: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayCandidates(List<Candidate> candidates, String title) {
        if (!candidates.isEmpty()) {
            // Add a title for the group
            TextView groupTitle = new TextView(this);
            groupTitle.setText(title);
            groupTitle.setTextSize(18);
            groupTitle.setTextColor(getResources().getColor(R.color.primary)); // Change this to your color resource
            candidatesContainer.addView(groupTitle);

            for (Candidate candidate : candidates) {
                addCandidateToLayout(candidate);
            }
        }
    }

    private void addCandidateToLayout(Candidate candidate) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View candidateView = inflater.inflate(R.layout.candidate_items, candidatesContainer, false);

        TextView candidateName = candidateView.findViewById(R.id.candidate_name);
        TextView candidateSection = candidateView.findViewById(R.id.candidate_section);
        ImageView candidateImage = candidateView.findViewById(R.id.candidate_image);
        LinearLayout chipContainer = candidateView.findViewById(R.id.chip); // The container for the chip

        // Set the candidate's name and section
        candidateName.setText(candidate.getFirstName() + " " + candidate.getLastName());
        candidateSection.setText(candidate.getSection());

        // Load the candidate image using Glide
        String imageUrl = candidate.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.profile)
                    .error(R.drawable.profile)
                    .into(candidateImage);
        } else {
            candidateImage.setImageResource(R.drawable.profile);
        }

        // Add a Chip dynamically to show the candidate status
        com.google.android.material.chip.Chip statusChip = new com.google.android.material.chip.Chip(this);
        statusChip.setText(candidate.getStatus());  // Set the text based on candidate status
        statusChip.setTextColor(getResources().getColor(android.R.color.white)); // Set text color to white
        // Set the Chip's background color based on the candidate's status
        statusChip.setChipStrokeWidth(0);
        switch (candidate.getStatus()) {
            case "approved":
                statusChip.setChipBackgroundColorResource(R.color.lightgreen); // Replace with your green color
                statusChip.setTextColor(getResources().getColor(R.color.green));

                break;
            case "canceled":
                statusChip.setChipBackgroundColorResource(R.color.lightred); // Replace with your red color
               statusChip.setTextColor(getResources().getColor(R.color.red));
                break;
            default:
                statusChip.setChipBackgroundColorResource(R.color.gray); // Replace with your gray color for pending
                break;
        }

        // Add the Chip to the container
        chipContainer.addView(statusChip);

        // Set an OnClickListener for the candidate card
        candidateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(adminCandidateApproval.this, adminCandidateApprove.class);
                intent.putExtra("CANDIDATE_UID", candidate.getUid()); // Use getUid() to pass the UID
                startActivity(intent);
            }
        });

        candidatesContainer.addView(candidateView); // Add the inflated view to the container
    }

}
