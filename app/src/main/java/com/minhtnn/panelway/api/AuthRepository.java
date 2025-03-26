package com.minhtnn.panelway.api;

import android.content.Context;
import android.content.SharedPreferences;

import com.minhtnn.panelway.api.services.AuthService;
import com.minhtnn.panelway.models.response.AuthResponse;
import com.minhtnn.panelway.models.request.LoginRequest;
import com.minhtnn.panelway.utils.TokenManager;

import io.reactivex.rxjava3.core.Single;

public class AuthRepository {
    private final AuthService authService;
    private final TokenManager tokenManager;

    public AuthRepository() {
        // Initialize the AuthService using ApiClient
        authService = ApiClient.getClient().create(AuthService.class);
        tokenManager = TokenManager.getInstance();
    }

    public Single<AuthResponse> login(String phoneNumber, String password, String role) {
        return authService.login(new LoginRequest(phoneNumber, password, role))
                .doOnSuccess(response -> {
                    if (response.getJwtToken() != null) {
                        tokenManager.saveToken(response.getJwtToken());
                    }
                });
    }
}