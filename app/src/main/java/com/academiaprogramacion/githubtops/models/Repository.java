package com.academiaprogramacion.githubtops.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Repository {

    @Expose
    Integer id;

    @Expose
    String name;

    @SerializedName("full_name")
    @Expose
    String fullName;

    @Expose
    User owner;

    @SerializedName("html_url")
    @Expose
    String htmlUrl;

    @Expose
    String description;

    @SerializedName("created_at")
    @Expose
    String createdAt;

    @SerializedName("updated_at")
    @Expose
    String updatedAt;

    @Expose
    String homepage;

    @SerializedName("stargazers_count")
    @Expose
    Integer stargazersCount;

    @Expose
    String language;

    @Expose
    Integer watchers;

    @Expose
    Double score;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    public User getOwner() {
        return owner;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getHomepage() {
        return homepage;
    }

    public Integer getStargazersCount() {
        return stargazersCount;
    }

    public String getLanguage() {
        return language;
    }

    public Integer getWatchers() {
        return watchers;
    }

    public Double getScore() {
        return score;
    }

    @Override
    public String toString() {
        return "Repository{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", fullName='" + fullName + '\'' +
                ", owner=" + owner +
                ", htmlUrl='" + htmlUrl + '\'' +
                ", description='" + description + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", homepage='" + homepage + '\'' +
                ", stargazersCount=" + stargazersCount +
                ", language='" + language + '\'' +
                ", watchers=" + watchers +
                ", score=" + score +
                '}';
    }
}