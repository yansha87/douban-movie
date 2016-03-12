package com.demon.doubanmovies.utils;

import android.content.Context;

public class DensityUtil {

    /**
     * translate dp into px by density
     * @param context context
     * @param dpValue dp
     * @return px
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * translate dx into dp by density
     * @param context context
     * @param pxValue dx
     * @return dp
     */
    @SuppressWarnings("unused")
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
