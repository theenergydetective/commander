/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.util;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;


/**
 * Created by pete on 8/25/2014.
 */
public class Validator {


    public static boolean isValidEmail(String value) {
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.(?:[a-zA-Z]{2,20})$";

        boolean valid = false;

        if (value.getClass().toString().equals(String.class.toString())) {
            valid = ((String) value).matches(emailPattern);
        } else {
            valid = ((Object) value).toString().matches(emailPattern);
        }

        return valid;

    }

    public static boolean isValidPassword(String value) {
        if (value.trim().length() < 8) return false;
        RegExp regExp = RegExp.compile("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,20})");
        MatchResult matcher = regExp.exec(value);
        return matcher != null;
    }
}
