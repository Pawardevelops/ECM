package com.example.mec.voters;

import android.content.Intent;
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
import java.util.List;

public class voters_section_menu_result extends AppCompatActivity {
    private LinearLayout classAResult, classBResult, classCResult;
    private LinearLayout completedElectionsContainer; // New LinearLayout for completed elections
    private DatabaseReference electionsRef; // Reference to the elections node
    private List<String> completedElections; // List to store completed election IDs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_voters_section_menu_result);


        completedElectionsContainer = findViewById(R.id.completedElectionsContainer);

        // Initialize Firebase reference
        electionsRef = FirebaseDatabase.getInstance().getReference("elections");

        // Fetch completed elections
        fetchCompletedElections();


    }

    // Method to fetch completed elections from Firebase
    // Method to fetch completed elections from Firebase
    private void fetchCompletedElections() {
        electionsRef.orderByChild("status").equalTo("completed").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                completedElections = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot electionSnapshot : dataSnapshot.getChildren()) {
                        String electionId = electionSnapshot.getKey();
                        completedElections.add(electionId); // Store election ID

                        // Inflate the custom CardView layout
                        View electionCardView = getLayoutInflater().inflate(R.layout.elections, completedElectionsContainer, false);

                        // Get references to TextView elements within the inflated layout
                        TextView electionNameTextView = electionCardView.findViewById(R.id.electionName);
                        TextView electionSectionTextView = electionCardView.findViewById(R.id.electionSection);

                        // Set text from Firebase data (assuming 'title' and 'section' fields exist)
                        String electionName = "Name : "+electionSnapshot.child("title").getValue(String.class);
                        String electionSection = "Section : "+electionSnapshot.child("section").getValue(String.class);

                        // Set the text of the TextViews
                        electionNameTextView.setText(electionName != null ? electionName : "Unnamed Election");
                        electionSectionTextView.setText(electionSection != null ? electionSection : "Unnamed Section");

                        // Set OnClickListener for the card
                        electionCardView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                navigateToResults(electionId);
                            }
                        });

                        // Add the inflated CardView to the container
                        completedElectionsContainer.addView(electionCardView);
                    }
                } else {
                    Toast.makeText(voters_section_menu_result.this, "No completed elections found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(voters_section_menu_result.this, "Error fetching elections: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Method to navigate to results activity and pass election ID
    private void navigateToResults(String electionId) {
        // Create intent and put electionId as extra
        Intent intent = new Intent(voters_section_menu_result.this, voters_allCandidate_result.class);
        intent.putExtra("ELECTION_ID", electionId); // Pass the electionId as an extra
        startActivity(intent);
    }
}
