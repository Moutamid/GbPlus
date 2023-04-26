package com.moutamid.gb.utilis;

import static com.facebook.ads.BuildConfig.DEBUG;

import android.content.Context;

import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.BuildConfig;

public class AudienceNetworkInitializeHelper implements AudienceNetworkAds.InitListener{

    static void initialize(Context context) {
        if (!AudienceNetworkAds.isInitialized(context)) {
            if (DEBUG) {
                AdSettings.turnOnSDKDebugger(context);
            }
            AdSettings.setTestMode(BuildConfig.DEBUG);
            AudienceNetworkAds
                    .buildInitSettings(context)
                    .withInitListener(new AudienceNetworkInitializeHelper())
                    .initialize();
        }
    }

    @Override
    public void onInitialized(AudienceNetworkAds.InitResult initResult) {

    }
}
