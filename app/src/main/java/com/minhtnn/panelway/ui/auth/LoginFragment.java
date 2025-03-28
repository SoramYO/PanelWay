package com.minhtnn.panelway.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.minhtnn.panelway.R;
import com.minhtnn.panelway.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private LoginViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().hide();
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        setupRoleSpinner();
        setupObservers();
        setupListeners();
    }

    private void setupRoleSpinner() {
        String[] roles = new String[]{"SpaceProvider", "AdvertisingClient"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                roles);
        binding.roleSpinner.setAdapter(adapter);
    }

    private void setupObservers() {
        viewModel.getLoginSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Navigation.findNavController(requireView()).navigate(R.id.action_login_to_home);
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.loginButton.setEnabled(!isLoading);
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
    }

    private void setupListeners() {
        binding.loginButton.setOnClickListener(v -> attemptLogin());
        binding.registerLink.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_login_to_register));
    }

    private void attemptLogin() {
        String phoneNumber = binding.phoneInput.getText().toString().trim();
        String password = binding.passwordInput.getText().toString().trim();
        String role = binding.roleSpinner.getSelectedItem().toString();

        if (phoneNumber.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.login(phoneNumber, password, role);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}