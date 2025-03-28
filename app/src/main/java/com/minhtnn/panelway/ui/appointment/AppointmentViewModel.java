package com.minhtnn.panelway.ui.appointment;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.minhtnn.panelway.api.ApiClient;
import com.minhtnn.panelway.api.services.AppointmentService;
import com.minhtnn.panelway.models.Appointment;
import com.minhtnn.panelway.utils.ErrorHandler;
import com.minhtnn.panelway.models.request.CreateAppointmentRequest;
import com.minhtnn.panelway.utils.UserManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AppointmentViewModel extends ViewModel {
    private final MutableLiveData<Integer> pendingAppointments = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> bookingEnabled = new MutableLiveData<>(true);
    private final MutableLiveData<Boolean> bookingResult = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private String advertisementId;
    private final AppointmentService appointmentService;
    private final CompositeDisposable disposables = new CompositeDisposable();


    public AppointmentViewModel() {
        appointmentService = ApiClient.getClient().create(AppointmentService.class);
    }

    public void setAdvertisementId(String adId) {
        this.advertisementId = adId;
    }

public void checkAvailability(Date date) {
    if (date == null) return;
    
    // Get user ID and role from UserManager
    String accountId = UserManager.getInstance().getUserId();
    String role = UserManager.getInstance().getUserRole();
    
    // Log the values for debugging
    Log.d("AppointmentViewModel", "Using accountId: " + accountId);
    Log.d("AppointmentViewModel", "Using role: " + role);
    
    if (accountId.isEmpty()) {
        error.setValue("User not logged in");
        return;
    }
    
    // Format date for API request
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
    String bookDate = dateFormat.format(date);
    
    disposables.add(
        appointmentService.getAccountAppointments(accountId, role, bookDate)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                appointments -> {
                    // Log successful response
                    Log.d("AppointmentViewModel", "Received " + appointments.size() + " appointments");
                    
                    int count = 0;
                    for (Appointment appointment : appointments) {
                        if (appointment.getRentalLocationId().equals(advertisementId)) {
                            count++;
                        }
                    }
                    pendingAppointments.setValue(count);
                    bookingEnabled.setValue(count < 5);
                },
                throwable -> {
                    // Log error details
                    Log.e("AppointmentViewModel", "Error fetching appointments", throwable);
                    error.setValue(ErrorHandler.getErrorMessage(throwable));
                }
            )
    );
}

public void bookAppointment(Date date, String time) {
    try {
        // Get user information
        String accountId = UserManager.getInstance().getUserId();
        
        if (accountId.isEmpty()) {
            error.setValue("User not logged in");
            return;
        }
        
        // Parse time and combine with date
        String[] timeParts = time.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        
        CreateAppointmentRequest request = new CreateAppointmentRequest();
        request.setBookingDate(calendar.getTime());
        request.setRentalLocationId(advertisementId);
        request.setAdContentId(advertisementId); // This might need to be different
        request.setPriority(0);
        
        // Generate a random code
        String code = "APPT-" + System.currentTimeMillis() % 10000;
        request.setCode(code);
        
        // Set place from user's info
        request.setPlace(UserManager.getInstance().getUserName() + "'s location");
        
        disposables.add(
            appointmentService.createAppointment(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    appointment -> bookingResult.setValue(true),
                    throwable -> error.setValue(ErrorHandler.getErrorMessage(throwable))
                )
        );
    } catch (Exception e) {
        error.setValue("Failed to book appointment: " + e.getMessage());
    }
}
    public LiveData<Integer> getPendingAppointments() {
        return pendingAppointments;
    }
    
    public LiveData<Boolean> getBookingEnabled() {
        return bookingEnabled;
    }

    public LiveData<Boolean> getBookingResult() {
        return bookingResult;
    }

    public LiveData<String> getError() {
        return error;
    }
    
    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }
}