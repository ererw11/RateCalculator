package com.eemery.android.ratecalculator;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Utils {

    public static final String ARG_CALC_TAG =
            "com.eemery.android.ratecalculator.calc";

    // Format doubles as currency and return a double
    public static double formatDoubleAsCurrency(double number) {
        NumberFormat format = new DecimalFormat("###.00");
        return Double.parseDouble(format.format(number));
    }

    // Format doubles as currency and return a string
    public static String formatDoubleAsCurrencyString(double number) {
        NumberFormat format = new DecimalFormat("###.00");
        return format.format(number);
    }

    // Format doubles as percentages and return a double
    public static double formatDoublePercentage(double number) {
        NumberFormat format = new DecimalFormat("#.000");
        return Double.parseDouble(format.format(number));
    }
}
