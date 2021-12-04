package com.example.gasitmobiledelvieryplatformapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import com.example.gasitmobiledelvieryplatformapplication.dialog.CancelConfirmationDialogFragment;
import com.example.gasitmobiledelvieryplatformapplication.models.Gasoline;
import com.example.gasitmobiledelvieryplatformapplication.models.SimpleRequestCallback;
import com.example.gasitmobiledelvieryplatformapplication.util.Formatter;
import com.squareup.picasso.Picasso;

import es.dmoral.toasty.Toasty;

public class CreateGasolineActivity extends AppCompatActivity {
    public static final String EXTRA_GASOLINE_KEY = "EXTRA_GASOLINE_KEY";

    private ActivityResultLauncher<Intent> imagePickActivityResultLauncher;

    private ProgressBar progressBar;
    private ImageView imageView;
    private EditText nameEditText, weightEditText, priceEditText, stocksEditText;

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

    private void onRequestSuccess(String message) {
        progressBar.setVisibility(android.view.View.GONE);
        Toasty.success(this, message, Toasty.LENGTH_SHORT).show();
    }

    private void onRequestError(String error) {
        progressBar.setVisibility(android.view.View.GONE);
        Toasty.error(this, error, Toasty.LENGTH_SHORT).show();
    }

    private void goBackToHome() {
        setResult(RESULT_OK);
        finish();
    }

    private void initViewsAndListeners(Gasoline parcelledGasoline) {
        // Initialize Views.
        progressBar = findViewById(R.id.progressBar);

        imageView = findViewById(R.id.gasolineImageView);

        nameEditText = findViewById(R.id.gasolineNameEditText);
        weightEditText = findViewById(R.id.gasolineWeightEditText);
        priceEditText = findViewById(R.id.gasolinePriceEditText);
        stocksEditText = findViewById(R.id.gasolineStocksEditText);

        final ImageButton goBackButton = findViewById(R.id.goBackButton);
        final Button cancelButton = findViewById(R.id.cancelButton);
        final Button createGasolineButton = findViewById(R.id.createGasolineButton);
        final Button deleteGasolineButton = findViewById(R.id.deleteGasolineButton);

        // Values of parcelled product.
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
            priceEditText.setText(getString(R.string.gasoline_price,
                    Formatter.formatMoneyForDisplay(parcelledGasoline.getPrice())));
            stocksEditText.setText(String.valueOf(parcelledGasoline.getStock()));

            deleteGasolineButton.setText(getString(R.string.delete_item, parcelledGasoline.getName()));
        }

        String createEditStr = parcelledGasoline == null
                ? getString(R.string.create_item, "Gasoline")
                : getString(R.string.edit_item, parcelledGasoline.getName());
        ((TextView) findViewById(R.id.createGasolineTextView)).setText(createEditStr);
        createGasolineButton.setText(createEditStr);

        // ImageView Listener.
        imageView.setOnClickListener(view -> openFileChooser());

        // Button Listeners.
        goBackButton.setOnClickListener(view -> goBackToHome());
        cancelButton.setOnClickListener(view -> askToGoBackToHomeDialog());
        createGasolineButton.setOnClickListener(view -> writeGasoline(parcelledGasoline));
        // TODO: Support delete and edit Gasoline.
//        if (parcelledGasoline != null) {
//            deleteGasolineButton.setVisibility(View.VISIBLE);
//            deleteGasolineButton.setOnClickListener(view -> presenter.deleteProduct(parcelledProduct.getUid()));
//        }
    }

    private void writeGasoline(Gasoline parcelledGasoline) {
        progressBar.setVisibility(android.view.View.VISIBLE);

        if (imageUri == null) {
            onRequestError("No image selected. Please choose one");
            return;
        }

        Gasoline gasoline = new Gasoline(nameEditText.getText().toString(),
                Float.parseFloat(weightEditText.getText().toString()),
                Formatter.formatMoneyForDB(
                        Float.parseFloat(priceEditText.getText().toString())),
                Integer.parseInt(stocksEditText.getText().toString()));

        if (parcelledGasoline != null) gasoline.setUid(parcelledGasoline.getUid());

        gasoline.uploadImage(imageUri, new SimpleRequestCallback() {
            @Override
            public void onSuccess(String message) {
                gasoline.write(new SimpleRequestCallback() {
                    @Override
                    public void onSuccess(String message) {
                        onRequestSuccess("Gasoline successfully created.");
                    }

                    @Override
                    public void onFailure(String error) {
                        onRequestError(error);
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                onRequestError(error);
            }
        });
    }

    private void askToGoBackToHomeDialog() {
        new CancelConfirmationDialogFragment()
                .show(getSupportFragmentManager(), CancelConfirmationDialogFragment.TAG);
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        imagePickActivityResultLauncher.launch(intent);
    }
}
