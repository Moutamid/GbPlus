package com.moutamid.gb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.moutamid.gb.databinding.ActivityTextToEmojiBinding;
import com.moutamid.gb.utilis.Constants;

import java.io.IOException;
import java.io.InputStream;

public class TextToEmojiActivity extends AppCompatActivity {
    ActivityTextToEmojiBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTextToEmojiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Constants.calledIniti(this);
        Constants.loadIntersAD(this, this);
        Constants.showBannerAdd(this, binding.adView);

        binding.goback.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.repeatbtn.setOnClickListener(v -> {
            if (binding.emoji.getText().toString().isEmpty() || binding.message.getText().toString().isEmpty()){
                Toast.makeText(this, "Please Fill All Details", Toast.LENGTH_SHORT).show();
            } else {
                char[] charArray = binding.message.getText().toString().toCharArray();
                binding.result.setText(" \n");
                binding.result.setBackgroundResource(R.drawable.round);
                for (char character : charArray) {
                    byte[] localObject3;
                    InputStream localObject2;
                    if (character == '?') {
                        try {
                            InputStream localObject1 = getBaseContext().getAssets().open("ques.txt");
                            localObject3 = new byte[localObject1.available()];
                            localObject1.read(localObject3);
                            localObject1.close();
                            binding.result.append(new String(localObject3).replaceAll("[*]", binding.emoji.getText().toString()) + "\n\n");
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        }
                    } else if (character == ((char) (character & 95)) || Character.isDigit(character)) {
                        try {
                            localObject2 = getBaseContext().getAssets().open(character + ".txt");
                            localObject3 = new byte[localObject2.available()];
                            localObject2.read(localObject3);
                            localObject2.close();
                            binding.result.append(new String(localObject3).replaceAll("[*]", binding.emoji.getText().toString()) + "\n\n");
                        } catch (IOException ioe2) {
                            ioe2.printStackTrace();
                        }
                    } else {
                        try {
                            localObject2 = getBaseContext().getAssets().open("low" + character + ".txt");
                            localObject3 = new byte[localObject2.available()];
                            localObject2.read(localObject3);
                            localObject2.close();
                            binding.result.append(new String(localObject3).replaceAll("[*]", binding.emoji.getText().toString()) + "\n\n");
                        } catch (IOException ioe22) {
                            ioe22.printStackTrace();
                        }
                    }
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
                Toast.makeText(this, "Please create some blank message first", Toast.LENGTH_SHORT).show();
            } else {
                Intent whatsappIntent = new Intent("android.intent.action.SEND");
                whatsappIntent.setType("text/plain");
                whatsappIntent.putExtra("android.intent.extra.TEXT", binding.result.getText().toString());
                try {
                    startActivity(whatsappIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(TextToEmojiActivity.this, "Some problems", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.delete.setOnClickListener(v -> {
            binding.emoji.setText("");
            binding.message.setText("");
            binding.result.setText("Your Result will be here...");
            binding.result.setBackgroundResource(0);
        });

    }
}