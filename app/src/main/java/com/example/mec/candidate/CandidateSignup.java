package com.example.mec.candidate;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mec.R;
import com.example.mec.services.Candidate;
import com.example.mec.services.NavigationService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class CandidateSignup extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    // UI elements
    private EditText firstNameInput, lastNameInput, emailInput, registrationNoInput, passwordInput, confirmPasswordInput, sloganInput;
    private EditText departmentInput, courseInput, sectionInput, semesterInput;
    private Button signUpButton;
    private TextView alreadyHaveAccount;
    private ImageView profileImageView;
    private Uri imageUri;  // Store the selected image URI

    // Firebase instances
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_candidate_signup);

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("candidates");
        storageRef = FirebaseStorage.getInstance().getReference("profile_images");

        // Initialize UI components
        firstNameInput = findViewById(R.id.first_name);
        lastNameInput = findViewById(R.id.last_name);
        emailInput = findViewById(R.id.email);
        registrationNoInput = findViewById(R.id.registration_no);
        passwordInput = findViewById(R.id.password);
        confirmPasswordInput = findViewById(R.id.confirm_password);
        sloganInput = findViewById(R.id.slogan);
        departmentInput = findViewById(R.id.department);
        courseInput = findViewById(R.id.course);
        sectionInput = findViewById(R.id.section);
        semesterInput = findViewById(R.id.semester);
        signUpButton = findViewById(R.id.candidateSignUp);
        alreadyHaveAccount = findViewById(R.id.candidate_login);
        profileImageView = findViewById(R.id.profile_image);

        // Set up click listener for sign-up button
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get input values
                String firstName = firstNameInput.getText().toString();
                String lastName = lastNameInput.getText().toString();
                String email = emailInput.getText().toString();
                String registrationNo = registrationNoInput.getText().toString();
                String password = passwordInput.getText().toString();
                String confirmPassword = confirmPasswordInput.getText().toString();
                String slogan = sloganInput.getText().toString();
                String department = departmentInput.getText().toString();
                String course = courseInput.getText().toString();
                String section = sectionInput.getText().toString();
                String semester = semesterInput.getText().toString();

                // Validate inputs
                if (validateInputs(firstName, lastName, email, registrationNo, password, confirmPassword, slogan, department, course, section, semester)) {
                    setLoadingState(true);
                    registerCandidate(firstName, lastName, email, registrationNo, password, slogan, department, course, section, semester);
                }
            }
        });

        // Set click listener for "Already have an account?" text
        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationService.navigateToActivity(CandidateSignup.this, candidateLogin.class);
            }
        });

        // Set click listener for profile image selection
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
    }

    // Open file chooser for image selection
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImageView.setImageURI(imageUri);  // Display the selected image
        }
    }

    // Validate all inputs
    private boolean validateInputs(String firstName, String lastName, String email, String registrationNo, String password, String confirmPassword, String slogan,
                                   String department, String course, String section, String semester) {
        if (firstName.isEmpty()) {
            firstNameInput.setError("First name is required");
            return false;
        }
        if (lastName.isEmpty()) {
            lastNameInput.setError("Last name is required");
            return false;
        }
        if (email.isEmpty()) {
            emailInput.setError("Email is required");
            return false;
        }
        if (registrationNo.isEmpty()) {
            registrationNoInput.setError("Registration number is required");
            return false;
        }
        if (password.isEmpty()) {
            passwordInput.setError("Password is required");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordInput.setError("Passwords do not match");
            return false;
        }
        if (slogan.isEmpty()) {
            sloganInput.setError("Slogan is required");
            return false;
        }
        if (department.isEmpty()) {
            departmentInput.setError("Department is required");
            return false;
        }
        if (course.isEmpty()) {
            courseInput.setError("Course is required");
            return false;
        }
        if (section.isEmpty()) {
            sectionInput.setError("Section is required");
            return false;
        }
        if (semester.isEmpty()) {
            semesterInput.setError("Semester is required");
            return false;
        }
        return true;
    }

    // Register candidate using Firebase Authentication and Realtime Database
    private void registerCandidate(String firstName, String lastName, String email, String registrationNo, String password, String slogan,
                                   String department, String course, String section, String semester) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();

                            if (imageUri != null) {
                                uploadProfileImage(userId, firstName, lastName, email, registrationNo, slogan, department, course, section, semester);
                            } else {
                                saveCandidateData(userId, firstName, lastName, email, registrationNo, slogan, null, department, course, section, semester);
                            }
                        }
                    } else {
                        Toast.makeText(CandidateSignup.this, "Sign-up failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        setLoadingState(false);
                    }
                });
    }

    // Upload profile image to Firebase Storage
    private void uploadProfileImage(String userId, String firstName, String lastName, String email, String registrationNo, String slogan,
                                    String department, String course, String section, String semester) {
        StorageReference fileReference = storageRef.child(userId + ".jpg");
        UploadTask uploadTask = fileReference.putFile(imageUri);

        uploadTask.addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(downloadUri -> {
            String imageUrl = downloadUri.toString();
            saveCandidateData(userId, firstName, lastName, email, registrationNo, slogan, imageUrl, department, course, section, semester);
        })).addOnFailureListener(e -> {
            Toast.makeText(CandidateSignup.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            setLoadingState(false);
        });
    }

    // Save candidate data to Firebase Realtime Database
    private void saveCandidateData(String userId, String firstName, String lastName, String email, String registrationNo, String slogan,
                                   String imageUrl, String department, String course, String section, String semester) {
        Candidate candidate = new Candidate(firstName, lastName, email, registrationNo, slogan, imageUrl, department, course, section.toUpperCase(), semester, "Candidate",userId);

        dbRef.child(userId).setValue(candidate)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(CandidateSignup.this, "Sign-up successful", Toast.LENGTH_SHORT).show();
                    NavigationService.navigateToActivity(CandidateSignup.this, CandidateSuccessfullyRegistered.class);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CandidateSignup.this, "Failed to register candidate: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    setLoadingState(false);
                });
    }

    // Set loading state for the sign-up button
    private void setLoadingState(boolean isLoading) {
        if (isLoading) {
            signUpButton.setEnabled(false);
            signUpButton.setText("Signing Up...");
        } else {
            signUpButton.setEnabled(true);
            signUpButton.setText("Sign Up");
        }
    }
}
