package com.minhtnn.panelway.ui.map;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.minhtnn.panelway.api.RentalLocationRepository;
import com.minhtnn.panelway.models.AdvertisementSpace;
import com.google.firebase.firestore.FirebaseFirestore;
import com.minhtnn.panelway.models.RentalLocation;
import com.minhtnn.panelway.models.response.RentalLocationsResponse;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MapViewModel extends ViewModel {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final RentalLocationRepository rentalLocationRepository = new RentalLocationRepository();

    private final MutableLiveData<List<AdvertisementSpace>> featuredSpaces = new MutableLiveData<>();
    private final MutableLiveData<List<AdvertisementSpace>> allSpaces = new MutableLiveData<>();
    private final MutableLiveData<List<RentalLocation>> rentalLocations = new MutableLiveData<>();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final MutableLiveData<String> error = new MutableLiveData<>();
    private int currentPage = 1;
    private boolean isLoading = false;

    public MapViewModel() {
        loadSpaces();
        loadRentalLocations();
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

    public void loadRentalLocations() {
        if (isLoading) return;

        isLoading = true;
        Disposable rentalLocationsDisposable = rentalLocationRepository.getAllRentalLocations(currentPage, 20)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            List<RentalLocation> newLocations = response.getItems();

                            // If it's the first page, set the value directly
                            if (currentPage == 1) {
                                rentalLocations.setValue(newLocations);
                            } else {
                                // Append new locations to existing list
                                List<RentalLocation> currentLocations = rentalLocations.getValue();
                                if (currentLocations == null) {
                                    currentLocations = new ArrayList<>();
                                }
                                currentLocations.addAll(newLocations);
                                rentalLocations.setValue(currentLocations);
                            }

                            // Increment page for next load
                            currentPage++;
                            isLoading = false;
                        },
                        error -> {
                            Log.e("API", "Error fetching rental locations", error);
                            this.error.setValue("Failed to load rental locations: " + error.getMessage());
                            isLoading = false;
                        }
                );
        compositeDisposable.add(rentalLocationsDisposable);
    }

    public LiveData<List<AdvertisementSpace>> getFeaturedSpaces() {
        return featuredSpaces;
    }

    public LiveData<List<AdvertisementSpace>> getAllSpaces() {
        return allSpaces;
    }

    public LiveData<List<RentalLocation>> getRentalLocations() {
        return rentalLocations;
    }

    public LiveData<String> getError() {
        return error;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}