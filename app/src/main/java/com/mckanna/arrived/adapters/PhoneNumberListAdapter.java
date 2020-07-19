package com.mckanna.arrived.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mckanna.arrived.R;
import com.mckanna.arrived.data.Contact;
import com.mckanna.arrived.view_holders.PhoneNumberViewHolder;
import com.mckanna.arrived.view_models.ArrivedViewModel;

import java.util.ArrayList;
import java.util.List;

public class PhoneNumberListAdapter extends RecyclerView.Adapter<PhoneNumberViewHolder> {
    private List<Contact> contacts;
    private OnContactRemovedListener listener;

    @Override
    public void onBindViewHolder(@NonNull final PhoneNumberViewHolder holder, final int position) {
        final String phoneNumber = contacts.get(position).phoneNumber;
        holder.setPhoneNumber(phoneNumber);
    }

    @NonNull
    @Override
    public PhoneNumberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_phone_number, parent, false);
        final PhoneNumberViewHolder vh = new PhoneNumberViewHolder(view);
        vh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Contact contact = contacts.get(vh.getAdapterPosition());
                listener.onContactRemoved(contact);
            }
        });
        return vh;
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public PhoneNumberListAdapter(OnContactRemovedListener listener) {
        contacts = new ArrayList<Contact>();
        this.listener = listener;
    }

    public void setData(List<Contact> contacts) {
        // TODO: use diffutil to figure out differences in old and new list
        this.contacts = contacts;
        notifyDataSetChanged();
    }

    public interface OnContactRemovedListener {
        void onContactRemoved(Contact contact);
    }
}
