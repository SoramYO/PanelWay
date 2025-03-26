package com.minhtnn.panelway.ui.appointment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.minhtnn.panelway.models.Appointment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

public class AppointmentManagementViewModel extends ViewModel {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<List<Appointment>> appointments = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private boolean isOwner = false; // Initialize with default value
    private String currentStatus = "pending";

    public AppointmentManagementViewModel() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                String role = documentSnapshot.getString("role");
                isOwner = "owner".equals(role);
                loadAppointments();
            })
            .addOnFailureListener(e -> {
                error.setValue("Failed to load user role: " + e.getMessage());
            });
    }

    public void setStatusFilter(String status) {
        this.currentStatus = status;
        loadAppointments();
    }

    private void loadAppointments() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query query = db.collection("appointments")
                .whereEqualTo("status", currentStatus);

        if (isOwner) {
            query = query.whereEqualTo("ownerId", userId);
        } else {
            query = query.whereEqualTo("userId", userId);
        }

        query.orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Appointment> appointmentList = queryDocumentSnapshots.toObjects(Appointment.class);
                    appointments.setValue(appointmentList);
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to load appointments: " + e.getMessage());
                });
    }

    public void updateAppointmentStatus(String appointmentId, String newStatus) {
        db.collection("appointments").document(appointmentId)
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    loadAppointments();
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to update appointment: " + e.getMessage());
                });
    }

    public boolean isOwner() {
        return isOwner;
    }

    public LiveData<List<Appointment>> getAppointments() {
        return appointments;
    }

    public LiveData<String> getError() {
        return error;
    }
}