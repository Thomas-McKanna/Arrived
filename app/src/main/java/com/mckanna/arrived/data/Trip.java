package com.mckanna.arrived.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity
public class Trip {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(defaultValue = "false")
    public boolean hasDestinationAndContacts;

    @ColumnInfo(defaultValue = "false")
    public boolean inProgress;

    public String geofenceRequestId;

    @Embedded public Destination destination;

    public Trip() {
        geofenceRequestId = UUID.randomUUID().toString();
    }
}
