package com.minhtnn.panelway.api.interceptors;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import com.minhtnn.panelway.utils.TokenManager;

/**
 * Interceptor to add authentication token to all API requests
 */
public class AuthInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        // Get the auth token from TokenManager or shared preferences
        String token = TokenManager.getInstance().getToken();

        // If token exists, add it to the request header
        if (token != null && !token.isEmpty()) {
            Request newRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .build();
            return chain.proceed(newRequest);
        }

        // If no token, proceed with the original request
        return chain.proceed(originalRequest);
    }
}