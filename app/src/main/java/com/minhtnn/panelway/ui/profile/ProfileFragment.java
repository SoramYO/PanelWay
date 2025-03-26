package com.minhtnn.panelway.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.minhtnn.panelway.R;
import com.minhtnn.panelway.databinding.FragmentProfileBinding;
import com.minhtnn.panelway.utils.TokenManager;
import com.minhtnn.panelway.utils.UserManager;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupViews();
    }
    
    private void setupViews() {
        // Display user information
        binding.userNameText.setText(UserManager.getInstance().getUserName());
        binding.userRoleText.setText(UserManager.getInstance().getUserRole());
        
        // Setup logout button
        binding.logoutButton.setOnClickListener(v -> {
            // Clear user session
            FirebaseAuth.getInstance().signOut();
            TokenManager.getInstance().clearToken();
            UserManager.getInstance().clear();
            
            // Navigate to login
            Navigation.findNavController(requireView()).navigate(R.id.action_profile_to_login);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}