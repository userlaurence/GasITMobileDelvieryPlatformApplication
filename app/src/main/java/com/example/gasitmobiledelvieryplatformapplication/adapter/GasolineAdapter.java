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
import java.util.Locale;

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

        String nameAndWeightText = String.format(Locale.getDefault(), "%s (%.02f kgs)",
                gasoline.getName(), gasoline.getWeight());
        String priceText = Formatter.formatMoneyWithPesoSign(gasoline.getPrice());
        String stockText = String.valueOf(gasoline.getStock());

        Picasso.get()
                .load(gasoline.getImageUrl())
                .placeholder(R.drawable.splash_screen_icon)
                .fit()
                .into(holder.gasolineImageView);
        holder.gasolineNameAndWeightTextView.setText(nameAndWeightText);
        holder.gasolinePriceTextView.setText(priceText);
        if (gasoline.getStock() <= 0) {
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
        TextView gasolineNameAndWeightTextView, gasolinePriceTextView, gasolineStockTextView;

        public GasolineViewHolder(@NonNull View itemView) {
            super(itemView);
            gasolineImageView = itemView.findViewById(R.id.gasolineImageView);
            gasolineNameAndWeightTextView = itemView.findViewById(R.id.gasolineNameAndWeightTextView);
            gasolinePriceTextView = itemView.findViewById(R.id.gasolinePriceTextView);
            gasolineStockTextView = itemView.findViewById(R.id.gasolineStockTextView);
        }
    }
}
