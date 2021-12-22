package com.example.gasitmobiledelvieryplatformapplication.model;

public interface ItemRequestCallback<T> {
    // Gets Success Message to Toast on each Activity...
    void onSuccess(T item);

    // Gets Failed/Error Message to Toast on each Activity...
    void onFailure(String error);
}
