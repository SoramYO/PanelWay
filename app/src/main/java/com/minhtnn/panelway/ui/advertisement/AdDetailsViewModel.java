package com.minhtnn.panelway.ui.advertisement;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.minhtnn.panelway.api.RentalLocationRepository;
import com.minhtnn.panelway.models.RentalLocation;
import com.minhtnn.panelway.models.request.RentalLocationRequest;
import com.minhtnn.panelway.utils.ErrorHandler;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class AdDetailsViewModel extends ViewModel {
    private static final String TAG = "AdDetailsViewModel";

    private final RentalLocationRepository repository;
    private final MutableLiveData<RentalLocation> rentalLocation = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> operationSuccess = new MutableLiveData<>();
    private final CompositeDisposable disposables = new CompositeDisposable();

    public AdDetailsViewModel() {
        repository = new RentalLocationRepository();
    }

    public void loadAdDetails(String adId) {
        loading.setValue(true);
        disposables.add(
                repository.getRentalLocationById(adId)
                        .subscribe(
                                location -> {
                                    rentalLocation.setValue(location);
                                    loading.setValue(false);
                                },
                                throwable -> {
                                    Log.e(TAG, "Error loading rental location", throwable);
                                    error.setValue("Failed to load details: " + ErrorHandler.getErrorMessage(throwable));
                                    loading.setValue(false);
                                }
                        )
        );
    }

    public void updateAdDetails(String adId, RentalLocationRequest request) {
        loading.setValue(true);
        disposables.add(
                repository.updateRentalLocation(adId, request)
                        .subscribe(
                                updatedLocation -> {
                                    rentalLocation.setValue(updatedLocation);
                                    operationSuccess.setValue(true);
                                    loading.setValue(false);
                                },
                                throwable -> {
                                    Log.e(TAG, "Error updating rental location", throwable);
                                    error.setValue("Failed to update details: " + ErrorHandler.getErrorMessage(throwable));
                                    loading.setValue(false);
                                }
                        )
        );
    }

    public void deleteAd(String adId) {
        loading.setValue(true);
        disposables.add(
                repository.deleteRentalLocation(adId)
                        .subscribe(
                                () -> {
                                    rentalLocation.setValue(null);
                                    operationSuccess.setValue(true);
                                    loading.setValue(false);
                                },
                                throwable -> {
                                    Log.e(TAG, "Error deleting rental location", throwable);
                                    error.setValue("Failed to delete ad: " + ErrorHandler.getErrorMessage(throwable));
                                    loading.setValue(false);
                                }
                        )
        );
    }

    public LiveData<RentalLocation> getRentalLocation() {
        return rentalLocation;
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
