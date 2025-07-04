package com.example.a2866777l_development_project.Repository;

import com.example.a2866777l_development_project.model.Review;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class ReviewRepository {
    private final FirebaseFirestore firestore;

    public ReviewRepository() {
        firestore = FirebaseFirestore.getInstance();
    }

    public void addReview(Review review) {
        firestore.collection("reviews").add(review);
    }

    public Task<QuerySnapshot> getUserReviews(String userId) {
        return firestore.collection("reviews").whereEqualTo("userId", userId).get();
    }
    public Task<QuerySnapshot> getRestaurantReviews(String restaurantId) {
        return firestore.collection("reviews").whereEqualTo("restaurantId", restaurantId).get();
    }


}
