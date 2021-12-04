package com.example.gasitmobiledelvieryplatformapplication.dialog;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.gasitmobiledelvieryplatformapplication.R;

public class CancelConfirmationDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireContext())
                .setMessage(getString(R.string.gasoline_cancel_create))
                .setPositiveButton(getString(R.string.ok), (dialog, which) -> {} )
                .create();
    }

    public static String TAG = "CancelConfirmationDialogFragment";
}
