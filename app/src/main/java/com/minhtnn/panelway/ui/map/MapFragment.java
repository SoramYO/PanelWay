package com.minhtnn.panelway.ui.map;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.minhtnn.panelway.R;
import com.minhtnn.panelway.adapters.AdSpaceAdapter;
import com.minhtnn.panelway.databinding.FragmentMapBinding;
import com.minhtnn.panelway.models.AdvertisementSpace;
import com.minhtnn.panelway.models.RentalLocation;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.util.List;


public class MapFragment extends Fragment {
    private FragmentMapBinding binding;
    private MapViewModel viewModel;
    private MapView mapView;
    private IMapController mapController;
    private AdSpaceAdapter featuredAdapter;
    private CustomInfoWindow currentInfoWindow;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Configure Osmdroid before inflating the view
        Configuration.getInstance().load(requireContext(),
                PreferenceManager.getDefaultSharedPreferences(requireContext()));

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
        mapView = binding.mapFragment;

        // Correct method calls for Osmdroid
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        // Get map controller
        mapController = mapView.getController();

        // Set default location (Ho Chi Minh City)
        GeoPoint defaultLocation = new GeoPoint(10.762622, 106.660172);
        mapController.setCenter(defaultLocation);
        mapController.setZoom(12.0);
    }

    private void setupRecyclerView() {
        featuredAdapter = new AdSpaceAdapter(adSpace -> {
            // Navigate to details
            Navigation.findNavController(requireView())
                    .navigate(MapFragmentDirections.actionMapToDetails(adSpace.getId()));
        });

        binding.featuredRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.featuredRecyclerView.setAdapter(featuredAdapter);
    }

    private void observeData() {
        // Observe featured spaces
        viewModel.getFeaturedSpaces().observe(getViewLifecycleOwner(), spaces -> {
            featuredAdapter.submitList(spaces);
        });

        // Observe all spaces and update map markers
        viewModel.getAllSpaces().observe(getViewLifecycleOwner(), spaces -> {
            updateMapMarkers(spaces);
        });

        // Observe rental locations and add their markers
        viewModel.getRentalLocations().observe(getViewLifecycleOwner(), rentalLocations -> {
            addRentalLocationMarkers(rentalLocations);
        });

        // Observe and show errors
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateMapMarkers(List<AdvertisementSpace> spaces) {
        // Clear existing markers
        mapView.getOverlays().clear();

        // Add advertisement space markers
        for (AdvertisementSpace space : spaces) {
            Marker marker = new Marker(mapView);
            GeoPoint point = new GeoPoint(space.getLatitude(), space.getLongitude());

            marker.setPosition(point);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setTitle(space.getTitle());
            marker.setSnippet(space.getStatus());
            marker.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_location)); // Custom marker icon for ad spaces

            marker.setOnMarkerClickListener((clickedMarker, mapView) -> {
                Navigation.findNavController(requireView())
                        .navigate(MapFragmentDirections.actionMapToDetails(space.getId()));
                return true;
            });

            mapView.getOverlays().add(marker);
        }

        // Refresh map
        mapView.invalidate();
    }

    private void addRentalLocationMarkers(List<RentalLocation> rentalLocations) {
        for (RentalLocation location : rentalLocations) {
            Marker marker = new Marker(mapView);
            GeoPoint point = new GeoPoint(location.getLatitude(), location.getLongitude());

            marker.setPosition(point);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setTitle(location.getCode());
            marker.setSnippet("Rental Location");
            marker.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_location));

            marker.setOnMarkerClickListener((clickedMarker, mapView) -> {
                // Close any existing info window
                if (currentInfoWindow != null) {
                    currentInfoWindow.close();
                }

                // Create and show custom info window
                currentInfoWindow = new CustomInfoWindow(mapView, location);
                marker.setInfoWindow(currentInfoWindow);
                marker.showInfoWindow();
                return true;
            });

            mapView.getOverlays().add(marker);
        }

        // Refresh map
        mapView.invalidate();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Required for proper map rendering
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Required for proper map rendering
        mapView.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private class CustomInfoWindow extends InfoWindow {
        private final RentalLocation location;

        public CustomInfoWindow(MapView mapView, RentalLocation location) {
            super(R.layout.popup_rental_location, mapView);
            this.location = location;
        }

        @Override
        public void onOpen(Object item) {
            // Find views
            View view = mView;
            TextView tvLocationCode = view.findViewById(R.id.tvLocationCode);
            TextView tvLocationDetails = view.findViewById(R.id.tvLocationDetails);
            Button btnClose = view.findViewById(R.id.btnClose);
            Button btnViewDetails = view.findViewById(R.id.btnViewDetails);

            // Set location information
            tvLocationCode.setText(location.getCode());
            tvLocationDetails.setText(buildLocationDetailsText(location));

            // Close button
            btnClose.setOnClickListener(v -> close());

            // View Details button
            btnViewDetails.setOnClickListener(v -> {
                // Navigate to rental location details
//                if (getView() != null) {
//                    Navigation.findNavController(getView())
//                            .navigate(MapFragmentDirections.actionMapToRentalLocationDetails(location.getId()));
//                }
            });

        }
        private String buildLocationDetailsText(RentalLocation location) {
            StringBuilder details = new StringBuilder();

            // Add available details, adjust based on your RentalLocation class
            if (location.getCode() != null) {
                details.append("Name: ").append(location.getCode()).append("\n");
            }
            if (location.getAddress() != null) {
                details.append("Address: ").append(location.getAddress()).append("\n");
            }
            // Add more details as needed

            return details.toString();
        }

        @Override
        public void onClose() {
            // Any cleanup if needed
            currentInfoWindow = null;
        }
    }
}