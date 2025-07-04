package com.example.a2866777l_development_project.profile;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a2866777l_development_project.R;
import com.example.a2866777l_development_project.Repository.UserRepository;
import com.example.a2866777l_development_project.model.Review;
import com.example.a2866777l_development_project.util.Utils;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {
    private List<Review> reviews;

    public ReviewsAdapter(List<Review> reviews) {
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.reviewName.setText(review.getRestaurantName());
        String timeStamp = Utils.convertTimeStamp(review.getTimestamp());
        holder.timeStamp.setText(timeStamp);
        setupRecommendation(holder, review);
        holder.mainFlavorsTextView.setText("Main Flavors: " + review.getMain_flavors());
        ChipGroup chipGroupSignatureDishes = holder.signatureDishes;
        showSigniture_dishes(holder, chipGroupSignatureDishes, review);
        getReviewPhotos(holder, review);
        setUsername(holder, review);
        setProfilePic(holder, review);

    }

    private void setProfilePic(ReviewViewHolder holder, Review review) {
        UserRepository userRepository = Utils.getUserRepository();
        userRepository.getUserProfile(review.getUserId()).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String imageUrl = documentSnapshot.getString("profileImage");
                Log.d("ProfileImage", "Fetched URL: " + imageUrl);
                if (imageUrl != null) {
                    Picasso.get()
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_person_foreground)
                            .into(holder.profileImg);
                } else {
                    holder.profileImg.setImageResource(R.drawable.profile);
                }
            }
        });
    }

    private void getReviewPhotos(ReviewViewHolder holder, Review review) {
        ArrayList<String> photoUrls = review.getPhotoUrls();
        if (photoUrls != null && !photoUrls.isEmpty()) {
            GridLayout photoGrid = holder.photoGrid;
            photoGrid.removeAllViews();
            int size = (int) holder.itemView.getResources().getDimension(R.dimen.photo_review_size);
            for (String photoUrl : photoUrls) {
                ImageView imageView = new ImageView(holder.itemView.getContext());
                Picasso.get()
                        .load(photoUrl)
                        .resize(size, size)
                        .centerCrop()
                        .into(imageView);
                photoGrid.addView(imageView);
            }
        }
    }

    private void setUsername(ReviewViewHolder holder, Review review) {
        if (review.getUserId() != null) {
            UserRepository userRepository = Utils.getUserRepository();
            userRepository.getUserProfile(review.getUserId()).addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String username = documentSnapshot.getString("username");
                    holder.username.setText(username);
                }
            });
        }
    }

    private void setupRecommendation(ReviewViewHolder holder, Review review) {
        if (review.getRecommendation()) {
            holder.recommendationIcon.setImageResource(R.drawable.thumbs_up); // Replace with your thumbs up icon
        } else {
            holder.recommendationIcon.setImageResource(R.drawable.thumbs_down); // Replace with your thumbs down icon
        }
    }

    private void showSigniture_dishes(ReviewViewHolder holder, ChipGroup chipGroupSignatureDishes, Review review) {
        ArrayList<String> signatureDishesList = review.getSignature_dishes();
        if(!signatureDishesList.isEmpty()) {
            for (String dish : signatureDishesList) {
                Chip chip = new Chip(holder.itemView.getContext());
                chip.setText(dish.trim());
                chip.setCloseIconVisible(false); // Hide the close icon
                chipGroupSignatureDishes.addView(chip);
            }
        }
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public void updateReviews(List<Review> newReviews) {
        this.reviews = newReviews;
        notifyDataSetChanged();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView reviewName;
        ImageView recommendationIcon;
        TextView mainFlavorsTextView;
        ChipGroup signatureDishes;
        TextView timeStamp;
        TextView username;
        ImageView profileImg;
        GridLayout photoGrid;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            timeStamp = itemView.findViewById(R.id.review_timestamp);
            reviewName = itemView.findViewById(R.id.review_name);
            recommendationIcon = itemView.findViewById(R.id.imageViewRecommendation);
            mainFlavorsTextView = itemView.findViewById(R.id.textViewMainFlavors);
            signatureDishes = itemView.findViewById(R.id.chipGroupSignatureDishes);
            username = itemView.findViewById(R.id.username);
            profileImg = itemView.findViewById(R.id.profile_imageView);
            photoGrid = itemView.findViewById(R.id.review_photoGrid);

        }
    }
}
