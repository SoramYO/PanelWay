package com.minhtnn.panelway.ui.management;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.minhtnn.panelway.R;
import com.minhtnn.panelway.adapters.RentalLocationAdapter;
import com.minhtnn.panelway.databinding.FragmentAdSpaceManagementBinding;
import com.minhtnn.panelway.models.RentalLocation;
import com.minhtnn.panelway.models.request.RentalLocationRequest;

import java.util.Calendar;
import java.util.Date;

public class AdSpaceManagementFragment extends Fragment implements RentalLocationAdapter.OnLocationActionListener {

    private FragmentAdSpaceManagementBinding binding;
    private AdSpaceManagementViewModel viewModel;
    private RentalLocationAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAdSpaceManagementBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(AdSpaceManagementViewModel.class);

    setupRecyclerView();
    setupTabLayout();
    setupAddButton();
    setupLoadMoreButton();
    observeViewModel();
    }

    private void setupLoadMoreButton() {
    binding.loadMoreButton.setOnClickListener(v -> viewModel.loadMore());
}

    private void setupRecyclerView() {
        adapter = new RentalLocationAdapter(this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupTabLayout() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Tất cả"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Khả dụng"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Đã thuê"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Chờ duyệt"));

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String filter = null;
                switch (tab.getPosition()) {
                    case 0: // Tất cả
                        filter = null;
                        break;
                    case 1: // Khả dụng
                        filter = "Available";
                        break;
                    case 2: // Đã thuê
                        filter = "Occupied";
                        break;
                    case 3: // Chờ duyệt
                        filter = "Pending";
                        break;
                }
                viewModel.setStatusFilter(filter);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setupAddButton() {
        binding.addButton.setOnClickListener(v -> showAddEditDialog(null));
    }

    private void observeViewModel() {
        viewModel.getRentalLocations().observe(getViewLifecycleOwner(), locations -> {
            adapter.submitList(locations);
            binding.emptyView.setVisibility(locations.isEmpty() ? View.VISIBLE : View.GONE);
            binding.recyclerView.setVisibility(locations.isEmpty() ? View.GONE : View.VISIBLE);
            binding.loadMoreButton.setVisibility(locations.isEmpty() ? View.GONE : View.VISIBLE);
        });

        viewModel.isLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
                viewModel.clearError();
            }
        });

        viewModel.getOperationSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(requireContext(), "Thao tác thành công", Toast.LENGTH_SHORT).show();
                viewModel.clearOperationSuccess();
            }
        });
    }

    @Override
    public void onEditClick(RentalLocation location) {
        showAddEditDialog(location);
    }

    @Override
    public void onDeleteClick(RentalLocation location) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa không gian quảng cáo " + location.getCode() + "?")
                .setPositiveButton("Xóa", (dialog, which) -> viewModel.deleteRentalLocation(location.getId()))
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onStatusClick(RentalLocation location) {
        String currentStatus = location.getStatus();
        String newStatus;

        switch (currentStatus) {
            case "Available":
                newStatus = "Occupied";
                break;
            case "Occupied":
                newStatus = "Available";
                break;
            case "Pending":
                newStatus = "Available";
                break;
            default:
                newStatus = "Available";
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Thay đổi trạng thái")
                .setMessage("Bạn có muốn thay đổi trạng thái từ " + currentStatus + " sang " + newStatus + "?")
                .setPositiveButton("Xác nhận", (dialog, which) -> viewModel.updateStatus(location.getId(), newStatus))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showAddEditDialog(RentalLocation location) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_rental_location, null);
        dialog.setContentView(dialogView);

        EditText codeInput = dialogView.findViewById(R.id.codeInput);
        EditText addressInput = dialogView.findViewById(R.id.addressInput);
        EditText descriptionInput = dialogView.findViewById(R.id.descriptionInput);
        EditText panelSizeInput = dialogView.findViewById(R.id.panelSizeInput);
        EditText priceInput = dialogView.findViewById(R.id.priceInput);
        EditText latitudeInput = dialogView.findViewById(R.id.latitudeInput);
        EditText longitudeInput = dialogView.findViewById(R.id.longitudeInput);
        DatePicker datePicker = dialogView.findViewById(R.id.datePicker);
        Spinner statusSpinner = dialogView.findViewById(R.id.statusSpinner);

        // Setup spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.rental_location_statuses,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);

        // Fill existing data if editing
        if (location != null) {
            codeInput.setText(location.getCode());
            addressInput.setText(location.getAddress());
            descriptionInput.setText(location.getDescription());
            panelSizeInput.setText(location.getPanelSize());
            priceInput.setText(String.valueOf(location.getPrice()));
            latitudeInput.setText(String.valueOf(location.getLatitude()));
            longitudeInput.setText(String.valueOf(location.getLongitude()));

            if (location.getAvailableDate() != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(location.getAvailableDate());
                datePicker.updateDate(
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                );
            }

            // Set status spinner selection
            String status = location.getStatus();
            for (int i = 0; i < adapter.getCount(); i++) {
                if (adapter.getItem(i).toString().equals(status)) {
                    statusSpinner.setSelection(i);
                    break;
                }
            }
        }

        dialogView.findViewById(R.id.cancelButton).setOnClickListener(v -> dialog.dismiss());

        dialogView.findViewById(R.id.saveButton).setOnClickListener(v -> {
            // Validate inputs
            if (codeInput.getText().toString().trim().isEmpty() ||
                    addressInput.getText().toString().trim().isEmpty() ||
                    panelSizeInput.getText().toString().trim().isEmpty() ||
                    priceInput.getText().toString().trim().isEmpty() ||
                    latitudeInput.getText().toString().trim().isEmpty() ||
                    longitudeInput.getText().toString().trim().isEmpty()) {

                Toast.makeText(requireContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                // Get values
                RentalLocationRequest request = new RentalLocationRequest();
                request.setCode(codeInput.getText().toString().trim());
                request.setAddress(addressInput.getText().toString().trim());
                request.setDescription(descriptionInput.getText().toString().trim());
                request.setPanelSize(panelSizeInput.getText().toString().trim());
                request.setPrice(Double.parseDouble(priceInput.getText().toString().trim()));
                request.setLatitude(Double.parseDouble(latitudeInput.getText().toString().trim()));
                request.setLongitude(Double.parseDouble(longitudeInput.getText().toString().trim()));
                request.setStatus(statusSpinner.getSelectedItem().toString());

                // Get date from DatePicker
                Calendar calendar = Calendar.getInstance();
                calendar.set(
                        datePicker.getYear(),
                        datePicker.getMonth(),
                        datePicker.getDayOfMonth()
                );
                request.setAvailableDate(calendar.getTime());

                if (location == null) {
                    // Create new
                    viewModel.createRentalLocation(request);
                } else {
                    // Update existing
                    viewModel.updateRentalLocation(location.getId(), request);
                }

                dialog.dismiss();

            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Định dạng số không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}