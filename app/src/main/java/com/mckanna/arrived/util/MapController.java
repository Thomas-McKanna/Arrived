package com.mckanna.arrived.util;

import android.app.Activity;
import android.location.Location;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Observer;

public class MapController {
    private GoogleMap map;
    private LocationCallback locationCallback;
    private LocationClient client;
    private Activity activity;
    private Marker currentLocationMarker;

    public MapController(Activity activity, GoogleMap map) {
        map.getUiSettings().setMapToolbarEnabled(false);
        map.getUiSettings().setRotateGesturesEnabled(false);
        this.map = map;
        this.activity = activity;
        client = new LocationClient(activity);
    }

    public void moveCamera(LatLng position) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 17.0f));
    }

    public Marker placeMarkerAtLatLng(LatLng position) {
        return map.addMarker(new MarkerOptions()
            .position(position));
    }

    public void placeMarkerAtCurrentLocation() {
        Task<Location> getLocation = client.getLastLocation();
        getLocation.addOnSuccessListener(activity, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                if (currentLocationMarker != null)
                    currentLocationMarker.remove();
                currentLocationMarker = placeMarkerAtLatLng(latLng);
            }
        });
    }

    public void moveMapToCurrentLocation() {
        Task<Location> getLocation = client.getLastLocation();
        getLocation.addOnSuccessListener(activity, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                moveCamera(latLng);
            }
        });
    }

    public void startTrackingUserLocation() {
        client.getLastLocation()
            .addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        client.startTrackingUserLocation(LocationClient.ONE_MINUTE, getLocationCallback());
                    }
                }
            });
    }

    public void stopTrackingUserLocation() {
        client.stopTrackingUserLocation(getLocationCallback());
    }

    private LocationCallback getLocationCallback() {
        return new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                placeMarkerAtCurrentLocation();
                moveMapToCurrentLocation();
            }
        };
    }
}
