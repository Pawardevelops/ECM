package com.example.mec;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        AppCompatButton adminButton = findViewById(R.id.enterAdminBttn);
        AppCompatButton candidateButton = findViewById(R.id.enterCandidateBttn);
        AppCompatButton voterButton = findViewById(R.id.enterVoterBttn);

        // Set onClickListeners for each button
        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to AdminLoginActivity
                Intent intent = new Intent(MainActivity.this, adminlogin.class);
                startActivity(intent);
            }
        });

        candidateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to CandidateLoginActivity
                Intent intent = new Intent(MainActivity.this, candidateLogin.class);
                startActivity(intent);
            }
        });

        voterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to VoterLoginActivity
                Intent intent = new Intent(MainActivity.this, voters_login.class);
                startActivity(intent);
            }
        });
    }
}