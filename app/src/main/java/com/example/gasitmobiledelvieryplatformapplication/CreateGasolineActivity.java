package com.example.gasitmobiledelvieryplatformapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gasitmobiledelvieryplatformapplication.model.Gasoline;
import com.example.gasitmobiledelvieryplatformapplication.model.SimpleRequestCallback;
import com.example.gasitmobiledelvieryplatformapplication.util.Formatter;
import com.squareup.picasso.Picasso;

import es.dmoral.toasty.Toasty;

public class CreateGasolineActivity extends AppCompatActivity {
    public static final String EXTRA_GASOLINE_KEY = "EXTRA_GASOLINE_KEY";

    private ActivityResultLauncher<Intent> imagePickActivityResultLauncher;

    private ProgressBar progressBar;
    private ImageView imageView;
    private EditText nameEditText, weightEditText, priceEditText, stockEditText;

    private Uri imageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_gasoline);

        Gasoline parcelledGasoline = savedInstanceState == null ?
                getIntent().getParcelableExtra(EXTRA_GASOLINE_KEY) :
                (Gasoline) savedInstanceState.getSerializable(EXTRA_GASOLINE_KEY);

        initViewsAndListeners(parcelledGasoline);

        imagePickActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent data = result.getData();

                    if (result.getResultCode() == RESULT_OK && data != null) {
                        imageUri = data.getData();

                        Picasso.get().load(imageUri).into(imageView);
                    }
                }
        );
    }

    // Toast Successful Message...
    private void onRequestSuccess(String message) {
        progressBar.setVisibility(View.GONE);
        Toasty.success(this, message, Toasty.LENGTH_SHORT).show();
    }

    // Toast Error Message...
    private void onRequestError(String error) {
        progressBar.setVisibility(View.GONE);
        Toasty.error(this, error, Toasty.LENGTH_SHORT).show();
    }

    // Return to Retailer Home Interface...
    private void goBackToHome() {
        setResult(RESULT_OK);
        finish();
    }

    // Event Initializer and Listeners...
    private void initViewsAndListeners(Gasoline parcelledGasoline) {
        // Initialize Views...
        progressBar = findViewById(R.id.progressBar);

        imageView = findViewById(R.id.gasolineImageView);

        nameEditText = findViewById(R.id.gasolineNameEditText);
        weightEditText = findViewById(R.id.gasolineWeightEditText);
        priceEditText = findViewById(R.id.gasolinePriceEditText);
        stockEditText = findViewById(R.id.gasolineStockEditText);

        // Interface Button ID's...
        final ImageButton goBackButton = findViewById(R.id.goBackButton);
        final Button createGasolineButton = findViewById(R.id.createGasolineButton);
        final Button deleteGasolineButton = findViewById(R.id.deleteGasolineButton);

        // Values of parcelled gasoline...
        if (parcelledGasoline != null) {
            if (parcelledGasoline.getImageUrl() != null) {
                imageUri = Uri.parse(parcelledGasoline.getImageUrl());

                Picasso.get()
                        .load(parcelledGasoline.getImageUrl())
                        .placeholder(R.mipmap.ic_launcher)
                        .fit()
                        .into((imageView));
            }

            nameEditText.setText(parcelledGasoline.getName());
            weightEditText.setText(String.valueOf(parcelledGasoline.getWeight()));
            priceEditText.setText(String.valueOf(
                    Formatter.formatMoneyForDisplay(parcelledGasoline.getPrice())));
            stockEditText.setText(String.valueOf(parcelledGasoline.getStock()));

            deleteGasolineButton.setText(getString(R.string.delete_item, parcelledGasoline.getName()));
        }

        String createEditStr = parcelledGasoline == null
                ? getString(R.string.create_item, "Gasoline")
                : getString(R.string.edit_item, parcelledGasoline.getName());
        ((TextView) findViewById(R.id.createGasolineTextView)).setText(createEditStr);
        createGasolineButton.setText(createEditStr);

        // ImageView Listener...
        imageView.setOnClickListener(view -> openFileChooser());

        // Button Listeners...
        goBackButton.setOnClickListener(view -> goBackToHome());
        createGasolineButton.setOnClickListener(view -> writeGasoline(parcelledGasoline));
        if (parcelledGasoline != null) {
            deleteGasolineButton.setVisibility(View.VISIBLE);
            deleteGasolineButton.setOnClickListener(view -> deleteGasoline(parcelledGasoline));
        }
    }

    // Deletion of Item Database...
    private void deleteGasoline(Gasoline gasoline) {
        gasoline.delete(new SimpleRequestCallback() {
            @Override
            public void onSuccess(String message) {
                onRequestSuccess(message);
                goBackToHome();
            }

            @Override
            public void onFailure(String error) {
                onRequestError(error);
            }
        });
    }

    // Creating Gasoline and Storing to Retailer Warehouse Database...
    private void writeGasoline(Gasoline parcelledGasoline) {
        progressBar.setVisibility(View.VISIBLE);

        if (imageUri == null) {
            onRequestError("No image selected. Please choose one");
            return;
        }

        Gasoline gasoline = new Gasoline(nameEditText.getText().toString(),
                Float.parseFloat(weightEditText.getText().toString()),
                Formatter.formatMoneyForDB(
                        Float.parseFloat(priceEditText.getText().toString())),
                Integer.parseInt(stockEditText.getText().toString()));

        if (parcelledGasoline != null) gasoline.setUid(parcelledGasoline.getUid());
        else gasoline.generateUid();

        if (parcelledGasoline != null &&
                imageUri.equals(Uri.parse(parcelledGasoline.getImageUrl()))) {
            gasoline.write(writeCallback());
            goBackToHome();
            return;
        }

        gasoline.uploadImage(imageUri, new SimpleRequestCallback() {
            @Override
            public void onSuccess(String imageDownloadUrl) {
                gasoline.setImageUrl(imageDownloadUrl);
                gasoline.write(writeCallback());
                goBackToHome();
            }

            @Override
            public void onFailure(String error) {
                onRequestError(error);
            }
        });
    }

    // Request Callback for Toasting Messages...
    private SimpleRequestCallback writeCallback() {
        return new SimpleRequestCallback() {
            @Override
            public void onSuccess(String message) {
                onRequestSuccess("Gasoline successfully created.");
            }

            @Override
            public void onFailure(String error) {
                onRequestError(error);
            }
        };
    }

    // Image Picker from the Database...
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        imagePickActivityResultLauncher.launch(intent);
    }
}
