package com.academiaprogramacion.githubtops.services;


import com.academiaprogramacion.githubtops.models.Contributor;
import com.academiaprogramacion.githubtops.models.LanguageRepos;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

import static com.academiaprogramacion.githubtops.helpers.GitHubServiceHelper.DEFAULT_PAGE_ITEMS;

public interface GitHub {
    @Headers({
            "Accept: application/vnd.github.v3+json",
            "User-Agent: GithubTopsApp"
    })
    @GET("/repos/{owner}/{repo}/contributors")
    Call<List<Contributor>> contributors(
            @Path("owner") String owner,
            @Path("repo") String repo);

    @Headers({
            "Accept: application/vnd.github.v3+json",
            "User-Agent: GithubTopsApp"
    })
    @GET("/search/repositories?sort=stars&order=desc&per_page=" + DEFAULT_PAGE_ITEMS)
    Call<LanguageRepos> getLanguageRepos(@Query("page") int page,@Query("q") String language);

}
