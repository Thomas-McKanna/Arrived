package com.mckanna.arrived.data;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class TripWithContacts {
    @Embedded public Trip trip;
    @Relation(
            parentColumn = "id",
            entityColumn = "phoneNumber",
            associateBy = @Junction(TripContactCrossRef.class)
    )
    public List<Contact> contacts;

    public boolean isIncomplete() {
        return trip.destination == null || contacts == null;
    }
}
