package com.mckanna.arrived.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.mckanna.arrived.data.SmsResult;

public class ExternalSmsResultBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsResultBroadcastRecei";

    @Override
    public void onReceive(Context context, Intent originalIntent) {
        String contactHandle = originalIntent.getStringExtra("contactHandle");
        Intent smsResultIntent = new Intent(context, InternalSmsResultBroadcastReceiver.class);
        smsResultIntent.putExtra(SmsResult.CONTACT_HANDLE, contactHandle);
        smsResultIntent.putExtra(SmsResult.RESULT_CODE, getResultCode());
        smsResultIntent.setAction(InternalSmsResultBroadcastReceiver.ACTION);
        LocalBroadcastManager.getInstance(context).sendBroadcast(smsResultIntent);
    }
}
