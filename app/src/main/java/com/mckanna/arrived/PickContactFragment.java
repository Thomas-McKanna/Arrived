package com.mckanna.arrived;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mckanna.arrived.data.Contact;
import com.mckanna.arrived.databinding.FragmentPickContactBinding;
import com.mckanna.arrived.util.InjectorUtils;
import com.mckanna.arrived.util.Permissions;

import static android.app.Activity.RESULT_OK;

public class PickContactFragment extends Fragment {
    private static final int SELECT_CONTACT_REQUEST = 9876;

    private FragmentPickContactBinding binding;
    private Permissions permissions;
    private OnContactPickedListener listener;

    public interface OnContactPickedListener {
        void onContactPicked(Contact contact);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissions = InjectorUtils.getPermissions((AppCompatActivity) getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPickContactBinding.inflate(inflater, container, false);
        setPickContactButtonListener();
        setAddNumberButtonListener();
        setNumberEditTextListener();
        setInitialUIState();
        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == SELECT_CONTACT_REQUEST && resultCode == RESULT_OK) {
            Contact contact = extractContact(data);
            if (contact != null) {
                listener.onContactPicked(contact);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Permissions.READ_CONTACTS_PERMISSION && Permissions.checkAllGranted(grantResults)) {
            launchSelectContactActivity();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Required
    public PickContactFragment() { }

    public void setOnContactPickedListener(OnContactPickedListener listener) {
        this.listener = listener;
    }

    private void setPickContactButtonListener() {
        binding.buttonPickContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (permissions.need(Permissions.READ_CONTACTS_PERMISSION)) {
                    permissions.request((AppCompatActivity) getActivity(), Permissions.READ_CONTACTS_PERMISSION);
                } else {
                    launchSelectContactActivity();
                }
            }
        });
    }

    private void launchSelectContactActivity() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, SELECT_CONTACT_REQUEST);
        }
    }

    private void setAddNumberButtonListener() {
        binding.buttonAddNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = binding.editNumber.getText().toString();
                Contact contact = new Contact();
                contact.phoneNumber = phoneNumber;
                listener.onContactPicked(contact);
                binding.editNumber.setText("");
            }
        });
    }

    private void setNumberEditTextListener() {
        binding.editNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                // (XXX) XXX-XXXX is 14 characters
                int phoneNumberLength = 14;
                boolean shouldEnable = (s.length() == phoneNumberLength);
                binding.buttonAddNumber.setEnabled(shouldEnable);
            }
        });
    }

    private void setInitialUIState() {
        binding.editNumber.setTransformationMethod(null); // do not hide numbers
        binding.buttonAddNumber.setEnabled(false);
    }

    private Contact extractContact(Intent data) {
        Uri contactUri = data.getData();
        Cursor cursor = getCursorForContactUri(contactUri);
        Contact contact = getContactFromCursor(cursor);
        return contact;
    }

    private Cursor getCursorForContactUri(Uri contactUri) {
        String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
        return getActivity().getContentResolver().query(contactUri, projection,
                null, null, null);
    }

    private Contact getContactFromCursor(Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            String phoneNumber = cursor.getString(numberIndex);
            Contact contact = new Contact();
            contact.phoneNumber = phoneNumber;
            return contact;
        } else {
            return null;
        }
    }
}
