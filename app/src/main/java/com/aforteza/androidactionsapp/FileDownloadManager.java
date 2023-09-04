package com.aforteza.androidactionsapp;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FileDownloadManager {
    private GitHubApi gitHubApi;
    private final String TAG = "FileDownloadManager";

    public FileDownloadManager() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .build();


        this.gitHubApi = retrofit.create(GitHubApi.class);
    }

    public void downLoadFile(String apkDownloadUrl, FileDownloadCallback callback) {
        Log.i("FileDownloadManager", "Starting download: " + apkDownloadUrl);

        Call<ResponseBody> call = gitHubApi.downloadFile(apkDownloadUrl);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    Log.i(TAG, "onResponse: " + response.isSuccessful() );
                    // Download the APK to a file on the device
                    try {
                        File apkFile = new File(callback.getCacheDir(), "/Downloads/app-debug.apk");
                        Log.i(TAG, "apkFile.getAbsolutePath(): " + apkFile.getAbsolutePath());
                        InputStream inputStream = null;
                        OutputStream outputStream = null;

                        try {
                            byte[] fileReader = new byte[4096];
                            long fileSize = response.body().contentLength();
                            long fileSizeDownloaded = 0;

                            inputStream = response.body().byteStream();
                            outputStream = new FileOutputStream(apkFile);

                            while (true) {
                                int read = inputStream.read(fileReader);
                                if (read == -1) {
                                    break;
                                }
                                outputStream.write(fileReader, 0, read);
                                fileSizeDownloaded += read;

                            }

                            Log.i(TAG, "onResponse: outputStream" + outputStream);
                            Log.i(TAG, "onResponse: fileSizeDownloaded" + fileSizeDownloaded);
                            outputStream.flush();
                            callback.onDownloadComplete(apkFile);

                            // Log success
                            Log.i("FileDownloadManager", "Download complete. File size: " + fileSizeDownloaded);
                        } catch (IOException e) {
                            e.printStackTrace();
                            callback.onDownloadFailed();

                            // Log failure
                            Log.e("FileDownloadManager", "Download failed: " + e.getMessage());
                        } finally {
                            if (inputStream != null) {
                                inputStream.close();
                            }
                            if (outputStream != null) {
                                outputStream.close();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        callback.onDownloadFailed();

                        // Log failure
                        Log.e("FileDownloadManager", "Download failed: " + e.getMessage());
                    }
                } else {

                    callback.onDownloadFailed();

                    // Log failure
                    Log.e("FileDownloadManager", "Download failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onDownloadFailed();

                // Log failure
                Log.e("FileDownloadManager", "Download failed: " + t.getMessage());
            }
        });
    }

}
