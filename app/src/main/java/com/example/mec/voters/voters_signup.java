package com.example.mec.voters;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mec.R;
import com.example.mec.services.Voter;
import com.example.mec.voters.voter_registered_successfully;
import com.example.mec.voters.voters_login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class voters_signup extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1; // Constant for image selection
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private StorageReference storageRef;

    private EditText firstNameEditText, lastNameEditText, emailEditText, registrationNoEditText, passwordEditText, confirmPasswordEditText;
    private EditText departmentEditText, sectionEditText, courseEditText, semesterEditText; // Add semester EditText
    private ImageView profileImageView;
    private ProgressBar progressBar;

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
        departmentEditText = findViewById(R.id.department);
        sectionEditText = findViewById(R.id.section);
        courseEditText = findViewById(R.id.course);
        semesterEditText = findViewById(R.id.semester); // Initialize semester EditText
        profileImageView = findViewById(R.id.profile_image);
        progressBar = findViewById(R.id.progressBar);

        // Set up listeners
        findViewById(R.id.upload_image).setOnClickListener(v -> selectImage());
        findViewById(R.id.candidateSignUp).setOnClickListener(v -> validateAndRegister());
        findViewById(R.id.candidate_login).setOnClickListener(v -> navigateToLogin());
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*"); // Set the type of content to be selected
        intent.setAction(Intent.ACTION_GET_CONTENT); // Open gallery
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData(); // Get the URI of the selected image
            profileImageView.setImageURI(imageUri); // Set the selected image to ImageView
        }
    }

    private void validateAndRegister() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String registrationNo = registrationNoEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        String department = departmentEditText.getText().toString().trim();
        String section = sectionEditText.getText().toString().trim();
        String course = courseEditText.getText().toString().trim();
        String semester = semesterEditText.getText().toString().trim(); // Get the semester

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || registrationNo.isEmpty() ||
                password.isEmpty() || confirmPassword.isEmpty() || department.isEmpty() || section.isEmpty() || course.isEmpty() || semester.isEmpty()) { // Check if semester is empty
            Toast.makeText(voters_signup.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(confirmPassword)) {
            Toast.makeText(voters_signup.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            if (imageUri != null) {
                uploadImageToStorage(firstName, lastName, email, registrationNo, password, department, section, course, semester); // Pass semester to the method
            } else {
                registerUser(firstName, lastName, email, registrationNo, password, null, department, section, course, semester); // Pass semester to the method
            }
        }
    }

    private void registerUser(String firstName, String lastName, String email, String registrationNo, String password, String imageUrl, String department, String section, String course, String semester) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            storeUserInDatabase(user.getUid(), firstName, lastName, email, registrationNo, imageUrl, department, section, course, semester); // Pass semester to the method
                        }
                    } else {
                        Toast.makeText(voters_signup.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void storeUserInDatabase(String uid, String firstName, String lastName, String email, String registrationNo, String imageUrl, String department, String section, String course, String semester) {
        Voter voter = new Voter(firstName, lastName, email, registrationNo, imageUrl, department, section, course, semester, "voter"); // Update Voter constructor

        dbRef.child(uid).setValue(voter)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(voters_signup.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(voters_signup.this, voter_registered_successfully.class));
                    } else {
                        Toast.makeText(voters_signup.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                });
    }

    private void uploadImageToStorage(String firstName, String lastName, String email, String registrationNo, String password, String department, String section, String course, String semester) {
        if (imageUri != null) {
            StorageReference fileRef = storageRef.child(System.currentTimeMillis() + ".jpg");
            fileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        registerUser(firstName, lastName, email, registrationNo, password, imageUrl, department, section, course, semester); // Pass semester to the method
                    }))
                    .addOnFailureListener(e -> {
                        Toast.makeText(voters_signup.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    });
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(voters_signup.this, voters_login.class);
        startActivity(intent);
        finish();
    }
}
