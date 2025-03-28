package com.minhtnn.panelway.models;

import java.util.Date;
import java.util.Objects;

public class RentalLocationImage {
    private String id;
    private String description;
    private String imageUrl;
    private boolean isDaylight;
    private String rentalLocationId;

    // Constructors
    public RentalLocationImage() {}

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRentalLocationId() {
        return rentalLocationId;
    }

    public void setRentalLocationId(String rentalLocationId) {
        this.rentalLocationId = rentalLocationId;
    }

    public boolean isDaylight() {
        return isDaylight;
    }

    public void setDaylight(boolean daylight) {
        isDaylight = daylight;
    }

    public String getUrlImage() {
        return imageUrl;
    }

    public void setUrlImage(String urlImage) {
        this.imageUrl = urlImage;
    }

    // Make sure to add equals and hashCode methods if needed
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RentalLocationImage that = (RentalLocationImage) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getUrlImage(), that.getUrlImage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUrlImage(), isDaylight(), getDescription());
    }
}