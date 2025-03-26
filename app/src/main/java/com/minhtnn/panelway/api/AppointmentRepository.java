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

public class AppointmentRepository {
    private final AppointmentService apiService;
    private final Context context;

    public AppointmentRepository(Context context) {
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

    public Single<List<Appointment>> getAccountAppointments(String accountId, String role, String bookDate) {
        return apiService.getAccountAppointments(accountId, role, bookDate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    

    

}