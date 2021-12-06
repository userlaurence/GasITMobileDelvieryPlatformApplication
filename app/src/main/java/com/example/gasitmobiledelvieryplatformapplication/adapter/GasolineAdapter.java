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
import com.example.gasitmobiledelvieryplatformapplication.models.Gasoline;
import com.example.gasitmobiledelvieryplatformapplication.util.Formatter;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GasolineAdapter extends RecyclerView.Adapter<GasolineAdapter.GasolineViewHolder> {
    private final List<Gasoline> gasolineArrayList;

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
        Gasoline gasoline = gasolineArrayList.get(position);

        String weightText = gasoline.getWeight() + " kgs";
        String priceText = Formatter.formatMoneyWithPesoSign(gasoline.getPrice());
        String stockText = gasoline.getStock() + " left";

        Picasso.get()
                .load(gasoline.getImageUrl())
                .placeholder(R.drawable.splash_screen_icon)
                .fit()
                .into(holder.gasolineImageView);
        holder.gasolineNameTextView.setText(gasoline.getName());
        holder.gasolinePriceTextView.setText(priceText);
        holder.gasolineWeightTextView.setText(weightText);
        if (gasoline.getStock() <= 0) {
            holder.gasolineCardContainer.setBackgroundResource(R.drawable.out_of_stock_background);
            holder.gasolineStockTextView.setTextColor(Color.parseColor("#E94646")); // RED
            holder.gasolineStockTextView.setText(R.string.gasoline_out_of_stock);
        } else {
            holder.gasolineStockTextView.setText(stockText);
        }
    }

    @Override
    public int getItemCount() {
        return gasolineArrayList.size();
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
    }
}
