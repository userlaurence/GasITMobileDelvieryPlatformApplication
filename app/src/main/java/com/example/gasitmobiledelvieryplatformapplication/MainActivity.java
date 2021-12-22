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

    // User Class...
    User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Signing Out all Active Current Account First...
        user.signOut();

        /**
         * 5secs. Delay Time Before Running Next Activity...
         **/
        splashScreen.postDelayed(new Runnable() {

            @Override
            public void run() {
                // Account Active Session Check...
                if (user.checkUserSession()) {
                    // Verify Active Account...
                    user.readAuthenticatedUser(new SimpleRequestCallback() {
                        // Verify if Current Active Account is an Retailer(Admin) or Customer...
                        @Override
                        public void onSuccess(String message) {
                            if (user.isAdmin())
                                startActivity(new Intent(MainActivity.this, RetailerMainActivity.class));
                            else
                                startActivity(new Intent(MainActivity.this, CustomerMainActivity.class));
                        }

                        // Else, Shows Error Account Authentication (Neither a Retailer or Customer)...
                        @Override
                        public void onFailure(String error) {
                            Toasty.error(MainActivity.this, error, Toasty.LENGTH_SHORT).show();
                        }
                    });

                    // Else, Shows Sign In Process...
                } else {
                    startActivity(new Intent(MainActivity.this, SignInActivity.class));
                }

            }
        }, 5000);
        /**
         * 5secs. Delay Time Before Running Next Activity...
         **/

    }
}