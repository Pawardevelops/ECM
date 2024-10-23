package com.example.mec.admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

    private AutoCompleteTextView department, course, section, semester;
    private EditText firstName, lastName, email, registrationNo, password, confirmPassword;
    private ImageView profileImage;
    private AppCompatButton signUpButton;
    private ProgressBar progressBar;

    private Uri imageUri;
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

        // Set up dropdown spinners using arrays from resources
        setUpDropdowns();

        // Handle image upload (opening gallery)
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        // Handle sign-up button click
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerAdmin();
            }
        });

        // Set focus listeners for each field to perform validation
        setFocusListeners();
    }

    private void setUpDropdowns() {
        ArrayAdapter<CharSequence> departmentAdapter = ArrayAdapter.createFromResource(this, R.array.departments, android.R.layout.simple_dropdown_item_1line);
        department.setAdapter(departmentAdapter);

        ArrayAdapter<CharSequence> courseAdapter = ArrayAdapter.createFromResource(this, R.array.courses, android.R.layout.simple_dropdown_item_1line);
        course.setAdapter(courseAdapter);

        ArrayAdapter<CharSequence> sectionAdapter = ArrayAdapter.createFromResource(this, R.array.sections, android.R.layout.simple_dropdown_item_1line);
        section.setAdapter(sectionAdapter);

        ArrayAdapter<CharSequence> semesterAdapter = ArrayAdapter.createFromResource(this, R.array.semesters, android.R.layout.simple_dropdown_item_1line);
        semester.setAdapter(semesterAdapter);
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
            profileImage.setImageURI(imageUri);
        }
    }

    private void registerAdmin() {
        progressBar.setVisibility(View.VISIBLE);
        signUpButton.setVisibility(View.GONE);

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

        // Validate inputs on register click
        if (!validateInput(firstNameInput, lastNameInput, departmentInput, courseInput, sectionInput, semesterInput, emailInput, registrationNoInput, passwordInput, confirmPasswordInput)) {
            progressBar.setVisibility(View.GONE);
            signUpButton.setVisibility(View.VISIBLE);
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(emailInput, passwordInput).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    uploadProfileImage(firstNameInput, lastNameInput, departmentInput, courseInput, sectionInput, semesterInput, emailInput, registrationNoInput);
                } else {
                    Toast.makeText(adminSignup.this, "Sign-up failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    signUpButton.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setFocusListeners() {
        // First Name
        firstName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && TextUtils.isEmpty(firstName.getText().toString().trim())) {
                firstName.setError("First name is required");
            }
        });

        // Last Name
        lastName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && TextUtils.isEmpty(lastName.getText().toString().trim())) {
                lastName.setError("Last name is required");
            }
        });

        // Email
        email.setOnFocusChangeListener((v, hasFocus) -> {
            String emailInput = email.getText().toString().trim();
            if (!hasFocus && (TextUtils.isEmpty(emailInput) || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailInput).matches())) {
                email.setError("Valid email is required");
            }
        });

        // Registration No (validate for 9 digits)
        registrationNo.setOnFocusChangeListener((v, hasFocus) -> {
            String regNoInput = registrationNo.getText().toString().trim();
            if (!hasFocus && (TextUtils.isEmpty(regNoInput) || regNoInput.length() != 9 || !regNoInput.matches("\\d{9}"))) {
                registrationNo.setError("Registration number must be exactly 9 digits");
            }
        });

        // Password
        password.setOnFocusChangeListener((v, hasFocus) -> {
            String passwordInput = password.getText().toString().trim();
            if (!hasFocus) {
                if (TextUtils.isEmpty(passwordInput)) {
                    password.setError("Password is required");
                } else if (passwordInput.length() < 8) {
                    password.setError("Password must be at least 8 characters long");
                } else if (!passwordInput.matches(".*[A-Z].*")) {
                    password.setError("Password must contain at least one uppercase letter");
                } else if (!passwordInput.matches(".*[a-z].*")) {
                    password.setError("Password must contain at least one lowercase letter");
                } else if (!passwordInput.matches(".*\\d.*")) {
                    password.setError("Password must contain at least one number");
                } else if (!passwordInput.matches(".*[@#\\$%^&+=!].*")) {
                    password.setError("Password must contain at least one special character");
                }
            }
        });

        // Confirm Password
        confirmPassword.setOnFocusChangeListener((v, hasFocus) -> {
            String confirmPasswordInput = confirmPassword.getText().toString().trim();
            if (!hasFocus && !confirmPasswordInput.equals(password.getText().toString().trim())) {
                confirmPassword.setError("Passwords do not match");
            }
        });
    }

    private boolean validateInput(String firstName, String lastName, String department, String course, String section, String semester, String email, String registrationNo, String password, String confirmPassword) {
        // Check if all fields are filled
        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(department) || TextUtils.isEmpty(course)
                || TextUtils.isEmpty(section) || TextUtils.isEmpty(semester) || TextUtils.isEmpty(email)
                || TextUtils.isEmpty(registrationNo) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return false;
        }
        // Password match
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void uploadProfileImage(String firstName, String lastName, String department, String course, String section, String semester, String email, String registrationNo) {
        if (imageUri != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String profileImageUrl = uri.toString();
                            saveUserToDatabase(firstName, lastName, department, course, section, semester, email, registrationNo, profileImageUrl);
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
            saveUserToDatabase(firstName, lastName, department, course, section, semester, email, registrationNo, null);
        }
    }

    private void saveUserToDatabase(String firstName, String lastName, String department, String course, String section, String semester, String email, String registrationNo, String profileImageUrl) {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String userId = firebaseUser.getUid();

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("firstName", firstName);
        userMap.put("lastName", lastName);
        userMap.put("department", department);
        userMap.put("course", course);
        userMap.put("section", section);
        userMap.put("semester", semester);
        userMap.put("email", email);
        userMap.put("registrationNo", registrationNo);
        userMap.put("profileImageUrl", profileImageUrl);

        databaseReference.child(userId).setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(adminSignup.this, "Registration successful", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    signUpButton.setVisibility(View.VISIBLE);
                    finish(); // Close the activity
                } else {
                    Toast.makeText(adminSignup.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    signUpButton.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private String getFileExtension(Uri uri) {
        return uri.getPath().substring(uri.getPath().lastIndexOf(".") + 1);
    }
}
