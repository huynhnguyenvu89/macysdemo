package com.example.vuhuyhn.macysapp.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.example.vuhuyhn.macysapp.MLog;

public class FileWrapperObject implements Parcelable {
    public static final String NO_EXTENSION_FOUND = "No Extension Found";
    String name;
    long length;

    public FileWrapperObject(String name, long length) {
        setName(name);
        setLength(length);
    }

    FileWrapperObject(Parcel source) {
        setName(source.readString());
        setLength(source.readLong());
    }

    final Parcelable.Creator<FileWrapperObject> CREATOR
            = new Parcelable.Creator<FileWrapperObject>() {
        @Override
        public FileWrapperObject createFromParcel(Parcel source) {
            return new FileWrapperObject(source);
        }

        @Override
        public FileWrapperObject[] newArray(int size) {
            return new FileWrapperObject[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getExtension() {
        try {
            return name.substring(name.lastIndexOf("."));
        } catch (IndexOutOfBoundsException e) {
            return NO_EXTENSION_FOUND;
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeLong(length);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
