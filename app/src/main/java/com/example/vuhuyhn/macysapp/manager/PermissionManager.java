package com.example.vuhuyhn.macysapp.manager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.PermissionChecker;

import com.example.vuhuyhn.macysapp.MacysApplication;

public class PermissionManager {

    public static boolean isReadExternalMemoryGranted() {
        return isPermissionsGranted(Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    public static boolean isPermissionsGranted(String... permissions) {
        for (String s : permissions) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!(MacysApplication.get().checkSelfPermission(s)
                        == PackageManager.PERMISSION_GRANTED))
                    return false;
            } else {
                if (!(PermissionChecker.checkSelfPermission(MacysApplication.get(), s)
                        == PackageManager.PERMISSION_GRANTED))
                    return false;
            }
        }

        return true;
    }
}

