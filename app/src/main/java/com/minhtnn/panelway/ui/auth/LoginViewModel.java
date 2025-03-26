package com.minhtnn.panelway.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.minhtnn.panelway.api.ApiClient;
import com.minhtnn.panelway.api.AuthRepository;
import com.minhtnn.panelway.api.services.AuthService;
import com.minhtnn.panelway.models.request.LoginRequest;
import com.minhtnn.panelway.models.response.AuthResponse;
import com.minhtnn.panelway.utils.TokenManager;
import com.minhtnn.panelway.utils.UserManager;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LoginViewModel extends ViewModel {
    private final AuthRepository authRepository;
    private final MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final CompositeDisposable disposables = new CompositeDisposable();

    public LoginViewModel() {
        authRepository = new AuthRepository();
    }

    public void login(String phoneNumber, String password, String role) {
        isLoading.setValue(true);
        
        disposables.add(
            authRepository.login(phoneNumber, password, role)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    this::handleLoginSuccess,
                    this::handleError
                )
        );
    }
    
        private void handleLoginSuccess(AuthResponse response) {
            isLoading.setValue(false);
            if (response.isSuccess()) {
                // Save the authentication token
                TokenManager.getInstance().saveToken(response.getJwtToken());
                
                // Save user info
                if (response.getAccountResponse() != null) {
                    UserManager.getInstance().saveUserInfo(
                        response.getAccountResponse().getId(),
                        response.getAccountResponse().getRole(),
                        response.getAccountResponse().getFullName()
                    );
                }
                loginSuccess.setValue(true);
            } else {
                errorMessage.setValue(response.getMessage());
            }
        }
    
    private void handleError(Throwable error) {
        isLoading.setValue(false);
        errorMessage.setValue("Login failed: " + error.getMessage());
    }
    
    public LiveData<Boolean> getLoginSuccess() {
        return loginSuccess;
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