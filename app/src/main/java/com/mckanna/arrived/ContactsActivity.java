package com.mckanna.arrived;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.mckanna.arrived.adapters.PhoneNumberListAdapter;
import com.mckanna.arrived.data.Contact;
import com.mckanna.arrived.data.Trip;
import com.mckanna.arrived.data.TripWithContacts;
import com.mckanna.arrived.data.tasks.AddContactToTripTask;
import com.mckanna.arrived.data.tasks.RemoveContactFromTripTask;
import com.mckanna.arrived.data.tasks.UpdateTripTask;
import com.mckanna.arrived.databinding.ActivityContactsBinding;
import com.mckanna.arrived.util.InjectorUtils;
import com.mckanna.arrived.util.Permissions;
import com.mckanna.arrived.util.PhoneNumberParser;
import com.mckanna.arrived.view_models.ArrivedViewModel;

import java.util.List;

public class ContactsActivity extends AppCompatActivity {
    private static final String TAG = "ContactsActivity";

    private PhoneNumberListAdapter adapterPhoneNumbers;
    private ActivityContactsBinding binding;
    private Permissions permissions;
    private Trip trip;
    private List<Contact> contacts;
    private ArrivedViewModel viewModel;
    private PickContactFragment fragmentPickContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        permissions = InjectorUtils.getPermissions(this);
        viewModel = InjectorUtils.getViewModel(this);
        startObservingTrip();
        configureRecyclerView();
        configurePickContactsFragment();
        setStartTripButtonListener();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Permissions.SEND_SMS_PERMISSION && Permissions.checkAllGranted(grantResults)) {
            if (Permissions.checkAllGranted(grantResults)) {
                launchTravelActivity();
            } else {
                this.permissions.request(this, Permissions.SEND_SMS_PERMISSION);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startObservingTrip() {
        long tripId = getIntent().getLongExtra("tripId", -1);
        if (tripId != -1) {
            viewModel.getTripWithContacts(tripId).observe(this, new Observer<TripWithContacts>() {
                @Override
                public void onChanged(TripWithContacts tripWithContacts) {
                    if (tripWithContacts != null) {
                        trip = tripWithContacts.trip;
                        contacts = tripWithContacts.contacts;
                        if (trip.inProgress) {
                            launchTravelActivity();
                        } else {
                            adapterPhoneNumbers.setData(contacts);
                            updateStartTripButton(contacts.size());
                        }
                    }
                }

            });
        }
    }

    private void updateStartTripButton(int numberContacts) {
        boolean shouldEnable = (numberContacts != 0);
        binding.buttonStartTrip.setEnabled(shouldEnable);
    }

    private void configureRecyclerView() {
        adapterPhoneNumbers = new PhoneNumberListAdapter(new PhoneNumberListAdapter.OnContactRemovedListener() {
            @Override
            public void onContactRemoved(Contact contact) {
                int numContactsAfterRemoval = contacts.size() - 1;
                new RemoveContactFromTripTask(viewModel, trip, contact).execute();
                if (numContactsAfterRemoval <= 0) {
                    trip.hasDestinationAndContacts = false;
                    new UpdateTripTask(viewModel, trip).execute();
                }
            }
        });
        binding.recyclerPhoneNumbers.setAdapter(adapterPhoneNumbers);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerPhoneNumbers.setLayoutManager(layoutManager);
    }

    private void attemptToAddPhoneNumberToArray(String phoneNumber) {
        if (PhoneNumberParser.parseNumber(phoneNumber).equals(PhoneNumberParser.INVALID_PHONE_NUMBER)) {
            showSnackbar(getString(R.string.error_invalid_phone_number));
            return;
        }
        if (phoneNumberAlreadyInArray(phoneNumber)) {
            showSnackbar(getString(R.string.error_duplicate_phone_number));
            return;
        }
        String parsedNumber = PhoneNumberParser.parseNumber(phoneNumber);
        Contact newContact = new Contact();
        newContact.phoneNumber = parsedNumber;
        new AddContactToTripTask(viewModel, trip, newContact).execute();
    }

    private void showSnackbar(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }

    private boolean phoneNumberAlreadyInArray(String newNumber) {
        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i).phoneNumber.equals(newNumber)) {
                return true;
            }
        }
        return false;
    }

    private void setStartTripButtonListener() {
        binding.buttonStartTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (permissions.need(Permissions.SEND_SMS_PERMISSION)) {
                    permissions.request(ContactsActivity.this, Permissions.SEND_SMS_PERMISSION);
                } else {
                    setTripInProgress();
                }
            }
        });
    }

    private void setTripInProgress() {
        trip.inProgress = true;
        new UpdateTripTask(viewModel, trip).execute();
    }

    private void launchTravelActivity() {
        Intent intent = new Intent(this, TravelActivity.class);
        intent.putExtra("tripId", trip.id);
        startActivity(intent);
    }

    private void configurePickContactsFragment() {
        FragmentManager fm = getSupportFragmentManager();
        fragmentPickContacts = (PickContactFragment) fm.findFragmentById(R.id.fragment_pick_contact);
        fragmentPickContacts.setOnContactPickedListener(new PickContactFragment.OnContactPickedListener() {
            @Override
            public void onContactPicked(Contact contact) {
                attemptToAddPhoneNumberToArray(contact.phoneNumber);
            }
        });
    }
}
