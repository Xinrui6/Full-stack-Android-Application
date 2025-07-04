package com.example.a2866777l_development_project.map;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.example.a2866777l_development_project.R; // Adjust the package name accordingly

class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mWindow;
//    private Context mContext;

    public CustomInfoWindowAdapter(Context context) {
        mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        // Use the default info window frame
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        render(marker, mWindow);
        return mWindow;
    }

    private void render(Marker marker, View view) {
        TextView title = view.findViewById(R.id.title);
        TextView openstatus = view.findViewById(R.id.open_now);
        ImageView checkInIcon = view.findViewById(R.id.check_in_icon);
        TextView recommendations = view.findViewById(R.id.recommendations);
        TextView disrecommendations = view.findViewById(R.id.disrecommendations);

        title.setText(marker.getTitle());

        Bundle markerData = (Bundle) marker.getTag();
        if (markerData != null) {
            // Update UI with additional data
            Boolean checkInStatus = markerData.getBoolean("checkInStatus");
            int recommendationCount = markerData.getInt("recommendations");
            int disrecommendationCount = markerData.getInt("disrecommendations");
            String openNow = markerData.getString("open_now");
            Log.d("CustomInfoWindowAdapter", "Open Now: " + openNow);
            if (openNow.equals("Open Now")) {
                openstatus.setTextColor(Color.GREEN);
            }else {
                openstatus.setTextColor(Color.RED);
            }
            openstatus.setText(openNow);

            if (checkInStatus) {
                checkInIcon.setVisibility(View.VISIBLE);
            } else {
                checkInIcon.setVisibility(View.GONE);
            }
            recommendations.setText(Integer.toString(recommendationCount));
            disrecommendations.setText(Integer.toString(disrecommendationCount));
        } else {
            checkInIcon.setVisibility(View.GONE);
            recommendations.setText("0");
            disrecommendations.setText("0");
        }
    }
}
