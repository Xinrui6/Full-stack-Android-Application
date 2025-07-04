package com.example.a2866777l_development_project.model;

import com.example.a2866777l_development_project.util.Utils;
import com.google.firebase.Timestamp;

import java.util.ArrayList;

public class TimelineItem {
    private String userId;
    private String username;
    private String restaurantName;
    private String type;
    private String mainFlavor = null; // For reviews
    private boolean recommend; // For reviews
    private ArrayList<String> signatureDishes = null; // For reviews
    private String timestamp;

    public TimelineItem() {}

    // Constructor for check-ins
    public TimelineItem(String userId, String username, String restaurantName, Timestamp timestamp) {
        this.userId = userId;
        this.username = username;
        this.restaurantName = restaurantName;
        this.type = "checkin";
        this.timestamp = Utils.convertTimeStamp(timestamp);
    }

    // Constructor for reviews
    public TimelineItem(Review review, String username) {
        this.userId = review. getUserId();
        this.username = username;
        this.restaurantName = review.getRestaurantName();
        this.type = "review";
        this.mainFlavor = review.getMain_flavors();
        this.recommend = review.getRecommendation();
        this.timestamp = Utils.convertTimeStamp(review.getTimestamp());
        this.signatureDishes = review.getSignature_dishes();
    }

    public TimelineItem(String userId, String username, String restaurantName, String type, String mainFlavor, boolean recommend, ArrayList<String> signatureDishes, String timestamp) {
        this.userId = userId;
        this.username = username;
        this.restaurantName = restaurantName;
        this.type = type;
        this.mainFlavor = mainFlavor;
        this.signatureDishes = signatureDishes;
        this.recommend = recommend;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }
    public String getUsername() {
        return username;
    }
    public String getRestaurantName() {
        return restaurantName;
    }
    public String getType() {
        return type;
    }
    public String getMainFlavor() {
        return mainFlavor;
    }
    public boolean getRecommend() {
        return recommend;
    }
    public String getText() {
        if (type.equals("checkin")) {
            return username + " checked in at " + restaurantName;
        } else {
            return username + " left a review for " + restaurantName + ": \n"
                    + "Main Flavor: " + mainFlavor +
                    "\nRecommendation: " + (recommend ? "Yes" : "No") +
                    "\nSignature Dishes: " + signatureDishes.toString().replace("[", "").replace("]", "");
        }
    }
    public ArrayList<String> getSignatureDishes() {
        return signatureDishes;
    }
    public String getTimestamp() {
        return timestamp;
    }
}
