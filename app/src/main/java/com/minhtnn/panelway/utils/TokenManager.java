package com.minhtnn.panelway.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Utility class to manage authentication tokens
 */
public class TokenManager {
    private static TokenManager instance;
    private SharedPreferences sharedPreferences;
    private static final String TOKEN_KEY = "auth_token";

    private TokenManager(Context context) {
        sharedPreferences = context.getSharedPreferences("TokenPrefs", Context.MODE_PRIVATE);
    }

    public static synchronized void init(Context context) {
        if (instance == null) {
            instance = new TokenManager(context.getApplicationContext());
        }
    }

    public static TokenManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("TokenManager must be initialized before use");
        }
        return instance;
    }

    public String getToken() {
        // Ensures an empty string is returned instead of null
        return sharedPreferences.getString(TOKEN_KEY, "");
    }

    public void saveToken(String token) {
        sharedPreferences.edit().putString(TOKEN_KEY, token).apply();
    }

    public void clearToken() {
        sharedPreferences.edit().remove(TOKEN_KEY).apply();
    }
}