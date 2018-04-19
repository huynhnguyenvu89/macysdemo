package com.example.vuhuyhn.macysapp.task;


import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Pair;

import com.example.vuhuyhn.macysapp.MLog;
import com.example.vuhuyhn.macysapp.listener.MainActivityListener;
import com.example.vuhuyhn.macysapp.model.FileWrapperObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;

public class ScanAllFilesTask extends AsyncTask<Void, Integer, ArrayList<FileWrapperObject>> {
    private File root;
    private int fileScannedCounter = 0;
    private ArrayList<FileWrapperObject> totalFiles;
    private FileWrapperObject[] largestFilesList;
    private double sizeAllFiles = 0;
    private MainActivityListener presenterListener;
    private final String TAG = ScanAllFilesTask.class.getSimpleName();
    private HashMap<String, Integer> frequencyMap;

    public static final String TOTAL_FILES_KEY = "TOTAL_FILES_KEY";
    public static final String AVERAGE_SIZE_KEY = "AVERAGE_SIZE_KEY";
    public static final String LARGEST_FILES_KEY = "LARGEST_FILES_KEY";
    public static final String FREQUEST_EXTENSIONS_KEY = "FREQUEST_EXTENSIONS_KEY";

    public ScanAllFilesTask(MainActivityListener presenterListener) {
        root = Environment.getExternalStorageDirectory();
        totalFiles = new ArrayList<>();
        largestFilesList = new FileWrapperObject[10];
        this.presenterListener = presenterListener;
        frequencyMap = new HashMap<>();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        presenterListener.onStartScanning();
    }

    @Override
    protected void onPostExecute(ArrayList<FileWrapperObject> fileWrapperObjects) {
        super.onPostExecute(fileWrapperObjects);
        Bundle outputBundle = new Bundle();
        outputBundle.putInt(TOTAL_FILES_KEY, getFileScannedCounter());
        outputBundle.putDouble(AVERAGE_SIZE_KEY, getAverageFileSize());
        outputBundle.putParcelableArrayList(LARGEST_FILES_KEY, getLargestFilesList());
        outputBundle.putSerializable(FREQUEST_EXTENSIONS_KEY, getMostFrequentExtensions());

        presenterListener.onScanComplete(outputBundle);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        presenterListener.onScanUpdate(values[0]);
    }

    @Override
    protected ArrayList<FileWrapperObject> doInBackground(Void... voids) {
        scanAllFilesAndDirectories(root);
        return totalFiles;
    }

    public int getFileScannedCounter() {
        return fileScannedCounter;
    }

    public double getAverageFileSize() {
        return sizeAllFiles / fileScannedCounter;
    }

    public ArrayList<FileWrapperObject> getLargestFilesList() {
        return new ArrayList<>(Arrays.asList(largestFilesList));
    }

    public HashMap<String, Integer> getMostFrequentExtensions() {
        HashMap<String, Integer> mostFrequentExtensionsList = new HashMap<>();
        TreeMap<String, Integer> sortedMap = new TreeMap<>(new ValueComparator(frequencyMap));
        sortedMap.putAll(frequencyMap);

        int i = 0;
        for (Map.Entry<String, Integer> entry : sortedMap.entrySet()) {
            mostFrequentExtensionsList.put(entry.getKey(), entry.getValue());
            i++;
            if (i == 5)
                break;
        }

        return mostFrequentExtensionsList;
    }

    class ValueComparator implements Comparator<String> {
        HashMap<String, Integer> map = new HashMap<String, Integer>();

        public ValueComparator(HashMap<String, Integer> m) {
            map.putAll(m);
        }

        @Override
        public int compare(String i1, String i2) {
            int diff = map.get(i2) - map.get(i1);
            return diff == 0 ? 1 : diff;
        }
    }

    private void scanAllFilesAndDirectories(File root) {
        if (root.isDirectory()) {
            String[] filesAndDirectories = root.list();

            for (String s : filesAndDirectories) {
                File file = new File(root.getAbsolutePath() + "/" + s);
                scanAllFilesAndDirectories(file);
            }
        } else {
            ++fileScannedCounter;
            sizeAllFiles += root.length();
            onProgressUpdate(fileScannedCounter);

            FileWrapperObject fileWrapper = new FileWrapperObject(root.getName(), root.length());
            totalFiles.add(fileWrapper);

            insertToLargestFileList(fileWrapper);
            insertToTopFrequentExtensionMap(fileWrapper.getExtension());
        }
    }

    private void insertToLargestFileList(FileWrapperObject fileObj) {

        double minLargestFileSize = largestFilesList[9] != null ? largestFilesList[9].getLength() : 0;

        double fileObjSize = fileObj.getLength();

        if (fileObjSize <= minLargestFileSize) {
            //Current file size is smaller than the smallest element's size. Ignore.
            return;
        } else {
            //Add to the largest file list
            for (int i = 0; i < 10; i++) {
                FileWrapperObject crrFile = largestFilesList[i];
                double crrFileSize = crrFile != null ? crrFile.getLength() : 0;

                if (crrFileSize == 0) {
                    //Add straight to that index
                    largestFilesList[i] = fileObj;
                    return;
                } else {
                    //If the file at current index is smaller or equals than the given file.
                    if (crrFileSize <= fileObjSize) {
                        //Replace the file at current index, and push the rest back 1 position.
                        FileWrapperObject tmp;
                        FileWrapperObject fileToInsert = fileObj;

                        while (i < 10) {
                            tmp = largestFilesList[i];
                            largestFilesList[i] = fileToInsert;
                            fileToInsert = tmp;
                            i++;
                        }
                        break;
                    }
                }
            }
        }
    }

    private void insertToTopFrequentExtensionMap(String extension) {
        if (extension.equals(FileWrapperObject.NO_EXTENSION_FOUND))
            return;
        if (frequencyMap.containsKey(extension)) {
            frequencyMap.put(extension, frequencyMap.get(extension) + 1);
        } else {
            frequencyMap.put(extension, 1);
        }
    }
}
