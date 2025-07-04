package com.example.a2866777l_development_project.util;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.example.a2866777l_development_project.Repository.ImageRepository;
import com.example.a2866777l_development_project.Repository.UserRepository;
import com.example.a2866777l_development_project.model.Review;
import com.example.a2866777l_development_project.profile.ReviewsAdapter;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;


public class Utils {


    private static FirebaseAuth auth = FirebaseAuth.getInstance();
    private static UserRepository userRepository = new UserRepository();
    private static String userId;
    private static ImageRepository imageRepository = new ImageRepository();

//Check In Utils
    public static String getUserId() {
        userId = auth.getCurrentUser().getUid();
        return userId;
    }

    public static FirebaseAuth getAuth() {
        return auth;
    }
    public static UserRepository getUserRepository() {
        return userRepository;
    }
    public static ImageRepository getImageRepository() { return imageRepository; }

    public static void onCheckIn(Button checkInButton, Bundle markerData) {
        if (!markerData.getBoolean("checkInStatus")) {
            checkInButton.setEnabled(false);
            checkInButton.setText("Checked In");
            markerData.putBoolean("checkInStatus", true);
            saveCheckInToUser(markerData);
        }
    }

    private static void saveCheckInToUser(Bundle markerData) {
        String userId = auth.getCurrentUser().getUid();
        String resId = markerData.getString("id");
        userRepository.addCheckIn(userId, resId);
    }

    public static void checkCheckInStatus(Button checkInButton, String restaurantId) {
        userRepository.getUserProfile(userId).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                ArrayList<String> checkIns = (ArrayList<String>) documentSnapshot.get("checkIns");
                if (checkIns != null && checkIns.contains(restaurantId)) {
                    checkInButton.setEnabled(false);
                    checkInButton.setText("Checked In");
                } else {
                    checkInButton.setEnabled(true);
                    checkInButton.setText("Check In");
                }
            }
        })
        .addOnFailureListener(e -> {
            Log.e("CheckInUtils", "Error checking check-in status", e);
        });
    }


    //Review Utils
    public static void updateReviewsUI(ArrayList<Review> userReviews, ReviewsAdapter reviewsAdapter) {
        if (!userReviews.isEmpty()) {
            reviewsAdapter.updateReviews(userReviews);
        }
    }
    public static String convertTimeStamp(Timestamp timestamp) {
        long timestampInMillis = timestamp.getSeconds() * 1000L + timestamp.getNanoseconds() / 1000000L;
        Date date = new Date(timestampInMillis);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(date);
    }


}
