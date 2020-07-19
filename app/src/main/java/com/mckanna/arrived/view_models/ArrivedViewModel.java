package com.mckanna.arrived.view_models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.mckanna.arrived.data.Contact;
import com.mckanna.arrived.data.Trip;
import com.mckanna.arrived.data.Repository;
import com.mckanna.arrived.data.TripWithContacts;

import java.util.List;

public class ArrivedViewModel extends ViewModel {
    private Repository repository;

    public ArrivedViewModel(Repository repository) {
        this.repository = repository;
    }

    public long insertTrip(Trip trip) {
        return repository.insertTrip(trip);
    }

    public void insertPhoneNumberContact(Contact contact) {
        repository.insertPhoneNumberContact(contact);
    }

    public void addPhoneNumberContactToTrip(Trip trip, Contact contact) {
        repository.bindTripAndPhoneNumberContact(trip, contact);
    }

    public void updateTrip(Trip trip) {
        repository.updateTrip(trip);
    }

    public void deleteTrip(Trip trip) {
        repository.deleteTrip(trip);
    }

    public void removePhoneNumberContactFromTrip(Trip trip, Contact contact) {
        repository.unbindTripAndPhoneNumberContact(trip, contact);
    }

    public LiveData<Trip> getTrip(long tripId) {
        return repository.getTrip(tripId);
    }

    public LiveData<TripWithContacts> getTripWithContacts(long tripId) {
        return repository.getTripWithContacts(tripId);
    }

    public LiveData<List<TripWithContacts>> getTripsWithContacts() {
        return repository.getAllTrips();
    }
}
