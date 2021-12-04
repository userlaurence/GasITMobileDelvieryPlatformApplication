package com.example.gasitmobiledelvieryplatformapplication.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gasitmobiledelvieryplatformapplication.CreateGasolineActivity;
import com.example.gasitmobiledelvieryplatformapplication.R;
import com.example.gasitmobiledelvieryplatformapplication.adapter.GasolineAdapter;
import com.example.gasitmobiledelvieryplatformapplication.models.Gasoline;
import com.example.gasitmobiledelvieryplatformapplication.models.ListItemRequestCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class HomeFragment extends Fragment {
    private ActivityResultLauncher<Intent> refreshFragmentActivityResultLauncher;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        progressBar = rootView.findViewById(R.id.progressBar);

        recyclerView = rootView.findViewById(R.id.gasolineRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        initData();

        refreshFragmentActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (getActivity() == null) return;

                    initData();
                });

        FloatingActionButton fabCreateGasoline = rootView.findViewById(R.id.fabCreateGasoline);
        fabCreateGasoline.setOnClickListener(view -> goToCreateGasoline());

        return rootView;
    }

    private void initData() {
        progressBar.setVisibility(View.VISIBLE);

        new Gasoline().readAll(new ListItemRequestCallback<Gasoline>() {
            @Override
            public void onSuccess(List<Gasoline> itemList) {
                progressBar.setVisibility(View.GONE);
                if (itemList == null) onDataEmpty();
                else onDataInitializedSuccess(itemList);
            }

            @Override
            public void onFailure(String error) {
                onRequestError(error);
            }
        });
    }

    private void onDataEmpty() {
        if (getActivity() == null) return;
        Toasty.info(getActivity(),
                "There's no gasoline yet! Going to Create New Gasoline.",
                Toasty.LENGTH_SHORT).show();
        goToCreateGasoline();
    }

    private void onDataInitializedSuccess(List<Gasoline> gasolineList) {
        GasolineAdapter gasolineAdapter = new GasolineAdapter(gasolineList);
        // TODO: Listener when clicked. Go to order fragment.
        // gasolineAdapter.setListener(this::displayProduct);
        recyclerView.setAdapter(gasolineAdapter);
    }

    private void onRequestError(String error) {
        progressBar.setVisibility(View.GONE);

        if (getActivity() == null) return;
        Toasty.error(getActivity(), error, Toasty.LENGTH_SHORT).show();
    }

    private void goToCreateGasoline() {
        Intent intent = new Intent(getActivity(), CreateGasolineActivity.class);
        refreshFragmentActivityResultLauncher.launch(intent);
    }
}
