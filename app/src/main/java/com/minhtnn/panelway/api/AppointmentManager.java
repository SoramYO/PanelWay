package com.minhtnn.panelway.api;

import com.minhtnn.panelway.api.services.AppointmentService;
import com.minhtnn.panelway.models.Appointment;
import com.minhtnn.panelway.models.request.CreateAppointmentRequest;
import com.minhtnn.panelway.models.request.RejectAppointmentRequest;
import com.minhtnn.panelway.utils.NotificationHelper;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

public class AppointmentManager {
    private final AppointmentService apiService;
    private final Context context;

    public AppointmentManager(Context context) {
        this.context = context;
        this.apiService = ApiClient.getClient().create(AppointmentService.class);
    }

    public Single<Appointment> createAppointment(CreateAppointmentRequest request) {
        return apiService.createAppointment(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(this::scheduleReminders);
    }

    private void scheduleReminders(Appointment appointment) {
        // Schedule 24-hour confirmation notification
        long hoursInMillis = TimeUnit.HOURS.toMillis(24);
        long timeUntilReminder = appointment.getAppointmentTime().getTime() - hoursInMillis - System.currentTimeMillis();
        
        if (timeUntilReminder > 0) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                NotificationHelper.createNotification(
                    context,
                    "Appointment Confirmation Needed",
                    "Please confirm your appointment scheduled for tomorrow"
                );
            }, timeUntilReminder);
        }
    }
    
    public Single<List<Appointment>> getAppointments(String status, String userId, String role) {
        return apiService.getAppointments(status, userId, role)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    
    public Single<Appointment> confirmAppointment(String appointmentId) {
        return apiService.confirmAppointment(appointmentId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    
    public Single<Appointment> rejectAppointment(String appointmentId, String reason) {
        RejectAppointmentRequest request = new RejectAppointmentRequest(reason);
        return apiService.rejectAppointment(appointmentId, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    
    public Single<Appointment> cancelAppointment(String appointmentId) {
        return apiService.cancelAppointment(appointmentId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}