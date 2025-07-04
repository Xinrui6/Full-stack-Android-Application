package com.example.a2866777l_development_project.Repository;

import com.example.a2866777l_development_project.model.Restaurant;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class RestaurantRepository {

    private final FirebaseFirestore firestore;

    public RestaurantRepository() {
        firestore = FirebaseFirestore.getInstance();
    }

    public void addRestaurant(Restaurant restaurant) {
        firestore.collection("restaurants").document(restaurant.getResId())
            .set(restaurant);
    }

    public Task<DocumentSnapshot> getRestaurantById(String resId) {
        return firestore.collection("restaurants").document(resId).get();
    }

    public void updateResImages(String resId, ArrayList<String> photoUrls) {
        for (String photoUrl : photoUrls) {
            firestore.collection("restaurants").document(resId).update("photoUrls", FieldValue.arrayUnion(photoUrl));
        }
    }
}
