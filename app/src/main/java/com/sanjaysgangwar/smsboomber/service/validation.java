package com.sanjaysgangwar.smsboomber.service;

import android.text.TextUtils;

import com.google.android.material.textfield.TextInputEditText;

import java.util.regex.Pattern;

public class validation {
    public static final Boolean isValidText(String text, TextInputEditText editText) {
        if (TextUtils.isEmpty(text)) {
            editText.requestFocus();
            editText.setError("Mandatory");
            return false;
        }
        return true;
    }

    public static final Boolean isValidPhone(String mobile, TextInputEditText editText) {
        if (TextUtils.isEmpty(mobile)) {
            editText.requestFocus();
            editText.setError("Enter your mobile number");
            return false;
        } else if (Pattern.matches("[a-zA-Z]+", mobile)) {
            editText.requestFocus();
            editText.setError("Mobile no. contains digits only");
            return false;
        }
        return true;
    }
}
