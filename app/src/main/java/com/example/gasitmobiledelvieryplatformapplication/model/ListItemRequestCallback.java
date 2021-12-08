package com.example.gasitmobiledelvieryplatformapplication.model;

import java.util.List;

public interface ListItemRequestCallback<T> {
    void onSuccess(List<T> itemList);
    void onFailure(String error);
}
