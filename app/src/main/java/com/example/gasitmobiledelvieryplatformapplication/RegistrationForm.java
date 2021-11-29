package com.example.gasitmobiledelvieryplatformapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gasitmobiledelvieryplatformapplication.R.id;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationForm extends AppCompatActivity {

//    FirebaseDatabase rootNode;
//    DatabaseReference reference;

//    Button cancelBtn;
//    Button registerBtn;

    //    EditText firstName, lastName, address, contact, username, password, confirmPassword;
    RadioGroup gender, accountType;
    //    EditText age;
    CheckBox terms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_form);

        Button cancelBtn = findViewById(id.regCancelBtn);
        cancelBtn.setOnClickListener(v -> returnSignInRegister());

        Button registerBtn = findViewById(id.regRegisterBtn);
        registerBtn.setOnClickListener(v -> validation());

        final EditText firstName = findViewById(id.regFirstName);
        final EditText lastName = findViewById(id.regLastName);
        final EditText address = findViewById(id.regAddress);
        final EditText contact = findViewById(id.regContact);
        final EditText username = findViewById(id.regUsername);
        final EditText password = findViewById(id.regPassword1);
        final EditText confirmPassword = findViewById(id.regPassword2);
        final RadioGroup gender = (RadioGroup) findViewById(id.regSexGroupRadioBtn);
        final RadioGroup accountType = (RadioGroup) findViewById(id.regTypeGroupRadioBtn);
        final EditText age = findViewById(id.regAge);
        final CheckBox terms = (CheckBox) findViewById(id.regTermsChckBox);
    }

    public void returnSignInRegister() {
        Intent signinregisterScreen = new Intent(this, SignInRegister.class);
        startActivity(signinregisterScreen);
    }

//    public void registerValidation() {
//        registerBtn.setOnClickListener(view -> validation());
//    }

    public void validation() {



        /*
        EditText firstName, lastName, address, contact, username, password, confirmPassword;
        EditText age;

//        rootNode = FirebaseDatabase.getInstance();
//        reference = rootNode.getReference("");
//        //What reference from the database you want to get (like users accounts)...
//
//        reference.setValue("");

        //RadioButton Gender Validation
        String genderSelected = String.valueOf(gender.getCheckedRadioButtonId());
        if(genderSelected == "Male"){
            // Validation form "Male" result
        }else if(genderSelected == "Female"){
            // Validation form "Female" result
        }else{
            Toast.makeText(RegistrationForm.this, "Please select one of the gender.", Toast.LENGTH_LONG).show();
            return;
        }

        //RadioButton AccountType Validation
        String accountTypeSelected = String.valueOf(accountType.getCheckedRadioButtonId());
        if(accountTypeSelected == "Consumer"){
            // Validation form "Consumer" result
        }else if(accountTypeSelected == "Retailer"){
            // Validation form "Retailer" result
        }else{
            Toast.makeText(RegistrationForm.this, "Please select one of the account type.", Toast.LENGTH_LONG).show();
            return;
        }

        //Checkbox Terms Validation
        if(!terms.isChecked()){
            Toast.makeText(RegistrationForm.this, "Please accept terms of use and privacy policy", Toast.LENGTH_LONG).show();
            return;
        }else{
            // Validation form "Terms Accepted" and Button Registration Activated
        }

        Toast.makeText(RegistrationForm.this, "Successful", Toast.LENGTH_SHORT).show();
        */


        Intent signinregisterScreen = new Intent(this, SignInRegister.class);
        startActivity(signinregisterScreen);
    }

}