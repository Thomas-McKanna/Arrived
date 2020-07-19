package com.mckanna.arrived.util;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.mckanna.arrived.ArrivedApplication;
import com.mckanna.arrived.R;

public class SimpleNotificationBuilder {
    public static int ID = 12345;

    private Context context;
    private NotificationCompat.Builder builder;

    public SimpleNotificationBuilder(Context context) {
        this.context = context;
        String channelId = ArrivedApplication.CHANNEL_ID;
        builder = new NotificationCompat.Builder(context, channelId);
        builder.setSmallIcon(R.drawable.ic_car);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setAutoCancel(true);
    }

    public SimpleNotificationBuilder setTitle(String title) {
        builder.setContentTitle(title);
        return this;
    }

    public SimpleNotificationBuilder setBody(String body) {
        builder.setContentText(body);
        return this;
    }

    public SimpleNotificationBuilder setPendingIntent(PendingIntent pendingIntent) {
        builder.setContentIntent(pendingIntent);
        return this;
    }

    public Notification build() {
        return builder.build();
    }
}
