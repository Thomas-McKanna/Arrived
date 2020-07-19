package com.mckanna.arrived.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity
public class Contact {
    @NonNull
    @PrimaryKey
    public String phoneNumber;
    public String contactName;

    public static String getFormattedContactsString(List<Contact> contacts) {
        StringBuilder sb = new StringBuilder();
        int size = contacts.size();
        if (size > 0) {
            for (int i = 0; i < size - 1; i++) {
                sb.append(contacts.get(i).phoneNumber);
                sb.append(", ");
            }
            sb.append(contacts.get(size - 1).phoneNumber);
        }
        return sb.toString();
    }
}
