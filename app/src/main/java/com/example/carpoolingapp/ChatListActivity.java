package com.example.carpoolingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatListActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private ChatListAdapter adapter;
    private ArrayList<ChatEntry> chatEntries;
    private TextView noChats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        noChats = findViewById(R.id.noChatsText);

        chatEntries = new ArrayList<>();
        adapter = new ChatListAdapter(chatEntries, entry -> {
            Intent intent = new Intent(ChatListActivity.this, ChatPage.class);
            intent.putExtra("driverName", entry.getDriverName());
            startActivity(intent);
        });

        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(adapter);

        loadDummyChats();
    }

    private void loadDummyChats() {
        chatEntries.clear();
        chatEntries.add(new ChatEntry("Daniel Miller", "Hey! Still good for tomorrow?"));
        chatEntries.add(new ChatEntry("Emma Johnson", "Thanks for the ride!"));

        adapter.notifyDataSetChanged();
        noChats.setVisibility(chatEntries.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
