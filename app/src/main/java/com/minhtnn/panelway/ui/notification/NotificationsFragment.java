package com.minhtnn.panelway.ui.notification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.minhtnn.panelway.adapters.NotificationsAdapter;
import com.minhtnn.panelway.databinding.FragmentNotificationsBinding;

public class NotificationsFragment extends Fragment {
    private FragmentNotificationsBinding binding;
    private NotificationsViewModel viewModel;
    private NotificationsAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);

        setupRecyclerView();
        observeData();
    }

    private void setupRecyclerView() {
        adapter = new NotificationsAdapter(notification -> {
            // Handle notification click
            // Navigate or perform action based on notification type
        });
        binding.notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.notificationsRecyclerView.setAdapter(adapter);
    }

    private void observeData() {
        viewModel.getNotifications().observe(getViewLifecycleOwner(), notifications -> {
            if (notifications.isEmpty()) {
                binding.emptyView.setVisibility(View.VISIBLE);
                binding.notificationsRecyclerView.setVisibility(View.GONE);
            } else {
                binding.emptyView.setVisibility(View.GONE);
                binding.notificationsRecyclerView.setVisibility(View.VISIBLE);
                adapter.submitList(notifications);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}