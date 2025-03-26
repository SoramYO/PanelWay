package com.minhtnn.panelway.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.minhtnn.panelway.R;
import com.minhtnn.panelway.databinding.FragmentRegisterBinding;

public class RegisterFragment extends Fragment {
    private FragmentRegisterBinding binding;
    private RegisterViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
        
        setupGenderSpinner();
        setupRoleSpinner();
        setupObservers();
        setupListeners();
    }
    
    private void setupGenderSpinner() {
        String[] genders = new String[]{"Male", "Female", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), 
                android.R.layout.simple_spinner_dropdown_item,
                genders);
        binding.genderSpinner.setAdapter(adapter);
    }
    
    private void setupRoleSpinner() {
        String[] roles = new String[]{"client", "owner"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), 
                android.R.layout.simple_spinner_dropdown_item,
                roles);
        binding.roleSpinner.setAdapter(adapter);
    }
    
    private void setupObservers() {
        viewModel.getRegisterSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_register_to_otpVerification);
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.registerButton.setEnabled(!isLoading);
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
    }

    private void setupListeners() {
        binding.registerButton.setOnClickListener(v -> attemptRegister());
    }

    private void attemptRegister() {
        String email = binding.emailInput.getText().toString().trim();
        String password = binding.passwordInput.getText().toString().trim();
        String fullName = binding.fullNameInput.getText().toString().trim();
        String phone = binding.phoneInput.getText().toString().trim();
        String userName = binding.userNameInput.getText().toString().trim();
        String ageStr = binding.ageInput.getText().toString().trim();
        String gender = binding.genderSpinner.getSelectedItem().toString();
        String role = binding.roleSpinner.getSelectedItem().toString();

        // Validate inputs
        if (email.isEmpty() || password.isEmpty() || fullName.isEmpty() || 
            phone.isEmpty() || userName.isEmpty() || ageStr.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Please enter a valid age", Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.register(email, password, fullName, phone, userName, age, gender, role);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}