package com.aforteza.androidactionsapp;
import com.google.gson.annotations.SerializedName;

public class ReleaseResponse {
    @SerializedName("tag_name")
    private String tagName;

    @SerializedName("name")
    private String releaseName;

    @SerializedName("body")
    private String releaseBody;

    @SerializedName("versionCode") // Add this annotation for the versionCode field
    private int versionCode;

    public String getTagName() {
        return tagName;
    }

    public String getReleaseName() {
        return releaseName;
    }

    public String getReleaseBody() {
        return releaseBody;
    }

    public int getVersionCode() {
        return versionCode;
    }


    @SerializedName("html_url")
    private String htmlUrl;
    public String getHtmlUrl() {
        return htmlUrl;
    }
}
