package com.minhtnn.panelway.api.services;

import com.minhtnn.panelway.models.RentalLocationImage;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RentalLocationImagesService {
    @GET("rental-location-images/rental-location/{id}")
    Single<List<RentalLocationImage>> getAllRentalLocationImageByRentalLocationIds(
            @Path("id") String id
    );
}
