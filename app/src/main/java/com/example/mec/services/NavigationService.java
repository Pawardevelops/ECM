package com.example.mec.services;

import android.content.Context;
import android.content.Intent;

public class NavigationService {

    // A reusable method for navigating between activities
    public static void navigateToActivity(Context context, Class<?> destinationActivity) {
        Intent intent = new Intent(context, destinationActivity);
        context.startActivity(intent);
    }

    public static void navigateToActivityAfterLogin(Context context, Class<?> destinationActivity) {
        Intent intent = new Intent(context, destinationActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}
