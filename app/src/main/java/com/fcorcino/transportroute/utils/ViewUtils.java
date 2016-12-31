package com.fcorcino.transportroute.utils;

import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.widget.EditText;

public class ViewUtils {

    /**
     * This method gets the trimmed text from an edit text.
     *
     * @param editText the edit text to get the text from.
     * @return the trim text.
     */
    public static String getTextFromEditText(EditText editText) {
        return editText.getText().toString().trim();
    }

    /**
     * This method validates a required edit text.
     *
     * @param editText     the edit text to be validated.
     * @param errorMessage the error message to be set in case the edit text is not valid.
     * @return true if it is valid or false otherwise.
     */
    public static boolean validateEditText(EditText editText, String errorMessage) {
        TextInputLayout textInputLayout = (TextInputLayout) editText.getParent();

        if (TextUtils.isEmpty(getTextFromEditText(editText))) {
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError(errorMessage);
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
            return true;
        }
    }
}
