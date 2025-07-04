package com.example.a2866777l_development_project.profile;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import com.example.a2866777l_development_project.R;
import com.example.a2866777l_development_project.Repository.ImageRepository;
import com.example.a2866777l_development_project.Repository.UserRepository;
import com.example.a2866777l_development_project.databinding.FragmentProfileBinding;
import com.example.a2866777l_development_project.login;
import com.example.a2866777l_development_project.model.Review;
import com.example.a2866777l_development_project.util.DynamicLinkUtils;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.example.a2866777l_development_project.util.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private String userId = Utils.getUserId();
    private ChipGroup flavorTags;
    private UserRepository userRepository;
    private ImageRepository imageRepository;
    private RecyclerView reviewsRecyclerView;
    private static ReviewsAdapter reviewsAdapter;
    private List<Review> reviewList;
    private Button logoutButton;
    private ImageView profileImg;
    private ActivityResultLauncher<Intent> resultLauncher;
    private static final int REQUEST_CODE_PERMISSIONS = 1001;



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userRepository = Utils.getUserRepository();
        imageRepository = Utils.getImageRepository();

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        UserViewModel userViewModel =
                new ViewModelProvider(this).get(UserViewModel.class);
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        checkPermissions();
        final TextView username = binding.textUsername;
        final TextView following = binding.textFollowing;
        final TextView follower = binding.textFollower;
        final TextView hometown = binding.textHomeTown;
        final TextView biography = binding.textBiography;
        profileImg = binding.imageProfile;
        flavorTags = root.findViewById(R.id.chip_group_flavors);
        ImageButton addFlavorButton = root.findViewById(R.id.fab_add_flavor);
        logoutButton = root.findViewById(R.id.logout_button);
        TextView edit = root.findViewById(R.id.profile_edit);
        ImageButton shareButton = root.findViewById(R.id.profile_share);


        userViewModel.getUserData(userId);
        reviewsRecyclerView = root.findViewById(R.id.review_recyclerView);
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        reviewList = new ArrayList<>();
        reviewsAdapter = new ReviewsAdapter(reviewList);
        reviewsRecyclerView.setAdapter(reviewsAdapter);

        userViewModel.getUsername().observe(getViewLifecycleOwner(), username::setText);
        userViewModel.getFollower().observe(getViewLifecycleOwner(), following::setText);
        userViewModel.getFollowing().observe(getViewLifecycleOwner(), follower::setText);
        userViewModel.getBiography().observe(getViewLifecycleOwner(), biography::setText);
        userViewModel.getHomeTown().observe(getViewLifecycleOwner(), hometown::setText);
        userViewModel.getReviews().observe(getViewLifecycleOwner(), reviews -> Utils.updateReviewsUI(reviews, reviewsAdapter));
        userViewModel.getFlavor().observe(getViewLifecycleOwner(), this::updateFlavorUI);
        userViewModel.getProfileImage().observe(getViewLifecycleOwner(), imageUrl -> {
            if (!imageUrl.isEmpty()) {
                Picasso.get()
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_person_foreground)
                        .into(profileImg);
            } else {
                Picasso.get()
                        .load(R.drawable.profile)
                        .into(profileImg);
            }
        });
        addFlavorButton.setOnClickListener(v -> addFlavorDialog(inflater));
        edit.setOnClickListener(v -> editProfileDialog(inflater));
        logoutButton.setOnClickListener(v -> logout());
        shareButton.setOnClickListener(v -> shareProfile(getContext()));
        choosePictureInitialisation();
        profileImg.setOnClickListener(v -> pickImage());




        return root;
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                    REQUEST_CODE_PERMISSIONS);
        } else {
            Log.e("photo Permission", "No Photo Permission");
        }
    }

    private void pickImage() {

        Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        intent.setType("image/*");
        resultLauncher.launch(intent);
    }

private void choosePictureInitialisation() {
    resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        imageRepository.uploadProfileImage(userId, imageUri).addOnSuccessListener(imageUrl -> {
                        userRepository.addProfileImage(userId, imageUrl.toString());

                        Picasso.get()
                                .load(imageUrl)
                                .placeholder(R.drawable.ic_person_foreground)
                                .error(R.drawable.ic_person_foreground)
                                .into(profileImg);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ImagePicker", "Failed to upload image", e);
                        Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    Toast.makeText(getContext(), "No Image Selected", Toast.LENGTH_SHORT).show();
                }
            }
        });
}




    private void addFlavorDialog(LayoutInflater inflater) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_App_MaterialAlertDialog);
        View dialogView = inflater.inflate(R.layout.dialog_add_flavor, null);

        EditText editTextFlavor = dialogView.findViewById(R.id.editTextFlavor);

        builder.setView(dialogView)
                .setTitle("Enter the flavor you like!")
                .setPositiveButton("ADD", (dialog, which) -> {
                    String flavor = editTextFlavor.getText().toString().trim();
                    if (!flavor.isEmpty()) {
                        addFlavorChip(flavor);
                        saveFlavorToDatabase(flavor);
                    } else {
                        editTextFlavor.setError("Flavor cannot be empty");
                    }
                })
                .setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss());

        builder.show();
    }


    private void addFlavorChip(String flavor) {
        Chip chip = new Chip(requireContext());
        chip.setText(flavor);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> {
            flavorTags.removeView(chip);
            removeFlavorFromDatabase(flavor);
        });
        flavorTags.addView(chip);
    }

    private void saveFlavorToDatabase(String flavor) {
        userRepository.addFlavorToUser(userId, flavor);
    }

    private void removeFlavorFromDatabase(String flavor) {
        userRepository.removeFlavorFromUser(userId, flavor);
    }

    private void updateFlavorUI(List<String> flavors) {
        flavorTags.removeAllViews();
        if (!flavors.isEmpty()) {
            for (String flavor : flavors) {
                addFlavorChip(flavor);
            }
        }
    }
    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    private void editProfileDialog(LayoutInflater inflater) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_App_MaterialAlertDialog);
        View dialogView = inflater.inflate(R.layout.dialog_edit_profile, null);

        EditText editUsername = dialogView.findViewById(R.id.editTextUsername);
        EditText editHomeTown = dialogView.findViewById(R.id.editTextHomeTown);
        EditText editBio = dialogView.findViewById(R.id.editTextBio);


        builder.setView(dialogView)
                .setTitle("Edit your profile")
                .setPositiveButton("ADD", (dialog, which) -> {
                    String username = editUsername.getText().toString().trim();
                    String homeTown = editHomeTown.getText().toString().trim();
                    String bio = editBio.getText().toString().trim();
                    saveChangesToDatabase(username, homeTown, bio);
                })
                .setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private void saveChangesToDatabase(String username, String homeTown, String bio) {
        if (!username.isEmpty()) {
            userRepository.updateUsername(userId, username);
        }
        if (!homeTown.isEmpty()) {
            userRepository.updateHomeTown(userId, homeTown);
        }
        if (!bio.isEmpty()) {
            userRepository.updateBio(userId, bio);
        }
    }

    private void shareProfile(Context context) {
        DynamicLinkUtils.createProfileLink(userId, "profile", new DynamicLinkUtils.OnLinkCreatedListener() {
            @Override
            public void onLinkCreated(String link) {

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "Check out my Profile! " + link);

                if (context != null) {
                    context.startActivity(Intent.createChooser(intent, "Share via"));
                } else {
                    Log.e(TAG, "Context is null, cannot start activity");
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(context, "Error creating share link", Toast.LENGTH_SHORT).show();
                Log.e("sharingProfile", "Error creating share link", e);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}