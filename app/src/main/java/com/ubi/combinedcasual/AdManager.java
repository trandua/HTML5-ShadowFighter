package com.ubi.combinedcasual;

import android.app.Activity;
import android.widget.FrameLayout;
import com.ubi.combinedcasual.adapter.AdAdapter;
import com.ubi.combinedcasual.adapter.AdmobAdapter;
import com.ubi.combinedcasual.adapter.IronsourceAdapter;
import com.ubi.combinedcasual.adapter.MAXAdapter;

public class AdManager {
    private static BillingManager mBillingManager;
    private static TimeChecker timeChecker;
    public static AdAdapter adAdapter;
    private static boolean hasRemoveAds;
    public static boolean IsRewardAvaiable() {
        return adAdapter.IsRewardAvaiable();
    }
    private static Activity mMainActivity;
    public static boolean HasInitSDK(){
        return adAdapter.IsInitSDK();
    }

    public static void init(Activity mainActivity, BillingManager billingManager, FrameLayout frameLayout) {
        mBillingManager = billingManager;
        timeChecker = new TimeChecker();
        mMainActivity = mainActivity;
        hasRemoveAds = mBillingManager != null && mBillingManager.isPurchased("remove_ads");
        timeChecker = new TimeChecker();
        adAdapter = new AdmobAdapter();
//        adAdapter = new MAXAdapter();
//        adAdapter = new IronsourceAdapter();
        adAdapter.initAds(mainActivity, frameLayout, true, hasRemoveAds);
    }

    public static void showIntersAd(Activity activity){
        //if(hasRemoveAds) return;
        if (timeChecker.hasOneMinutePassed()){
            adAdapter.showInterstitial(activity, new AdAdapter.IntersCloseCallback(){
                @Override
                public void onInterClose() {
                    startTime = System.currentTimeMillis();
//                    System.out.println("TAGG-Inters Close");
                }
            });
        }
    }
//    public static void showIntersAd(Context context){
//        //if(hasRemoveAds) return;
//        if (timeChecker.hasOneMinutePassed()){
//            adAdapter.showInterstitial(mMainActivity, new AdAdapter.IntersCloseCallback(){
//                @Override
//                public void onInterClose() {
//                    startTime = System.currentTimeMillis();
//                    System.out.println("TAGG-Inters Close");
//                }
//            });
//        }
//    }

    public static void showRewardedAd(Activity activity, AdAdapter.RewardedAdCallback callback){
        adAdapter.showRewardedVideo(callback, new AdAdapter.IntersCloseCallback(){
            @Override
            public void onInterClose() {
                startTime = System.currentTimeMillis();
//                System.out.println("TAGG-Inters Close by Video");
            }
        });
    }
    public static boolean Is30Min() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;
        return elapsedTime >= 30.000; // 30.000 milliseconds = nửa phút
    }

    private static long startTime;
}