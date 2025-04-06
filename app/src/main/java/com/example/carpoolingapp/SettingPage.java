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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting_page); // reuse the layout file
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up RecyclerView and message
        tripRecyclerView = findViewById(R.id.tripRecyclerView);
        emptyTextView = findViewById(R.id.emptyTextView);
        tripRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TripHistoryAdapter(tripList, trip -> {
            Intent intent = new Intent(SettingPage.this, TripDetailActivity.class);
            intent.putExtra("trip", trip);
            startActivity(intent);
        });
        tripRecyclerView.setAdapter(adapter);

        loadTripsFromJson();

        // Bottom nav
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                if (!(getClass().equals(MainActivity.class))) {
                    startActivity(new Intent(this, MainActivity.class));
                }
                return true;
            } else if (item.getItemId() == R.id.nav_chat) {
                if (!(getClass().equals(MessagePage.class))) {
                    startActivity(new Intent(this, MessagePage.class));
                }
                return true;
            } else if (item.getItemId() == R.id.nav_settings) {
                // We're already here (Settings is now Trip History)
                return true;
            }
            return false;
        });
    }

    private void loadTripsFromJson() {
        try {
            InputStream is = getResources().openRawResource(R.raw.trips);
            Scanner scanner = new Scanner(is).useDelimiter("\\A");
            String json = scanner.hasNext() ? scanner.next() : "";
            scanner.close();

            JSONArray tripArray = new JSONArray(json);
            for (int i = 0; i < tripArray.length(); i++) {
                JSONObject obj = tripArray.getJSONObject(i);
                Trip trip = new Trip(
                        obj.getString("destination"),
                        obj.getDouble("price"),
                        obj.getString("driver"),
                        obj.getString("date"),
                        obj.getString("time")
                );
                tripList.add(trip);
            }

            if (tripList.isEmpty()) {
                emptyTextView.setVisibility(View.VISIBLE);
                tripRecyclerView.setVisibility(View.GONE);
            } else {
                emptyTextView.setVisibility(View.GONE);
                tripRecyclerView.setVisibility(View.VISIBLE);
                adapter.updateTrips(tripList);
            }

        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
            emptyTextView.setVisibility(View.VISIBLE);
            emptyTextView.setText("Error loading trips.");
        }
    }
}
