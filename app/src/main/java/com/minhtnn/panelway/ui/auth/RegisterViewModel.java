package com.minhtnn.panelway.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.minhtnn.panelway.api.ApiClient;
import com.minhtnn.panelway.api.services.AuthService;
import com.minhtnn.panelway.models.request.RegisterRequest;
import com.minhtnn.panelway.models.response.AuthResponse;
import com.minhtnn.panelway.utils.TokenManager;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RegisterViewModel extends ViewModel {
    private final AuthService authService;
    private final MutableLiveData<Boolean> registerSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final CompositeDisposable disposables = new CompositeDisposable();

    public RegisterViewModel() {
        authService = ApiClient.getClient().create(AuthService.class);
    }

    public void register(String email, String password, String fullName, String phone, String userName, int age, String gender, String role) {
        isLoading.setValue(true);
        RegisterRequest request = new RegisterRequest(email, password, fullName, phone, userName, age, gender, role);

        disposables.add(
                authService.register(request)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                this::handleRegisterSuccess,
                                this::handleError
                        )
        );
    }

    private void handleRegisterSuccess(AuthResponse response) {
        isLoading.setValue(false);
        if (response.isSuccess()) {
            // Save the authentication token if registration immediately logs in
            if (response.getToken() != null) {
                TokenManager.getInstance().saveToken(response.getToken());
            }
            registerSuccess.setValue(true);
        } else {
            errorMessage.setValue(response.getMessage());
        }
    }

    private void handleError(Throwable error) {
        isLoading.setValue(false);
        errorMessage.setValue("Registration failed: " + error.getMessage());
    }

    public LiveData<Boolean> getRegisterSuccess() {
        return registerSuccess;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }
}