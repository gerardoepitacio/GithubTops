package com.academiaprogramacion.githubtops.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Contributor {
    @SerializedName("login")
    @Expose
    String mUserName;

    @SerializedName("contributions")
    @Expose
    int mContributions;

    @SerializedName("avatar_url")
    @Expose
    String mAvatarUrl;

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public int getContributions() {
        return mContributions;
    }

    public void setContributions(int contributions) {
        mContributions = contributions;
    }

    public String getAvatarUrl() {
        return mAvatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        mAvatarUrl = avatarUrl;
    }

    @Override
    public String toString() {
        return "Contributor{" +
                "mUserName='" + mUserName + '\'' +
                ", mContributions=" + mContributions +
                ", mAvatarUrl='" + mAvatarUrl + '\'' +
                '}';
    }
}