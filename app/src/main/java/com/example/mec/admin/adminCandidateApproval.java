package com.example.mec.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.mec.R;
import com.example.mec.services.Candidate;
import com.example.mec.voters.voters_all_candidates;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Assuming your Voter model has the appropriate fields
                    Candidate candidate = snapshot.getValue(Candidate.class);
                        addCandidateToLayout(candidate, snapshot.getKey()); // Pass the UID to the method

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(adminCandidateApproval.this, "Failed to load candidates: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addCandidateToLayout(Candidate candidate, String candidateUid) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View candidateView = inflater.inflate(R.layout.candidate_items, candidatesContainer, false);

        TextView candidateName = candidateView.findViewById(R.id.candidate_name);
        TextView candidateSection = candidateView.findViewById(R.id.candidate_section);
        ImageView candidateImage = candidateView.findViewById(R.id.candidate_image);

        candidateName.setText(candidate.getFirstName() + " " + candidate.getLastName());
        candidateSection.setText(candidate.getSection());

        // Load the candidate image using Glide
        String imageUrl = candidate.getImageUrl(); // Get the image URL
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
        candidateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to AdminCandidateApproval activity
                Intent intent = new Intent(adminCandidateApproval.this, adminCandidateApprove.class);
                intent.putExtra("CANDIDATE_UID", candidateUid); // Send the candidate's UID
                startActivity(intent);
            }
        });

        candidatesContainer.addView(candidateView); // Add the inflated view to the container
    }
}