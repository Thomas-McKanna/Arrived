package com.mckanna.arrived.data;

import androidx.lifecycle.LiveData;

import java.util.List;

public class Repository {
    private TripDao dao;

    public Repository(TripDao dao) {
        this.dao = dao;
    }

    public long insertTrip(final Trip trip) {
        return dao.insertTrip(trip);
    }

    public void insertPhoneNumberContact(final Contact contact) {
        dao.insertPhoneNumberContact(contact);
    }

    private TripContactCrossRef makeTripPhoneNumberContactCrossRef(Trip trip, Contact contact) {
        TripContactCrossRef crossRef = new TripContactCrossRef();
        crossRef.id = trip.id;
        crossRef.phoneNumber = contact.phoneNumber;
        return crossRef;
    }

    public void bindTripAndPhoneNumberContact(Trip trip, Contact contact) {
        TripContactCrossRef crossRef = makeTripPhoneNumberContactCrossRef(trip, contact);
        dao.insertTripContactCrossRef(crossRef);
    }

    public void updateTrip(Trip trip) {
        dao.updateTrip(trip);
    }

    public void deleteTrip(Trip trip) {
        dao.deleteTrip(trip);
    }

    public void unbindTripAndPhoneNumberContact(Trip trip, Contact contact) {
        TripContactCrossRef crossRef = makeTripPhoneNumberContactCrossRef(trip, contact);
        dao.deleteTripContactCrossRef(crossRef);
    }

    public LiveData<Trip> getTrip(long tripId) {
        return dao.getTrip(tripId);
    }

    public LiveData<TripWithContacts> getTripWithContacts(long tripId) {
        return dao.getTripWithContacts(tripId);
    }

    public TripWithContacts getTripWithContacts(String geofenceRequestId) {
        return dao.getTripWithContacts(geofenceRequestId);
    }

    public LiveData<List<TripWithContacts>> getAllTrips() {
        return dao.getTripsWithContacts();
    }
}
