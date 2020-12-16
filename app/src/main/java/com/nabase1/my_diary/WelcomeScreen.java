package com.nabase1.my_diary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class WelcomeScreen extends AppCompatActivity {
    private static int splash_time = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        new Handler().postDelayed(() -> {
            Intent mapIntent = new Intent(WelcomeScreen.this, LockScreen.class);
            startActivity(mapIntent);
            finish();
        },splash_time);
    }
}