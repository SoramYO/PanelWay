package com.minhtnn.panelway;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.minhtnn.panelway.utils.TokenManager;

import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity {
    private NavController navController;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TokenManager.init(this);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        if (BuildConfig.DEBUG) {
            Log.d("API", "Setting maximum log size");
            try {
                Field field = Log.class.getDeclaredField("MAX_LOG_LINE_LENGTH");
                field.setAccessible(true);
                field.set(null, Integer.MAX_VALUE);
            } catch (Exception e) {
                Log.e("API", "Failed to increase log line length", e);
            }
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
        navController = navHostFragment.getNavController();

        // Check if user is logged in
        if (auth.getCurrentUser() != null) {
            navController.navigate(R.id.homeFragment);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}