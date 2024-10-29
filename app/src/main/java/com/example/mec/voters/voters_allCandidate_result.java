package com.example.mec.voters;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mec.R;
import com.example.mec.services.Candidate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.Set;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;  // Make sure to include Glide for image loading

public class voters_allCandidate_result extends AppCompatActivity {
    private DatabaseReference votesRef, candidatesRef;
    private String electionId; // Store the election ID
    private Set<String> candidateIds; // Store unique candidate IDs from votes
    private LinearLayout candidateList; // To hold the dynamically added candidate views

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voters_all_candidate_result);

        // Get the election ID from the intent
        electionId = getIntent().getStringExtra("ELECTION_ID");

        // Initialize Firebase references for votes and candidates
        votesRef = FirebaseDatabase.getInstance().getReference("votes");
        candidatesRef = FirebaseDatabase.getInstance().getReference("candidates");

        candidateIds = new HashSet<>(); // Initialize set to store unique candidate IDs
        candidateList = findViewById(R.id.candidateList); // Find the candidate list layout

        // Fetch votes and log candidate names
        fetchVotes();
    }

    // Method to fetch votes from Firebase and log matching candidates
    private void fetchVotes() {
        votesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot votesSnapshot) {
                if (votesSnapshot.exists()) {
                    for (DataSnapshot voteSnapshot : votesSnapshot.getChildren()) {
                        // Extracting the fields from each vote
                        String voteElectionId = voteSnapshot.child("electionId").getValue(String.class);
                        String candidateId = voteSnapshot.child("candidateId").getValue(String.class);

                        // Only collect candidate IDs for the current election
                        if (electionId.equals(voteElectionId)) {
                            candidateIds.add(candidateId); // Add candidateId to the set
                        }
                    }

                    // Once all votes are processed, fetch candidate details
                    fetchCandidateDetails();
                } else {
                    Toast.makeText(voters_allCandidate_result.this, "No votes found for this election.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                finish();
            }
        });
    }

    // Method to fetch candidate details from Firebase based on candidateIds from votes
    private void fetchCandidateDetails() {
        for (String candidateId : candidateIds) {
            candidatesRef.child(candidateId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot candidateSnapshot) {
                    if (candidateSnapshot.exists()) {
                        // Extract candidate fields manually
                        String firstName = candidateSnapshot.child("firstName").getValue(String.class);
                        String lastName = candidateSnapshot.child("lastName").getValue(String.class);
                        String section = candidateSnapshot.child("section").getValue(String.class);
                        String imageUrl = candidateSnapshot.child("imageUrl").getValue(String.class);
                        int votes = candidateSnapshot.child("voteCount").getValue(Integer.class) != null
                                ? candidateSnapshot.child("voteCount").getValue(Integer.class)
                                : 0;

                        // Create a new view for each candidate and add it to the candidateList
                        addCandidateView(firstName + " " + lastName, section, votes, imageUrl);
                    } else {
                        Log.d("CandidateLog", "Candidate not found for ID: " + candidateId);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("CandidateLog", "Error fetching candidate: " + databaseError.getMessage());
                }
            });
        }
    }

    // Method to inflate candidate_items.xml and populate it with data
    private void addCandidateView(String name, String section, int votes, String imageUrl) {
        // Inflate the candidate_items.xml layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View candidateView = inflater.inflate(R.layout.candidate_items, candidateList, false);

        // Get references to the views in the candidate_items layout
        TextView candidateNameView = candidateView.findViewById(R.id.candidate_name);
        TextView candidateSectionView = candidateView.findViewById(R.id.candidate_section);
        TextView candidateVotesView = candidateView.findViewById(R.id.candidateVotes1);
        ImageView candidateImageView = candidateView.findViewById(R.id.candidate_image);

        // Set the text and image for the candidate
        candidateNameView.setText(name);
        candidateSectionView.setText(section);
        candidateVotesView.setText(votes + " votes");

        // Load the candidate's image using Glide (or any image loader)
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this).load(imageUrl).into(candidateImageView);
        } else {
            candidateImageView.setImageResource(R.drawable.profile); // Set default image if no URL
        }

        // Add the inflated view to the candidateList layout
        candidateList.addView(candidateView);
    }
}
