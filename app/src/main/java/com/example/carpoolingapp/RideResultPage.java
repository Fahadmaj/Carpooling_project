package com.example.carpoolingapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.carpoolingapp.databinding.ActivityRideResultPageBinding;

public class RideResultPage extends AppCompatActivity {

    private ActivityRideResultPageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRideResultPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //  Set up the toolbar
        setSupportActionBar(binding.getRoot().findViewById(R.id.toolbar));

        //  Grab NavController from the fragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_ride_result_page);
        NavController navController = navHostFragment.getNavController();

        //  AppBar config: top-level destinations
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
        ).build();

        //  Link ActionBar + BottomNav to navigation
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }
}
