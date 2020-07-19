package com.mckanna.arrived.data.tasks;

import android.os.AsyncTask;

import com.mckanna.arrived.data.Trip;
import com.mckanna.arrived.view_models.ArrivedViewModel;

public class UpdateTripTask extends AsyncTask<Void, Void, Void> {
    private ArrivedViewModel model;
    private Trip trip;

    public UpdateTripTask(ArrivedViewModel model, Trip trip) {
        this.model = model;
        this.trip = trip;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        model.updateTrip(trip);
        return null;
    }
}
