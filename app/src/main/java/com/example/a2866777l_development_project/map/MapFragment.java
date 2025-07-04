package com.example.a2866777l_development_project.map;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.a2866777l_development_project.R;
import com.example.a2866777l_development_project.Repository.RestaurantRepository;
import com.example.a2866777l_development_project.Repository.ReviewRepository;
import com.example.a2866777l_development_project.Repository.UserRepository;
import com.example.a2866777l_development_project.databinding.FragmentMapBinding;
import com.example.a2866777l_development_project.model.Restaurant;
import com.example.a2866777l_development_project.model.Review;
import com.example.a2866777l_development_project.profile.UserViewModel;
import com.example.a2866777l_development_project.util.Utils;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AddressComponents;
import com.google.android.libraries.places.api.model.DayOfWeek;
import com.google.android.libraries.places.api.model.LocalTime;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.Period;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TimeOfWeek;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.auth.FirebaseAuth;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private ActivityResultLauncher<String> requestPermissionLauncher;
    private FragmentMapBinding binding;
    private GoogleMap map;
    private boolean locationPermitted;
    private FusedLocationProviderClient client;
    private Location currentLocation = null;
    private AutocompleteSupportFragment autocompleteFragment;
    private String userID;
    private final UserRepository userRepository = new UserRepository();
    private final ReviewRepository reviewRepository = new ReviewRepository();
    private String sharedUserId;
    private String restaurantId;
    private RestaurantRepository restaurantRepository = new RestaurantRepository();




    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        binding = FragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        client = LocationServices.getFusedLocationProviderClient(requireActivity());
        initialisePermission();

        mapInitialise();
        placeInitialise();
        searchLocation();

        userID = Utils.getUserId();
        userViewModel.getUserData(userID);
        if (getArguments() != null) {
            sharedUserId = getArguments().getString("userId");
            restaurantId = getArguments().getString("restaurantId");
        }

        return root;
    }

    private void mapInitialise() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void placeInitialise() {
        if (!Places.isInitialized()) {
            Places.initializeWithNewPlacesApiEnabled(getContext().getApplicationContext(), getString(R.string.google_maps_key));
        }
        PlacesClient placesClient = Places.createClient(getContext());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
        map.setInfoWindowAdapter(new CustomInfoWindowAdapter(getContext()));

        map.setOnMarkerClickListener(marker -> {
            marker.showInfoWindow();
            return true;
        });

        map.setOnInfoWindowClickListener(marker -> {
            showRestaurantDetailsFragment(marker);
            marker.hideInfoWindow();
        });

        if (getArguments() != null) {
            String restaurantId = getArguments().getString("restaurantId");
            showMarkerForRestaurant(restaurantId);
        }
    }

    private void showRestaurantDetailsFragment(Marker marker) {
        RestaurantDetails fragment = RestaurantDetails.newInstance(marker);
        FragmentManager fragmentManager = getParentFragmentManager();
        fragment.show(fragmentManager, "restaurantDetails");
    }

    public void showMarkerForRestaurant(String restaurantId) {
        restaurantRepository.getRestaurantById(restaurantId).addOnSuccessListener(
            documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Restaurant restaurant = documentSnapshot.toObject(Restaurant.class);
                    LatLng restaurantLocation = new LatLng(restaurant.getX(), restaurant.getY());
                    Bundle markerData = new Bundle();
                    putRestaurantDetails(restaurant, markerData);
                    setInfoWindow(restaurant.getResName(), restaurantLocation, markerData);
                }
        });
    }

    private void initialisePermission() {
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    locationPermitted = isGranted;
                    updateLocationUI();
                    getDeviceLocation();
                }
        );
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermitted = true;
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }


    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermitted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("error", e.getMessage());
        }
    }

    private void setLocation(double x, double y) {
        LatLng address = new LatLng(x,y);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(address, 15));

    }

    private void getDeviceLocation() {
        if (!locationPermitted) {
            setDefaultLocation();
            return;
        }
        try {
            Task<Location> location = client.getLastLocation();
            location.addOnCompleteListener(requireActivity(), task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    currentLocation = task.getResult();
                    if (currentLocation != null) {
                        setLocation(currentLocation.getLatitude(),
                                    currentLocation.getLongitude());
                    }
                } else {
                    setDefaultLocation();
                }
            });
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    private void setDefaultLocation() {
        setLocation(-34, 151);
        map.getUiSettings().setMyLocationButtonEnabled(false);
    }

    public void searchLocation() {
        autocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager()
                .findFragmentById(R.id.autocomplete_fragment);
        getPlaceDetails();
        setupAutocompleteFragment();
    }

    public void getPlaceDetails() {
        autocompleteFragment.setPlaceFields(Arrays.asList(
                Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.BUSINESS_STATUS,
                Place.Field.OPENING_HOURS, Place.Field.PHONE_NUMBER, Place.Field.PRICE_LEVEL, Place.Field.ADDRESS_COMPONENTS));
    }

    public void setupAutocompleteFragment() {
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                Bundle markerData = new Bundle();
                String id = place.getId();
                String resName = place.getName();
                LatLng selected = place.getLatLng();
                getOpeningHours(place, markerData);
                getCounts(id, markerData);

                restaurantRepository.getRestaurantById(id).addOnSuccessListener(
                    documentSnapshot -> {
                        if (!documentSnapshot.exists()) {
                            String cityName = getResCity(place);
                            String address = place.getAddress();
                            String phoneNumber = place.getPhoneNumber();
                            String openingHours = markerData.getString("opening_hours");
                            int priceLevel = getResPriceLevel(place);
                            ArrayList<String> reviews = new ArrayList<>();
                            ArrayList<String> images = new ArrayList<>();
                            Restaurant restaurant = new Restaurant(id, resName, cityName,
                                    selected.latitude, selected.longitude, address, priceLevel, openingHours, phoneNumber, reviews, images);
                            markerData.putInt("recommendations", 0);
                            markerData.putInt("disrecommendations", 0);
                            putRestaurantDetails(restaurant, markerData);
                            restaurantRepository.addRestaurant(restaurant);


                        } else {
                            Restaurant restaurant = documentSnapshot.toObject(Restaurant.class);
                            putRestaurantDetails(restaurant, markerData);
                        }
                    });

                setInfoWindow(resName, selected, markerData);
            }
            @Override
            public void onError(@NonNull Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    public void getOpeningHours(Place place, Bundle markerData) {
        boolean isOpenNow = false;
        OpeningHours openingHours = place.getOpeningHours();
        StringBuilder hoursBuilder = new StringBuilder();
        isOpenNow = isRestaurantOpen(openingHours);
        if (openingHours != null) {
            List<String> weekdayText = openingHours.getWeekdayText();
            for (String hours : weekdayText) {
                hoursBuilder.append(hours).append("\n");
            }
        }

        String openNowStatus = isOpenNow ? "Open Now" : "Closed";
        String openingHoursText = hoursBuilder.toString().trim();

        markerData.putString("opening_hours", openingHoursText);
        markerData.putString("open_now", openNowStatus);
    }

    public boolean isRestaurantOpen(OpeningHours openingHours) {
        if (openingHours == null) {
            return false;
        }

        List<Period> periods = openingHours.getPeriods();
        if (periods == null || periods.isEmpty()) {
            return false;
        }

        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_WEEK);
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        for (Period period : periods) {
            TimeOfWeek open = period.getOpen();
            TimeOfWeek close = period.getClose();

            if (open.getDay().ordinal() == currentDay - 1) {
                int openHour = open.getTime().getHours();
                int openMinute = open.getTime().getMinutes();
                int closeHour = close.getTime().getHours();
                int closeMinute = close.getTime().getMinutes();

                if ((currentHour > openHour || (currentHour == openHour && currentMinute >= openMinute)) &&
                        (currentHour < closeHour || (currentHour == closeHour && currentMinute < closeMinute))) {
                    return true;
                }
            }
        }

        return false;
    }

    private void getCounts(String resId,Bundle markerData) {
        reviewRepository.getRestaurantReviews(resId).addOnSuccessListener(
            queryDocumentSnapshots -> {
                int recommendations = 0;
                int disrecommendations = 0;

                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    Review review = document.toObject(Review.class);
                    if (review != null) {
                        if (review.getRecommendation()) {
                            recommendations++;
                        } else {
                            disrecommendations++;
                        }
                    }
                }
                markerData.putInt("recommendations", recommendations);
                Log.d("getCounts", "recommendations: " + recommendations);
                markerData.putInt("disrecommendations", disrecommendations);

            });
    }


    public void putRestaurantDetails(Restaurant restaurant, Bundle markerData) {
        String id = restaurant.getResId();
        markerData.putString("id", id);
        markerData.putDouble("x", restaurant.getX());
        markerData.putDouble("y", restaurant.getY());
        markerData.putString("name", restaurant.getResName());
        markerData.putString("city", restaurant.getCity());
        markerData.putInt("price_level", restaurant.getPriceLevel());
        markerData.putString("address", restaurant.getAddress());
        markerData.putString("phone_number", restaurant.getPhoneNumber());
        markerData.putString("opening_hours", restaurant.getOpeningHours());
        getCounts(id, markerData);
        markerData.putString("open_now", "Open Now");

        setCheckIns(id, markerData);
    }

    private void setInfoWindow(String resName, LatLng selected, Bundle markerData) {
        Marker restaurantMarker = setMarker(resName, selected);
        restaurantMarker.setTag(markerData);
    }

    public Marker setMarker(String name, LatLng latLng) {
        Marker restaurantMarker = map.addMarker(new MarkerOptions().position(latLng).title(name));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        return restaurantMarker;
    }

    private int getResPriceLevel(Place place) {
       if (place.getPriceLevel() != null) {
          return place.getPriceLevel();
       } else return 0;
   }

    private String getResCity(Place place) {
        String cityName = null;
        AddressComponents addressComponents = place.getAddressComponents();
        if (addressComponents != null) {
            cityName = place.getAddressComponents().asList().get(3).getName();
            return cityName;
        } else {
            return "Other";
        }
    }

    public void setCheckIns(String id, Bundle markerData) {
        userRepository.getUserProfile(userID).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                ArrayList<String> checkIns = (ArrayList<String>) documentSnapshot.get("checkIns");
                if (checkIns != null && checkIns.contains(id)) {
                    markerData.putBoolean("checkInStatus", true);
                } else {
                    markerData.putBoolean("checkInStatus", false);
                }
            } else {
                Log.e("setCheckIns", "DocumentSnapshot does not exist");
            }
        });
    }

}