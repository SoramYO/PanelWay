package com.minhtnn.panelway.ui.map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.minhtnn.panelway.models.AdvertisementSpace;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MapViewModel extends ViewModel {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<List<AdvertisementSpace>> featuredSpaces = new MutableLiveData<>();
    private final MutableLiveData<List<AdvertisementSpace>> allSpaces = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public MapViewModel() {
        loadSpaces();
    }

    private void loadSpaces() {
        db.collection("spaces")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
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
                })
                .addOnFailureListener(e -> {
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