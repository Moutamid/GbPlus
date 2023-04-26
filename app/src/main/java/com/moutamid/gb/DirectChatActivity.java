package com.moutamid.gb;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.moutamid.gb.databinding.ActivityDirectChatBinding;
import com.moutamid.gb.utilis.Constants;

public class DirectChatActivity extends AppCompatActivity {
    ActivityDirectChatBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDirectChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Constants.calledIniti(this);
        Constants.loadIntersAD(this, this);
        Constants.showBannerAdd(this, binding.adView);
        binding.goback.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.sned.setOnClickListener(v -> {
            if (binding.mobileNumber.getEditText().getText().toString().isEmpty()){
                Toast.makeText(this, "Please add a number", Toast.LENGTH_SHORT).show();
            } else {
                String number = binding.countryPick.getSelectedCountryCode().toString() + binding.mobileNumber.getEditText().getText().toString();
                String message = binding.message.getText().toString();
                openWhatsApp(number, message);
            }
        });

    }

    private void openWhatsApp(String smsNumber, String text) {
        Log.d(TAG, "openWhatsApp: smsNumber: " + smsNumber);
        Log.d(TAG, "openWhatsApp: text: " + text);

        startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(String.format("https://api.whatsapp.com/send?phone=%s&text=%s",
                        smsNumber, text))));
    }
}