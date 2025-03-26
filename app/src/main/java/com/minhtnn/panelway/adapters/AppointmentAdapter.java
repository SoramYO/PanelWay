package com.minhtnn.panelway.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.minhtnn.panelway.databinding.ItemAppointmentBinding;
import com.minhtnn.panelway.models.Appointment;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class AppointmentAdapter extends ListAdapter<Appointment, AppointmentAdapter.AppointmentViewHolder> {
    private static final String TAG = "AppointmentAdapter";
    private final boolean isOwner;
    private final AppointmentCallback callback;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());

    public interface AppointmentCallback {
        void onAccept(String appointmentId);
        void onReject(String appointmentId);
    }

    public AppointmentAdapter(boolean isOwner, AppointmentCallback callback) {
        super(new DiffUtil.ItemCallback<Appointment>() {
            @Override
            public boolean areItemsTheSame(@NonNull Appointment oldItem, @NonNull Appointment newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Appointment oldItem, @NonNull Appointment newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.isOwner = isOwner;
        this.callback = callback;
        Log.d(TAG, "Adapter created. Is Owner: " + isOwner);
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAppointmentBinding binding = ItemAppointmentBinding.inflate(
            LayoutInflater.from(parent.getContext()), parent, false);
        return new AppointmentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = getItem(position);
        Log.d(TAG, "Binding appointment at position " + position + ": " + appointment.getId());
        holder.bind(appointment);
    }

    @Override
    public int getItemCount() {
        int count = super.getItemCount();
        Log.d(TAG, "Item count: " + count);
        return count;
    }

    class AppointmentViewHolder extends RecyclerView.ViewHolder {
        private final ItemAppointmentBinding binding;

        AppointmentViewHolder(ItemAppointmentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Appointment appointment) {
            try {
                // Detailed logging for debugging
                Log.d(TAG, "Binding appointment details:");
                Log.d(TAG, "  ID: " + appointment.getId());
                Log.d(TAG, "  Code: " + appointment.getCode());
                Log.d(TAG, "  Status: " + appointment.getStatus());
                Log.d(TAG, "  Place: " + appointment.getPlace());
                Log.d(TAG, "  Booking Date: " + appointment.getBookingDate());

                // Bind basic appointment details
                binding.appointmentCodeText.setText(appointment.getCode());
                binding.appointmentStatusText.setText(appointment.getStatus());
                binding.appointmentPlaceText.setText(appointment.getPlace());
                
                // Format and set booking date
                if (appointment.getBookingDate() != null) {
                    binding.appointmentDateText.setText(dateFormat.format(appointment.getBookingDate()));
                }

                // Configure buttons based on owner status and appointment status
                configureButtons(appointment);

            } catch (Exception e) {
                Log.e(TAG, "Error binding appointment", e);
            }
        }

        private void configureButtons(Appointment appointment) {
            // Buttons visibility and interaction based on user role and appointment status
            if (isOwner) {
                binding.actionButtonsLayout.setVisibility(View.VISIBLE);
                
                // Enable/disable buttons based on appointment status
                boolean canTakeAction = "Pending".equals(appointment.getStatus());
                binding.acceptButton.setEnabled(canTakeAction);
                binding.rejectButton.setEnabled(canTakeAction);

                binding.acceptButton.setOnClickListener(v -> {
                    Log.d(TAG, "Accept button clicked for appointment: " + appointment.getId());
                    callback.onAccept(appointment.getId());
                });

                binding.rejectButton.setOnClickListener(v -> {
                    Log.d(TAG, "Reject button clicked for appointment: " + appointment.getId());
                    callback.onReject(appointment.getId());
                });
            } else {
                // Hide action buttons for non-owners
                binding.actionButtonsLayout.setVisibility(View.GONE);
            }
        }
    }
}