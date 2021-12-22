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

import com.example.gasitmobiledelvieryplatformapplication.model.SimpleRequestCallback;
import com.example.gasitmobiledelvieryplatformapplication.model.User;
import com.example.gasitmobiledelvieryplatformapplication.util.FieldUtil;

import es.dmoral.toasty.Toasty;

public class SignUpActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private EditText firstNameEditText, lastNameEditText, phoneNumberEditText, addressEditText,
            ageEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private RadioGroup genderRadioGroup, roleRadioGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initViewsAndListeners();
    }

    // Return to Log In Screen after Registering an Account or Cancel Registration...
    private void goToLogIn() {
        finish();
    }

    // Toast Successful Message after Registration...
    private void onRequestSuccess(String message) {
        progressBar.setVisibility(View.GONE);
        Toasty.success(this, message, Toasty.LENGTH_LONG).show();
    }

    // Toast Error/Failed Message after Registration...
    private void onRequestError(String error) {
        progressBar.setVisibility(View.GONE);
        Toasty.error(this, error, Toasty.LENGTH_SHORT).show();
    }

    // XML Layout ID's...
    private void initViewsAndListeners() {
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
        // roleRadioGroup = findViewById(R.id.userRoleRadioGroup);

        final ImageButton goBackButton = findViewById(R.id.goBackButton);
        final Button signUpButton = findViewById(R.id.signUpButton);
        goBackButton.setOnClickListener(view -> goToLogIn());
        signUpButton.setOnClickListener(view -> signUp());
    }

    // Registration Validation Process...
    private void signUp() {
        /// Field Validations for Errors and Missing...
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
                // FieldUtil.haveNotSelectedRadioButton(roleRadioGroup)
            return;

        // Radio Button Group for Getting the Value...
        RadioButton genderRadioButton = findViewById(genderRadioGroup.getCheckedRadioButtonId());
        // RadioButton roleRadioButton = findViewById(roleRadioGroup.getCheckedRadioButtonId());

        // Register Account to Database...
        progressBar.setVisibility(View.VISIBLE);
        User user = new User(firstNameEditText.getText().toString(),
                lastNameEditText.getText().toString(),
                phoneNumberEditText.getText().toString(),
                addressEditText.getText().toString(),
                genderRadioButton.getText().toString(),
                Integer.parseInt(ageEditText.getText().toString()),
                emailEditText.getText().toString(),
                passwordEditText.getText().toString());
                // roleRadioButton.getText().toString()

        // Toast Messages for Success, Failure and Error Registration...
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










/*
*
                <TextView
                    android:id="@+id/userRoleTextView"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="@string/signup_role_label"
                    android:textColor="@color/white"
                    android:textSize="@dimen/input_text_size"
                    app:layout_constraintBottom_toBottomOf="@id/userRoleRadioGroup"
                    app:layout_constraintStart_toStartOf="parent"
                    app:layout_constraintTop_toTopOf="@id/userRoleRadioGroup" />

                <RadioGroup
                    android:id="@+id/userRoleRadioGroup"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:orientation="horizontal"
                    app:layout_constraintEnd_toEndOf="parent"
                    app:layout_constraintStart_toStartOf="@id/thirdPanelGuideline"
                    app:layout_constraintTop_toBottomOf="@id/userConfirmPasswordTextInputLayout">

                    <RadioButton
                        android:id="@+id/userRetailerRole"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:buttonTint="@color/light_blue"
                        android:minHeight="48dp"
                        android:text="@string/signup_role_retailer"
                        android:textColor="@color/white" />

                    <RadioButton
                        android:id="@+id/userCustomerRole"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:buttonTint="@color/light_blue"
                        android:minHeight="48dp"
                        android:text="@string/signup_role_customer"
                        android:textColor="@color/white" />
                </RadioGroup>
*
*/