package com.mckanna.arrived.data.tasks;

import android.os.AsyncTask;

import com.mckanna.arrived.data.Contact;
import com.mckanna.arrived.data.Trip;
import com.mckanna.arrived.view_models.ArrivedViewModel;

public class RemoveContactFromTripTask extends AsyncTask<Void, Void, Void> {
    private ArrivedViewModel model;
    private Trip trip;
    private Contact contact;

    public RemoveContactFromTripTask(ArrivedViewModel model, Trip trip, Contact contact) {
        this.model = model;
        this.trip = trip;
        this.contact = contact;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        model.removePhoneNumberContactFromTrip(trip, contact);
        return null;
    }
}