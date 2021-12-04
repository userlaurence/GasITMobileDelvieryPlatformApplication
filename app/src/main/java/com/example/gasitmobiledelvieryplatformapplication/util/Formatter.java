package com.example.gasitmobiledelvieryplatformapplication.util;

import java.util.Locale;

public class Formatter {
    private static float CENTS_PLACE = 100.0f; // 2 decimal places.

    public static float formatMoneyForDisplay(int money) {
        return money / CENTS_PLACE;
    }

    public static int formatMoneyForDB(float money) {
        return (int) (money * CENTS_PLACE);
    }

    public static String formatMoneyWithPesoSign(int money) {
        return String.format(Locale.getDefault(), "₱%.02f", formatMoneyForDisplay(money));
    }
}
