package com.example.gasitmobiledelvieryplatformapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gasitmobiledelvieryplatformapplication.model.Gasoline;
import com.example.gasitmobiledelvieryplatformapplication.model.ItemRequestCallback;
import com.example.gasitmobiledelvieryplatformapplication.model.Order;
import com.example.gasitmobiledelvieryplatformapplication.model.SimpleRequestCallback;
import com.example.gasitmobiledelvieryplatformapplication.model.User;
import com.example.gasitmobiledelvieryplatformapplication.util.FieldUtil;
import com.example.gasitmobiledelvieryplatformapplication.util.Formatter;
import com.squareup.picasso.Picasso;

import es.dmoral.toasty.Toasty;

public class OrderActivity extends AppCompatActivity {
    public static final String EXTRA_ORDER_GASOLINE_KEY = "EXTRA ORDER GASOLINE KEY";

    private ProgressBar progressBar;
    private TextView howManyTextView;
    private EditText phoneNumberEditText, addressEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        Gasoline parcelledGasoline = savedInstanceState == null ?
                getIntent().getParcelableExtra(EXTRA_ORDER_GASOLINE_KEY) :
                (Gasoline) savedInstanceState.getSerializable(EXTRA_ORDER_GASOLINE_KEY);

        new User().readAuthenticatedUser(new ItemRequestCallback<User>() {
            @Override
            public void onSuccess(User user) {
                initViewsAndListeners(parcelledGasoline, user);
            }

            @Override
            public void onFailure(String error) {
                onRequestError(error);
                goBack(RESULT_CANCELED);
            }
        });
    }

    private void goBack(int result) {
        setResult(result);
        finish();
    }

    private void onRequestSuccess(String message) {
        progressBar.setVisibility(View.GONE);
        Toasty.success(this, message, Toasty.LENGTH_SHORT).show();
    }

    private void onRequestError(String error) {
        progressBar.setVisibility(View.GONE);
        Toasty.error(this, error, Toasty.LENGTH_SHORT).show();
    }

    private void initViewsAndListeners(Gasoline parcelledGasoline, User currentUser) {
        progressBar = findViewById(R.id.progressBar);

        final TextView nameTextView = findViewById(R.id.gasolineNameTextView);
        final TextView priceTextView = findViewById(R.id.gasolinePriceTextView);
        final TextView stocksTextView = findViewById(R.id.gasolineStockTextView);
        final TextView subtotalTextView = findViewById(R.id.gasolineTotalTextView);
        howManyTextView = findViewById(R.id.quantityCounterTextView);

        phoneNumberEditText = findViewById(R.id.userPhoneNumberEditText);
        addressEditText = findViewById(R.id.userAddressEditText);

        final ImageView gasolineImageView = findViewById(R.id.gasolineImageView);
        final ImageView increaseImageView = findViewById(R.id.increaseButton);
        final ImageView decreaseImageView = findViewById(R.id.decreaseButton);
        final ImageView phoneNumberEditButton = findViewById(R.id.phoneNumberEditButton);
        final ImageView addressEditButton = findViewById(R.id.addressEditButton);

        final Button proceedButton = findViewById(R.id.proceedButton);
        final Button cancelButton = findViewById(R.id.cancelButton);

        Picasso.get()
                .load(parcelledGasoline.getImageUrl())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into((gasolineImageView));
        nameTextView.setText(parcelledGasoline.getName());

        priceTextView.setText(Formatter.formatMoneyWithPesoSign(parcelledGasoline.getPrice()));
        stocksTextView.setText(getString(R.string.gasoline_stock, parcelledGasoline.getStock()));
        subtotalTextView.setText(getString(R.string.gasoline_total, 0.00));

        if (currentUser == null) {
            makeEditTextEditable(phoneNumberEditText);
            makeEditTextEditable(addressEditText);
        } else {
            phoneNumberEditText.setText(currentUser.getPhoneNumber());
            addressEditText.setText(currentUser.getAddress());
        }

        increaseImageView.setOnClickListener(view1 -> {
            int qty = Integer.parseInt(howManyTextView.getText().toString()) + 1;
            qty = Math.min(qty, parcelledGasoline.getStock());

            proceedButton.setEnabled(qty <= parcelledGasoline.getStock());
            howManyTextView.setText(String.valueOf(qty));
            subtotalTextView.setText(getString(R.string.gasoline_total,
                    Formatter.formatMoneyForDisplay(qty * parcelledGasoline.getPrice())));
        });

        decreaseImageView.setOnClickListener(view3 -> {
            int qty = Integer.parseInt(howManyTextView.getText().toString()) - 1;
            qty = Math.max(qty, 0);

            proceedButton.setEnabled(qty > 0);
            howManyTextView.setText(String.valueOf(qty));
            subtotalTextView.setText(getString(R.string.gasoline_total,
                    Formatter.formatMoneyForDisplay(qty * parcelledGasoline.getPrice())));
        });

        phoneNumberEditButton.setOnClickListener(v -> makeEditTextEditable(phoneNumberEditText));
        addressEditButton.setOnClickListener(v -> makeEditTextEditable(addressEditText));

        proceedButton.setOnClickListener(v -> proceedToOrder(parcelledGasoline, currentUser));
        cancelButton.setOnClickListener(v -> goBack(RESULT_CANCELED));
    }

    private void makeEditTextEditable(EditText editText) {
        editText.setClickable(true);
        editText.setEnabled(true);
    }

    private void proceedToOrder(Gasoline gasoline, User currentUser) {
        if (FieldUtil.isEmptyEditText("Phone Number", phoneNumberEditText) ||
                FieldUtil.isEmptyEditText("Address", addressEditText)) {
            return;
        }

        String phoneNumber = phoneNumberEditText.getText().toString();
        String address = addressEditText.getText().toString();

        // In cases where the user signed in using his/her Google or Facebook account,
        // Store their phone number and address on the User node in Firebase Realtime Database.
        if (currentUser == null) {
            currentUser = new User();
            currentUser.setPhoneNumber(phoneNumber);
            currentUser.setAddress(address);
            currentUser.checkUserSession(); // To setup uid and firebaseUser.
            // We don't care about the callback since this will happen in the background
            // without the user's knowledge.
            currentUser.write(new SimpleRequestCallback() {
                @Override
                public void onSuccess(String message) {}
                @Override
                public void onFailure(String error) {}
            });
        }

        int howMany = Integer.parseInt(howManyTextView.getText().toString());
        new Order(Order.STATUS_PENDING, gasoline.getPrice(), howMany, gasoline.getUid(),
                gasoline.getName(), phoneNumber, address)
                .write(new SimpleRequestCallback() {
                    @Override
                    public void onSuccess(String gasolineUid) {
                        reduceGasolineQuantity(gasoline, howMany);
                    }

                    @Override
                    public void onFailure(String error) {
                        onRequestError(error);
                    }
                });
    }

    private void reduceGasolineQuantity(Gasoline gasoline, int howMany) {
        new Gasoline(gasoline.getUid(), gasoline.getStock() - howMany)
                .write(new SimpleRequestCallback() {
                    @Override
                    public void onSuccess(String message) {
                        onRequestSuccess(gasoline.getName() + " purchase request has been sent.");
                        goBack(RESULT_OK);
                    }

                    @Override
                    public void onFailure(String error) {
                        onRequestError(error);
                    }
                });
    }
}
