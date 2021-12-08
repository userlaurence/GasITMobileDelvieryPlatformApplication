package com.example.gasitmobiledelvieryplatformapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gasitmobiledelvieryplatformapplication.model.SimpleRequestCallback;
import com.example.gasitmobiledelvieryplatformapplication.model.User;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    // Handler for Application Splash Screen Opening...
    Handler splashScreen = new Handler();

    // User Authentication Class...
    User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // sdk.dir=F\:\\AndroidStudio\\Sdk
        // 3Secs. Delay Time Before Running Next Activity...
        splashScreen.postDelayed(new Runnable() {

            @Override
            public void run() {
                // Signing Out all Active Current Account First...
                user.signOut();

                // Account Authentication including Toast Messages and Class Activity Process...
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
        }, 3000);
        // 3Secs. Delay Time Before Running Next Activity...

    }
}