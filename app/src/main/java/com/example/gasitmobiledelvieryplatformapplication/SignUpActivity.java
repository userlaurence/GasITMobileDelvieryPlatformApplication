package com.example.gasitmobiledelvieryplatformapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.gasitmobiledelvieryplatformapplication.models.SimpleRequestCallback;
import com.example.gasitmobiledelvieryplatformapplication.models.User;
import com.example.gasitmobiledelvieryplatformapplication.util.FieldUtil;

import es.dmoral.toasty.Toasty;

public class SignUpActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private EditText firstNameEditText, lastNameEditText, phoneNumberEditText, addressEditText,
            ageEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private RadioGroup genderRadioGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initViewsAndListeners();
    }

    private void goToLogIn() {
        finish();
    }

    private void onRequestSuccess(String message) {
        progressBar.setVisibility(android.view.View.GONE);
        Toasty.success(this, message, Toasty.LENGTH_LONG).show();
    }

    private void onRequestError(String error) {
        progressBar.setVisibility(android.view.View.GONE);
        Toasty.error(this, error, Toasty.LENGTH_SHORT).show();
    }

    private void initViewsAndListeners() {
        // Initialize Views.
        progressBar = findViewById(R.id.progressBar);

        firstNameEditText = findViewById(R.id.userFirstNameEditText);
        lastNameEditText = findViewById(R.id.userLastNameEditText);
        phoneNumberEditText = findViewById(R.id.userPhoneNumberEditText);
        addressEditText = findViewById(R.id.userAddressEditText);

        genderRadioGroup = findViewById(R.id.userGenderRadioGroup);
        ageEditText = findViewById(R.id.userAgeEditText);

        emailEditText = findViewById(R.id.userEmailEditText);
        passwordEditText = findViewById(R.id.userPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.userConfirmPasswordEditText);

        final ImageButton goBackButton = findViewById(R.id.goBackButton);
        final Button signUpButton = findViewById(R.id.signUpButton);
        goBackButton.setOnClickListener(view -> goToLogIn());
        signUpButton.setOnClickListener(view -> signUp());
    }

    private void signUp() {
        if (FieldUtil.isEmptyEditText("First Name", firstNameEditText) ||
                FieldUtil.isEmptyEditText("Last Name", lastNameEditText) ||
                FieldUtil.isEmptyEditText("Phone number", phoneNumberEditText) ||
                FieldUtil.isEmptyEditText("Address", addressEditText) ||
                FieldUtil.haveNotSelectedRadioButton(genderRadioGroup) ||
                FieldUtil.isEmptyEditText("Age", ageEditText) ||
                FieldUtil.isEmptyEditText("Email", emailEditText) ||
                FieldUtil.isEmptyEditText("Password", passwordEditText) ||
                FieldUtil.isEmptyEditText("Confirm Password", confirmPasswordEditText) ||
                FieldUtil.isEmailIncorrect(emailEditText) ||
                FieldUtil.isIncorrectPasswordLength("Password", passwordEditText) ||
                FieldUtil.isIncorrectPasswordLength("Confirm Password", confirmPasswordEditText) ||
                FieldUtil.areDifferentPasswords(passwordEditText, confirmPasswordEditText))
            return; // There is an error within the fields and should not proceed to sign up.

        RadioButton genderRadioButton = findViewById(genderRadioGroup.getCheckedRadioButtonId());

        progressBar.setVisibility(View.VISIBLE);
        User user = new User(firstNameEditText.getText().toString(),
                lastNameEditText.getText().toString(),
                phoneNumberEditText.getText().toString(),
                addressEditText.getText().toString(),
                genderRadioButton.getText().toString(),
                Integer.parseInt(ageEditText.getText().toString()),
                emailEditText.getText().toString(),
                passwordEditText.getText().toString());

        user.signUp(new SimpleRequestCallback() {
            @Override
            public void onSuccess(String signupMessage) {
                user.write(new SimpleRequestCallback() {
                    @Override
                    public void onSuccess(String message) {
                        onRequestSuccess(signupMessage);
                        goToLogIn();
                    }

                    @Override
                    public void onFailure(String error) {
                        onRequestError(error);
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                onRequestError(error);
            }
        });
    }
}