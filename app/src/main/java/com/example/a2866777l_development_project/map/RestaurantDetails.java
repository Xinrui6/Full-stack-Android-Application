package com.example.a2866777l_development_project.map;

import static android.content.ContentValues.TAG;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a2866777l_development_project.Repository.ImageRepository;
import com.example.a2866777l_development_project.model.TimelineItem;
import com.example.a2866777l_development_project.util.DynamicLinkUtils;
import com.example.a2866777l_development_project.R;
import com.example.a2866777l_development_project.Repository.RestaurantRepository;
import com.example.a2866777l_development_project.Repository.ReviewRepository;
import com.example.a2866777l_development_project.Repository.UserRepository;
import com.example.a2866777l_development_project.profile.ReviewsAdapter;
import com.example.a2866777l_development_project.util.Utils;
import com.example.a2866777l_development_project.model.Review;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestaurantDetails extends DialogFragment {
    private Context context;
    private Map<String, ArrayList<String>> cityLists;
    private String userId;
    private UserRepository userRepository;
    private ReviewRepository reviewRepository;
    private RecyclerView reviewsRecyclerView;
    private ImageRepository imageRepository;
    private RestaurantRepository restaurantRepository;
    private static ReviewsAdapter reviewsAdapter;
    private ArrayList<Review> reviewList;
    private ArrayList<Uri> selectedImageUris = new ArrayList<>();
    private ArrayList<String> photoUrls = new ArrayList<>();
    private ActivityResultLauncher<Intent> resultLauncher;
    private GridLayout photoGrid;
    private RecyclerView carouselRecyclerView;
    private CarouselAdapter carouselAdapter;

    public RestaurantDetails() {}

    public static RestaurantDetails newInstance(Marker marker) {
        RestaurantDetails fragment = new RestaurantDetails();
        Bundle args = new Bundle();
        Bundle markerData = (Bundle) marker.getTag();
        args.putBundle("markerData", markerData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            Log.e("RestaurantDetailsFragment", "No user is currently signed in.");
        }
        userRepository = new UserRepository();
        reviewRepository = new ReviewRepository();
        imageRepository = new ImageRepository();
        restaurantRepository = new RestaurantRepository();
        initializeImagePicker();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.dialog_restaurant_detail, container, false);

        // Set place details to the dialog views
        TextView nameTextView = dialogView.findViewById(R.id.dialog_place_name);
        TextView addressTextView = dialogView.findViewById(R.id.dialog_place_address);
        TextView phoneTextView = dialogView.findViewById(R.id.dialog_place_phone);
        TextView openNow = dialogView.findViewById(R.id.dialog_place_open_now);
        TextView openingHoursTextView = dialogView.findViewById(R.id.dialog_place_opening_hours);
        TextView recommendationsTextView = dialogView.findViewById(R.id.dialog_recommendations);
        TextView disrecommendationsTextView = dialogView.findViewById(R.id.dialog_disrecommendations);

        ImageButton backButton = dialogView.findViewById(R.id.back);
        Button checkInButton = dialogView.findViewById(R.id.dialog_check_in_button);
        Button addToListButton = dialogView.findViewById(R.id.dialog_addToList);
        ExtendedFloatingActionButton commentButton = dialogView.findViewById(R.id.dialog_add_comment);
        ImageButton shareButton = dialogView.findViewById(R.id.dialog_share);
        carouselRecyclerView = dialogView.findViewById(R.id.carousel_recycler_view);
        carouselRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        if (getArguments() != null) {
            Bundle markerData = getArguments().getBundle("markerData");
            if (markerData != null) {
                // Set marker details to the views
                nameTextView.setText(markerData.getString("name"));
                addressTextView.setText(markerData.getString("address"));
                phoneTextView.setText(markerData.getString("phone_number"));
                openNow.setText(markerData.getString("open_now"));
                if (openNow.equals("Open Now")) {
                    openNow.setTextColor(Color.GREEN);
                }else {
                    openNow.setTextColor(Color.RED);
                }
                openingHoursTextView.setText(markerData.getString("opening_hours"));
                recommendationsTextView.setText(" " + markerData.getInt("recommendations"));
                disrecommendationsTextView.setText(" " + markerData.getInt("disrecommendations"));
                int priceLevel = markerData.getInt("price_level");
                setPriceLevel(dialogView, priceLevel);

                String restaurantId = markerData.getString("id");
                Utils.checkCheckInStatus(checkInButton, restaurantId);


                // review layout
                reviewsRecyclerView = dialogView.findViewById(R.id.dialog_details_reviews);
                reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                reviewList = new ArrayList<>();
                reviewsAdapter = new ReviewsAdapter(reviewList);
                reviewsRecyclerView.setAdapter(reviewsAdapter);
                showReviews(restaurantId);


                checkInButton.setOnClickListener(v -> onCheckIn(checkInButton, markerData));
                addToListButton.setOnClickListener(v -> {
                    addToCityList(markerData);
                    Toast.makeText(getContext(), "Restaurant added to city list", Toast.LENGTH_SHORT).show();
                });
                loadPhotos(restaurantId);

                commentButton.setOnClickListener(v -> showCommentDialog(markerData));
                shareButton.setOnClickListener(v -> shareRestaurant(restaurantId));
                backButton.setOnClickListener(v -> dismiss());
            }
        }

        return dialogView;
    }

    private void loadPhotos(String restaurantId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("restaurants").document(restaurantId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> imageUrls = (List<String>) documentSnapshot.get("photoUrls");
                        if (imageUrls != null) {
                            carouselAdapter = new CarouselAdapter(getContext(), (ArrayList<String>) imageUrls);
                            carouselRecyclerView.setAdapter(carouselAdapter);
                        } else {
                            Log.d("loadPhotos", "No photos found for restaurantId: " + restaurantId);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("loadPhotos", "Failed to load photos", e));
    }


    private void showReviews(String restaurantId) {
        userRepository.getUserProfile(userId).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                ArrayList<String> followersIds = (ArrayList<String>) documentSnapshot.get("followers");
                reviewRepository.getRestaurantReviews(restaurantId).addOnSuccessListener(
                    queryDocumentSnapshots -> {
                        reviewList.clear();
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            Review review = document.toObject(Review.class);
                            if (followersIds != null && followersIds.contains(review.getUserId())) {
                                reviewList.add(review);
                            }
                        }
                        Utils.updateReviewsUI(reviewList, reviewsAdapter);
                    });
            }
        });
    }

    private void saveComment(String restaurantId, String resName, boolean recommend, String mainFlavors, ArrayList<String> signatureDishes, ArrayList<String> photoUrls) {
        Review review = new Review(userId, restaurantId, resName, recommend, mainFlavors, signatureDishes, photoUrls);
        reviewRepository.addReview(review);
        saveReviewToTimeline(review);

    }

    private void saveReviewToTimeline(Review review) {
        userRepository.getUserProfile(userId).addOnSuccessListener( documentSnapshot -> {
            String username = documentSnapshot.getString("username");
            TimelineItem reviewTimeline = new TimelineItem(review, username);
            Log.d("Timeline", "Timeline item: " + reviewTimeline.getText());
            updateFollowersTimelines(reviewTimeline);
        });
    }

    private void onCheckIn(Button checkInButton, Bundle markerData) {
        if (!markerData.getBoolean("checkInStatus")) {
            checkInButton.setEnabled(false);
            checkInButton.setText("Checked In");
            markerData.putBoolean("checkInStatus", true);
            saveCheckInToUser(markerData);
            createTimelineItem(markerData);
        }
    }

    private void createTimelineItem(Bundle markerData) {
        String restaurantName = markerData.getString("name");
        userRepository.getUserProfile(userId).addOnSuccessListener(documentSnapshot -> {
            String username = documentSnapshot.getString("username");
            TimelineItem timelineItem = new TimelineItem(userId, username, restaurantName, Timestamp.now());
            updateFollowersTimelines(timelineItem);
        });
    }

    private void updateFollowersTimelines(TimelineItem timelineItem) {
        userRepository.getUserProfile(userId).addOnSuccessListener(documentSnapshot -> {
            ArrayList<String> followers = (ArrayList<String>) documentSnapshot.get("following");
            if (followers != null) {
                for (String followerId : followers) {
                    userRepository.addTimelineToUser(followerId, timelineItem);
                }
            }
        });
    }

    private void saveCheckInToUser(Bundle markerData) {
        String resId = markerData.getString("id");
        userRepository.addCheckIn(userId, resId);
    }

    private void addToCityList(Bundle markerData) {
        String resId = markerData.getString("id");
        String resCity = markerData.getString("city");
        addResToList(resCity, resId);
    }

    private void addResToList(String resCity, String resId) {
        userRepository.getUserProfile(userId).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                cityLists = (HashMap<String, ArrayList<String>>) documentSnapshot.get("cityLists");
                // If cityList of the specific city is null, initialize it
                assert cityLists != null;
                ArrayList<String> cityList = cityLists.getOrDefault(resCity, new ArrayList<>());
                // If the restaurant is not in the city list, add it
                if (!cityList.contains(resId)) {
                    addToList(cityList, resId, resCity);
                } else {
                    Toast.makeText(getContext(), "Restaurant already in city list", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addToList(ArrayList<String> cityList, String resId, String resCity) {
        cityList.add(resId);
        cityLists.put(resCity, cityList);
        userRepository.addCityList(userId, resCity, cityList);
    }



    private void setPriceLevel(View dialogView, int priceLevel) {
        TextView priceLevelTextView = dialogView.findViewById(R.id.dialog_place_price_level);

        switch (priceLevel) {
            case 1:
                priceLevelTextView.setText("$");
                break;
            case 2:
                priceLevelTextView.setText("$$");
                break;
            case 3:
                priceLevelTextView.setText("$$$");
                break;
            case 4:
                priceLevelTextView.setText("$$$$");
                break;
            default:
                priceLevelTextView.setText("");
        }

    }

    private void shareRestaurant(String restaurantId) {

        DynamicLinkUtils.createResShareLink(userId, restaurantId, "restaurant", new DynamicLinkUtils.OnLinkCreatedListener() {
            @Override
            public void onLinkCreated(String link) {

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "Check out this restaurant: " + link);

                if (context != null) {
                    context.startActivity(Intent.createChooser(intent, "Share via"));
                } else {
                    Log.e(TAG, "Context is null, cannot start activity");
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(context, "Error creating share link", Toast.LENGTH_SHORT).show();
                Log.e("sharingRestaurant", "Error creating share link", e);
            }
        });
    }

    private void showCommentDialog(Bundle markerData) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.dialog_comment, null);
        TextView restaurantNameTextView = view.findViewById(R.id.comment_RestaurantName);
        RadioGroup recommendGroup = view.findViewById(R.id.radioGroupRecommend);
        EditText mainFlavorsEditText = view.findViewById(R.id.editTextMainFlavor);
        EditText signatureDishesEditText = view.findViewById(R.id.editTextSignatureDishes);
        photoGrid = view.findViewById(R.id.photoGrid);
        Button addPhotosButton = view.findViewById(R.id.buttonAddPhotos);
        Button submitButton = view.findViewById(R.id.buttonSubmitComment);
        String resName = markerData.getString("name");
        restaurantNameTextView.setText(resName);
        selectedImageUris.clear();
        photoUrls.clear();


        builder.setView(view);
        AlertDialog dialog = builder.create();

        addPhotosButton.setOnClickListener(v -> openImagePicker());

        submitButton.setOnClickListener(v -> {
            String restaurantId = markerData.getString("id");

            boolean recommend = recommendGroup.getCheckedRadioButtonId() == R.id.radioButtonRecommend;
            String mainFlavors = mainFlavorsEditText.getText().toString();
            String signatureDishes = signatureDishesEditText.getText().toString();
            ArrayList<String> signatureDishesList = signatureDishes.isEmpty() ? new ArrayList<>() : new ArrayList<>(Arrays.asList(signatureDishes.split(",")));

            uploadReviewPhoto(restaurantId, () -> {
                restaurantRepository.updateResImages(restaurantId, photoUrls);
                saveComment(restaurantId, resName, recommend, mainFlavors, signatureDishesList, photoUrls);
                Toast.makeText(context, "Successfully left your comment!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
        });

        dialog.show();
    }

    private void uploadReviewPhoto(String resId, Runnable onComplete) {
        final int[] uploadCount = {0};
        final int totalUploads = selectedImageUris.size();
        if (totalUploads == 0) { onComplete.run();}
        for (Uri imageUri : selectedImageUris) {
            imageRepository.uploadResImages(resId, imageUri).addOnSuccessListener(url -> {
                photoUrls.add(url.toString());
                uploadCount[0]++;
                if (uploadCount[0] == totalUploads) {
                    onComplete.run();
                }
            }).addOnFailureListener(e ->
                    Log.e("ImageUpload", "Error uploading image", e)
            );
        }

    }

    private void updatePhotoPreview() {
        photoGrid.removeAllViews();
        int size = (int) getResources().getDimension(R.dimen.photo_preview_size);
        for (Uri uri : selectedImageUris) {
            ImageView imageView = new ImageView(getContext());
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.setMargins(8, 8, 8, 8);
            imageView.setLayoutParams(params);
            Picasso.get()
                    .load(uri)
                    .resize(size, size)
                    .centerCrop()
                    .into(imageView);
            photoGrid.addView(imageView);

        }
    }

    private void initializeImagePicker() {
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        if (result.getData().getClipData() != null) {
                            ClipData clipData = result.getData().getClipData();
                            for (int i = 0; i < clipData.getItemCount() && selectedImageUris.size() < 6; i++) {
                                Uri imageUri = clipData.getItemAt(i).getUri();
                                selectedImageUris.add(imageUri);

                            }

                        }
                        updatePhotoPreview();
                    }
                });
    }

    private void openImagePicker() {
        if (selectedImageUris.size() < 6) {
            Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            resultLauncher.launch(intent);
        } else {
            Toast.makeText(getContext(), "You can only add up to 6 photos", Toast.LENGTH_SHORT).show();
        }
    }







}