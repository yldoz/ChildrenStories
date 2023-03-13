package com.example.alamaudioplayer;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Locale;

public class Lang implements Parcelable {
    Locale locale ;

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Lang(Locale locale) {
        this.locale = locale;
    }

    protected Lang(Parcel in) {
    }

    public static final Creator<Lang> CREATOR = new Creator<Lang>() {
        @Override
        public Lang createFromParcel(Parcel in) {
            return new Lang(in);
        }

        @Override
        public Lang[] newArray(int size) {
            return new Lang[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }
}
