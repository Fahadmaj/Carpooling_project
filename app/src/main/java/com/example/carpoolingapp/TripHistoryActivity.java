package com.example.carpoolingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class TripHistoryActivity extends AppCompatActivity {

    private RecyclerView tripRecyclerView;
    private TripHistoryAdapter adapter;
    private TextView emptyTextView;
    private List<Trip> tripList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_history);

        tripRecyclerView = findViewById(R.id.tripRecyclerView);
        emptyTextView = findViewById(R.id.emptyTextView);

        tripRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TripHistoryAdapter(tripList, trip -> {
            Intent intent = new Intent(TripHistoryActivity.this, TripDetailActivity.class);
            intent.putExtra("trip", trip);
            startActivity(intent);
        });
        tripRecyclerView.setAdapter(adapter);

        loadTripsFromJson();
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
                        obj.getString("time"),
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

        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
            emptyTextView.setVisibility(View.VISIBLE);
            emptyTextView.setText("Error loading trips.");
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
            return obj.getString("driver") + "_" + obj.getString("date") + "_" + obj.getString("time");
        } catch (JSONException e) {
            return String.valueOf(System.currentTimeMillis());
        }
    }
}
