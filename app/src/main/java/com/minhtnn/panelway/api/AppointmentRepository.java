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
import android.util.Log;

public class AppointmentRepository {
    private final AppointmentService apiService;
    private final Context context;

    public AppointmentRepository(Context context) {
        this.context = context;
        this.apiService = ApiClient.getClient().create(AppointmentService.class);
    }

    public Single<Appointment> createAppointment(CreateAppointmentRequest request) {
        Log.d("AppointmentRepository", "Starting createAppointment with request: " + request.toString());
        return apiService.createAppointment(request)
                .subscribeOn(Schedulers.io())
                .doOnSuccess(appointment -> Log.d("AppointmentRepository", "API response: " + appointment.toString()))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(this::scheduleReminders)
                .doOnError(throwable -> Log.e("AppointmentRepository", "Error in createAppointment: " + throwable.getMessage(), throwable));
    }

    private void scheduleReminders(Appointment appointment) {
        try {
            if (appointment == null || appointment.getAppointmentTime() == null) {
                Log.w("AppointmentRepository", "Cannot schedule reminder: appointment or appointmentTime is null");
                return;
            }

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
            } else {
                Log.d("AppointmentRepository", "Reminder time is in the past, not scheduling: " + timeUntilReminder);
            }
        } catch (Exception e) {
            Log.e("AppointmentRepository", "Error in scheduleReminders: " + e.getMessage(), e);
            // Không ném lại ngoại lệ để không làm gián đoạn luồng RxJava
        }
    }

    public Single<List<Appointment>> getAccountAppointments(String accountId, String role, String bookDate) {
        return apiService.getAccountAppointments(accountId, role, bookDate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    

    

}