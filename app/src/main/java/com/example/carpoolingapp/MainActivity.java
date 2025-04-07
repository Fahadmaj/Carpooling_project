// MainActivity.java
package com.example.carpoolingapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RawRes;
import androidx.appcompat.app.AlertDialog;
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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText dateInput, timeInput;
    RecyclerView rideList;
    RideAdapter adapter;
    List<Ride> rides = new ArrayList<>();
    TextView availableRidesTitle;
    private String selectedDate;
    private String selectedTime;
    private String acceptedDriverName;

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

        Button searchButton = findViewById(R.id.search_button);

        searchButton.setOnClickListener(v -> {
            availableRidesTitle.setVisibility(View.VISIBLE);
            rideList.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Searching for rides...", Toast.LENGTH_SHORT).show();

            rides.clear();

            try {
                JSONArray jsonArray = new JSONArray(loadJSONFromRawResource(R.raw.rides));
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    Ride ride = new Ride(
                            obj.getString("driverName"),
                            obj.getString("destination"),
                            obj.getString("price"),
                            obj.getString("seats"),
                            obj.optString("date", "N/A"),
                            obj.optString("time", "N/A")
                    );
                    rides.add(ride);
                }
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                Log.e("JSON", "JSON parsing error: " + e.getMessage());
                e.printStackTrace();
                Toast.makeText(this, "Failed to parse JSON rides data.", Toast.LENGTH_SHORT).show();
            } catch (NullPointerException e) {
                Log.e("JSON", "Null pointer exception during JSON processing: " + e.getMessage());
                e.printStackTrace();
                Toast.makeText(this, "Error processing rides data.", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e("JSON", "An unexpected error occurred: " + e.getMessage());
                e.printStackTrace();
                Toast.makeText(this, "An unexpected error occurred.", Toast.LENGTH_SHORT).show();
            }
        });

        rideList = findViewById(R.id.ride_list);
        availableRidesTitle = findViewById(R.id.available_rides_title);
        adapter = new RideAdapter(rides, this::onRideAccepted);
        rideList.setAdapter(adapter);
        rideList.setLayoutManager(new LinearLayoutManager(this));

        Spinner passengerSpinner = findViewById(R.id.passenger_spinner);

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"1", "2", "3", "4", "5"}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        passengerSpinner.setAdapter(adapter);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                if (!(getClass().equals(MainActivity.class))) {
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                return true;
            } else if (item.getItemId() == R.id.nav_chat) {
                if (!(getClass().equals(MessagePage.class))) {
                    Intent intent = new Intent(MainActivity.this, MessagePage.class);
                    startActivity(intent);
                }
                return true;
            } else if (item.getItemId() == R.id.nav_settings) {
                Intent intent = new Intent(MainActivity.this, SettingPage.class);
                intent.putExtra("acceptedDriver", acceptedDriverName);
                intent.putExtra("selectedDate", selectedDate);
                intent.putExtra("selectedTime", selectedTime);
                startActivity(intent);
                return true;
            }
            return false;
        });

        dateInput = findViewById(R.id.date_input);
        timeInput = findViewById(R.id.time_input);

        dateInput.setFocusable(false);
        timeInput.setFocusable(false);

        dateInput.setOnClickListener(v -> showDatePicker());
        timeInput.setOnClickListener(v -> showTimePicker());
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    dateInput.setText(selectedDate);
                },
                year, month, day
        );
        datePicker.show();
    }

    private void showTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePicker = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {
                    selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute);
                    timeInput.setText(selectedTime);
                },
                hour, minute, true
        );
        timePicker.show();
    }

    private String loadJSONFromRawResource(@RawRes int resourceId) {
        String json = null;
        try {
            InputStream is = getResources().openRawResource(resourceId);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
            Log.d("JSON", "Loaded JSON: " + json);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return json;
    }

    public void onRideAccepted(String driverName) {
        acceptedDriverName = driverName;
        showAcceptedDialog();
    }

    private void showAcceptedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Go to feedback page to review driver.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // No action needed, dialog closes
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}