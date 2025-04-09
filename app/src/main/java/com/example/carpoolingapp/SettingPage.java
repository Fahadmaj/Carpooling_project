package com.example.carpoolingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SettingPage extends AppCompatActivity {

    private RecyclerView tripRecyclerView;
    private TripHistoryAdapter adapter;
    private TextView emptyTextView;
    private List<Trip> tripList = new ArrayList<>();
    private String acceptedDriver;
    private String selectedDate;
    private String selectedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tripRecyclerView = findViewById(R.id.tripRecyclerView);
        emptyTextView = findViewById(R.id.emptyTextView);
        tripRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TripHistoryAdapter(tripList, trip -> {
            Intent intent = new Intent(SettingPage.this, TripDetailActivity.class);
            intent.putExtra("trip", trip);
            startActivity(intent);
        });
        tripRecyclerView.setAdapter(adapter);

        acceptedDriver = getIntent().getStringExtra("acceptedDriver");
        selectedDate = getIntent().getStringExtra("selectedDate");
        selectedTime = getIntent().getStringExtra("selectedTime");

        loadAcceptedTripFromJson();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                if (!(getClass().equals(MainActivity.class))) {
                    Intent intent = new Intent(SettingPage.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
                return true;
            } else if (item.getItemId() == R.id.nav_chat) {
                if (!(getClass().equals(MessagePage.class))) {
                    Intent intent = new Intent(SettingPage.this, MessagePage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
                return true;
            } else if (item.getItemId() == R.id.nav_settings) {
                return true;
            }
            return false;
        });
    }

    private void loadAcceptedTripFromJson() {
        if (acceptedDriver != null && !acceptedDriver.isEmpty())  {
            Trip trip = new Trip(
                    "Your destination",
                    15.99,
                    acceptedDriver,
                    selectedDate != null ? selectedDate : "N/A",
                    selectedTime != null ? selectedTime : "N/A"
            );
            tripList.add(trip);

            emptyTextView.setVisibility(View.GONE);
            tripRecyclerView.setVisibility(View.VISIBLE);
            adapter.updateTrips(tripList);
        } else {
            emptyTextView.setVisibility(View.VISIBLE);
            tripRecyclerView.setVisibility(View.GONE);
        }
    }
}