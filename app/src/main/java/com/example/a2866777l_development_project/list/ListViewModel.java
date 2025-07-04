package com.example.a2866777l_development_project.list;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.a2866777l_development_project.Repository.RestaurantRepository;
import com.example.a2866777l_development_project.Repository.UserRepository;
import com.example.a2866777l_development_project.model.City;
import com.example.a2866777l_development_project.model.Restaurant;

public class ListViewModel extends ViewModel {
    private final UserRepository userRepository = new UserRepository();
    private final RestaurantRepository restaurantRepository = new RestaurantRepository();
    private final MutableLiveData<ArrayList<City>> cities;
    private final MutableLiveData<ArrayList<Restaurant>> restaurants;

    public ListViewModel() {
        restaurants = new MutableLiveData<>();
        cities = new MutableLiveData<>();
    }

    public void fetchUserLists(String userId) {
        userRepository.getUserProfile(userId).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<City> cityList = new ArrayList<>();
                HashMap<String, List<String>> cityMap = (HashMap<String, List<String>>) documentSnapshot.get("cityLists");
                if (cities != null) {
                    for (String cityName : cityMap.keySet()) {
                        ArrayList restaurants = (ArrayList) cityMap.get(cityName);
                        cityList.add(new City(cityName, restaurants));
                    }
                }
                cities.setValue((ArrayList)cityList);
            } else {
                cities.setValue(new ArrayList<>());
            }
        });
    }

    public void fetchRestaurants(List<String> restaurantIds) {
        ArrayList<Restaurant> resList = new ArrayList<>();
        for (String restaurantId : restaurantIds) {
            Log.d("ListViewModel", "fetchRestaurants: " + restaurantId);
            restaurantRepository.getRestaurantById(restaurantId).addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Restaurant restaurant = documentSnapshot.toObject(Restaurant.class);
                    Log.d("ListViewModel", "if restaurant exists: " + restaurant);

                    resList.add(restaurant);
                    Log.d("ListViewModel", "Added restaurant: " + restaurant.getResName());
                    restaurants.setValue(new ArrayList<>(resList));
                }
            });
        }
    }

//    public void getRestaurants(String cityName, String userId) {
//        userRepository.getUserProfile(userId).addOnSuccessListener(documentSnapshot -> {
//            if (documentSnapshot.exists()) {
//                List<City> cityList = new ArrayList<>();
//                HashMap<String, List<String>> cityMap = (HashMap<String, List<String>>) documentSnapshot.get("cityLists");
//                if (cities != null) {
//                    if (cityMap.keySet().contains(cityMap)) {
//
//                        ArrayList listOfRestaurants = (ArrayList) cityMap.get(cityName);
//                        getLiveRestaurants(listOfRestaurants);
//                    }
//                }
//                cities.setValue((ArrayList)cityList);
//            } else {
//                cities.setValue(new ArrayList<>());
//            }
//        });
//    }
//
//    public void getLiveRestaurants(ArrayList<String> restaurantList) {
//        ArrayList<Restaurant> res = new ArrayList<>();
//        for (String restaurantId : restaurantList) {
//            restaurantRepository.getRestaurant(restaurantId).addOnSuccessListener(documentSnapshot -> {
//            if (documentSnapshot.exists()) {
//                Restaurant restaurant = documentSnapshot.toObject(Restaurant.class);
//                res.add(restaurant);
//            }
//            });
//        }
//        restaurants.setValue(res);
//    }

    public LiveData<ArrayList<City>> getCities() {
        return cities;
    }

    public LiveData<ArrayList<Restaurant>> getRestaurants() {
        return restaurants;
    }


}