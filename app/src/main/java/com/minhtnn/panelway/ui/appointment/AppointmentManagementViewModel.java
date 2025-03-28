package com.minhtnn.panelway.ui.appointment;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.minhtnn.panelway.api.ApiClient;
import com.minhtnn.panelway.api.services.AppointmentService;
import com.minhtnn.panelway.models.Appointment;
import com.minhtnn.panelway.models.request.RejectAppointmentRequest;
import com.minhtnn.panelway.utils.ErrorHandler;
import com.minhtnn.panelway.utils.UserManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AppointmentManagementViewModel extends ViewModel {
    private static final String TAG = "AppointmentManagementVM";

    private final MutableLiveData<List<Appointment>> appointments = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<Date> selectedDate = new MutableLiveData<>(new Date());
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final AppointmentService appointmentService;


    private boolean isOwner = false;
    private String currentStatus = null; // Default to show all statuses

    public AppointmentManagementViewModel() {
        // Initialize API service
        appointmentService = ApiClient.getClient().create(AppointmentService.class);
        
        // Get role from UserManager
        String role = UserManager.getInstance().getUserRole();
        isOwner = "SpaceProvider".equals(role) || "Owner".equals(role);
        this.currentStatus = null;
        // Load appointments
        loadAppointments();
    }
    public void setStatusFilter(String status) {
        Log.d(TAG, "Setting status filter: " + status);
        
        // Handle null status properly
        if (status == null) {
            this.currentStatus = null; // Show all statuses
        } else {
            // Use switch or if statements for specific statuses
            switch(status) {
                case "Pending":
                    this.currentStatus = "Pending";
                    break;
                case "Confirmed":
                    this.currentStatus = "Confirmed";
                    break;
                case "Canceled":
                    this.currentStatus = "Canceled";
                    break;
                case "Expired":
                    this.currentStatus = "Expired";
                    break;
                default:
                    this.currentStatus = null; // Show all for any other input
            }
        }
        
        loadAppointments();
    }
    
    public void setSelectedDate(Date date) {
        selectedDate.setValue(date);
        loadAppointments();
    }

    public LiveData<Date> getSelectedDate() {
        return selectedDate;
    }


    public void updateAppointment(String appointmentId, String newStatus) {
        try {
            // Kiểm tra người dùng có đăng nhập không
            String accountId = UserManager.getInstance().getUserId();
            if (accountId.isEmpty()) {
                error.setValue("User not logged in");
                return;
            }

            // Gọi API để lấy thông tin cuộc hẹn trước khi cập nhật
            disposables.add(
                    appointmentService.getAppointmentById(appointmentId)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    appointment -> {
                                        Log.d(TAG, "Fetched appointment details: " + appointment.getId());

                                        // Lấy ngày hiện tại và định dạng theo chuẩn ISO
                                        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                                        String bookingDate = isoFormat.format(appointment.getBookingDate());

                                        // Tạo request body với thông tin cũ nhưng cập nhật status
                                        RejectAppointmentRequest request = new RejectAppointmentRequest();
                                        request.setId(appointment.getId());
                                        request.setStatus(newStatus);
                                        request.setBookingDate(bookingDate);
                                        request.setPlace(appointment.getPlace());
                                        request.setPriority(appointment.getPriority());
                                        request.setCode(appointment.getCode());

                                        Log.d(TAG, "Updating appointment: " + appointmentId + " to status: " + newStatus);
                                        Log.d(TAG, "Request: " + request.toString());

                                        // Gọi API PATCH để cập nhật trạng thái
                                        disposables.add(
                                                appointmentService.updateAppointment(request)
                                                        .subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe(
                                                                updatedAppointment -> {
                                                                    Log.d(TAG, "Appointment updated successfully!");
                                                                    loadAppointments(); // **Tải lại danh sách sau khi cập nhật thành công**
                                                                },
                                                                throwable -> {
                                                                    Log.e(TAG, "Failed to update appointment", throwable);
                                                                    error.setValue("Failed to update appointment: " + ErrorHandler.getErrorMessage(throwable));
                                                                }
                                                        )
                                        );
                                    },
                                    throwable -> {
                                        Log.e(TAG, "Failed to fetch appointment details", throwable);
                                        error.setValue("Failed to fetch appointment: " + ErrorHandler.getErrorMessage(throwable));
                                    }
                            )
            );
        } catch (Exception e) {
            error.setValue("Failed to update appointment: " + e.getMessage());
        }
    }



    public void loadAppointments() {
        loading.setValue(true);
        
        // Get user ID from UserManager
        String accountId = UserManager.getInstance().getUserId();
        String role = UserManager.getInstance().getUserRole();
        
        if (accountId.isEmpty()) {
            error.setValue("User not logged in");
            loading.setValue(false);
            return;
        }

        // Format date for API request
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
        String formattedDate = dateFormat.format(selectedDate.getValue());
        
        disposables.add(
            appointmentService.getAccountAppointments(accountId, role, formattedDate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    appointmentList -> {
                        Log.d(TAG, "Total appointments received: " + appointmentList.size());
                        
                        // Filter appointments
                        List<Appointment> filteredList = filterAppointments(appointmentList);
                        
                        Log.d(TAG, "Filtered appointments: " + filteredList.size());
                        
                        appointments.setValue(filteredList);
                        loading.setValue(false);
                    },
                    throwable -> {
                        Log.e(TAG, "Error loading appointments", throwable);
                        error.setValue("Failed to load appointments: " + ErrorHandler.getErrorMessage(throwable));
                        loading.setValue(false);
                    }
                )
        );
    }

    private List<Appointment> filterAppointments(List<Appointment> allAppointments) {
        // If no status filter is set, return all appointments
        if (currentStatus == null) {
            return allAppointments;
        }
        
        // Filter appointments by status
        List<Appointment> filteredList = new ArrayList<>();
        for (Appointment appointment : allAppointments) {
            Log.d(TAG, "Checking appointment: " + appointment.getCode() + 
                  ", Status: " + appointment.getStatus() + 
                  ", Current Filter: " + currentStatus);
            
            // Perform case-insensitive comparison
            if (currentStatus.equalsIgnoreCase(appointment.getStatus())) {
                filteredList.add(appointment);
            }
        }
        
        return filteredList;
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
    
    public LiveData<Boolean> getLoading() {
        return loading;
    }
    
    @Override
    protected void onCleared() {
        super.onCleared();
        // Clean up disposables to prevent memory leaks
        disposables.clear();
    }
}