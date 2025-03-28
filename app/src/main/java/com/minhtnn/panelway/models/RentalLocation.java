package com.minhtnn.panelway.models;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class RentalLocation {
    private String id;
    private String code;
    private double latitude;
    private double longitude;
    private String address;
    private String panelSize;
    private String description;
    private Date postDate;
    private Date availableDate;
    private double price;
    private String status;
    private String spaceProviderId;
    private String managerId;

    private List<RentalLocationImage> rentalLocationImageList;

    public List<RentalLocationImage> getRentalLocationImageList() {
        return rentalLocationImageList;
    }

    public void setRentalLocationImageList(List<RentalLocationImage> rentalLocationImageList) {
        this.rentalLocationImageList = rentalLocationImageList;
    }

    // Constructors
    public RentalLocation() {}

    // Getters and Setters
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPanelSize() {
        return panelSize;
    }

    public void setPanelSize(String panelSize) {
        this.panelSize = panelSize;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public Date getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(Date availableDate) {
        this.availableDate = availableDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSpaceProviderId() {
        return spaceProviderId;
    }

    public void setSpaceProviderId(String spaceProviderId) {
        this.spaceProviderId = spaceProviderId;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RentalLocation that = (RentalLocation) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getCode(), that.getCode()) &&
                Objects.equals(getAddress(), that.getAddress()) &&
                Objects.equals(getDescription(), that.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getCode(), getAddress(), getDescription());
    }
}