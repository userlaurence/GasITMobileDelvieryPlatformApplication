package com.example.gasitmobiledelvieryplatformapplication.models;

import java.util.List;

public interface ListItemRequestCallback<T> {
    void onSuccess(List<T> itemList);
    void onFailure(String error);
}
