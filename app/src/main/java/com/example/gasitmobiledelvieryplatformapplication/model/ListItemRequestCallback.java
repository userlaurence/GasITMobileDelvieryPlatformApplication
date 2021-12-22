package com.example.gasitmobiledelvieryplatformapplication.model;

import java.util.List;

public interface ListItemRequestCallback<T> {
    // Gets Success Message to Toast on each Activity...
    void onSuccess(List<T> itemList);

    // Gets Failed/Error Message to Toast on each Activity...
    void onFailure(String error);
}
