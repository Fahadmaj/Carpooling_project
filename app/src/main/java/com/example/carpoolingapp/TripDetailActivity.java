// TripDetailActivity.java
package com.example.carpoolingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;

public class TripDetailActivity extends AppCompatActivity {

    private TextView destinationText, priceText, driverText, dateText, timeText;
    private RatingBar ratingBar;
    private EditText commentEditText;
    private Button submitReviewButton, reportButton, backButton; // Added backButton

    private Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);

        destinationText = findViewById(R.id.detailDestination);
        priceText = findViewById(R.id.detailPrice);
        driverText = findViewById(R.id.detailDriver);
        dateText = findViewById(R.id.detailDate);
        timeText = findViewById(R.id.detailTime);
        ratingBar = findViewById(R.id.ratingBar);
        commentEditText = findViewById(R.id.commentEditText);
        submitReviewButton = findViewById(R.id.submitReviewButton);
        reportButton = findViewById(R.id.reportButton);
        backButton = findViewById(R.id.backButton); // Initialize backButton

        trip = (Trip) getIntent().getSerializableExtra("trip");
        if (trip != null) {
            destinationText.setText("Destination: " + trip.getDestination());
            priceText.setText("Price: $" + String.format("%.2f", trip.getPrice()));
            driverText.setText("Driver: " + trip.getDriver());
            dateText.setText("Date: " + trip.getDate());
            timeText.setText("Time: " + trip.getTime());
        }

        submitReviewButton.setOnClickListener(v -> {
            saveReview((int) ratingBar.getRating(), commentEditText.getText().toString(), false);
            Toast.makeText(this, "Review submitted!", Toast.LENGTH_SHORT).show();
            finish(); // Go back to TripHistoryActivity
        });

        reportButton.setOnClickListener(v -> {
            saveReview(1, "Reported for suspicious activity.", true);
            Toast.makeText(this, "Trip reported!", Toast.LENGTH_SHORT).show();
            finish(); // Go back to TripHistoryActivity
        });

        backButton.setOnClickListener(v -> {
            finish(); // Go back to TripHistoryActivity
        });

        copyReviewsFileIfNeeded();
    }

    private void copyReviewsFileIfNeeded() {
        File file = new File(getFilesDir(), "reviews.json");
        if (!file.exists()) {
            try (InputStream is = getResources().openRawResource(R.raw.reviews);
                 FileOutputStream fos = openFileOutput("reviews.json", MODE_PRIVATE)) {
                byte[] buffer = new byte[is.available()];
                is.read(buffer);
                fos.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveReview(int stars, String comment, boolean isReport) {
        File file = new File(getFilesDir(), "reviews.json");
        try {
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            reader.close();

            JSONObject root = new JSONObject(builder.toString());
            String driverName = trip.getDriver();

            if (!root.has(driverName)) {
                root.put(driverName, new JSONObject()
                        .put("ratings", new JSONArray())
                        .put("comments", new JSONArray())
                        .put("reports", 0));
            }

            JSONObject driverReviews = root.getJSONObject(driverName);
            driverReviews.getJSONArray("ratings").put(stars);
            driverReviews.getJSONArray("comments").put(comment);
            if (isReport) {
                int currentReports = driverReviews.getInt("reports");
                driverReviews.put("reports", currentReports + 1);
            }

            // Save back to file
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(root.toString(2)); // Indent with 2 spaces
            writer.close();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}