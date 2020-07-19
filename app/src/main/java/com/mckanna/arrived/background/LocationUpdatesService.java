package com.mckanna.arrived.background;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.mckanna.arrived.R;
import com.mckanna.arrived.TravelActivity;
import com.mckanna.arrived.util.LocationClient;
import com.mckanna.arrived.util.SimpleNotificationBuilder;

import org.joda.time.Interval;
import org.joda.time.Period;

import java.util.Date;

/*
 * The purpose of this class is to force the phone to update the user's location. Without
 * this service, the geofence is never triggered.
 */
public class LocationUpdatesService extends Service {
    private static long SLEEP_IN_MILLISECONDS = 60000;
    private static String TAG = "LocationUpdatesService";

    private Looper serviceLooper;
    private IdleServiceHandler serviceHandler;
    private HandlerThread thread;
    private LocationClient client;
    private Date startDate;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        startDate = new Date();
        startThreadAndInitializeHandler();
        client = new LocationClient(getApplicationContext());
        client.startTrackingUserLocation(LocationClient.ONE_MINUTE, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.d(TAG, "LOCATION RETRIEVED");
            }
        });
        startForeground(SimpleNotificationBuilder.ID, getNotificationWithBody(getElapsedTimeString()));
        Log.d(TAG, "Started periodic location tracking.");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Message msg = serviceHandler.obtainMessage();
        serviceHandler.sendMessage(msg);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        thread.interrupt();
        stopForeground(true);
        client.stopTrackingUserLocation(new LocationCallback());
        Log.d(TAG, "Stopped periodic location tracking.");
    }

    private void startThreadAndInitializeHandler() {
        thread = new HandlerThread("IdleThread", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        serviceLooper = thread.getLooper();
        serviceHandler = new IdleServiceHandler(serviceLooper, new OnSleepOverListener() {
            @Override
            public void OnSleepOver() {
                Notification notification = getNotificationWithBody(getElapsedTimeString());
                sendNotification(notification);
            }
        });
    }

    private Notification getNotificationWithBody(String body) {
        PendingIntent pendingIntent = getPendingIntentForNotification();
        SimpleNotificationBuilder builder = new SimpleNotificationBuilder(this);
        builder.setTitle(getString(R.string.title_tracking_location));
        builder.setBody(body);
        builder.setPendingIntent(pendingIntent);
        return builder.build();
    }

    private PendingIntent getPendingIntentForNotification() {
        Intent intent = new Intent(this, TravelActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void sendNotification(Notification notification) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(SimpleNotificationBuilder.ID, notification);
    }

    private String elapsedMinutesSince(Date start) {
        Date now = new Date();
        Interval interval = new Interval(start.getTime(), now.getTime());
        Period period = interval.toPeriod();
        return Integer.toString(period.toStandardMinutes().getMinutes());
    }

    private String getElapsedTimeString() {
        String elapsedMinutes = elapsedMinutesSince(startDate);
        return String.format("You started your trip %s minutes ago.", elapsedMinutes);
    }
}
