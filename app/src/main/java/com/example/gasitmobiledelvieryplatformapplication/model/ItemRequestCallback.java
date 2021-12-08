package com.example.gasitmobiledelvieryplatformapplication.model;

public interface ItemRequestCallback<T> {
    void onSuccess(T item);
    void onFailure(String error);
}
