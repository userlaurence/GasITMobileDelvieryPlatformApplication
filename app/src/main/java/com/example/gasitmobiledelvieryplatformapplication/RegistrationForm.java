package com.example.gasitmobiledelvieryplatformapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.gasitmobiledelvieryplatformapplication.R.id;

public class RegistrationForm extends AppCompatActivity {

    Button cancelBtn;
    Button registerBtn;
    EditText firstName, lastName, address, contact, username, password, confirmPassword;
    RadioGroup gender, accountType;
    EditText age;
    CheckBox terms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_form);

        cancelBtn = findViewById(id.regCancelBtn);
        cancelBtn.setOnClickListener(v -> returnSignInRegister());

        registerBtn = findViewById(id.regRegisterBtn);
        registerBtn.setOnClickListener(v -> registerValidation());

        firstName = findViewById(id.regFirstName);
        lastName = findViewById(id.regLastName);
        address = findViewById(id.regAddress);
        contact = findViewById(id.regContact);
        username = findViewById(id.regUsername);
        password = findViewById(id.regPassword1);
        confirmPassword = findViewById(id.regPassword2);
        gender = (RadioGroup) findViewById(id.regSexGroupRadioBtn);
        accountType = (RadioGroup) findViewById(id.regTypeGroupRadioBtn);
        age = findViewById(id.regAge);
        terms = (CheckBox) findViewById(id.regTermsChckBox);

    }

    public void returnSignInRegister() {
        Intent registerScreen = new Intent(this, SignInRegister.class);
        startActivity(registerScreen);
    }

    public void registerValidation() {
//        Intent registerScreen = new Intent(this, RegistrationForm.class);
//        startActivity(registerScreen);



    }
}