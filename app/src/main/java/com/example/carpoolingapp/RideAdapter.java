package com.example.carpoolingapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RideAdapter extends RecyclerView.Adapter<RideAdapter.ViewHolder> {

    private final List<Ride> rideList;

    public RideAdapter(List<Ride> rideList) {
        this.rideList = rideList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView driverName;
        public TextView destination;
        public Button requestButton;

        public ViewHolder(View itemView) {
            super(itemView);
            driverName = itemView.findViewById(R.id.driver_name);
            destination = itemView.findViewById(R.id.destination);
            requestButton = itemView.findViewById(R.id.request_button);
        }
    }

    @Override
    public RideAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ride_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Ride ride = rideList.get(position);
        holder.driverName.setText(ride.driverName);
        holder.destination.setText(ride.destination);
        holder.requestButton.setOnClickListener(v -> {
            //this is the request part, add ur stuff here
        });
    }

    @Override
    public int getItemCount() {
        return rideList.size();
    }
}
