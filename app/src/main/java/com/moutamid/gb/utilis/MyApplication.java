package com.moutamid.gb.utilis;

import android.app.Application;

import com.facebook.ads.AudienceNetworkAds;
import com.fxn.stash.Stash;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stash.init(this);
        AudienceNetworkInitializeHelper.initialize(this);
        AudienceNetworkAds.initialize(this);
    }
}
