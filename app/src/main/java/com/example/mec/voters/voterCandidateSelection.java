package com.example.mec.voters;

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
import com.example.mec.services.Candidate; // Import your Candidate model
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class voterCandidateSelection extends AppCompatActivity {

    private LinearLayout candidatesContainer;
    private DatabaseReference candidatesRef;
    private String section;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voter_candidate_selection);

        candidatesContainer = findViewById(R.id.candidates_container); // Container to add candidate views dynamically

        // Get the section passed from the previous activity
        section = getIntent().getStringExtra("SECTION");

        if (section == null) {
            Toast.makeText(this, "No section found", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if no section is found
        }

        // Initialize Firebase Database reference
        candidatesRef = FirebaseDatabase.getInstance().getReference("candidates");

        // Fetch and display candidates from the specified section
        fetchCandidatesBySection(section);
    }

    private void fetchCandidatesBySection(String section) {
        // Query candidates where the section matches
        Query query = candidatesRef.orderByChild("section").equalTo(section);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                candidatesContainer.removeAllViews(); // Clear any previous candidates
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Candidate candidate = snapshot.getValue(Candidate.class);
                    if (candidate != null) {
                        addCandidateToLayout(candidate, snapshot.getKey()); // Pass the candidate UID
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(voterCandidateSelection.this, "Failed to load candidates: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addCandidateToLayout(Candidate candidate, String candidateUid) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View candidateView = inflater.inflate(R.layout.candidate_items, candidatesContainer, false); // Assuming you have a candidate_items layout

        TextView candidateName = candidateView.findViewById(R.id.candidate_name);
        TextView candidateSection = candidateView.findViewById(R.id.candidate_section);
        ImageView candidateImage = candidateView.findViewById(R.id.candidate_image);

        candidateName.setText(candidate.getFirstName() + " " + candidate.getLastName());
        candidateSection.setText(candidate.getSection());

        // Load the candidate image using Glide
        String imageUrl = candidate.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.profile) // Placeholder image
                    .error(R.drawable.profile) // Error image if loading fails
                    .into(candidateImage);
        } else {
            candidateImage.setImageResource(R.drawable.profile); // Set a default image if URL is empty
        }

        // Set an OnClickListener for the candidate card
        candidateView.setOnClickListener(view -> {
            // Navigate to electedCandidate activity
            Intent intent = new Intent(voterCandidateSelection.this, voterSelectedCandidate.class);
            intent.putExtra("CANDIDATE_UID", candidateUid); // Pass the candidate's UID
            startActivity(intent);
        });

        candidatesContainer.addView(candidateView); // Add the inflated view to the container
    }
}
