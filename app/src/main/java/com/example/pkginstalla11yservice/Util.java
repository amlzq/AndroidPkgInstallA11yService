package com.example.pkginstalla11yservice;

import android.util.Log;

public class Util {

    public static void print(String msg) {
        if (BuildConfig.DEBUG || BuildConfig.BUILD_TYPE == "debug") {
            Log.d("", msg);
        }
    }

}
