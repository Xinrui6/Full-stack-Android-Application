package com.example.a2866777l_development_project.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class City implements Parcelable {
    private String name;
    private ArrayList<String> list;

    public City(String cityName, ArrayList<String> restaurantIds) {
        this.name = cityName;
        this.list = restaurantIds;
    }

    protected City(Parcel in) {
        name = in.readString();
        list = in.createStringArrayList();
    }

    public static final Creator<City> CREATOR = new Creator<City>() {
        @Override
        public City createFromParcel(Parcel in) {
            return new City(in);
        }

        @Override
        public City[] newArray(int size) {
            return new City[size];
        }
    };

    public String getName() {
        return name;
    }

    public ArrayList<String> getRestaurants() {
        return list;
    }

    public int getNumberOfRes() {
        return list.size();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(name);
        parcel.writeStringList(list);
    }

}
