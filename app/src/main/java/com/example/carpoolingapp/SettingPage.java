package com.example.carpoolingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import org.json.JSONObject;
import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public class SettingPage extends AppCompatActivity {

    private RecyclerView tripRecyclerView;
    private TripHistoryAdapter adapter;
    private TextView emptyTextView;
    private List<Trip> tripList = new ArrayList<>();

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

        loadAllSavedTrips();

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

    private void loadAllSavedTrips() {
        try {
            File file = new File(getFilesDir(), "rides_custom.json");
            if (!file.exists()) {
                emptyTextView.setVisibility(View.VISIBLE);
                tripRecyclerView.setVisibility(View.GONE);
                return;
            }

            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();

            String content = new String(data, StandardCharsets.UTF_8);
            JSONArray array = new JSONArray(content);

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                Trip trip = new Trip(
                        obj.getString("destination"),
                        Double.parseDouble(obj.getString("price").replace("$", "")),
                        obj.getString("driverName"),
                        obj.optString("date", "N/A"),
                        obj.optString("time", "N/A"),
                        obj.optString("tripId", generateFallbackId(obj))
                );
                tripList.add(trip);
            }

            sortTripsByDateTimeDesc(tripList);

            if (tripList.isEmpty()) {
                emptyTextView.setVisibility(View.VISIBLE);
                tripRecyclerView.setVisibility(View.GONE);
            } else {
                emptyTextView.setVisibility(View.GONE);
                tripRecyclerView.setVisibility(View.VISIBLE);
                adapter.updateTrips(tripList);
            }

        } catch (Exception e) {
            Log.e("SETTING_PAGE", "Error reading rides_custom.json: " + e.getMessage());
            e.printStackTrace();
            emptyTextView.setVisibility(View.VISIBLE);
            emptyTextView.setText("Error loading trips.");
            tripRecyclerView.setVisibility(View.GONE);
        }
    }

    private void sortTripsByDateTimeDesc(List<Trip> trips) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        trips.sort((t1, t2) -> {
            try {
                Date d1 = sdf.parse(t1.getDate() + " " + t1.getTime());
                Date d2 = sdf.parse(t2.getDate() + " " + t2.getTime());
                return d2.compareTo(d1); // Newest first
            } catch (Exception e) {
                return 0;
            }
        });
    }

    private String generateFallbackId(JSONObject obj) {
        try {
            return obj.getString("driverName") + "_" + obj.optString("date", "N/A") + "_" + obj.optString("time", "N/A");
        } catch (JSONException e) {
            return String.valueOf(System.currentTimeMillis());
        }
    }
}
