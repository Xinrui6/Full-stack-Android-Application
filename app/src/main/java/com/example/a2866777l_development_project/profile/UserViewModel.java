package com.example.a2866777l_development_project.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.a2866777l_development_project.Repository.ReviewRepository;
import com.example.a2866777l_development_project.Repository.UserRepository;
import com.example.a2866777l_development_project.model.Review;
import com.example.a2866777l_development_project.util.Utils;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class UserViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private MutableLiveData<String> username = new MutableLiveData<>();

    private MutableLiveData<String> hometown = new MutableLiveData<>();
    private MutableLiveData<String> followers = new MutableLiveData<>();
    private MutableLiveData<String> following = new MutableLiveData<>();
    private MutableLiveData<String> biography = new MutableLiveData<>();
    private MutableLiveData<ArrayList<String>> flavors = new MutableLiveData<>();
    private MutableLiveData<HashMap<String, ArrayList<String>>> lists = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Review>> userReviews = new MutableLiveData<>();
    private MutableLiveData<ArrayList<String>> checkinsList = new MutableLiveData<>();
    private MutableLiveData<String> profileImage = new MutableLiveData<>();

    public UserViewModel() {

        userRepository = Utils.getUserRepository();
        reviewRepository = new ReviewRepository();


    }

    public void getUserData(String userId) {
        userRepository.getUserProfile(userId).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                ArrayList<String> followingCount = documentSnapshot.get("following") == null ? new ArrayList<>() : (ArrayList<String>)documentSnapshot.get("following");
                ArrayList<String> followerCount = documentSnapshot.get("followers") == null ? new ArrayList<>() : (ArrayList<String>)documentSnapshot.get("followers");
                String hometown = documentSnapshot.getString("homeTown");
                String biography = documentSnapshot.getString("biography");
                HashMap<String, ArrayList<String>> cityLists = (HashMap<String, ArrayList<String>>) documentSnapshot.get("cityLists");
                ArrayList<String> userFlavors = (ArrayList<String>) documentSnapshot.get("flavor");
                ArrayList<String> checkins = documentSnapshot.get("checkins") == null ? new ArrayList<>() : (ArrayList<String>)documentSnapshot.get("checkIns");
                String profileImageURL = documentSnapshot.getString("profileImage");
                //set values
                username.setValue("Hello, " + documentSnapshot.getString("username"));
                setOptionalInfo(hometown, biography, cityLists);
                following.setValue(String.valueOf(followingCount.size()));
                followers.setValue(String.valueOf(followerCount.size()));
                checkinsList.setValue(checkins);
                profileImage.setValue(profileImageURL);
                setFlavor(userFlavors);
                setReview(userId);
            } else {
                    username.setValue("User not found");
            }
        });
    }

    private void setOptionalInfo(String hometown, String biography, HashMap<String, ArrayList<String>> cityLists) {
        if (hometown != null) this.hometown.setValue(hometown);
        else this.hometown.setValue("");

        if (biography != null) this.biography.setValue(biography);
        else this.biography.setValue("");

        if (cityLists != null) lists.setValue(cityLists);
        else this.lists.setValue(new HashMap<>());
    }

    private void setFlavor(ArrayList<String> flavors) {
        if (flavors != null) {
            this.flavors.setValue(flavors);
        } else {
            this.flavors.setValue(new ArrayList<>());
        }
    }

    private void setReview(String userId) {
        reviewRepository.getUserReviews(userId).addOnSuccessListener(queryDocumentSnapshots -> {
            ArrayList<Review> reviewList = new ArrayList<>();
            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                reviewList.add(document.toObject(Review.class));
            }
            userReviews.setValue(reviewList);

        });
    }



   public LiveData<String> getUsername() {
        return username;
    }

    public LiveData<String> getBiography() {
        return biography;
    }

    public LiveData<String> getFollower() {
        return followers;
    }

    public LiveData<String> getHomeTown() {
        return hometown;
    }

    public LiveData<String> getFollowing() {
        return following;
    }

    public LiveData<ArrayList<String>> getFlavor() {
        return flavors;
    }

    public LiveData<ArrayList<Review>> getReviews() {
        return userReviews;
    }

    public LiveData<ArrayList<String>> getCheckins() { return checkinsList; }

    public LiveData<String> getProfileImage() { return profileImage; }


}