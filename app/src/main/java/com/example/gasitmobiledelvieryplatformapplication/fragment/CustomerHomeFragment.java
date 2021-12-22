package com.example.gasitmobiledelvieryplatformapplication.fragment;

import android.app.Activity;
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

import com.example.gasitmobiledelvieryplatformapplication.OrderActivity;
import com.example.gasitmobiledelvieryplatformapplication.R;
import com.example.gasitmobiledelvieryplatformapplication.adapter.GasolineAdapter;
import com.example.gasitmobiledelvieryplatformapplication.model.Gasoline;
import com.example.gasitmobiledelvieryplatformapplication.model.ListItemRequestCallback;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class CustomerHomeFragment extends Fragment {
    private ActivityResultLauncher<Intent> refreshFragmentActivityResultLauncher;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    public CustomerHomeFragment() {
        super(R.layout.fragment_customer_home);
    }

    /**
     * Customer Home Fragment XML ID's...
     **/
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_customer_home, container, false);

        progressBar = rootView.findViewById(R.id.progressBar);

        recyclerView = rootView.findViewById(R.id.gasolineRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // Initialization of Data's...
        initData();

        refreshFragmentActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_CANCELED) return;
                    initData();
                });

        return rootView;
    }

    /**
     * Customer Home Fragment XML ID's...
     **/

    // Data Initialization...
    private void initData() {
        progressBar.setVisibility(View.VISIBLE);

        // Read Stock Gasoline Data...
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

    // Toast Message if Empty Lists...
    private void onDataEmpty() {
        if (getActivity() == null) return;
        Toasty.info(getActivity(),
                "There's No Gasoline Yet!",
                Toasty.LENGTH_SHORT).show();
    }

    // Displays all Gathered Gasoline Data Lists...
    private void onDataInitializedSuccess(List<Gasoline> gasolineList) {
        GasolineAdapter gasolineAdapter = new GasolineAdapter(false, gasolineList);
        gasolineAdapter.setOnClickListener(this::goToOrder);
        recyclerView.setAdapter(gasolineAdapter);
    }

    // Order Interface Process via Gasoline Adapter Class...
    private void goToOrder(Gasoline gasoline) {
        Intent intent = new Intent(getActivity(), OrderActivity.class);
        intent.putExtra(OrderActivity.EXTRA_ORDER_GASOLINE_KEY, gasoline);
        refreshFragmentActivityResultLauncher.launch(intent);
    }

    // Toast Message for Failure and Error Reading Data Lists...
    private void onRequestError(String error) {
        progressBar.setVisibility(View.GONE);

        if (getActivity() == null) return;
        Toasty.error(getActivity(), error, Toasty.LENGTH_SHORT).show();
    }
}