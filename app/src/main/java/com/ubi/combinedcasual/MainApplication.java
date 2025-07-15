package com.ubi.combinedcasual;

import android.app.Application;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //TODO Init Solar Engine
        SolarEngine.initEngine(getApplicationContext());
    }
}
