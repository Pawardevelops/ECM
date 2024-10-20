package com.example.mec.admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.mec.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class adminSignup extends AppCompatActivity {

    private EditText firstName, lastName, department, course, section, semester, email, registrationNo, password, confirmPassword;
    private ImageView profileImage;
    private AppCompatButton signUpButton;
    private ProgressBar progressBar;

    private Uri imageUri;  // To store the URI of the selected image
    private static final int PICK_IMAGE_REQUEST = 1;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_signup);

        // Initialize Firebase Auth, Database, and Storage
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Admins");
        storageReference = FirebaseStorage.getInstance().getReference("profile_images");

        // Initialize input fields
        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        department = findViewById(R.id.department);
        course = findViewById(R.id.course);
        section = findViewById(R.id.section);
        semester = findViewById(R.id.semester);
        email = findViewById(R.id.email);
        registrationNo = findViewById(R.id.registration_no);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirm_password);
        profileImage = findViewById(R.id.profile_image);
        signUpButton = findViewById(R.id.candidateSignUp);
        progressBar = findViewById(R.id.progressBar);

        // Handle image upload (opening gallery)
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        // Handle sign up button click
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerAdmin();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImage.setImageURI(imageUri);  // Preview the selected image
        }
    }

    private void registerAdmin() {
        // Show the progress bar and hide the sign-up button
        progressBar.setVisibility(View.VISIBLE);
        signUpButton.setVisibility(View.GONE);

        // Get text from input fields
        String firstNameInput = firstName.getText().toString().trim();
        String lastNameInput = lastName.getText().toString().trim();
        String departmentInput = department.getText().toString().trim();
        String courseInput = course.getText().toString().trim();
        String sectionInput = section.getText().toString().trim();
        String semesterInput = semester.getText().toString().trim();
        String emailInput = email.getText().toString().trim();
        String registrationNoInput = registrationNo.getText().toString().trim();
        String passwordInput = password.getText().toString().trim();
        String confirmPasswordInput = confirmPassword.getText().toString().trim();

        // Input validation
        if (!validateInput(firstNameInput, lastNameInput, emailInput, passwordInput, confirmPasswordInput)) {
            progressBar.setVisibility(View.GONE);
            signUpButton.setVisibility(View.VISIBLE);
            return;
        }

        // Create a new user with email and password
        firebaseAuth.createUserWithEmailAndPassword(emailInput, passwordInput).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // If sign-up is successful, upload the profile image
                    uploadProfileImage(firstNameInput, lastNameInput, departmentInput, courseInput, sectionInput, semesterInput, emailInput, registrationNoInput);
                } else {
                    Toast.makeText(adminSignup.this, "Sign-up failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    signUpButton.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void uploadProfileImage(String firstName, String lastName, String department, String course, String section, String semester, String email, String registrationNo) {
        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".jpg");
            fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();
                            // Store admin details in the Realtime Database
                            storeAdminInDatabase(firstName, lastName, department, course, section, semester, email, registrationNo, imageUrl);

                            Intent intent = new Intent(adminSignup.this,AdminSignin.class);
                            startActivity(intent);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(adminSignup.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    signUpButton.setVisibility(View.VISIBLE);
                }
            });
        } else {
            Toast.makeText(this, "Please upload a profile image", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            signUpButton.setVisibility(View.VISIBLE);
        }
    }

    private boolean validateInput(String firstName, String lastName, String email, String password, String confirmPassword) {
        if (TextUtils.isEmpty(firstName)) {
            this.firstName.setError("First name is required");
            return false;
        }

        if (TextUtils.isEmpty(lastName)) {
            this.lastName.setError("Last name is required");
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            this.email.setError("Email is required");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            this.password.setError("Password is required");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            this.confirmPassword.setError("Passwords do not match");
            return false;
        }

        return true;
    }

    private void storeAdminInDatabase(String firstName, String lastName, String department, String course, String section, String semester, String email, String registrationNo, String imageUrl) {
        // Get the currently authenticated user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            Map<String, Object> adminDetails = new HashMap<>();
            adminDetails.put("firstName", firstName);
            adminDetails.put("lastName", lastName);
            adminDetails.put("department", department);
            adminDetails.put("course", course);
            adminDetails.put("section", section);
            adminDetails.put("semester", semester);
            adminDetails.put("email", email);
            adminDetails.put("registrationNo", registrationNo);
            adminDetails.put("imageUrl", imageUrl);
            adminDetails.put("role", "Admin");

            databaseReference.child(userId).setValue(adminDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(adminSignup.this, "Admin registered successfully!", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        signUpButton.setVisibility(View.VISIBLE);
                        resetInputFields();
                    } else {
                        Toast.makeText(adminSignup.this, "Registration failed", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        signUpButton.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
            Toast.makeText(this, "User is not authenticated", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            signUpButton.setVisibility(View.VISIBLE);
        }
    }

    private void resetInputFields() {
        firstName.setText("");
        lastName.setText("");
        department.setText("");
        course.setText("");
        section.setText("");
        semester.setText("");
        email.setText("");
        registrationNo.setText("");
        password.setText("");
        confirmPassword.setText("");
        profileImage.setImageResource(R.drawable.profile);  // Reset to default image
    }
}
