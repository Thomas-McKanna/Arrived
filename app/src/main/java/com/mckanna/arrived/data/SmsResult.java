package com.mckanna.arrived.data;

import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;

public class SmsResult implements Parcelable {
    public static final int SUCCESS = Activity.RESULT_OK;
    public static final String RESULT_CODE = "resultCode";
    public static final String CONTACT_HANDLE = "contactHandle";

    private String contactHandle;
    private int resultCode;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(contactHandle);
        parcel.writeInt(resultCode);
    }

    public String getContactHandle() {
        return contactHandle;
    }

    public int getResultCode() {
        return resultCode;
    }

    private SmsResult(Parcel in) {
        contactHandle = in.readString();
        resultCode = in.readInt();
    }

    public SmsResult(String contactHandle, int result) {
        this.contactHandle = contactHandle;
        this.resultCode = result;
    }

    public static final Parcelable.Creator<SmsResult> CREATOR = new Parcelable.Creator<SmsResult>() {
        @Override
        public SmsResult createFromParcel(Parcel parcel) {
            return new SmsResult(parcel);
        }

        @Override
        public SmsResult[] newArray(int i) {
            return new SmsResult[i];
        }
    };
}
