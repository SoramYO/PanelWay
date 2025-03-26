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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.minhtnn.panelway.adapters.AppointmentAdapter;
import com.minhtnn.panelway.databinding.FragmentAppointmentManagementBinding;
import com.google.android.material.tabs.TabLayout;

public class AppointmentManagementFragment extends Fragment {
    private FragmentAppointmentManagementBinding binding;
    private AppointmentManagementViewModel viewModel;
    private AppointmentAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAppointmentManagementBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AppointmentManagementViewModel.class);

        setupRecyclerView();
        setupTabLayout();
        observeData();
    }

    private void setupRecyclerView() {
        adapter = new AppointmentAdapter(viewModel.isOwner(), new AppointmentAdapter.AppointmentCallback() {
            @Override
            public void onAccept(String appointmentId) {
                viewModel.updateAppointmentStatus(appointmentId, "confirmed");
            }

            @Override
            public void onReject(String appointmentId) {
                viewModel.updateAppointmentStatus(appointmentId, "canceled");
            }
        });

        binding.appointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.appointmentsRecyclerView.setAdapter(adapter);
    }

    private void setupTabLayout() {
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String status = tab.getText().toString().toLowerCase();
                viewModel.setStatusFilter(status);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void observeData() {
        viewModel.getAppointments().observe(getViewLifecycleOwner(), appointments -> {
            adapter.submitList(appointments);
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