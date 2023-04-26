package com.moutamid.gb.utilis;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.UriPermission;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.moutamid.gb.R;
import com.moutamid.gb.model.StatusItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Constants {
    public static final String SAVED_FOLDER = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/GbWhatsTool/Saved Status/";
    public static final String SOURCE_FOLDER_WA_OLD = Environment.getExternalStorageDirectory().getAbsolutePath() + "/WhatsApp/Media/.Statuses";
    public static final String SOURCE_FOLDER_WA_NEW = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/media/com.whatsapp/WhatsApp/Media/.Statuses";
    public static List<StatusItem> allWAImageItems = new ArrayList<>();
    public static List<StatusItem> allWAVideoItems = new ArrayList<>();
    public static List<StatusItem> allSavedItems = new ArrayList<>();
    public static final String WaSavedRoute = "WaSavedRoute";
    private static InterstitialAd mInterstitialAd;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static final String[] permissions13 = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_AUDIO
    };

    public static final String[] permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    public static final int PERMISSION_CODE = 1;

    public static boolean checkFolder() {
        File dirs = new File(SAVED_FOLDER);
        if (dirs.exists()) return true;
        else if (!dirs.mkdirs()) {
            dirs.mkdirs();
            return true;

        } else return false;
    }

    public static void calledIniti(Context context){
        AudienceNetworkAds.initialize(context);
        AudienceNetworkInitializeHelper.initialize(context);
    }

    public static void showBannerAdd(Context context, LinearLayout adContainer){
        AdView adView = new AdView(context, context.getResources().getString(R.string.Banner_ID), AdSize.BANNER_HEIGHT_50);
        adContainer.addView(adView);
        adView.loadAd();
    }

    public static void loadIntersAD(Context context, Activity activity){
        // InterstitialAd interstitialAd = new InterstitialAd(context, "YOUR_PLACEMENT_ID");
        InterstitialAd interstitialAd = new InterstitialAd(context, context.getResources().getString(R.string.Interstial_ID));
        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback
                Log.e(TAG, "Interstitial ad displayed.");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
                Log.e(TAG, "Interstitial ad dismissed.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed
                Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                // Show the ad
                interstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                Log.d(TAG, "Interstitial ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                Log.d(TAG, "Interstitial ad impression logged!");
            }
        };

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        interstitialAd.loadAd(
                interstitialAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());

        new Handler().postDelayed(new Runnable() {
            public void run() {
                // Check if interstitialAd has been loaded successfully
                if(interstitialAd == null || !interstitialAd.isAdLoaded()) {
                    return;
                }
                // Check if ad is already expired or invalidated, and do not show ad if that is the case. You will not get paid to show an invalidated ad.
                if(interstitialAd.isAdInvalidated()) {
                    return;
                }
                // Show the ad
                interstitialAd.show();
            }
        }, 5000); // Show the ad after 15 minutes
    }

    public static boolean copyFileInSavedDir(Context context, String path, String name) {
        String string = SAVED_FOLDER + name;
        Uri uri = Uri.fromFile(new File(string));
        try {
            Uri uri1 = Uri.parse(path);
            InputStream inputStream = context.getContentResolver().openInputStream(uri1);
            OutputStream outputStream = context.getContentResolver().openOutputStream(uri, "w");
            try {
                byte[] arrayOfByte = new byte[1024];
                while (true) {
                    int i = inputStream.read(arrayOfByte);
                    if (i > 0) {
                        outputStream.write(arrayOfByte, 0, i);
                        continue;
                    }
                    inputStream.close();
                    outputStream.flush();
                    outputStream.close();

                    Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                    intent.setData(uri);
                    context.sendBroadcast(intent);
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Boolean checkIfGotAccess(Context context, Uri treeUriWA) {
        List<UriPermission> permissionList = context.getContentResolver().getPersistedUriPermissions();
        for (UriPermission it : permissionList) {
            if (it.getUri().equals(treeUriWA) && it.isReadPermission())
                return true;
        }
        return false;
    }

    public static boolean isPermissionGranted(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (
                    (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED) &&
                            (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED)
            ) {
                return true;
            } else return false;
        } else {
            if (
                    (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                            (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            ) {
                return true;
            } else return false;
        }
    }

    public static void checkApp(Activity activity) {
        String appName = "gbPlusVer";

        new Thread(() -> {
            URL google = null;
            try {
                google = new URL("https://raw.githubusercontent.com/Moutamid/Moutamid/main/apps.txt");
            } catch (final MalformedURLException e) {
                e.printStackTrace();
            }
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(google != null ? google.openStream() : null));
            } catch (final IOException e) {
                e.printStackTrace();
            }
            String input = null;
            StringBuffer stringBuffer = new StringBuffer();
            while (true) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        if ((input = in != null ? in.readLine() : null) == null) break;
                    }
                } catch (final IOException e) {
                    e.printStackTrace();
                }
                stringBuffer.append(input);
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
            String htmlData = stringBuffer.toString();

            try {
                JSONObject myAppObject = new JSONObject(htmlData).getJSONObject(appName);

                boolean value = myAppObject.getBoolean("value");
                String msg = myAppObject.getString("msg");

                if (value) {
                    activity.runOnUiThread(() -> {
                        new AlertDialog.Builder(activity)
                                .setMessage(msg)
                                .setCancelable(false)
                                .show();
                    });
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }).start();
    }

}
