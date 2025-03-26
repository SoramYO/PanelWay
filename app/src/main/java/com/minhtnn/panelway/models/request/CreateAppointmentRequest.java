package com.minhtnn.panelway.models.request;

import java.util.Date;

public class CreateAppointmentRequest {
    private String code;
    private Date bookingDate;
    private String place;
    private int priority;
    private String adContentId;
    private String rentalLocationId;

    public CreateAppointmentRequest() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
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

    public String getAdContentId() {
        return adContentId;
    }

    public void setAdContentId(String adContentId) {
        this.adContentId = adContentId;
    }

    public String getRentalLocationId() {
        return rentalLocationId;
    }

    public void setRentalLocationId(String rentalLocationId) {
        this.rentalLocationId = rentalLocationId;
    }
}