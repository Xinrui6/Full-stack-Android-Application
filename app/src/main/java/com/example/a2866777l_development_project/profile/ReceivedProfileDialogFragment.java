package com.example.a2866777l_development_project.profile;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.a2866777l_development_project.R;
import com.example.a2866777l_development_project.Repository.UserRepository;
import com.example.a2866777l_development_project.util.Utils;
import com.example.a2866777l_development_project.model.User;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class ReceivedProfileDialogFragment extends DialogFragment {
    private UserRepository userRepository = Utils.getUserRepository();
    private String receivedUserId;
    private User receivedUser;
    private String currentUserId = Utils.getUserId();

    private ChipGroup flavorTags;
    private TextView nameTextView, hometownTextView, biographyTextView, checkinsTextView;
    private Button followButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_received_profile_dialog, container, false);
        nameTextView = view.findViewById(R.id.name_text_view);
        hometownTextView = view.findViewById(R.id.hometown_text_view);
        flavorTags = view.findViewById(R.id.chip_group_flavors);
        biographyTextView = view.findViewById(R.id.biography_text_view);
        checkinsTextView = view.findViewById(R.id.checkins_text_view);
        followButton = view.findViewById(R.id.follow_button);

        if (getArguments() != null) {
            receivedUserId = getArguments().getString("userId");
        }
        userRepository = Utils.getUserRepository();
        fetchUserProfile();
        followButton.setOnClickListener(v -> {
            followUser();
        });

        return view;
    }

    private void fetchUserProfile() {
        if (receivedUserId != null) {
            userRepository.getUserProfile(receivedUserId).addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    receivedUser = documentSnapshot.toObject(User.class);
                    updateUI();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Failed to load user profile", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void updateUI() {
        nameTextView.setText("Username: " + receivedUser.getUsername());
        hometownTextView.setText("HomeTown: " + receivedUser.getHomeTown());
        biographyTextView.setText("Biography: " + receivedUser.getBiography());
        checkinsTextView.setText("Number of Checkins: " + String.valueOf(receivedUser.getCheckIns()));
        updateFlavorUI(receivedUser.getFlavor());
    }

    private void addFlavorChip(String flavor) {
        Chip chip = new Chip(requireContext());
        chip.setText(flavor);
        chip.setCloseIconVisible(false);
        flavorTags.addView(chip);
    }


    private void updateFlavorUI(List<String> flavors) {
        flavorTags.removeAllViews();
        if (!flavors.isEmpty()) {
            for (String flavor : flavors) {
                addFlavorChip(flavor);
            }
        }
    }

    private void followUser() {
        Log.d("follower", "receivedUser: " + receivedUser.getUserID());
        Log.d("following", "currentUserId: " + currentUserId);
        userRepository.addFollower(currentUserId, receivedUser.getUserID());
        userRepository.addFollowing(receivedUser.getUserID(), currentUserId);
        Toast.makeText(getContext(), "You are now following " + receivedUser.getUsername(), Toast.LENGTH_SHORT).show();
        dismiss();
    }
}