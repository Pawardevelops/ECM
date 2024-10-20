package com.example.mec.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.mec.R;
import com.example.mec.services.Candidate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class adminCandidateApprove extends AppCompatActivity {

    private DatabaseReference candidateRef; // Database reference for the candidate
    private String candidateUid; // UID of the candidate to be fetched

    // UI elements
    private TextView candidateNameTextView;
    private TextView candidateSectionTextView;
    private TextView candidatePhoneTextView;
    private TextView candidateDepartmentTextView;
    private TextView candidateCourseTextView;
    private TextView candidateSemesterTextView;
    private ImageView candidateImageView;
    private Button approveButton;
    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_andmincandidate_approved);

        // Initialize UI elements
        candidateNameTextView = findViewById(R.id.candidate_username);
        candidateSectionTextView = findViewById(R.id.section_value);
        candidatePhoneTextView = findViewById(R.id.phone_number);  // Added phone TextView
        candidateDepartmentTextView = findViewById(R.id.department_value);  // Added department TextView
        candidateCourseTextView = findViewById(R.id.course_value);  // Added course TextView
        candidateSemesterTextView = findViewById(R.id.sem_value);  // Added semester TextView
        candidateImageView = findViewById(R.id.profile_image);
        approveButton = findViewById(R.id.approv_candidate);
        cancelButton = findViewById(R.id.cancel_candidate);

        // Retrieve the candidate UID from the intent
        candidateUid = getIntent().getStringExtra("CANDIDATE_UID");

        if (candidateUid != null) {
            // Initialize Firebase Database reference for the specific candidate
            candidateRef = FirebaseDatabase.getInstance().getReference("candidates").child(candidateUid);
            fetchCandidateDetails();
        } else {
            Toast.makeText(this, "No candidate UID found", Toast.LENGTH_SHORT).show();
        }

        // Set onClickListener for approve button
        approveButton.setOnClickListener(v -> updateCandidateStatus("approved"));

        // Set onClickListener for cancel button
        cancelButton.setOnClickListener(v -> updateCandidateStatus("canceled"));
    }

    private void fetchCandidateDetails() {
        candidateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Candidate candidate = dataSnapshot.getValue(Candidate.class);
                    if (candidate != null) {
                        // Set the UI elements with candidate details
                        candidateNameTextView.setText(candidate.getFirstName() + " " + candidate.getLastName());
                        candidateSectionTextView.setText(candidate.getSection());
                        candidatePhoneTextView.setText(candidate.getEmail()); // Assuming the phone field is represented by the email field.
                        candidateDepartmentTextView.setText(candidate.getDepartment());
                        candidateCourseTextView.setText(candidate.getCourse());
                        candidateSemesterTextView.setText(candidate.getSemester());

                        // Load the candidate image using Glide
                        String imageUrl = candidate.getImageUrl();
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Glide.with(adminCandidateApprove.this)
                                    .load(imageUrl)
                                    .placeholder(R.drawable.profile) // Placeholder image
                                    .error(R.drawable.profile) // Error image if loading fails
                                    .into(candidateImageView);
                        } else {
                            candidateImageView.setImageResource(R.drawable.profile); // Default image
                        }

                        // Check if candidate is already approved and disable cancel button if so
                        if ("approved".equalsIgnoreCase(candidate.getStatus())) {
                            approveButton.setEnabled(false); // Disable approve button
                            cancelButton.setVisibility(View.GONE); // Hide cancel button
                        } else if ("canceled".equalsIgnoreCase(candidate.getStatus())) {
                            cancelButton.setEnabled(false); // Enable approve button
                            approveButton.setVisibility(View.GONE); // Show cancel button
                        }
                    } else {
                        Toast.makeText(adminCandidateApprove.this, "Candidate not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(adminCandidateApprove.this, "Candidate data does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(adminCandidateApprove.this, "Failed to load candidate details: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCandidateStatus(String status) {
        candidateRef.child("status").setValue(status).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(adminCandidateApprove.this, "Candidate status updated to: " + status, Toast.LENGTH_SHORT).show();
                if (status.equals("approved")) {
                    approveButton.setEnabled(false);
                    cancelButton.setVisibility(View.GONE); // Hide cancel button if approved
                } else if (status.equals("canceled")) {
                    cancelButton.setEnabled(false);
                    approveButton.setVisibility(View.GONE);
                }
            } else {
                Toast.makeText(adminCandidateApprove.this, "Failed to update status", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
