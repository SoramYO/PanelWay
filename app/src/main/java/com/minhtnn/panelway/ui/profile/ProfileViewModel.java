package com.minhtnn.panelway.ui.profile;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.minhtnn.panelway.api.ApiClient;
import com.minhtnn.panelway.api.services.UserService;
import com.minhtnn.panelway.models.User;

import com.minhtnn.panelway.utils.ErrorHandler;
import com.minhtnn.panelway.utils.TokenManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.UUID;

public class ProfileViewModel extends ViewModel {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final UserService userService;
    private final MutableLiveData<User> userData = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> saveSuccess = new MutableLiveData<>();
    private final CompositeDisposable disposables = new CompositeDisposable();

    public ProfileViewModel() {
        userService = ApiClient.getClient().create(UserService.class);
        loadUserData();
    }

    private void loadUserData() {
        String userId = auth.getCurrentUser().getUid();
        
        disposables.add(userService.getUserById(userId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                user -> userData.setValue(user),
                throwable -> error.setValue(ErrorHandler.getErrorMessage(throwable))
            ));
    }

    public void uploadProfileImage(Uri imageUri) {
        String imageName = UUID.randomUUID().toString();
        StorageReference ref = storage.getReference().child("profiles/" + imageName);

        ref.putFile(imageUri)
            .addOnSuccessListener(taskSnapshot -> {
                ref.getDownloadUrl().addOnSuccessListener(uri -> {

                });
            })
            .addOnFailureListener(e -> {
                error.setValue("Failed to upload image: " + e.getMessage());
            });
    }


    public void logout() {
        TokenManager.getInstance().clearToken();
        auth.signOut();
    }

    public LiveData<User> getUserData() {
        return userData;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getSaveSuccess() {
        return saveSuccess;
    }
    
    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }
}