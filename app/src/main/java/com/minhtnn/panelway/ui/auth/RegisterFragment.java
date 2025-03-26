package com.minhtnn.panelway.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.minhtnn.panelway.R;
import com.minhtnn.panelway.databinding.FragmentRegisterBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterFragment extends Fragment {
    private FragmentRegisterBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        binding.registerButton.setOnClickListener(v -> attemptRegister());
    }

    private void attemptRegister() {
        String email = binding.emailInput.getText().toString();
        String password = binding.passwordInput.getText().toString();
        String fullName = binding.fullNameInput.getText().toString();
        String phone = binding.phoneInput.getText().toString();

        if (email.isEmpty() || password.isEmpty() || fullName.isEmpty() || phone.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String userId = authResult.getUser().getUid();
                    saveUserData(userId, email, fullName, phone);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Registration failed: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void saveUserData(String userId, String email, String fullName, String phone) {
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("fullName", fullName);
        user.put("phone", phone);
        user.put("role", "client"); // Default role is client

        db.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Navigation.findNavController(requireView())
                            .navigate(R.id.action_register_to_otpVerification);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to save user data: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}