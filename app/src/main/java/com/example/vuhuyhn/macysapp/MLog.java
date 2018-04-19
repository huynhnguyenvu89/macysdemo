package com.example.vuhuyhn.macysapp;

import android.util.Log;

public class MLog {

    private static boolean isLoggable = true;

    protected static void setLoggable(boolean isLoggable) {
        MLog.isLoggable = isLoggable;
    }

    public static void d(String tag, String msg) {
        if (isLoggable) {
            Log.d(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (isLoggable) {
            Log.e(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (isLoggable) {
            Log.w(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (isLoggable) {
            Log.i(tag, msg);
        }
    }

    public static void wtf(String tag, String msg) {
        if (isLoggable) {
            Log.wtf(tag, msg);
        }
    }
}
