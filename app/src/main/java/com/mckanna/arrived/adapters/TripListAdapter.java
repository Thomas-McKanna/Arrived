package com.mckanna.arrived.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mckanna.arrived.R;
import com.mckanna.arrived.data.Contact;
import com.mckanna.arrived.data.Destination;
import com.mckanna.arrived.data.Trip;
import com.mckanna.arrived.data.TripWithContacts;
import com.mckanna.arrived.view_holders.TripViewHolder;

import java.util.ArrayList;
import java.util.List;

public class TripListAdapter extends RecyclerView.Adapter<TripViewHolder> {
    private static String TAG = "TripListAdapter";

    private List<TripWithContacts> tripsWithContacts;
    private OnClickTripListener listener;

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        TripWithContacts tripWithContacts = tripsWithContacts.get(position);
        Trip trip = tripWithContacts.trip;
        Destination destination = trip.destination;
        List<Contact> contacts = tripWithContacts.contacts;
        String destinationName = attemptGetDestinationName(destination);
        holder.setDestinationName(destinationName);
        String formattedContacts = Contact.getFormattedContactsString(contacts);
        holder.setContacts(formattedContacts);
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_trip, parent, false);
        final TripViewHolder vh = new TripViewHolder(view);
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Trip trip = tripsWithContacts.get(vh.getAdapterPosition()).trip;
                listener.onClickTrip(trip);
            }
        });
        return vh;
    }

    @Override
    public int getItemCount() {
        return tripsWithContacts.size();
    }

    public TripListAdapter(OnClickTripListener listener) {
        tripsWithContacts = new ArrayList<TripWithContacts>();
        this.listener = listener;
    }

    public void setData(List<TripWithContacts> trips) {
        tripsWithContacts = trips;
        notifyDataSetChanged();
        // TODO: use diffutil
    }

    public interface OnClickTripListener {
        void onClickTrip(Trip trip);
    }

    private String attemptGetDestinationName(Destination destination) {
        String destinationName;
        if (destination != null) {
            destinationName = destination.getName();
        } else {
            destinationName = new String("");
        }
        return destinationName;
    }
}
