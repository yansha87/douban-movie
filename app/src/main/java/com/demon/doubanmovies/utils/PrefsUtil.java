package com.demon.doubanmovies.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.PreferenceManager;

public class PrefsUtil {

    public static String getPrefDayNightMode(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String value = sp.getString("day_night", Constant.MODE_AUTO);
        return value;
    }

    public static String getPrefImageSize(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String value = sp.getString("image_size", Constant.IMAGE_MEDIUM);
        return value;
    }

    public static void switchDayNightMode(String mode) {
        if (mode.equals(Constant.MODE_DAY)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (mode.equals(Constant.MODE_NIGHT)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
        }
    }
}
