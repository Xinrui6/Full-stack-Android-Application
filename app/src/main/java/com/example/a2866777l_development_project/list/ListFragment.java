package com.example.a2866777l_development_project.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a2866777l_development_project.R;
import com.example.a2866777l_development_project.Repository.UserRepository;
import com.example.a2866777l_development_project.databinding.FragmentListBinding;
import com.example.a2866777l_development_project.model.City;
import com.example.a2866777l_development_project.profile.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListFragment extends Fragment {

    private FragmentListBinding binding;
    private UserRepository userRepository = new UserRepository();
    private RecyclerView recyclerView;
    private CityListAdapter cityListAdapter;
    private FirebaseAuth auth;
    private String userID;
    private TextView editTextView;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ListViewModel listViewModel =
                new ViewModelProvider(this).get(ListViewModel.class);
        View view = inflater.inflate(R.layout.fragment_list, container, false);



        binding = FragmentListBinding.inflate(inflater, container, false);
//        View root = binding.getRoot();

        auth = FirebaseAuth.getInstance();
        userID = auth.getCurrentUser().getUid();
        cityListAdapter = new CityListAdapter(getContext(), new ArrayList<>(), userRepository, userID);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(cityListAdapter);
        editTextView = view.findViewById(R.id.edit);



        listViewModel.fetchUserLists(userID);
        listViewModel.getCities().observe(getViewLifecycleOwner(), cityList -> {
            cityListAdapter.updateCityList(cityList);
        });

        editTextView.setOnClickListener(v -> {
            boolean isEditing = cityListAdapter.isEditingMode();
            cityListAdapter.setEditingMode(!isEditing);
            editTextView.setText(isEditing ? "Edit" : "Done");
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}