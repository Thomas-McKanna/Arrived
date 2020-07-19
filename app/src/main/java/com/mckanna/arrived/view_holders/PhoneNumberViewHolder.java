package com.mckanna.arrived.view_holders;

import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mckanna.arrived.R;

public class PhoneNumberViewHolder extends RecyclerView.ViewHolder {
    private TextView textViewPhoneNumber;
    private Button buttonRemovePhoneNumber;

    public PhoneNumberViewHolder(@NonNull View itemView) {
        super(itemView);
        textViewPhoneNumber = itemView.findViewById(R.id.text_phone_number);
        buttonRemovePhoneNumber = itemView.findViewById(R.id.button_remove_phone_number);
    }

    public void setPhoneNumber(String phoneNumber) {
        textViewPhoneNumber.setText(phoneNumber);
    }

    public void setOnClickListener(View.OnClickListener listener) {
        buttonRemovePhoneNumber.setOnClickListener(listener);
    }
}