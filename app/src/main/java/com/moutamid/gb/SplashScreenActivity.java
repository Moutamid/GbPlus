package com.moutamid.gb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.moutamid.gb.databinding.ActivitySplashScreenBinding;

public class SplashScreenActivity extends AppCompatActivity {
    ActivitySplashScreenBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
//        finish();
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
            finish();
        }, 2000);

    }
}