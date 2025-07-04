package com.example.a2866777l_development_project.model;

import java.util.ArrayList;
import java.util.HashMap;

public class User {
    private String userID;
    private String username;
    private String homeTown;
    private String biography;
    private String email;
    private String password;
    private ArrayList<String> flavor;
    private ArrayList<String> following;
    private ArrayList<String> followers;
    private ArrayList<String> checkIns;
    private HashMap<String, ArrayList<String>> cityLists;


    public User() {}

    public User(String userID, String username, String password, String email,String homeTown, String biography, ArrayList<String> flavor, ArrayList<String> following, ArrayList<String> followers, ArrayList<String> checkIns, HashMap<String, ArrayList<String>> cityLists) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.email = email;
        this.homeTown = homeTown;
        this.biography = biography;
        this.flavor = flavor;
        this.following = following;
        this.followers = followers;
        this.checkIns = checkIns;
        this.cityLists = cityLists;
    }

    public String getUserID() {return userID;}

    public String getUsername() {return username;}

    public String getHomeTown() {return homeTown;}

    public String getBiography() {return biography;}

    public ArrayList<String> getFlavor() {return flavor;}

    public int getFollowing() {return following.size();}

    public int getFollowers() {return followers.size();}

    public int getCheckIns() {return checkIns.size();}




}
