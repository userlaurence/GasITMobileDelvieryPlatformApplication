package com.example.gasitmobiledelvieryplatformapplication.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
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

public class Gasoline implements Parcelable {
    private static final String FIREBASE_NODE = "Gasoline";
    private static final String FIREBASE_STORAGE = "Gasoline Images";
    private final FirebaseStorage firebaseStorageReference = FirebaseStorage
            .getInstance("gs://gasit-a1713.appspot.com");
    private final DatabaseReference databaseReference = FirebaseDatabase
            .getInstance("https://gasit-a1713-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference(FIREBASE_NODE);
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

    /**
     * For reducing stock after order.
     */
    public Gasoline(String uid, int newStock) {
        this.uid = uid;
        this.stock = newStock;

        gasolineMap = new HashMap<>();
        gasolineMap.put("stock", stock);
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

    protected Gasoline(Parcel in) {
        uid = in.readString();
        imageUrl = in.readString();
        name = in.readString();
        weight = in.readFloat();
        price = in.readInt();
        stock = in.readInt();

        gasolineMap = new HashMap<>();
        gasolineMap.put("imageUrl", imageUrl);
        gasolineMap.put("name", name);
        gasolineMap.put("weight", weight);
        gasolineMap.put("price", price);
        gasolineMap.put("stock", stock);
    }

    public static final Creator<Gasoline> CREATOR = new Creator<Gasoline>() {
        @Override
        public Gasoline createFromParcel(Parcel in) {
            return new Gasoline(in);
        }

        @Override
        public Gasoline[] newArray(int size) {
            return new Gasoline[size];
        }
    };

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

    public void readOne(String uid, ItemRequestCallback<Gasoline> callback) {
        databaseReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Gasoline gasoline;

                // Guard to secure that child node exist.
                // And assign the child node and check if gasoline is not null.
                if (snapshot.getValue() == null ||
                        (gasoline = snapshot.getValue(Gasoline.class)) == null) {
                    callback.onSuccess(null);
                    return;
                }

                gasoline.setUid(snapshot.getKey());
                callback.onSuccess(gasoline);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
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
            if (error == null) callback.onSuccess(getName() + " is successfully deleted.");
            else callback.onFailure(error.getMessage());
        });
    }

    public void uploadImage(Uri imageUri, SimpleRequestCallback callback) {
        if (uid == null) {
            // Call generateUid() first before calling uploadImage().
            callback.onFailure("Failed to upload image to the server. Please try again.");
            Log.d("FirebaseError",
                    "The UID must be provided. Call generateUid() before uploading image.");
            return;
        }

        StorageReference storageReference = firebaseStorageReference
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

    public void generateUid() {
        setUid(databaseReference.push().getKey());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(imageUrl);
        dest.writeString(name);
        dest.writeFloat(weight);
        dest.writeInt(price);
        dest.writeInt(stock);
    }
}
