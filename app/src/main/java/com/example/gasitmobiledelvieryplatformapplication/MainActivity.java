package com.example.gasitmobiledelvieryplatformapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Kotlin for hiding the Action Bar
        //supportActionBar?.hide()

        // TODO: Uncomment SignIn after supporting FireAuth.
        Intent intent = new Intent(MainActivity.this, SignInRegister.class);
//        Intent intent = new Intent(MainActivity.this, RetailerMainActivity.class);

        //Incrementing Timer for Splash Screen
        //Splash Screen (3secs.)
        int SPLASH_SCREEN = 3000;
        new Handler().postDelayed(() -> {
            startActivity(intent);
            finish();
        }, SPLASH_SCREEN);
    }
}