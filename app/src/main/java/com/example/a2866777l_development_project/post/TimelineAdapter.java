package com.example.a2866777l_development_project.post;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.a2866777l_development_project.R;
import com.example.a2866777l_development_project.model.TimelineItem;

import java.util.ArrayList;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder> {

    private ArrayList<TimelineItem> timelineItems;

    public TimelineAdapter(ArrayList<TimelineItem> timelineItems) {
        this.timelineItems = timelineItems;
    }


    @Override
    public TimelineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timeline, parent, false);
        return new TimelineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TimelineViewHolder holder, int position) {
        TimelineItem item = timelineItems.get(position);
        holder.timelineText.setText(item.getText());
        holder.timelineTime.setText(item.getTimestamp());

        // Change icon based on the type of timeline event
        if (item.getType().equals("checkin")) {
            holder.timelineIcon.setImageResource(R.drawable.ic_check_in);
        } else if (item.getType().equals("review")) {
            holder.timelineIcon.setImageResource(R.drawable.rate_review);
        }
    }

    @Override
    public int getItemCount() {
        return timelineItems.size();
    }

    public static class TimelineViewHolder extends RecyclerView.ViewHolder {
        ImageView timelineIcon;
        TextView timelineText, timelineTime;

        public TimelineViewHolder(View itemView) {
            super(itemView);
            timelineIcon = itemView.findViewById(R.id.timeline_icon);
            timelineText = itemView.findViewById(R.id.timeline_text);
            timelineTime = itemView.findViewById(R.id.timeline_time);
        }
    }
}