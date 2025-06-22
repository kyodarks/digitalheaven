package com.example.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class DoubleToCop {
    private static NumberFormat formatter = NumberFormat.getCurrencyInstance(
        Locale.forLanguageTag("es-CO"));

    public static String get(double input){
        return formatter.format(input);
    }
}
