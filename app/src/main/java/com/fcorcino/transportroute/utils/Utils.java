package com.fcorcino.transportroute.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class Utils {

    /**
     * This method displays a toast in the window.
     *
     * @param context to display the toast.
     * @param message to be displayed in the toast.
     */
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * This method sets a shared preference value.
     *
     * @param context the context to get the SharedPreferences object.
     * @param key     the key of the shared preference.
     * @param value   the value of the shared preference.
     */
    public static void setSharedPreference(Context context, String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * This method gets a shared preference value.
     *
     * @param context the context to get the SharedPreferences object.
     * @param key     the key of the shared preference.
     */
    public static String getSharedPreference(Context context, String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(key, null);
    }

    /**
     * This method cleans up the shared preferences values.
     *
     * @param context the context to get the SharedPreferences object.
     */
    public static void cleanUpPreferences(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(Constants.SHARED_PREF_USER_ID_KEY);
        editor.remove(Constants.SHARED_PREF_USER_NAME_KEY);
        editor.remove(Constants.SHARED_PREF_USER_TYPE_KEY);
        editor.remove(Constants.SHARED_PREF_USER_TURN_ID_KEY);
        editor.commit();
    }

    /**
     * This method hides the soft keyboard.
     *
     * @param activity the activity displayed.
     */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) view = new View(activity);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
