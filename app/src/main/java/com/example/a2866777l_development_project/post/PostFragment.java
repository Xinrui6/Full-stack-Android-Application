package com.example.a2866777l_development_project.post;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a2866777l_development_project.R;
import com.example.a2866777l_development_project.model.TimelineItem;
import com.example.a2866777l_development_project.util.Utils;

import java.util.ArrayList;

public class PostFragment extends Fragment {

    private RecyclerView recyclerView;
    private TimelineAdapter timelineAdapter;
    private ArrayList<TimelineItem> timelineItems;
    private String userId = Utils.getUserId();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PostViewModel postViewModel =
                new ViewModelProvider(this).get(PostViewModel.class);
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        Log.d("PostFragment", "User ID: " + userId);
        postViewModel.fetchTimeline(userId);
        recyclerView = view.findViewById(R.id.recyclerView_timeline);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        timelineItems = new ArrayList<>();
        timelineAdapter = new TimelineAdapter(timelineItems);
        recyclerView.setAdapter(timelineAdapter);

        postViewModel.getTimeline().observe(getViewLifecycleOwner(), items -> loadTimelineData(items, timelineAdapter));

        return view;
    }

    private void loadTimelineData(ArrayList<TimelineItem> items, TimelineAdapter timelineAdapter) {
        if (!items.isEmpty()) {
            timelineItems.clear();
            timelineItems.addAll(items);
            timelineAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerView.setAdapter(null);
    }


}