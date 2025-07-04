package com.example.a2866777l_development_project.model;

import java.util.ArrayList;

public class Restaurant {
    String resId;
    String resName;
    double x;
    double y;
    String address;
    int priceLevel;
    String city;
    String phoneNumber;
    String openingHours;
    ArrayList<String> reviews;
    ArrayList<String> images;

    public Restaurant() {}

    public Restaurant(String restaurantId, String restaurantName, String city, double x, double y, String address, int price_level, String openingHours, String phoneNumber, ArrayList<String> reviews, ArrayList<String> images) {
        this.resId = restaurantId;
        this.resName = restaurantName;
        this.x = x;
        this.y = y;
        this.address = address;
        this.city = city;
        this.priceLevel = price_level;
        this.openingHours = openingHours;
        this.phoneNumber = phoneNumber;
        this.reviews = reviews;
        this.images = images;
    }

    public String getResId() {
        return resId;
    }

    public String getResName() {
        return resName;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public String getAddress() {
        return address;
    }

    public int getPriceLevel() {
        return priceLevel;
    }


    public String getCity() { return city; }

    public void setCity(String city) { this.city = city; }

    public String getPhoneNumber() { return this.phoneNumber; }

    public String getOpeningHours() { return this.openingHours; }

    public ArrayList<String> getImages() { return this.images; }

}
