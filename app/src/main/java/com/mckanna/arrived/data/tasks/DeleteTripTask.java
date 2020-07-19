package com.mckanna.arrived.data.tasks;

import android.os.AsyncTask;

import com.mckanna.arrived.data.Trip;
import com.mckanna.arrived.view_models.ArrivedViewModel;

public class DeleteTripTask extends AsyncTask<Void, Void, Void> {
    private ArrivedViewModel model;
    private Trip trip;

    public DeleteTripTask(ArrivedViewModel model, Trip trip) {
        this.model = model;
        this.trip = trip;
    };

    @Override
    protected Void doInBackground(Void... voids) {
        model.deleteTrip(trip);
        return null;
    }
}
