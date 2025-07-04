package com.example.a2866777l_development_project.post;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.a2866777l_development_project.Repository.UserRepository;
import com.example.a2866777l_development_project.model.TimelineItem;
import com.example.a2866777l_development_project.util.Utils;

import java.util.ArrayList;
import java.util.Map;

public class PostViewModel extends ViewModel {

    private final UserRepository userRepository;
    private MutableLiveData<ArrayList<TimelineItem>> timeline = new MutableLiveData<>();

    public PostViewModel() {
        userRepository = Utils.getUserRepository();
    }

    public void fetchTimeline(String userId) {
        userRepository.getUserProfile(userId).addOnSuccessListener(documentSnapshot -> {
            Log.d("Timeline", "User profile fetched successfully.");
            if (documentSnapshot.exists()) {
                ArrayList<Map<String, Object>> timelineMaps = (ArrayList<Map<String, Object>>) documentSnapshot.get("timeline");

                if (timelineMaps != null) {
                    ArrayList<TimelineItem> timelineItems = new ArrayList<>();

                    for (Map<String, Object> map : timelineMaps) {
                        TimelineItem item = convertMapToTimelineItem(map);
                        if (item != null) {
                            timelineItems.add(item);
                        } else {
                            Log.d("Timeline", "Failed to convert map to TimelineItem.");
                        }
                    }
                    timeline.setValue(timelineItems);
                } else {
                    Log.d("Timeline", "Timeline is empty or not found.");
                }
            } else {
                Log.d("Timeline", "User profile does not exist.");
            }
        }).addOnFailureListener(e -> {
            Log.e("Timeline", "Error getting user profile", e);
        });
    }

    private TimelineItem convertMapToTimelineItem(Map<String, Object> map) {
        try {
            String userId = (String) map.get("userId");
            String type = (String) map.get("type");
            String username = (String) map.get("username");
            String restaurantName = (String) map.get("restaurantName");
            String mainFlavor = (String) map.get("mainFlavor");
            boolean recommend = (boolean) map.get("recommend");
            ArrayList<String> signatureDishes = (ArrayList<String>) map.get("signatureDishes");
            String timestamp = (String) map.get("timestamp");

            return new TimelineItem(userId, username, restaurantName, type, mainFlavor, recommend, signatureDishes, timestamp);
        } catch (Exception e) {
            Log.e("Timeline", "Error converting map to TimelineItem", e);
            return null;
        }
    }


    public LiveData<ArrayList<TimelineItem>> getTimeline() {
        return timeline;
    }
}
