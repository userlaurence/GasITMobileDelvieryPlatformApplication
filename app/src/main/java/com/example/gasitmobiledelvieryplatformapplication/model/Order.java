package com.example.gasitmobiledelvieryplatformapplication.model;

import androidx.annotation.NonNull;

import com.example.gasitmobiledelvieryplatformapplication.util.DateUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Order {
    private static final String FIREBASE_NODE = "Order";
    private final DatabaseReference databaseReference = FirebaseDatabase
            .getInstance("https://gasit-a1713-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference(FIREBASE_NODE);

    private final HashMap<String, Object> orderMap;
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_SHIPPING = "shipping";
    public static final String STATUS_DELIVERED = "delivered";

    private String status; // Don't put this on the orderMap since this acts as the parent node.
    private String uid;
    private String gasolineUid; // For querying Sales (it requires gasoline uid for synced stocks count).
    private String gasoline;
    private int price;
    private int quantity;
    private int subtotal;
    private String phoneNumber;
    private String address;
    private long date;

    public Order() {
        orderMap = new HashMap<>();
    }

    public Order(String status) {
        this.status = status;
        orderMap = new HashMap<>();
    }

    public Order(String status, int price, int quantity, String gasolineUid,
                 String gasoline, String phoneNumber, String address) {
        this.status = status;
        this.price = price;
        this.quantity = quantity;
        this.subtotal = quantity * price;
        this.gasolineUid = gasolineUid;
        this.gasoline = gasoline;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.date = DateUtil.now().toMills();

        orderMap = new HashMap<>();
        orderMap.put("price", price);
        orderMap.put("quantity", quantity);
        orderMap.put("subtotal", subtotal);
        orderMap.put("gasolineUid", gasolineUid);
        orderMap.put("gasoline", gasoline);
        orderMap.put("phoneNumber", phoneNumber);
        orderMap.put("address", address);
        orderMap.put("date", date);
    }

    public String getStatus() { return status;  }
    public String getUid() {   return uid;    }
    public int getPrice() {   return price;    }
    public int getQuantity() {   return quantity;    }
    public int getSubtotal() {   return subtotal;    }
    public String getGasolineUid() {   return gasolineUid;    }
    public String getGasoline() {   return gasoline;    }
    public String getPhoneNumber() {   return phoneNumber;    }
    public String getAddress() {   return address;    }
    public long getDate() {   return date;    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUid(String uid) {
        this.uid = uid;
        orderMap.put("uid", uid);
    }

    public void setGasolineUid(String gasolineUid) {
        this.gasolineUid = gasolineUid;
        orderMap.put("gasolineUid", gasolineUid);
    }

    public void setGasoline(String gasoline) {
        this.gasoline = gasoline;
        orderMap.put("gasoline", gasoline);
    }

    public void setPrice(int price) {
        this.price = price;
        orderMap.put("price", price);
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        orderMap.put("quantity", quantity);
    }

    public void setSubtotal(int subtotal) {
        this.subtotal = subtotal;
        orderMap.put("subtotal", subtotal);
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        orderMap.put("phoneNumber", phoneNumber);
    }

    public void setAddress(String address) {
        this.address = address;
        orderMap.put("address", address);
    }

    public void setDate(long date) {
        this.date = date;
        orderMap.put("date", date);
    }

    public void readAll(ListItemRequestCallback<Order> callback) {
        databaseReference.child(getStatus()).orderByValue().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {
                    callback.onSuccess(null);
                    return;
                }

                List<Order> orderList = new ArrayList<>();
                snapshot.getChildren().forEach(postSnapshot -> {
                    Order order = postSnapshot.getValue(Order.class);
                    if (order != null) {
                        order.setUid(postSnapshot.getKey());
                        order.setStatus(getStatus() /* Status of Order.*/);
                        setPrice(order.price);
                        setQuantity(order.quantity);
                        setSubtotal(order.subtotal);
                        setGasolineUid(order.gasolineUid);
                        setGasoline(order.gasoline);
                        setPhoneNumber(order.phoneNumber);
                        setAddress(order.address);
                        setDate(order.date);
                        orderList.add(order);
                    }
                });
                callback.onSuccess(orderList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.toException().getMessage());
            }
        });
    }

    public void write(SimpleRequestCallback callback) {
        if (getUid() == null) generateUid();

        databaseReference.child(getStatus() + "/" + getUid()).updateChildren(orderMap, (error, ref) -> {
            if (error == null) callback.onSuccess(getUid());
            else callback.onFailure(error.toException().getMessage());
        });
    }

    public void delete(SimpleRequestCallback callback) {
        if (uid == null) callback.onFailure("UID is missing.");

        databaseReference.child(getStatus() + "/" + getUid()).removeValue((error, ref) -> {
            if (error == null) callback.onSuccess(getGasoline() + " is successfully deleted.");
            else callback.onFailure(error.getMessage());
        });
    }

    public void generateUid() {
        setUid(databaseReference.push().getKey());
    }
}
