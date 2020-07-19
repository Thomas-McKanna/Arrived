package com.mckanna.arrived.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mckanna.arrived.R;
import com.mckanna.arrived.data.SmsResult;
import com.mckanna.arrived.view_holders.SmsResultViewHolder;

import java.util.List;

public class SmsResultListAdapter extends RecyclerView.Adapter<SmsResultViewHolder> {
    private List<SmsResult> smsResults;

    public SmsResultListAdapter(List<SmsResult> smsResults) {
        this.smsResults = smsResults;
    }

    @NonNull
    @Override
    public SmsResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_sms_result, parent, false);
        SmsResultViewHolder vh = new SmsResultViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull SmsResultViewHolder holder, int position) {
        SmsResult result = smsResults.get(position);
        holder.setContactHandle(result.getContactHandle());
        holder.setResultFromCode(result.getResultCode());
    }

    @Override
    public int getItemCount() {
        return smsResults.size();
    }
}
