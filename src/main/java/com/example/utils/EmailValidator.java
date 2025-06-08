package com.example.utils;

import java.util.regex.Pattern;

public class EmailValidator {
    private static final Pattern emailPattern = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
    );

    public static boolean isValid(String email) {
        return emailPattern.matcher(email).matches();
    }
}
