package com.example.mec.admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import com.example.mec.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class adminElections extends AppCompatActivity {

    private LinearLayout electionsContainer;  // Container to add election buttons dynamically
    private DatabaseReference databaseReference;
    private AppCompatButton createElectionButton;  // Add a reference to the Create Election button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_elections);

        electionsContainer = findViewById(R.id.electionsContainer);  // This is where we'll add election buttons
        createElectionButton = findViewById(R.id.electionCreate);    // Find the Create Election button
        databaseReference = FirebaseDatabase.getInstance().getReference("elections");

        // Set up OnClickListener for Create Election button
        createElectionButton.setOnClickListener(v -> {
            Intent createElectionIntent = new Intent(adminElections.this, adminCreateElections.class);
            startActivity(createElectionIntent);  // Navigate to the screen to create elections
        });

        // Fetch and display elections
        fetchAndDisplayElections();
    }

    // Method to fetch and display elections dynamically
    private void fetchAndDisplayElections() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                electionsContainer.removeAllViews();  // Clear previous views if any

                for (DataSnapshot electionSnapshot : dataSnapshot.getChildren()) {
                    // Extract election data
                    String electionId = electionSnapshot.getKey();
                    String electionTitle = electionSnapshot.child("title").getValue(String.class);

                    // Dynamically create buttons for each election using a styled context
                    ContextThemeWrapper newContext = new ContextThemeWrapper(adminElections.this, R.style.SolidButton);
                    AppCompatButton electionButton = new AppCompatButton(newContext);
                    electionButton.setText(electionTitle);

                    // Set margins for the button
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,  // Width
                            LinearLayout.LayoutParams.WRAP_CONTENT   // Height
                    );
                    params.setMargins(16, 16, 16, 16);  // Set margin for each button
                    electionButton.setLayoutParams(params);

                    // Set click listener for each button
                    electionButton.setOnClickListener(v -> {
                        showElectionOptionsDialog(electionId, electionTitle);
                    });

                    // Add the button to the container
                    electionsContainer.addView(electionButton);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
                Toast.makeText(adminElections.this, "Failed to load elections.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    // Method to show a dialog with options when an election is clicked
    private void showElectionOptionsDialog(String electionId, String electionTitle) {
        String[] options = {"Update", "Delete"};
        new MaterialAlertDialogBuilder(this)
                .setTitle(electionTitle)
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: // Update
                                Intent editIntent = new Intent(adminElections.this, adminCreateElections.class);
                                editIntent.putExtra("electionId", electionId);  // Pass electionId to edit the election
                                startActivity(editIntent);
                                break;
                            case 1: // Delete
                                showDeleteConfirmationDialog(electionId);
                                break;
                        }
                    }
                })
                .show();
    }

    // Show delete confirmation dialog before deleting election
    private void showDeleteConfirmationDialog(String electionId) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete Election")
                .setMessage("Are you sure you want to delete this election?")
                .setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteElection(electionId);
                })
                .show();
    }

    // Method to delete the election from Firebase
    private void deleteElection(String electionId) {
        databaseReference.child(electionId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(adminElections.this, "Election deleted successfully.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(adminElections.this, "Failed to delete election.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
