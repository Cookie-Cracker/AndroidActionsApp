package com.aforteza.androidactionsapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.BuildCompat;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;





public class MainActivity extends AppCompatActivity {

    private Button btnCheckForUpdates;
    private final String TAG = "VERSION";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCheckForUpdates = findViewById(R.id.btnCheckForUpdates);

        btnCheckForUpdates.setOnClickListener(view -> {

            checkForUpdate();
        });



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

                    int latestVersionCode = release.getVersionCode();
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
    // Use string resources for messages
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Display release notes in the update dialog
    private void showUpdateAvailableDialog(final String updateUrl, String releaseNotes) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Available");
        builder.setMessage("A new version of the app is available. Do you want to update?\n\nRelease Notes:\n" + releaseNotes);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Open the update URL in a web browser or a WebView
                openPlayStoreForUpdates(updateUrl);
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

    // Method to open the Play Store for updates
// Method to open the Play Store for updates
    private void openPlayStoreForUpdates(String updateUrl) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Handle the case where the Play Store app is not installed or cannot be opened
            // You can provide an alternative way for the user to update the app
            showToast("Google Play Store not found on this device.");
        }
    }

}