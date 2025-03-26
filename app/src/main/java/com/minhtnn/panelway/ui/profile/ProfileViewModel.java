package com.minhtnn.panelway.ui.profile;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.minhtnn.panelway.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileViewModel extends ViewModel {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final MutableLiveData<User> userData = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> saveSuccess = new MutableLiveData<>();

    public ProfileViewModel() {
        loadUserData();
    }

    private void loadUserData() {
        String userId = auth.getCurrentUser().getUid();
        db.collection("users").document(userId)
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null) {
                        error.setValue("Failed to load profile: " + e.getMessage());
                        return;
                    }
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        userData.setValue(user);
                    }
                });
    }

    public void uploadProfileImage(Uri imageUri) {
        String imageName = UUID.randomUUID().toString();
        StorageReference ref = storage.getReference().child("profiles/" + imageName);

        ref.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    ref.getDownloadUrl().addOnSuccessListener(uri -> {
                        updateProfileImage(uri.toString());
                    });
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to upload image: " + e.getMessage());
                });
    }

    private void updateProfileImage(String imageUrl) {
        String userId = auth.getCurrentUser().getUid();
        db.collection("users").document(userId)
                .update("profileImage", imageUrl)
                .addOnFailureListener(e -> {
                    error.setValue("Failed to update profile image: " + e.getMessage());
                });
    }

    public void updateProfile(String name, String email, String phone) {
        String userId = auth.getCurrentUser().getUid();
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("email", email);
        updates.put("phone", phone);

        db.collection("users").document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    saveSuccess.setValue(true);
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to update profile: " + e.getMessage());
                });
    }

    public void logout() {
        auth.signOut();
    }

    public LiveData<User> getUserData() {
        return userData;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getSaveSuccess() {
        return saveSuccess;
    }
}