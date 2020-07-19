package com.mckanna.arrived.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;

@Entity(primaryKeys = {"phoneNumber", "id"})
public class TripContactCrossRef {
    @NonNull public String phoneNumber;
    @NonNull public long id;
}
