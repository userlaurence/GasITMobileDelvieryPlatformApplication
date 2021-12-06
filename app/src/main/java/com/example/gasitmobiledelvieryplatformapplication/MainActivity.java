package com.example.gasitmobiledelvieryplatformapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gasitmobiledelvieryplatformapplication.models.SimpleRequestCallback;
import com.example.gasitmobiledelvieryplatformapplication.models.User;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        User user = new User();
        // TODO: Remove this once all of sign in options are tested working.
        user.signOut();
        if (user.checkUserSession()) {
            user.readAuthenticatedUser(new SimpleRequestCallback() {
                @Override
                public void onSuccess(String message) {
                    if (user.isAdmin())
                        startActivity(new Intent(MainActivity.this, RetailerMainActivity.class));
                    else
                        startActivity(new Intent(MainActivity.this, CustomerMainActivity.class));
                }

                @Override
                public void onFailure(String error) {
                    Toasty.error(MainActivity.this, error, Toasty.LENGTH_SHORT).show();
                }
            });
        } else {
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
        }
    }
}