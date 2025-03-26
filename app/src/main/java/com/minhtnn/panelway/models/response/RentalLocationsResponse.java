package com.minhtnn.panelway.models.response;

import com.minhtnn.panelway.models.RentalLocation;
import java.util.List;

public class RentalLocationsResponse {
    private int size;
    private int page;
    private int total;
    private int totalPages;
    private List<RentalLocation> items;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<RentalLocation> getItems() {
        return items;
    }

    public void setItems(List<RentalLocation> items) {
        this.items = items;
    }
}