package com.example.mec.admin;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.mec.R;
import com.example.mec.services.Elections;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class adminCreateElections extends AppCompatActivity {

    private TextInputEditText electionTitleInput, electionDescriptionInput, electionDateInput, electionTimeInput;
    private AutoCompleteTextView electionSectionInput, electionDepartmentInput;
    private Button createElectionButton;
    private DatabaseReference databaseReference;
    private String electionId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_create_elections);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("elections");

        // Find views by ID
        electionTitleInput = findViewById(R.id.election_title_input);
        electionDescriptionInput = findViewById(R.id.election_description_input);
        electionDateInput = findViewById(R.id.election_date_input);
        electionTimeInput = findViewById(R.id.election_time_input);
        electionSectionInput = findViewById(R.id.election_type_input);
        electionDepartmentInput = findViewById(R.id.election_department_input);
        createElectionButton = findViewById(R.id.createElectionButton);

        // Set up dropdowns for Section and Department
        setupDropdowns();

        // Check if this is editing an existing election or creating a new one
        electionId = getIntent().getStringExtra("electionId");
        if (electionId != null) {
            fetchElectionData(electionId);
            createElectionButton.setText("Update Election");
        } else {
            createElectionButton.setText("Create Election");
        }

        // Date Picker for Election Date
        electionDateInput.setOnClickListener(v -> showDatePickerDialog());

        // Time Picker for Election Time
        electionTimeInput.setOnClickListener(v -> showTimePickerDialog());

        // Handle create/update election button click
        createElectionButton.setOnClickListener(v -> {
            if (electionId != null) {
                showConfirmationDialog("Update", "Are you sure you want to update this election?");
            } else {
                showConfirmationDialog("Create", "Are you sure you want to create this election?");
            }
        });

        // Set focus change listeners for real-time validation
        setFocusChangeListeners();
    }

    // Set up dropdowns for Section and Department
    private void setupDropdowns() {
        // ArrayAdapters for dropdown items
        ArrayAdapter<CharSequence> sectionAdapter = ArrayAdapter.createFromResource(this,
                R.array.sections, android.R.layout.simple_dropdown_item_1line);
        electionSectionInput.setAdapter(sectionAdapter);

        ArrayAdapter<CharSequence> departmentAdapter = ArrayAdapter.createFromResource(this,
                R.array.departments, android.R.layout.simple_dropdown_item_1line);
        electionDepartmentInput.setAdapter(departmentAdapter);
    }

    // Set focus change listeners to validate inputs when moving to another field
    private void setFocusChangeListeners() {
        electionTitleInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (TextUtils.isEmpty(electionTitleInput.getText().toString().trim())) {
                    electionTitleInput.setError("Title is required");
                }
            }
        });

        electionDescriptionInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (TextUtils.isEmpty(electionDescriptionInput.getText().toString().trim())) {
                    electionDescriptionInput.setError("Description is required");
                }
            }
        });

        electionDateInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (TextUtils.isEmpty(electionDateInput.getText().toString().trim())) {
                    electionDateInput.setError("Date is required");
                }
            }
        });

        electionTimeInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (TextUtils.isEmpty(electionTimeInput.getText().toString().trim())) {
                    electionTimeInput.setError("Time is required");
                }
            }
        });

        electionSectionInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (TextUtils.isEmpty(electionSectionInput.getText().toString().trim())) {
                    electionSectionInput.setError("Section is required");
                }
            }
        });

        electionDepartmentInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (TextUtils.isEmpty(electionDepartmentInput.getText().toString().trim())) {
                    electionDepartmentInput.setError("Department is required");
                }
            }
        });
    }

    // Method to show DatePickerDialog
    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                adminCreateElections.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    electionDateInput.setText(date);
                },
                year, month, day);
        datePickerDialog.show();
    }

    // Method to show TimePickerDialog
    private void showTimePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                adminCreateElections.this,
                (view, selectedHour, selectedMinute) -> {
                    String time = String.format("%02d:%02d", selectedHour, selectedMinute);
                    electionTimeInput.setText(time);
                },
                hour, minute, true);
        timePickerDialog.show();
    }

    // Fetch election data for editing
    private void fetchElectionData(String electionId) {
        databaseReference.child(electionId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Elections election = snapshot.getValue(Elections.class);
                    if (election != null) {
                        electionTitleInput.setText(election.getTitle());
                        electionDescriptionInput.setText(election.getDescription());
                        electionDateInput.setText(election.getDate());
                        electionTimeInput.setText(election.getTime());
                        electionSectionInput.setText(election.getSection());
                        electionDepartmentInput.setText(election.getDepartment());
                    }
                } else {
                    Toast.makeText(adminCreateElections.this, "Election not found.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(adminCreateElections.this, "Failed to load election data.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    // Show confirmation dialog before submitting data
    private void showConfirmationDialog(String action, String message) {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(adminCreateElections.this)
                .setTitle(action + " Election")
                .setMessage(message)
                .setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setNegativeButton("Decline", (dialog, which) -> Toast.makeText(adminCreateElections.this, action + " declined.", Toast.LENGTH_SHORT).show())
                .setPositiveButton(action, (dialog, which) -> saveElectionData())
                .show();
    }

    // Method to save or update election data to Firebase
    private void saveElectionData() {
        String title = electionTitleInput.getText().toString().trim();
        String description = electionDescriptionInput.getText().toString().trim();
        String date = electionDateInput.getText().toString().trim();
        String time = electionTimeInput.getText().toString().trim();
        String section = electionSectionInput.getText().toString().trim();
        String department = electionDepartmentInput.getText().toString().trim();

        // Validate input
        if (!validateInputs(title, description, date, time, section, department)) {
            return;
        }

        // Create or update election
        Elections election = new Elections(title, description, date, time, section, department);
        if (electionId == null) {
            electionId = databaseReference.push().getKey();
        }

        databaseReference.child(electionId).setValue(election).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(adminCreateElections.this, "Election saved successfully.", Toast.LENGTH_LONG).show();
                clearInputs();
            } else {
                Toast.makeText(adminCreateElections.this, "Failed to save election.", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Validate inputs
    private boolean validateInputs(String title, String description, String date, String time, String section, String department) {
        if (TextUtils.isEmpty(title)) {
            electionTitleInput.setError("Title is required");
            return false;
        }
        if (TextUtils.isEmpty(description)) {
            electionDescriptionInput.setError("Description is required");
            return false;
        }
        if (TextUtils.isEmpty(date)) {
            electionDateInput.setError("Date is required");
            return false;
        }
        if (TextUtils.isEmpty(time)) {
            electionTimeInput.setError("Time is required");
            return false;
        }
        if (TextUtils.isEmpty(section)) {
            electionSectionInput.setError("Section is required");
            return false;
        }
        if (TextUtils.isEmpty(department)) {
            electionDepartmentInput.setError("Department is required");
            return false;
        }
        return true;
    }

    // Clear input fields
    private void clearInputs() {
        electionTitleInput.setText("");
        electionDescriptionInput.setText("");
        electionDateInput.setText("");
        electionTimeInput.setText("");
        electionSectionInput.setText("");
        electionDepartmentInput.setText("");
    }
}
