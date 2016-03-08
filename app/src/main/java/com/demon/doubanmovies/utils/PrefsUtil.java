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
        return sp.getString(DAY_NIGHT, Constant.MODE_AUTO);
    }

    public static String getPrefImageSize(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(IMAGE_SIZE, Constant.IMAGE_LARGE);
    }

    public static String getPrefNickname(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(NICKNAME, Constant.NICKNAME);
    }

    public static String getPrefSignature(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(SIGNATURE, Constant.SIGNATURE);
    }

    public static void switchDayNightMode(String mode) {
        switch (mode) {
            case Constant.MODE_DAY:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case Constant.MODE_NIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case Constant.MODE_AUTO:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
                break;
        }
    }
}
