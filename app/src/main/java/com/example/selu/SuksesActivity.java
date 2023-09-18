package com.example.selu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

public class SuksesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sukses);

        // Wait for 3 seconds and then redirect to the website link
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                redirectToWebsite();
            }
        }, 3000); // 3000 milliseconds = 3 seconds
    }

    private void redirectToWebsite() {
        String websiteUrl = "https://bit.ly/Selucrips"; // Replace this with your desired website URL
        Uri uri = Uri.parse(websiteUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

        // Close the current activity to prevent going back to it when pressing the back button from the website
        finish();
    }
}
