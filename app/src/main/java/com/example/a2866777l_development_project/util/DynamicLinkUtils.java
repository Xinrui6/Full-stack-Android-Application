package com.example.a2866777l_development_project.util;

import android.net.Uri;

import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

import java.util.ArrayList;

public class DynamicLinkUtils {

    public static void createResShareLink(String userId, String contentId, String contentType, OnLinkCreatedListener listener) {
        String link = "https://example.com/?userId=" + userId + "&contentId=" + contentId + "&contentType=" + contentType;
        String domainUriPrefix = "https://DiningFinder.page.link";

       linkBuilder(link, domainUriPrefix, listener);
    }

    public static void createCityListLink(String cityName, ArrayList<String> restaurantIds, String contentType, OnLinkCreatedListener listener) {
        StringBuilder cityListParam = new StringBuilder();
        cityListParam.append(cityName).append(":");
        for (String restaurantId : restaurantIds) {
            cityListParam.append(restaurantId).append(",");
        }
        cityListParam.setLength(cityListParam.length() - 1); // Remove the trailing comma

        String link = "https://example.com/?cityList=" + cityListParam + "&contentType=" + contentType;
        String domainUriPrefix = "https://DiningFinder.page.link" ;
        linkBuilder(link, domainUriPrefix, listener);

    }

    public static void createProfileLink(String userId, String contentType, OnLinkCreatedListener listener) {
        String link = "https://example.com/?userId=" + userId + "&contentType=" + contentType;
        String domainUriPrefix = "https://DiningFinder.page.link";

        linkBuilder(link, domainUriPrefix, listener);
    }

    private static void linkBuilder(String link, String domainUriPrefix, OnLinkCreatedListener listener) {
        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(link))
                .setDomainUriPrefix(domainUriPrefix)
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .buildShortDynamicLink()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Uri shortLink = task.getResult().getShortLink();
                        listener.onLinkCreated(shortLink.toString());
                    } else {
                        listener.onError(task.getException());
                    }
                });
    }

    public interface OnLinkCreatedListener {
        void onLinkCreated(String link);
        void onError(Exception e);
    }
}