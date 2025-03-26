package com.minhtnn.panelway.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.minhtnn.panelway.models.AdvertisementSpace;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeViewModel extends ViewModel {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<List<AdvertisementSpace>> featuredSpaces = new MutableLiveData<>();
    private final MutableLiveData<List<AdvertisementSpace>> allSpaces = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final Set<String> activeStatusFilters = new HashSet<>();
    private String locationFilter = "";

    public HomeViewModel() {
        loadSpaces();
    }

    public void setStatusFilter(String status, boolean isActive) {
        if (isActive) {
            activeStatusFilters.add(status);
        } else {
            activeStatusFilters.remove(status);
        }
        loadSpaces();
    }

    public void setLocationFilter(String location) {
        this.locationFilter = location;
        loadSpaces();
    }

    private void loadSpaces() {
        Query query = db.collection("spaces");

        if (!activeStatusFilters.isEmpty()) {
            query = query.whereIn("status", new ArrayList<>(activeStatusFilters));
        }

        if (!locationFilter.isEmpty()) {
            query = query.whereEqualTo("location", locationFilter);
        }

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<AdvertisementSpace> spaces = new ArrayList<>();
            queryDocumentSnapshots.forEach(doc -> {
                AdvertisementSpace space = doc.toObject(AdvertisementSpace.class);
                spaces.add(space);
            });

            // Filter featured spaces
            List<AdvertisementSpace> featured = spaces.stream()
                    .filter(AdvertisementSpace::isFeatured)
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

            featuredSpaces.setValue(featured);
            allSpaces.setValue(spaces);
        }).addOnFailureListener(e -> {
            error.setValue("Failed to load spaces: " + e.getMessage());
        });
    }

    public LiveData<List<AdvertisementSpace>> getFeaturedSpaces() {
        return featuredSpaces;
    }

    public LiveData<List<AdvertisementSpace>> getAllSpaces() {
        return allSpaces;
    }

    public LiveData<String> getError() {
        return error;
    }
}