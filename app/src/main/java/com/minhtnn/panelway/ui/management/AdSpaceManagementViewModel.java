package com.minhtnn.panelway.ui.management;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.minhtnn.panelway.models.AdvertisementSpace;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AdSpaceManagementViewModel extends ViewModel {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final MutableLiveData<List<AdvertisementSpace>> adSpaces = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final List<String> pendingImages = new ArrayList<>();

    public AdSpaceManagementViewModel() {
        loadAdSpaces();
    }

    private void loadAdSpaces() {
        String ownerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("spaces")
                .whereEqualTo("ownerId", ownerId)
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        error.setValue("Failed to load ad spaces: " + e.getMessage());
                        return;
                    }
                    List<AdvertisementSpace> spaces = value.toObjects(AdvertisementSpace.class);
                    adSpaces.setValue(spaces);
                });
    }

    public void uploadImage(Uri imageUri) {
        String imageName = UUID.randomUUID().toString();
        StorageReference ref = storage.getReference().child("spaces/" + imageName);

        ref.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    ref.getDownloadUrl().addOnSuccessListener(uri -> {
                        pendingImages.add(uri.toString());
                    });
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to upload image: " + e.getMessage());
                });
    }

    public void createAdSpace(String title, String description, String location, double price, String status) {
        String ownerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> space = new HashMap<>();
        space.put("ownerId", ownerId);
        space.put("title", title);
        space.put("description", description);
        space.put("location", location);
        space.put("price", price);
        space.put("status", status);
        space.put("images", pendingImages);
        space.put("createdAt", System.currentTimeMillis());

        db.collection("spaces")
                .add(space)
                .addOnSuccessListener(documentReference -> {
                    pendingImages.clear();
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to create ad space: " + e.getMessage());
                });
    }

    public void updateAdSpace(String spaceId, String title, String description,
                              String location, double price, String status) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("title", title);
        updates.put("description", description);
        updates.put("location", location);
        updates.put("price", price);
        updates.put("status", status);
        if (!pendingImages.isEmpty()) {
            updates.put("images", pendingImages);
        }

        db.collection("spaces").document(spaceId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    pendingImages.clear();
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to update ad space: " + e.getMessage());
                });
    }

    public void deleteAdSpace(String spaceId) {
        db.collection("spaces").document(spaceId)
                .delete()
                .addOnFailureListener(e -> {
                    error.setValue("Failed to delete ad space: " + e.getMessage());
                });
    }

    public LiveData<List<AdvertisementSpace>> getAdSpaces() {
        return adSpaces;
    }

    public LiveData<String> getError() {
        return error;
    }
}