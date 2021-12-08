package com.example.gasitmobiledelvieryplatformapplication.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gasitmobiledelvieryplatformapplication.R;
import com.example.gasitmobiledelvieryplatformapplication.layout.SalesTableLayout;
import com.example.gasitmobiledelvieryplatformapplication.model.Gasoline;
import com.example.gasitmobiledelvieryplatformapplication.model.ItemRequestCallback;
import com.example.gasitmobiledelvieryplatformapplication.model.ListItemRequestCallback;
import com.example.gasitmobiledelvieryplatformapplication.model.Sales;
import com.example.gasitmobiledelvieryplatformapplication.util.DateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class SalesFragment extends Fragment {
    final int DAY = 0, WEEK = 1, MONTH = 2, YEAR = 3;
    private int dateFormat = DAY;

    private ProgressBar progressBar;
    private Spinner dateFormatSpinner;
    private Button dateButton;
    private SalesTableLayout salesTableLayout;

    public SalesFragment() {
        super(R.layout.fragment_sales);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sales, container, false);

        // These fields should be initialized before calling presenter.initSales().
        progressBar = rootView.findViewById(R.id.progressBar);
        dateFormatSpinner = rootView.findViewById(R.id.dateFormatSpinner);

        dateButton = rootView.findViewById(R.id.dateButton);
        ImageButton previousButton = rootView.findViewById(R.id.previousButton);
        ImageButton nextButton = rootView.findViewById(R.id.nextButton);

        salesTableLayout = new SalesTableLayout(getContext());
        ((ScrollView)rootView.findViewById(R.id.salesTableScrollView)).addView(salesTableLayout);

        long currentDate = System.currentTimeMillis();
        String dateText = DateUtil.toDateTime(currentDate).toString(DateUtil.DISPLAY_DATE_PATTERN);
        dateButton.setText(dateText);

        initDateFormatSpinner();

        dateButton.setOnClickListener(v -> openCalendarDialog());
        previousButton.setOnClickListener(v -> goTo(false));
        nextButton.setOnClickListener(v -> goTo(true));

        return rootView;
    }

    private void onRequestError(String error) {
        progressBar.setVisibility(View.GONE);
        if (getActivity() == null) return;
        Toasty.error(getActivity(), error, Toasty.LENGTH_SHORT).show();
    }

    private void initDateFormatSpinner() {
        ArrayList<String> dateFormatList = new ArrayList<>();
        dateFormatList.add("Day");
        dateFormatList.add("Week");
        dateFormatList.add("Month");
        dateFormatList.add("Year");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getActivity(), R.layout.spinner_item, dateFormatList);

        dateFormatSpinner.setAdapter(adapter);
        dateFormatSpinner.setSelection(DAY);
        dateFormatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dateFormat = position;
                calculateBasedOnFormat(true);
                updateSales();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void openCalendarDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                (datePicker, year1, month1, dayOfMonth) -> {
                    dateButton.setText(String.format("%s/%s/%s", dayOfMonth, month1 + 1, year1));
                    updateSales();
                },
                year,
                month,
                day);

        datePickerDialog.show();
    }

    private void goTo(boolean isNext) {
        dateButton.setText(calculateNewDateStr(isNext));
        updateSales();
    }

    private String calculateNewDateStr(boolean isNext) {
        DateUtil dateUtil = DateUtil.dateToDateTime(dateButton.getText().toString());

        switch (dateFormat) {
            case DAY:
                if (isNext) dateUtil.toNextDay();
                else        dateUtil.toPrevDay();
                break;
            case WEEK:
                if (isNext) dateUtil.toNextWeek();
                else        dateUtil.toPrevWeek();
                break;
            case MONTH:
                if (isNext) dateUtil.toNextMonth();
                else        dateUtil.toPrevMonth();
                break;
            case YEAR:
                if (isNext) dateUtil.toNextYear();
                else        dateUtil.toPrevYear();
                break;
        }

        return dateUtil.toString(DateUtil.DISPLAY_DATE_PATTERN);
    }

    private long calculateBasedOnFormat(boolean isEnd) {
        long dateLong = DateUtil.dateToDateTime(dateButton.getText().toString()).toMills();
        DateUtil dateUtil = DateUtil.toDateTime(dateLong);

        switch (dateFormat) {
            case DAY:
                if (isEnd) dateUtil.toEndOfDay();
                else       dateUtil.toStartOfDay();
                break;
            case WEEK:
                if (isEnd) dateUtil.toEndOfWeek();
                else       dateUtil.toStartOfWeek();
                break;
            case MONTH:
                if (isEnd) dateUtil.toEndOfMonth();
                else       dateUtil.toStartOfMonth();
                break;
            case YEAR:
                if (isEnd) dateUtil.toEndOfYear();
                else       dateUtil.toStartOfYear();
                break;
        }

        return dateUtil.toMills();
    }

    private void updateSales() {
        long timestamp = calculateBasedOnFormat(false);
        long upTo = calculateBasedOnFormat(true);

        salesTableLayout.clearItems();
        progressBar.setVisibility(View.VISIBLE);
        new Sales(timestamp).readAll(upTo, new ListItemRequestCallback<Sales>() {
            @Override
            public void onSuccess(List<Sales> salesList) {
                progressBar.setVisibility(View.GONE);

                if (salesList == null) {
                    salesTableLayout.onEmptySales();
                    return;
                }

                salesList.forEach(sales -> new Gasoline().readOne(
                        sales.getGasolineUid(),
                        new ItemRequestCallback<Gasoline>() {
                            @Override
                            public void onSuccess(Gasoline item) {
                                salesTableLayout.addTableItem(sales, item.getStock());
                            }

                            @Override
                            public void onFailure(String error) {
                                // Do nothing.
                            }
                        }));
            }

            @Override
            public void onFailure(String error) {
                onRequestError(error);
            }
        });
    }
}
