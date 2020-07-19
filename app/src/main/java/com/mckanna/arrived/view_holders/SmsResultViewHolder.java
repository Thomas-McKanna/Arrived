package com.mckanna.arrived.view_holders;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mckanna.arrived.R;
import com.mckanna.arrived.data.SmsResult;

public class SmsResultViewHolder extends RecyclerView.ViewHolder {
    private TextView textContactHandle;
    private TextView textResult;
    private Context context;

    public SmsResultViewHolder(@NonNull View itemView) {
        super(itemView);
        context = itemView.getContext();
        textContactHandle = itemView.findViewById(R.id.text_contact_handle);
        textResult = itemView.findViewById(R.id.text_result);
    }

    public void setContactHandle(String contactHandle) {
        textContactHandle.setText(contactHandle);
    }

    public void setResultFromCode(int resultCode) {
        int stringId;
        int colorId;
        if (resultCode == SmsResult.SUCCESS) {
            stringId = R.string.msg_message_sent;
            colorId = ContextCompat.getColor(context, R.color.success_green);
        } else {
            stringId = R.string.msg_message_failed;
            colorId = ContextCompat.getColor(context, R.color.failure_red);
        }
        textResult.setText(stringId);
        textResult.setTextColor(colorId);
    }

    public void setResultColor(int colorId) {
        textResult.setTextColor(colorId);
    }
}
