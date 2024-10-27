package com.example.mec.voters;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mec.R;
import com.example.mec.services.Voter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class voters_signup extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private StorageReference storageRef;

    // UI components
    private AutoCompleteTextView departmentDropdown, sectionDropdown, courseDropdown, semesterDropdown;
    private EditText firstNameEditText, lastNameEditText, emailEditText, registrationNoEditText, passwordEditText, confirmPasswordEditText;
    private ImageView profileImageView;
    private ProgressBar progressBar;
    private View signUpButton;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voters_signup);

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("voters");
        storageRef = FirebaseStorage.getInstance().getReference("profile_images");

        // Initialize views
        firstNameEditText = findViewById(R.id.first_name);
        lastNameEditText = findViewById(R.id.last_name);
        emailEditText = findViewById(R.id.email);
        registrationNoEditText = findViewById(R.id.registration_no);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirm_password);
        profileImageView = findViewById(R.id.profile_image);
        progressBar = findViewById(R.id.progressBar);
        signUpButton = findViewById(R.id.candidateSignUp); // Sign-up button

        // Initialize AutoCompleteTextView dropdowns
        departmentDropdown = findViewById(R.id.department);
        sectionDropdown = findViewById(R.id.section);
        courseDropdown = findViewById(R.id.course);
        semesterDropdown = findViewById(R.id.semester);

        // Set up listeners
        findViewById(R.id.upload_image).setOnClickListener(v -> selectImage());
        signUpButton.setOnClickListener(v -> validateAndRegister());
        findViewById(R.id.candidate_login).setOnClickListener(v -> navigateToLogin());

        // Set up dropdown data for AutoCompleteTextViews
        setUpDropdowns();
    }

    private void setUpDropdowns() {
        // Populate department dropdown
        ArrayAdapter<CharSequence> departmentAdapter = ArrayAdapter.createFromResource(this, R.array.departments, android.R.layout.simple_dropdown_item_1line);
        departmentDropdown.setAdapter(departmentAdapter);

        // Populate section dropdown
        ArrayAdapter<CharSequence> sectionAdapter = ArrayAdapter.createFromResource(this, R.array.sections, android.R.layout.simple_dropdown_item_1line);
        sectionDropdown.setAdapter(sectionAdapter);

        // Populate course dropdown
        ArrayAdapter<CharSequence> courseAdapter = ArrayAdapter.createFromResource(this, R.array.courses, android.R.layout.simple_dropdown_item_1line);
        courseDropdown.setAdapter(courseAdapter);

        // Populate semester dropdown
        ArrayAdapter<CharSequence> semesterAdapter = ArrayAdapter.createFromResource(this, R.array.semesters, android.R.layout.simple_dropdown_item_1line);
        semesterDropdown.setAdapter(semesterAdapter);
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImageView.setImageURI(imageUri);
        }
    }

    private void validateAndRegister() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String registrationNo = registrationNoEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        String department = departmentDropdown.getText().toString().trim();
        String section = sectionDropdown.getText().toString().trim();
        String course = courseDropdown.getText().toString().trim();
        String semester = semesterDropdown.getText().toString().trim();

        // Validation logic
        if (firstName.isEmpty()) {
            firstNameEditText.setError("First name is required");
            firstNameEditText.requestFocus();
            return;
        }
        if (lastName.isEmpty()) {
            lastNameEditText.setError("Last name is required");
            lastNameEditText.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please provide a valid email");
            emailEditText.requestFocus();
            return;
        }
        if (registrationNo.isEmpty()) {
            registrationNoEditText.setError("Registration number is required");
            registrationNoEditText.requestFocus();
            return;
        } else if (registrationNo.length() != 9 || !registrationNo.matches("\\d{9}")) {
            registrationNoEditText.setError("Registration number must be exactly 9 digits");
            registrationNoEditText.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            return;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            confirmPasswordEditText.requestFocus();
            return;
        }
        if (department.isEmpty()) {
            departmentDropdown.setError("Please select a department");
            departmentDropdown.requestFocus();
            return;
        }
        if (section.isEmpty()) {
            sectionDropdown.setError("Please select a section");
            sectionDropdown.requestFocus();
            return;
        }
        if (course.isEmpty()) {
            courseDropdown.setError("Please select a course");
            courseDropdown.requestFocus();
            return;
        }
        if (semester.isEmpty()) {
            semesterDropdown.setError("Please select a semester");
            semesterDropdown.requestFocus();
            return;
        }

        // Show loading state
        setLoadingState(true);

        // Proceed with registration
        if (imageUri != null) {
            uploadImageToStorage(firstName, lastName, email, registrationNo, password, department, section, course, semester);
        } else {
            registerUser(firstName, lastName, email, registrationNo, password, null, department, section, course, semester);
        }
    }

    private void registerUser(String firstName, String lastName, String email, String registrationNo, String password, String imageUrl, String department, String section, String course, String semester) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            storeUserInDatabase(user.getUid(), firstName, lastName, email, registrationNo, imageUrl, department, section, course, semester);
                        }
                    } else {
                        Toast.makeText(voters_signup.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        setLoadingState(false); // Reset loading state on failure
                    }
                });
    }

    private void storeUserInDatabase(String uid, String firstName, String lastName, String email, String registrationNo, String imageUrl, String department, String section, String course, String semester) {
        Voter voter = new Voter(firstName, lastName, email, registrationNo, imageUrl, department, section, course, semester, "voter");

        dbRef.child(uid).setValue(voter)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(voters_signup.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(voters_signup.this, voter_registered_successfully.class));
                    } else {
                        Toast.makeText(voters_signup.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                    }
                    setLoadingState(false); // Reset loading state after success/failure
                });
    }

    private void uploadImageToStorage(String firstName, String lastName, String email, String registrationNo, String password, String department, String section, String course, String semester) {
        if (imageUri != null) {
            StorageReference fileRef = storageRef.child(System.currentTimeMillis() + ".jpg");
            fileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        registerUser(firstName, lastName, email, registrationNo, password, imageUrl, department, section, course, semester);
                    }))
                    .addOnFailureListener(e -> {
                        Toast.makeText(voters_signup.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        setLoadingState(false); // Reset loading state on failure
                    });
        }
    }

    private void setLoadingState(boolean isLoading) {
        if (isLoading) {
            signUpButton.setVisibility(View.GONE); // Hide the sign-up button
            progressBar.setVisibility(View.VISIBLE); // Show the progress bar
        } else {
            signUpButton.setVisibility(View.VISIBLE); // Show the sign-up button
            progressBar.setVisibility(View.GONE); // Hide the progress bar
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(voters_signup.this, voters_login.class);
        startActivity(intent);
        finish();
    }
}
