package com.minhtnn.panelway.api.services;

import com.minhtnn.panelway.models.response.AuthResponse;
import com.minhtnn.panelway.models.request.LoginRequest;
import com.minhtnn.panelway.models.request.RegisterRequest;
import com.minhtnn.panelway.models.request.VerifyOtpRequest;
import com.minhtnn.panelway.models.response.VerifyOtpResponse;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {
    @POST("auth/login")
    Single<AuthResponse> login(@Body LoginRequest request);

    @POST("auth/sign-up")
    Single<AuthResponse> register(@Body RegisterRequest request);

    @POST("verify")
    Single<VerifyOtpResponse> verifyOtp(@Body VerifyOtpRequest request);
}