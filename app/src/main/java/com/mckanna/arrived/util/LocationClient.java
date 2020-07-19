package com.mckanna.arrived.util;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class LocationClient {
    // All values in milliseconds
    public static long ONE_MINUTE = 60000;
    public static long FIVE_MINUTES = 300000;
    public static long TEN_MINUTES = 600000;
    public static long THIRTY_MINUTES = 1800000;
    public static long SIXTY_MINUTES = 3600000;
    public static int RESULT = 13579;

    private static long DEBUG_INTERVAL = 5000;
    private static int PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY;

    private Context context;
    private Activity activity;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;

    public LocationClient(Activity activity) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
    }

    public LocationClient(Context context) {
        this.context = context;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    private void createLocationRequest(long interval) {
        locationRequest = LocationRequest.create();
        // TODO: Make sure to use the interval passed in when not debugging
        locationRequest.setInterval(DEBUG_INTERVAL);
        locationRequest.setPriority(PRIORITY);
    }

    public Task<Location> getLastLocation() {
        return fusedLocationClient.getLastLocation();
    }

    public void startTrackingUserLocation(long interval, final LocationCallback callback) {
        createLocationRequest(interval);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(context);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build())
            .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    startLocationUpdates(callback);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (activity != null) {
                        resolveLocationApiException(e);
                    }
                }
            });
    }

    private void startLocationUpdates(LocationCallback callback) {
        fusedLocationClient.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper());
    }

    public void stopTrackingUserLocation(LocationCallback callback) {
        fusedLocationClient.removeLocationUpdates(callback);
    }

    private void resolveLocationApiException(Exception e) {
        if (e instanceof ResolvableApiException) {
            ResolvableApiException resolvable = (ResolvableApiException) e;
            try {
                resolvable.startResolutionForResult(this.activity, RESULT);
            } catch (IntentSender.SendIntentException sendEx) {}
        }
    }
}
