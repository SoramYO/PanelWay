package com.minhtnn.panelway.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.minhtnn.panelway.adapters.AdSpaceAdapter;
import com.minhtnn.panelway.adapters.AdSpaceAdapter.OnAdSpaceClickListener;
import com.minhtnn.panelway.adapters.AdvertisementCardAdapter;
import com.minhtnn.panelway.databinding.FragmentHomeBinding;
import com.google.android.material.chip.Chip;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    private AdSpaceAdapter featuredAdapter;
    private AdSpaceAdapter allSpacesAdapter;

    private AdvertisementCardAdapter allAdvertisementCardAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        setupRecyclerViews();
        setupFilters();
        setupSearch();
        observeData();
    }

    private void setupRecyclerViews() {

        AdSpaceAdapter.OnAdSpaceClickListener clickListener = adSpace ->
                Navigation.findNavController(requireView())
                        .navigate(HomeFragmentDirections.actionHomeToDetails(adSpace.getId()));

        featuredAdapter = new AdSpaceAdapter(clickListener);
        allSpacesAdapter = new AdSpaceAdapter(clickListener);

        AdvertisementCardAdapter.OnAdvertisementClickListener advertisementCardClickListener = advertisementCard ->
                Navigation.findNavController(requireView())
                        .navigate(HomeFragmentDirections.actionHomeToDetails(advertisementCard.getId()));
        allAdvertisementCardAdapter = new AdvertisementCardAdapter(advertisementCardClickListener);

        binding.featuredRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.featuredRecyclerView.setAdapter(featuredAdapter);

        binding.spacesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        binding.spacesRecyclerView.setAdapter(allSpacesAdapter);
        binding.spacesRecyclerView.setAdapter(allAdvertisementCardAdapter);
    }

    private void setupFilters() {
        binding.chipAvailable.setOnCheckedChangeListener((chip, isChecked) ->
                viewModel.setStatusFilter("Available", isChecked));
        binding.chipRented.setOnCheckedChangeListener((chip, isChecked) ->
                viewModel.setStatusFilter("Rented", isChecked));
        binding.chipPending.setOnCheckedChangeListener((chip, isChecked) ->
                viewModel.setStatusFilter("Pending", isChecked));
    }

    private void setupSearch() {
        binding.searchInput.setOnEditorActionListener((v, actionId, event) -> {
            viewModel.setLocationFilter(v.getText().toString());
            return true;
        });
    }

    private void observeData() {
        viewModel.getFeaturedSpaces().observe(getViewLifecycleOwner(), spaces -> {
            featuredAdapter.submitList(spaces);
        });

        viewModel.getAllSpaces().observe(getViewLifecycleOwner(), spaces -> {
            allSpacesAdapter.submitList(spaces);
        });

        viewModel.getRentalLocationsPaging().observe(getViewLifecycleOwner(), rentalLocationsPaging -> {
            allAdvertisementCardAdapter.submitList(rentalLocationsPaging.getItems());
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