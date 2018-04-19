package com.example.vuhuyhn.macysapp.presenter;

import android.Manifest;
import android.app.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.example.vuhuyhn.macysapp.MLog;
import com.example.vuhuyhn.macysapp.MacysApplication;
import com.example.vuhuyhn.macysapp.listener.MainActivityListener;
import com.example.vuhuyhn.macysapp.manager.PermissionManager;
import com.example.vuhuyhn.macysapp.task.ScanAllFilesTask;
import com.example.vuhuyhn.macysapp.util.MUtils;

import java.util.HashMap;

import static com.example.vuhuyhn.macysapp.task.ScanAllFilesTask.AVERAGE_SIZE_KEY;
import static com.example.vuhuyhn.macysapp.task.ScanAllFilesTask.FREQUEST_EXTENSIONS_KEY;
import static com.example.vuhuyhn.macysapp.task.ScanAllFilesTask.LARGEST_FILES_KEY;
import static com.example.vuhuyhn.macysapp.task.ScanAllFilesTask.TOTAL_FILES_KEY;
import static com.example.vuhuyhn.macysapp.util.MUtils.getStringFromFileListDemo;
import static com.example.vuhuyhn.macysapp.util.MUtils.getStringFromHashMapDemo;

public class MainActivityPresenter {

    private Activity activity;
    private final String TAG = MainActivityPresenter.class.getSimpleName();
    private MainActivityListener presenterListener;
    private int READ_EXTERNAL_MEMORY_REQUEST = 1;
    private String[] permissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private ScanAllFilesTask scanTask;
    private Bundle lastSavedInstanceState;

    public MainActivityPresenter(Activity activity, MainActivityListener presenterListener) {
        this.activity = activity;
        this.presenterListener = presenterListener;
    }

    public void init(Bundle savedInstanceState) {
        if (!PermissionManager.isReadExternalMemoryGranted()) {
            presenterListener.onMissingPermission();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                activity.requestPermissions(permissions, READ_EXTERNAL_MEMORY_REQUEST);
        } else {
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                presenterListener.onExternalMemoryNotAccessible();
            else {
                if (savedInstanceState == null) {
                    scanTask = new ScanAllFilesTask(presenterListener);
                    scanTask.execute();
                } else {
                    lastSavedInstanceState = savedInstanceState;
                    presenterListener.onScanComplete(savedInstanceState);
                }
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            init(null);
        }
    }

    public Bundle onSaveInstanceState(Bundle outState) {
        if (lastSavedInstanceState != null) {
            outState.putInt(TOTAL_FILES_KEY,
                    lastSavedInstanceState.getInt(TOTAL_FILES_KEY, 0));
            outState.putDouble(AVERAGE_SIZE_KEY,
                    lastSavedInstanceState.getDouble(AVERAGE_SIZE_KEY, 0.0));
            outState.putParcelableArrayList(LARGEST_FILES_KEY,
                    lastSavedInstanceState.getParcelableArrayList(LARGEST_FILES_KEY));
            outState.putSerializable(FREQUEST_EXTENSIONS_KEY, lastSavedInstanceState
                    .getSerializable(FREQUEST_EXTENSIONS_KEY));
            return outState;
        } else {
            outState.putInt(TOTAL_FILES_KEY, scanTask.getFileScannedCounter());
            outState.putDouble(AVERAGE_SIZE_KEY, scanTask.getAverageFileSize());
            outState.putParcelableArrayList(LARGEST_FILES_KEY, scanTask.getLargestFilesList());
            outState.putSerializable(FREQUEST_EXTENSIONS_KEY, scanTask.getMostFrequentExtensions());

            return outState;
        }
    }

    public void onDestroy() {
        if (scanTask == null)
            return;
        scanTask.cancel(true);
    }

    public void onStopScanning() {
        onDestroy();
        presenterListener.onStopScanning();
    }

    public void onShareData() {
        Intent shareIntent = new Intent();
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "MACY'S DEMO");
        shareIntent.setAction(Intent.ACTION_SEND);

        Bundle bundle = onSaveInstanceState(new Bundle());
        shareIntent.putExtra(Intent.EXTRA_TEXT, MUtils.getDemoDataFromBundle(bundle));

        shareIntent.setType("text/plain");

        MacysApplication.get().startActivity(shareIntent);
    }
}
