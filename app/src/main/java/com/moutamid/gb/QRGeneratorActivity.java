package com.moutamid.gb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.moutamid.gb.databinding.ActivityQrgeneratorBinding;
import com.moutamid.gb.utilis.Constants;

import java.io.File;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;

public class QRGeneratorActivity extends AppCompatActivity {
    ActivityQrgeneratorBinding binding;
    private String savePath;
    private Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQrgeneratorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Constants.calledIniti(this);
        Constants.loadIntersAD(this, this);
        Constants.showBannerAdd(this, binding.adView);
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/QrCode/");
        savePath = file.getPath();

        binding.goback.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.repeatbtn.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(QRGeneratorActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                if (!binding.message.getText().toString().isEmpty()){
                    WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
                    Display display = manager.getDefaultDisplay();
                    Point point = new Point();
                    display.getSize(point);
                    int width = point.x;
                    int height = point.y;
                    int smallerDimension = Math.min(width, height);
                    smallerDimension = smallerDimension * 3 / 4;
                    // Initializing the QR Encoder with your value to be encoded, type you required and Dimension
                    QRGEncoder qrgEncoder = new QRGEncoder(binding.message.getText().toString(), null, QRGContents.Type.TEXT, smallerDimension);
                    qrgEncoder.setColorBlack(getResources().getColor(R.color.dark_header));
                    qrgEncoder.setColorWhite(getResources().getColor(R.color.dark_bg));
                    // Getting QR-Code as Bitmap
                    bitmap = qrgEncoder.getBitmap();
                    // Setting Bitmap to ImageView
                    binding.image.setImageBitmap(bitmap);
                } else {
                    Toast.makeText(this, "Add some message", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.save.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                try {
                    boolean save = new QRGSaver().save(savePath, binding.message.getText().toString().trim(), bitmap, QRGContents.ImageType.IMAGE_JPEG);
                    String result = save ? "Image Saved" : "Image Not Saved";
                    Toast.makeText(QRGeneratorActivity.this, result, Toast.LENGTH_LONG).show();
                    binding.message.setText(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                ActivityCompat.requestPermissions(QRGeneratorActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
        });

    }
}