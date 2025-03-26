package com.minhtnn.panelway.api.services;

import com.minhtnn.panelway.models.Appointment;
import com.minhtnn.panelway.models.request.CreateAppointmentRequest;
import com.minhtnn.panelway.models.request.RejectAppointmentRequest;

import java.util.List;
import java.util.Date;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AppointmentService {
    @POST("appointments")
    Single<Appointment> createAppointment(@Body CreateAppointmentRequest request);

    @GET("appointments/account/{id}")
    Single<List<Appointment>> getAccountAppointments(
            @Path("id") String accountId,
            @Query("role") String role,
            @Query("bookDate") String bookDate
    );


    Completable cancelAppointment(String appointmentId);
}