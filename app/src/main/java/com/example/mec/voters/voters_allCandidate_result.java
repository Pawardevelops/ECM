package com.example.mec.voters;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mec.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class voters_allCandidate_result extends AppCompatActivity {
    private LinearLayout candidateList;
    private DatabaseReference candidatesRef, votesRef; // References for candidates and votes
    private String electionId; // Store the election ID
    private int totalVotes = 0; // To track total votes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_voters_all_candidate_result);

        // Initialize views
        candidateList = findViewById(R.id.candidateList);

        // Get the election ID from the intent
        electionId = getIntent().getStringExtra("ELECTION_ID");

        // Initialize Firebase references for candidates and votes
        candidatesRef = FirebaseDatabase.getInstance().getReference("elections").child(electionId).child("candidates");
        votesRef = FirebaseDatabase.getInstance().getReference("votes").child(electionId);

        // Fetch candidates and their corresponding votes
        fetchCandidates();
    }

    // Method to fetch candidates and votes from Firebase
    private void fetchCandidates() {
        candidatesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot candidatesSnapshot) {
                if (candidatesSnapshot.exists()) {
                    List<Candidate> candidates = new ArrayList<>();
                    Map<String, Integer> candidateVotesMap = new HashMap<>();

                    // Iterate through each candidate
                    for (DataSnapshot candidateSnapshot : candidatesSnapshot.getChildren()) {
                        String candidateId = candidateSnapshot.getKey(); // Get candidate ID
                        String name = candidateSnapshot.child("name").getValue(String.class);
                        String section = candidateSnapshot.child("section").getValue(String.class);

                        // Initialize vote count to 0 for this candidate
                        candidateVotesMap.put(candidateId, 0);

                        candidates.add(new Candidate(candidateId, name, section, 0, 0.0));
                    }

                    // Fetch votes and update the candidate vote counts
                    fetchVotes(candidates, candidateVotesMap);
                } else {
                    Toast.makeText(voters_allCandidate_result.this, "No candidates found for this election.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(voters_allCandidate_result.this, "Error fetching candidates: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to fetch votes and update the vote count for each candidate
    private void fetchVotes(List<Candidate> candidates, Map<String, Integer> candidateVotesMap) {
        votesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot votesSnapshot) {
                if (votesSnapshot.exists()) {
                    totalVotes = (int) votesSnapshot.getChildrenCount(); // Total number of votes

                    // Iterate through each vote and update vote count for the candidate
                    for (DataSnapshot voteSnapshot : votesSnapshot.getChildren()) {
                        String candidateId = voteSnapshot.getValue(String.class); // Here we get candidateId directly

                        // Increment vote count for the candidate
                        if (candidateVotesMap.containsKey(candidateId)) {
                            int currentVotes = candidateVotesMap.get(candidateId);
                            candidateVotesMap.put(candidateId, currentVotes + 1);
                        }
                    }

                    // Update the candidate list with vote count and percentage
                    updateCandidateVoteData(candidates, candidateVotesMap);
                } else {
                    Toast.makeText(voters_allCandidate_result.this, "No votes found for this election.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(voters_allCandidate_result.this, "Error fetching votes: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to update candidates with vote count and percentage
    private void updateCandidateVoteData(List<Candidate> candidates, Map<String, Integer> candidateVotesMap) {
        for (Candidate candidate : candidates) {
            int votes = candidateVotesMap.get(candidate.getId());
            double percentage = totalVotes > 0 ? (votes * 100.0) / totalVotes : 0.0;
            candidate.setVotes(votes);
            candidate.setPercentage(percentage);
        }

        // Sort candidates based on percentage (descending order)
        Collections.sort(candidates, new Comparator<Candidate>() {
            @Override
            public int compare(Candidate c1, Candidate c2) {
                return Double.compare(c2.getPercentage(), c1.getPercentage());
            }
        });

        // Display candidates
        displayCandidates(candidates);
    }

    // Method to display candidates in the LinearLayout
    private void displayCandidates(List<Candidate> candidates) {
        for (Candidate candidate : candidates) {
            // Inflate the candidate layout
            View candidateCardView = getLayoutInflater().inflate(R.layout.candidate_items, candidateList, false);

            // Get references to the TextView elements within the inflated layout
            TextView candidateNameTextView = candidateCardView.findViewById(R.id.candidate_name);
            TextView candidateSectionTextView = candidateCardView.findViewById(R.id.candidate_section);
            TextView candidatePercentageTextView = candidateCardView.findViewById(R.id.candidatePercentage1);
            TextView candidateVotesTextView = candidateCardView.findViewById(R.id.candidateVotes1);

            // Set the candidate details
            candidateNameTextView.setText(candidate.getName());
            candidateSectionTextView.setText(candidate.getSection());
            candidatePercentageTextView.setText(String.format("%.2f%%", candidate.getPercentage()));
            candidateVotesTextView.setText(String.format("%d votes", candidate.getVotes()));

            // Add the inflated view to the candidate list
            candidateList.addView(candidateCardView);
        }
    }

    // Candidate model class
    private static class Candidate {
        private String id;
        private String name;
        private String section;
        private int votes;
        private double percentage;

        public Candidate(String id, String name, String section, int votes, double percentage) {
            this.id = id;
            this.name = name;
            this.section = section;
            this.votes = votes;
            this.percentage = percentage;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getSection() {
            return section;
        }

        public int getVotes() {
            return votes;
        }

        public void setVotes(int votes) {
            this.votes = votes;
        }

        public double getPercentage() {
            return percentage;
        }

        public void setPercentage(double percentage) {
            this.percentage = percentage;
        }
    }
}
