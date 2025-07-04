package com.example.a2866777l_development_project;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


import com.example.a2866777l_development_project.util.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class splash extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        db = FirebaseFirestore.getInstance();
        auth = Utils.getAuth();
        FirebaseUser currentUser = auth.getCurrentUser();
        initialize();
        screen();
        if (currentUser != null) {
            navigateToMainContent();
        } else {
            navigateToLogin();
        }
    }

    private void navigateToMainContent() {
        Intent intent = new Intent(splash.this, MainActivity.class); // Replace HomeActivity with your main content activity
        startActivity(intent);
        finish();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(splash.this, login.class);
        startActivity(intent);
        finish();
    }


    private void screen() {
        new Handler().postDelayed(() -> {
            finish();
        }, 3000);
    }

    private void initialize() {
        db.collection("reviews").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().isEmpty()) {
                    // The collection is empty or does not exist
                    WriteBatch batch = db.batch();
                    Map<String, Object> initialReview = new HashMap<>();
                    initialReview.put("userId", "dummyUserId");
                    initialReview.put("restaurantId", "dummyRestaurantId");
                    initialReview.put("recommendation",0);
                    initialReview.put("dis-recommendation", 0);
                    initialReview.put("signature dishes", new ArrayList<String>());
                    initialReview.put("timestamp", com.google.firebase.firestore.FieldValue.serverTimestamp());

                    batch.set(db.collection("reviews").document(), initialReview);
                    batch.commit();
                }
            }
        });

        db.collection("restaurants").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().isEmpty()) {
                    // The collection is empty or does not exist
                    WriteBatch batch = db.batch();
                    Map<String, Object> initialRestaurant = new HashMap<>();
                    initialRestaurant.put("resId", "dummyResId");
                    initialRestaurant.put("x", 0.0);
                    initialRestaurant.put("y",0.0);
                    initialRestaurant.put("address", "dummyAddress");
                    initialRestaurant.put("price_level", 0);

                    batch.set(db.collection("restaurants").document(), initialRestaurant);
                    batch.commit();
                }
            }
        });
    }
}