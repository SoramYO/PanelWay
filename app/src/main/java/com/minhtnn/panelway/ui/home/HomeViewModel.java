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

import io.reactivex.rxjava3.core.Single;
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
                .flatMap(this::loadImagesForLocations)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            // Update with locations that now have images
                            rentalLocationPaging.setValue(response);
                        },
                        throwable -> {
                            Log.e("API", "Error fetching rental locations", throwable);
                            error.setValue("Failed to load rental locations: " + throwable.getMessage());
                        }
                );

        compositeDisposable.add(rentalLocationsDisposable);
    }
    private Single<RentalLocationsResponse> loadImagesForLocations(RentalLocationsResponse response) {
        List<RentalLocation> locations = response.getItems();

        if (locations == null || locations.isEmpty()) {
            return Single.just(response);
        }

        // Create a list of Singles for image loading
        List<Single<RentalLocation>> imageLoadingSingles = locations.stream()
                .map(this::loadImagesForSingleLocation)
                .collect(Collectors.toList());

        // Combine all image loading operations
        return Single.zip(imageLoadingSingles, objects -> {
            // Convert back to original response type
            for (int i = 0; i < objects.length; i++) {
                locations.set(i, (RentalLocation) objects[i]);
            }
            return response;
        });
    }

    private Single<RentalLocation> loadImagesForSingleLocation(RentalLocation location) {
        return rentalLocationImagesRepository.getImagesByRentalLocationId(location.getId())
                .map(images -> {
                    // Only set images if the list is not null and not empty
                    if (images != null && !images.isEmpty()) {
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
                    }
                    return location;
                })
                .onErrorReturn(throwable -> {
                    // Handle error for this specific location
                    Log.e("ImageLoading", "Error fetching images for location " + location.getId(), throwable);
                    return location; // Return original location even if image loading fails
                });
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