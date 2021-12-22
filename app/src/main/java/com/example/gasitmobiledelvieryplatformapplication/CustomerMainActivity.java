package com.example.gasitmobiledelvieryplatformapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.gasitmobiledelvieryplatformapplication.fragment.LocationFragment;
import com.example.gasitmobiledelvieryplatformapplication.fragment.CustomerHomeFragment;
import com.example.gasitmobiledelvieryplatformapplication.model.SimpleRequestCallback;
import com.example.gasitmobiledelvieryplatformapplication.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import es.dmoral.toasty.Toasty;

public class CustomerMainActivity extends AppCompatActivity {

    // User Class...
    User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);

        // Sign Out Button...
        Button signOutButton = findViewById(R.id.signOutBtn);
        signOutButton.setOnClickListener(v -> goToSignIn());

        // Fragment Container...
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                new CustomerHomeFragment()).commit();

        // Navigation of Customer Interface...
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment;

            /**
             * Getting Active Navigation ID(s) for Customer Interface Fragment...
             **/
            int id = item.getItemId();
            if (id == R.id.navOrder) selectedFragment = new CustomerHomeFragment();
            else if (id == R.id.navMap) selectedFragment = new LocationFragment();
            else return false;

            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                    selectedFragment).commit();
            /**
             * Getting Active Navigation ID(s) for Customer Interface Fragment...
             **/

            return true;
        });
    }

    // Signing Out Active Account Session...
    private void goToSignIn() {
        user.signOutCurrent(new SimpleRequestCallback() {
            @Override
            public void onSuccess(String message) {
                onRequestSuccess(message);
            }

            @Override
            public void onFailure(String error) {
                onRequestError(error);
            }
        });

        startActivity(new Intent(this, MainActivity.class));
    }

    // Toast Successful Message after Signing Out...
    private void onRequestSuccess(String message) {
        Toasty.success(this, message, Toasty.LENGTH_LONG).show();
    }

    // Toast Error/Failed Message when Signing Out...
    private void onRequestError(String error) {
        Toasty.error(this, error, Toasty.LENGTH_SHORT).show();
    }
}