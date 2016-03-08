package com.demon.doubanmovies.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.PreferenceManager;

public class PrefsUtil {

    private static final String DAY_NIGHT = "day_night";
    private static final String IMAGE_SIZE = "image_size";
    private static final String NICKNAME = "nickname";
    private static final String SIGNATURE = "signature";

    public static String getPrefDayNightMode(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String value = sp.getString(DAY_NIGHT, Constant.MODE_AUTO);
        return value;
    }

    public static String getPrefImageSize(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String value = sp.getString(IMAGE_SIZE, Constant.IMAGE_LARGE);
        return value;
    }

    public static String getPrefNickname(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String value = sp.getString(NICKNAME, Constant.NICKNAME);
        return value;
    }

    public static String getPrefSignature(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String value = sp.getString(SIGNATURE, Constant.SIGNATURE);
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
