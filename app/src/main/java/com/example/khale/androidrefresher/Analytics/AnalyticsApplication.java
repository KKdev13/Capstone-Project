package com.example.khale.androidrefresher.Analytics;

import android.app.Application;

import com.example.khale.androidrefresher.R;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class AnalyticsApplication extends Application{
    private static GoogleAnalytics gAnalytics;
    private static Tracker gTracker;

    @Override
    public void onCreate() {
        super.onCreate();
        gAnalytics = GoogleAnalytics.getInstance(this);
    }

    synchronized public Tracker getDefaultTracker(){
        // To enable debug logging: use adb shell setprop log.tag.GAv4 DEBUG
        if (gTracker == null){
            gTracker = gAnalytics.newTracker(R.xml.global_tracker);
        }

        return gTracker;

    }
}
