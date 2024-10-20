package com.example.mec.admin;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mec.R;
import com.example.mec.services.Elections;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class adminCreateElections extends AppCompatActivity {

    private EditText electionTitleInput, electionDescriptionInput, electionDateInput, electionTimeInput, electionSectionInput, electionDepartmentInput;
    private Button createElectionButton;
    private DatabaseReference databaseReference;

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

        // Date Picker for Election Date
        electionDateInput.setOnClickListener(v -> showDatePickerDialog());

        // Time Picker for Election Time
        electionTimeInput.setOnClickListener(v -> showTimePickerDialog());

        // Handle create election button click
        createElectionButton.setOnClickListener(v -> showConfirmationDialog());
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
                    // Set selected date to EditText
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
                    // Set selected time to EditText
                    String time = String.format("%02d:%02d", selectedHour, selectedMinute);
                    electionTimeInput.setText(time);
                },
                hour, minute, true);
        timePickerDialog.show();
    }

    // Show confirmation dialog before submitting data
    private void showConfirmationDialog() {
        // Validate inputs before showing the dialog
        if (!validateInputs()) {
            return;
        }

        // Show a Material Design confirmation dialog
        new MaterialAlertDialogBuilder(adminCreateElections.this)
                .setTitle("Confirm Submission")
                .setMessage("Are you sure you want to create this election?")
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing, just dismiss the dialog
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle negative button click (optional)
                        Toast.makeText(adminCreateElections.this, "Election creation declined", Toast.LENGTH_SHORT).show();
                    }
                })
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Proceed with saving data
                        saveElectionData();
                    }
                })
                .show();
    }

    // Method to save election data to Firebase
    private void saveElectionData() {
        String title = electionTitleInput.getText().toString().trim();
        String description = electionDescriptionInput.getText().toString().trim();
        String date = electionDateInput.getText().toString().trim();
        String time = electionTimeInput.getText().toString().trim();
        String section = electionSectionInput.getText().toString().trim();
        String department = electionDepartmentInput.getText().toString().trim();

        // Generate a unique key for each election
        String electionId = databaseReference.push().getKey();

        // Create an Election object
        Elections election = new Elections(title, description, date, time, section, department);

        // Save the election object to Firebase
        if (electionId != null) {
            databaseReference.child(electionId).setValue(election).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(adminCreateElections.this, "Election created successfully", Toast.LENGTH_LONG).show();
                    clearInputs();
                } else {
                    Toast.makeText(adminCreateElections.this, "Failed to create election", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    // Validate form inputs
    private boolean validateInputs() {
        String title = electionTitleInput.getText().toString().trim();
        String description = electionDescriptionInput.getText().toString().trim();
        String date = electionDateInput.getText().toString().trim();
        String time = electionTimeInput.getText().toString().trim();
        String section = electionSectionInput.getText().toString().trim();
        String department = electionDepartmentInput.getText().toString().trim();

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

    // Clear input fields after submission
    private void clearInputs() {
        electionTitleInput.setText("");
        electionDescriptionInput.setText("");
        electionDateInput.setText("");
        electionTimeInput.setText("");
        electionSectionInput.setText("");
        electionDepartmentInput.setText("");
    }
}
