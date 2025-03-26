package com.minhtnn.panelway.models.request;

public class RejectAppointmentRequest {
    private String reason;

    public RejectAppointmentRequest() {
        // Required empty constructor
    }

    public RejectAppointmentRequest(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}