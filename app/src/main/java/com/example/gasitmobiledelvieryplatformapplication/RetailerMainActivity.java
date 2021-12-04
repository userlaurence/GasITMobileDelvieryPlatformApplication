package com.example.gasitmobiledelvieryplatformapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.gasitmobiledelvieryplatformapplication.fragment.HomeFragment;
import com.example.gasitmobiledelvieryplatformapplication.fragment.LocationFragment;
import com.example.gasitmobiledelvieryplatformapplication.fragment.OrderFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class RetailerMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                new HomeFragment()).commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment;

            int id = item.getItemId();
            //TODO: Add retailer navigation fragments.
            if (id == R.id.navHome)             selectedFragment = new HomeFragment();
//            else if (id == R.id.navOrder)       selectedFragment = new OrderNotificationFragment();
//            else if (id == R.id.navWarehouse)   selectedFragment = new WarehouseFragment();
//            else if (id == R.id.navSales)       selectedFragment = new SalesFragment();
            else return false;

            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                    selectedFragment).commit();

            return true;
        });
    }

}