package com.example.a2866777l_development_project.model;

import com.google.firebase.Timestamp;
import java.util.ArrayList;

public class Review {
    private String userId;
    private String restaurantId;
    private String restaurantName;
    Boolean recommendation;
    private String main_flavors;
    private ArrayList<String> signature_dishes;
    private Timestamp timestamp;
    private ArrayList<String> photoUrls;

    public Review() {
    }

    public Review(String userId, String restaurantId, String restaurantName, boolean recommendation,String main_flavors, ArrayList<String> signature_dishes, ArrayList<String> photoUrls) {
        this.userId = userId;
        this.restaurantName = restaurantName;
        this.restaurantId = restaurantId;
        this.recommendation = recommendation;
        this.main_flavors = main_flavors;
        this.signature_dishes = signature_dishes;
        this.timestamp = Timestamp.now();
        this.photoUrls = photoUrls;
    }

    public String getUserId() {
        return userId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public boolean getRecommendation() {
        return recommendation;
    }

    public String getMain_flavors() { return main_flavors; }

    public ArrayList<String> getSignature_dishes() { return signature_dishes; }

    public Timestamp getTimestamp() {
        return this.timestamp;
    }

    public ArrayList<String> getPhotoUrls() { return photoUrls; }


}
