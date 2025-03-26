package com.minhtnn.panelway.models;

import java.util.Date;

public class Appointment {
    private String id;
    private String code;
    private String spaceId;
    private String spaceTitle;
    private String location;
    private String clientId;
    private String ownerId;
    private Date appointmentTime;
    private Date bookingDate;
    private String status;
    private String notes;
    private boolean confirmed;
    private String place;
    private int priority;
    private String adContentId;
    private String rentalLocationId;

    // Required empty constructor for Firestore
    public Appointment() {}

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

    public String getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }

    public String getSpaceTitle() {
        return spaceTitle;
    }

    public void setSpaceTitle(String spaceTitle) {
        this.spaceTitle = spaceTitle;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Date getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(Date appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    // For compatibility with existing code
    public Date getDate() {
        return appointmentTime;
    }

    public void setDate(Date date) {
        this.appointmentTime = date;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
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