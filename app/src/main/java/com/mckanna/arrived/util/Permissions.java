package com.mckanna.arrived.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.HashMap;
import java.util.Map;

public class Permissions {
    public static int GOOGLE_PLAY_RESPONSE = -1;
    public static int ACCESS_LOCATION_PERMISSION = 0;
    public static int READ_CONTACTS_PERMISSION = 1;
    public static int SEND_SMS_PERMISSION = 2;

    private static final String DIALOG_TAG = "PERMISSION_REQUESTER_DIALOG";

    private Context context;
    private Map<Integer, String[]> idToPermissions;
    private Map<Integer, String> idToRationale;

    public static boolean checkAllGranted(int[] grantResults) {
        boolean permissionGranted = true;
        for (int i = 0; i < grantResults.length; i++) {
            permissionGranted = permissionGranted && grantResults[i] == PackageManager.PERMISSION_GRANTED;
        }
        return permissionGranted;
    }

    public Permissions(Context context) {
        this.context = context;
        idToPermissions = new HashMap<>();
        idToRationale = new HashMap<>();
    }

    public boolean have(int permissionId) {
        String[] permission = idToPermissions.get(permissionId);
        boolean havePermission = true;
        for (int i = 0; i < permission.length; i++) {
            havePermission = havePermission &&
                    ContextCompat.checkSelfPermission(context, permission[i]) == PackageManager.PERMISSION_GRANTED;
        }
        return havePermission;
    }

    public boolean need(int permissionId) {
        return !have(permissionId);
    }

    public void register(int permissionId, String[] permissionNames, String rationale) {
        idToPermissions.put(permissionId, permissionNames);
        idToRationale.put(permissionId, rationale);
    }

    public void request(AppCompatActivity activity, int permissionId) {
        String[] permissions = idToPermissions.get(permissionId);
        if (shouldShowRationaleForAtLeastOne(activity, permissions)) {
            showRationaleAndThenRequestPermission(activity, permissionId);
        } else {
            ActivityCompat.requestPermissions(activity, permissions, permissionId);
        }
    }

    public void requestPlayServicesIfNecessary(AppCompatActivity activity) {
        GoogleApiAvailability availability = GoogleApiAvailability.getInstance();
        int result = availability.isGooglePlayServicesAvailable(context);
        if (result != ConnectionResult.SUCCESS) {
            Dialog googlePlayDialog = availability.getErrorDialog(activity, result, GOOGLE_PLAY_RESPONSE);
        }
    }

    public boolean PlayServicesActive() {
        GoogleApiAvailability availability = GoogleApiAvailability.getInstance();
        int result = availability.isGooglePlayServicesAvailable(context);
        return result == ConnectionResult.SUCCESS;
    }

    private boolean shouldShowRationaleForAtLeastOne(Activity activity, String[] permissions) {
        boolean shouldShowRationale = false;
        boolean currentNeedsRationale;
        for (int i = 0; i < permissions.length; i++) {
            currentNeedsRationale = ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[i]);
            shouldShowRationale = shouldShowRationale || currentNeedsRationale;
        }
        return shouldShowRationale;
    }

    private void showRationaleAndThenRequestPermission(final AppCompatActivity activity, final int id) {
        final String[] permissions = idToPermissions.get(id);
        String rationale = idToRationale.get(id);
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ActivityCompat.requestPermissions(activity, permissions, id);
            }
        };
        PermissionRationaleDialogFragment dialog = new PermissionRationaleDialogFragment(rationale, listener);
        dialog.show(activity.getSupportFragmentManager(), DIALOG_TAG);
    }
}
