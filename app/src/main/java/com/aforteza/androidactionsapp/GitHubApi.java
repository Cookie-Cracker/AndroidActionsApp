package com.aforteza.androidactionsapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GitHubApi {
    @GET("/repos/{owner}/{repo}/releases/latest")
    Call<ReleaseResponse> getLatestRelease(
            @Path("owner") String owner,
            @Path("repo") String repo
    );
}
