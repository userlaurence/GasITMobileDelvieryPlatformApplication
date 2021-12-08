package com.example.gasitmobiledelvieryplatformapplication.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gasitmobiledelvieryplatformapplication.R;
import com.example.gasitmobiledelvieryplatformapplication.RetailerMainActivity;
import com.example.gasitmobiledelvieryplatformapplication.adapter.OrderAdapter;
import com.example.gasitmobiledelvieryplatformapplication.model.ListItemRequestCallback;
import com.example.gasitmobiledelvieryplatformapplication.model.Order;
import com.example.gasitmobiledelvieryplatformapplication.model.Sales;
import com.example.gasitmobiledelvieryplatformapplication.model.SimpleRequestCallback;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class OrderStatusFragment extends Fragment {
    private RecyclerView orderStatusRecyclerView;
    private ProgressBar progressBar;

    private final String orderStatus;

    public OrderStatusFragment(String orderStatus) {
        super(R.layout.fragment_order_status);
        this.orderStatus = orderStatus;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_order_status, container, false);

        progressBar = rootView.findViewById(R.id.progressBar);

        orderStatusRecyclerView = rootView.findViewById(R.id.orderStatusRecyclerView);
        orderStatusRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        orderStatusRecyclerView.setItemAnimator(new DefaultItemAnimator());

        initData();
        return rootView;
    }

    private void initData() {
        progressBar.setVisibility(View.VISIBLE);

        new Order(this.orderStatus).readAll(new ListItemRequestCallback<Order>() {
            @Override
            public void onSuccess(List<Order> itemList) {
                progressBar.setVisibility(View.GONE);
                if (itemList != null) onDataInitialized(itemList);
            }

            @Override
            public void onFailure(String error) {
                onRequestError(error);
            }
        });
    }

    private void onDataInitialized(List<Order> orderList) {
        OrderAdapter orderAdapter = new OrderAdapter(orderList);
        orderAdapter.setOnClickListener(this::changeStatus);
        orderStatusRecyclerView.setAdapter(orderAdapter);
    }

    private void changeStatus(Order order) {
        order.delete(new SimpleRequestCallback() {
            @Override
            public void onSuccess(String message) {
                if (order.getStatus().equals(Order.STATUS_DELIVERED)) {
                    new Sales(order.getGasolineUid(), order.getGasoline(), order.getQuantity(), order.getPrice())
                            .write(changeStatusCallback("Transaction done!"));
                    initData();
                    return;
                }

                if (order.getStatus().equals(Order.STATUS_PENDING))
                    order.setStatus(Order.STATUS_SHIPPING);
                else if (order.getStatus().equals(Order.STATUS_SHIPPING))
                    order.setStatus(Order.STATUS_DELIVERED);

                String transitionMessage = "Order of '" + order.getGasoline() + "' is now " + order.getStatus();
                order.write(changeStatusCallback(transitionMessage));
            }

            @Override
            public void onFailure(String error) {
                onRequestError(error);
            }
        });
    }

    private SimpleRequestCallback changeStatusCallback(String transitionMessage) {
        return new SimpleRequestCallback() {
            @Override
            public void onSuccess(String message) {
                onRequestSuccess(transitionMessage);
                initData();
                if (getActivity() != null)
                    ((RetailerMainActivity) getActivity()).recountNotifications();
            }

            @Override
            public void onFailure(String error) {
                onRequestError(error);
            }
        };
    }

    private void onRequestSuccess(String message) {
        progressBar.setVisibility(View.GONE);

        if (getActivity() == null) return;
        Toasty.success(getActivity(), message, Toasty.LENGTH_SHORT).show();
    }

    private void onRequestError(String error) {
        progressBar.setVisibility(View.GONE);

        if (getActivity() == null) return;
        Toasty.error(getActivity(), error, Toasty.LENGTH_SHORT).show();
    }
}