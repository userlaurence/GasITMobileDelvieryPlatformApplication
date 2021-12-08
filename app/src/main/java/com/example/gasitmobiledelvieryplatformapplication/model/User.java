package com.example.gasitmobiledelvieryplatformapplication.model;

import androidx.annotation.NonNull;

import com.facebook.AccessToken;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class User {
    private static final String FIREBASE_NODE = "User";
    private final DatabaseReference databaseReference = FirebaseDatabase
            .getInstance("https://gasit-a1713-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference(FIREBASE_NODE);
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser;

    private final HashMap<String, Object> userMap;

    private static final String ROLE_ADMIN = "admin";
    private static final String ROLE_CUSTOMER = "customer";

    private String uid;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String gender;
    private int age;
    private String email;
    private String password;
    private String role; // Can't be initialized using constructor.

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
    public String getRole() {   return role;    }
    public String getFullName() {   return firstName + " " + lastName;    }

    public void setUid(String uid) {
        this.uid = uid;
        userMap.put("uid", uid);
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        userMap.put("phoneNumber", phoneNumber);
    }

    public void setAddress(String address) {
        this.address = address;
        userMap.put("address", address);
    }

    public void setPassword(String password) {
        this.password = password;
        userMap.put("password", password);
    }

    public void setRole(String role) {
        this.role = role;
        userMap.put("role", role);
    }

    public boolean isAdmin() {
        return ROLE_ADMIN.equals(getRole());
    }

    public void readAuthenticatedUser(ItemRequestCallback<User> callback) {
        if (!checkUserSession()) {
            callback.onFailure("User is not authenticated.");
            return;
        }

        databaseReference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                if (user != null) setUid(snapshot.getKey());

                // Return regardless if null or not.
                callback.onSuccess(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    public void readAuthenticatedUser(SimpleRequestCallback callback) {
        databaseReference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                String fullName = "User";

                if (user != null) {
                    setUid(snapshot.getKey());
                    setRole(user.getRole());
                    fullName = user.getFullName();
                }
                callback.onSuccess(fullName + " successfully signed in.");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    public void signInWithEmailAndPassword(SimpleRequestCallback callback) {
        if (getEmail() == null || getPassword() == null)
            return; // Make sure to use constructor for sign in.

        firebaseAuth.signInWithEmailAndPassword(getEmail(), getPassword())
                .addOnCompleteListener(task -> {
                    /*
                     * Different from this.firebaseUser since currentUser is the newly created
                     * user and may not be the same with this.firebaseUser if an error occurs.
                     */
                    firebaseUser = firebaseAuth.getCurrentUser();

                    if (!task.isSuccessful() || firebaseUser == null) {
                        callback.onFailure("Failed to login! Please check your credentials.");
                        return;
                    }

                    if (!firebaseUser.isEmailVerified()) {
                        firebaseUser.sendEmailVerification();
                        callback.onFailure("Check your email to verify your account. Then try again.");
                        return;
                    }

                    readAuthenticatedUser(callback);
                });
    }

    private void signInWithCredential(AuthCredential authCredential, SimpleRequestCallback callback) {
        firebaseAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        callback.onFailure("Authentication Failed.");
                        return;
                    }

                    firebaseUser = firebaseAuth.getCurrentUser();
                    readAuthenticatedUser(callback);
                });
    }

    public void signInWithFacebook(AccessToken accessToken, SimpleRequestCallback callback) {
        AuthCredential authCredential = FacebookAuthProvider.getCredential(accessToken.getToken());
        signInWithCredential(authCredential, callback);
    }

    public void signInWithGoogle(String idToken, SimpleRequestCallback callback) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(idToken, null);
        signInWithCredential(authCredential, callback);
    }

    public void signUp(SimpleRequestCallback callback) {
        firebaseAuth.createUserWithEmailAndPassword(getEmail(), getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        setPassword(null); /* So that it won't be seen in write(). */
                        /*
                         * All users using the signup are customers.
                         * Admin are assign directly on Firebase and must communicate
                         * with the Database admin.
                         */
                        setRole(ROLE_CUSTOMER);
                        callback.onSuccess(getFirstName() + " successfully created. " +
                                "Please check your email to verify your account.");
                    }
                    else if (task.getException() != null)
                        callback.onFailure(task.getException().getMessage());
                });
    }

    public void signOut() {
        if (!checkUserSession())
            return;

        firebaseAuth.signOut();
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

    public boolean checkUserSession() {
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
