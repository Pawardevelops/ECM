package com.example.mec.candidate;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
    private AutoCompleteTextView departmentInput, courseInput, sectionInput, semesterInput;
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

        // Initialize AutoCompleteTextViews
        departmentInput = findViewById(R.id.department);
        courseInput = findViewById(R.id.course);
        sectionInput = findViewById(R.id.section);
        semesterInput = findViewById(R.id.semester);

        signUpButton = findViewById(R.id.candidateSignUp);
        alreadyHaveAccount = findViewById(R.id.candidate_login);
        profileImageView = findViewById(R.id.profile_image);

        // Populate dropdowns
        setUpDropdowns();

        // Set up sign-up button listener
        signUpButton.setOnClickListener(this::onSignUpButtonClick);

        // Set up "Already have an account?" listener
        alreadyHaveAccount.setOnClickListener(v -> NavigationService.navigateToActivity(CandidateSignup.this, candidateLogin.class));

        // Set up profile image selection
        profileImageView.setOnClickListener(v -> openFileChooser());
    }

    // Set up dropdown data for AutoCompleteTextView
    private void setUpDropdowns() {
        ArrayAdapter<CharSequence> departmentAdapter = ArrayAdapter.createFromResource(this, R.array.departments, android.R.layout.simple_dropdown_item_1line);
        departmentInput.setAdapter(departmentAdapter);

        ArrayAdapter<CharSequence> courseAdapter = ArrayAdapter.createFromResource(this, R.array.courses, android.R.layout.simple_dropdown_item_1line);
        courseInput.setAdapter(courseAdapter);

        ArrayAdapter<CharSequence> sectionAdapter = ArrayAdapter.createFromResource(this, R.array.sections, android.R.layout.simple_dropdown_item_1line);
        sectionInput.setAdapter(sectionAdapter);

        ArrayAdapter<CharSequence> semesterAdapter = ArrayAdapter.createFromResource(this, R.array.semesters, android.R.layout.simple_dropdown_item_1line);
        semesterInput.setAdapter(semesterAdapter);
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

    // Handle sign-up button click
    private void onSignUpButtonClick(View v) {
        String firstName = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String registrationNo = registrationNoInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();
        String slogan = sloganInput.getText().toString().trim();
        String department = departmentInput.getText().toString().trim();
        String course = courseInput.getText().toString().trim();
        String section = sectionInput.getText().toString().trim();
        String semester = semesterInput.getText().toString().trim();

        // Validate inputs
        if (validateInputs(firstName, lastName, email, registrationNo, password, confirmPassword, slogan, department, course, section, semester)) {
            setLoadingState(true);
            registerCandidate(firstName, lastName, email, registrationNo, password, slogan, department, course, section, semester);
        }
    }

    // Validate all inputs
    private boolean validateInputs(String firstName, String lastName, String email, String registrationNo, String password,
                                   String confirmPassword, String slogan, String department, String course,
                                   String section, String semester) {
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
            Toast.makeText(this, "Please select a department", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (course.isEmpty()) {
            Toast.makeText(this, "Please select a course", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (section.isEmpty()) {
            Toast.makeText(this, "Please select a section", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (semester.isEmpty()) {
            Toast.makeText(this, "Please select a semester", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // Register candidate using Firebase Authentication and Realtime Database
    private void registerCandidate(String firstName, String lastName, String email, String registrationNo, String password,
                                   String slogan, String department, String course, String section, String semester) {
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
        // Assign default values to avoid nulls
        Candidate candidate = new Candidate(
                userId,
                firstName != null ? firstName : "",
                lastName != null ? lastName : "",
                email != null ? email : "",
                registrationNo != null ? registrationNo : "",
                slogan != null ? slogan : "",
                imageUrl != null ? imageUrl : "",
                department != null ? department : "",
                course != null ? course : "",
                section != null ? section : "",
                semester != null ? semester : "",
                "Candidate",
                0

        );

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
        signUpButton.setEnabled(!isLoading);
        signUpButton.setText(isLoading ? "Signing Up..." : "Sign Up");
    }
}