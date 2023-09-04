package com.aforteza.androidactionsapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private Button btnCheckForUpdates;
    private final String APK_DOWNLOAD_URL = "https://github.com/Cookie-Cracker/AndroidActionsApp/releases/download/v.2.0/app-debug.apk";
    private final String TAG = "VERSION";
    private FileDownloadManager fileDownloadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnCheckForUpdates = findViewById(R.id.btnCheckForUpdates);
        fileDownloadManager = new FileDownloadManager();
        btnCheckForUpdates.setOnClickListener(view -> checkForUpdate());
    }

    private void checkForUpdate() {
        // Create a Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GitHubApi gitHubApi = retrofit.create(GitHubApi.class);

        Call<ReleaseResponse> call = gitHubApi.getLatestRelease("Cookie-Cracker", "AndroidActionsApp");

        call.enqueue(new Callback<ReleaseResponse>() {
            @Override
            public void onResponse(Call<ReleaseResponse> call, Response<ReleaseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ReleaseResponse release = response.body();

                    int latestVersionCode = release.getReleaseBody();
                    int currentVersionCode = BuildConfig.VERSION_CODE;

                    Log.i(TAG, "Latest Version Code: " + latestVersionCode);
                    Log.i(TAG, "Current Version Code: " + currentVersionCode);

                    if (latestVersionCode > currentVersionCode) {
                        // An update is available; show the update dialog
                        showUpdateAvailableDialog(release.getHtmlUrl(), release.getReleaseBody());
                    } else {
                        // No update available
                        showToast("Your app is up to date.");
                    }
                } else {
                    // Handle the case where the HTTP request is not successful
                    showToast("Failed to check for updates. Please try again later.");
                }
            }

            @Override
            public void onFailure(Call<ReleaseResponse> call, Throwable t) {
                // Handle network errors
                showToast("Network error occurred. Please check your internet connection.");
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showUpdateAvailableDialog(final String updateUrl, Integer releaseNotes) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Available");
        builder.setMessage("A new version of the app is available. Do you want to update?\n\nRelease Notes:\n" + releaseNotes);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadAndInstallApk(APK_DOWNLOAD_URL);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void downloadAndInstallApk(String apkDownloadUrl) {
        // Get the cache directory
        File cacheDir = getExternalCacheDir();
        Log.i(TAG, "cacheDir: " + cacheDir);

        // Create the "Downloads" directory if it doesn't exist
        File downloadsDir = new File(cacheDir, "Downloads");
        Log.i(TAG, "downloadsDir: " + downloadsDir);

        if (!downloadsDir.exists()) {
            Log.i(TAG, "Created the directory");

            downloadsDir.mkdirs(); // Create the directory and any necessary parent directories
        }

        // Now, specify the full path for the APK file
        File apkFile = new File(downloadsDir, "app-debug.apk");

        // Log the file path for debugging
        Log.d(TAG, "APK File Path: " + apkFile.getAbsolutePath());

        // Continue with the download as before
        fileDownloadManager.downLoadFile(apkDownloadUrl, new FileDownloadCallback() {
            @Override
            public void onDownloadComplete(File file) {
                // Check if the file exists before attempting to install
                if (file.exists()) {
                    // Install the downloaded APK
                    Log.i(TAG, "onDownloadComplete: file.exists()" + file.exists());
                    installApk(file);
                } else {
                    Log.i(TAG, "onDownloadComplete: file.exists()" + file.exists());

                    showToast("APK file not found.");
                }
            }

            @Override
            public void onDownloadFailed() {
                showToast("Failed to download the APK.");
            }

            @Override
            public File getCacheDir() {
                return cacheDir;
            }
        });
    }

    private void installApk(File file) {
        try {
            if (file.exists()) {
                Uri apkUri = FileProvider.getUriForFile(
                        this,
                        BuildConfig.APPLICATION_ID + ".fileprovider",
                        file);
                Log.i(TAG, "installApk: APK URI" + apkUri);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                if (getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
                    startActivity(intent);
                } else {
                    showToast("No app available to handle the installation.");
                }
            } else {
                showToast("APK file not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
