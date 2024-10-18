package com.example.mec.voters;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mec.R;
import com.example.mec.services.NavigationService;

public class voters_section_menu_result extends AppCompatActivity {
    private LinearLayout classAResult, classBResult, classCResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_voters_section_menu_result);

        classAResult = findViewById(R.id.classAResult);
        classBResult = findViewById(R.id.classBResult);
        classCResult = findViewById(R.id.classCResult);

        // Set click listeners for navigation
        classBResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationService.navigateToActivity(voters_section_menu_result.this,voters_allCandidate_result.class);
            }
        });


    }
}