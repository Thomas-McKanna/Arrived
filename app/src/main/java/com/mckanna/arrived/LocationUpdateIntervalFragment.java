package com.mckanna.arrived;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mckanna.arrived.databinding.FragmentLocationUpdateIntervalBinding;
import com.mckanna.arrived.util.LocationClient;

import java.util.ArrayList;
import java.util.List;

public class LocationUpdateIntervalFragment extends Fragment {
    private static final int NUM_INTERVALS = 5;
    private static final int DEFAULT_INTERVAL = 2; // 10 minutes
    private static final long[] INTERVALS = {
            LocationClient.ONE_MINUTE,
            LocationClient.FIVE_MINUTES,
            LocationClient.TEN_MINUTES,
            LocationClient.THIRTY_MINUTES,
            LocationClient.SIXTY_MINUTES
    };

    private FragmentLocationUpdateIntervalBinding binding;
    private int selectedInterval;
    private OnLocationUpdationIntervalChangedListener listener;
    private List<TextView> intervalLabelViews;

    public interface OnLocationUpdationIntervalChangedListener {
        void onStartChangeInterval();
        void onStopChangeInterval(long newInterval);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLocationUpdateIntervalBinding.inflate(inflater, container, false);
        selectedInterval = DEFAULT_INTERVAL;
        makeIntervalLabelArray();
        configureSeekBar();
        return binding.getRoot();
    }

    // Required.
    public LocationUpdateIntervalFragment() { }

    public void setOnLocationUpdateIntervalChangedListener(OnLocationUpdationIntervalChangedListener listener) {
        this.listener = listener;
    }

    public long getInterval() {
        return INTERVALS[selectedInterval];
    }

    private void makeIntervalLabelArray() {
        intervalLabelViews = new ArrayList<TextView>();
        intervalLabelViews.add(binding.slider1);
        intervalLabelViews.add(binding.slider5);
        intervalLabelViews.add(binding.slider10);
        intervalLabelViews.add(binding.slider30);
        intervalLabelViews.add(binding.slider60);
    }

    private void configureSeekBar() {
        binding.seekbarLocationInterval.setMax(NUM_INTERVALS - 1);
        binding.seekbarLocationInterval.setProgress(DEFAULT_INTERVAL);
        updateSelectedLocationInterval();
        binding.seekbarLocationInterval.setOnSeekBarChangeListener(getSeekBarListener());
    }

    private SeekBar.OnSeekBarChangeListener getSeekBarListener() {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                selectedInterval = i;
                updateSelectedLocationInterval();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                listener.onStartChangeInterval();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                listener.onStopChangeInterval(INTERVALS[selectedInterval]);
            }
        };
    }

    private void updateSelectedLocationInterval() {
        resetAllIntervalLabels();
        int primaryColor = ContextCompat.getColor(getContext(), R.color.colorPrimary);
        TextView selectedView = intervalLabelViews.get(selectedInterval);
        selectedView.setTextColor(primaryColor);
        selectedView.setTypeface(null, Typeface.BOLD);
    }

    private void resetAllIntervalLabels() {
        int black = ContextCompat.getColor(getContext(), R.color.black);
        for (TextView view : intervalLabelViews) {
            view.setTextColor(black);
            view.setTypeface(null, Typeface.NORMAL);
        }
    }
}
