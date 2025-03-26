package com.minhtnn.panelway.ui.appointment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.minhtnn.panelway.databinding.FragmentAppointmentBinding;
import com.google.android.material.chip.Chip;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AppointmentFragment extends Fragment {
    private FragmentAppointmentBinding binding;
    private AppointmentViewModel viewModel;
    private Date selectedDate;
    private String selectedTime;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAppointmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String adId = AppointmentFragmentArgs.fromBundle(getArguments()).getAdId();
        viewModel = new ViewModelProvider(this).get(AppointmentViewModel.class);
        viewModel.setAdvertisementId(adId);

        setupViews();
        observeData();
    }

    private void setupViews() {
        binding.toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(requireView()).navigateUp());

        binding.calendarView.setMinDate(System.currentTimeMillis());
        binding.calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            selectedDate = calendar.getTime();
            viewModel.checkAvailability(selectedDate);
        });

        setupTimeSlots();

        binding.bookButton.setOnClickListener(v -> {
            if (selectedDate == null || selectedTime == null) {
                Toast.makeText(getContext(), "Please select date and time", Toast.LENGTH_SHORT).show();
                return;
            }
            viewModel.bookAppointment(selectedDate, selectedTime);
        });
    }

    private void setupTimeSlots() {
        binding.timeChipGroup.removeAllViews();
        String[] timeSlots = {"09:00", "10:00", "11:00", "14:00", "15:00", "16:00"};

        for (String time : timeSlots) {
            Chip chip = new Chip(requireContext());
            chip.setText(time);
            chip.setCheckable(true);
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedTime = time;
                    viewModel.checkAvailability(selectedDate);
                }
            });
            binding.timeChipGroup.addView(chip);
        }
    }

    private void observeData() {
        viewModel.getPendingAppointments().observe(getViewLifecycleOwner(), count -> {
            if (count > 0) {
                binding.pendingAppointmentsText.setText(
                        String.format("Current pending appointments: %d/5", count));
                binding.bookButton.setEnabled(count < 5);
            } else {
                binding.pendingAppointmentsText.setText("");
                binding.bookButton.setEnabled(true);
            }
        });

        viewModel.getBookingResult().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(getContext(), "Appointment booked successfully",
                        Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigateUp();
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}