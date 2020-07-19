package com.mckanna.arrived.util;

import com.mckanna.arrived.data.SmsResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class PendingSmsResults {
    private List<SmsResult> results;
    private CountDownLatch  doneSignal;

    public PendingSmsResults(int numExpectedResults) {
        results = new ArrayList<>();
        doneSignal = new CountDownLatch(numExpectedResults);
    }

    public void addResult(SmsResult result) {
        results.add(result);
        doneSignal.countDown();
    }

    public void await(int timeout, TimeUnit unit) {
        try {
            doneSignal.await(timeout, unit);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public List<SmsResult> getResults() {
        return results;
    }
}
