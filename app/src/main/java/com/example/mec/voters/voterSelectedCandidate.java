package com.example.mec.voters;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.mec.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    private FirebaseAuth mAuth;


    private DatabaseReference candidatesRef;


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

        // Get the candidate ID from the intent extra
        String candidateId = getIntent().getStringExtra("CANDIDATE_UID");

        // Initialize Firebase Database reference
        candidatesRef = FirebaseDatabase.getInstance().getReference("candidates");

        if (candidateId != null) {
            // Fetch candidate data using the candidate ID
            fetchCandidateData(candidateId);
        } else {
            // Handle case when candidate ID is not passed
            Log.e("Firebase", "Candidate ID is null");
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

}
