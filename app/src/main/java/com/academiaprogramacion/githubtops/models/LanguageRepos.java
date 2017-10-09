package com.academiaprogramacion.githubtops.models;

import java.util.List;

public class LanguageRepos {
    public final int total_count;
    public final boolean incomplete_results;
    public final List<Repository> items;

    public LanguageRepos(int total_count, boolean incomplete_results, List<Repository> items) {
        this.total_count = total_count;
        this.incomplete_results = incomplete_results;
        this.items = items;
    }
}