package com.ming.journalapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class Journal implements Parcelable {
    private String title;
    private String desc;
    private String userId;
    private String imageUrl;
    private String timeAdded;
    private String userName;
    private static String journalId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(String timeAdded) {
        this.timeAdded = timeAdded;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    protected Journal(Parcel in) {
        title = in.readString();
        desc = in.readString();
        userId = in.readString();
        imageUrl = in.readString();
        timeAdded = in.readString();
        userName = in.readString();
        journalId = in.readString();
    }

    public Journal() {
    }

    public Journal(String title, String desc, String userId, String imageUrl, String timeAdded, String userName) {
        this.title = title;
        this.desc = desc;
        this.imageUrl = imageUrl;
        this.timeAdded = timeAdded;
        this.userName = userName;
        this.userId = userId;
    }

    public static final Creator<Journal> CREATOR = new Creator<Journal>() {
        @Override
        public Journal createFromParcel(Parcel in) {
            return new Journal(in);
        }

        @Override
        public Journal[] newArray(int size) {
            return new Journal[size];
        }
    };

    public Map<String, Object> toMap() {
        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("desc", desc);
        data.put("imageUrl", imageUrl);
        data.put("timeAdded", timeAdded);
        data.put("userId",userId);
        data.put("userName", userName);
        return data;
    }

    public String getJournalId() {
        return journalId;
    }

    public void setJournalId(String journalId) {
        this.journalId = journalId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(desc);
        parcel.writeString(userId);
        parcel.writeString(imageUrl);
        parcel.writeString(timeAdded);
        parcel.writeString(userName);
        parcel.writeString(journalId);
    }
}
