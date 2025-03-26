package com.minhtnn.panelway.api.services;

import com.minhtnn.panelway.models.RentalLocation;
import com.minhtnn.panelway.models.request.RentalLocationRequest;
import com.minhtnn.panelway.models.response.RentalLocationsResponse;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RentalLocationService {
    // Get all rental locations with pagination
    @GET("rental-locations")
    Single<RentalLocationsResponse> getAllRentalLocations(
            @Query("page") int page,
            @Query("size") int size
    );

    // Get rental locations by space provider ID
    @GET("rental-locations/space-provider/{id}")
    Single<List<RentalLocation>> getRentalLocationsByProvider(
            @Path("id") String providerId,
            @Query("status") String status
    );

    // Get rental location by ID
    @GET("rental-locations/{id}")
    Single<RentalLocation> getRentalLocationById(
            @Path("id") String id
    );

    // Create new rental location
    @POST("rental-locations")
    Single<RentalLocation> createRentalLocation(
            @Body RentalLocationRequest request
    );

    // Update rental location
    @PUT("rental-locations/{id}")
    Single<RentalLocation> updateRentalLocation(
            @Path("id") String id,
            @Body RentalLocationRequest request
    );

    // Update rental location status
    @PUT("rental-locations/{id}/status")
    Single<RentalLocation> updateStatus(
            @Path("id") String id,
            @Query("status") String status
    );

    // Delete rental location
    @DELETE("rental-locations/{id}")
    Completable deleteRentalLocation(
            @Path("id") String id
    );
}