package com.mckanna.arrived.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TripDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertPhoneNumberContact(Contact contact);

    @Insert
    long insertTrip(Trip trip);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertTripContactCrossRef(TripContactCrossRef crossRef);

    @Update
    void updateTrip(Trip trip);

    @Delete
    void deleteTripContactCrossRef(TripContactCrossRef crossRef);

    @Delete
    void deleteTrip(Trip trip);

    @Query("SELECT * FROM Trip WHERE id = :tripId")
    LiveData<Trip> getTrip(long tripId);

    @Transaction
    @Query("SELECT * FROM Trip WHERE id = :tripId")
    LiveData<TripWithContacts> getTripWithContacts(long tripId);

    @Transaction
    @Query("SELECT * FROM Trip WHERE geofenceRequestId = :geofenceRequestId")
    TripWithContacts getTripWithContacts(String geofenceRequestId);

    @Transaction
    @Query("SELECT * FROM Trip")
    LiveData<List<TripWithContacts>> getTripsWithContacts();
}
