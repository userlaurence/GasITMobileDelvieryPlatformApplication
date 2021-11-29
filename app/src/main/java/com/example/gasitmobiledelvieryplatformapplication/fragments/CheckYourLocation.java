package com.example.gasitmobiledelvieryplatformapplication.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.gasitmobiledelvieryplatformapplication.R;
import com.example.gasitmobiledelvieryplatformapplication.RegistrationForm;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class CheckYourLocation extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Initialize View...
        View view = inflater.inflate(R.layout.fragment_check_your_location, container, false);

        //Initialize Map Fragment...
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);

        //Async Map...
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                //When Map is Loaded...
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        //When Clicked on Map...
                        //Initialize Marker Options...
                        MarkerOptions markerOptions = new MarkerOptions();
                        //Set Position of Marker...
                        markerOptions.position(latLng);
                        //Set Title of Marker...
                        markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                        //Remove All Marker...
                        googleMap.clear();
                        //Animating for Zooming Marker...
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                latLng, 10
                        ));
                        //Add Marker on Map...
                        googleMap.addMarker(markerOptions);
                    }
                });
            }
        });

        //Return View...
        return view;
    }
}