package com.minhtnn.panelway.utils;

import retrofit2.HttpException;

import com.google.gson.Gson;
import com.minhtnn.panelway.models.ApiError;

import java.io.IOException;

public class ErrorHandler {
    public static String getErrorMessage(Throwable throwable) {
        if (throwable instanceof HttpException) {
            HttpException httpException = (HttpException) throwable;
            try {
                String errorBody = httpException.response().errorBody().string();
                ApiError apiError = new Gson().fromJson(errorBody, ApiError.class);
                return apiError.getMessage();
            } catch (IOException e) {
                return "An error occurred";
            }
        }
        return throwable.getMessage();
    }
}