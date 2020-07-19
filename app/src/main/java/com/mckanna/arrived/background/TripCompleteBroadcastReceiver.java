package com.mckanna.arrived.background;

import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.mckanna.arrived.TripCompleteActivity;

public class TripCompleteBroadcastReceiver extends BroadcastReceiver implements LifecycleObserver {
    public static final String ACTION = "TRIP_COMPLETE";

    private boolean receivedTripCompleteIntent;
    private Intent tripCompleteIntent;
    private Context context;
    private Lifecycle lifecycle;

    public TripCompleteBroadcastReceiver(Context context, Lifecycle lifecycle) {
        this.context = context;
        this.lifecycle = lifecycle;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        receivedTripCompleteIntent = true;
        tripCompleteIntent = intent;
        if (lifecycle.getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
            attemptStartTripCompleteActivity();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private void attemptStartTripCompleteActivity() {
        if (receivedTripCompleteIntent) {
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            Intent intent = new Intent(context, TripCompleteActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtras(tripCompleteIntent);
            stackBuilder.addNextIntentWithParentStack(intent);
            stackBuilder.startActivities();
        }
    }
}
