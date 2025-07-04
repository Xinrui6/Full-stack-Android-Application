package com.example.a2866777l_development_project.Repository;

import android.net.Uri;
import android.util.Log;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;



public class ImageRepository {

    private final FirebaseStorage storage;
    private final StorageReference storageRef;

    public ImageRepository() {
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }

    public Task<Uri> uploadProfileImage(String userId, Uri imageUri) {
        StorageReference profileImageRef = storageRef.child("profile_images/" + userId + ".jpg");
        UploadTask uploadTask = profileImageRef.putFile(imageUri);

        return uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            return profileImageRef.getDownloadUrl();
        }).addOnSuccessListener(uri -> {
            Log.d("ProfileImage", "Image upload successful, URL: " + uri.toString());
        }).addOnFailureListener(e -> {
            Log.e("ProfileImage", "Failed to upload image", e);
        });
    }

    public Task<Uri> uploadResImages(String resId, Uri imageUri) {
        StorageReference resRef = storageRef.child("restaurant_images/" + resId);
        String fileName = System.currentTimeMillis() + ".jpg";
        StorageReference imageRef = resRef.child(fileName);
        UploadTask uploadTask = imageRef.putFile(imageUri);

        return uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            return imageRef.getDownloadUrl();
        }).addOnSuccessListener(uri -> {
            Log.d("resImage", "Image upload successful, URL: " + uri.toString());
        }).addOnFailureListener(e -> {
            Log.e("resImage", "Failed to upload image", e);
        });
    }

}
