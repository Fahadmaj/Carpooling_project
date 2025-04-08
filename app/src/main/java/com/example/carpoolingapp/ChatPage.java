package com.example.carpoolingapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;

public class ChatPage extends AppCompatActivity {

    private String driverName;
    private FirebaseFirestore db;
    private ArrayList<Message> messages = new ArrayList<>();
    private MessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_page);

        driverName = getIntent().getStringExtra("driverName");

        RecyclerView recyclerView = findViewById(R.id.recyclerViewChat);
        EditText input = findViewById(R.id.editTextChat);
        Button sendBtn = findViewById(R.id.sendButtonChat);

        db = FirebaseFirestore.getInstance();
        adapter = new MessageAdapter(messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadChatMessages();

        sendBtn.setOnClickListener(v -> {
            String text = input.getText().toString().trim();
            if (!text.isEmpty()) {
                Message message = new Message("You", text, new Date());
                db.collection("messages").add(message);
                input.setText("");
            }
        });
    }

    private void loadChatMessages() {
        db.collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) return;
                    messages.clear();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        Message msg = doc.toObject(Message.class);
                        if (driverName.equals(msg.getSender()) || "You".equals(msg.getSender())) {
                            messages.add(msg);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}

