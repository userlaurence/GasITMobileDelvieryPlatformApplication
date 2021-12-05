package com.example.gasitmobiledelvieryplatformapplication.models;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class User {
    private static final String FIREBASE_NODE = "User";
    private final DatabaseReference databaseReference = FirebaseDatabase
            .getInstance("https://gasit-a1713-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference(FIREBASE_NODE);
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser;
    private final HashMap<String, Object> userMap;

    private String uid;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String gender;
    private int age;
    private String email;
    private String password;

    public User() {
        userMap = new HashMap<>();
        checkUserSession();
    }

    /**
     * Used for Sign in.
     */
    public User(String email, String password) {
        this.email = email;
        this.password = password;

        userMap = new HashMap<>();
        userMap.put("email", email);
        userMap.put("password", password);
    }

    /**
     * Used for Signup.
     */
    public User(String firstName, String lastName, String phoneNumber, String address,
                String gender, int age, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.address = address;

        this.gender = gender;
        this.age = age;

        this.email = email;
        this.password = password;

        userMap = new HashMap<>();
        userMap.put("firstName", firstName);
        userMap.put("lastName", lastName);
        userMap.put("phoneNumber", phoneNumber);
        userMap.put("address", address);

        userMap.put("gender", gender);
        userMap.put("age", age);

        userMap.put("email", email);
        userMap.put("password", password);
    }

    public String getUid() {   return uid;    }
    public String getFirstName() {   return firstName;    }
    public String getLastName() {   return lastName;    }
    public String getPhoneNumber() {    return phoneNumber; }
    public String getAddress() {  return address;   }
    public String getGender() {  return gender;   }
    public int getAge() {  return age;   }
    public String getEmail() {  return email;   }
    public String getPassword() {   return password;    }

    public void setEmail(String email) {
        this.email = email;
        userMap.put("email", email);
    }

    public void setPassword(String password) {
        this.password = password;
        userMap.put("password", password);
    }

    public void signIn(String email, String password, SimpleRequestCallback callback) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    /*
                     * Different from this.firebaseUser since currentUser is the newly created
                     * user and may not be the same with this.firebaseUser if an error occurs.
                     */
                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();

                    if (!task.isSuccessful() || currentUser == null) {
                        callback.onFailure("Failed to login! Please check your credentials.");
                        return;
                    }

                    if (!currentUser.isEmailVerified()) {
                        currentUser.sendEmailVerification();
                        callback.onFailure("Check your email to verify your account. Then try again.");
                        return;
                    }

                    callback.onSuccess(currentUser.getUid());
                });
    }

    // TODO: Support Facebook and Gmail logins.
    // TODO: Logout.
    public void signUp(SimpleRequestCallback callback) {
        if (getEmail() == null || getPassword() == null)
            return; // Make sure to use constructor for signup.

        firebaseAuth.createUserWithEmailAndPassword(getEmail(), getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        setPassword(null); /* So that it won't be seen in write(). */
                        callback.onSuccess(getFirstName() + " successfully created. " +
                                "Please check your email to verify your account.");
                    }
                    else if (task.getException() != null)
                        callback.onFailure(task.getException().getMessage());
                });
    }

    public void write(SimpleRequestCallback callback) {
        if (!checkUserSession()) {
            /* Must call signup first before calling write to create the uid. */
            callback.onFailure("UID is missing.");
            return;
        }

        if (getPassword() != null) {
            firebaseUser.updatePassword(getPassword());
            // Prevents from write "password" in User node.
            userMap.remove("password");
        }

        if (getEmail() != null)
            firebaseUser.updateEmail(getEmail());

        databaseReference.child(getUid()).updateChildren(userMap, (error, ref) -> {
            if (error == null) callback.onSuccess(getUid());
            else callback.onFailure(error.toException().getMessage());
        });
    }

    public void resetPassword(SimpleRequestCallback callback) {
        if (getEmail() == null)
            callback.onFailure("Email has not been set. Please try again.");

        firebaseAuth.sendPasswordResetEmail(getEmail())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        callback.onSuccess("Check your email for the reset password link.");
                    else if (task.getException() != null)
                        callback.onFailure(task.getException().getMessage());
                });
    }

    private boolean checkUserSession() {
        if (firebaseAuth.getCurrentUser() == null)
            return false;

        this.firebaseUser = firebaseAuth.getCurrentUser();
        this.uid = firebaseUser.getUid();
        return true;
    }

    public void delete(SimpleRequestCallback callback) {
        if (uid == null) callback.onFailure("UID is missing.");

        databaseReference.child(uid).removeValue((error, ref) -> {
            if (error == null) callback.onSuccess("User is successfully deleted.");
            else callback.onFailure(error.getMessage());
        });
    }
}
