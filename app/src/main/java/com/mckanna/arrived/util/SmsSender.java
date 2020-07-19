package com.mckanna.arrived.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.mckanna.arrived.background.ExternalSmsResultBroadcastReceiver;
import com.mckanna.arrived.background.InternalSmsResultBroadcastReceiver;
import com.mckanna.arrived.data.Contact;
import com.mckanna.arrived.data.Destination;
import com.mckanna.arrived.data.SmsResult;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class SmsSender {
    private static final int INVALID_SUBSCRIPTION_ID = -1;

    private Context context;
    private SmsManager smsManager;

    public SmsSender(Context context) throws Exception {
        this.context = context;
        int subscriptionId = getSubscriptionId(context);
        if (subscriptionId != INVALID_SUBSCRIPTION_ID) {
            smsManager = SmsManager.getSmsManagerForSubscriptionId(subscriptionId);
        } else {
            throw new Exception("No valid subscription ID found to send SMS with.");
        }
    }

    // This method will block - do not run on the UI thread!
    public List<SmsResult> sendArrivalMessages(List<Contact> contacts, Destination destination) {
        int numExpectedResults = contacts.size();
        PendingSmsResults pendingResults = new PendingSmsResults(numExpectedResults);
        InternalSmsResultBroadcastReceiver receiver = getReceiver(pendingResults);
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, getIntentFilter());
        attemptSendTextMessages(contacts, destination.getName());
        pendingResults.await(10, TimeUnit.SECONDS);
        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
        return pendingResults.getResults();
    }

    private InternalSmsResultBroadcastReceiver getReceiver(PendingSmsResults pendingSmsResults) {
        InternalSmsResultBroadcastReceiver receiver = new InternalSmsResultBroadcastReceiver(pendingSmsResults);
        return receiver;
    }

    private void attemptSendTextMessages(List<Contact> contacts, String destinationName) {
        String message = String.format("I've arrived at %s!", destinationName);
        for (int i = 0; i < contacts.size(); i++) {
            sendTextMessage(i, contacts.get(i).phoneNumber, message);
        }
    }

    private void sendTextMessage(int messageId, String phoneNumber, String message) {
        Intent intent = new Intent(context, ExternalSmsResultBroadcastReceiver.class);
        intent.putExtra("contactHandle", phoneNumber);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, messageId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        smsManager.sendTextMessage(phoneNumber, null, message, pendingIntent, null);
    }

    private int getSubscriptionId(Context context) {
        SubscriptionManager subscriptionManager = context.getSystemService(SubscriptionManager.class);
        int subscriptionId = INVALID_SUBSCRIPTION_ID;
        try {
            if (subscriptionManager != null) {
                subscriptionId = getFirstSubscriptionId(subscriptionManager);
            }
        } catch (SecurityException e) {
            // We will have already asked for permission before calling this function. This
            // block should never be reached, but is required by Android.
        }
        return subscriptionId;
    }

    private int getFirstSubscriptionId(SubscriptionManager manager) throws SecurityException {
        List<SubscriptionInfo> subInfoList = manager.getActiveSubscriptionInfoList();
        if (subInfoList != null && !subInfoList.isEmpty()) {
            return subInfoList.get(0).getSubscriptionId();
        } else {
            return INVALID_SUBSCRIPTION_ID;
        }
    }

    private IntentFilter getIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(InternalSmsResultBroadcastReceiver.ACTION);
        return intentFilter;
    }
}
