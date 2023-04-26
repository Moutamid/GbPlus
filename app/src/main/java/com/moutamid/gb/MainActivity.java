package com.moutamid.gb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.facebook.ads.AudienceNetworkAds;
import com.moutamid.gb.databinding.ActivityMainBinding;
import com.moutamid.gb.utilis.Constants;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Constants.checkApp(this);
        AudienceNetworkAds.initialize(this);
        Constants.calledIniti(this);
        Constants.loadIntersAD(this, this);
        Constants.showBannerAdd(this, binding.adView);

//        AdView adView = new AdView(this, getResources().getString(R.string.Banner_ID), AdSize.BANNER_HEIGHT_50);
//        binding.adView.addView(adView);
//        adView.loadAd();

        binding.directChat.setOnClickListener(v -> {
            startActivity(new Intent(this, DirectChatActivity.class));
        });

        binding.textRepeat.setOnClickListener(v -> {
            startActivity(new Intent(this, TextRepeatActivity.class));
        });

        binding.blank.setOnClickListener(v -> {
            startActivity(new Intent(this, BlankMessageActivity.class));
        });

        binding.qrCodeScanner.setOnClickListener(v -> {
            startActivity(new Intent(this, QRScannerActivity.class));
        });

        binding.qrGener.setOnClickListener(v -> {
            startActivity(new Intent(this, QRGeneratorActivity.class));
        });

        binding.textEmoji.setOnClickListener(v -> {
            startActivity(new Intent(this, TextToEmojiActivity.class));
        });

        binding.statusSaver.setOnClickListener(v -> {
            startActivity(new Intent(this, StatusSaverActivity.class));
        });

    }
}