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
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
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

    EditText startInput;
    EditText endInput;

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
//        clearCustomRidesFile();

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "replace_key");
        }

        Button searchButton = findViewById(R.id.search_button);

        searchButton.setOnClickListener(v -> {
            availableRidesTitle.setVisibility(View.VISIBLE);
            rideList.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Searching for rides...", Toast.LENGTH_SHORT).show();

            rides.clear();

            String start = startInput.getText().toString();
            String end = endInput.getText().toString();
            String passengers = ((Spinner) findViewById(R.id.passenger_spinner)).getSelectedItem().toString();
            String date = dateInput.getText().toString();
            String time = timeInput.getText().toString();

            if (start.isEmpty() || end.isEmpty()) {
                Toast.makeText(this, "Please enter start and end locations.", Toast.LENGTH_SHORT).show();
                return;
            }

            double randomPrice = 10 + Math.random() * 15;
            String price = String.format("%.2f$", randomPrice);
            String[] drivers = {
                    "Daniel Miller",
                    "Emma Johnson",
                    "Liam Thompson",
                    "Sophia Roberts",
                    "Ethan Brown",
                    "Olivia Davis",
                    "Noah Clark",
                    "Ava Wilson",
                    "Mason Harris",
                    "Isabella Lewis",
                    "James Walker",
                    "Charlotte Hall",
                    "Benjamin Young",
                    "Amelia Scott",
                    "Jack Turner"
            };


            String randomDriver = drivers[(int)(Math.random() * drivers.length)];


            String destinationFormatted = end;


            Ride customRide = new Ride(
                    randomDriver,
                    destinationFormatted,
                    price,
                    passengers,
                    date.isEmpty() ? "N/A" : date,
                    time.isEmpty() ? "N/A" : time
            );

            rides.add(customRide);
            saveRideToInternalStorage(customRide);

            JSONArray customRidesArray = loadCustomRidesFromStorage();
            for (int i = 0; i < customRidesArray.length(); i++) {
                JSONObject obj = null;
                try {
                    obj = customRidesArray.getJSONObject(i);
                    Ride ride = new Ride(
                            obj.getString("driverName"),
                            obj.getString("destination"),
                            obj.getString("price"),
                            obj.getString("seats"),
                            obj.optString("date", "N/A"),
                            obj.optString("time", "N/A")
                    );
                    rides.add(ride);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            }
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
            } catch (Exception e) {
                Log.e("JSON", "Unexpected error: " + e.getMessage());
                e.printStackTrace();
                Toast.makeText(this, "Unexpected error occurred.", Toast.LENGTH_SHORT).show();
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
                return true;
            } else if (item.getItemId() == R.id.nav_chat) {
                if (!(getClass().equals(MessagePage.class))) {
                    Intent intent = new Intent(MainActivity.this, MessagePage.class);
                    intent.putExtra("acceptedDriver", acceptedDriverName);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
                return true;
            } else if (item.getItemId() == R.id.nav_settings) {
                Intent intent = new Intent(MainActivity.this, SettingPage.class);
                intent.putExtra("acceptedDriver", acceptedDriverName);
                intent.putExtra("selectedDate", selectedDate);
                intent.putExtra("selectedTime", selectedTime);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
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

        startInput = findViewById(R.id.start_location_input);
        endInput = findViewById(R.id.end_location_input);

        startInput.setFocusable(false);
        endInput.setFocusable(false);

        startInput.setOnClickListener(v -> launchAutocomplete(1001));
        endInput.setOnClickListener(v -> launchAutocomplete(1002));
    }

    private void launchAutocomplete(int requestCode) {
        List<Place.Field> fields = Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS
        );

        // limit stuff to Kelowna
        RectangularBounds kelownaBounds = RectangularBounds.newInstance(
                new LatLng(49.8370, -119.6234),
                new LatLng(49.9500, -119.3320)
        );

        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .setLocationBias(kelownaBounds)
                .setCountries(Arrays.asList("CA"))
                .build(this);

        startActivityForResult(intent, requestCode);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == 1001 || requestCode == 1002) && resultCode == RESULT_OK && data != null) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            if (requestCode == 1001) {
                startInput.setText(place.getAddress());
            } else {
                endInput.setText(place.getAddress());
            }
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(this, "Error: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }
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


        // Start a timer that ends the trip after 10 seconds
        new android.os.Handler().postDelayed(() -> {
            Toast.makeText(this, "Trip completed! You can now leave a review.", Toast.LENGTH_LONG).show();



            // Navigate to trip history
            Intent intent = new Intent(MainActivity.this, SettingPage.class);
            intent.putExtra("acceptedDriver", acceptedDriverName);
            intent.putExtra("selectedDate", selectedDate);
            intent.putExtra("selectedTime", selectedTime);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);

            // Reset UI fields
            startInput.setText("");
            endInput.setText("");
            dateInput.setText("");
            timeInput.setText("");
            rides.clear();
            adapter.notifyDataSetChanged();


        }, 10000); // 10 seconds
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
    private void saveRideToInternalStorage(Ride ride) {
        try {
            File file = new File(getFilesDir(), "rides_custom.json");
            JSONArray ridesArray;

            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                byte[] data = new byte[(int) file.length()];
                fis.read(data);
                fis.close();
                String content = new String(data, StandardCharsets.UTF_8);
                ridesArray = new JSONArray(content);
            } else {
                ridesArray = new JSONArray();
            }

            JSONObject newRide = new JSONObject();
            newRide.put("driverName", ride.driverName);
            newRide.put("destination", ride.destination);
            newRide.put("price", ride.price);
            newRide.put("seats", ride.seats);
            newRide.put("date", ride.date);
            newRide.put("time", ride.time);

            ridesArray.put(newRide);

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(ridesArray.toString().getBytes(StandardCharsets.UTF_8));
            fos.close();

        } catch (Exception e) {
            Log.e("SAVE_RIDE", "Failed to save custom ride: " + e.getMessage());
        }
    }
    //Use this to get the custom driver
    private JSONArray loadCustomRidesFromStorage() {
        try {
            File file = new File(getFilesDir(), "rides_custom.json");
            if (!file.exists()) return new JSONArray();

            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();

            String content = new String(data, StandardCharsets.UTF_8);
            return new JSONArray(content);
        } catch (Exception e) {
            Log.e("LOAD_CUSTOM", "Error reading custom rides: " + e.getMessage());
            return new JSONArray();
        }
    }

    private void clearCustomRidesFile() {
        File file = new File(getFilesDir(), "rides_custom.json");
        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                Toast.makeText(this, "Custom rides reset successfully.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to reset custom rides.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No custom rides to reset.", Toast.LENGTH_SHORT).show();
        }
    }

}