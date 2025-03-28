package com.minhtnn.panelway.api;

import com.minhtnn.panelway.api.services.RentalLocationImagesService;
import com.minhtnn.panelway.models.RentalLocation;
import com.minhtnn.panelway.models.RentalLocationImage;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RentalLocationImagesRepository {
    private final RentalLocationImagesService rentalLocationImagesService;

    public RentalLocationImagesRepository() {
        this.rentalLocationImagesService = ApiClient.getClient().create(RentalLocationImagesService.class);
    }

    public Single<List<RentalLocationImage>> getImagesByRentalLocationId(String id) {
        Single<List<RentalLocationImage>> images = rentalLocationImagesService.getAllRentalLocationImageByRentalLocationIds(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        return images;
    }
}
