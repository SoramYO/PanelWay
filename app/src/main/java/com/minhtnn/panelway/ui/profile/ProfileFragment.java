package com.minhtnn.panelway.ui.profile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.minhtnn.panelway.R;
import com.minhtnn.panelway.databinding.FragmentProfileBinding;
import com.minhtnn.panelway.models.User;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Uri imageUri = result.getData().getData();
                    viewModel.uploadProfileImage(imageUri);
                }
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        setupViews();
        observeData();
    }

    private void setupViews() {
        binding.editImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        binding.saveButton.setOnClickListener(v -> {
            String name = binding.nameInput.getText().toString();
            String email = binding.emailInput.getText().toString();
            String phone = binding.phoneInput.getText().toString();

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.updateProfile(name, email, phone);
        });

        binding.logoutButton.setOnClickListener(v -> {
            viewModel.logout();
            // Navigate to login screen
            Navigation.findNavController(v).navigate(R.id.action_profile_to_login);
        });
    }

    private void observeData() {
        viewModel.getUserData().observe(getViewLifecycleOwner(), this::updateUI);

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getSaveSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(User user) {
        binding.nameInput.setText(user.getName());
        binding.emailInput.setText(user.getEmail());
        binding.phoneInput.setText(user.getPhone());
        binding.roleChip.setText(user.getRole());

        if (user.getProfileImage() != null) {
            Glide.with(this)
                    .load(user.getProfileImage())
                    .placeholder(R.drawable.default_profile)
                    .into(binding.profileImage);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}