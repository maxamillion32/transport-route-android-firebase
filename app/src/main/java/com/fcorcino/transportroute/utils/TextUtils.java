package com.fcorcino.transportroute.utils;

/**
 * Created by frederick on 1/12/2017.
 */
public class TextUtils {
    public static String decodeEmail(String email) {
        return email.replace(".", ",");
    }

    public static String encodeEmail(String email) {
        return email.replace(",", ".");
    }
}
