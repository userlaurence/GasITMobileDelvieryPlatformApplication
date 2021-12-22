package com.example.gasitmobiledelvieryplatformapplication.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.gasitmobiledelvieryplatformapplication.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationFragment extends Fragment {
    public LocationFragment() {
        super(R.layout.fragment_location);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Initialize View...
        View view = inflater.inflate(R.layout.fragment_location, container, false);

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

        return view;
    }
}