package com.minhtnn.panelway.ui.advertisement;

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

import com.bumptech.glide.Glide;
import com.minhtnn.panelway.R;
import com.minhtnn.panelway.adapters.ImagePagerAdapter;
import com.minhtnn.panelway.databinding.FragmentAdDetailsBinding;
import com.minhtnn.panelway.models.AdvertisementSpace;
import com.google.firebase.auth.FirebaseAuth;


public class AdDetailsFragment extends Fragment {
    private FragmentAdDetailsBinding binding;
    private AdDetailsViewModel viewModel;
    private ImagePagerAdapter imagePagerAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAdDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String adId = AdDetailsFragmentArgs.fromBundle(getArguments()).getAdId();
        viewModel = new ViewModelProvider(this).get(AdDetailsViewModel.class);
        viewModel.loadAdDetails(adId);

        setupViews();
        observeData();
    }

    private void setupViews() {
        imagePagerAdapter = new ImagePagerAdapter();
        binding.imageViewPager.setAdapter(imagePagerAdapter);

        binding.toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(requireView()).navigateUp());

        binding.bookButton.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                AdDetailsFragmentDirections.ActionDetailsToAppointment action =
                        AdDetailsFragmentDirections.actionDetailsToAppointment(
                                viewModel.getAdDetails().getValue().getId());
                Navigation.findNavController(v).navigate(action);
            } else {
                Toast.makeText(getContext(), "Please login first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void observeData() {
        viewModel.getAdDetails().observe(getViewLifecycleOwner(), this::updateUI);
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(AdvertisementSpace space) {
        binding.titleText.setText(space.getTitle());
        binding.priceText.setText(String.format("$%.2f", space.getPrice()));
        binding.locationText.setText(space.getLocation());
        binding.descriptionText.setText(space.getDescription());
        binding.ownerName.setText(space.getOwnerName());
        binding.ratingBar.setRating(space.getRating());

        imagePagerAdapter.submitList(space.getImages());

        Glide.with(this)
                .load(space.getOwnerImage())
                .circleCrop()
                .into(binding.ownerImage);

        // Hide book button if current user is the owner
        if (FirebaseAuth.getInstance().getCurrentUser() != null &&
                FirebaseAuth.getInstance().getCurrentUser().getUid().equals(space.getOwnerId())) {
            binding.bookButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}