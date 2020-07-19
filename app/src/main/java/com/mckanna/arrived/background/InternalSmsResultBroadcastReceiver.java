package com.mckanna.arrived.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mckanna.arrived.data.SmsResult;
import com.mckanna.arrived.util.PendingSmsResults;

public class InternalSmsResultBroadcastReceiver extends BroadcastReceiver {
    public static String ACTION = "RECEIVE_SMS_RESULT";

    private PendingSmsResults pendingSmsResults;

    public InternalSmsResultBroadcastReceiver(PendingSmsResults pendingSmsResults) {
        super();
        this.pendingSmsResults = pendingSmsResults;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String contactHandle = intent.getStringExtra(SmsResult.CONTACT_HANDLE);
        int resultCode = intent.getIntExtra(SmsResult.RESULT_CODE, -99);
        pendingSmsResults.addResult(new SmsResult(contactHandle, resultCode));
    }
}