package com.minhtnn.panelway.ui.appointment;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.minhtnn.panelway.databinding.FragmentAppointmentConfirmationBinding;
import com.minhtnn.panelway.models.Appointment;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AppointmentConfirmationFragment extends Fragment {
    private FragmentAppointmentConfirmationBinding binding;
    private AppointmentConfirmationViewModel viewModel;
    private CountDownTimer countDownTimer;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAppointmentConfirmationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String appointmentId = AppointmentConfirmationFragmentArgs.fromBundle(getArguments()).getAppointmentId();
        viewModel = new ViewModelProvider(this).get(AppointmentConfirmationViewModel.class);
        viewModel.loadAppointment(appointmentId);

        setupViews();
        observeData();
    }

    private void setupViews() {
        binding.toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(requireView()).navigateUp());

        binding.confirmButton.setOnClickListener(v -> {
            viewModel.confirmAppointment();
        });

        binding.cancelButton.setOnClickListener(v -> {
            viewModel.cancelAppointment();
        });
    }

    private void observeData() {
        viewModel.getAppointment().observe(getViewLifecycleOwner(), this::updateUI);

        viewModel.getActionResult().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Navigation.findNavController(requireView()).navigateUp();
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(Appointment appointment) {
        binding.titleText.setText(appointment.getSpaceTitle());
        binding.locationText.setText(appointment.getLocation());
        binding.dateTimeText.setText(dateFormat.format(appointment.getDate()));

        startCountdownTimer(appointment.getDate().getTime() - System.currentTimeMillis());
    }

    private void startCountdownTimer(long millisUntilAppointment) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(millisUntilAppointment, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60;
                binding.confirmationTimeText.setText(String.format(Locale.getDefault(),
                        "Time remaining: %02d:%02d", hours, minutes));
            }

            @Override
            public void onFinish() {
                binding.confirmationTimeText.setText("Time expired");
                binding.confirmButton.setEnabled(false);
                viewModel.handleExpiredConfirmation();
            }
        }.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        binding = null;
    }
}