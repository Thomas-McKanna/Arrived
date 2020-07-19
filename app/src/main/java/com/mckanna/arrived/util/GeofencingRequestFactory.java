package com.mckanna.arrived.util;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.model.LatLng;
import com.mckanna.arrived.data.Destination;

public class GeofencingRequestFactory {
    private static final LatLng FAKE_DESTINATION = new LatLng(45.391, -121.701);
    private static final int GEOFENCE_RADIUS_IN_METERS = 1000;

    public static GeofencingRequest makeGeofencingRequest(Destination destination, String requestId) {
        Geofence geofence = getGeofenceForLatLng(destination.getLatLng(), requestId);
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofence(geofence);
        return builder.build();
    }

    private static Geofence getGeofenceForLatLng(LatLng latLng, String requestId) {
        Geofence.Builder builder = new Geofence.Builder();
        builder.setRequestId(requestId);
//        Double lat = latLng.latitude;
//        Double lng = latLng.longitude;
        Double lat = FAKE_DESTINATION.latitude;
        Double lng = FAKE_DESTINATION.longitude;
        builder.setCircularRegion(lat, lng, GEOFENCE_RADIUS_IN_METERS);
        builder.setExpirationDuration(Geofence.NEVER_EXPIRE);
        builder.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER);
        return builder.build();
    }
}
