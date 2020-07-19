package com.mckanna.arrived;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.View;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.AddressComponents;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.snackbar.Snackbar;
import com.mckanna.arrived.data.Destination;
import com.mckanna.arrived.data.Trip;
import com.mckanna.arrived.data.tasks.DeleteTripTask;
import com.mckanna.arrived.data.tasks.UpdateTripTask;
import com.mckanna.arrived.databinding.ActivityDestinationBinding;
import com.mckanna.arrived.util.InjectorUtils;
import com.mckanna.arrived.util.LocationClient;
import com.mckanna.arrived.util.MapController;
import com.mckanna.arrived.util.Permissions;
import com.mckanna.arrived.view_models.ArrivedViewModel;

import java.util.Arrays;
import java.util.List;

public class DestinationActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "DestinationActivity";

    private MapController mapController;
    private Trip trip;
    private ArrivedViewModel viewModel;
    private Permissions permissions;
    private ActivityDestinationBinding binding;
    private AutocompleteSupportFragment fragmentAutocomplete;
    private SupportMapFragment fragmentMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDestinationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        permissions = InjectorUtils.getPermissions(this);
        viewModel = InjectorUtils.getViewModel(this);
        startObservingTrip();
        configureMapFragment();
        configureAutocompleteFragment();
        setPickContactsButtonListener();
        configureBackButtonCallback();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LocationClient client = new LocationClient(this);
        mapController = new MapController(this, googleMap);
        attemptMoveMapToCurrentLocation();
        setAutocompleteListener(fragmentAutocomplete);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Permissions.ACCESS_LOCATION_PERMISSION) {
            boolean allPermissionsGranted = this.permissions.checkAllGranted(grantResults);
            if (allPermissionsGranted && !destinationIsSet()) {
                mapController.moveMapToCurrentLocation();
            }
        }
    }

    private void startObservingTrip() {
        long tripId = getIntent().getLongExtra("tripId", -1);
        if (tripId == -1) {
            return;
        } else {
            viewModel.getTrip(tripId).observe(this, new Observer<Trip>() {
                @Override
                public void onChanged(Trip theTrip) {
                    trip = theTrip;
                    attemptUpdateUI();
                }
            });
        }
    }

    private void attemptUpdateUI() {
        if (destinationIsSet()) {
            Destination destination = trip.destination;
            fragmentAutocomplete.setText(destination.destinationName);
            mapController.placeMarkerAtLatLng(destination.getLatLng());
            mapController.moveCamera(destination.getLatLng());
            binding.buttonPickContacts.setEnabled(true);
        } else {
            binding.buttonPickContacts.setEnabled(false);
        }
    }

    private void configureMapFragment() {
        FragmentManager fm = getSupportFragmentManager();
        fragmentMap = (SupportMapFragment) fm.findFragmentById(R.id.fragment_map);
        fragmentMap.getMapAsync(this);
    }

    private void configureAutocompleteFragment() {
        FragmentManager fm = getSupportFragmentManager();
        fragmentAutocomplete = (AutocompleteSupportFragment) fm.findFragmentById(R.id.fragment_autocomplete);
        List<Place.Field> fields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS_COMPONENTS, Place.Field.LAT_LNG);
        fragmentAutocomplete.setPlaceFields(fields);
        fragmentAutocomplete.setHint("Where are you going?");
    }

    private void setAutocompleteListener(@NonNull final AutocompleteSupportFragment autocompleteFragment) {
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                if (trip != null) {
                    AddressComponents components = place.getAddressComponents();
                    if (Destination.hasStreetAddress(components)) {
                        String name = Destination.getShortenedAddress(components);
                        LatLng location = place.getLatLng();
                        mapController.placeMarkerAtLatLng(location);
                        mapController.moveCamera(location);
                        trip.destination = new Destination(name, location);
                        new UpdateTripTask(viewModel, trip).execute();
                    } else {
                        Snackbar.make(binding.getRoot(), getString(R.string.msg_choose_specific_location), Snackbar.LENGTH_LONG).show();
                        binding.buttonPickContacts.setEnabled(false);
                    }
                }
            }
            @Override
            public void onError(@NonNull Status status) { }
        });
    }

    private void setPickContactsButtonListener() {
        binding.buttonPickContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DestinationActivity.this, ContactsActivity.class);
                intent.putExtra("tripId", trip.id);
                startActivity(intent);
            }
        });
    }

    private void configureBackButtonCallback() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (trip != null && !trip.hasDestinationAndContacts) {
                    new DeleteTripTask(viewModel, trip).execute();
                }
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void attemptMoveMapToCurrentLocation() {
        if (permissions.PlayServicesActive()) {
            if (permissions.have(Permissions.ACCESS_LOCATION_PERMISSION)) {
                if (!destinationIsSet()) {
                    mapController.moveMapToCurrentLocation();
                }
            } else {
                permissions.request(this, Permissions.ACCESS_LOCATION_PERMISSION);
            }
        } else {
            permissions.requestPlayServicesIfNecessary(this);
        }
    }

    private boolean destinationIsSet() {
        return trip != null && trip.destination != null;
    }
}
