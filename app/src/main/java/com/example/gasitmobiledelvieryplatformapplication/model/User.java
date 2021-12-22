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
    private String role; // Can't be Initialized Using Constructor

    // Use for User Session...
    public User() {
        userMap = new HashMap<>();
        checkUserSession();
    }

    // Use for Signing In...
    public User(String email, String password) {
        this.email = email;
        this.password = password;

        userMap = new HashMap<>();
        userMap.put("email", email);
        userMap.put("password", password);
    }

    /**
     * Use for Sign Up / Registration...
     **/
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
        this.role = role;

        userMap = new HashMap<>();
        userMap.put("firstName", firstName);
        userMap.put("lastName", lastName);
        userMap.put("phoneNumber", phoneNumber);
        userMap.put("address", address);

        userMap.put("gender", gender);
        userMap.put("age", age);

        userMap.put("email", email);
        userMap.put("password", password);
        // userMap.put("role", role);
    }

    public String getUid() {
        return uid;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getGender() {
        return gender;
    }

    public int getAge() {
        return age;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

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

    /**
     * Use for Sign Up / Registration...
     **/

    // Use for Retailer (Admin) User...
    public boolean isAdmin() {
        return ROLE_ADMIN.equals(getRole());
    }

    // Use for User Authentication on Item(s)...
    public void readAuthenticatedUser(ItemRequestCallback<User> callback) {
        if (!checkUserSession()) {
            callback.onFailure("User is not Authenticated.");
            return;
        }

        databaseReference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                if (user != null) setUid(snapshot.getKey());

                // Return Regardless if Null or Not.
                callback.onSuccess(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    // Use for User Authentication when Logging In...
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

    // Use for Database Authentication via Email & Password only...
    public void signInWithEmailAndPassword(SimpleRequestCallback callback) {
        if (getEmail() == null || getPassword() == null)
            return; // Make Sure to Use Constructor for Sign In.

        firebaseAuth.signInWithEmailAndPassword(getEmail(), getPassword())
                .addOnCompleteListener(task -> {
                    /*
                     * Different from this.firebaseUser since currentUser is the Newly Created
                     * User and may not be the same with this.firebaseUser if an Error Occurs.
                     */
                    firebaseUser = firebaseAuth.getCurrentUser();

                    if (!task.isSuccessful() || firebaseUser == null) {
                        callback.onFailure("Incorrect Input, Please Re-enter Account Email/Password.");
                        // callback.onFailure("Failed to Login! Please check your credentials.");
                        return;
                    }

                    if (!firebaseUser.isEmailVerified()) {
                        firebaseUser.sendEmailVerification();
                        callback.onFailure("Check your Email to Verify your Account. Then Try Again.");
                        return;
                    }

                    readAuthenticatedUser(callback);
                });
    }

    // Use for Facebook and Google Credentials...
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

    // Use for Signing In via Facebook...
    public void signInWithFacebook(AccessToken accessToken, SimpleRequestCallback callback) {
        AuthCredential authCredential = FacebookAuthProvider.getCredential(accessToken.getToken());
        signInWithCredential(authCredential, callback);
    }

    // Use for Signing In via Google...
    public void signInWithGoogle(String idToken, SimpleRequestCallback callback) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(idToken, null);
        signInWithCredential(authCredential, callback);
    }

    // Use for Database Account Registration...
    public void signUp(SimpleRequestCallback callback) {
        firebaseAuth.createUserWithEmailAndPassword(getEmail(), getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        setPassword(null); /* So that it won't be seen in write(). */
                        /*
                         * All Users Using the SignUp are Customers.
                         * Retailer (Admin) are Assign Directly on Firebase and must Communicate
                         * with the Database Admin.
                         */
                        setRole(ROLE_CUSTOMER);
                        callback.onSuccess(getFirstName() + " Successfully Created. " +
                                "Please Check Your Email to Verify Your Account.");
                    } else if (task.getException() != null)
                        callback.onFailure(task.getException().getMessage());
                });
    }

    // Use for Clearing Active Account Session...
    public void signOut() {
        if (!checkUserSession())
            return;

        firebaseAuth.signOut();
    }

    // Use for Button Signing Out Current Active Account...
    public void signOutCurrent(SimpleRequestCallback callback) {
        if (checkUserSession()) {
            callback.onSuccess("Successfully Signed Out");
            return;
        }

        firebaseAuth.signOut();
    }

    // Use for Writing Account being Registered to Database...
    public void write(SimpleRequestCallback callback) {
        if (!checkUserSession()) {
            /* Must Call SignUp First Before Calling Write to Create the User ID. */
            callback.onFailure("UID is Missing.");
            return;
        }

        if (getPassword() != null) {
            firebaseUser.updatePassword(getPassword());
            // Prevents from Write "password" in User node.
            userMap.remove("password");
        }

        if (getEmail() != null)
            firebaseUser.updateEmail(getEmail());

        databaseReference.child(getUid()).updateChildren(userMap, (error, ref) -> {
            if (error == null) callback.onSuccess(getUid());
            else callback.onFailure(error.toException().getMessage());
        });
    }

    // Use for Resetting Password...
    public void resetPassword(SimpleRequestCallback callback) {
        if (getEmail() == null)
            callback.onFailure("Email Has Not Been Set. Please Try Again.");

        firebaseAuth.sendPasswordResetEmail(getEmail())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        callback.onSuccess("Check Your Email for the Reset Password Link.");
                    else if (task.getException() != null)
                        callback.onFailure(task.getException().getMessage());
                });
    }

    // Use for Checking Session of User...
    public boolean checkUserSession() {
        if (firebaseAuth.getCurrentUser() == null)
            return false;

        this.firebaseUser = firebaseAuth.getCurrentUser();
        this.uid = firebaseUser.getUid();
        return true;
    }

    // Use for Account Deletion...
    public void delete(SimpleRequestCallback callback) {
        if (uid == null) callback.onFailure("UID is Missing.");

        databaseReference.child(uid).removeValue((error, ref) -> {
            if (error == null) callback.onSuccess("User is Successfully Deleted.");
            else callback.onFailure(error.getMessage());
        });
    }
}
