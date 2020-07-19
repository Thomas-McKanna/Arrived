package com.mckanna.arrived.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.mckanna.arrived.R;

public class PermissionRationaleDialogFragment extends DialogFragment {

    private String rationale;
    private DialogInterface.OnClickListener clickListener;

    public PermissionRationaleDialogFragment(String rationale, DialogInterface.OnClickListener listener) {
        this.rationale = rationale;
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(rationale)
                .setTitle(R.string.title_permission_needed)
                .setPositiveButton(android.R.string.ok, clickListener);
        return builder.create();
    }
}
