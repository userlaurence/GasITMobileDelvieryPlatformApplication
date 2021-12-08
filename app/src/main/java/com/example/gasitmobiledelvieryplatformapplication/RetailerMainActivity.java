package com.example.gasitmobiledelvieryplatformapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.gasitmobiledelvieryplatformapplication.fragment.OrderStatusFragment;
import com.example.gasitmobiledelvieryplatformapplication.fragment.RetailerHomeFragment;
import com.example.gasitmobiledelvieryplatformapplication.fragment.SalesFragment;
import com.example.gasitmobiledelvieryplatformapplication.model.Order;
import com.example.gasitmobiledelvieryplatformapplication.model.ListItemRequestCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class RetailerMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retailer_main);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                new RetailerHomeFragment()).commit();

        countNotification(Order.STATUS_PENDING, findViewById(R.id.pendingNotificationTextView));
        countNotification(Order.STATUS_SHIPPING, findViewById(R.id.shippingNotificationTextView));
        countNotification(Order.STATUS_DELIVERED, findViewById(R.id.deliveredNotificationTextView));

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
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
            }
            else return false;

            ((TextView) findViewById(R.id.fragmentTitleTextView)).setText(title);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                    selectedFragment).commit();

            return true;
        });

    }

    public void recountNotifications() {
        countNotification(Order.STATUS_PENDING, findViewById(R.id.pendingNotificationTextView));
        countNotification(Order.STATUS_SHIPPING, findViewById(R.id.shippingNotificationTextView));
        countNotification(Order.STATUS_DELIVERED, findViewById(R.id.deliveredNotificationTextView));
    }

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
}