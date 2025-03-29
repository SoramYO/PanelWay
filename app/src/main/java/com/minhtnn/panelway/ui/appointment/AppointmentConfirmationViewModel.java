package com.minhtnn.panelway.ui.appointment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.minhtnn.panelway.models.Appointment;
import com.google.firebase.firestore.FirebaseFirestore;

public class AppointmentConfirmationViewModel extends ViewModel {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<Appointment> appointment = new MutableLiveData<>();
    private final MutableLiveData<Boolean> actionResult = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private String appointmentId;

    public void loadAppointment(String appointmentId) {
        this.appointmentId = appointmentId;
        db.collection("appointments").document(appointmentId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Appointment apt = documentSnapshot.toObject(Appointment.class);
                    if (apt != null) {
                        appointment.setValue(apt);
                    } else {
                        error.setValue("Appointment not found");
                    }
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to load appointment: " + e.getMessage());
                });
    }

    public void confirmAppointment() {
        db.collection("appointments").document(appointmentId)
                .update("confirmed", true)
                .addOnSuccessListener(aVoid -> {
                    actionResult.setValue(true);
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to confirm appointment: " + e.getMessage());
                });
    }

    public void cancelAppointment() {
        db.collection("appointments").document(appointmentId)
                .update("status", "canceled")
                .addOnSuccessListener(aVoid -> {
                    actionResult.setValue(true);
                    loadAppointment(appointmentId);
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to cancel appointment: " + e.getMessage());
                });
    }

    public void handleExpiredConfirmation() {
        cancelAppointment();
    }

    public LiveData<Appointment> getAppointment() {
        return appointment;
    }

    public LiveData<Boolean> getActionResult() {
        return actionResult;
    }

    public LiveData<String> getError() {
        return error;
    }
}