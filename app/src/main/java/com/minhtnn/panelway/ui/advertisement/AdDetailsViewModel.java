package com.minhtnn.panelway.ui.advertisement;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.minhtnn.panelway.models.AdvertisementSpace;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdDetailsViewModel extends ViewModel {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<AdvertisementSpace> adDetails = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public void loadAdDetails(String adId) {
        db.collection("spaces").document(adId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    AdvertisementSpace space = documentSnapshot.toObject(AdvertisementSpace.class);
                    if (space != null) {
                        adDetails.setValue(space);
                    } else {
                        error.setValue("Advertisement not found");
                    }
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to load details: " + e.getMessage());
                });
    }

    public LiveData<AdvertisementSpace> getAdDetails() {
        return adDetails;
    }

    public LiveData<String> getError() {
        return error;
    }
}