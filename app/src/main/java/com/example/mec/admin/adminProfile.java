package com.example.mec.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.mec.MainActivity;
import com.example.mec.ProfileActivity;
import com.example.mec.R;
import com.example.mec.services.SharedPreferenceHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class adminProfile extends AppCompatActivity {

    private TextView candidateUsername, candidateRegistrationNumber, phoneNumber, departmentValue, sectionValue, courseValue, semValue;
    private ImageView profileImage;
    private Button logoutButton;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize Database Reference
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(adminProfile.this, "No user logged in", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if user is not logged in
            return;
        }

        String userId = currentUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Admins").child(userId);

        // Initialize UI elements
        candidateUsername = findViewById(R.id.candidate_username);
        candidateRegistrationNumber = findViewById(R.id.candidate_registration_number);
        phoneNumber = findViewById(R.id.phone_number);
        departmentValue = findViewById(R.id.department_value);
        sectionValue = findViewById(R.id.section_value);
        courseValue = findViewById(R.id.course_value);
        semValue = findViewById(R.id.sem_value);
        profileImage = findViewById(R.id.profile_image);
        logoutButton = findViewById(R.id.logoutButton);

        // Fetch and display admin profile data
        fetchAdminProfileData();

        // Handle logout button click
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                SharedPreferenceHelper.clearLoginInfo(adminProfile.this);

                Intent intent = new Intent(adminProfile.this, MainActivity.class); // Redirect to login screen
                startActivity(intent);
                finish(); // Close profile activity
            }
        });
    }

    private void fetchAdminProfileData() {
        // Listen for data from the "Admins" collection
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Fetch and display the data
                    String username = dataSnapshot.child("firstName").getValue(String.class) + " " + dataSnapshot.child("lastName").getValue(String.class);
                    String registrationNumber = dataSnapshot.child("registrationNo").getValue(String.class);
                    String phone = dataSnapshot.child("phone").getValue(String.class);
                    String department = dataSnapshot.child("department").getValue(String.class);
                    String section = dataSnapshot.child("section").getValue(String.class);
                    String course = dataSnapshot.child("course").getValue(String.class);
                    String semester = dataSnapshot.child("semester").getValue(String.class);
                    String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);

                    // Set the data to the UI components
                    candidateUsername.setText(username);
                    candidateRegistrationNumber.setText(registrationNumber);
                    phoneNumber.setText(phone != null ? phone : "N/A");
                    departmentValue.setText(department != null ? department : "N/A");
                    sectionValue.setText(section != null ? section : "N/A");
                    courseValue.setText(course != null ? course : "N/A");
                    semValue.setText(semester != null ? semester : "N/A");

                    // Load the profile image using Glide or any image loading library
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Glide.with(adminProfile.this)
                                .load(imageUrl)
                                .placeholder(R.drawable.profile) // default profile image
                                .into(profileImage);
                    }
                } else {
                    Toast.makeText(adminProfile.this, "Admin profile data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(adminProfile.this, "Failed to load profile: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
