package com.mckanna.arrived.data.tasks;

import android.os.AsyncTask;

import com.mckanna.arrived.data.Trip;
import com.mckanna.arrived.view_models.ArrivedViewModel;

public class InsertNewEmptyTripTask extends AsyncTask<Void, Void, Long> {
    private ArrivedViewModel model;
    private OnTripInsertedCallback callback;

    public InsertNewEmptyTripTask(ArrivedViewModel model, OnTripInsertedCallback callback) {
        this.model = model;
        this.callback = callback;
    }

    @Override
    protected Long doInBackground(Void... voids) {
        Trip newTrip = new Trip();
        return model.insertTrip(newTrip);
    }

    @Override
    protected void onPostExecute(Long aLong) {
        super.onPostExecute(aLong);
        callback.onTripInserted(aLong);
    }
}
