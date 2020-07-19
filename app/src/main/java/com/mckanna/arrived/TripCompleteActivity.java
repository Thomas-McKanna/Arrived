package com.mckanna.arrived;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.mckanna.arrived.adapters.SmsResultListAdapter;
import com.mckanna.arrived.data.SmsResult;
import com.mckanna.arrived.databinding.ActivityTripCompleteBinding;

import java.util.ArrayList;

public class TripCompleteActivity extends AppCompatActivity {
    private static final String TAG = "TripCompleteActivity";

    private ActivityTripCompleteBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTripCompleteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        ArrayList<SmsResult> smsResults = intent.getParcelableArrayListExtra("smsResults");
        configureSmsResultRecyclerView(smsResults);
        configureNewTripButton();
    }

    private void configureSmsResultRecyclerView(ArrayList<SmsResult> smsResults) {
        SmsResultListAdapter adapter = new SmsResultListAdapter(smsResults);
        binding.recyclerSmsResults.setAdapter(adapter);
        binding.recyclerSmsResults.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerSmsResults.hasFixedSize();
    }

    private void configureNewTripButton() {
        binding.buttonNewTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
