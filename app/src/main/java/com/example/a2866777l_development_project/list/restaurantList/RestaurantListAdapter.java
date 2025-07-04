package com.example.a2866777l_development_project.list.restaurantList;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.a2866777l_development_project.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a2866777l_development_project.Repository.UserRepository;
import com.example.a2866777l_development_project.model.Restaurant;
import com.google.firebase.auth.FirebaseAuth;
import com.example.a2866777l_development_project.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class RestaurantListAdapter extends RecyclerView.Adapter<RestaurantListAdapter.RestaurantViewHolder> {
    private List<Restaurant> restaurantList;
    private String cityName;
    private Context context;
    private UserRepository userRepository = new UserRepository();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private String userId = auth.getCurrentUser().getUid();
//    private List<Restaurant> filteredList;

    public RestaurantListAdapter(List<Restaurant> restaurantList, String cityName, Context context) {
        this.restaurantList = restaurantList;
        Log.d("restaurantListAdapter constructor", "cityName: " + cityName);
        this.cityName = cityName;
        this.context = context;
//        this.filteredList = new ArrayList<>(restaurantList);


    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_restaurant, parent, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        Restaurant restaurant = restaurantList.get(position);
        holder.restaurantName.setText(restaurant.getResName());
        Log.d("restaurantListAdapter onBindViewHolder", "cityName: " + cityName);


        Utils.checkCheckInStatus(holder.checkInButton, restaurant.getResId());


        holder.checkInButton.setOnClickListener(v -> {
            Restaurant checkedInRes = restaurantList.get(position);
            Bundle markerData = new Bundle();
            markerData.putString("id", checkedInRes.getResId());
            markerData.putBoolean("checkInStatus", false);
            Utils.onCheckIn(holder.checkInButton, markerData);
            holder.checkInButton.setEnabled(false);



        });
        holder.deleteButton.setOnClickListener(v -> {
            deleteRestaurant(userId, cityName, restaurant.getResId(), position);
        });
    }

//    public void filter(String query) {
//        filteredList.clear();
//        if (query.isEmpty()) {
//            filteredList.addAll(restaurantList);
//        } else {
//            String lowerCaseQuery = query.toLowerCase();
//            for (Restaurant restaurant : restaurantList) {
//                if (restaurant.getResName().toLowerCase().contains(lowerCaseQuery)) {
//                    filteredList.add(restaurant);
//                }
//            }
//        }
//        notifyDataSetChanged();
//    }
//
//


    private void deleteRestaurant(String userId, String cityName, String restaurantId, int position) {
        // Remove restaurant from the database
        userRepository.removeResFromList(userId, cityName, restaurantId);
        restaurantList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, restaurantList.size());

    }

    public void updateRestaurantList(List<Restaurant> newRestaurantList) {
        restaurantList = newRestaurantList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public static class RestaurantViewHolder extends RecyclerView.ViewHolder {
        TextView restaurantName;
        Button checkInButton;
        Button deleteButton;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            restaurantName = itemView.findViewById(R.id.restaurant_name);
            checkInButton = itemView.findViewById(R.id.check_in_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}
