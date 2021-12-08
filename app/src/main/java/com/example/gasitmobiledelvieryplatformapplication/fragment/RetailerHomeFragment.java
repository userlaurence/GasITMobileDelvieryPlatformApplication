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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gasitmobiledelvieryplatformapplication.CreateGasolineActivity;
import com.example.gasitmobiledelvieryplatformapplication.R;
import com.example.gasitmobiledelvieryplatformapplication.adapter.GasolineAdapter;
import com.example.gasitmobiledelvieryplatformapplication.model.Gasoline;
import com.example.gasitmobiledelvieryplatformapplication.model.ListItemRequestCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class RetailerHomeFragment extends Fragment {
    private ActivityResultLauncher<Intent> refreshFragmentActivityResultLauncher;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    public RetailerHomeFragment() {
        super(R.layout.fragment_retailer_home);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_retailer_home, container, false);

        progressBar = rootView.findViewById(R.id.progressBar);

        recyclerView = rootView.findViewById(R.id.gasolineRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        initData();

        refreshFragmentActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (getActivity() == null) return;

                    initData();
                });

        FloatingActionButton fabCreateGasoline = rootView.findViewById(R.id.fabCreateGasoline);
        fabCreateGasoline.setOnClickListener(view -> goToCreateGasoline(null));

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
        goToCreateGasoline(null);
    }

    private void onDataInitializedSuccess(List<Gasoline> gasolineList) {
        GasolineAdapter gasolineAdapter = new GasolineAdapter(gasolineList);
        gasolineAdapter.setOnClickListener(this::goToCreateGasoline);
        recyclerView.setAdapter(gasolineAdapter);
    }

    private void onRequestError(String error) {
        progressBar.setVisibility(View.GONE);

        if (getActivity() == null) return;
        Toasty.error(getActivity(), error, Toasty.LENGTH_SHORT).show();
    }

    private void goToCreateGasoline(Gasoline parcelGasoline) {
        Intent intent = new Intent(getActivity(), CreateGasolineActivity.class);
        if (parcelGasoline != null)
            intent.putExtra(CreateGasolineActivity.EXTRA_GASOLINE_KEY, parcelGasoline);

        refreshFragmentActivityResultLauncher.launch(intent);
    }
}
