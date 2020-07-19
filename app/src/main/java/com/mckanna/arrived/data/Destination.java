package com.mckanna.arrived.data;


import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.AddressComponent;
import com.google.android.libraries.places.api.model.AddressComponents;

public class Destination {
    public String destinationName;
    public double lat;
    public double lng;

    public static boolean hasStreetAddress(AddressComponents components) {
        for (AddressComponent component : components.asList()) {
            for (String type : component.getTypes()) {
                if (type.equals("street_number")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String getShortenedAddress(AddressComponents components) {
        StringBuilder builder = new StringBuilder();
        for (AddressComponent component : components.asList()) {
            for (String type : component.getTypes()) {
                if (type.equals("street_number")) {
                    builder.append(component.getName() + " ");
                } else if (type.equals("route")) {
                    builder.append(component.getName() + ", ");
                } else if (type.equals("locality")) {
                    builder.append(component.getName() + ", ");
                } else if (type.equals("administrative_area_level_1")) {
                    builder.append(component.getShortName());
                }
            }
        }
        return builder.toString();
    }

    public Destination() {}

    public Destination(String name, LatLng location) {
        destinationName = name;
        lat = location.latitude;
        lng = location.longitude;
    }

    public String getName() {
        return destinationName;
    }

    public LatLng getLatLng() {
        return new LatLng(lat, lng);
    }
}
