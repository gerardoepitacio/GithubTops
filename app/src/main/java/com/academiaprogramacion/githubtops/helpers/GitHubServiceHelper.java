package com.academiaprogramacion.githubtops.helpers;

import com.academiaprogramacion.githubtops.services.GitHub;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class GitHubServiceHelper {
    public static final String API_URL = "https://api.github.com";
    public static final int DEFAULT_PAGE_ITEMS = 15;

    /**
     * Create an instance of our GitHub API interface.
     * @return
     */
    public static GitHub getGitHubClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(GitHub.class);
    }

    public static String getLanguageParameter( String text) {
        return "language:" + text;
    }

}

