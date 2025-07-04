package com.example.a2866777l_development_project;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.a2866777l_development_project.databinding.ActivityMainBinding;
import com.example.a2866777l_development_project.list.ListFragment;
import com.example.a2866777l_development_project.list.ReceivedListDialogFragment;
import com.example.a2866777l_development_project.map.MapFragment;
import com.example.a2866777l_development_project.model.City;
import com.example.a2866777l_development_project.post.PostFragment;
import com.example.a2866777l_development_project.profile.ProfileFragment;
import com.example.a2866777l_development_project.profile.ReceivedProfileDialogFragment;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.Arrays;

//TODO: add review database in the database.

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new MapFragment());
        binding.navView.setBackground(null);

        binding.navView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_map:
                    replaceFragment(new MapFragment());
                    break;
                case R.id.navigation_list:
                    replaceFragment(new ListFragment());
                    break;
                case R.id.navigation_post:
                    replaceFragment(new PostFragment());
                    break;
                case R.id.navigation_profile:
                    replaceFragment(new ProfileFragment());
                    break;
                default:

            }
            return true;
        });




        handleIntent(getIntent());
    }



    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // update the intent
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(intent)
                .addOnSuccessListener(this, pendingDynamicLinkData -> {
                    if (pendingDynamicLinkData != null) {
                        Uri deepLink = pendingDynamicLinkData.getLink();
                        if (deepLink != null) {
                            Log.d("MainActivity", "Deep link received: " + deepLink.toString());
                            handleDeepLink(deepLink);
                        }
                    }
                })
                .addOnFailureListener(this, e -> Log.w(TAG, "getDynamicLink:onFailure", e));
    }


    private void handleDeepLink(Uri deepLink) {
        String userId = deepLink.getQueryParameter("userId");
        String contentId = deepLink.getQueryParameter("contentId");
        String contentType = deepLink.getQueryParameter("contentType");
        String cityListParam = deepLink.getQueryParameter("cityList");
        Log.d(TAG, "userId: " + userId);
        Log.d(TAG, "contentId: " + contentId);
        Log.d(TAG, "contentType: " + contentType);
        Log.d(TAG, "cityListParam: " + cityListParam);


        if (contentType != null) {
            switch (contentType) {
                case "profile":
                    openUserProfile(userId);
                    break;
                case "restaurant":
                    openRestaurantOnMap(userId, contentId);
                    break;
                case "cityList":
                    if (cityListParam != null) {
                        City city = parseCityListFromParam(cityListParam);
                        if (city != null) openCityList(city);
                    } else {
                        Log.e(TAG, "City list param is null");
                    }
                    break;
                default:
                    Log.e(TAG, "Unknown content type: " + contentType);
                    break;

            }
        }
    }

    private void openUserProfile(String userId) {
        Bundle args = new Bundle();
        args.putString("userId", userId);
        Log.d(TAG, "userId: " + userId);
        ReceivedProfileDialogFragment profiledialogFragment = new ReceivedProfileDialogFragment();
        profiledialogFragment.setArguments(args);
        profiledialogFragment.show(getSupportFragmentManager(), "ProfileDialog");

    }



    private void openRestaurantOnMap(String userId, String restaurantId) {
        Bundle args = new Bundle();
        args.putString("userId", userId);
        args.putString("restaurantId", restaurantId);

        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_map);
        if (mapFragment != null) {
            mapFragment.setArguments(args); // Pass data if needed
        } else {
            Log.e("MainActivity", "SupportMapFragment not found!");
        }
    }


    private void openCityList(City city) {
        Bundle args = new Bundle();
        args.putParcelable("city", city);

        ReceivedListDialogFragment dialogFragment = new ReceivedListDialogFragment();
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "CityListDialog");
    }

    private City parseCityListFromParam(String cityListParam) {
        String[] parts = cityListParam.split(":");
        String cityName = parts[0];
        String[] restaurantIds = parts[1].split(",");
        ArrayList<String> restaurantIdList =  new ArrayList<>(Arrays.asList(restaurantIds));
        return new City(cityName, restaurantIdList);
    }




    private void replaceFragment(Fragment f) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment_map, f);
        fragmentTransaction.commit();

    }


}
