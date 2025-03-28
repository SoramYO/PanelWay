package com.minhtnn.panelway.ui.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.minhtnn.panelway.api.RentalLocationImagesRepository;
import com.minhtnn.panelway.api.RentalLocationRepository;
import com.minhtnn.panelway.models.AdvertisementSpace;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.minhtnn.panelway.models.RentalLocation;
import com.minhtnn.panelway.models.RentalLocationImage;
import com.minhtnn.panelway.models.response.RentalLocationsResponse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomeViewModel extends ViewModel {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final RentalLocationRepository rentalLocationRepository;
    private final RentalLocationImagesRepository rentalLocationImagesRepository;
    private final MutableLiveData<List<AdvertisementSpace>> featuredSpaces;
    private final MutableLiveData<List<AdvertisementSpace>> allSpaces;
    private final MutableLiveData<RentalLocationsResponse> rentalLocationPaging;
    private final MutableLiveData<String> error;
    private final Set<String> activeStatusFilters = new HashSet<>();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private String locationFilter = "";

    public HomeViewModel() {
        rentalLocationRepository = new RentalLocationRepository();
        rentalLocationImagesRepository = new RentalLocationImagesRepository();
        allSpaces = new MutableLiveData<>();
        featuredSpaces = new MutableLiveData<>();
        rentalLocationPaging = new MutableLiveData<>();
        error = new MutableLiveData<>();
        loadData();
    }

    private void loadData() {
        loadRentalLocations();
        loadSpaces();
    }

    private void loadRentalLocations() {
        Disposable rentalLocationsDisposable = rentalLocationRepository.getAllRentalLocations(1, 10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            // First, set the initial response
                            List<RentalLocation> locations = response.getItems();

                            // Load images for each rental location
                            if (locations != null && !locations.isEmpty()) {
                                loadRentalLocationImages(locations);
                            }

                            rentalLocationPaging.setValue(response);
                        },
                        error -> {
                            Log.e("API", "Error fetching rental locations", error);
                            this.error.setValue("Failed to load rental locations: " + error.getMessage());
                        }
                );

        compositeDisposable.add(rentalLocationsDisposable);
    }

    private void loadRentalLocationImages(List<RentalLocation> locations) {
        for (RentalLocation location : locations) {
            Disposable imageDisposable = rentalLocationImagesRepository.getImagesByRentalLocationId(location.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            images -> {
                                // Detailed debugging
                                Log.d("ImageLoading", "Location ID: " + location.getId());
                                Log.d("ImageLoading", "Images received: " + images.size());

                                // Extensive logging for each image
                                for (int i = 0; i < images.size(); i++) {
                                    RentalLocationImage image = images.get(i);
                                    Log.d("ImageLoading", "Image " + i + " details:");
                                    Log.d("ImageLoading", "  - ID: " + image.getId());
                                    Log.d("ImageLoading", "  - Original URL: " + image.getUrlImage());
                                    Log.d("ImageLoading", "  - Rental Location ID: " + image.getRentalLocationId());
                                    Log.d("ImageLoading", "  - Description: " + image.getDescription());
                                }

                                // Modify image mapping to preserve all original properties
                                List<RentalLocationImage> mappedImages = images.stream()
                                        .map(image -> {
                                            RentalLocationImage locationImage = new RentalLocationImage();
                                            locationImage.setId(image.getId());
                                            locationImage.setUrlImage(image.getUrlImage());
                                            locationImage.setDescription(image.getDescription());
                                            locationImage.setDaylight(image.isDaylight());
                                            locationImage.setRentalLocationId(image.getRentalLocationId());
                                            return locationImage;
                                        })
                                        .collect(Collectors.toList());

                                location.setRentalLocationImageList(mappedImages);

                                // Trigger an update of the entire list
                                RentalLocationsResponse currentResponse = rentalLocationPaging.getValue();
                                if (currentResponse != null) {
                                    rentalLocationPaging.setValue(currentResponse);
                                }
                            },
                            error -> {
                                Log.e("ImageLoading", "Error fetching images for location " + location.getId(), error);
                            }
                    );

            compositeDisposable.add(imageDisposable);
        }
    }

    private void loadSpaces() {
        Query query = db.collection("spaces");

        // Apply filters if any
        if (!activeStatusFilters.isEmpty()) {
            query = query.whereIn("status", new ArrayList<>(activeStatusFilters));
        }

        if (!locationFilter.isEmpty()) {
            query = query.whereEqualTo("location", locationFilter);
        }

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<AdvertisementSpace> spaces = new ArrayList<>();
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                AdvertisementSpace space = doc.toObject(AdvertisementSpace.class);
                spaces.add(space);
            }

            // Filter featured spaces
            List<AdvertisementSpace> featured = spaces.stream()
                    .filter(AdvertisementSpace::isFeatured)
                    .collect(Collectors.toList());

            featuredSpaces.setValue(featured);
            allSpaces.setValue(spaces);
        }).addOnFailureListener(e -> {
            error.setValue("Failed to load spaces: " + e.getMessage());
        });
    }

    public void setStatusFilter(String status, boolean isActive) {
        if (isActive) {
            activeStatusFilters.add(status);
        } else {
            activeStatusFilters.remove(status);
        }
        loadData();
    }

    public void setLocationFilter(String location) {
        this.locationFilter = location;
        loadData();
    }

    public LiveData<List<AdvertisementSpace>> getFeaturedSpaces() {
        return featuredSpaces;
    }

    public LiveData<List<AdvertisementSpace>> getAllSpaces() {
        return allSpaces;
    }

    public LiveData<RentalLocationsResponse> getRentalLocationsPaging() {
        return rentalLocationPaging;
    }

    public LiveData<String> getError() {
        return error;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear(); // Dispose of all subscriptions
    }
}