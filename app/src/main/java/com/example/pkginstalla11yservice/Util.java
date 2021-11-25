package com.example.pkginstalla11yservice;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class Util {

    public static void print(String msg) {
        if (BuildConfig.DEBUG || BuildConfig.BUILD_TYPE == "debug") {
            Log.d("", msg); // Tag can be customized
        }
    }

    public static void error(String msg) {
        if (BuildConfig.DEBUG || BuildConfig.BUILD_TYPE == "debug") {
            Log.e("", msg); // Tag can be customized
        }
    }

    static final String PREFS_NAME = "phone_user";
    static final String KEY_PASSWORD = "password";

    public static void savePassword(Context context, String password) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_PASSWORD, password);
        editor.apply();
    }

    public static String getPassword(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_PASSWORD, "");
    }

    public static void editClearFocus(EditText editText) {
        editText.setText("");
        editText.clearFocus();
        InputMethodManager manager = (InputMethodManager) editText.getContext().getSystemService(INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
