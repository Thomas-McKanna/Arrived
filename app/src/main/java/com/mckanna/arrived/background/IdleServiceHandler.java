package com.mckanna.arrived.background;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

public class IdleServiceHandler extends Handler {
    private static long SLEEP_IN_MILLISECONDS = 60000;

    private OnSleepOverListener listener;

    public IdleServiceHandler(Looper looper, OnSleepOverListener listener) {
        super(looper);
        this.listener = listener;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        while (true) {
            try {
                Thread.sleep(SLEEP_IN_MILLISECONDS);
                listener.OnSleepOver();
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
