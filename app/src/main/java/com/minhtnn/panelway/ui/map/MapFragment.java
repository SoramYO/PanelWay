package com.minhtnn.panelway.ui.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.minhtnn.panelway.databinding.FragmentMapBinding;
import com.minhtnn.panelway.models.AdvertisementSpace;
import com.minhtnn.panelway.adapters.AdSpaceAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import com.minhtnn.panelway.R;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private FragmentMapBinding binding;
    private MapViewModel viewModel;
    private GoogleMap googleMap;
    private AdSpaceAdapter featuredAdapter;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    enableMyLocation();
                } else {
                    Toast.makeText(requireContext(), "Location permission is required to show your location", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MapViewModel.class);

        setupMap();
        setupRecyclerView();
        observeData();
    }

    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void setupRecyclerView() {
        featuredAdapter = new AdSpaceAdapter(adSpace -> {
//             Navigate to details
//             Uncomment and update navigation as needed
             Navigation.findNavController(requireView())
                 .navigate(MapFragmentDirections.actionMapToDetails(adSpace.getId()));
        });
        binding.featuredRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.featuredRecyclerView.setAdapter(featuredAdapter);
    }

    private void observeData() {
        viewModel.getFeaturedSpaces().observe(getViewLifecycleOwner(), spaces -> {
            featuredAdapter.submitList(spaces);
        });

        viewModel.getAllSpaces().observe(getViewLifecycleOwner(), spaces -> {
            if (googleMap != null) {
                updateMapMarkers(spaces);
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        enableMyLocation();

        // Set default location (e.g., city center)
        LatLng defaultLocation = new LatLng(10.762622, 106.660172); // Ho Chi Minh City
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f));

        // Update markers if data is already available
        viewModel.getAllSpaces().getValue();
    }

    private void updateMapMarkers(List<AdvertisementSpace> spaces) {
        googleMap.clear();
        for (AdvertisementSpace space : spaces) {
            LatLng position = new LatLng(space.getLatitude(), space.getLongitude());
            googleMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title(space.getTitle())
                    .snippet(space.getStatus()));
        }
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (googleMap != null) {
                googleMap.setMyLocationEnabled(true);
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}