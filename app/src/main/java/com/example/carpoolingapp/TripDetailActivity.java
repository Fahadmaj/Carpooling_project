package com.example.carpoolingapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TripDetailActivity extends AppCompatActivity {

    private TextView destinationText, priceText, driverText, dateText, timeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);

        destinationText = findViewById(R.id.detailDestination);
        priceText = findViewById(R.id.detailPrice);
        driverText = findViewById(R.id.detailDriver);
        dateText = findViewById(R.id.detailDate);
        timeText = findViewById(R.id.detailTime);

        Trip trip = (Trip) getIntent().getSerializableExtra("trip");
        if (trip != null) {
            destinationText.setText("Destination: " + trip.getDestination());
            priceText.setText("Price: $" + String.format("%.2f", trip.getPrice()));
            driverText.setText("Driver: " + trip.getDriver());
            dateText.setText("Date: " + trip.getDate());
            timeText.setText("Time: " + trip.getTime());
        }
    }
}
