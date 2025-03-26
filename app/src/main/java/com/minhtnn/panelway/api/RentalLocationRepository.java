package com.minhtnn.panelway.api;

import com.minhtnn.panelway.api.services.RentalLocationService;
import com.minhtnn.panelway.models.RentalLocation;
import com.minhtnn.panelway.models.request.RentalLocationRequest;
import com.minhtnn.panelway.models.response.RentalLocationsResponse;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RentalLocationRepository {
    private final RentalLocationService rentalLocationService;

    public RentalLocationRepository() {
        this.rentalLocationService = ApiClient.getClient().create(RentalLocationService.class);
    }

    public Single<RentalLocationsResponse> getAllRentalLocations(int page, int size) {
        return rentalLocationService.getAllRentalLocations(page, size)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<RentalLocation>> getRentalLocationsByProvider(String providerId, String status) {
        return rentalLocationService.getRentalLocationsByProvider(providerId, status)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<RentalLocation> getRentalLocationById(String id) {
        return rentalLocationService.getRentalLocationById(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<RentalLocation> createRentalLocation(RentalLocationRequest request) {
        return rentalLocationService.createRentalLocation(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<RentalLocation> updateRentalLocation(String id, RentalLocationRequest request) {
        return rentalLocationService.updateRentalLocation(id, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<RentalLocation> updateStatus(String id, String status) {
        return rentalLocationService.updateStatus(id, status)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable deleteRentalLocation(String id) {
        return rentalLocationService.deleteRentalLocation(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}