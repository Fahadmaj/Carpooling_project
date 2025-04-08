package com.example.carpoolingapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> {

    private final List<ChatEntry> chatList;
    private final OnChatClickListener listener;

    public interface OnChatClickListener {
        void onChatClick(ChatEntry chatEntry);
    }

    public ChatListAdapter(List<ChatEntry> chatList, OnChatClickListener listener) {
        this.chatList = chatList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatEntry entry = chatList.get(position);
        holder.driverName.setText(entry.getDriverName());
        holder.lastMessage.setText(entry.getLastMessagePreview());
        holder.itemView.setOnClickListener(v -> listener.onChatClick(entry));
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView driverName, lastMessage;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            driverName = itemView.findViewById(R.id.chatDriverName);
            lastMessage = itemView.findViewById(R.id.chatLastMessage);
        }
    }
}
