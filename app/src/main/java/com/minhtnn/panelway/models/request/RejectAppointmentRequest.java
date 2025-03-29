package com.minhtnn.panelway.models.request;

import java.util.Date;

public class RejectAppointmentRequest {
    private String id;
    private String code;
    private String  bookingDate;
    private String place;
    private int priority;
    private String status;
    private String reason;

    public RejectAppointmentRequest() {
        // Required empty constructor
    }

    public RejectAppointmentRequest(String id, String code, String  bookingDate, String place, int priority, String status) {
        this.id = id;
        this.code = code;
        this.bookingDate = bookingDate;
        this.place = place;
        this.priority = priority;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String  getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String  bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}