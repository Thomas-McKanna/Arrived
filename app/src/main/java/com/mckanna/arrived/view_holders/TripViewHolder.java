package com.mckanna.arrived.view_holders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mckanna.arrived.R;

public class TripViewHolder extends RecyclerView.ViewHolder {
    private TextView textDestinationName;
    private TextView textContacts;

    public TripViewHolder(@NonNull View itemView) {
        super(itemView);
        textDestinationName = itemView.findViewById(R.id.text_destination_name);
        textContacts = itemView.findViewById(R.id.text_contact_info);
    }

    public void setDestinationName(String name) {
        textDestinationName.setText(name);
    }

    public void setContacts(String contacts) {
        textContacts.setText(contacts);
    }
}
