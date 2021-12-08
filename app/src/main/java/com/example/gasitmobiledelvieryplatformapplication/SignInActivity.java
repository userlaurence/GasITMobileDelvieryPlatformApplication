package com.example.gasitmobiledelvieryplatformapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gasitmobiledelvieryplatformapplication.R.id;
import com.example.gasitmobiledelvieryplatformapplication.model.SimpleRequestCallback;
import com.example.gasitmobiledelvieryplatformapplication.model.User;
import com.example.gasitmobiledelvieryplatformapplication.util.FieldUtil;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.util.Arrays;

import es.dmoral.toasty.Toasty;

public class SignInActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private EditText emailEditText, passwordEditText;
    private CallbackManager callbackManager;
    private GoogleSignInClient googleSignInClient;
    private ActivityResultLauncher<Intent> googleSignInActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        callbackManager = CallbackManager.Factory.create();
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("31879182604-423qhjh7hgp5i0vvop374it08rd21ak2.apps.googleusercontent.com")
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        googleSignInActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getData() == null) {
                        onRequestCancel("Google authentication was cancelled.");
                        return;
                    }

                    progressBar.setVisibility(View.VISIBLE);
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    try {
                        GoogleSignInAccount googleSignInAccount = task.getResult(ApiException.class);
                        User user = new User();
                        user.signInWithGoogle(googleSignInAccount.getIdToken(), new SimpleRequestCallback() {
                            @Override
                            public void onSuccess(String message) {
                                onRequestSuccess(message, user.isAdmin());
                            }

                            @Override
                            public void onFailure(String error) {
                                onRequestError(error);
                            }
                        });
                    } catch (ApiException e) {
                        e.printStackTrace();
                        onRequestError(e.getMessage());
                    }
                }
        );

        initViewsAndListeners();
    }

    private void initViewsAndListeners() {
        progressBar = findViewById(R.id.progressBar);

        emailEditText = findViewById(id.signInEmailEditText);
        passwordEditText = findViewById(id.signInPasswordEditText);

        Button signInButton = findViewById(id.signInButton);
        Button signInWithFacebookButton = findViewById(id.signInWithFacebookButton);
        Button signInWithGoogleButton = findViewById(id.signInWithGoogleButton);
        Button registerButton = findViewById(id.registerButton);

        signInButton.setOnClickListener(v -> signInWithEmailAndPassword());
        signInWithFacebookButton.setOnClickListener(v -> signInWithFacebook());
        signInWithGoogleButton.setOnClickListener(v -> signInWithGoogle());
        registerButton.setOnClickListener(v -> goToSignUp());
    }

    private void onRequestSuccess(String message, boolean isAdmin) {
        progressBar.setVisibility(android.view.View.GONE);
        Toasty.success(this, message, Toasty.LENGTH_LONG).show();
        goToRoleSpecificMainActivity(isAdmin);
    }

    private void onRequestCancel(String message) {
        progressBar.setVisibility(android.view.View.GONE);
        Toasty.info(this, message, Toasty.LENGTH_LONG).show();
    }

    private void onRequestError(String error) {
        progressBar.setVisibility(android.view.View.GONE);
        Toasty.error(this, error, Toasty.LENGTH_SHORT).show();
    }

    private void goToRoleSpecificMainActivity(boolean isAdmin) {
        Class<? extends AppCompatActivity> activityClass;
        if (isAdmin)    activityClass = RetailerMainActivity.class;
        else            activityClass = CustomerMainActivity.class;
        startActivity(new Intent(getApplicationContext(), activityClass));
        finish();
    }

    private User validateInputs() {
        if (FieldUtil.isEmptyEditText("Email", emailEditText) ||
                FieldUtil.isEmptyEditText("Password", passwordEditText) ||
                FieldUtil.isEmailIncorrect(emailEditText) ||
                FieldUtil.isIncorrectPasswordLength("Password", passwordEditText))
            return null;

        return new User(emailEditText.getText().toString(), passwordEditText.getText().toString());
    }

    private void signInWithEmailAndPassword() {
        User user = validateInputs();
        if (user == null) return;

        progressBar.setVisibility(View.VISIBLE);
        user.signInWithEmailAndPassword(new SimpleRequestCallback() {
            @Override
            public void onSuccess(String message) {
                onRequestSuccess(message, user.isAdmin());
            }

            @Override
            public void onFailure(String error) {
                onRequestError(error);
            }
        });
    }

    private void signInWithFacebook() {
        progressBar.setVisibility(View.VISIBLE);

        LoginManager.getInstance().logInWithReadPermissions(
                this, callbackManager, Arrays.asList("public_profile", "email"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                User user = new User();
                user.signInWithFacebook(loginResult.getAccessToken(),
                        new SimpleRequestCallback() {
                            @Override
                            public void onSuccess(String message) {
                                onRequestSuccess(message, user.isAdmin());
                            }

                            @Override
                            public void onFailure(String error) {
                                onRequestError(error);
                            }
                        });
            }

            @Override
            public void onCancel() {
                onRequestCancel("Facebook authentication was cancelled.");
            }

            @Override
            public void onError(@NonNull FacebookException e) {
                onRequestError(e.getMessage());
            }
        });
    }

    private void signInWithGoogle() {
        googleSignInActivityResultLauncher.launch(googleSignInClient.getSignInIntent());
    }

    private void goToSignUp() {
        startActivity(new Intent(this, SignUpActivity.class));
    }
}