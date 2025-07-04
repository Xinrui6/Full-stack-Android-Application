package com.example.a2866777l_development_project.Repository;


import com.example.a2866777l_development_project.model.TimelineItem;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class UserRepository {
    private final FirebaseFirestore firestore;

    public UserRepository() {
        firestore = FirebaseFirestore.getInstance();
    }

    public Task<DocumentSnapshot> getUserProfile(String userId) {
        return firestore.collection("users").document(userId).get();
    }

    public void addFlavorToUser(String userId, String flavor) {
        firestore.collection("users").document(userId).update("flavor", FieldValue.arrayUnion(flavor));
    }

    public void removeFlavorFromUser(String userId, String flavor) {
        firestore.collection("users").document(userId).update("flavor", FieldValue.arrayRemove(flavor));
    }

    public void updateUsername(String userId, String username) {
        firestore.collection("users").document(userId).update("username", username);
    }

    public void updateHomeTown(String userId, String homeTown) {
        firestore.collection("users").document(userId).update("homeTown", homeTown);
    }

    public void updateBio(String userId, String bio) {
        firestore.collection("users").document(userId).update("biography", bio);
    }

    public void addCityList(String userId, String city, ArrayList<String> cityList) {
        firestore.collection("users").document(userId).update("cityLists." + city, cityList);
    }

    public void addCheckIn(String userId, String checkIn) {
        firestore.collection("users").document(userId).update("checkIns", FieldValue.arrayUnion(checkIn));
    }

    public void addFollowing(String userId, String followingId) {
        firestore.collection("users").document(userId).update("following", FieldValue.arrayUnion(followingId));
    }

    public void addFollower(String userId, String followerId) {
        firestore.collection("users").document(userId).update("followers", FieldValue.arrayUnion(followerId));
    }

    public void removeCheckIn(String userId, String checkIn) {
        firestore.collection("users").document(userId).update("checkIns", FieldValue.arrayRemove(checkIn));
    }

    public void removeResFromList(String userId, String city, String resId) {
        firestore.collection("users").document(userId).update("cityLists." + city, FieldValue.arrayRemove(resId));
    }

    public void removeCityFromUser(String userId, String city) {
        firestore.collection("users").document(userId).update("cityLists." + city, FieldValue.delete());
    }

    public void addTimelineToUser(String userId, TimelineItem timeline) {
        firestore.collection("users").document(userId).update("timeline", FieldValue.arrayUnion(timeline));

    }

    public void addProfileImage(String userId, String imageUrl) {
        firestore.collection("users").document(userId).update("profileImage", imageUrl);

    }

}