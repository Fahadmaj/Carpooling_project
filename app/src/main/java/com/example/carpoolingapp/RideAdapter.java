package com.example.carpoolingapp;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Random;

public class RideAdapter extends RecyclerView.Adapter<RideAdapter.ViewHolder> {
    private final List<Ride> rideList;
    private Random random = new Random();
    private boolean hasAcceptedRide = false;

    public RideAdapter(List<Ride> rideList) {
        this.rideList = rideList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView driverName, destination, price, seats;
        public Button requestButton;
        public LinearLayout extraDetails;

        public ViewHolder(View itemView) {
            super(itemView);
            driverName = itemView.findViewById(R.id.driver_name);
            destination = itemView.findViewById(R.id.destination);
            requestButton = itemView.findViewById(R.id.request_button);
            price = itemView.findViewById(R.id.price);
            seats = itemView.findViewById(R.id.seats);
            extraDetails = itemView.findViewById(R.id.extra_details);
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
        holder.itemView.setOnClickListener(v -> {
            ride.isExpanded = !ride.isExpanded;
            notifyItemChanged(holder.getAdapterPosition());
        });

        holder.driverName.setText(ride.driverName);
        holder.destination.setText(ride.destination);
        holder.price.setText("Price: " + ride.price);
        holder.seats.setText("Seats Available: " + ride.seats);

        holder.extraDetails.setVisibility(ride.isExpanded ? View.VISIBLE : View.GONE);


        updateRequestButton(holder, ride);

        holder.requestButton.setOnClickListener(v -> {

            if (ride.status.equals("none")) {
                // User is requesting a ride
                ride.status = "requested";
                updateRequestButton(holder, ride);

                Toast.makeText(holder.itemView.getContext(),
                        "Ride requested from " + ride.driverName, Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(() -> {
                    boolean accepted = random.nextInt(10) < 7;

                    if (ride.status.equals("requested")) {
                        if (accepted) {
                            ride.status = "accepted";

                            hasAcceptedRide = true;
                            Toast.makeText(holder.itemView.getContext(),
                                    ride.driverName + " accepted your ride request!",
                                    Toast.LENGTH_LONG).show();

                            notifyDataSetChanged();
                        } else {
                            ride.status = "declined";
                            Toast.makeText(holder.itemView.getContext(),
                                    ride.driverName + " declined your ride request.",
                                    Toast.LENGTH_LONG).show();
                        }

                        notifyItemChanged(holder.getAdapterPosition());
                    }
                }, 2000 + random.nextInt(3000));

            } else if (ride.status.equals("requested")) {
                ride.status = "none";
                updateRequestButton(holder, ride);
                Toast.makeText(holder.itemView.getContext(),
                        "Ride request cancelled", Toast.LENGTH_SHORT).show();
            } else if (ride.status.equals("declined")) {
                ride.status = "none";
                updateRequestButton(holder, ride);
                Toast.makeText(holder.itemView.getContext(),
                        "You can request again", Toast.LENGTH_SHORT).show();
            } else if (ride.status.equals("accepted")) {
                ride.status = "none";
                hasAcceptedRide = false;
                notifyDataSetChanged();
                Toast.makeText(holder.itemView.getContext(),
                        "Ride cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateRequestButton(ViewHolder holder, Ride ride) {
        switch (ride.status) {
            case "none":
                if (hasAcceptedRide) {
                    holder.requestButton.setText("Unavailable");
                    holder.requestButton.setEnabled(false);
                } else {
                    holder.requestButton.setText("Request");
                    holder.requestButton.setEnabled(true);
                }
                break;
            case "requested":
                holder.requestButton.setText("Cancel Request");
                holder.requestButton.setEnabled(true);
                break;
            case "accepted":
                holder.requestButton.setText("Cancel Ride");
                holder.requestButton.setEnabled(true);
                break;
            case "declined":
                if (hasAcceptedRide) {
                    holder.requestButton.setText("Unavailable");
                    holder.requestButton.setEnabled(false);
                } else {
                    holder.requestButton.setText("Try Again");
                    holder.requestButton.setEnabled(true);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return rideList.size();
    }
}