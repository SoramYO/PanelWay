package com.minhtnn.panelway.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Utility class to manage authentication tokens
 */
public class TokenManager {
    private static final String PREF_NAME = "AuthPrefs";
    private static final String KEY_TOKEN = "auth_token";

    private static TokenManager instance;
    private SharedPreferences preferences;

    private TokenManager(Context context) {
        preferences = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized TokenManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("TokenManager must be initialized with init() method before getting instance");
        }
        return instance;
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new TokenManager(context);
        }
    }

    public void saveToken(String token) {
        preferences.edit().putString(KEY_TOKEN, token).apply();
    }

    public String getToken() {
        return preferences.getString(KEY_TOKEN, null);
    }

    public void clearToken() {
        preferences.edit().remove(KEY_TOKEN).apply();
    }
}