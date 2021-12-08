package com.example.gasitmobiledelvieryplatformapplication.layout;

import android.content.Context;
import android.graphics.Typeface;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.gasitmobiledelvieryplatformapplication.R;
import com.example.gasitmobiledelvieryplatformapplication.model.Sales;
import com.example.gasitmobiledelvieryplatformapplication.util.Formatter;

import java.util.HashMap;

public class SalesTableLayout extends TableLayout {
    private final HashMap<String, Sales> salesMap;
    private final static String[] headers = {"Gasoline", "Sold", "Stocks", "Sales"};

    public SalesTableLayout(Context context) {
        super(context);
        initLayout();
        generateHeader();
        salesMap = new HashMap<>();
        clearItems();
    }

    private void initLayout() {
        setLayoutParams(new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setStretchAllColumns(true);
        setScrollContainer(true);
    }

    public void addTableItem(Sales tableItem, int stocks) {
        boolean alreadyInMap = salesMap.containsKey(tableItem.getGasoline());
        Sales mapSalesItem;

        if (alreadyInMap) {
            mapSalesItem = salesMap.get(tableItem.getGasoline());
            if (mapSalesItem != null) {
                mapSalesItem.setQuantitySold(tableItem.getQuantitySold());
                mapSalesItem.setSales(tableItem.getSales());
                setSalesItem(mapSalesItem);
            }
        } else {
            mapSalesItem = tableItem;
            mapSalesItem.setIndex(getChildCount());
            addSalesItem(tableItem, stocks);
        }

        salesMap.put(tableItem.getGasoline(), mapSalesItem);
    }

    public void clearItems() {
        removeAllViewsInLayout();
        generateHeader();
        salesMap.clear();
    }

    public void onEmptySales() {
        removeAllViewsInLayout();
        addView(createRow(new String[]{"No record has been found."}, false), 0);
    }

    private void setSalesItem(Sales mapSalesItem) {
        final int INDEX_SOLD = 1;
        final int INDEX_SALES = 3;
        TableRow tableRow = (TableRow) getChildAt(mapSalesItem.getIndex());
        ((TextView) tableRow.getChildAt(INDEX_SOLD))
                .setText(String.valueOf(mapSalesItem.getQuantitySold()));
        ((TextView) tableRow.getChildAt(INDEX_SALES))
                .setText(Formatter.formatMoneyWithPesoSign(mapSalesItem.getSales()));
    }

    private void addSalesItem(Sales salesItem, int stocks) {
        String[] columnTexts = new String[] {
                salesItem.getGasoline(),
                String.valueOf(salesItem.getQuantitySold()),
                String.valueOf(stocks),
                Formatter.formatMoneyWithPesoSign(salesItem.getSales())
        };

        addView(createRow(columnTexts, false));
    }

    private void generateHeader() {
        addView(createRow(headers, true));
    }

    private TableRow createRow(String[] columnTexts, boolean isHeader) {
        TableRow tableRow = new TableRow(getContext());

        for (String columnText : columnTexts)
            tableRow.addView(createColumn(columnText, isHeader));

        return tableRow;
    }

    private TextView createColumn(String text, boolean isHeader) {
        TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setTextColor(getResources().getColor(R.color.background, null));
        textView.setTextSize(20);
        textView.setTypeface(null, isHeader ? Typeface.BOLD : Typeface.NORMAL);
        return textView;
    }
}
