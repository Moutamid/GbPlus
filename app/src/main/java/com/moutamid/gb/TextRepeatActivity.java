package com.moutamid.gb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.moutamid.gb.databinding.ActivityTextRepeatBinding;
import com.moutamid.gb.utilis.Constants;

public class TextRepeatActivity extends AppCompatActivity {
    ActivityTextRepeatBinding binding;
    String s = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTextRepeatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Constants.calledIniti(this);
        Constants.loadIntersAD(this, this);
        Constants.showBannerAdd(this, binding.adView);
        binding.goback.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.repeatbtn.setOnClickListener(v -> {
            if (binding.repeat.getText().toString().isEmpty() || binding.message.getText().toString().isEmpty()){
                Toast.makeText(this, "Please Enter The Required Data", Toast.LENGTH_SHORT).show();
            } else {
                if (binding.newLineSwitch.isChecked()) {
                    int t = Integer.parseInt(binding.repeat.getText().toString());
                    for (int i = 0; i < t; i++) {
                        s = s + binding.message.getText().toString() + "\n";
                    }
                    binding.result.setText(s);
                } else {
                    int t = Integer.parseInt(binding.repeat.getText().toString());
                    for (int i = 0; i < t; i++) {
                        s = s + binding.message.getText().toString() + " ";
                    }
                    binding.result.setText(s);
                }
            }
        });

        binding.copy.setOnClickListener(v -> {
            int sdk = android.os.Build.VERSION.SDK_INT;
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

            if (binding.result.getText().toString().equals("Your Result will be here...")){
                Toast.makeText(this, "Nothing To Copy", Toast.LENGTH_SHORT).show();
            } else {
                if(sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    clipboard.setText(binding.result.getText().toString());
                    Toast.makeText(this, "Copied To Clipboard", Toast.LENGTH_SHORT).show();
                } else {
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Blank Message", binding.result.getText().toString());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(this, "Copied To Clipboard", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.share.setOnClickListener(v -> {
            if (binding.result.getText().toString().equals("Your Result will be here...")){
                Toast.makeText(this, "Please repeat some text first", Toast.LENGTH_SHORT).show();
            } else {
                Intent whatsappIntent = new Intent("android.intent.action.SEND");
                whatsappIntent.setType("text/plain");
                whatsappIntent.putExtra("android.intent.extra.TEXT", binding.result.getText().toString());
                try {
                    startActivity(whatsappIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(TextRepeatActivity.this, "Some problems", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.delete.setOnClickListener(v -> {
            binding.repeat.setText("");
            binding.message.setText("");
            binding.result.setText("Your Result will be here...");
            s = "";
        });

    }


}