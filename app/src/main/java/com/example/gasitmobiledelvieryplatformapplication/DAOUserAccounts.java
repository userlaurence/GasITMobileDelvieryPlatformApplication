package com.example.gasitmobiledelvieryplatformapplication;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DAOUserAccounts {
    private DatabaseReference databaseReference;

    public DAOUserAccounts() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(UserAccounts.class.getSimpleName());
    }

    public Task<Void> add(UserAccounts users) {
        return databaseReference.push().setValue(users);
    }

}
