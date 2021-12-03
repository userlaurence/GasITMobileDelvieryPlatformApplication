package com.example.gasitmobiledelvieryplatformapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.gasitmobiledelvieryplatformapplication.fragment.LocationFragment;
import com.example.gasitmobiledelvieryplatformapplication.fragment.OrderFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CustomerMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                new OrderFragment()).commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment;

            int id = item.getItemId();
            if (id == R.id.navOrder)    selectedFragment = new OrderFragment();
            else if (id == R.id.navMap) selectedFragment = new LocationFragment();
            else return false;

            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                    selectedFragment).commit();

            return true;
        });
    }

}