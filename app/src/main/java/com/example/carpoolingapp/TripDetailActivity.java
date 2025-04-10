package com.example.carpoolingapp;

import android.os.Bundle;
import android.view.View;
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
    private TextView thankYouText, rateLabel;
    private RatingBar ratingBar;
    private EditText commentEditText;
    private Button submitReviewButton, reportButton, backButton;

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
        backButton = findViewById(R.id.backButton);
        thankYouText = findViewById(R.id.thankYouText);
        rateLabel = findViewById(R.id.rateLabel);

        trip = (Trip) getIntent().getSerializableExtra("trip");
        if (trip != null) {
            destinationText.setText("Destination: " + trip.getDestination());
            priceText.setText("Price: $" + String.format("%.2f", trip.getPrice()));
            String avgRating = getAverageRating(trip.getDriver());
            driverText.setText("Driver: " + trip.getDriver() + avgRating);
            dateText.setText("Date: " + trip.getDate());
            timeText.setText("Time: " + trip.getTime());
        }

        if (hasRatedTrip(trip)) {
            hideReviewUI();
            rateLabel.setVisibility(View.GONE);
            thankYouText.setVisibility(View.VISIBLE);
        } else {
            submitReviewButton.setOnClickListener(v -> {
                saveReview((int) ratingBar.getRating(), commentEditText.getText().toString(), false);
                markTripAsRated(trip);
                Toast.makeText(this, "Review submitted!", Toast.LENGTH_SHORT).show();
                hideReviewUI();
                rateLabel.setVisibility(View.GONE);
                thankYouText.setVisibility(View.VISIBLE);
            });

            reportButton.setOnClickListener(v -> {
                saveReview(1, "Reported for suspicious activity.", true);
                markTripAsRated(trip);
                Toast.makeText(this, "Trip reported!", Toast.LENGTH_SHORT).show();
                hideReviewUI();
                rateLabel.setVisibility(View.GONE);
                thankYouText.setVisibility(View.VISIBLE);
            });
        }

        backButton.setOnClickListener(v -> finish());
        copyReviewsFileIfNeeded();
    }

    private void hideReviewUI() {
        ratingBar.setVisibility(View.GONE);
        commentEditText.setVisibility(View.GONE);
        submitReviewButton.setVisibility(View.GONE);
        reportButton.setVisibility(View.GONE);
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
            while ((line = reader.readLine()) != null) builder.append(line);
            reader.close();

            JSONObject root = new JSONObject(builder.toString());
            String driverName = trip.getDriver();

            if (!root.has(driverName)) {
                root.put(driverName, new JSONObject()
                        .put("ratings", new JSONArray())
                        .put("comments", new JSONArray())
                        .put("reportCount", 0));
            }

            JSONObject driverReviews = root.getJSONObject(driverName);
            driverReviews.getJSONArray("ratings").put(stars);
            driverReviews.getJSONArray("comments").put(comment);
            if (isReport) {
                int currentReports = driverReviews.getInt("reportCount");
                driverReviews.put("reportCount", currentReports + 1);
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(root.toString(2));
            writer.close();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private String getAverageRating(String driverName) {
        File file = new File(getFilesDir(), "reviews.json");
        if (!file.exists()) return "";

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) builder.append(line);
            reader.close();

            JSONObject root = new JSONObject(builder.toString());
            if (root.has(driverName)) {
                JSONArray ratings = root.getJSONObject(driverName).getJSONArray("ratings");
                int total = 0;
                for (int i = 0; i < ratings.length(); i++) {
                    total += ratings.getInt(i);
                }
                if (ratings.length() > 0) {
                    double average = (double) total / ratings.length();
                    return String.format(" (%.1f⭐)", average);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private boolean hasRatedTrip(Trip trip) {
        File file = new File(getFilesDir(), "rated_trips.json");
        if (!file.exists()) return false;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) builder.append(line);
            reader.close();

            JSONArray array = new JSONArray(builder.toString());
            String id = trip.getTripId();
            for (int i = 0; i < array.length(); i++) {
                if (array.getString(i).equals(id)) return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void markTripAsRated(Trip trip) {
        File file = new File(getFilesDir(), "rated_trips.json");
        JSONArray array = new JSONArray();

        try {
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) builder.append(line);
                reader.close();
                array = new JSONArray(builder.toString());
            }

            array.put(trip.getTripId());
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(array.toString());
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
