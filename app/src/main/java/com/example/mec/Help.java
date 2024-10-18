package com.example.mec;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Help extends AppCompatActivity {

    private LinearLayout faqItem1, faqItem2;
    private TextView answer1, answer2;
    private ImageView toggleIcon1, toggleIcon2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);  // Make sure your XML is linked here

        // FAQ Item 1
        faqItem1 = findViewById(R.id.faq_item_1);
        answer1 = findViewById(R.id.answer_1);
        toggleIcon1 = findViewById(R.id.toggle_icon_1);

        faqItem1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (answer1.getVisibility() == View.GONE) {
                    answer1.setVisibility(View.VISIBLE);
                    toggleIcon1.setImageResource(R.drawable.ic_minus);  // Switch to "-" icon
                } else {
                    answer1.setVisibility(View.GONE);
                    toggleIcon1.setImageResource(R.drawable.ic_add);  // Switch back to "+" icon
                }
            }
        });

        // FAQ Item 2
        faqItem2 = findViewById(R.id.faq_item_2);
        answer2 = findViewById(R.id.answer_2);
        toggleIcon2 = findViewById(R.id.toggle_icon_2);

        faqItem2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (answer2.getVisibility() == View.GONE) {
                    answer2.setVisibility(View.VISIBLE);
                    toggleIcon2.setImageResource(R.drawable.ic_minus);  // Switch to "-" icon
                } else {
                    answer2.setVisibility(View.GONE);
                    toggleIcon2.setImageResource(R.drawable.ic_add);  // Switch back to "+" icon
                }
            }
        });
    }
}
