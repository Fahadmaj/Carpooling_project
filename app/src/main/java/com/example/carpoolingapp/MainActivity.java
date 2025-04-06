package com.example.carpoolingapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText dateInput, timeInput;
    RecyclerView rideList;
    RideAdapter adapter;
    List<Ride> rides = new ArrayList<>();
    TextView availableRidesTitle;
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
        // search button stuff
        Button searchButton = findViewById(R.id.search_button);

        searchButton.setOnClickListener(v -> {
            availableRidesTitle.setVisibility(View.VISIBLE);
            rideList.setVisibility(View.VISIBLE);
            //dummy drivers add here
            rides.clear();
            rides.add(new Ride("Alice", "123 Main St"));
            rides.add(new Ride("Bob", "456 Park Ave"));
            rides.add(new Ride("Carol", "789 Market Rd"));
            rides.add(new Ride("John", "789 Market Rd"));
            rides.add(new Ride("Niko", "222 Lawrence Ave"));
            Toast.makeText(this, "Searching for rides...", Toast.LENGTH_SHORT).show();
        });
        rideList = findViewById(R.id.ride_list);
        availableRidesTitle = findViewById(R.id.available_rides_title);
        adapter = new RideAdapter(rides);
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
                    String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    dateInput.setText(date);
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
                    String time = String.format("%02d:%02d", selectedHour, selectedMinute);
                    timeInput.setText(time);
                },
                hour, minute, true
        );
        timePicker.show();
    }





}