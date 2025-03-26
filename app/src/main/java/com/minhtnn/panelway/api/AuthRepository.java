package com.minhtnn.panelway.api;

import android.content.SharedPreferences;

import com.minhtnn.panelway.api.services.AuthService;
import com.minhtnn.panelway.models.response.AuthResponse;
import com.minhtnn.panelway.models.request.LoginRequest;

import io.reactivex.rxjava3.core.Single;

public class AuthRepository {
    private AuthService authService;
    private SharedPreferences prefs;

    public Single<AuthResponse> login(String email, String password) {
        return authService.login(new LoginRequest(email, password))
                .doOnSuccess(response -> saveAuthToken(response.getToken()));
    }

    private void saveAuthToken(String token) {
        prefs.edit().putString("token", token).apply();
    }
}