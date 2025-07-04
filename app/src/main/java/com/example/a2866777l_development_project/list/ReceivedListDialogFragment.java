package com.example.a2866777l_development_project.list;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a2866777l_development_project.R;
import com.example.a2866777l_development_project.Repository.UserRepository;
import com.example.a2866777l_development_project.model.City;
import com.example.a2866777l_development_project.util.Utils;
import com.example.a2866777l_development_project.list.restaurantList.RestaurantListAdapter;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ReceivedListDialogFragment extends DialogFragment {
    private RecyclerView recyclerView;
    private RestaurantListAdapter adapter;
    private UserRepository userRepository;
    private String userId;
    private City receivedCity;
    private ListViewModel listViewModel;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_received_list_dialog, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        Button cancelButton = view.findViewById(R.id.cancel_button);
        Button saveButton = view.findViewById(R.id.save_button);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        if (getArguments() != null) {
            Log.d("ReceivedListDialogFragment", "getArguments() != null");
            receivedCity = getArguments().getParcelable("city");
            listViewModel = new ViewModelProvider(this).get(ListViewModel.class);
            listViewModel.fetchRestaurants(receivedCity.getRestaurants());
            listViewModel.getRestaurants().observe(getViewLifecycleOwner(), restaurants -> {
                if (restaurants != null) {
                    adapter = new RestaurantListAdapter(restaurants, receivedCity.getName(), getContext());
                    recyclerView.setAdapter(adapter);
                }
            });

        }
//        if (getArguments() != null) {
//            String cityListJson = getArguments().getString("cityListJson");
//            if (cityListJson != null) {
//                cityList = new Gson().fromJson(cityListJson, new TypeToken<List<String>>(){}.getType());
//                adapter.updateCityList(cityList);
//            }
//        }

//        if (getArguments() != null) {
//            String cityJson = getArguments().getString("cityListJson");
//            receivedCity = parseCityList(cityJson);
//        }

        userRepository = Utils.getUserRepository();
        userId = Utils.getUserId();

        cancelButton.setOnClickListener(v -> dismiss());
        saveButton.setOnClickListener(v -> {
            saveCityList();
            dismiss();
        });

        return view;
    }

    private void saveCityList() {
        if (receivedCity == null) {
            Toast.makeText(getContext(), "No city data to save", Toast.LENGTH_SHORT).show();
            return;
        }

        userRepository.getUserProfile(userId).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Map<String, ArrayList<String>> cityLists = (Map<String, ArrayList<String>>) documentSnapshot.get("cityLists");
                if (cityLists == null) {
                    cityLists = new HashMap<>();
                }

                String cityName = receivedCity.getName();
                ArrayList<String> restaurantIds = receivedCity.getRestaurants();

                if (cityLists.containsKey(cityName)) {
                    ArrayList<String> existingList = cityLists.get(cityName);
                    for (String resId : restaurantIds) {
                        if (!existingList.contains(resId)) {
                            existingList.add(resId);
                        }
                    }
                } else {
                    cityLists.put(cityName, new ArrayList<>(restaurantIds));
                }
                userRepository.addCityList(userId, cityName, restaurantIds);
                Toast.makeText(getContext(), "City List is saved!", Toast.LENGTH_SHORT).show();

            }
        });
    }
}