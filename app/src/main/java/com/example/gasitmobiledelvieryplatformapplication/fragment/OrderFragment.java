package com.example.gasitmobiledelvieryplatformapplication.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gasitmobiledelvieryplatformapplication.R;

public class OrderFragment extends Fragment {
    public OrderFragment() {
        super(R.layout.fragment_order);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order, container, false);
    }
}