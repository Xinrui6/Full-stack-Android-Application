package com.example.a2866777l_development_project.list;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.cardview.widget.CardView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a2866777l_development_project.R;
import com.example.a2866777l_development_project.Repository.UserRepository;
import com.example.a2866777l_development_project.list.restaurantList.CityRestaurantsFragment;
import com.example.a2866777l_development_project.model.City;

import java.util.ArrayList;
import java.util.List;

public class CityListAdapter extends RecyclerView.Adapter<CityListAdapter.CityViewHolder> {
    private List<City> cityList;
    private Context context;
    private boolean isEditingMode = false;
    private final UserRepository userRepository;
    private final String userId;




    public CityListAdapter(Context context, List<City> cityList, UserRepository userRepository, String userId) {
        this.context = context;
        this.cityList = cityList;
        this.userRepository = userRepository;
        this.userId = userId;
    }

    public boolean isEditingMode() {
        return isEditingMode;
    }

    @NonNull
    @Override
    public CityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_cardview, parent, false);
        return new CityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CityViewHolder holder, int position) {
        City city = cityList.get(position);
        holder.textViewCityName.setText(city.getName());
        holder.textViewCount.setText(city.getNumberOfRes() + " restaurants");

        holder.closeIcon.setVisibility(isEditingMode ? View.VISIBLE : View.GONE);

        holder.closeIcon.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete City")
                    .setMessage("Are you sure you want to delete this city?")
                    .setPositiveButton("Delete", (dialog, which) -> {

                        removeCity(position);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        holder.cardView.setOnClickListener(v -> {
            FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, CityRestaurantsFragment.newInstance(city.getName(), city.getRestaurants()));
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

//        // Assuming the first restaurant ID can be used to fetch the image
//        String firstRestaurantId = city.getFirstId() == null ? null : city.getFirstId();
//
//        if (firstRestaurantId != null) {
//            // Replace this with the actual logic to get the restaurant image URL
//            String imageUrl = getRestaurantImageUrl(firstRestaurantId);
//            Glide.with(context)
//                    .load(imageUrl)
//                    .placeholder(R.drawable.placeholder_image)
//                    .into(holder.imageView);
//        } else {
//            holder.imageView.setImageResource(R.drawable.placeholder_image);
//        }
    }

    public void setEditingMode(boolean isEditingMode) {
        this.isEditingMode = isEditingMode;
        notifyDataSetChanged();
    }

    private void removeCity(int position) {
        City cityToRemove = cityList.get(position);
        cityList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, cityList.size());
        removeCityFromDatabase(cityToRemove.getName());
    }

    private void removeCityFromDatabase(String cityName) {
        userRepository.removeCityFromUser(userId, cityName);
    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }

    public static class CityViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewCityName;
        TextView textViewCount;
        CardView cardView;
        ImageView closeIcon;


        public CityViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            textViewCityName = itemView.findViewById(R.id.text_view_city_name);
            textViewCount = itemView.findViewById(R.id.text_view_count);
            cardView = itemView.findViewById(R.id.card_view);
            closeIcon = itemView.findViewById(R.id.close_icon);
        }
    }

    public void updateCityList(ArrayList<City> newCityList) {
        this.cityList = newCityList;
        notifyDataSetChanged();
    }

    private String getRestaurantImageUrl(String restaurantId) {
        // Logic to get the restaurant image URL based on restaurant ID
        // This is just a placeholder. Implement the actual logic as needed.
        return "https://example.com/images/" + restaurantId + ".jpg";
    }
}