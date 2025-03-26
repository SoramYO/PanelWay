package com.minhtnn.panelway;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.minhtnn.panelway.utils.TokenManager;
import com.minhtnn.panelway.utils.UserManager;

public class MainActivity extends AppCompatActivity {
    private NavController navController;
    private FirebaseAuth auth;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TokenManager.init(this);
        UserManager.init(this);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Set up the toolbar as ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        // Log initialization
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Application started in DEBUG mode");
        }

        // Set up window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Set up Navigation
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

            // Set up Bottom Navigation
            BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

            // Configure which destinations are considered top-level (no back button)
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.homeFragment,
                    R.id.mapFragment,
                    R.id.appointmentManagementFragment,
                    R.id.profileFragment)
                    .build();

            // Only setup the ActionBar if toolbar exists
            if (toolbar != null) {
                NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            }

            // Connect bottom navigation with NavController
            if (bottomNav != null) {
                bottomNav.setOnItemSelectedListener(item -> {
                    int itemId = item.getItemId();
                    if (itemId == R.id.home) {
                        navigateToDestination(R.id.homeFragment);
                        return true;
                    } else if (itemId == R.id.map) {
                        navigateToDestination(R.id.mapFragment);
                        return true;
                    } else if (itemId == R.id.appointments) {
                        navigateToDestination(R.id.appointmentManagementFragment);
                        return true;
                    } else if (itemId == R.id.profile) {
                        navigateToDestination(R.id.profileFragment);
                        return true;
                    }
                    return false;
                });

                // Update the selected item in the bottom navigation when navigation changes
                navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                    // Hide bottom navigation for authentication fragments
                    if (destination.getId() == R.id.loginFragment ||
                            destination.getId() == R.id.registerFragment ||
                            destination.getId() == R.id.otpVerificationFragment) {
                        bottomNav.setVisibility(View.GONE);
                    } else {
                        bottomNav.setVisibility(View.VISIBLE);

                        // Update selected item based on current destination
                        int id = destination.getId();
                        if (id == R.id.homeFragment) {
                            bottomNav.setSelectedItemId(R.id.home);
                        } else if (id == R.id.mapFragment) {
                            bottomNav.setSelectedItemId(R.id.map);
                        } else if (id == R.id.appointmentManagementFragment) {
                            bottomNav.setSelectedItemId(R.id.appointments);
                        } else if (id == R.id.profileFragment) {
                            bottomNav.setSelectedItemId(R.id.profile);
                        }
                    }
                });
            }

            // Improved authentication check
            checkAuthenticationStatus();
        }
    }

    private void navigateToDestination(int destinationId) {
        // Prevent continuous navigation to the same destination
        if (navController.getCurrentDestination() != null &&
                navController.getCurrentDestination().getId() != destinationId) {

            // Use NavOptions to clear the back stack and avoid multiple instances
            NavOptions navOptions = new NavOptions.Builder()
                    .setPopUpTo(destinationId, true)
                    .build();

            navController.navigate(destinationId, null, navOptions);
        }
    }

private void checkAuthenticationStatus() {
    // Get saved token and user information
    String token = TokenManager.getInstance().getToken();
    String userId = UserManager.getInstance().getUserId();
    
    Log.d(TAG, "Checking auth status - Token exists: " + !token.isEmpty() + ", User ID exists: " + !userId.isEmpty());
    
    // CHỈ điều hướng đến login khi CẢ HAI token VÀ userId đều rỗng
    if (token.isEmpty() && userId.isEmpty()) {
        Log.d(TAG, "No valid session found, redirecting to login");
        // Clear any existing back stack and navigate to login
        NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(R.id.nav_graph, true)
                .build();

        navController.navigate(R.id.loginFragment, null, navOptions);
    } else {
        // Nếu có ít nhất một thông tin (token hoặc userId), chuyển đến trang home
        Log.d(TAG, "Session information found, going to home screen");
        NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(R.id.nav_graph, true)
                .build();
        
        navController.navigate(R.id.homeFragment, null, navOptions);
    }
}

    private void validateTokenWithBackend() {
        // Implement your token validation logic here
        // If token is invalid, logout and navigate to login
        // Example pseudo-code:
        /*
        apiService.validateToken(token).enqueue(new Callback<TokenValidationResponse>() {
            @Override
            public void onResponse(Call<TokenValidationResponse> call, Response<TokenValidationResponse> response) {
                if (!response.isSuccessful() || !response.body().isValid()) {
                    // Token is invalid, logout
                    auth.signOut();
                    TokenManager.getInstance().clearToken();
                    navController.navigate(R.id.loginFragment);
                }
            }

            @Override
            public void onFailure(Call<TokenValidationResponse> call, Throwable t) {
                // Handle network errors
                navController.navigate(R.id.loginFragment);
            }
        });
        */
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}