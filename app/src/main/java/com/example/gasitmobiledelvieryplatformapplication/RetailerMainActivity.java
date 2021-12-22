package com.example.gasitmobiledelvieryplatformapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.gasitmobiledelvieryplatformapplication.fragment.OrderStatusFragment;
import com.example.gasitmobiledelvieryplatformapplication.fragment.RetailerHomeFragment;
import com.example.gasitmobiledelvieryplatformapplication.fragment.SalesFragment;
import com.example.gasitmobiledelvieryplatformapplication.model.Order;
import com.example.gasitmobiledelvieryplatformapplication.model.ListItemRequestCallback;
import com.example.gasitmobiledelvieryplatformapplication.model.SimpleRequestCallback;
import com.example.gasitmobiledelvieryplatformapplication.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class RetailerMainActivity extends AppCompatActivity {

    // User Class...
    User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retailer_main);

        // Fragment Container from the Retailer Main Activity...
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                new RetailerHomeFragment()).commit();

        // Pending Notifications...
        countNotification(Order.STATUS_PENDING, findViewById(R.id.pendingNotificationTextView));
        countNotification(Order.STATUS_SHIPPING, findViewById(R.id.shippingNotificationTextView));
        countNotification(Order.STATUS_DELIVERED, findViewById(R.id.deliveredNotificationTextView));

        // Bottom Navigation...
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        // Bottom Navigation Fragments...
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment;

            String title;
            int id = item.getItemId();
            if (id == R.id.navHome) {
                title = getString(R.string.nav_home);
                selectedFragment = new RetailerHomeFragment();
            } else if (id == R.id.navPending) {
                title = getString(R.string.nav_order_pending);
                selectedFragment = new OrderStatusFragment(Order.STATUS_PENDING);
            } else if (id == R.id.navShipping) {
                title = getString(R.string.nav_order_shipping);
                selectedFragment = new OrderStatusFragment(Order.STATUS_SHIPPING);
            } else if (id == R.id.navDelivered) {
                title = getString(R.string.nav_order_delivered);
                selectedFragment = new OrderStatusFragment(Order.STATUS_DELIVERED);
            }
            //TODO: Add retailer navigation fragments.
//            else if (id == R.id.navWarehouse) {
//                title = "Warehouse";
//                selectedFragment = new WarehouseFragment();
//            }
            else if (id == R.id.navSales) {
                title = "Sales";
                selectedFragment = new SalesFragment();
            } else return false;

            // Fragment Title Text View...
            ((TextView) findViewById(R.id.fragmentTitleTextView)).setText(title);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                    selectedFragment).commit();

            return true;
        });

        // Sign Out Button...
        Button signOutButton = findViewById(R.id.signOutBtn);
        signOutButton.setOnClickListener(v -> goToSignIn());
    }

    // Notification Counter Status...
    public void recountNotifications() {
        countNotification(Order.STATUS_PENDING, findViewById(R.id.pendingNotificationTextView));
        countNotification(Order.STATUS_SHIPPING, findViewById(R.id.shippingNotificationTextView));
        countNotification(Order.STATUS_DELIVERED, findViewById(R.id.deliveredNotificationTextView));
    }

    // Notification Counter...
    private void countNotification(String status, TextView notificationTextView) {
        new Order(status).readAll(new ListItemRequestCallback<Order>() {
            @Override
            public void onSuccess(List<Order> orderList) {
                int size = orderList == null ? 0 : orderList.size();
                notificationTextView.setVisibility(size == 0 ? View.GONE : View.VISIBLE);
                notificationTextView.setText(String.valueOf(size));
            }

            @Override
            public void onFailure(String error) {
                // Do nothing.
            }
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