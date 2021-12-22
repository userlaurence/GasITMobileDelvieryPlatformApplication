package com.example.gasitmobiledelvieryplatformapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gasitmobiledelvieryplatformapplication.R;
import com.example.gasitmobiledelvieryplatformapplication.model.Order;
import com.example.gasitmobiledelvieryplatformapplication.util.DateUtil;
import com.example.gasitmobiledelvieryplatformapplication.util.Formatter;

import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private final List<Order> orderArrayList;
    private OnClickListener listener;

    public interface OnClickListener {
        void onClick(Order order);
    }

    // Order Adapter for Listing Order in an ArrayList...
    public OrderAdapter(List<Order> orderArrayList) {
        this.orderArrayList = orderArrayList;
    }

    // Order Card View Holder for Viewing Items from Customer...
    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_order, parent, false);
        return new OrderViewHolder(view);
    }

    // Getting Positions from the List of each Item...
    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.onBind(orderArrayList.get(position), listener);
    }

    // Counting the Number of Lists of all Items...
    @Override
    public int getItemCount() {
        return orderArrayList.size();
    }

    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    // Order View Holder for Recycler Viewer on XML Binding...
    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderDateTextView, orderNameTextView, orderComputationTextView, orderTotalTextView,
                customerPhoneNumberTextView, customerAddressTextView;
        Button changeStatusButton;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderDateTextView = itemView.findViewById(R.id.orderDateTextView);
            orderNameTextView = itemView.findViewById(R.id.orderNameTextView);
            orderComputationTextView = itemView.findViewById(R.id.orderComputationTextView);
            orderTotalTextView = itemView.findViewById(R.id.orderTotalTextView);
            customerPhoneNumberTextView = itemView.findViewById(R.id.customerPhoneNumberTextView);
            customerAddressTextView = itemView.findViewById(R.id.customerAddressTextView);
            changeStatusButton = itemView.findViewById(R.id.changeStatusButton);
        }

        public void onBind(Order order, OnClickListener listener) {
            String date = DateUtil.toDateTime(order.getDate()).toString();
            String computation = String.format(Locale.getDefault(), "₱%.2f x %dpcs = ",
                    Formatter.formatMoneyForDisplay(order.getPrice()), order.getQuantity());

            orderDateTextView.setText(date);
            orderNameTextView.setText(order.getGasoline());
            orderComputationTextView.setText(computation);
            orderTotalTextView.setText(Formatter.formatMoneyWithPesoSign(order.getSubtotal()));
            customerPhoneNumberTextView.setText(order.getPhoneNumber());
            customerAddressTextView.setText(order.getAddress());
            changeStatusButton.setOnClickListener(v -> listener.onClick(order));
        }
    }
}
