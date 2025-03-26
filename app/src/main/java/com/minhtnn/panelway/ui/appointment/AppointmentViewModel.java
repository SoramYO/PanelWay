package com.minhtnn.panelway.ui.appointment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AppointmentViewModel extends ViewModel {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<Integer> pendingAppointments = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> bookingResult = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private String advertisementId;

    public void setAdvertisementId(String adId) {
        this.advertisementId = adId;
    }

    public void checkAvailability(Date date) {
        if (date == null) return;

        db.collection("appointments")
                .whereEqualTo("advertisementId", advertisementId)
                .whereEqualTo("status", "pending")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    pendingAppointments.setValue(queryDocumentSnapshots.size());
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to check availability: " + e.getMessage());
                });
    }

    public void bookAppointment(Date date, String time) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> appointment = new HashMap<>();
        appointment.put("advertisementId", advertisementId);
        appointment.put("userId", userId);
        appointment.put("date", date);
        appointment.put("time", time);
        appointment.put("status", "pending");
        appointment.put("createdAt", new Date());

        db.collection("appointments")
                .add(appointment)
                .addOnSuccessListener(documentReference -> {
                    bookingResult.setValue(true);
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to book appointment: " + e.getMessage());
                });
    }

    public LiveData<Integer> getPendingAppointments() {
        return pendingAppointments;
    }

    public LiveData<Boolean> getBookingResult() {
        return bookingResult;
    }

    public LiveData<String> getError() {
        return error;
    }
}