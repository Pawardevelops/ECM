package com.example.mec.candidate;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mec.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class candidateCampaing extends AppCompatActivity {

    private TextInputEditText sloganInput, agendaInput, promiseInput1;
    private Button saveSloganButton, saveAgendaButton, savePromisesButton;
    private DatabaseReference candidateRef;
    private String candidateId; // Current candidate's ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_candidate_campaing);

        // Initialize Firebase references
        candidateId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        candidateRef = FirebaseDatabase.getInstance().getReference("candidates").child(candidateId);

        // Initialize UI elements
        sloganInput = findViewById(R.id.election_slogan_input);
        agendaInput = findViewById(R.id.election_agenda_input);
        promiseInput1 = findViewById(R.id.promise_input_1);

        saveSloganButton = findViewById(R.id.saveButton);
        saveAgendaButton = findViewById(R.id.saveAgenda);
        savePromisesButton = findViewById(R.id.savePromises);

        // Set up button click listeners
        saveSloganButton.setOnClickListener(v -> saveSlogan());
        saveAgendaButton.setOnClickListener(v -> saveAgenda());
        savePromisesButton.setOnClickListener(v -> savePromises());
    }

    // Method to save the slogan to Firebase
    private void saveSlogan() {
        String slogan = sloganInput.getText().toString().trim();
        if (!slogan.isEmpty()) {
            candidateRef.child("slogan").setValue(slogan)
                    .addOnSuccessListener(aVoid -> Toast.makeText(candidateCampaing.this, "Slogan updated successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(candidateCampaing.this, "Failed to update slogan", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Slogan cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to save the agenda to Firebase
    private void saveAgenda() {
        String agenda = agendaInput.getText().toString().trim();
        if (!agenda.isEmpty()) {
            candidateRef.child("agenda").setValue(agenda)
                    .addOnSuccessListener(aVoid -> Toast.makeText(candidateCampaing.this, "Agenda saved successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(candidateCampaing.this, "Failed to save agenda", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Agenda cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to save the promises to Firebase
    private void savePromises() {
        String promise1 = promiseInput1.getText().toString().trim();
        if (!promise1.isEmpty()) {
            candidateRef.child("promises").child("promise1").setValue(promise1)
                    .addOnSuccessListener(aVoid -> Toast.makeText(candidateCampaing.this, "Promises saved successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(candidateCampaing.this, "Failed to save promises", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Promise cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }
}
