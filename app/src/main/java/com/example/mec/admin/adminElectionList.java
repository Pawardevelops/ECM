package com.example.mec.admin;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mec.R;
import com.example.mec.services.Elections;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class adminElectionList extends AppCompatActivity {

    private LinearLayout electionsContainer;
    private ArrayList<Elections> electionList;  // Store elections in ArrayList
    private DatabaseReference electionRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_election_list);

        electionsContainer = findViewById(R.id.electionsContainer);

        // Initialize Firebase reference
        electionRef = FirebaseDatabase.getInstance().getReference("elections");

        // Initialize ArrayList
        electionList = new ArrayList<>();

        // Fetch and display elections
        fetchElections();
    }

    private void fetchElections() {
        electionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                electionList.clear();
                electionsContainer.removeAllViews();  // Clear any existing views

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Elections election = snapshot.getValue(Elections.class);
                    if (election != null) {
                        election.setId(snapshot.getKey());

                        // Add election to the ArrayList
                        electionList.add(election);

                        // Dynamically add the election card (election.xml) for each election
                        addElectionCard(election);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(adminElectionList.this, "Failed to load elections.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addElectionCard(Elections election) {
        // Inflate the election.xml layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View electionCardView = inflater.inflate(R.layout.elections, electionsContainer, false);

        // Get references to the views in the card
        TextView electionName = electionCardView.findViewById(R.id.electionName);
        TextView electionSection = electionCardView.findViewById(R.id.electionSection);
        LinearLayout chipContainer = electionCardView.findViewById(R.id.chip);

        // Set the election data to the TextViews
        electionName.setText(election.getTitle());  // Assuming the election title corresponds to "Candidate Name"
        electionSection.setText("Section : "+election.getSection());  // Assuming the election section corresponds to "Candidate Section"

        // Dynamically create and add a Chip to display the election status
        Chip statusChip = new Chip(this);
        statusChip.setText(election.getStatus() != null ? election.getStatus() : "Not Started");
        statusChip.setChipStrokeWidth(0);
        statusChip.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        // Set the Chip's background color based on the status
        switch (election.getStatus() != null ? election.getStatus() : "not_started") {
            case "Started":
                statusChip.setChipBackgroundColorResource(R.color.lightgreen);  // Green for started
                statusChip.setTextColor(getResources().getColor(R.color.green));
                break;
            case "Finished":
                statusChip.setChipBackgroundColorResource(R.color.tertiary);  // Blue for completed
                statusChip.setTextColor(getResources().getColor(R.color.primary));
                break;
            default:  // Not started or no status
                statusChip.setChipBackgroundColorResource(R.color.lightred);  // Red for not started
                statusChip.setTextColor(getResources().getColor(R.color.red));
                break;
        }

        // Add the Chip to the chip container
        chipContainer.addView(statusChip);

        // Add the card to the elections container
        electionsContainer.addView(electionCardView);

        // Set click listener for further actions (if needed)
        electionCardView.setOnClickListener(v -> showElectionDialog(election));
    }

    private void showElectionDialog(Elections election) {
        String[] options = {"Start Election", "Complete Election"};

        new MaterialAlertDialogBuilder(this)
                .setTitle(election.getTitle())
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: // Start Election
                                updateElectionStatus(election.getId(), "Started");
                                break;
                            case 1: // Complete Election
                                updateElectionStatus(election.getId(), "Finished");
                                break;
                        }
                    }
                })
                .show();
    }

    private void updateElectionStatus(String electionId, String status) {
        electionRef.child(electionId).child("status").setValue(status).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(adminElectionList.this, "Election status updated to " + status, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(adminElectionList.this, "Failed to update election status.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
