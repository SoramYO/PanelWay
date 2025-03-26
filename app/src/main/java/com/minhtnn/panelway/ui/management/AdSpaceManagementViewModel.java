package com.minhtnn.panelway.ui.management;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.minhtnn.panelway.api.RentalLocationRepository;
import com.minhtnn.panelway.models.RentalLocation;
import com.minhtnn.panelway.models.request.RentalLocationRequest;
import com.minhtnn.panelway.utils.ErrorHandler;
import com.minhtnn.panelway.utils.UserManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class AdSpaceManagementViewModel extends ViewModel {
    private static final String TAG = "AdSpaceManagementVM";

    private final RentalLocationRepository repository;
    private final MutableLiveData<List<RentalLocation>> rentalLocations = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> operationSuccess = new MutableLiveData<>();
    private final CompositeDisposable disposables = new CompositeDisposable();

    private String currentStatusFilter = null;
    private int currentPage = 1;
    private final int pageSize = 10;

    public AdSpaceManagementViewModel() {
        repository = new RentalLocationRepository();
        loadRentalLocations();
    }

    public void loadRentalLocations() {
        loading.setValue(true);
        
        // Use general API endpoint with pagination
        disposables.add(
            repository.getAllRentalLocations(currentPage, pageSize)
                .subscribe(
                    response -> {
                        // Get locations from the response
                        List<RentalLocation> locations = response.getItems();
                        
                        // Apply client-side filtering if needed
                        if (currentStatusFilter != null && !currentStatusFilter.isEmpty()) {
                            locations = locations.stream()
                                    .filter(location -> currentStatusFilter.equals(location.getStatus()))
                                    .collect(Collectors.toList());
                        }
                        
                        rentalLocations.setValue(locations);
                        loading.setValue(false);
                    },
                    throwable -> {
                        Log.e(TAG, "Error loading rental locations", throwable);
                        error.setValue("Failed to load locations: " + ErrorHandler.getErrorMessage(throwable));
                        loading.setValue(false);
                    }
                )
        );
    }

    public void setStatusFilter(String status) {
        this.currentStatusFilter = status;
        currentPage = 1; // Reset to first page when changing filter
        loadRentalLocations();
    }

    public void loadMore() {
        currentPage++;
        loadRentalLocations();
    }

    public void createRentalLocation(RentalLocationRequest request) {
        loading.setValue(true);

        disposables.add(
                repository.createRentalLocation(request)
                        .subscribe(
                                location -> {
                                    List<RentalLocation> currentList = rentalLocations.getValue();
                                    if (currentList != null) {
                                        currentList.add(location);
                                        rentalLocations.setValue(currentList);
                                    }
                                    operationSuccess.setValue(true);
                                    loading.setValue(false);
                                },
                                throwable -> {
                                    Log.e(TAG, "Error creating rental location", throwable);
                                    error.setValue("Failed to create location: " + ErrorHandler.getErrorMessage(throwable));
                                    loading.setValue(false);
                                }
                        )
        );
    }

    public void updateRentalLocation(String id, RentalLocationRequest request) {
        loading.setValue(true);

        disposables.add(
                repository.updateRentalLocation(id, request)
                        .subscribe(
                                updatedLocation -> {
                                    List<RentalLocation> currentList = rentalLocations.getValue();
                                    if (currentList != null) {
                                        for (int i = 0; i < currentList.size(); i++) {
                                            if (currentList.get(i).getId().equals(id)) {
                                                currentList.set(i, updatedLocation);
                                                break;
                                            }
                                        }
                                        rentalLocations.setValue(currentList);
                                    }
                                    operationSuccess.setValue(true);
                                    loading.setValue(false);
                                },
                                throwable -> {
                                    Log.e(TAG, "Error updating rental location", throwable);
                                    error.setValue("Failed to update location: " + ErrorHandler.getErrorMessage(throwable));
                                    loading.setValue(false);
                                }
                        )
        );
    }

    public void updateStatus(String id, String status) {
        loading.setValue(true);

        disposables.add(
                repository.updateStatus(id, status)
                        .subscribe(
                                updatedLocation -> {
                                    List<RentalLocation> currentList = rentalLocations.getValue();
                                    if (currentList != null) {
                                        for (int i = 0; i < currentList.size(); i++) {
                                            if (currentList.get(i).getId().equals(id)) {
                                                currentList.set(i, updatedLocation);
                                                break;
                                            }
                                        }
                                        rentalLocations.setValue(currentList);
                                    }
                                    operationSuccess.setValue(true);
                                    loading.setValue(false);
                                },
                                throwable -> {
                                    Log.e(TAG, "Error updating status", throwable);
                                    error.setValue("Failed to update status: " + ErrorHandler.getErrorMessage(throwable));
                                    loading.setValue(false);
                                }
                        )
        );
    }

    public void deleteRentalLocation(String id) {
        loading.setValue(true);

        disposables.add(
                repository.deleteRentalLocation(id)
                        .subscribe(
                                () -> {
                                    List<RentalLocation> currentList = rentalLocations.getValue();
                                    if (currentList != null) {
                                        List<RentalLocation> updatedList = new ArrayList<>();
                                        for (RentalLocation location : currentList) {
                                            if (!location.getId().equals(id)) {
                                                updatedList.add(location);
                                            }
                                        }
                                        rentalLocations.setValue(updatedList);
                                    }
                                    operationSuccess.setValue(true);
                                    loading.setValue(false);
                                },
                                throwable -> {
                                    Log.e(TAG, "Error deleting rental location", throwable);
                                    error.setValue("Failed to delete location: " + ErrorHandler.getErrorMessage(throwable));
                                    loading.setValue(false);
                                }
                        )
        );
    }

    public LiveData<List<RentalLocation>> getRentalLocations() {
        return rentalLocations;
    }

    public LiveData<Boolean> isLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getOperationSuccess() {
        return operationSuccess;
    }

    public void clearError() {
        error.setValue(null);
    }

    public void clearOperationSuccess() {
        operationSuccess.setValue(null);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}