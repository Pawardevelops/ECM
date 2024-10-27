package com.example.mec;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Help extends AppCompatActivity {

    // Declare FAQ items and answers
    private LinearLayout faqItem1, faqItem2, faqItem3, faqItem4, faqItem5, faqItem6, faqItem7, faqItem8, faqItem9;
    private TextView answer1, answer2, answer3, answer4, answer5, answer6, answer7, answer8, answer9;
    private ImageView toggleIcon1, toggleIcon2, toggleIcon3, toggleIcon4, toggleIcon5, toggleIcon6, toggleIcon7, toggleIcon8, toggleIcon9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        // Initialize each FAQ item and its components
        setupFaqItem(R.id.faq_item_1, R.id.answer_1, R.id.toggle_icon_1);
        setupFaqItem(R.id.faq_item_2, R.id.answer_2, R.id.toggle_icon_2);
        setupFaqItem(R.id.faq_item_3, R.id.answer_3, R.id.toggle_icon_3);
        setupFaqItem(R.id.faq_item_4, R.id.answer_4, R.id.toggle_icon_4);
        setupFaqItem(R.id.faq_item_5, R.id.answer_5, R.id.toggle_icon_5);
        setupFaqItem(R.id.faq_item_6, R.id.answer_6, R.id.toggle_icon_6);
        setupFaqItem(R.id.faq_item_7, R.id.answer_7, R.id.toggle_icon_7);
        setupFaqItem(R.id.faq_item_8, R.id.answer_8, R.id.toggle_icon_8);
        setupFaqItem(R.id.faq_item_9, R.id.answer_9, R.id.toggle_icon_9);
    }

    private void setupFaqItem(int faqItemId, int answerId, int toggleIconId) {
        // Set up each FAQ item, answer, and toggle icon
        LinearLayout faqItem = findViewById(faqItemId);
        TextView answer = findViewById(answerId);
        ImageView toggleIcon = findViewById(toggleIconId);

        // Set click listener to toggle visibility of answer and icon change
        faqItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (answer.getVisibility() == View.GONE) {
                    answer.setVisibility(View.VISIBLE);
                    toggleIcon.setImageResource(R.drawable.ic_minus);  // Switch to "-" icon
                } else {
                    answer.setVisibility(View.GONE);
                    toggleIcon.setImageResource(R.drawable.ic_add);  // Switch back to "+" icon
                }
            }
        });
    }
}
