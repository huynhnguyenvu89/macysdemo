package com.example.vuhuyhn.macysapp.listener;

import android.os.Bundle;

import com.example.vuhuyhn.macysapp.presenter.MainActivityPresenter;

import java.util.ArrayList;

public interface MainActivityListener {
    void onMissingPermission(String... permissions);

    void onExternalMemoryNotAccessible();

    void onStartScanning();

    void onStopScanning();

    void onScanUpdate(int inProgress);

    void onScanComplete(Bundle scanResultBundle);
}
