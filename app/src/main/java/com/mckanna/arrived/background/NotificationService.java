package com.mckanna.arrived.background;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.mckanna.arrived.R;
import com.mckanna.arrived.TripCompleteActivity;
import com.mckanna.arrived.data.Contact;
import com.mckanna.arrived.data.Destination;
import com.mckanna.arrived.data.Repository;
import com.mckanna.arrived.data.SmsResult;
import com.mckanna.arrived.data.TripWithContacts;
import com.mckanna.arrived.util.InjectorUtils;
import com.mckanna.arrived.util.SimpleNotificationBuilder;
import com.mckanna.arrived.util.SmsSender;

import java.util.ArrayList;
import java.util.List;

public class NotificationService extends JobIntentService {
    public static final int JOB_ID = 999;

    private static String TAG = "NotificationService";

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, NotificationService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            int errorCode = geofencingEvent.getErrorCode();
            String errorMessage = GeofenceStatusCodes.getStatusCodeString(errorCode);
            Log.e(TAG, errorMessage);
        } else {
            stopPeriodicLocationService();
            Geofence geofence = geofencingEvent.getTriggeringGeofences().get(0);
            List<SmsResult> smsResults = sendArrivalMessages(geofence);
            sendArrivalNotification(smsResults);
            sendTripCompleteBroadcast(smsResults);
        }
    }

    private List<SmsResult> sendArrivalMessages(Geofence geofence) {
        TripWithContacts tripWithContacts =  getTripAndSetProgressToFalse(geofence);
        List<SmsResult> smsResults = null;
        try {
            SmsSender smsSender = new SmsSender(getApplicationContext());
            List<Contact> contacts = tripWithContacts.contacts;
            Destination destination = tripWithContacts.trip.destination;
            smsResults =  smsSender.sendArrivalMessages(contacts, destination);
        } catch (Exception e) {
            Log.e(TAG, "Unable to get a reference to a SmsManager.");
        }
        return smsResults;
    }

    private TripWithContacts getTripAndSetProgressToFalse(Geofence geofence) {
        String requestId = geofence.getRequestId();
        Repository repository = InjectorUtils.getRepository(getApplicationContext());
        TripWithContacts tripWithContacts = repository.getTripWithContacts(requestId);
        tripWithContacts.trip.inProgress = false;
        repository.updateTrip(tripWithContacts.trip);
        return tripWithContacts;
    }

    private void sendArrivalNotification(List<SmsResult> smsResults) {
        Notification notification = getArrivalNotification(smsResults);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(SimpleNotificationBuilder.ID, notification);
    }

    public Notification getArrivalNotification(List<SmsResult> smsResults) {
        Intent intent = new Intent(this, TripCompleteActivity.class);
        addSmsResultsToIntent(intent, smsResults);
        PendingIntent pendingIntent = getPendingIntentForNotification(intent);
        SimpleNotificationBuilder builder = new SimpleNotificationBuilder(this);
        builder.setTitle(getString(R.string.title_arrived));
        builder.setBody(getString(R.string.msg_contacts_notified));
        builder.setPendingIntent(pendingIntent);
        return builder.build();
    }

    private PendingIntent getPendingIntentForNotification(Intent intent) {
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intent);
        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void sendTripCompleteBroadcast(List<SmsResult> smsResults) {
        Intent intent = new Intent(getApplicationContext(), TripCompleteBroadcastReceiver.class);
        intent.setAction(TripCompleteBroadcastReceiver.ACTION);
        addSmsResultsToIntent(intent, smsResults);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    private void addSmsResultsToIntent(Intent intent, List<SmsResult> smsResults) {
        ArrayList<SmsResult> smsResultArrayList = new ArrayList<>(smsResults);
        intent.putParcelableArrayListExtra("smsResults", smsResultArrayList);
    }

    private void stopPeriodicLocationService() {
        Intent intent = new Intent(getApplicationContext(), LocationUpdatesService.class);
        getApplicationContext().stopService(intent);
    }
}
