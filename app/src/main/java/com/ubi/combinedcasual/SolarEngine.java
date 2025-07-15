package com.ubi.combinedcasual;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdFormat;
import com.applovin.sdk.AppLovinSdk;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.AdapterResponseInfo;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.ironsource.mediationsdk.impressionData.ImpressionData;
//import com.ubi.combinedcasual.MainActivity;
import com.reyun.solar.engine.OnAttributionListener;
import com.reyun.solar.engine.OnInitializationCallback;
import com.reyun.solar.engine.SolarEngineConfig;
import com.reyun.solar.engine.SolarEngineManager;
import com.reyun.solar.engine.infos.SEAdImpEventModel;

import org.json.JSONObject;

public class SolarEngine {
    private static Context mContext;
    private static String APP_KEY = "60e1a189e7933495";
    public static void showLogInit(){
        //Log.e("TIGG", "Solar Engine: " + hasInitSuccess);
    }
    private static boolean hasInitSuccess;
    public static void initEngine(Context context){
        mContext = context;
        if(!hasInitSuccess) {
            SolarEngineManager.getInstance().preInit(mContext, APP_KEY);

            SolarEngineConfig config = new SolarEngineConfig.Builder()
                    .logEnabled()
//                .isDebugModel(true)
                    .build();
            config.setOnAttributionListener(new OnAttributionListener() {
                @Override
                public void onAttributionSuccess(JSONObject attribution) {
                    //Performed when the attribution results are successfully obtained
                    Log.e("TIGG", "Solar Engine successfully obtained");
                }

                @Override
                public void onAttributionFail(int errorCode) {
                    //Performed when the attribution results are not obtained
                    Log.e("TIGG", "Solar Engine results are not obtained");
                }
            });
            SolarEngineManager.getInstance().initialize(mContext, APP_KEY, config, new OnInitializationCallback() {
                @Override
                public void onInitializationCompleted(int code) {
                    if (code == 0) {
                        //Initialization success
                        Log.e("TIGG", "Solar Engine Initialization success");
                        hasInitSuccess = true;
                    } else {
                        //Initialization failed, please check the code table below for specific failure reason
                        Log.e("TIGG", "Solar Engine Initialization failed: " + code);
                        hasInitSuccess = false;
                    }
                }
            });
        }
    }
    public static void logAdImpression(Bundle extras){
//        String mediationGroupName = extras.getString("mediation_group_name");
//        String mediationABTestName = extras.getString("mediation_ab_test_name");
//        String mediationABTestVariant = extras.getString("mediation_ab_test_variant");
//        //SE SDK processing logic
//        SEAdImpEventModel seAdImpEventModel =  new SEAdImpEventModel();
//        //Monetization Platform Name
//        seAdImpEventModel.setAdNetworkPlatform(adSourceName);
//        //Mediation Platform Name (e.g. admob SDK as "admob")
//        seAdImpEventModel.setMediationPlatform("admob");
//        //Displayed Ad Type (Taking Rewarded Ad as an example, adType = 1)
//        seAdImpEventModel.setAdType(1);
//        //Monetization Platform App ID
//        seAdImpEventModel.setAdNetworkAppID(adSourceId);
//        //Monetization Platform Ad Unit ID
//        seAdImpEventModel.setAdNetworkADID(adUnitId);
//        //Ad eCPM
//        seAdImpEventModel.setEcpm(valueMicros/1000);
//        //Monetization Platform Currency Type
//        seAdImpEventModel.setCurrencyType(currencyCode);
//        //True: rendered success
//        seAdImpEventModel.setRenderSuccess(true);
//        //You can add custom properties as needed. Here we do not give examples.
//        SolarEngineManager.getInstance().trackAdImpression(seAdImpEventModel);
    }
    public static void logAdmobAdImpression(InterstitialAd mInterstitialAd, AdValue adValue, MY_AD_TYPE adType){
// Extract the impression-level ad revenue data.
        double valueMicros = adValue.getValueMicros();
        String currencyCode = adValue.getCurrencyCode();
        int precision = adValue.getPrecisionType();

        // Get the ad unit ID.
        String adUnitId = mInterstitialAd.getAdUnitId();

        AdapterResponseInfo loadedAdapterResponseInfo = mInterstitialAd.getResponseInfo().
                getLoadedAdapterResponseInfo();
        String adSourceName = loadedAdapterResponseInfo.getAdSourceName();
        String adSourceId = loadedAdapterResponseInfo.getAdSourceId();
        String adSourceInstanceName = loadedAdapterResponseInfo.getAdSourceInstanceName();
        String adSourceInstanceId = loadedAdapterResponseInfo.getAdSourceInstanceId();

        Bundle extras = mInterstitialAd.getResponseInfo().getResponseExtras();
        String mediationGroupName = extras.getString("mediation_group_name");
        String mediationABTestName = extras.getString("mediation_ab_test_name");
        String mediationABTestVariant = extras.getString("mediation_ab_test_variant");
        //SE SDK processing logic
        SEAdImpEventModel seAdImpEventModel =  new SEAdImpEventModel();
        //Monetization Platform Name
        seAdImpEventModel.setAdNetworkPlatform(adSourceName);
        //Mediation Platform Name (e.g. admob SDK as "admob")
        seAdImpEventModel.setMediationPlatform("admob");
        //Displayed Ad Type (Taking Rewarded Ad as an example, adType = 1)
        seAdImpEventModel.setAdType(adType.getValue());
        //Monetization Platform App ID
        seAdImpEventModel.setAdNetworkAppID(adSourceId);
        //Monetization Platform Ad Unit ID
        seAdImpEventModel.setAdNetworkADID(adUnitId);
        //Ad eCPM
        seAdImpEventModel.setEcpm(valueMicros/1000);
        //Monetization Platform Currency Type
        seAdImpEventModel.setCurrencyType(currencyCode);
        //True: rendered success
        seAdImpEventModel.setRenderSuccess(true);
        //You can add custom properties as needed. Here we do not give examples.
        SolarEngineManager.getInstance().trackAdImpression(seAdImpEventModel);
        //end
        Log.e("TIGG", "Admob: " + seAdImpEventModel.toString());
    }
    public static void logLevelPlayAdImpression(ImpressionData impressionData, MY_AD_TYPE adType) {
// The onImpressionSuccess will be reported when the rewarded video and interstitial ad is opened.
        // For banners, the impression is reported on load success.  Log.d(TAG, "onImpressionSuccess" + impressionData);
        if (impressionData != null) {
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.AD_PLATFORM, "ironSource");
            bundle.putString(FirebaseAnalytics.Param.AD_SOURCE, impressionData.getAdNetwork());//.adNetwork());
            bundle.putString(FirebaseAnalytics.Param.AD_FORMAT, impressionData.getAdUnit());
            bundle.putString(FirebaseAnalytics.Param.AD_UNIT_NAME, impressionData.getInstanceName());
            bundle.putString(FirebaseAnalytics.Param.CURRENCY, "USD");
            bundle.putDouble(FirebaseAnalytics.Param.VALUE, impressionData.getRevenue());

            WebViewActivity.analysticManager.logEvent(FirebaseAnalytics.Event.AD_IMPRESSION, bundle);

            //SE SDK processing logic
            SEAdImpEventModel seAdImpEventModel = new SEAdImpEventModel();
            //Monetization Platform Name
            seAdImpEventModel.setAdNetworkPlatform(impressionData.getAdNetwork());
            //Mediation Platform Name, IronSource SDK set "ironSource"
            seAdImpEventModel.setMediationPlatform("ironSource");
            //Displayed Ad Type (Taking Rewarded Ad as an example, adType = 1) For specific details, refer to adType field in the doc
            seAdImpEventModel.setAdType(adType.getValue());
            //Monetization Platform App ID (optional) You can input the appKey in SE SDK.
            seAdImpEventModel.setAdNetworkAppID(APP_KEY);
            //Monetization Platform Ad Unit ID
            seAdImpEventModel.setAdNetworkADID(impressionData.getInstanceId());
            //Ad eCPM
            seAdImpEventModel.setEcpm(impressionData.getRevenue() * 1000);
            //Monetization Platform Currency Type (Default USD)
            seAdImpEventModel.setCurrencyType("USD");
            //True: rendered success
            seAdImpEventModel.setRenderSuccess(true);
            //You can add custom properties as needed. Here we do not give examples.
            SolarEngineManager.getInstance().trackAdImpression(seAdImpEventModel);
            //end
            //Log.e("TIGG", "LevelPlay: " + seAdImpEventModel.toString());
            showLogInit();
        }
    }
    public static void logMAXAdImpression(MaxAd ad, MY_AD_TYPE adType){
        double revenue = ad.getRevenue(); // In USD

        // Miscellaneous data
        String countryCode = AppLovinSdk.getInstance(mContext).getConfiguration().getCountryCode(); // "US" for the United States, etc - Note: Do not confuse this with currency code which is "USD"
        String networkName = ad.getNetworkName(); // Display name of the network that showed the ad
        String adUnitId = ad.getAdUnitId(); // The MAX Ad Unit ID
        MaxAdFormat adFormat = ad.getFormat(); // The ad format of the ad (e.g. BANNER, MREC, INTERSTITIAL, REWARDED)
        String placement = ad.getPlacement(); // The placement this ad's postbacks are tied to
        String networkPlacement = ad.getNetworkPlacement(); // The placement ID from the network that showed the ad

        //SE SDK processing logic
        SEAdImpEventModel seAdImpEventModel =  new SEAdImpEventModel();
        //Monetization Platform Name
        seAdImpEventModel.setAdNetworkPlatform( ad.getNetworkName());
        //Mediation Platform Name (Max SDK set as "Max")
        seAdImpEventModel.setMediationPlatform("Max");
        //Displayed Ad Type (If rewarded ad, adType = 1)
        seAdImpEventModel.setAdType(adType.getValue());
        //Monetization Platform App ID (optional) You can input the appKey in SE SDK.
        seAdImpEventModel.setAdNetworkAppID(APP_KEY);
        //Monetization Platform Ad Unit ID
        seAdImpEventModel.setAdNetworkADID(ad.getNetworkPlacement());
        //Ad eCPM
        seAdImpEventModel.setEcpm(ad.getRevenue()*1000);
        //Monetization Platform Currency Type (USD)
        seAdImpEventModel.setCurrencyType("USD");
        //True: rendered success
        seAdImpEventModel.setRenderSuccess(true);
        SolarEngineManager.getInstance().trackAdImpression(seAdImpEventModel);
        //You can add custom properties as needed. Here we do not give examples.
        //end
        Log.e("TIGG", "MAX: " + seAdImpEventModel.toString());
    }
    public static enum MY_AD_TYPE{
        Rewarded(1),
        Splash(2),
        Interstitial(3),
        FullscreenVideo(4),
        Banner(5),
        Native(6),
        NativeVideo(7),
        BannerMPU(8),
        InstreamVideo(9),
        MREC(10),
        Other(0);
        private final int value;

        MY_AD_TYPE(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}