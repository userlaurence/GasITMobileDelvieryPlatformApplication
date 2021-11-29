package com.example.gasitmobiledelvieryplatformapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gasitmobiledelvieryplatformapplication.R.id;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignInRegister extends AppCompatActivity {

    FirebaseDatabase rootNode;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_register);

        EditText signUsername = findViewById(id.signUsername);
        EditText signPassword = findViewById(id.signPassword);

        Button signRegister = findViewById(id.signRegisterBtn);
        signRegister.setOnClickListener(v -> openregisterScreen());

        Button signLogin = findViewById(id.signLoginBtn);
        signLogin.setOnClickListener(v -> openloginScreen());

        ImageButton signFB = findViewById(id.signFbBtn);
        signFB.setOnClickListener(v -> openfacebookScreen());

        ImageButton signGoogle = findViewById(id.signGoogleBtn);
        signGoogle.setOnClickListener(v -> opengoogleScreen());
    }

    public void openloginScreen() {
        //insert here for verification of account in the database...
        //conditional statements...

        /*
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("");
        //What reference from the database you want to get (like users accounts)...

        reference.setValue("");
        */

        Intent logInAccess = new Intent(this, CustomerInterfaceTab.class);
        startActivity(logInAccess);
    }

    public void openfacebookScreen() {
        //Intent facebookScreen = new Intent(this, facebook.Class);
        //startActivity(facebookScreen);

        //insert here facebook activity...
    }

    public void opengoogleScreen() {
        //Intent facebookScreen = new Intent(this, facebook.Class);
        //startActivity(facebookScreen);

        //insert here google activity...
    }

    public void openregisterScreen() {
        Intent registerScreen = new Intent(this, RegistrationForm.class);
        startActivity(registerScreen);
    }
}