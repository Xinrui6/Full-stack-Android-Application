package com.example.a2866777l_development_project.list.restaurantList;


import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a2866777l_development_project.R;
import com.example.a2866777l_development_project.list.ListViewModel;
import com.example.a2866777l_development_project.util.DynamicLinkUtils;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;


import java.util.ArrayList;

public class CityRestaurantsFragment extends Fragment {
    private static final String ARG_CITY_NAME = "city_name";
    private static final String ARG_RESTAURANT_IDS = "restaurant_ids";


    private String cityName;
    private RecyclerView recyclerView;
    private RestaurantListAdapter adapter;
    private ListViewModel listViewModel;
    private ArrayList<String> restaurantIds;
    private ArrayList<String> cityList = new ArrayList<>();



    public static CityRestaurantsFragment newInstance(String cityName,  ArrayList<String> restaurantIds) {
        CityRestaurantsFragment fragment = new CityRestaurantsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CITY_NAME, cityName);
        args.putStringArrayList(ARG_RESTAURANT_IDS, new ArrayList<>(restaurantIds));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cityName = getArguments().getString(ARG_CITY_NAME);
            restaurantIds = getArguments().getStringArrayList(ARG_RESTAURANT_IDS);
        }
        listViewModel = new ViewModelProvider(this).get(ListViewModel.class);
        listViewModel.fetchRestaurants(restaurantIds);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_city_restaurants, container, false);
        TextView city = view.findViewById(R.id.city_name);
        ImageButton shareButton = view.findViewById(R.id.share_button);
        city.setText(cityName);

        // Set up RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view_restaurants);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set up Adapter
        adapter = new RestaurantListAdapter(new ArrayList<>(), cityName, getContext());
        recyclerView.setAdapter(adapter);

        // Fetch and display restaurants for the city
        listViewModel.getRestaurants().observe(getViewLifecycleOwner(), restaurants -> {
            adapter.updateRestaurantList(restaurants);
        });

        Toolbar toolbar = view.findViewById(R.id.tool_bar);
        toolbar.setTitle("");

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back); // Use your back icon
        }

        toolbar.setNavigationOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });
        shareButton.setOnClickListener(v -> shareCityList());

        return view;
    }

    private void shareCityList() {
        DynamicLinkUtils.createCityListLink(cityName, restaurantIds, "cityList", new DynamicLinkUtils.OnLinkCreatedListener() {
            @Override
            public void onLinkCreated(String link) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "Check out this list in " + cityName + ": " + link);
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, "Share City List"));
            }

            @Override
            public void onError(Exception e) {
                Log.w(TAG, "Failed to create short link", e);
            }
        });
    }



}