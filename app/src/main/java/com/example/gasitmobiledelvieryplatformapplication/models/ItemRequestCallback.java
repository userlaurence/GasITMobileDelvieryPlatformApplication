package com.example.gasitmobiledelvieryplatformapplication.models;

public interface ItemRequestCallback<T> {
    void onSuccess(T item);
    void onFailure(String error);
}
