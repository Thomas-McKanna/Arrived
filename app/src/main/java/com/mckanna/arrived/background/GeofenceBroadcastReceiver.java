package com.mckanna.arrived.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent geofenceResultIntent) {
        Intent serviceIntent = new Intent(context, NotificationService.class);
        serviceIntent.putExtras(geofenceResultIntent);
        NotificationService.enqueueWork(context, serviceIntent);
    }
}
