package com.minhtnn.panelway.ui.management;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.minhtnn.panelway.databinding.FragmentAdSpaceManagementBinding;
import com.minhtnn.panelway.models.AdvertisementSpace;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

// Add these imports
import android.widget.Spinner;
import com.google.android.material.textfield.TextInputEditText;
import com.minhtnn.panelway.R;
import com.minhtnn.panelway.adapters.AdSpaceAdapter;

public class AdSpaceManagementFragment extends Fragment {
    private FragmentAdSpaceManagementBinding binding;
    private AdSpaceManagementViewModel viewModel;
    private AdSpaceAdapter adapter;
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Uri imageUri = result.getData().getData();
                    viewModel.uploadImage(imageUri);
                }
            });

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
        setupAddButton();
        observeData();
    }

    private void setupRecyclerView() {
        adapter = new AdSpaceAdapter(space -> {
            if (space != null) {
                showEditDialog(space);
            } else {
                showDeleteConfirmation(space);
            }
        });
        binding.adSpacesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.adSpacesRecyclerView.setAdapter(adapter);
    }

    private void setupAddButton() {
        binding.addButton.setOnClickListener(v -> showEditDialog(null));
    }

    private void showEditDialog(AdvertisementSpace space) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_ad_space_edit, null);
        setupDialogViews(dialogView, space);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(space == null ? "Add Ad Space" : "Edit Ad Space")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    saveAdSpace(dialogView, space);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void setupDialogViews(View dialogView, AdvertisementSpace space) {
        // Setup status spinner
        String[] statuses = {"Available", "Rented", "Pending"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, statuses);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner statusSpinner = (Spinner) dialogView.findViewById(R.id.statusSpinner);
        statusSpinner.setAdapter(statusAdapter);

        // Fill existing data if editing
        if (space != null) {
            ((TextInputEditText) dialogView.findViewById(R.id.titleInput)).setText(space.getTitle());
            ((TextInputEditText) dialogView.findViewById(R.id.descriptionInput)).setText(space.getDescription());
            ((TextInputEditText) dialogView.findViewById(R.id.locationInput)).setText(space.getLocation());
            ((TextInputEditText) dialogView.findViewById(R.id.priceInput)).setText(String.valueOf(space.getPrice()));
            statusSpinner.setSelection(getStatusPosition(space.getStatus(), statuses));
        }

        // Setup image upload
        dialogView.findViewById(R.id.uploadImageButton).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });
    }

    private void saveAdSpace(View dialogView, AdvertisementSpace existingSpace) {
        String title = ((TextInputEditText) dialogView.findViewById(R.id.titleInput)).getText().toString();
        String description = ((TextInputEditText) dialogView.findViewById(R.id.descriptionInput)).getText().toString();
        String location = ((TextInputEditText) dialogView.findViewById(R.id.locationInput)).getText().toString();
        String priceStr = ((TextInputEditText) dialogView.findViewById(R.id.priceInput)).getText().toString();
        String status = ((Spinner) dialogView.findViewById(R.id.statusSpinner)).getSelectedItem().toString();

        if (title.isEmpty() || description.isEmpty() || location.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);

        if (existingSpace == null) {
            viewModel.createAdSpace(title, description, location, price, status);
        } else {
            viewModel.updateAdSpace(existingSpace.getId(), title, description, location, price, status);
        }
    }

    private void showDeleteConfirmation(AdvertisementSpace space) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Ad Space")
                .setMessage("Are you sure you want to delete this ad space?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.deleteAdSpace(space.getId());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void observeData() {
        viewModel.getAdSpaces().observe(getViewLifecycleOwner(), spaces -> {
            adapter.submitList(spaces);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int getStatusPosition(String status, String[] statuses) {
        for (int i = 0; i < statuses.length; i++) {
            if (statuses[i].equals(status)) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}