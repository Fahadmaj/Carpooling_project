package com.example.carpoolingapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TripHistoryAdapter extends RecyclerView.Adapter<TripHistoryAdapter.TripViewHolder> {

    private List<Trip> tripList;
    private final OnTripClickListener listener;

    public interface OnTripClickListener {
        void onTripClick(Trip trip);
    }

    public TripHistoryAdapter(List<Trip> tripList, OnTripClickListener listener) {
        this.tripList = tripList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_item, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Trip trip = tripList.get(position);
        holder.bind(trip);
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    public class TripViewHolder extends RecyclerView.ViewHolder {
        TextView destinationText, priceText, driverText, dateText, timeText;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            destinationText = itemView.findViewById(R.id.destinationText);
            priceText = itemView.findViewById(R.id.priceText);
            driverText = itemView.findViewById(R.id.driverText);
            dateText = itemView.findViewById(R.id.dateText);
            timeText = itemView.findViewById(R.id.timeText);
        }

        public void bind(final Trip trip) {
            destinationText.setText(trip.getDestination());
            priceText.setText(String.format("$%.2f", trip.getPrice()));

            double avgRating = ReviewUtils.getAverageRating(itemView.getContext(), trip.getDriver());
            String displayName = trip.getDriver();
            if (avgRating > 0) {
                displayName += String.format(" (%.1f★)", avgRating);
            }

            driverText.setText(displayName);
            dateText.setText(trip.getDate());
            timeText.setText(trip.getTime());

            itemView.setOnClickListener(v -> listener.onTripClick(trip));
        }
    }

    public void updateTrips(List<Trip> trips) {
        this.tripList = trips;
        notifyDataSetChanged();
    }
}
