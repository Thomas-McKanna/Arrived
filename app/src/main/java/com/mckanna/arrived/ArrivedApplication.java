package com.mckanna.arrived;

import android.Manifest;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.google.android.libraries.places.api.Places;
import com.mckanna.arrived.util.Permissions;

import java.util.ArrayList;
import java.util.List;

import static com.mckanna.arrived.util.Permissions.ACCESS_LOCATION_PERMISSION;
import static com.mckanna.arrived.util.Permissions.READ_CONTACTS_PERMISSION;
import static com.mckanna.arrived.util.Permissions.SEND_SMS_PERMISSION;


public class ArrivedApplication extends Application {
    public static String CHANNEL_ID = "ArrivedApplicationChannel";

    public Permissions permissions;

    @Override
    public void onCreate() {
        super.onCreate();
        Places.initialize(this, getString(R.string.google_maps_key));
        permissions = new Permissions(this);
        registerPermissions(permissions);
        createNotificationChannel();
    }

    private void registerPermissions(Permissions permissions) {
        registerAccessLocationPermission(permissions);
        registerReadContactsPermission(permissions);
        registerSendSmsPermission(permissions);
    }

    private void registerReadContactsPermission(Permissions permissions) {
        Build.VERSION version = new Build.VERSION();
        List<String> permissionIds = new ArrayList<String>();
        permissionIds.add(Manifest.permission.ACCESS_FINE_LOCATION);
        // Android 10 or higher needs to request access to background location
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissionIds.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        }
        permissions.register(
            ACCESS_LOCATION_PERMISSION,
            permissionIds.toArray(new String[permissionIds.size()]),
            this.getString(R.string.rationale_location_permission));
    }

    private void registerAccessLocationPermission(Permissions permissions) {
        permissions.register(
            READ_CONTACTS_PERMISSION,
            new String[]{Manifest.permission.READ_CONTACTS},
            this.getString(R.string.rationale_read_contacts_permission));
    }

    private void registerSendSmsPermission(Permissions permissions) {
        permissions.register(
            SEND_SMS_PERMISSION,
            new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE},
            this.getString(R.string.rationale_send_sms_permission));
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.notification_channel_name);
            String description = getString(R.string.notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
