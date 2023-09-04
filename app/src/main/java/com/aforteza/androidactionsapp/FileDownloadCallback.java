package com.aforteza.androidactionsapp;

import java.io.File;

public interface FileDownloadCallback {

    void onDownloadComplete(File file);
    void onDownloadFailed();
    File getCacheDir();
}
