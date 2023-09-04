package com.aforteza.androidactionsapp;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface GitHubApi {
    @GET("/repos/{owner}/{repo}/releases/latest")
    Call<ReleaseResponse> getLatestRelease(
            @Path("owner") String owner,
            @Path("repo") String repo
    );
    @GET
    Call<ResponseBody> downloadFile(@Url String fileUrl);
}
