package com.mckanna.arrived;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.appcompat.app.AlertDialog;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.mckanna.arrived.background.LocationUpdatesService;
import com.mckanna.arrived.background.TripCompleteBroadcastReceiver;
import com.mckanna.arrived.data.Contact;
import com.mckanna.arrived.data.Destination;
import com.mckanna.arrived.data.TripWithContacts;
import com.mckanna.arrived.data.tasks.UpdateTripTask;
import com.mckanna.arrived.background.GeofenceBroadcastReceiver;
import com.mckanna.arrived.databinding.ActivityTravelBinding;
import com.mckanna.arrived.util.GeofencingRequestFactory;
import com.mckanna.arrived.util.InjectorUtils;
import com.mckanna.arrived.util.MapController;
import com.mckanna.arrived.view_models.ArrivedViewModel;

public class TravelActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "TravelActivity";

    private MapController mapController;
    private ArrivedViewModel viewModel;
    private TripWithContacts tripWithContacts;
    private ActivityTravelBinding binding;
    private TripControlFragment fragmentTripControl;
    private LocationUpdateIntervalFragment fragmentLocationUpdateInterval;
    private SupportMapFragment fragmentMap;
    private TripCompleteBroadcastReceiver tripCompleteBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTravelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = InjectorUtils.getViewModel(this);
        startObservingTripWithContacts();
        configureTripControlFragmentListeners();
        configureLocationUpdateIntervalFragment();
        configureMapFragment();
        configureBackButtonCallback();
        configureTripCompleteBroadcastReceiver();
    }

    @Override
    protected void onDestroy() {
        pauseTrip();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(tripCompleteBroadcastReceiver);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapController != null) {
            mapController.startTrackingUserLocation();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapController.stopTrackingUserLocation();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapController = new MapController(this, googleMap);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mapController.startTrackingUserLocation();
    }

    private void startObservingTripWithContacts() {
        long tripId = getIntent().getLongExtra("tripId", -1);
        if (tripId != -1) {
            viewModel.getTripWithContacts(tripId).observe(this, new Observer<TripWithContacts>() {
                @Override
                public void onChanged(TripWithContacts trip) {
                    boolean firstObservation = (tripWithContacts == null);
                    tripWithContacts = trip;
                    if (firstObservation) {
                        doInitialUISetup();
                    }
                    fragmentTripControl.setDestinationName(tripWithContacts.trip.destination.destinationName);
                    fragmentTripControl.setContactsList(Contact.getFormattedContactsString(tripWithContacts.contacts));
                    adjustServicesToMatchTripProgressState();
                }
            });
        }
    }

    private void doInitialUISetup() {
        LatLng destinationLatLng = tripWithContacts.trip.destination.getLatLng();
        mapController.placeMarkerAtLatLng(destinationLatLng);
        boolean shouldShowPause = tripWithContacts.trip.inProgress;
        fragmentTripControl.setPausedShowing(shouldShowPause);
    }

    private void adjustServicesToMatchTripProgressState() {
        if (tripWithContacts.trip.inProgress) {
            resumeTrip();
        } else {
            pauseTrip();
        }
    }

    private void pauseTrip() {
        stopLocationUpdatesService();
        GeofencingClient client = LocationServices.getGeofencingClient(this);
        client.removeGeofences(getGeofencePendingIntent());
        fragmentTripControl.setPausedShowing(false);
    }

    private void stopLocationUpdatesService() {
        Intent intent = getLocationUpdatesServiceIntent();
        getApplicationContext().stopService(intent);
    }

    private void resumeTrip() {
        long interval = fragmentLocationUpdateInterval.getInterval();
        startLocationUpdatesServiceWithInterval(interval);
        GeofencingClient client = LocationServices.getGeofencingClient(this);
        Destination destination = tripWithContacts.trip.destination;
        String requestId = tripWithContacts.trip.geofenceRequestId;
        GeofencingRequest geofencingRequest = GeofencingRequestFactory.makeGeofencingRequest(destination, requestId);
        client.addGeofences(geofencingRequest, getGeofencePendingIntent());
        fragmentTripControl.setPausedShowing(true);
    }

    private void startLocationUpdatesServiceWithInterval(long interval) {
        Intent intent = getLocationUpdatesServiceIntent();
        intent.putExtra("interval", interval);
        startService(intent);
    }

    private Intent getLocationUpdatesServiceIntent() {
        return new Intent(getApplicationContext(), LocationUpdatesService.class);
    }

    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void configureMapFragment() {
        FragmentManager fm = getSupportFragmentManager();
        fragmentMap = (SupportMapFragment) fm.findFragmentById(R.id.fragment_map);
        fragmentMap.getMapAsync(this);
    }

    private void configureLocationUpdateIntervalFragment() {
        FragmentManager fm = getSupportFragmentManager();
        fragmentLocationUpdateInterval = (LocationUpdateIntervalFragment) fm.findFragmentById(R.id.fragment_location_update_interval);
        fragmentLocationUpdateInterval.setOnLocationUpdateIntervalChangedListener(new LocationUpdateIntervalFragment.OnLocationUpdationIntervalChangedListener() {
            @Override
            public void onStartChangeInterval() {
                if (tripWithContacts.trip.inProgress) {
                    pauseTrip();
                }
            }

            @Override
            public void onStopChangeInterval(long newInterval) {
                if (tripWithContacts.trip.inProgress) {
                    resumeTrip();
                }
            }
        });
    }

    private void configureTripControlFragmentListeners() {
        FragmentManager fm = getSupportFragmentManager();
        fragmentTripControl = (TripControlFragment) fm.findFragmentById(R.id.fragment_control_trip);
        fragmentTripControl.setOnUserControlTripListener(new TripControlFragment.OnUserControlTripListener() {
            @Override
            public void onTripResumed() {
                updateTripInProgressTo(true);
            }

            @Override
            public void onTripPaused() {
                updateTripInProgressTo(false);
            }

            @Override
            public void onTripStopped() {
                attemptShowStopTripDialog();
            }
        });
    }

    private void updateTripInProgressTo(boolean inProgress) {
        tripWithContacts.trip.inProgress = inProgress;
        new UpdateTripTask(viewModel, tripWithContacts.trip).execute();
    }

    private void attemptShowStopTripDialog() {
        if (tripWithContacts != null && tripWithContacts.trip.inProgress) {
            showStopTripAlertDialog();
        } else {
            finish();
        }
    }

    private void configureBackButtonCallback() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                attemptShowStopTripDialog();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void showStopTripAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.title_cancel_trip)
                .setMessage(R.string.msg_contacts_will_not__be_notified)
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton(R.string.msg_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        updateTripInProgressTo(false);
                        finish();
                    }
                })
                .setNegativeButton(R.string.msg_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) { }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void configureTripCompleteBroadcastReceiver() {
        tripCompleteBroadcastReceiver = new TripCompleteBroadcastReceiver(getApplicationContext(), getLifecycle());
        getLifecycle().addObserver(tripCompleteBroadcastReceiver);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TripCompleteBroadcastReceiver.ACTION);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(tripCompleteBroadcastReceiver, intentFilter);
    }
}
