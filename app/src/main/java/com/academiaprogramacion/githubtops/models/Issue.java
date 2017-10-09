package com.academiaprogramacion.githubtops.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Issue {
    @SerializedName("number")
    @Expose
    Integer mNumber;

    @SerializedName("title")
    @Expose
    String mTitle;

    @SerializedName("state")
    @Expose
    String mState;

    @SerializedName("user")
    @Expose
    User mUser;

    public Integer getNumber() {
        return mNumber;
    }

    public void setNumber(Integer number) {
        mNumber = number;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getState() {
        return mState;
    }

    public void setState(String state) {
        mState = state;
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
    }

    @Override
    public String toString() {
        return "Issue{" +
                "mNumber='" + mNumber + '\'' +
                ", mTitle='" + mTitle + '\'' +
                ", mState=" + mState +
                ", mUser=" + mUser +
                '}';
    }
}
