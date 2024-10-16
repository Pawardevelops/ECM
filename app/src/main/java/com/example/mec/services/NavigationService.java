package com.example.mec.services;

import android.content.Context;
import android.content.Intent;

public class NavigationService {

    // A reusable method for navigating between activities
    public static void navigateToActivity(Context context, Class<?> destinationActivity) {
        Intent intent = new Intent(context, destinationActivity);
        context.startActivity(intent);
    }
}
