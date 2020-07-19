package com.mckanna.arrived;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.mckanna.arrived.adapters.TripListAdapter;
import com.mckanna.arrived.data.Trip;
import com.mckanna.arrived.data.TripWithContacts;
import com.mckanna.arrived.data.tasks.InsertNewEmptyTripTask;
import com.mckanna.arrived.data.tasks.OnTripInsertedCallback;
import com.mckanna.arrived.databinding.ActivityRecentTripsBinding;
import com.mckanna.arrived.util.InjectorUtils;
import com.mckanna.arrived.view_models.ArrivedViewModel;

import java.util.ArrayList;
import java.util.List;

public class RecentTripsActivity extends AppCompatActivity {
    private static String TAG = "RecentTripsActivity";

    private TripListAdapter adapter;
    private ArrayList<TripWithContacts> tripsWithContacts;
    private ArrivedViewModel viewModel;
    private ActivityRecentTripsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecentTripsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        configurePreviousTripsRecycler();
        setAddTripButtonListener();
        startObservingTripsWithContacts();
    }

    private void startObservingTripsWithContacts() {
        viewModel = InjectorUtils.getViewModel(this);
        viewModel.getTripsWithContacts().observe(this, new Observer<List<TripWithContacts>>() {
            @Override
            public void onChanged(List<TripWithContacts> tripWithContacts) {
                adapter.setData(tripWithContacts);
                if (tripWithContacts.isEmpty()) {
                    binding.cardNoRecentTrips.setVisibility(View.VISIBLE);
                } else {
                    binding.cardNoRecentTrips.setVisibility(View.GONE);
                }
            }
        });
    }

    private void configurePreviousTripsRecycler() {
        adapter = new TripListAdapter(new TripListAdapter.OnClickTripListener() {
            @Override
            public void onClickTrip(Trip trip) {
                Intent firstIntent = new Intent(RecentTripsActivity.this, DestinationActivity.class);
                firstIntent.putExtra("tripId", trip.id);
                Intent secondIntent = new Intent(RecentTripsActivity.this, ContactsActivity.class);
                secondIntent.putExtra("tripId", trip.id);
                startActivities(new Intent[]{firstIntent, secondIntent});
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerPreviousTrips.setLayoutManager(layoutManager);
        DividerItemDecoration decoration = new DividerItemDecoration(binding.recyclerPreviousTrips.getContext(), DividerItemDecoration.VERTICAL);
        binding.recyclerPreviousTrips.addItemDecoration(decoration);
        binding.recyclerPreviousTrips.setAdapter(adapter);
    }

    private void setAddTripButtonListener() {
        binding.floatingButtonAddTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new InsertNewEmptyTripTask(viewModel, new OnTripInsertedCallback() {
                    @Override
                    public void onTripInserted(Long id) {
                        Intent intent = new Intent(RecentTripsActivity.this, DestinationActivity.class);
                        intent.putExtra("tripId", id);
                        startActivity(intent);
                    }
                }).execute();
            }
        });
    }
}
