package com.example.gasitmobiledelvieryplatformapplication.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gasitmobiledelvieryplatformapplication.R;
import com.example.gasitmobiledelvieryplatformapplication.model.Gasoline;
import com.example.gasitmobiledelvieryplatformapplication.util.Formatter;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GasolineAdapter extends RecyclerView.Adapter<GasolineAdapter.GasolineViewHolder> {
    private final List<Gasoline> gasolineArrayList;
    private OnClickListener listener;

    public interface OnClickListener {
        void onClick(Gasoline gasoline);
    }

    public GasolineAdapter(List<Gasoline> gasolineArrayList) {
        this.gasolineArrayList = gasolineArrayList;
    }

    @NonNull
    @Override
    public GasolineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_gasoline, parent, false);
        return new GasolineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GasolineViewHolder holder, int position) {
        holder.onBind(gasolineArrayList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return gasolineArrayList.size();
    }

    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    public static class GasolineViewHolder extends RecyclerView.ViewHolder {
        ImageView gasolineImageView;
        TextView gasolineNameTextView, gasolinePriceTextView,
                gasolineStockTextView, gasolineWeightTextView;
        View gasolineCardContainer;

        public GasolineViewHolder(@NonNull View itemView) {
            super(itemView);
            gasolineCardContainer = itemView.findViewById(R.id.gasolineCardContainer);
            gasolineImageView = itemView.findViewById(R.id.gasolineImageView);
            gasolineNameTextView = itemView.findViewById(R.id.gasolineNameTextView);
            gasolinePriceTextView = itemView.findViewById(R.id.gasolinePriceTextView);
            gasolineStockTextView = itemView.findViewById(R.id.gasolineStockTextView);
            gasolineWeightTextView = itemView.findViewById(R.id.gasolineWeightTextView);
        }

        public void onBind(Gasoline gasoline, OnClickListener listener) {
            String weightText = gasoline.getWeight() + " kgs";
            String priceText = Formatter.formatMoneyWithPesoSign(gasoline.getPrice());
            String stockText = gasoline.getStock() + " left";

            Picasso.get()
                    .load(gasoline.getImageUrl())
                    .placeholder(R.drawable.splash_screen_icon)
                    .fit()
                    .into(gasolineImageView);
            gasolineNameTextView.setText(gasoline.getName());
            gasolinePriceTextView.setText(priceText);
            gasolineWeightTextView.setText(weightText);
            if (gasoline.getStock() <= 0) {
                gasolineCardContainer.setBackgroundResource(R.drawable.out_of_stock_background);
                gasolineStockTextView.setTextColor(Color.parseColor("#E94646")); // RED
                gasolineStockTextView.setText(R.string.gasoline_out_of_stock);
            } else {
                gasolineStockTextView.setText(stockText);
                itemView.setOnClickListener(v -> {
                    if (listener == null || getAdapterPosition() == RecyclerView.NO_POSITION) return;
                    listener.onClick(gasoline);
                });
            }
        }
    }
}
