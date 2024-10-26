package com.example.mec;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.mec.services.SharedPreferenceHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    // Firebase Authentication instance
    private FirebaseAuth mAuth;

    // Firebase Database reference
    private DatabaseReference userProfileRef;

    // UI elements
    private TextView usernameTextView, registrationNumberTextView, phoneNumberTextView, departmentTextView, sectionTextView, courseTextView, semTextView;
    private ImageView profileImageView;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile); // Your XML layout file name

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Find UI elements
        logoutButton = findViewById(R.id.logoutButton);
        usernameTextView = findViewById(R.id.candidate_username);
        registrationNumberTextView = findViewById(R.id.candidate_registration_number);
        phoneNumberTextView = findViewById(R.id.phone_number); // Now a TextView
        departmentTextView = findViewById(R.id.department_value); // Now a TextView
        sectionTextView = findViewById(R.id.section_value); // Now a TextView
        courseTextView = findViewById(R.id.course_value); // Now a TextView
        semTextView = findViewById(R.id.sem_value); // Now a TextView
        profileImageView = findViewById(R.id.profile_image);
        Intent intent = getIntent();
        String db = intent.getStringExtra("db");

        // Get current user and UID
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid(); // This is the unique UID for the logged-in user
            fetchUserProfile(db,uid); // Fetch user profile based on UID
            Toast.makeText(this, "User id : "+uid, Toast.LENGTH_SHORT).show();


        } else {
            // If no user is logged in, show a message or redirect to login
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
        logoutButton.setOnClickListener(v -> {
            // When logging out:
            SharedPreferenceHelper.clearLoginInfo(ProfileActivity.this);

            logoutUser();
        });
    }

    private void fetchUserProfile(String db,String uid) {
        // Assuming the user's data is stored in the Firebase Realtime Database under "voters" node
        userProfileRef = FirebaseDatabase.getInstance().getReference(db).child(uid);

        // Fetching data from the database
        userProfileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Fetching values from the database
                    String username = dataSnapshot.child("firstName").getValue(String.class);
                    String registrationNumber = dataSnapshot.child("registrationNo").getValue(String.class);
                    String phoneNumber = dataSnapshot.child("email").getValue(String.class);
                    String department = dataSnapshot.child("department").getValue(String.class);
                    String section = dataSnapshot.child("section").getValue(String.class);
                    String course = dataSnapshot.child("course").getValue(String.class);
                    String semester = dataSnapshot.child("semester").getValue(String.class);
                    String profileImageUrl = dataSnapshot.child("imageUrl").getValue(String.class); // URL of the profile image

                    // Update UI elements with retrieved data
                    usernameTextView.setText(username != null ? username : "N/A");
                    registrationNumberTextView.setText(registrationNumber != null ? registrationNumber : "N/A");
                    phoneNumberTextView.setText(phoneNumber != null ? phoneNumber : "N/A");
                    departmentTextView.setText(department != null ? department : "N/A");
                    sectionTextView.setText(section != null ? section : "N/A");
                    courseTextView.setText(course != null ? course : "N/A");
                    semTextView.setText(semester != null ? semester : "N/A");

                    // Handle profile image (if present)
                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        // Use Glide to load the profile image
                        Glide.with(ProfileActivity.this)
                                .load(profileImageUrl)
                                .placeholder(R.drawable.profile) // Optional placeholder image
                                .error(R.drawable.profile) // Error image if loading fails
                                .into(profileImageView);
                    } else {
                        // If no profile image URL is available, set a default image
                        profileImageView.setImageResource(R.drawable.profile);
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Profile data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                Toast.makeText(ProfileActivity.this, "Failed to load profile: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void logoutUser() {
        // Sign out the user from Firebase Auth
        mAuth.signOut();

        // Redirect to MainActivity
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear activity stack
        startActivity(intent);
        finish(); // Close the current activity
    }
}
