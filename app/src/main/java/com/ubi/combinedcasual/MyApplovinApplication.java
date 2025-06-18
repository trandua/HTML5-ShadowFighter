package com.ubi.combinedcasual;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAppOpenAd;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;

public class MyApplovinApplication extends Application {
    private ExampleAppOpenManager appOpenManager;


    @Override
    public void onCreate() {
        super.onCreate();
        appOpenManager = new ExampleAppOpenManager(MyApplovinApplication.this);
    }


}

class ExampleAppOpenManager implements LifecycleObserver, MaxAdListener {
    //    private final MaxAppOpenAd appOpenAd;
    private final Context context;

    public ExampleAppOpenManager(final Context context) {
//        System.out.println("AppLovinSdk-TAGG- Init AppOpen");
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        this.context = context;
//        appOpenAd = new MaxAppOpenAd(Utils.ID_MAX_APPOPEN, context);

        if (!hasRemoveAds()) {
//            appOpenAd.setListener(this);
//            appOpenAd.loadAd();
        }
    }

    public boolean hasRemoveAds() {
//        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
//        return prefs.getBoolean(PRODUCT_IDS.get("remove_ads"), false);
        SharedPreferences prefs = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        return prefs.getBoolean("remove_ads", false);
    }

    private boolean isFirst = false;
    private int countResume = 0;

    private void showAdIfReady() {
        if (!hasRemoveAds()) {
            if (!isFirst) {
                isFirst = true;
//            if (appOpenAd == null || !AppLovinSdk.getInstance(context).isInitialized()) return;
//
//            if (appOpenAd.isReady()) {
//                appOpenAd.showAd(Utils.ID_APP_OPEN_PLACEMENT);
//            } else {
//                appOpenAd.loadAd();
//            }
            } else {
                if (countResume <= 3) {
                    countResume++;
                } else {
//                    AdManager.showIntersAd(context);
                    countResume = 0;
                }
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        showAdIfReady();
    }

    @Override
    public void onAdLoaded(final MaxAd ad) {
//        System.out.println("AppLovinSdk-TAGG-Appopen loaded");
    }

    @Override
    public void onAdLoadFailed(final String adUnitId, final MaxError error) {
//        System.out.println("AppLovinSdk-TAGG-Appopen load fail: "+ error.toString());
    }

    @Override
    public void onAdDisplayed(final MaxAd ad) {
//        System.out.println("AppLovinSdk-TAGG-Appopen display");
    }

    @Override
    public void onAdClicked(final MaxAd ad) {
    }

    @Override
    public void onAdHidden(final MaxAd ad) {
//        appOpenAd.loadAd();
    }

    @Override
    public void onAdDisplayFailed(final MaxAd ad, final MaxError error) {
//        System.out.println("AppLovinSdk-TAGG-Appopen display fail");
//        appOpenAd.loadAd();
    }
}
