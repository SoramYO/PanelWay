package com.minhtnn.panelway.api.services;

import com.minhtnn.panelway.models.User;


import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserService {
    @GET("users/{id}")
    Single<User> getUserById(@Path("id") String userId);
}