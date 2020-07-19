package com.mckanna.arrived;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mckanna.arrived.databinding.FragmentTripControlBinding;

public class TripControlFragment extends Fragment {
    private FragmentTripControlBinding binding;
    private OnUserControlTripListener listener;
    private boolean showingPause;

    public interface OnUserControlTripListener {
        void onTripResumed();
        void onTripPaused();
        void onTripStopped();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTripControlBinding.inflate(inflater, container, false);
        setPauseResumeButtonListener();
        setStopButtonListener();
        return binding.getRoot();
    }

    // Required.
    public TripControlFragment() { }

    public void setOnUserControlTripListener(OnUserControlTripListener listener) {
        this.listener = listener;
    }

    public void setDestinationName(String name) {
        binding.textDestinationName.setText(name);
    }

    public void setContactsList(String list) {
        binding.textContacts.setText(list);
    }

    public void setPausedShowing(boolean shouldShowPause) {
        showingPause = shouldShowPause;
        Drawable img;
        String status;
        if (showingPause) {
            img = ContextCompat.getDrawable(getContext(), R.drawable.ic_pause);
            status = getString(R.string.msg_pause_trip);
        } else {
            img = ContextCompat.getDrawable(getContext(), R.drawable.ic_play);
            status = getString(R.string.msg_resume_trip);
        }
        binding.buttonPauseResumeTrip.setText(status);
        binding.buttonPauseResumeTrip.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
    }

    private void setPauseResumeButtonListener() {
        binding.buttonPauseResumeTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePauseResumeButton();
            }
        });
    }

    private void togglePauseResumeButton() {
        showingPause = !showingPause;
        setPausedShowing(showingPause);
        if (listener != null) {
            triggerCallbackForPauseState();
        }
    }

    private void triggerCallbackForPauseState() {
        if (showingPause) {
            listener.onTripResumed();
        } else {
            listener.onTripPaused();
        }
    }

    private void setStopButtonListener() {
        binding.buttonStopTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onTripStopped();
            }
        });
    }
}
