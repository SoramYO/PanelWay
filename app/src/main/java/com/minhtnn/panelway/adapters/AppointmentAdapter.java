package com.minhtnn.panelway.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.minhtnn.panelway.databinding.ItemAppointmentBinding;
import com.minhtnn.panelway.models.Appointment;

public class AppointmentAdapter extends ListAdapter<Appointment, AppointmentAdapter.AppointmentViewHolder> {
    private final boolean isOwner;
    private final AppointmentCallback callback;

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
        holder.bind(appointment);
    }

    class AppointmentViewHolder extends RecyclerView.ViewHolder {
        private final ItemAppointmentBinding binding;

        AppointmentViewHolder(ItemAppointmentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Appointment appointment) {
            // Bind appointment data to views
            binding.getRoot().setOnClickListener(v -> {
                // Handle click if needed
            });

            if (isOwner) {
                binding.acceptButton.setOnClickListener(v -> 
                    callback.onAccept(appointment.getId()));
                binding.rejectButton.setOnClickListener(v -> 
                    callback.onReject(appointment.getId()));
            }
        }
    }
}