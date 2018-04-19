package com.example.vuhuyhn.macysapp;

import android.app.Application;

public class MacysApplication extends Application {

    private static MacysApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        MLog.setLoggable(true);
        instance = this;
    }

    /**
     * Get application level context from anywhere in the app.
     *
     * @return MacysApplication instance.
     */
    public static MacysApplication get() {
        return instance;
    }
}
