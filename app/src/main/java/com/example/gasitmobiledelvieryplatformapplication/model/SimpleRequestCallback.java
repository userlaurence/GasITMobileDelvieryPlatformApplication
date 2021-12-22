package com.example.gasitmobiledelvieryplatformapplication.model;

public interface SimpleRequestCallback {
    // Gets Success Message to Toast on each Activity...
    void onSuccess(String message);

    // Gets Failed/Error Message to Toast on each Activity...
    void onFailure(String error);
}
