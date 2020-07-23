package com.example.android.skoob.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

public class AdMobUtil {

    public static int getAdViewHeightInDP(Activity activity) {
        int adHeight = 0;

        int screenHeightInDP = getScreenHeightInDP(activity);
        if (screenHeightInDP < 400)
            adHeight = 32;
        else if (screenHeightInDP <= 720)
            adHeight = 50;
        else
            adHeight = 90;

        return adHeight;
    }

    public static int getScreenHeightInDP(Activity activity) {
        DisplayMetrics displayMetrics = ((Context) activity).getResources().getDisplayMetrics();

        float screenHeightInDP = displayMetrics.heightPixels / displayMetrics.density;

        return Math.round(screenHeightInDP);
    }
}
