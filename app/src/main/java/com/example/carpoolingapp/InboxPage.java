package com.example.carpoolingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.DocumentSnapshot;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashSet;

public class InboxPage extends AppCompatActivity {

    private RecyclerView recyclerView;
    private InboxAdapter adapter;
    private TextView textNoMessages;
    private ArrayList<String> driverNames = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox_page);

        recyclerView = findViewById(R.id.recyclerViewInbox);
        textNoMessages = findViewById(R.id.textNoMessages);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new InboxAdapter(driverNames, driverName -> {
            Intent intent = new Intent(InboxPage.this, ChatPage.class);
            intent.putExtra("driverName", driverName);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        loadInboxDrivers();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                if (!(getClass().equals(MainActivity.class))) {
                    startActivity(new Intent(this, MainActivity.class));
                }
                return true;
            } else if (item.getItemId() == R.id.nav_chat) {
                return true; // Already here
            } else if (item.getItemId() == R.id.nav_settings) {
                if (!(getClass().equals(SettingPage.class))) {
                    startActivity(new Intent(this, SettingPage.class));
                }
                return true;
            }
            return false;
        });
    }

    private void loadInboxDrivers() {
        db.collection("messages").get().addOnSuccessListener(querySnapshot -> {
            HashSet<String> uniqueDrivers = new HashSet<>();
            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                String sender = doc.getString("sender");
                if (!"You".equals(sender)) {
                    uniqueDrivers.add(sender);
                }
            }

            driverNames.clear();
            driverNames.addAll(uniqueDrivers);
            adapter.notifyDataSetChanged();

            if (driverNames.isEmpty()) {
                textNoMessages.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                textNoMessages.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

}
