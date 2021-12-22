package com.example.gasitmobiledelvieryplatformapplication.util;

import android.util.Patterns;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

// Registration Form Validations and Verification...
public class FieldUtil {
    public static int MIN_PASSWORD_LENGTH = 8;

    public static boolean isEmptyEditText(String field, EditText editText) {
        if (!editText.getText().toString().trim().isEmpty())
            return false;

        editText.setError(field + " should not be empty.");
        editText.requestFocus();
        return true;
    }

    public static boolean haveNotSelectedRadioButton(RadioGroup radioGroup) {
        if (radioGroup.getCheckedRadioButtonId() != -1)
            return false;

        RadioButton radioButton = (RadioButton) radioGroup.getChildAt(0);
        radioButton.requestFocus();
        radioButton.setError("Please select one of the options.");
        return true;
    }

    public static boolean isEmailIncorrect(EditText editText) {
        if (Patterns.EMAIL_ADDRESS.matcher(
                editText.getText().toString().trim())
                .matches())
            return false;

        editText.setError("Please provide valid email.");
        editText.requestFocus();
        return true;
    }

    public static boolean isIncorrectPasswordLength(String passwordField, EditText editText) {
        if (isEmptyEditText(passwordField, editText))
            return true;

        if (editText.getText().toString().trim().length() >= MIN_PASSWORD_LENGTH)
            return false;

        editText.setError("Minimum password length should be 8 characters.");
        editText.requestFocus();
        return true;
    }

    public static boolean areDifferentPasswords(EditText aEditText, EditText bEditText) {
        if (aEditText.getText().toString().trim()
                .equals(bEditText.getText().toString().trim()))
            return false;

        bEditText.setError("Confirm Password is different from Password.");
        bEditText.requestFocus();
        return true;
    }
}
