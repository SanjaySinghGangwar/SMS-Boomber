package com.sanjaysgangwar.smsboomber.model;

import android.content.Context;
import android.content.SharedPreferences;

public class mSharedPreference {
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    private String APP_SHARED_PREFS;

    public mSharedPreference(Context mContext) {
        sharedPreferences = mContext.getSharedPreferences(APP_SHARED_PREFS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        APP_SHARED_PREFS = "SMS_BOOMBER";
    }

    public void clearPreferences() {
        editor.clear();
        editor.commit();
    }

    public Boolean getAds() {
        return sharedPreferences.getBoolean("ads", true);
    }

    public void setAds(Boolean Seconds) {
        editor.putBoolean("ads", Seconds);
        editor.commit();
    }

}
