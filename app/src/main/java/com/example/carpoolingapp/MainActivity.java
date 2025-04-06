package com.example.carpoolingapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // need this for all activities
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {

                if (!(getClass().equals(MainActivity.class))) {
                    // If not, start MainActivity using an Intent
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                return true;
            } else if (item.getItemId() == R.id.nav_chat) {
                // Check if we're already on MessagePage
                if (!(getClass().equals(MessagePage.class))) {
                    // If not, start MessagePage using an Intent
                    Intent intent = new Intent(MainActivity.this, MessagePage.class);
                    startActivity(intent);
                }
                return true;
            } else if (item.getItemId() == R.id.nav_settings) {
                if (!(getClass().equals(SettingPage.class))) {
                    // If not, start MessagePage using an Intent
                    Intent intent = new Intent(MainActivity.this, SettingPage.class);
                    startActivity(intent);
                }
                return true;
            }

            return false;
        });
    }
}