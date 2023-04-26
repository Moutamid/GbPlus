package com.moutamid.gb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;
import com.moutamid.gb.databinding.ActivityQrscannerBinding;
import com.moutamid.gb.utilis.Constants;

public class QRScannerActivity extends AppCompatActivity {
    ActivityQrscannerBinding binding;
    private CodeScanner mCodeScanner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQrscannerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Constants.calledIniti(this);
        Constants.loadIntersAD(this, this);
        Constants.showBannerAdd(this, binding.adView);
        binding.goback.setOnClickListener(v -> {
            onBackPressed();
        });

        mCodeScanner = new CodeScanner(this, binding.scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.result.setText(result.getText());
                    }
                });
            }
        });

        binding.repeatbtn.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(QRScannerActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
            } else {
                mCodeScanner.startPreview();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCodeScanner.stopPreview();
    }

}