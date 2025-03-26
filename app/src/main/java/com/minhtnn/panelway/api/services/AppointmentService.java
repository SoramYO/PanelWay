package com.minhtnn.panelway.api.services;

import com.minhtnn.panelway.models.Appointment;
import com.minhtnn.panelway.models.request.CreateAppointmentRequest;
import com.minhtnn.panelway.models.request.RejectAppointmentRequest;

import java.util.List;

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

    @GET("appointments")
    Single<List<Appointment>> getAppointments(
            @Query("status") String status,
            @Query("userId") String userId,
            @Query("role") String role
    );

    @PUT("appointments/{id}")
    Single<Appointment> confirmAppointment(@Path("id") String appointmentId);

    @PUT("appointments/{id}")
    Single<Appointment> rejectAppointment(
            @Path("id") String appointmentId,
            @Body RejectAppointmentRequest request
    );

    @PUT("appointments/{id}/cancel")
    Single<Appointment> cancelAppointment(@Path("id") String appointmentId);
}