package com.steelcolossus.mobiledev.mobileapplicationdevelopmentcoursework;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.webkit.WebView;

public class UserGuideActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_guide);

        WebView webView = findViewById(R.id.webView);
        webView.loadUrl("file:///android_res/raw/user_guide.html");
    }
}
