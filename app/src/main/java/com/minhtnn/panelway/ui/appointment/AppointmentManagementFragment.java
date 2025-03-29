package com.minhtnn.panelway.ui.appointment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.minhtnn.panelway.R;
import com.minhtnn.panelway.adapters.AppointmentAdapter;
import com.minhtnn.panelway.databinding.FragmentAppointmentManagementBinding;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AppointmentManagementFragment extends Fragment {
    private FragmentAppointmentManagementBinding binding;
    private AppointmentManagementViewModel viewModel;
    private AppointmentAdapter adapter;
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    private static final class AppointmentStatus {
        static final String ALL = "All";
        static final String PENDING = "Pending";
        static final String CONFIRMED = "Confirmed";
        static final String CANCELED = "Canceled";
        static final String EXPIRED = "Expired";
    }

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
        setupDatePicker();
        observeData();
    }

    private void setupRecyclerView() {
        adapter = new AppointmentAdapter(viewModel.isOwner(), new AppointmentAdapter.AppointmentCallback() {
            @Override
            public void onAccept(String appointmentId) {
                viewModel.updateAppointment(appointmentId, "confirmed");
            }

            @Override
            public void onReject(String appointmentId) {
                viewModel.updateAppointment(appointmentId, "canceled");
            }
        });

    binding.appointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    binding.appointmentsRecyclerView.setAdapter(adapter);
    }

    private void setupTabLayout() {
        String[] tabTitles = {
                AppointmentStatus.ALL,
                AppointmentStatus.PENDING,
                AppointmentStatus.CONFIRMED,
                AppointmentStatus.CANCELED,
                AppointmentStatus.EXPIRED
        };

        for (String title : tabTitles) {
            TabLayout.Tab tab = binding.tabLayout.newTab();
            View customTab = LayoutInflater.from(getContext()).inflate(R.layout.custom_tab, null);
            TextView tabText = customTab.findViewById(R.id.tabText);
            tabText.setText(title);
            tab.setCustomView(customTab);
            binding.tabLayout.addTab(tab);
        }

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                TextView tabText = tab.getCustomView().findViewById(R.id.tabText);

                // Set selected tab color
                if (tabText != null) {
                    tabText.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
                }

                // Apply status filter
                String status = tabText.getText().toString();
                if (AppointmentStatus.ALL.equals(status)) {
                    viewModel.setStatusFilter(null);
                } else {
                    viewModel.setStatusFilter(status);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Reset unselected tab color
                TextView tabText = tab.getCustomView().findViewById(R.id.tabText);
                if (tabText != null) {
                    tabText.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorAccent));
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Optional: You can add any reselection logic here
            }
        });

        // Set the first tab (All) as selected by default
        if (binding.tabLayout.getTabCount() > 0) {
            binding.tabLayout.getTabAt(0).select();
        }
    }

    private void setupDatePicker() {
        binding.selectedDateText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            
            // If there's a selected date, use it
            Date currentDate = viewModel.getSelectedDate().getValue();
            if (currentDate != null) {
                calendar.setTime(currentDate);
            }

            new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedCalendar = Calendar.getInstance();
                    selectedCalendar.set(year, month, dayOfMonth);
                    viewModel.setSelectedDate(selectedCalendar.getTime());
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show();
        });
    }

    private void observeData() {
        viewModel.getAppointments().observe(getViewLifecycleOwner(), appointments -> {
            adapter.submitList(appointments);
            binding.emptyView.setVisibility(appointments.isEmpty() ? View.VISIBLE : View.GONE);
            binding.appointmentsRecyclerView.setVisibility(appointments.isEmpty() ? View.GONE : View.VISIBLE);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
        
        viewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.loadingIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
        
        viewModel.getSelectedDate().observe(getViewLifecycleOwner(), date -> {
            binding.selectedDateText.setText(dateFormatter.format(date));
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}