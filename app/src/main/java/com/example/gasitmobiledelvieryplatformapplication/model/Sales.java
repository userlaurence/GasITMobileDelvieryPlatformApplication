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

public class Sales {
    private static final String FIREBASE_NODE = "Sales";
    private final DatabaseReference databaseReference = FirebaseDatabase
            .getInstance("https://gasit-a1713-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference(FIREBASE_NODE);

    private final HashMap<String, Object> salesMap;

    private String uid;
    private String gasolineUid; // For querying Sales (it requires gasoline uid for synced stocks count).
    private String gasoline;
    private int quantitySold;
    private int price;
    private int sales;
    private long timestamp;
    private int index; // This is for the SalesTableLayout.

    public Sales() {
        salesMap = new HashMap<>();
    }

    public Sales(long timestamp) {
        this.timestamp = timestamp;
        salesMap = new HashMap<>();
        salesMap.put("timestamp", timestamp);
    }

    public Sales(String gasolineUid, String gasoline, int quantitySold, int price) {
        this.gasolineUid = gasolineUid; // Don't add to salesMap (does not need to appear on the node).
        this.gasoline = gasoline;
        this.quantitySold = quantitySold;
        this.price = price;
        this.sales = quantitySold * price;
        this.timestamp = DateUtil.now().toMills();

        salesMap = new HashMap<>();
        generateUid();
        salesMap.put("gasolineUid", gasolineUid);
        salesMap.put("gasoline", gasoline);
        salesMap.put("quantitySold", quantitySold);
        salesMap.put("price", price);
        salesMap.put("sales", sales);
        salesMap.put("timestamp", timestamp);
    }

    public String getUid() {   return uid;    }
    public String getGasolineUid() {   return gasolineUid;    }
    public String getGasoline() {   return gasoline;    }
    public int getQuantitySold() {   return quantitySold;    }
    public int getPrice() {   return price;    }
    public int getSales() {   return sales;    }
    public long getTimestamp() {   return timestamp;    }

    public void setUid(String uid) {
        this.uid = uid;
        salesMap.put("uid", uid);
    }

    public void setGasolineUid(String gasolineUid) {
        this.gasolineUid = gasolineUid;
        salesMap.put("gasolineUid", gasolineUid);
    }

    public void setGasoline(String gasoline) {
        this.gasoline = gasoline;
        salesMap.put("gasoline", gasoline);
    }

    public void setQuantitySold(int quantitySold) {
        this.quantitySold += quantitySold;
        salesMap.put("quantitySold", quantitySold);
    }

    public void setPrice(int price) {
        this.price = price;
        salesMap.put("price", price);
    }

    public void setSales(int sales) {
        this.sales += sales;
        salesMap.put("sales", sales);
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        salesMap.put("timestamp", timestamp);
    }

    public int getIndex() { return index;   }
    public void setIndex(int index) {   this.index = index; }

    public void readAll(long upTo, ListItemRequestCallback<Sales> callback) {
        databaseReference.orderByChild("timestamp")
                .startAt(timestamp)
                .endAt(upTo)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() == null) {
                            callback.onSuccess(null);
                            return;
                        }

                        List<Sales> salesList = new ArrayList<>();
                        snapshot.getChildren().forEach(postSnapshot -> {
                            Sales sales = postSnapshot.getValue(Sales.class);
                            if (sales != null) {
                                sales.setUid(postSnapshot.getKey());
                                salesList.add(sales);
                            }
                        });
                        callback.onSuccess(salesList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure(error.getMessage());
                    }
                });
    }

    public void write(SimpleRequestCallback callback) {
        if (getUid() == null) callback.onFailure("UID is missing.");

        databaseReference.child(getUid()).updateChildren(salesMap, (error, ref) -> {
            if (error == null) callback.onSuccess(getUid());
            else callback.onFailure(error.toException().getMessage());
        });
    }

    public void delete(SimpleRequestCallback callback) {
        if (uid == null) callback.onFailure("UID is missing.");

        databaseReference.child(getUid()).removeValue((error, ref) -> {
            if (error == null) callback.onSuccess(getGasoline() + " is successfully deleted.");
            else callback.onFailure(error.getMessage());
        });
    }

    public void generateUid() {
        setUid(databaseReference.push().getKey());
    }
}
