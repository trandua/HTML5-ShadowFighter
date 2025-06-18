package com.ubi.combinedcasual;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAppOpenAd;
import com.applovin.sdk.AppLovinSdk;

public class AAppOpenManager implements MaxAdListener, Application.ActivityLifecycleCallbacks, DefaultLifecycleObserver {
    private final MaxAppOpenAd appOpenAd;
    private final Context context;
    private boolean isShowingAd = false;
    public boolean isFirstLoad = false;
    public AAppOpenManager(final Context context, MyApplication _application)
    {
//        ProcessLifecycleOwner.get().getLifecycle().addObserver( this );
        _application.registerActivityLifecycleCallbacks(this);
        this.context = context;
        Utils.showLogMessage("init AppOpenAds");
        appOpenAd = new MaxAppOpenAd( Utils.ID_MAX_APPOPEN, context);
        appOpenAd.setListener( this );
        appOpenAd.loadAd();
    }

    public void showAdIfReady()
    {
        if ( appOpenAd == null || !AppLovinSdk.getInstance( context ).isInitialized() ) return;

        if (!isShowingAd && appOpenAd.isReady())
        {
            Utils.showLogMessage("show App Open Ads");
            appOpenAd.showAd(Utils.ID_APP_OPEN_PLACEMENT);
        }
        else
        {
            appOpenAd.loadAd();
        }
    }

//    @OnLifecycleEvent(Lifecycle.Event.ON_START)
//    public void onStart()
//    {
//        showAdIfReady();
//    }


    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onStart(owner);
        showAdIfReady();
    }

    @Override
    public void onAdLoaded(final MaxAd ad) {
        Utils.showLogMessage("AppOpen Loaded");
        if(!isFirstLoad){
            showAdIfReady();
            isFirstLoad = true;
        }
    }
    @Override
    public void onAdLoadFailed(final String adUnitId, final MaxError error) {
        Utils.showLogMessage("AppOpen Load Fail");
    }
    @Override
    public void onAdDisplayed(final MaxAd ad) {}
    @Override
    public void onAdClicked(final MaxAd ad) {}

    @Override
    public void onAdHidden(final MaxAd ad)
    {
        appOpenAd.loadAd();
        isShowingAd = false;
    }

    @Override
    public void onAdDisplayFailed(final MaxAd ad, final MaxError error)
    {
        appOpenAd.loadAd();
    }

    //TODO -->>
    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        showAdIfReady();
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
