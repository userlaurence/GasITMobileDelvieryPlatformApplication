package com.example.gasitmobiledelvieryplatformapplication.models;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class Gasoline {
    private static final String FIREBASE_NODE = "Gasoline";
    private static final String FIREBASE_STORAGE = "Gasoline Images";
    private final DatabaseReference databaseReference =
            FirebaseDatabase.getInstance().getReference(FIREBASE_NODE);
    private final Map<String, Object> gasolineMap;

    private String uid;
    private String imageUrl;
    private String name;
    private float weight;
    private int price;
    private int stock;

    public Gasoline() {
        gasolineMap = new HashMap<>();
    }

    public Gasoline(String name, float weight, int price, int stock) {
        /* Image URL will be passed using setter since it is using an async call. */
        this.name = name;
        this.weight = weight;
        this.price = price;
        this.stock = stock;

        gasolineMap = new HashMap<>();
        gasolineMap.put("name", name);
        gasolineMap.put("weight", weight);
        gasolineMap.put("price", price);
        gasolineMap.put("stock", stock);
    }

    public String getUid() {   return uid;    }
    public String getImageUrl() {   return imageUrl;    }
    public String getName() {   return name;    }
    public float getWeight() {  return weight;  }
    public int getPrice() { return price;   }
    public int getStock() { return stock;   }
    public void setUid(String uid) {  this.uid = uid;   }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        gasolineMap.put("imageUrl", imageUrl);
    }

    public void setName(String name) {
        this.name = name;
        gasolineMap.put("name", name);
    }
    public void setWeight(float weight) {
        this.weight = weight;
        gasolineMap.put("weight", weight);
    }
    public void setPrice(int price) {
        this.price = price;
        gasolineMap.put("price", price);
    }
    public void setStock(int stock) {
        this.stock = stock;
        gasolineMap.put("stock", stock);
    }

    public void readAll(ListItemRequestCallback<Gasoline> callback) {
        databaseReference.orderByValue().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {
                    callback.onSuccess(null);
                    return;
                }

                List<Gasoline> gasolineList = new ArrayList<>();
                snapshot.getChildren().forEach(postSnapshot -> {
                    Gasoline gasoline = postSnapshot.getValue(Gasoline.class);
                    if (gasoline != null) {
                        gasoline.setUid(postSnapshot.getKey());
                        gasolineList.add(gasoline);
                    }
                });
                callback.onSuccess(gasolineList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.toException().getMessage());
            }
        });
    }

    public void write(SimpleRequestCallback callback) {
        if (uid == null) generateUid();

        databaseReference.child(this.uid).updateChildren(gasolineMap, (error, ref) -> {
            if (error == null) callback.onSuccess(this.uid);
            else callback.onFailure(error.toException().getMessage());
        });
    }

    public void delete(SimpleRequestCallback callback) {
        if (uid == null) callback.onFailure("UID is missing.");

        databaseReference.child(uid).removeValue((error, ref) -> {
            if (error == null) callback.onSuccess("Gasoline is successfully deleted.");
            else callback.onFailure(error.getMessage());
        });
    }

    public void uploadImage(Uri imageUri, SimpleRequestCallback callback) {
        if (uid == null) {
            callback.onFailure("Failed to upload image to the server. Please try again.");
            Log.d("FirebaseError",
                    "The UID must be provided. Call generateUid() before uploading image.");
            return;
        }

        StorageReference storageReference = FirebaseStorage.getInstance()
                .getReference(FIREBASE_STORAGE + "/" + this.uid);

        storageReference.putFile(imageUri)
                .continueWithTask(task -> storageReference.getDownloadUrl())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null)
                        callback.onSuccess(task.getResult().toString());
                    else if (task.getException() != null)
                        callback.onFailure(task.getException().getMessage());
                });
    }

    private void generateUid() {
        setUid(databaseReference.push().getKey());
    }
}
