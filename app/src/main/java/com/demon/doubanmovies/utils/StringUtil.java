package com.demon.doubanmovies.utils;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import java.util.List;

public final class StringUtil {

    /**
     * get spannable string by color
     *
     * @param str   string
     * @param color color
     * @return SpannableString
     */
    public static SpannableString getSpannableString(String str, int color) {
        SpannableString span = new SpannableString(str);
        span.setSpan(new ForegroundColorSpan(
                color), 0, span.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return span;
    }

    public static SpannableString getSpannableString1(String str, Object... whats) {
        SpannableString span = new SpannableString(str);
        for (Object what : whats) {
            span.setSpan(what, 0, span.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return span;
    }

    public static <T> T getListString(List<T> list, char s) {
        StringBuilder str = new StringBuilder();
        for (int i = 0, size = list.size(); i < size; i++) {
            str.append(i == 0 ? "" : s).append(list.get(i));
        }
        return (T) str.toString();
    }
}
