
package com.example.alamaudioplayer.model;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Rows implements Parcelable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("storyName")
    @Expose
    private String storyName;
    @SerializedName("language")
    @Expose
    private String language;
    @SerializedName("ageFrom")
    @Expose
    private String ageFrom;
    @SerializedName("ageTo")
    @Expose
    private String ageTo;
    @SerializedName("audio")
    @Expose
    private String audio;
    @SerializedName("storypicture")
    @Expose
    private String storypicture;
    @SerializedName("producerID")
    @Expose
    private String producerID;
    @SerializedName("verified")
    @Expose
    private String verified;
    @SerializedName("status")
    @Expose
    private String status;

    public Rows(String id, String storyName, String language, String ageFrom, String ageTo, String audio, String storypicture, String producerID, String verified, String status, String date) {
        this.id = id;
        this.storyName = storyName;
        this.language = language;
        this.ageFrom = ageFrom;
        this.ageTo = ageTo;
        this.audio = audio;
        this.storypicture = storypicture;
        this.producerID = producerID;
        this.verified = verified;
        this.status = status;
        this.date = date;
    }

    @SerializedName("date")
    @Expose
    private String date;

    protected Rows(Parcel in) {
        id = in.readString();
        storyName = in.readString();
        language = in.readString();
        ageFrom = in.readString();
        ageTo = in.readString();
        audio = in.readString();
        storypicture = in.readString();
        producerID = in.readString();
        verified = in.readString();
        status = in.readString();
        date = in.readString();
    }

    public static final Creator<Rows> CREATOR = new Creator<Rows>() {
        @Override
        public Rows createFromParcel(Parcel in) {
            return new Rows(in);
        }

        @Override
        public Rows[] newArray(int size) {
            return new Rows[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStoryName() {
        return storyName;
    }

    public void setStoryName(String storyName) {
        this.storyName = storyName;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getAgeFrom() {
        return ageFrom;
    }

    public void setAgeFrom(String ageFrom) {
        this.ageFrom = ageFrom;
    }

    public String getAgeTo() {
        return ageTo;
    }

    public void setAgeTo(String ageTo) {
        this.ageTo = ageTo;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getStorypicture() {
        return storypicture;
    }

    public void setStorypicture(String storypicture) {
        this.storypicture = storypicture;
    }

    public String getProducerID() {
        return producerID;
    }

    public void setProducerID(String producerID) {
        this.producerID = producerID;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(storyName);
        dest.writeString(language);
        dest.writeString(ageFrom);
        dest.writeString(ageTo);
        dest.writeString(audio);
        dest.writeString(storypicture);
        dest.writeString(producerID);
        dest.writeString(verified);
        dest.writeString(status);
        dest.writeString(date);
    }
}
