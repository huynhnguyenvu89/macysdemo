package com.example.vuhuyhn.macysapp.util;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.vuhuyhn.macysapp.MLog;
import com.example.vuhuyhn.macysapp.MacysApplication;
import com.example.vuhuyhn.macysapp.R;
import com.example.vuhuyhn.macysapp.model.FileWrapperObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.vuhuyhn.macysapp.task.ScanAllFilesTask.AVERAGE_SIZE_KEY;
import static com.example.vuhuyhn.macysapp.task.ScanAllFilesTask.FREQUEST_EXTENSIONS_KEY;
import static com.example.vuhuyhn.macysapp.task.ScanAllFilesTask.LARGEST_FILES_KEY;
import static com.example.vuhuyhn.macysapp.task.ScanAllFilesTask.TOTAL_FILES_KEY;

/**
 * Util class
 */
public class MUtils {
    private static NumberFormat numberFormat = new DecimalFormat("#0.00");

    public static void slideUp(View view) {
        if (view == null)
            return;
        Animation animationUp = AnimationUtils.loadAnimation(MacysApplication.get(),
                R.anim.slide_up);
        view.startAnimation(animationUp);
    }

    public static String getStringFromHashMapDemo(HashMap<String, Integer> map) {
        if (map == null)
            return null;

        StringBuffer stringBuffer = new StringBuffer();
        int i = 0;

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            stringBuffer.append("\n" + (++i) + ". " + entry.getKey() + " - frequency: " + entry.getValue());
        }

        return stringBuffer.toString();
    }

    public static String getStringFromFileListDemo(ArrayList<?> list) {
        if (list == null)
            return null;

        StringBuffer stringBuffer = new StringBuffer();

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) instanceof FileWrapperObject) {
                FileWrapperObject file = (FileWrapperObject) list.get(i);
                stringBuffer.append("\n" + (i + 1) + ". " + file.getName() + " - " + convertByteToMegabyte(file.getLength()) + " MB");
            } else if (list.get(i) instanceof String) {
                String string = (String) list.get(i);
                stringBuffer.append("\n" + (i + 1) + ". " + string);
            }
        }

        return stringBuffer.toString();
    }

    public static String getDemoDataFromBundle(Bundle bundle) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Complete scanning " +
                bundle.getInt(TOTAL_FILES_KEY, 0) + " files" + "\n");
        stringBuffer.append("Average file size: " +
                MUtils.convertByteToMegabyte(bundle.getDouble(AVERAGE_SIZE_KEY, 0.0)) +
                " MB" + "\n");
        stringBuffer.append("Name and size of 10 largest files: " +
                getStringFromFileListDemo(bundle.getParcelableArrayList(LARGEST_FILES_KEY)) + "\n");
        stringBuffer.append("The 5 most frequent file extensions: " +
                getStringFromHashMapDemo((HashMap<String, Integer>)
                        bundle.getSerializable(FREQUEST_EXTENSIONS_KEY)) + "\n");

        return stringBuffer.toString();
    }

    public static String convertByteToMegabyte(double bytes) {
        return numberFormat.format(bytes / 1024 / 1024);
    }
}
